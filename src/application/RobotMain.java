package application;

import TI.BoeBot;
import TI.Timer;
import hardware.Settings;
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

//todo connect buzzer to pin number in settings

/**
 * The heart of the BoeBot application. It initializes the BoeBot and makes sure hardware is running.
 *
 * @author Team B2
 */
public class RobotMain implements IRReceiverCallback, UltrasonicCallback, ButtonCallback, LineSensorsCallback, BluetoothCallback, EngineCallback {
    // ArrayList containing all hardware that needs to run constantly
    private static final ArrayList<Updatable> updatables = new ArrayList<>();

    // Settings of the current BoeBot setup. May differ between BoeBots.
    private final Settings settings = new Settings(
            500,
            3, 100,
            25, 50, 0, -3, 1000,
            800,
            1400, 1800, 400, 700, 500,
            115200, 2200, 1250,
            14, 1, 13, 12, 0, 1, new int[]{0, 1, 2}, 2);

    // Output updatables, they alter the state of the BoeBot and/or the environment
    private final Engine engine = new Engine(settings.LEFT_WHEEL_PIN, settings.RIGHT_WHEEL_PIN, settings.ENGINE_NEUTRAL_OFFSET_LEFT, settings.ENGINE_NEUTRAL_OFFSET_RIGHT, settings.ENGINE_FORWARD_SPEED, settings.ENGINE_BACK_STEER_SPEED, settings.ENGINE_TURN_TIME, this);
    private final Gripper gripper = new Gripper(settings.GRIPPER_PIN, settings.GRIPPER_OPEN_FREQUENCY, settings.GRIPPER_CLOSE_FREQUENCY);
    private final NeoPixel pixel0 = new NeoPixel(0, Color.BLACK);
    private final NeoPixel pixel1 = new NeoPixel(1, Color.BLACK);
    private final NeoPixel pixel2 = new NeoPixel(2, Color.BLACK);
    private final NeoPixel pixel3 = new NeoPixel(3, Color.BLACK);
    private final NeoPixel pixel4 = new NeoPixel(4, Color.BLACK);
    private final NeoPixel pixel5 = new NeoPixel(5, Color.BLACK);
    private final Buzzer buzzer = new Buzzer(settings.BUZZER_PIN);

    // Input updatables, they check for input from surroundings
    private final UltrasonicSensor ultrasonicFront = new UltrasonicSensor(settings.ULTRASONIC_INPUT_PIN, settings.ULTRASONIC_OUTPUT_PIN, settings.ULTRASONIC_GRAB_THRESHOLD, settings.ULTRASONIC_UNKNOWN_OBJECT_THRESHOLD, pixel2, this);
    private final IRReceiver irReceiver = new IRReceiver(settings.IR_RECEIVER_PIN, pixel0, settings.IR_RECEIVER_BIT_THRESHOLD, this);
    private final LineSensors lineSensors = new LineSensors(settings.LINE_SENSOR_ADC_PINS, settings.LINE_SENSOR_THRESHOLD, settings.LINE_SENSOR_CEILING, settings.LINE_SENSORS_WAIT_AFTER_DEVIATION, settings.LINE_SENSORS_WAIT_BEFORE_CROSSROAD, settings.LINE_SENSORS_DELAY_AFTER_CROSSROAD, this);
    private final Bluetooth bluetoothReceiver = new Bluetooth(settings.BLUETOOTH_BAUDRATE, this);


    private boolean isListening = true;
    private boolean emergencyBrakeEnabled = false;
    private Timer objectPlacementTimer;

    /**
     * Runs when the BoeBot has booted up. Makes sure initialization happens and that hardware will execute
     *
     * @author Simon
     */
    public static void main(String[] args) {
        // Cannot call run() directly, since the main method is static
        RobotMain main = new RobotMain();
        main.init();
        main.run();
    }

    /**
     * Adds all hardware to the updatables ArrayList, so they can be updated constantly.
     *
     * @author Simon
     */
    private void init() {
        updatables.add(ultrasonicFront);
        updatables.add(irReceiver);
        updatables.add(lineSensors);
        updatables.add(engine);
        updatables.add(bluetoothReceiver);
    }

    /**
     * Continuously calls the update method in every hardware class. This is equivalent
     * to 'running' the hardware.
     *
     * @author Simon
     */
    private void run() {
        while (true) {
            // Runs update method of all things listed in the updatables variable
            for (Updatable updatable : updatables) {
                updatable.update();
            }

            if (objectPlacementTimer != null && objectPlacementTimer.timeout()) {
                gripper.open();
                objectPlacementTimer = null;
            }

            BoeBot.wait(1);
        }
    }

