package hardware.outputdevices;

import TI.BoeBot;
import TI.Servo;
import application.RobotMain;
import hardware.PinRegistry;

public class Engine {
    private Servo wheelLeft;
    private Servo wheelRight;
    private final int stopPulseLengthLeft = 1500;
    private final int stopPulseLengthRight = 1500;
    private int speedWheelLeft = 1500;
    private int speedWheelRight = 1500;
    private String driveState;

    public Engine(int leftWheelPin, int rightWheelPin) {
        PinRegistry.registerPins(new int[]{leftWheelPin, rightWheelPin}, new boolean[]{false, false});
        wheelLeft = new Servo(leftWheelPin);
        wheelRight = new Servo(rightWheelPin);
    }

    public void drive(int speed) {
        int frequencyLeft = stopPulseLengthLeft + speed;
        int frequencyRight = stopPulseLengthRight - speed;

        if (wheelLeft.getPulseWidth() != frequencyLeft) {
            wheelLeft.update(frequencyLeft);
        }
        if (wheelRight.getPulseWidth() != frequencyRight) {
            wheelRight.update(frequencyRight);
        }

        driveState = "forward";
    }

    public void turnSpeed(Integer leftWheelSpeed, Integer rightWheelSpeed) {
        if (leftWheelSpeed != null) {
            wheelLeft.update(stopPulseLengthLeft + leftWheelSpeed);
        }
        if (rightWheelSpeed != null) {
            wheelRight.update(stopPulseLengthRight - rightWheelSpeed);
        }
    }

    public void turnDegrees(int degree, int speed) {
        int wheelSpeed;
        if (speed < 0) {
            wheelSpeed = -speed;
            driveState = "turn1";
        } else {
            wheelSpeed = speed;
            driveState = "turn2";
        }
        int speedRatio = (int) ((wheelSpeed / 5) * 1.9);
        int turn90Degrees = 16000 / speedRatio;
        int askedTurn = (turn90Degrees / 90) * degree;
        wheelLeft.update(stopPulseLengthLeft - speed);
        wheelRight.update(stopPulseLengthRight - speed);
        BoeBot.wait(askedTurn);
        brake();
    }

    public void brake() {
        wheelLeft.update(stopPulseLengthLeft);
        wheelRight.update(stopPulseLengthRight);
        driveState = "stopped";
    }

    /**
     * @author Kerr
     *
     * Increase the speed (pulse width)  of the left wheel by a given amount.
     * @param speed the speed increase the wheel should receive. Can be negative in order to decrease speed (int).
     *
     */
    public void increaseSpeedWheelLeft(int speed) {
        wheelLeft.update(speedWheelLeft + speed);
        speedWheelLeft += speed;
    }

    /**
     * @author Kerr
     *
     * Increase the speed (pulse width)  of the right wheel by a given amount.
     * @param speed the speed increase the wheel should receive. Can be negative in order to decrease speed (int).
     *
     */
    public void increaseSpeedWheelRight(int speed) {
        wheelRight.update(speedWheelRight + speed);
        speedWheelRight += speed;
    }

    /**
     * @author Kerr
     *
     * Update the speed (pulse width)  of the left wheel.
     * @param speed the speed to which the wheel should be updated (int).
     *
     */
    public void setSpeedWheelLeft(int speed) {
        wheelLeft.update(speed);
        speedWheelLeft = speed;
    }

    /**
     * @author Kerr
     *
     * Update the speed (pulse width)  of the right wheel.
     * @param speed the speed to which the wheel should be updated (int).
     *
     */
    public void setSpeedWheelRight(int speed) {
        wheelRight.update(speed);
        speedWheelRight = speed;
    }

    /**
     * @author Kerr
     *
     * get the current speed (pulse width) of the left wheel.
     *
     * @return current speed of the left wheel (int).
     */
    public int getCurrentSpeedLeft() {
        return speedWheelLeft;
    }

    /**
     * @author Kerr
     *
     * get the current speed (pulse width) of the right wheel.
     *
     * @return current speed of the left wheel (int).
     */
    public int getCurrentSpeedRight() {
        return speedWheelRight;
    }
}