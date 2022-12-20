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
import hardware.outputdevices.led.BasicLED;
import hardware.outputdevices.led.NeoPixel;
import link.Updatable;
import link.callbacks.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

public class RobotMain implements IRReceiverCallback, SensorCallback, ButtonCallback,
        LineSensorsCallback, BluetoothCallback {
    // ArrayList containing all hardware
    private static final ArrayList<Updatable> updatables = new ArrayList<>();

    // Settings of the current BoeBot setup. May differ between BoeBots.
    // TODO not yet all settings are in use. Fix this.
    private final Settings settings = new Settings(3, Integer.MAX_VALUE, 1500, 1400, 1800,
            115200, 2200, 1250, 14, 1, 13,
            12, 0, 1, new int[]{0, 1, 2});

    // Input updatables, they check for input from surroundings
    private final UltrasonicSensor ultrasonicFront = new UltrasonicSensor(settings.ULTRASONIC_INPUT_PIN, settings.ULTRASONIC_OUTPUT_PIN, this);
    private final IRReceiver irReceiver = new IRReceiver(settings.IR_RECEIVER_PIN, this);
    private final LineSensors lineSensors = new LineSensors(settings.LINE_SENSOR_ADC_PINS, this);
    private final Bluetooth bluetoothReceiver = new Bluetooth(this, settings.BLUETOOTH_BAUDRATE);

    // Output updatables, they alter the state of the BoeBot and/or the environment
    private final Engine engine = new Engine(settings.LEFT_WHEEL_PIN, settings.RIGHT_WHEEL_PIN);
    private final Gripper gripper = new Gripper(settings.GRIPPER_PIN);
    private final NeoPixel irPixel = new NeoPixel(1, Color.BLACK);
    private final NeoPixel locationPixel = new NeoPixel(0, new Color(128, 0, 0));
    private final NeoPixel ultrasonicPixel = new NeoPixel(2, new Color(0, 128, 0));
    //TODO make NeoPixel manager for controlling NeoPixels that indicate direction
    private final NeoPixel leftLinePixel = new NeoPixel(5, Color.BLACK);
    private final NeoPixel middleLinePixel = new NeoPixel(4, Color.BLACK);
    private final NeoPixel rightLinePixel = new NeoPixel(3, Color.BLACK);
    private final BasicLED tempBluetoothLED = new BasicLED(2); //TODO Temp LED for demo, remove later

    // Used as a cooldown for inputting coordinates, to prevent inputting the same number twice
    // when the button was only pressed once.
    private Timer coordinateInputTimer;


    /**
     * Runs when the BoeBot has started up (only if the code in this project has been
     * uploaded to the BoeBot).
     */
    public static void main(String[] args) {
        // Cannot call run() directly, since the main method is static
        RobotMain main = new RobotMain();
        main.init();
        main.run();
    }

    /**
     * Adds all hardware to the updatables ArrayList, so they can be updated.
     */
    private void init() {
        updatables.add(ultrasonicFront);
        updatables.add(irReceiver);
        updatables.add(lineSensors);
        updatables.add(engine);
        updatables.add(bluetoothReceiver);
    }

    /**
     * Continuously calls the update method in every hardware class
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

    @Override
    public void onDetectCrossroad() {
        String command = NavigationManager.nextCommandAndUpdate();

        if (command.equals("turn")) {
            engine.turn90(true);
        } else if (command.equals("brake")) {
            engine.brake();
            overrideLineSensors();
        }
        leftLinePixel.setColorAndTurnOn(new Color(128, 0, 0));
        middleLinePixel.setColorAndTurnOn(new Color(128, 0, 0));
        rightLinePixel.setColorAndTurnOn(new Color(128, 0, 0));
    }

    @Override
    public void onDeviate(boolean toLeft) {
        if (toLeft) {
            // Too far to left, so BoeBot should turn right
            engine.turnSpeed(25, -50);
            leftLinePixel.setColorAndTurnOn(new Color(128, 0, 0));
            rightLinePixel.setColorAndTurnOn(Color.BLACK);
        } else {
            // Too far to right, so BoeBot should turn left
            engine.turnSpeed(-50, 25);
            leftLinePixel.setColorAndTurnOn(Color.BLACK);
            rightLinePixel.setColorAndTurnOn(new Color(128, 0, 0));
        }
        middleLinePixel.setColorAndTurnOn(Color.BLACK);
    }

    @Override
    public void onDriveStraight() {
        engine.drive(25);
        leftLinePixel.setColorAndTurnOn(Color.BLACK);
        middleLinePixel.setColorAndTurnOn(new Color(128, 0, 0));
        rightLinePixel.setColorAndTurnOn(Color.BLACK);
    }

    /**
     * Runs when the infrared receiver callback is triggered
     */
    @Override
    public void onIRReceiverEvent(int receiverCode) {
        irPixel.setColorAndTurnOn(new Color(100, 100, 100));

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

    @Override
    public void onStopReceiving() {
        irPixel.turnOff();
    }

    /**
     * Runs when any sensor's callback is triggered. Executes code based
     * on which sensor triggered this method.
     *
     * @param source the type of Sensor that triggered the method.
     */
    @Override
    public void onSensorEvent(Sensor source) {
        if (source == ultrasonicFront) {
            gripper.close();
            ultrasonicPixel.setColorAndTurnOn(new Color(128, 0, 0));
        }
    }

    @Override
    public void onBluetoothEvent(int data) {
        if (data == 48) {
            tempBluetoothLED.turnOff();
        } else if (data == 49) {
            tempBluetoothLED.turnOn();
        }

    }

    @Override
    public void onButtonEvent(Button source) {

    }

    private void overrideLineSensors() {
        lineSensors.disable();
        NavigationManager.resetDestination();
        locationPixel.setColorAndTurnOn(new Color(128, 0, 0));
        leftLinePixel.setColorAndTurnOn(Color.BLACK);
        middleLinePixel.setColorAndTurnOn(Color.BLACK);
        rightLinePixel.setColorAndTurnOn(Color.BLACK);
    }

    private void enableUltrasonic() {
        ultrasonicFront.enable();
        ultrasonicPixel.setColorAndTurnOn(new Color(0, 128, 0));
        gripper.open();
    }
}
