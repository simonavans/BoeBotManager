package link;

import TI.Timer;
import application.RobotMain;
import hardware.inputdevices.sensor.LineSensor;

/**
 * This class sits in between the LineSensor and RobotMain classes. It directly
 * controls the three line sensors on the BoeBot and, using this information,
 * can detect deviations and crossroads.
 */
public class LineSensors implements Updatable {
    // More clarification for some of the variables in this class
    // can be found in the Settings class.

    private final int waitAfterDeviation;
    private final int waitBeforeCrossroad;
    private final int waitAfterDrivingBackwards;
    private RobotMain callback;

    private LineSensor sensorLeft;
    private LineSensor sensorMiddle;
    private LineSensor sensorRight;

    // Whether each line sensor is currently seeing a black line
    // or not. The first element in the array is the leftmost
    // sensor and the last element is the rightmost sensor.
    private boolean[] seesLineStates = {false, false, false};

    private Timer beforeCrossroadTimer;
    private Timer deviateLeftTimer;
    private Timer deviateRightTimer;

    // Whether a crossroad was detected or not. If true, the
    // BoeBot can not run its code for when the bot deviates
    // from the line. This is to prevent accidental detection
    // of a deviation, when in reality it is a crossroad.
    private boolean detectedCrossroad = false;
    private boolean isAlreadyDrivingStraight = false;

    private boolean enabled;
    private Timer delayedEnablingTimer;

    public LineSensors(int[] pinNumbers, int lineSensorThresholdLeft, int lineSensorThresholdMiddle, int lineSensorThresholdRight, int lineSensorCeiling, int waitAfterDeviation, int waitBeforeCrossroad, int waitAfterDrivingBackwards, RobotMain callback) {
        sensorLeft = new LineSensor(pinNumbers[0], lineSensorThresholdLeft, lineSensorCeiling);
        sensorMiddle = new LineSensor(pinNumbers[1], lineSensorThresholdMiddle, lineSensorCeiling);
        sensorRight = new LineSensor(pinNumbers[2], lineSensorThresholdRight, lineSensorCeiling);

        this.waitAfterDeviation = waitAfterDeviation;
        this.waitBeforeCrossroad = waitBeforeCrossroad;
        this.waitAfterDrivingBackwards = waitAfterDrivingBackwards;
        this.callback = callback;

        this.enabled = false;
    }

    /**
     * Method that updates all line sensors and checks if
     * there was a deviation, a crossroad, or just a straight
     * line.
     *
     * @author Simon
     */
    @Override
    public void update() {
        if (enabled) {
            // Give seesLineStates the most up-to-date value from line sensors
            seesLineStates = new boolean[] {
                    sensorLeft.isOnOrOverThreshold(),
                    sensorMiddle.isOnOrOverThreshold(),
                    sensorRight.isOnOrOverThreshold()
            };

            // If there was a before crossroad timer and it timed out, then
            // the BoeBot is exactly on a crossroad. Therefore, call
            // onAlignedOnCrossroad().
            if (beforeCrossroadTimer != null && beforeCrossroadTimer.timeout()) {
                beforeCrossroadTimer = null;
                detectedCrossroad = false;
                enabled = false;
                isAlreadyDrivingStraight = false;
                callback.onAlignedOnCrossroad();
                return;
            }

            // If at this point a crossroad was detected, we don't wanna
            // check for deviations or whether we found a crossroad, because
            // the BoeBot is going to stop soon anyway.
            if (detectedCrossroad) {
                // Set deviation timers to null, because we're not going to
                // check for deviations anymore.
                deviateLeftTimer = null;
                deviateRightTimer = null;
                return;
            }

            // If both left and right see no line, that means that the bot can just
            // drive straight. But only if it isn't already driving straight, to prevent
            // constantly calling back when there is no deviation or crossroad.
            if (!seesLineStates[0] && !seesLineStates[2] && !isAlreadyDrivingStraight) {
                isAlreadyDrivingStraight = true;
                callback.onDriveStraight();
                return;
            }

            // If all of the line sensor see a black line, that means for sure
            // that the BoeBot came across a crossroad.
            if (seesLineStates[0] &&
                    seesLineStates[1] &&
                    seesLineStates[2]) {
                detectedCrossroad = true;

                // Delays the callback to RobotMain to make sure the bot drives
                // just a little further to end up exactly at the crossroad.
                beforeCrossroadTimer = new Timer(waitBeforeCrossroad);
                beforeCrossroadTimer.mark();

                // If the BoeBot was currently turning because it saw a deviation,
                // it now needs to drive straight. Otherwise, the Boebot will
                // turn too far while the before crossroad timer is running.
                // This means that it can end up in the wrong place when it brakes.
                isAlreadyDrivingStraight = true;
                callback.onDriveStraight();
                return;
            }

            // If no timer for deviating left is defined previously
            if (deviateLeftTimer == null) {
                // Check if now the BoeBot does in fact detect
                // a deviation to the left. Sensor left must not
                // see a line, while sensor right must see a line.
                if (!seesLineStates[0] && seesLineStates[2]) {
                    // Start a timer which delays the deviation. This
                    // is to prevent that the bot accidentally sees
                    // a deviation when it is actually a crossroad.
                    deviateLeftTimer = new Timer(waitAfterDeviation);
                    deviateLeftTimer.mark();
                    return;
                }
            }
            // If the timer for deviating left timed out, and while it was
            // running, a crossroad was never detected. This means that
            // the deviation was in fact a deviation and not a crossroad.
            else if (deviateLeftTimer.timeout()) {
                deviateLeftTimer = null;
                isAlreadyDrivingStraight = false;
                callback.onDeviate(false);
                return;
            }

            // If no timer for deviating right is defined previously
            if (deviateRightTimer == null) {
                // Check if now the BoeBot does in fact detect
                // a deviation to the right. Sensor left must see
                // a line, while sensor right must not see a line.
                if (seesLineStates[0] && !seesLineStates[2]) {
                    // Start a timer which delays the deviation. This
                    // is to prevent that the bot accidentally sees
                    // a deviation when it is actually a crossroad.
                    deviateRightTimer = new Timer(waitAfterDeviation);
                    deviateRightTimer.mark();
                }
            }
            // If the timer for deviating right timed out, and while it was
            // running, a crossroad was never detected. This means that
            // the deviation was in fact a deviation and not a crossroad.
            else if (deviateRightTimer.timeout()) {
                deviateRightTimer = null;
                isAlreadyDrivingStraight = false;
                callback.onDeviate(true);
            }
        }
        // If delayEnabledWhenDrivingBackwards() was called, and it timed out
        else if (delayedEnablingTimer != null && delayedEnablingTimer.timeout()) {
            delayedEnablingTimer = null;
            enabled = true;
        }
    }

    /**
     * Delays the enabling of the line sensors when the BoeBot drives
     * backwards. This is to account for the fact that otherwise, the
     * line sensors would detect the crossroad it is already on as the
     * next crossroad.
     *
     * @author Simon
     */
    public void delayEnabledWhenDrivingBackwards() {
        if (delayedEnablingTimer == null) {
            delayedEnablingTimer = new Timer(this.waitAfterDrivingBackwards);
            delayedEnablingTimer.mark();
        }
    }

    /**
     * @param isEnabled whether the line sensors should be enabled or not
     * @author Simon
     */
    public void setEnabled(boolean isEnabled) {
        delayedEnablingTimer = null;
        detectedCrossroad = false;
        enabled = isEnabled;
    }
}
