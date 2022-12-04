package application;

import TI.BoeBot;
import hardware.inputdevices.Button;
import hardware.outputdevices.Buzzer;
import hardware.outputdevices.led.LED;
import link.Updatable;
import hardware.inputdevices.sensor.AntennaSensor;
import hardware.inputdevices.sensor.LineSensor;
import hardware.inputdevices.IRReceiver;
import hardware.inputdevices.sensor.Sensor;
import hardware.inputdevices.sensor.UltrasonicSensor;
import hardware.outputdevices.Engine;
import hardware.outputdevices.Gripper;
import link.callbacks.*;

import java.util.ArrayList;

public class RobotMain implements IRReceiverCallback, SensorCallback, ButtonCallback {
    // ArrayList containing all hardware
    private static ArrayList<Updatable> devices = new ArrayList<>();

    // Input devices, they check for input from surroundings
    private AntennaSensor antennaFront = new AntennaSensor((byte) 0, this);
    private UltrasonicSensor ultrasonicFront = new UltrasonicSensor((byte) 0, (byte) 0, this);
    private LineSensor lineSensorLeft = new LineSensor((byte) 0, this);
    private LineSensor lineSensorMiddle = new LineSensor((byte) 0, this);
    private LineSensor lineSensorRight = new LineSensor((byte) 0, this);
    private IRReceiver irReceiver = new IRReceiver((byte) 0, this);

    // Output devices, they alter the state of the BoeBot and/or the environment
    private Engine engine = new Engine((byte) 0, (byte) 0);
    private Gripper gripper = new Gripper((byte) 0);

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
        devices.add(lineSensorLeft);
        devices.add(lineSensorMiddle);
        devices.add(lineSensorRight);
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

    /**
     * Runs when the infrared receiver callback is triggered
     */
    @Override
    public void onIRReceiverEvent() {

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

        } else if (source == lineSensorLeft) {

        } else if (source == lineSensorMiddle) {

        } else if (source == lineSensorRight) {

        }
    }

    @Override
    public void onButtonEvent(Button source) {

    }
}
