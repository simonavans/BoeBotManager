package hardware.inputdevices.sensor;

import application.RobotMain;
import link.Updatable;

import java.util.Arrays;

public class LineSensors extends Updatable {
    private RobotMain callback;
    private LineSensor sensorLeft;
    private LineSensor sensorMiddle;
    private LineSensor sensorRight;
    private boolean[] detectionStates = {false, false, false};
    private boolean detectedCrossroad = false;

    public LineSensors(int[] pinNumbers, boolean[] pinModes, RobotMain callback) {
        super(pinNumbers, pinModes);
        sensorLeft = new LineSensor(pinNumbers[0], this);
        sensorMiddle = new LineSensor(pinNumbers[1], this);
        sensorRight = new LineSensor(pinNumbers[2], this);
        this.callback = callback;
    }

    @Override
    public void update() {
        if (Arrays.equals(detectionStates, new boolean[]{true, true, true}) &&
                !detectedCrossroad) {
            callback.onDetectCrossroad();
            detectedCrossroad = true;
        } else if (detectionStates[0] && !detectionStates[2]) {
            callback.onDeviateRight();
            detectedCrossroad = false;
        } else if (!detectionStates[0] && detectionStates[2]) {
            callback.onDeviateLeft();
            detectedCrossroad = false;
        } else if (detectionStates[0] && detectionStates[2]) {
            callback.onDriveStraight();
            detectedCrossroad = false;
        }
    }

    public void onLineDetectEvent(LineSensor source) {
        if (source == sensorLeft) {
            detectionStates[0] = true;
        } else if (source == sensorMiddle) {
            detectionStates[1] = true;
        } else if (source == sensorRight) {
            detectionStates[2] = true;
        }
    }

    public void onLineUndetectEvent(LineSensor source) {
        if (source == sensorLeft) {
            detectionStates[0] = false;
        } else if (source == sensorMiddle) {
            detectionStates[1] = false;
        } else if (source == sensorRight) {
            detectionStates[2] = false;
        }
    }
}
