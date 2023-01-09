package link;

import TI.Timer;
import application.RobotMain;
import hardware.inputdevices.sensor.LineSensor;
import link.callbacks.LineSensorCallback;

import java.util.Arrays;

public class LineSensors implements Updatable, LineSensorCallback {
    private final int waitAfterDeviation;
    private final int waitBeforeCrossroad;
    private final int waitAfterCrossroad;
    private RobotMain callback;

    private LineSensor sensorLeft;
    private LineSensor sensorMiddle;
    private LineSensor sensorRight;
    private boolean[] seesLineStates = {false, false, false};

    private Timer beforeCrossroadTimer;
    private Timer deviateLeftTimer;
    private Timer deviateRightTimer;
    private boolean detectedCrossroad;

    private boolean enabled = false;
    private Timer delayedEnablingTimer;

    public LineSensors(int[] pinNumbers, int lineSensorThreshold, int lineSensorCeiling, int waitAfterDeviation, int waitBeforeCrossroad, int waitAfterCrossroad, RobotMain callback) {
        sensorLeft = new LineSensor(pinNumbers[0], lineSensorThreshold, lineSensorCeiling, this);
        sensorMiddle = new LineSensor(pinNumbers[1], lineSensorThreshold, lineSensorCeiling, this);
        sensorRight = new LineSensor(pinNumbers[2], lineSensorThreshold, lineSensorCeiling, this);

        this.waitAfterDeviation = waitAfterDeviation;
        this.waitBeforeCrossroad = waitBeforeCrossroad;
        this.waitAfterCrossroad = waitAfterCrossroad;
        this.callback = callback;
    }

    @Override
    public void update() {
        if (enabled) {
            sensorLeft.update();
            sensorMiddle.update();
            sensorRight.update();

            if (!seesLineStates[0] && !seesLineStates[2]) {
                detectedCrossroad = false;
                callback.onDriveStraight();
                return;
            }

            if (beforeCrossroadTimer != null && beforeCrossroadTimer.timeout()) {
                beforeCrossroadTimer = null;
                callback.onDetectCrossroad();
                return;
            }

            if (deviateLeftTimer == null) {
                if (!seesLineStates[0] && seesLineStates[2]) {
                    deviateLeftTimer = new Timer(waitAfterDeviation);
                    deviateLeftTimer.mark();
                }
            } else if (deviateLeftTimer.timeout() && !detectedCrossroad) {
                deviateLeftTimer = null;
                callback.onDeviate(true);
                return;
            }

            if (deviateRightTimer == null) {
                if (seesLineStates[0] && !seesLineStates[2]) {
                    deviateRightTimer = new Timer(waitAfterDeviation);
                    deviateRightTimer.mark();
                }
            } else if (deviateRightTimer.timeout() && !detectedCrossroad) {
                deviateRightTimer = null;
                callback.onDeviate(false);
                return;
            }

            if (seesLineStates[0] && seesLineStates[1] && seesLineStates[2]) {
                detectedCrossroad = true;
                beforeCrossroadTimer = new Timer(waitBeforeCrossroad);
                beforeCrossroadTimer.mark();
                callback.onDriveStraight();
            }
        } else if (delayedEnablingTimer != null && delayedEnablingTimer.timeout()) {
            delayedEnablingTimer = null;
            enabled = true;
        }
    }

    @Override
    public void onLineDetectedEvent(LineSensor source) {
        // Change detectionState depending on which sensor called this method
        if (source == sensorLeft) {
            seesLineStates[0] = true;
        } else if (source == sensorMiddle) {
            seesLineStates[1] = true;
        } else if (source == sensorRight) {
            seesLineStates[2] = true;
        }
    }

    @Override
    public void onNoLineDetectedEvent(LineSensor source) {
        // Change detectionState depending on which sensor called this method
        if (source == sensorLeft) {
            seesLineStates[0] = false;
        } else if (source == sensorMiddle) {
            seesLineStates[1] = false;
        } else if (source == sensorRight) {
            seesLineStates[2] = false;
        }
    }

    public void delayedEnable() {
        if (delayedEnablingTimer == null) {
            delayedEnablingTimer = new Timer(this.waitAfterCrossroad);
            delayedEnablingTimer.mark();
        }
    }

    public void enable() {
        delayedEnablingTimer = null;
        enabled = true;
    }

    public void disable() {
        enabled = false;
    }
}
