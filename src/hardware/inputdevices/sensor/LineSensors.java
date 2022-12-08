package hardware.inputdevices.sensor;

import TI.Timer;
import application.NavigationManager;
import application.RobotMain;
import link.Updatable;
import link.callbacks.LineSensorCallback;

import java.util.Arrays;

public class LineSensors implements Updatable, LineSensorCallback {
    private RobotMain callback;
    private LineSensor sensorLeft;
    private LineSensor sensorMiddle;
    private LineSensor sensorRight;
    private boolean[] detectionStates = {false, false, false};
    private Timer afterCrossroadTimer;
    private Timer beforeCrossroadTimer;
    private Timer deviateLeftTimer;
    private Timer deviateRightTimer;
    private boolean detectedCrossroad;
    private boolean enabled = false;

    public LineSensors(int[] pinNumbers, RobotMain callback) {
        sensorLeft = new LineSensor(pinNumbers[0], 1400, 1800, this);
        sensorMiddle = new LineSensor(pinNumbers[1], 1500, 1800, this);
        sensorRight = new LineSensor(pinNumbers[2], 1400, 1800, this);
        this.callback = callback;
    }

    @Override
    public void update() {
        if (afterCrossroadTimer == null && beforeCrossroadTimer == null && enabled) {
            sensorLeft.update();
            sensorMiddle.update();
            sensorRight.update();

            if (deviateLeftTimer == null) {
                if (!detectionStates[0] && detectionStates[2]) {
                    deviateLeftTimer = new Timer(400);
                    deviateLeftTimer.mark();
                }
            } else if (deviateLeftTimer.timeout() && !detectedCrossroad) {
                callback.onDeviate(true);
                deviateLeftTimer = null;
            }

            if (deviateRightTimer == null) {
                if (detectionStates[0] && !detectionStates[2]) {
                    deviateRightTimer = new Timer(400);
                    deviateRightTimer.mark();
                }
            } else if (deviateRightTimer.timeout() && !detectedCrossroad) {
                callback.onDeviate(false);
                deviateRightTimer = null;
            }

            if (Arrays.equals(detectionStates, new boolean[]{true, true, true})) {
                detectedCrossroad = true;
                beforeCrossroadTimer = new Timer(700);
                beforeCrossroadTimer.mark();
                callback.onDriveStraight();
            } else if (!detectionStates[0] && !detectionStates[2]) {
                detectedCrossroad = false;
                callback.onDriveStraight();
            }
        } else if (afterCrossroadTimer != null && afterCrossroadTimer.timeout()) {
            afterCrossroadTimer = null;
            detectedCrossroad = false;
        } else if (beforeCrossroadTimer != null && beforeCrossroadTimer.timeout()) {
            beforeCrossroadTimer = null;
            if (NavigationManager.isTurnNext()) {
                afterCrossroadTimer = new Timer(1500);
            } else {
                afterCrossroadTimer = new Timer(200);
            }
            afterCrossroadTimer.mark();
            callback.onDetectCrossroad();
        }
    }

    @Override
    public void onLineDetectedEvent(LineSensor source) {
        if (source == sensorLeft) {
            detectionStates[0] = true;
        } else if (source == sensorMiddle) {
            detectionStates[1] = true;
        } else if (source == sensorRight) {
            detectionStates[2] = true;
        }
    }

    @Override
    public void onLineUndetectedEvent(LineSensor source) {
        if (source == sensorLeft) {
            detectionStates[0] = false;
        } else if (source == sensorMiddle) {
            detectionStates[1] = false;
        } else if (source == sensorRight) {
            detectionStates[2] = false;
        }
    }

    public void enable() {
        enabled = true;
    }

    public void disable() {
        enabled = false;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
