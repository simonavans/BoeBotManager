package hardware.outputdevices;

import TI.Servo;
import TI.Timer;
import application.RobotMain;
import hardware.PinRegistry;
import link.Updatable;

public class Engine implements Updatable {
    private final RobotMain callback;

    private Servo wheelLeft;
    private Servo wheelRight;
    private final int leftWheelNeutralOffset;
    private final int rightWheelNeutralOffset;
    private final int driveSpeed;
    private final int backSteerSpeed;
    private final int turnTime;
    private final int objectPlacementTime;
    private Timer turnTimer;
    private Timer objectPlacementTimer;
    private boolean reverseMode;

    public Engine(int leftWheelPin, int rightWheelPin, int leftWheelNeutralOffset, int rightWheelNeutralOffset, int forwardSpeed, int backSteerSpeed, int turnTime, int objectPlacementTime, RobotMain callback) {
        PinRegistry.registerPins(new int[]{leftWheelPin, rightWheelPin}, new String[]{"output", "output"});
        this.wheelLeft = new Servo(leftWheelPin);
        this.wheelRight = new Servo(rightWheelPin);
        this.leftWheelNeutralOffset = 1500 + leftWheelNeutralOffset;
        this.rightWheelNeutralOffset = 1500 + rightWheelNeutralOffset;
        this.driveSpeed = forwardSpeed;
        this.backSteerSpeed = -backSteerSpeed;
        this.turnTime = turnTime;
        this.objectPlacementTime = objectPlacementTime;
        this.callback = callback;
        this.reverseMode = false;

        brake();
    }

    @Override
    public void update() {
        if (turnTimer != null && turnTimer.timeout()) {
            brake();
            turnTimer = null;
            callback.onCompletedTurn();
        }

        if (objectPlacementTimer != null && objectPlacementTimer.timeout()) {
            objectPlacementTimer = null;
            callback.onObjectCanBeDropped();
        }
    }

    public void drive(boolean isInReverse) {
        this.reverseMode = isInReverse;
        if (reverseMode) {
            setWheelSpeed(-driveSpeed, driveSpeed);
        } else {
            setWheelSpeed(driveSpeed, -driveSpeed);
        }
    }

    public void changeDirection(boolean toLeft) {
        if (toLeft) {
            setWheelSpeed(backSteerSpeed, driveSpeed);
        } else {
            setWheelSpeed(driveSpeed, backSteerSpeed);
        }
    }

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

    public void brake() {
        wheelLeft.update(leftWheelNeutralOffset);
        wheelRight.update(rightWheelNeutralOffset);
    }

    public void driveBackwardsUntilCrossroad() {
        if (objectPlacementTimer == null) {
            drive(true);
            objectPlacementTimer = new Timer(objectPlacementTime);
            objectPlacementTimer.mark();
        } else {
            System.out.println("Warning: An objectPlacementTimer is still in progress, can not start another one yet! (Engine class)");
        }
    }

    public void resetBackwardsTimer() {
        objectPlacementTimer = null;
    }

    public boolean isInReverse() {
        return reverseMode;
    }

    private void setWheelSpeed(int leftWheelSpeed, int rightWheelSpeed) {
        wheelLeft.update(leftWheelNeutralOffset + leftWheelSpeed);
        wheelRight.update(rightWheelNeutralOffset - rightWheelSpeed);
    }
}