package application;

import TI.BoeBot;
import TI.Timer;
import hardware.Settings;
import hardware.inputdevices.Bluetooth;
import hardware.inputdevices.Button;
import hardware.inputdevices.IRReceiver;
import hardware.inputdevices.sensor.LineSensors;
import hardware.inputdevices.sensor.Sensor;
import hardware.inputdevices.sensor.UltrasonicSensor;
import hardware.outputdevices.Engine;
import hardware.outputdevices.Gripper;
import hardware.outputdevices.led.NeoPixel;
import link.Updatable;
import link.callbacks.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * The heart of the BoeBot application. It initializes the BoeBot and makes sure hardware is running.
 *
 * @author Team B2
 */
public class RobotMain implements IRReceiverCallback, SensorCallback, ButtonCallback, LineSensorsCallback, BluetoothCallback, UnknownObjectCallback {
    // ArrayList containing all hardware that needs to run constantly
    private static final ArrayList<Updatable> updatables = new ArrayList<>();

    // Settings of the current BoeBot setup. May differ between BoeBots.
    // TODO not yet all settings are in use. Fix this.
    private final Settings settings = new Settings(
            3, 200,
            25, 1500, 1497,
            1400, 1800,
            115200, 2200, 1250,
            14, 1, 13, 12, 0, 1, new int[]{0, 1, 2});

    // Input updatables, they check for input from surroundings
    private final UltrasonicSensor ultrasonicFront = new UltrasonicSensor(settings.ULTRASONIC_INPUT_PIN, settings.ULTRASONIC_OUTPUT_PIN, settings.ULTRASONIC_GRAB_THRESHOLD, settings.ULTRASONIC_UNKNOWN_OBJECT_THRESHOLD, this);
    private final IRReceiver irReceiver = new IRReceiver(settings.IR_RECEIVER_PIN, new NeoPixel(1), this);
    private final LineSensors lineSensors = new LineSensors(settings.LINE_SENSOR_ADC_PINS, this);
    private final Bluetooth bluetoothReceiver = new Bluetooth(settings.BLUETOOTH_BAUDRATE, this);

    // Output updatables, they alter the state of the BoeBot and/or the environment
    private final Engine engine = new Engine(settings.LEFT_WHEEL_PIN, settings.RIGHT_WHEEL_PIN, settings.ENGINE_STOP_SPEED_LEFT, settings.ENGINE_STOP_SPEED_RIGHT, settings.ENGINE_FORWARD_SPEED);
    private final Gripper gripper = new Gripper(settings.GRIPPER_PIN, settings.GRIPPER_OPEN_FREQUENCY, settings.GRIPPER_CLOSE_FREQUENCY);
    private final NeoPixel locationPixel = new NeoPixel(0, new Color(128, 0, 0));
    private final NeoPixel ultrasonicPixel = new NeoPixel(2, new Color(0, 128, 0));
    //TODO make NeoPixel manager for controlling NeoPixels that indicate direction
    private final NeoPixel leftTurnPixel = new NeoPixel(5);
    private final NeoPixel forwardPixel = new NeoPixel(4);
    private final NeoPixel rightTurnPixel = new NeoPixel(3);

