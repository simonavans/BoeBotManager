package hardware.outputdevices;

import TI.Servo;
import TI.Timer;
import application.RobotMain;
import hardware.PinRegistry;
import link.Updatable;

public class Engine implements Updatable {
    // More clarification for some of the variables in this class
    // can be found in the Settings class.

    private final int leftWheelNeutralOffset;
    private final int rightWheelNeutralOffset;
    private final int driveSpeed;
    private final int backSteerSpeed;
    private final int turnTime;
    private final int objectPlacementTime;
    private Timer turnTimer;
    private Timer objectPlacementTimer;
    private final RobotMain callback;

    private Servo wheelLeft;
    private Servo wheelRight;

    // Whether the BoeBot is in reverse, meaning drive() will make the bot drive
    // backwards.
    private boolean isInReverse;

    public Engine(int leftWheelPin, int rightWheelPin, int leftWheelNeutralOffset, int rightWheelNeutralOffset, int driveSpeed, int backSteerSpeed, int turnTime, int objectPlacementTime, RobotMain callback) {
        PinRegistry.registerPins(new int[]{leftWheelPin, rightWheelPin}, new String[]{"output", "output"});
        this.wheelLeft = new Servo(leftWheelPin);
        this.wheelRight = new Servo(rightWheelPin);
        this.leftWheelNeutralOffset = 1500 + leftWheelNeutralOffset;
        this.rightWheelNeutralOffset = 1500 + rightWheelNeutralOffset;
        this.driveSpeed = driveSpeed;
        this.backSteerSpeed = -backSteerSpeed;
        this.turnTime = turnTime;
        this.objectPlacementTime = objectPlacementTime;
        this.callback = callback;
        this.isInReverse = false;

        brake();
    }

    /**
     * This method checks if any Timer variables have timed out in order to
     * take action.
     *
     * @author Simon
     */
    @Override
    public void update() {
        // Checks if the turn timer has timed out, if so, it calls
        // onCompletedTurn in RobotMain
        if (turnTimer != null && turnTimer.timeout()) {
            brake();
            turnTimer = null;
            callback.onCompletedTurn();
        }

        // Checks if the object placement timer has timed out, if so,
        // it calls onObjectCanBeDropped in RobotMain
        if (objectPlacementTimer != null && objectPlacementTimer.timeout()) {
            objectPlacementTimer = null;
            callback.onObjectCanBeDropped();
        }
    }

    /**
     * Makes the BoeBot drive forward or backward, depending on whether
     * it is in reverse or not.
     * @param isInReverse whether to reverse the BoeBot.
     *
     * @author Simon
     */
    public void drive(boolean isInReverse) {
        this.isInReverse = isInReverse;
        if (this.isInReverse) {
            setWheelSpeed(driveSpeed, driveSpeed);
        } else {
            setWheelSpeed(driveSpeed, driveSpeed);
        }
    }

    /**
     * Method used for steering to the right for the left. Used for
     * correcting deviations from the line that needs to be followed.
     * @param toLeft whether to steer to the left or to the right.
     *
     * @author Simon
     */
    public void changeDirection(boolean toLeft) {
        if (toLeft) {
            setWheelSpeed(backSteerSpeed, driveSpeed);
        } else {
            setWheelSpeed(driveSpeed, backSteerSpeed);
        }
    }

    /**
     * Makes the BoeBot turn 90 degrees using a Timer. When the Timer
     * runs out, it will call back to RobotMain (onCompletedTurn).
     * @param toLeft whether to turn to the left or to the right.
     *
     * @author Simon
     */
    public void turn90(boolean toLeft) {
        if (turnTimer == null) {
            if (toLeft) {
                setWheelSpeed(backSteerSpeed, driveSpeed);
            } else {
                setWheelSpeed(driveSpeed, backSteerSpeed);
            }

            turnTimer = new Timer(turnTime);
            turnTimer.mark();
        } else {
            System.out.println("Warning: A turnTimer is still in progress, can not start another one yet! (Engine class)");
        }
    }

    /**
     * Stops the BoeBot.
     *
     * @author Simon
     */
    public void brake() {
        // Resets the object placement timer for when the BoeBot
        // was driving backwards. Since it has stopped driving,
        // the timer can be reset.
        objectPlacementTimer = null;
        wheelLeft.update(leftWheelNeutralOffset);
        wheelRight.update(rightWheelNeutralOffset);
    }

    /**
     * Drives backwards until the line sensors see a crossroad. The
     * line sensors part is handled by RobotMain. It starts a Timer
     * which times out when the object that is held can be dropped.
     * In that case, it will call back to RobotMain (onObjectCanBeDropped).
     *
     * @author Simon
     */
    public void driveBackwardsUntilCrossroad() {
        if (objectPlacementTimer == null) {
            drive(true);
            objectPlacementTimer = new Timer(objectPlacementTime);
            objectPlacementTimer.mark();
        } else {
            System.out.println("Warning: An objectPlacementTimer is still in progress, can not start another one yet! (Engine class)");
        }
    }

    /**
     * @author Simon
     */
    public boolean isInReverse() {
        return isInReverse;
    }

    /**
     * Sets wheel speeds of both wheels.
     *
     * @author Simon
     */
    private void setWheelSpeed(int leftWheelSpeed, int rightWheelSpeed) {
        wheelLeft.update(leftWheelNeutralOffset + leftWheelSpeed);
        wheelRight.update(rightWheelNeutralOffset - rightWheelSpeed);
    }
}