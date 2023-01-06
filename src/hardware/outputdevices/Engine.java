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
    private Timer turnTimer;
    private boolean reverseMode = false;

    public Engine(int leftWheelPin, int rightWheelPin, int leftWheelNeutralOffset, int rightWheelNeutralOffset, int forwardSpeed, int backSteerSpeed, int turnTime, RobotMain callback) {
        PinRegistry.registerPins(new int[]{leftWheelPin, rightWheelPin}, new String[]{"output", "output"});
        wheelLeft = new Servo(leftWheelPin);
        wheelRight = new Servo(rightWheelPin);
        this.leftWheelNeutralOffset = 1500 + leftWheelNeutralOffset;
        this.rightWheelNeutralOffset = 1500 + rightWheelNeutralOffset;
        this.driveSpeed = forwardSpeed;
        this.backSteerSpeed = -backSteerSpeed;
        this.turnTime = turnTime;
        this.callback = callback;

        brake();
    }

    @Override
    public void update() {
        if (turnTimer == null) return;

        if (turnTimer.timeout()) {
            brake();
            turnTimer = null;
            callback.onCompletedTurn();
        }
    }

    public void drive() {
        if (wheelLeft.getPulseWidth() != leftWheelNeutralOffset + driveSpeed) {
            setLeftWheelSpeed(driveSpeed);
        }
        if (wheelRight.getPulseWidth() != rightWheelNeutralOffset - driveSpeed) {
            setRightWheelSpeed(driveSpeed);
        }
    }

    public void changeDirection(boolean toLeft) {
        setLeftWheelSpeed(toLeft ? backSteerSpeed : driveSpeed);
        setRightWheelSpeed(toLeft ? driveSpeed : backSteerSpeed);
    }

    public void turn90(boolean toLeft) {
        if (turnTimer == null) {
            setLeftWheelSpeed(toLeft ? backSteerSpeed : driveSpeed);
            setRightWheelSpeed(toLeft ? driveSpeed : backSteerSpeed);

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

    public boolean isInReverse() { return reverseMode; }

    public void setReverseMode(boolean reverseMode) {
        this.reverseMode = reverseMode;
    }

    private void setLeftWheelSpeed(int leftWheelSpeed) {
        if (reverseMode) {
            wheelLeft.update(leftWheelNeutralOffset - leftWheelSpeed);
        } else {
            wheelLeft.update(leftWheelNeutralOffset + leftWheelSpeed);
        }
    }

    private void setRightWheelSpeed(int rightWheelSpeed) {
        if (reverseMode) {
            wheelRight.update(rightWheelNeutralOffset + rightWheelSpeed);
        } else {
            wheelRight.update(rightWheelNeutralOffset - rightWheelSpeed);
        }
    }
}