package application;

import TI.BoeBot;
import hardware.inputdevices.Button;
import hardware.inputdevices.IRReceiver;
import hardware.inputdevices.sensor.AntennaSensor;
import hardware.inputdevices.sensor.Sensor;
import hardware.inputdevices.sensor.UltrasonicSensor;
import hardware.outputdevices.Engine;
import hardware.outputdevices.Gripper;
import link.Updatable;
import link.callbacks.ButtonCallback;
import link.callbacks.IRReceiverCallback;
import link.callbacks.LineSensorsCallback;
import link.callbacks.SensorCallback;

import java.util.ArrayList;

public class RobotMain implements IRReceiverCallback, SensorCallback, ButtonCallback,
        LineSensorsCallback {
    // ArrayList containing all hardware
    private static ArrayList<Updatable> devices = new ArrayList<>();

    // Input devices, they check for input from surroundings
    private AntennaSensor antennaFront = new AntennaSensor(0, this);
    private UltrasonicSensor ultrasonicFront = new UltrasonicSensor(0, 0, this);
    private IRReceiver irReceiver = new IRReceiver((byte) 0, this);

    // Output devices, they alter the state of the BoeBot and/or the environment
    private Engine engine = new Engine(0, 0);
    private Gripper gripper = new Gripper(0);

    private boolean overrideMode = false;

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
     * Adds all hardware to the devices ArrayList, so they can be updated.
     */
    private void init() {
        devices.add(antennaFront);
        devices.add(ultrasonicFront);
        devices.add(irReceiver);
    }

    /**
     * Continuously calls the update method in every hardware class
     */
    private void run() {
        while(true) {
            for (Updatable device : devices) {
                device.update();
            }
            BoeBot.wait(10);
        }
    }

    @Override
    public void onDetectCrossroad() {
        if (!overrideMode) {
            engine.turnDegrees(90, 1600);
        }
    }

    @Override
    public void onDeviateRight() {
        if (!overrideMode) {
            engine.turnDegrees(20, 1600);
        }
    }

    @Override
    public void onDeviateLeft() {
        if (!overrideMode) {
            engine.turnDegrees(-20, 1600);
        }
    }

    @Override
    public void onDriveStraight() {

    }

    /**
     * Runs when the infrared receiver callback is triggered
     */
    @Override
    public void onIRReceiverEvent(String command) {
        overrideMode = true;
    }

    /**
     * Runs when any sensor's callback is triggered. Executes code based
     * on which sensor triggered this method.
     * @param source the type of Sensor that triggered the method.
     */
    @Override
    public void onSensorEvent(Sensor source) {
        if (source == antennaFront) {

        } else if (source == ultrasonicFront) {
            if (ultrasonicFront.getSensorValue() > 200) {
                gripper.open();
            } else {
                gripper.close();
                ultrasonicFront.disable();
            }
        }
    }

    @Override
    public void onButtonEvent(Button source) {

    }
}
