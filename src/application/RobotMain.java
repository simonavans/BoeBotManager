package application;

import TI.BoeBot;
import TI.Timer;
import hardware.inputdevices.Bluetooth;
import hardware.inputdevices.IRReceiver;
import hardware.outputdevices.Buzzer;
import link.LineSensors;
import hardware.inputdevices.sensor.UltrasonicSensor;
import hardware.outputdevices.Engine;
import hardware.outputdevices.Gripper;
import hardware.outputdevices.led.NeoPixel;
import link.Updatable;
import link.callbacks.*;

import java.awt.*;
import java.util.ArrayList;

//todo physically connect buzzer and far ultrasonic to pin number in settings

/**
 * The heart of the BoeBot application. It initializes the BoeBot and makes sure hardware is running.
 * It also makes sure that all the BoeBot's high level logic, like stopping on crossroads, work.
 * The BoeBot can receive Bluetooth commands from a BoeBot application running on another computer.
 * This class makes sure the received commands are interpreted correctly and that they control the
 * BoeBot's hardware correctly.
 *
 * @author all members of team B2
 */
public class RobotMain implements IRReceiverCallback, UltrasonicCallback, ButtonCallback, LineSensorsCallback, BluetoothCallback, EngineCallback {
    // ArrayList containing all hardware that needs to run constantly
    private static final ArrayList<Updatable> updatables = new ArrayList<>();

    // Settings of the current BoeBot setup. Values may differ between BoeBots.
    private final Settings settings = new Settings(
            200,
            3, 100,
            25, 50, 0, -3, 1000, 500,
            800,
            1400, 1800, 400, 700, 500,
            115200, 2200, 1250,
            14, 1, 11, 3, 13, 12, 0, 1, new int[]{0, 1, 2}, 2);

    // Output updatables, they alter the state of the BoeBot or log useful information to the user, like LEDs
    private final Engine engine = new Engine(settings.LEFT_WHEEL_PIN, settings.RIGHT_WHEEL_PIN, settings.ENGINE_NEUTRAL_OFFSET_LEFT, settings.ENGINE_NEUTRAL_OFFSET_RIGHT, settings.ENGINE_FORWARD_SPEED, settings.ENGINE_BACK_STEER_SPEED, settings.ENGINE_TURN_TIME, settings.ENGINE_OBJECT_PLACEMENT_TIME, this);
    private final Gripper gripper = new Gripper(settings.GRIPPER_PIN, settings.GRIPPER_OPEN_FREQUENCY, settings.GRIPPER_CLOSE_FREQUENCY);
    private final NeoPixel pixel0 = new NeoPixel(0, Color.BLACK);
    private final NeoPixel pixel1 = new NeoPixel(1, Color.BLACK);
    private final NeoPixel pixel2 = new NeoPixel(2, Color.BLACK);
    private final NeoPixel pixel3 = new NeoPixel(3, Color.BLACK);
    private final NeoPixel pixel4 = new NeoPixel(4, Color.BLACK);
    private final NeoPixel pixel5 = new NeoPixel(5, Color.BLACK);
    private final Buzzer buzzer = new Buzzer(settings.BUZZER_PIN);

    // Input updatables, they check for input from the BoeBot's environment
    private final UltrasonicSensor ultrasonicClose = new UltrasonicSensor(settings.ULTRASONIC_CLOSE_ECHO_PIN, settings.ULTRASONIC_CLOSE_TRIGGER_PIN, settings.ULTRASONIC_CLOSE_THRESHOLD, pixel2, this);
    private final UltrasonicSensor ultrasonicFar = new UltrasonicSensor(settings.ULTRASONIC_FAR_ECHO_PIN, settings.ULTRASONIC_FAR_TRIGGER_PIN, settings.ULTRASONIC_FAR_THRESHOLD, null, this);
    private final IRReceiver irReceiver = new IRReceiver(settings.IR_RECEIVER_PIN, pixel0, settings.IR_RECEIVER_BIT_THRESHOLD, this);
    private final LineSensors lineSensors = new LineSensors(settings.LINE_SENSOR_ADC_PINS, settings.LINE_SENSOR_THRESHOLD, settings.LINE_SENSOR_CEILING, settings.LINE_SENSORS_WAIT_AFTER_DEVIATION, settings.LINE_SENSORS_WAIT_BEFORE_CROSSROAD, settings.LINE_SENSORS_WAIT_AFTER_DRIVING_BACKWARDS, this);
    private final Bluetooth bluetoothReceiver = new Bluetooth(settings.BLUETOOTH_BAUDRATE, this);


