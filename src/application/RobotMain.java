package application;

import TI.BoeBot;
import TI.Timer;
import hardware.inputdevices.Button;
import hardware.inputdevices.IRReceiver;
import hardware.inputdevices.sensor.LineSensors;
import hardware.inputdevices.sensor.Sensor;
import hardware.inputdevices.sensor.UltrasonicSensor;
import hardware.outputdevices.Engine;
import hardware.outputdevices.Gripper;
import hardware.outputdevices.led.NeoPixel;
import link.Updatable;
import link.callbacks.ButtonCallback;
import link.callbacks.IRReceiverCallback;
import link.callbacks.LineSensorsCallback;
import link.callbacks.SensorCallback;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

public class RobotMain implements IRReceiverCallback, SensorCallback, ButtonCallback,
        LineSensorsCallback {
    // ArrayList containing all hardware
    private static ArrayList<Updatable> updatables = new ArrayList<>();

    // Input updatables, they check for input from surroundings
    private UltrasonicSensor ultrasonicFront = new UltrasonicSensor(14, 1, this);
    private IRReceiver irReceiver = new IRReceiver(15, this);
    //TODO change line sensor pin order on the BoeBot
    private LineSensors lineSensors = new LineSensors(new int[]{0, 1, 2}, this);

    // Output updatables, they alter the state of the BoeBot and/or the environment
    private Engine engine = new Engine(13, 12);
    private Gripper gripper = new Gripper(0);
    private NeoPixel irPixel = new NeoPixel(1, Color.BLACK);
    private NeoPixel locationPixel = new NeoPixel(0, new Color(128, 0, 0));
    //TODO make NeoPixel manager for controlling NeoPixels that indicate direction
    private NeoPixel pixelLeft = new NeoPixel(5, Color.BLACK);
    private NeoPixel pixelMiddle = new NeoPixel(4, Color.BLACK);
    private NeoPixel pixelRight = new NeoPixel(3, Color.BLACK);

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
    }

    /**
     * Continuously calls the update method in every hardware class
     */
    private void run() {
        while (true) {
            for (Updatable updatable : updatables) {
                updatable.update();
            }
            if (coordinateInputTimer != null && coordinateInputTimer.timeout()) {
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
        pixelLeft.setColorAndTurnOn(new Color(128, 0, 0));
        pixelMiddle.setColorAndTurnOn(new Color(128, 0, 0));
        pixelRight.setColorAndTurnOn(new Color(128, 0, 0));
    }

    @Override
    public void onDeviate(boolean toLeft) {
        if (toLeft) {
            // Too far to left, so BoeBot should turn right
            engine.turnSpeed(25, -50);
            pixelLeft.setColorAndTurnOn(new Color(128, 0, 0));
            pixelRight.setColorAndTurnOn(Color.BLACK);
        } else {
            // Too far to right, so BoeBot should turn left
            engine.turnSpeed(-50, 25);
            pixelLeft.setColorAndTurnOn(Color.BLACK);
            pixelRight.setColorAndTurnOn(new Color(128, 0, 0));
        }
        pixelMiddle.setColorAndTurnOn(Color.BLACK);
    }

    @Override
    public void onDriveStraight() {
        engine.drive(25);
        pixelLeft.setColorAndTurnOn(Color.BLACK);
        pixelMiddle.setColorAndTurnOn(new Color(128, 0, 0));
        pixelRight.setColorAndTurnOn(Color.BLACK);
    }

    /**
     * Runs when the infrared receiver callback is triggered
     */
    @Override
    public void onIRReceiverEvent(String command) {
        //TODO check receiver codes
        //  Make brake button the on/off button on the remote
        if (command.equals("001010010000")) {
            // Button: Mute sound
            gripper.open();
        } else if (command.equals("101010010000")) {
            // Button: Power
            engine.brake();
            overrideLineSensors();
        } else if (command.equals("001100010000")) {
            // Button: Enter
            ultrasonicFront.enable();
        } else if (command.equals("010010010000")) {
            // Button: Vol+ >
            engine.turn90(false);
            overrideLineSensors();
        } else if (command.equals("110010010000")) {
            // Button: < Vol-
            engine.turn90(true);
            overrideLineSensors();
        } else if (command.equals("100010010000")) {
            // Button: Ch-
            engine.drive(-25);
            overrideLineSensors();
        } else if (command.equals("000010010000")) {
            // Button: Ch+
            engine.drive(25);
            overrideLineSensors();
        } else if (coordinateInputTimer == null) {
            int code = Integer.parseInt(new StringBuilder(command.substring(0, 7)).reverse().toString(), 2);
            if (code <= 9) {
                int selectedNumber = code + 1;

                if (Arrays.equals(NavigationManager.getDestination(), new Integer[]{null, null})) {
                    NavigationManager.setX(selectedNumber);
                    locationPixel.setColorAndTurnOn(new Color(128, 80, 0));
                    coordinateInputTimer = new Timer(500);
                    coordinateInputTimer.mark();
                } else if (NavigationManager.getDestination()[1] == null) {
                    NavigationManager.setY(selectedNumber);
                    locationPixel.setColorAndTurnOn(Color.BLACK);
                    lineSensors.enable();
                }
            }
        }
        irPixel.setColorAndTurnOn(new Color(100, 100, 100));
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
        }
    }

    @Override
    public void onButtonEvent(Button source) {

    }

    public void overrideLineSensors() {
        lineSensors.disable();
        NavigationManager.resetDestination();
        locationPixel.setColorAndTurnOn(new Color(128, 0, 0));
    }
}
