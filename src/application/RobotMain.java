package application;

import TI.BoeBot;
import hardware.inputdevices.Button;
import hardware.inputdevices.IRReceiver;
import hardware.inputdevices.sensor.LineSensors;
import hardware.inputdevices.sensor.Sensor;
import hardware.inputdevices.sensor.UltrasonicSensor;
import hardware.outputdevices.Engine;
import hardware.outputdevices.Gripper;
import link.Updatable;
import link.callbacks.ButtonCallback;
import link.callbacks.IRReceiverCallback;
import link.callbacks.LineSensorsCallback;
import link.callbacks.SensorCallback;

import java.awt.*;
import java.util.ArrayList;

public class RobotMain implements IRReceiverCallback, SensorCallback, ButtonCallback,
        LineSensorsCallback {
    // ArrayList containing all hardware
    private static ArrayList<Updatable> devices = new ArrayList<>();

    // Input devices, they check for input from surroundings
    private UltrasonicSensor ultrasonicFront = new UltrasonicSensor(14, 1, this);
    private IRReceiver irReceiver = new IRReceiver((byte) 15, this);
    private LineSensors lineSensors = new LineSensors(new int[]{2, 1, 0}, this);

    // Output devices, they alter the state of the BoeBot and/or the environment
    private Engine engine = new Engine(13, 12);
    private Gripper gripper = new Gripper(0);

    private boolean overrideMode = false;

    private int counter = 0;

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
        devices.add(ultrasonicFront);
        devices.add(irReceiver);
        devices.add(lineSensors);
        devices.add(engine);
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
            engine.turn90(25, -50);
            BoeBot.rgbSet(3, new Color(128, 0,0));
            BoeBot.rgbSet(4, new Color(128, 0,0));
            BoeBot.rgbSet(5, new Color(128, 0,0));
            BoeBot.rgbShow();
        }
    }

    @Override
    public void onDeviate(boolean toLeft) {
        if (!overrideMode) {
            if (toLeft) {
                engine.turnSpeed(25, -50);
                BoeBot.rgbSet(3, new Color(0, 0,0));
                BoeBot.rgbSet(4, new Color(0, 0,0));
                BoeBot.rgbSet(5, new Color(128, 0,0));
                BoeBot.rgbShow();
            } else {
                engine.turnSpeed(-50, 25);
                BoeBot.rgbSet(3, new Color(128, 0,0));
                BoeBot.rgbSet(4, new Color(0, 0,0));
                BoeBot.rgbSet(5, new Color(0, 0,0));
                BoeBot.rgbShow();
            }
        }
    }

    @Override
    public void onDriveStraight() {
        if (!overrideMode) {
            engine.drive(25);
        }
        BoeBot.rgbSet(3, new Color(0, 0,0));
        BoeBot.rgbSet(4, new Color(128, 0,0));
        BoeBot.rgbSet(5, new Color(0, 0,0));
        BoeBot.rgbShow();
    }

    /**
     * Runs when the infrared receiver callback is triggered
     */
    @Override
    public void onIRReceiverEvent(String command) {
        overrideMode = true;

        if (command.equals("000000010000")) {
            gripper.open();
        } else if (command.equals("010000010000")) {
            gripper.close();
        } else if (command.equals("010010010000")) {
            engine.turn90(25, -50);
        } else if (command.equals("110010010000")) {
            engine.turn90(-50, 25);
        } else if (command.equals("100010010000")) {
            engine.drive(-25);
        } else if (command.equals("000010010000")) {
            engine.drive(25);
        } else if (command.equals("100000010000")) {
            engine.brake();
        }
    }

    /**
     * Runs when any sensor's callback is triggered. Executes code based
     * on which sensor triggered this method.
     * @param source the type of Sensor that triggered the method.
     */
    @Override
    public void onSensorEvent(Sensor source) {
        if (source == ultrasonicFront) {
            if (ultrasonicFront.getSensorValue() > 200) {
//                gripper.open();
            } else {
//                gripper.close();
//                ultrasonicFront.disable();
            }
        }
    }

    @Override
    public void onButtonEvent(Button source) {

    }
}