    // Whether the BoeBot is listening on commands. It will only be true if the BoeBot is standing
    // still on a crossroad and when it is not busy executing a command from the application
    private boolean listeningForCommands = true;

    // Notifies whether the emergency brake has been enabled on the BoeBot. This will block any
    // input to the BoeBot, except for the resume button on the remote and the emergency button
    // on the bot, both of which will disable the emergency brake when it is enabled.
    private boolean emergencyBrakeEnabled = false;

    // A Timer used for when the BoeBot drove backwards to the previous crossroad. If the
    // bot would stop immediately when the line sensors detected a crossroad, the bot would
    // not be perfectly centered on the crossroad. This increases the chance of not finding
    // the line back during a turn. To solve this problem, the bot needs to drive forward
    // just a little bit to be centered on the crossroad, which is what this Timer is for.
    private Timer nudgeForwardTimer;

    /**
     * Runs as the very first method after the BoeBot has booted up. Makes sure initialization
     * happens and that hardware update loops will start running.
     *
     * @author Simon
     */
    public static void main(String[] args) {
        // Cannot call init() and run() directly, since the main method is static
        RobotMain main = new RobotMain();
        main.init();
        main.run();
    }

    /**
     * Adds all hardware to an updatables ArrayList, so they can be updated constantly in the
     * run() method.
     *
     * @author Simon
     */
    private void init() {
        updatables.add(ultrasonicClose);
        updatables.add(irReceiver);
        updatables.add(lineSensors);
        updatables.add(engine);
        updatables.add(bluetoothReceiver);
    }

    /**
     * Continuously calls the update method in every hardware class. This way, the hardware
     * classes will receive an update() call every time the while-loop in this method runs.
     *
     * @author Simon
     */
    private void run() {
        while (true) {
            // Runs update method of all things listed in the updatables variable
            for (Updatable updatable : updatables) {
                updatable.update();
            }

            // If this Timer has timed out, meaning the BoeBot is now perfectly aligned
            // on the crossroad
            if (nudgeForwardTimer != null && nudgeForwardTimer.timeout()) {
                engine.brake();
                listeningForCommands = true;
                bluetoothReceiver.transmitCommand("Boebot: Succeeded");
            }

            BoeBot.wait(1);
        }
    }

    /**
     * Runs when the LineSensors class notifies that a crossroad has been detected. The
     * BoeBot will turn off all of its sensors and brake on its current position, keeping
     * itself perfectly aligned on the crossroad. It will also send a 'Succeeded' command
     * to the application, which can then send the BoeBot new commands.
     *
     * @author Simon
     */
    @Override
    public void onDetectCrossroad() {
        ultrasonicClose.setEnabled(false);
        ultrasonicFar.setEnabled(false);
        engine.brake();

        if (engine.isInReverse()) {
            pixel1.resetBlink();
            buzzer.resetRepeatingBeep();

            // If the BoeBot was previously driving backwards, we have not accounted yet
            // for the bot ending up slightly misaligned on the crossroad. Therefore,
            // we need the BoeBot to drive just a tiny bit forward.
            nudgeForwardTimer = new Timer(settings.ROBOTMAIN_NUDGE_FORWARD_TIME);
            nudgeForwardTimer.mark();
            engine.drive(false);
        } else {
            listeningForCommands = true;
            bluetoothReceiver.transmitCommand("Boebot: Succeeded");
        }

    }

    /**
     * Runs when the line sensors detect that the BoeBot deviates from its route.
     * There is a slight delay between the actual detection and calling this method.
     * This is to verify that the bot has not accidentally detected part of a crossroad.
     * When this runs, the bot will try to align itself by steering back toward the line.
     *
     * @param toLeft Whether a deviation to the left has occurred, or to the right.
     * @author Simon
     */
    @Override
    public void onDeviate(boolean toLeft) {
        if (toLeft) {
            // Too far to the left, so BoeBot should turn right
            engine.changeDirection(false);
        } else {
            // Too far to the right, so BoeBot should turn left
            engine.changeDirection(true);
        }
    }

