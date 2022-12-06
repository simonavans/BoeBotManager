package hardware.inputdevices.sensor;

import TI.Timer;
import application.RobotMain;
import link.Updatable;

import java.util.Arrays;

public class LineSensors extends Updatable {
    private RobotMain callback;
    private LineSensor sensorLeft;
    private LineSensor sensorMiddle;
    private LineSensor sensorRight;
    private boolean[] detectionStates = {false, false, false};
    private Timer crossroadTimer;

    public LineSensors(int[] pinNumbers, RobotMain callback) {
        super(pinNumbers, new boolean[]{});
        sensorLeft = new LineSensor(pinNumbers[0], 1000, 1300, this);
        sensorMiddle = new LineSensor(pinNumbers[1],1100, 1500, this);
        sensorRight = new LineSensor(pinNumbers[2], 700, 1300, this);
        this.callback = callback;
    }

    @Override
    public void update() {
        if (crossroadTimer == null) {
            sensorLeft.update();
            sensorMiddle.update();
            sensorRight.update();

            if (Arrays.equals(detectionStates, new boolean[]{true, true, true})) {
                callback.onDetectCrossroad();
                crossroadTimer = new Timer(2000);
                crossroadTimer.mark();
            } else if (detectionStates[0] && !detectionStates[2]) {
                callback.onDeviate(false);
            } else if (!detectionStates[0] && detectionStates[2]) {
                callback.onDeviate(true);
            } else if (!detectionStates[0] && detectionStates[1]) {
                callback.onDriveStraight();
            }
        } else if(crossroadTimer.timeout()) {
            crossroadTimer = null;
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