    @Override
    public void onDetectCrossroad() {
        lineSensors.disable();
        engine.brake();

        if (engine.isInReverse()) {
            ultrasonicFront.enable();
            pixel1.resetBlink();
            buzzer.resetRepeatingBeep();
            engine.setReverseMode(false);
        }
        isListening = true;
        bluetoothReceiver.transmitCommand("Boebot: Succeeded");
    }

    /**
     * Runs when the line sensors detect that the BoeBot deviates from its route.
     * There is a slight delay between the actual detection and calling this method.
     * When this runs, the bot will try to align itself by steering toward the line.
     *
     * @param toLeft Whether a deviation to the left occurred, or to the right.
     * @author Simon
     */
    @Override
    public void onDeviate(boolean toLeft) {
        if (toLeft) {
            // Too far to left, so BoeBot should turn right
            engine.changeDirection(false);
        } else {
            // Too far to right, so BoeBot should turn left
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
        engine.drive();
    }

    /**
     * Runs when the infrared receiver callback is triggered. It decides
     * which action to undertake based on its parameter.
     *
     * @author Timo and Simon
     */
    @Override
    public void onIRReceiverEvent(int receiverCode) {
        switch (receiverCode) {
            case 12:
                // Button: Enter (001100010000)
                if (emergencyBrakeEnabled) {
                    disableEmergencyBrake();
                    bluetoothReceiver.transmitCommand("Remote: Resume");
                }
                break;
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
                bluetoothReceiver.transmitCommand("Remote: Brake");
                break;
            default:
                System.out.println("Warning: Unknown remote command: " + receiverCode);
        }
    }

    /**
     * Runs when any sensor's callback is triggered. Executes code based
     * on which sensor triggered this method.
     *
     * @param isUnknownObject whether the object detected is an unknown object or
     * @author Simon
     */
    @Override
    public void onSensorEvent(boolean isUnknownObject) {
        if (isUnknownObject) {
            bluetoothReceiver.transmitCommand("Boebot: Object");
        } else {
            gripper.close();
        }
    }

    @Override
    public void onCompletedTurn() {
        isListening = true;
        bluetoothReceiver.transmitCommand("Boebot: Succeeded");
    }

    /**
     * Runs when the bluetooth chip receives a signal. Executes code based
     * on the data parameter.
     *
     * @param command the command which is transmitted to the BoeBot.
     * @author Simon and Kerr
     */
    @Override
    public void onBluetoothEvent(String command) {
        if (command.equals("Application: Brake")) {
            // brake
            enableEmergencyBrake();
            return;
        }

        if (isListening && !emergencyBrakeEnabled) {
            switch (command) {
                case "Application: Forward":
                    // forward
                    engine.drive();
                    lineSensors.delayedEnable();
                    break;
                case "Application: Left":
                    // turn left
                    engine.turn90(true);
                    pixel5.blink(Color.ORANGE, 1000, 2, false);
                    break;
                case "Application: Right":
                    // turn right
                    engine.turn90(false);
                    pixel3.blink(Color.ORANGE, 1000, 2, false);
                    break;
                case "Application: Place":
                    // place object
                    engine.setReverseMode(true);
                    engine.drive();
                    lineSensors.delayedEnable();
                    pixel1.blink(new Color(100, 100, 100), 10000, 20, false);
                    buzzer.repeatingBeep(10000, 20, false);
                    objectPlacementTimer = new Timer(settings.ROBOTMAIN_OBJECT_PLACEMENT_TIME);
                    objectPlacementTimer.mark();
                    break;
                default:
                    System.out.println("Warning: Unknown bluetooth command: " + command);
            }

            isListening = false;
        }
    }

    /**
     * Runs when the Button callback is triggered.
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

    private void enableEmergencyBrake() {
        emergencyBrakeEnabled = true;
        lineSensors.disable();
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

    private void disableEmergencyBrake() {
        emergencyBrakeEnabled = false;
        ultrasonicFront.enable();
        pixel0.resetBlink();
        pixel1.resetBlink();
        pixel2.resetBlink();
        pixel3.resetBlink();
        pixel4.resetBlink();
        pixel5.resetBlink();
        buzzer.resetRepeatingBeep();
    }
}