    /**
     * Runs when there is no deviation or crossroad detected, meaning the
     * BoeBot will just drive forward.
     *
     * @author Simon
     */
    @Override
    public void onDriveStraight() {
        engine.drive(false);
    }

    /**
     * If the Engine class was instructed to turn, it notifies when it has completed
     * turning. As a result, this method will run. All it does is notify the application
     * that new commands can be sent.
     */
    @Override
    public void onCompletedTurn() {
        listeningForCommands = true;
        bluetoothReceiver.transmitCommand("Boebot: Succeeded");
    }

    /**
     * When the BoeBot receives instructions for placing the object on the crossroad,
     * it can not open its gripper instantly, because then the object would be slightly
     * off the crossroad. To be able to place the object right on the crossroad, a
     * wait time must be used, which is handled by the Engine class. When this time has
     * passed, this method runs, which opens the gripper.
     *
     * @author Simon
     */
    @Override
    public void onObjectCanBeDropped() {
        gripper.open();
    }

    /**
     * Runs when any ultrasonic sensor's callback is triggered. If the sensor for grabbing
     * objects called this method, the sensor itself will be disabled and the gripper
     * will be closed. However, if the sensor for detecting far away objects triggered it,
     * the BoeBot will send a message to the application asking if the object it observed
     * is an object that was not known by the application.
     *
     * @param source which ultrasonic sensor called this method.
     * @author Simon
     */
    @Override
    public void onUltrasonicSensorEvent(UltrasonicSensor source) {
        if (source == ultrasonicClose) {
            ultrasonicClose.setEnabled(false);
            gripper.close();
        } else if (source == ultrasonicFar) {
            bluetoothReceiver.transmitCommand("Boebot: Object");
        }
    }

    /**
     * Runs when the bluetooth chip receives a Bluetooth signal from the application.
     * Executes code based on the command parameter.
     *
     * @param command the command which was transmitted to the BoeBot from the application.
     * @author Simon and Kerr
     */
    @Override
    public void onBluetoothEvent(String command) {
        // This first switch statements can run at anytime, regardless of what the BoeBot is doing.
        switch (command) {
            // This command is received in emergency situations, after which the BoeBot will activate
            // its emergency brake.
            case "Application: Brake":
                enableEmergencyBrake();
                return;

            // If the IR remote asked for manual control, but it is disallowed by the application,
            // this code will run. Used for when the remote tries to move the BoeBot into an
            // obstruction, which is not allowed by the application.
            case "Application: Disallowed":
                buzzer.repeatingBeep(500, 2, false);
                return;

            // When the application finds that the object which the BoeBot detected was not on the
            // route, it sends this command. As a result, the bot tries to go to the previous
            // crossroad. The application recalculates the route.
            case "Application: Uncharted":
                engine.drive(true);
                lineSensors.setEnabled(true);
                pixel1.blink(new Color(100, 0, 0), 10000, 20, false);
                buzzer.repeatingBeep(10000, 20, false);
                return;
        }

        if (listeningForCommands && !emergencyBrakeEnabled) {
            switch (command) {
                // The BoeBot is instructed to go to the next crossroad in front of itself
                case "Application: Forward":
                    lineSensors.setEnabled(true);
                    engine.drive(false);
                    ultrasonicClose.setEnabled(true);
                    ultrasonicFar.setEnabled(true);
                    break;

                // The BoeBot is instructed to make a left turn, but stays on the same crossroad
                case "Application: Left":
                    // turn left
                    pixel5.blink(Color.ORANGE, 1000, 2, false);
                    engine.turn90(true);
                    break;

                // The BoeBot is instructed to make a right turn, but stays on the same crossroad
                case "Application: Right":
                    // turn right
                    pixel3.blink(Color.ORANGE, 1000, 2, false);
                    engine.turn90(false);
                    break;

                // The BoeBot is instructed to place an object on the crossroad it is currently
                // on. It will also drive backwards to the previous crossroad. During this process,
                // and after some time, it will drop the object on the crossroad on the correct
                // crossroad.
                case "Application: Place":
                    // Activate line sensors only after a delay. Otherwise, the line sensors would
                    // detect the crossroad it is currently on as the next crossroad.
                    lineSensors.delayWhenDrivingBackwards();
                    engine.driveBackwardsUntilCrossroad();
                    pixel1.blink(new Color(100, 100, 100), 10000, 20, false);
                    buzzer.repeatingBeep(10000, 20, false);
                    break;
                default:
                    System.out.println("Warning: Unknown bluetooth command: " + command);
            }
            listeningForCommands = false;
        }
    }