    // Used as a cooldown for inputting coordinates, to prevent inputting the same number twice
    // when the button was only pressed once.
    private Timer coordinateInputTimer;

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
            if (coordinateInputTimer != null && coordinateInputTimer.timeout()) {
                // Resets this Timer
                coordinateInputTimer = null;
            }
            BoeBot.wait(10);
        }
    }

    /**
     * Runs when a crossroad is detected on the grid by the line sensors.
     *
     * @author Simon
     */
    @Override
    public void onDetectCrossroad() {
        String command = NavigationManager.nextCommandAndUpdate();

        if (command.equals("turn")) {
            engine.turn90(true);
        } else if (command.equals("brake")) {
            engine.brake();
            overrideLineSensors();
        }
        leftTurnPixel.setColorAndTurnOn(new Color(128, 0, 0));
        forwardPixel.setColorAndTurnOn(new Color(128, 0, 0));
        rightTurnPixel.setColorAndTurnOn(new Color(128, 0, 0));
    }

    /**
     * Runs when the line sensors detect that the BoeBot deviates from its route.
     * There is a slight delay between the actual detection and calling this method.
     * When this runs, the bot will try to align itself by steering toward the line.
     * @param toLeft Whether a deviation to the left occurred, or to the right.
     *
     * @author Simon
     */
    @Override
    public void onDeviate(boolean toLeft) {
        if (toLeft) {
            // Too far to left, so BoeBot should turn right
            engine.turnSpeed(25, -50);
            leftTurnPixel.setColorAndTurnOn(new Color(128, 0, 0));
            rightTurnPixel.turnOff();
        } else {
            // Too far to right, so BoeBot should turn left
            engine.turnSpeed(-50, 25);
            leftTurnPixel.turnOff();
            rightTurnPixel.setColorAndTurnOn(new Color(128, 0, 0));
        }
        forwardPixel.turnOff();
    }

    /**
     * Runs when there is no deviation or crossroad detected, meaning the
     * BoeBot will just drive forward.
     *
     * @author Simon
     */
    @Override
    public void onDriveStraight() {
        engine.drive(25);
        leftTurnPixel.turnOff();
        forwardPixel.setColorAndTurnOn(new Color(128, 0, 0));
        rightTurnPixel.turnOff();
    }

    /**
     * Runs when the infrared receiver callback is triggered. It decides
     * which action to undertake based on its parameter.
     *
     * @author Timo and Simon
     */
    @Override
    public void onIRReceiverEvent(int receiverCode) {
        if (receiverCode == 12) {
            // Button: Enter (001100010000)
            enableUltrasonic();
        } else if (receiverCode == 16) {
            // Button: Ch+ (000010010000)
            engine.drive(25);
            overrideLineSensors();
        } else if (receiverCode == 17) {
            // Button: Ch- (100010010000)
            engine.drive(-25);
            overrideLineSensors();
        } else if (receiverCode == 18) {
            // Button: Vol+ > (010010010000)
            engine.turn90(false);
            overrideLineSensors();
        } else if (receiverCode == 19) {
            // Button: < Vol- (110010010000)
            engine.turn90(true);
            overrideLineSensors();
        } else if (receiverCode == 20) {
            // Button: Mute sound (001010010000)
            gripper.open();
        } else if (receiverCode == 21) {
            // Button: Power (101010010000)
            engine.brake();
            overrideLineSensors();
        } else if (coordinateInputTimer == null) {
            int numpadNumber = receiverCode + 1;

            // The button for 0 will have numpadNumber 10, but has to be set to 0.
            if (numpadNumber == 10) receiverCode = 0;

            // Check if the code was a numpad number
            if (receiverCode < 10) {
                if (Arrays.equals(NavigationManager.getDestination(), new Integer[]{null, null})) {
                    // Set X-coordinate
                    NavigationManager.setX(numpadNumber);
                    locationPixel.setColorAndTurnOn(new Color(128, 123, 0));
                    coordinateInputTimer = new Timer(500);
                    coordinateInputTimer.mark();
                } else if (NavigationManager.getDestination()[1] == null) {
                    // Set Y-coordinate
                    NavigationManager.setY(numpadNumber);
                    locationPixel.setColorAndTurnOn(Color.BLACK);
                    lineSensors.enable();
                    enableUltrasonic();
                }
            }
        }
    }

    /**
     * Runs when any sensor's callback is triggered. Executes code based
     * on which sensor triggered this method.
     * @param source the type of Sensor that triggered the method.
     *
     * @author Simon
     */
    @Override
    public void onSensorEvent(Sensor source) {
        if (source == ultrasonicFront) {
            gripper.close();
            ultrasonicPixel.setColorAndTurnOn(new Color(128, 0, 0));
        }
    }

    @Override
    public void onDetectedUnknownObject() {
        // buzzer and neopixels go beserk
        engine.brake();
        overrideLineSensors();
    }

    /**
     * Runs when the bluetooth chip receives a signal. Executes code based
     * on the data parameter.
     * @param data the data which is transmitted to the BoeBot.
     *
     * @author Simon
     */
    @Override
    public void onBluetoothEvent(int data) {

    }

    /**
     * Runs when the Button callback is triggered. Executes code based
     * on which Button triggered this method.
     * @param source the type of Button that triggered the method.
     *
     * @author Simon
     */
    @Override
    public void onButtonEvent(Button source) {

    }

    /**
     * Turns off line sensors and therefore the coordinate
     * navigation ability of the BoeBot.
     *
     * @author Simon
     */
    private void overrideLineSensors() {
        lineSensors.disable();
        NavigationManager.resetDestination();
        locationPixel.setColorAndTurnOn(new Color(128, 0, 0));
        leftTurnPixel.turnOff();
        forwardPixel.turnOff();
        rightTurnPixel.turnOff();
    }

    /**
     * Method to re-enable the ultrasonic sensor
     *
     * @author Simon
     */
    private void enableUltrasonic() {
        ultrasonicFront.enable();
        ultrasonicPixel.setColorAndTurnOn(new Color(0, 128, 0));
        gripper.open();
    }
}
