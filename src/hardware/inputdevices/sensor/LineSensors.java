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
        // Any Timer variables that have to do with a crossroad must be null.
        // Otherwise, the line sensors would cause interference while passing the crossroad.
        if (afterCrossroadTimer == null && beforeCrossroadTimer == null && enabled) {
            sensorLeft.update();
            sensorMiddle.update();
            sensorRight.update();

            // If there is not yet a Timer for deviating left while the line sensors do detect
            // a deviation to the left, start a Timer for it.
            if (deviateLeftTimer == null) {
                if (!detectionStates[0] && detectionStates[2]) {
                    deviateLeftTimer = new Timer(400);
                    deviateLeftTimer.mark();
                }
            }
            // If the Timer for deviating left has finished and still no crossroad is detected,
            // it is safe to realign with the grid.
            else if (deviateLeftTimer.timeout() && !detectedCrossroad) {
                callback.onDeviate(true);
                // Reset the Timer
                deviateLeftTimer = null;
            }

            // If there is not yet a Timer for deviating right while the line sensors do detect
            // a deviation to the right, start a Timer for it.
            if (deviateRightTimer == null) {
                if (detectionStates[0] && !detectionStates[2]) {
                    deviateRightTimer = new Timer(400);
                    deviateRightTimer.mark();
                }
            }
            // If the Timer for deviating right has finished and still no crossroad is detected,
            // it is safe to realign with the grid.
            else if (deviateRightTimer.timeout() && !detectedCrossroad) {
                callback.onDeviate(false);
                // Reset the Timer
                deviateRightTimer = null;
            }

            // If a crossroad is detected, the bot cannot yet turn since the wheels don't align
            // with the crossroad yet. Therefore, start a Timer.
            if (Arrays.equals(detectionStates, new boolean[]{true, true, true})) {
                detectedCrossroad = true;
                beforeCrossroadTimer = new Timer(700);
                beforeCrossroadTimer.mark();
                // During this process, the bot must be driving straight to prevent turning too much.
                callback.onDriveStraight();
            }
            // If the bot is driving straight, set detectedCrossroad to false (for deviate left and right
            // Timers) and call back.
            else if (!detectionStates[0] && !detectionStates[2]) {
                detectedCrossroad = false;
                callback.onDriveStraight();
            }
        }
        // If the Timer for before the crossroad is finished
        else if (beforeCrossroadTimer != null && beforeCrossroadTimer.timeout()) {
            // Reset the Timer
            beforeCrossroadTimer = null;
            // Check with NavigationManager whether the bot has to turn on the next crossroad.
            // If so, the wait time needs to be longer, since turning takes more time to get off the
            // crossroad than driving straight over it.
            if (NavigationManager.isTurnNext()) {
                afterCrossroadTimer = new Timer(1500);
            } else {
                afterCrossroadTimer = new Timer(200);
            }
            afterCrossroadTimer.mark();
            callback.onDetectCrossroad();
        }
        // If the Timer for after the crossroad is finished
        else if (afterCrossroadTimer != null && afterCrossroadTimer.timeout()) {
            // Reset the Timer
            afterCrossroadTimer = null;
            // Set detectedCrossroad to false, for deviate left and right Timers.
            detectedCrossroad = false;
        }
    }

    @Override
    public void onLineDetectedEvent(LineSensor source) {
        // Change detectionState depending on which sensor called this method
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
        // Change detectionState depending on which sensor called this method
        if (source == sensorLeft) {
            detectionStates[0] = false;
        } else if (source == sensorMiddle) {
            detectionStates[1] = false;
        } else if (source == sensorRight) {
            detectionStates[2] = false;
        }
    }

    public boolean lineSensorCrossroadDetector(){
        // Any Timer variables that have to do with a crossroad must be null.
        // Otherwise, the line sensors would cause interference while passing the crossroad.
        if (afterCrossroadTimer == null && beforeCrossroadTimer == null && enabled) {
            sensorLeft.update();
            sensorMiddle.update();
            sensorRight.update();

            // If a crossroad is detected, the bot cannot yet turn since the wheels don't align
            // with the crossroad yet. Therefore, start a Timer.
            if (Arrays.equals(detectionStates, new boolean[]{true, true, true})) {
                detectedCrossroad = true;
                beforeCrossroadTimer = new Timer(700);
                beforeCrossroadTimer.mark();
                // During this process, the bot must be driving straight to prevent turning too much.
                callback.onDriveStraight();
                return true;
            }
        }

        return false;
    }

    public void enable() {
        enabled = true;
    }

    public void disable() {
        enabled = false;
    }
}