    /**
     * Runs when the IR remote has sent a command to the BoeBot. The BoeBot does not process
     * these commands directly when received. Rather, it is done by the application. The
     * reason for this design choice is to keep the BoeBot 'dumb', and the application 'smart'.
     *
     * @author Timo and Simon
     */
    @Override
    public void onIRReceiverEvent(int receiverCode) {
        // This command disables the emergency brake and is the only IR remote command that can
        // be executed when the emergency brake is enabled.
        if (emergencyBrakeEnabled) {
            if (receiverCode == 12) {
                // Button: Enter (001100010000)
                disableEmergencyBrake();
                bluetoothReceiver.transmitCommand("Remote: Resume");
            }
            return;
        }

        switch (receiverCode) {
            case 16:
                // Button: Ch+ (000010010000)
                bluetoothReceiver.transmitCommand("Remote: Forward");
                break;
            case 17:
                // Button: Ch- (100010010000)
                bluetoothReceiver.transmitCommand("Remote: Backward");
                break;
            case 18:
                // Button: Vol+ > (010010010000)
                bluetoothReceiver.transmitCommand("Remote: Right");
                break;
            case 19:
                // Button: < Vol- (110010010000)
                bluetoothReceiver.transmitCommand("Remote: Left");
                break;
            case 20:
                // Button: Mute sound (001010010000)
                bluetoothReceiver.transmitCommand("Remote: Place");
                break;
            case 21:
                // Button: Power (101010010000)
                // Enabling the emergency brake is the only thing that the BoeBot processes
                // directly after a command, because it is an emergency situation.
                enableEmergencyBrake();
                bluetoothReceiver.transmitCommand("Remote: Brake");
                break;
            default:
                System.out.println("Warning: Unknown remote command: " + receiverCode);
        }
    }

    /**
     * Runs when the button for the emergency brake is toggled. When the emergency brake is
     * not enabled, it enables the emergency brake. If it is enabled, though, it disables
     * the emergency brake.
     *
     * @author Simon
     */
    @Override
    public void onButtonEvent() {
        if (emergencyBrakeEnabled) {
            disableEmergencyBrake();
        } else {
            enableEmergencyBrake();
        }
    }

    /**
     * This method contains everything that needs to happen when the emergency brake is set
     * to enabled. It tells the application that the BoeBot has braked.
     */
    private void enableEmergencyBrake() {
        emergencyBrakeEnabled = true;
        lineSensors.setEnabled(false);
        ultrasonicClose.setEnabled(false);
        engine.brake();
        bluetoothReceiver.transmitCommand("Boebot: Brake");

        // The poor Boebot starts to panic because it has no logic for what to do now
        pixel0.blink(Color.RED, 300000, 600, true);
        pixel1.blink(Color.RED, 300000, 600, true);
        pixel2.blink(Color.RED, 300000, 600, true);
        pixel3.blink(Color.RED, 300000, 600, true);
        pixel4.blink(Color.RED, 300000, 600, true);
        pixel5.blink(Color.RED, 300000, 600, true);
        buzzer.repeatingBeep(300000, 600, true);
    }

    /**
     * This method contains everything that needs to happen when the emergency brake is set
     * to disabled. It tells the application that the BoeBot has resumed its tasks.
     */
    private void disableEmergencyBrake() {
        emergencyBrakeEnabled = false;
        listeningForCommands = true;

        if (gripper.isOpened()) ultrasonicClose.setEnabled(true);

        pixel0.resetBlink();
        pixel1.resetBlink();
        pixel2.resetBlink();
        pixel3.resetBlink();
        pixel4.resetBlink();
        pixel5.resetBlink();
        buzzer.resetRepeatingBeep();

        bluetoothReceiver.transmitCommand("Remote: Resume");
    }
}
