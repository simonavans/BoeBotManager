package hardware.outputdevices;

import TI.Servo;
import TI.Timer;
import hardware.PinRegistry;
import link.Updatable;

public class Engine implements Updatable {
    private Servo wheelLeft;
    private Servo wheelRight;
    private final int stopSpeedLeft;
    private final int stopSpeedRight;
    private final int forwardSpeed;
    private Timer turnTimer;

    //TODO delete if Kerr is fine with it
    private int speedWheelLeft = 1500;
    private int speedWheelRight = 1500;

    public Engine(int leftWheelPin, int rightWheelPin, int stopSpeedLeft, int stopSpeedRight, int forwardSpeed) {
        PinRegistry.registerPins(new int[]{leftWheelPin, rightWheelPin}, new String[]{"output", "output"});
        wheelLeft = new Servo(leftWheelPin);
        wheelRight = new Servo(rightWheelPin);
        this.stopSpeedLeft = stopSpeedLeft;
        this.stopSpeedRight = stopSpeedRight;
        this.forwardSpeed = forwardSpeed;

        brake();
    }

    @Override
    public void update() {
        if (turnTimer == null) return;

        if (turnTimer.timeout()) {
            drive(25);
            turnTimer = null;
        }
    }

    public void drive(int speed) {
        int frequencyLeft = stopSpeedLeft + speed;
        int frequencyRight = stopSpeedRight - speed;

        if (wheelLeft.getPulseWidth() != frequencyLeft) {
            wheelLeft.update(frequencyLeft);
        }
        if (wheelRight.getPulseWidth() != frequencyRight) {
            wheelRight.update(frequencyRight);
        }
    }

    public void turnSpeed(int leftWheelSpeed, int rightWheelSpeed) {
        wheelLeft.update(stopSpeedLeft + leftWheelSpeed);
        wheelRight.update(stopSpeedRight - rightWheelSpeed);
    }

    public void turn90(boolean toLeft) {
        if (turnTimer == null) {
            int leftWheelSpeed = toLeft ? -50 : 25;
            int rightWheelSpeed = toLeft ? 25 : -50;

            wheelLeft.update(stopSpeedLeft + leftWheelSpeed);
            wheelRight.update(stopSpeedRight - rightWheelSpeed);
            turnTimer = new Timer(1000);
            turnTimer.mark();
        }
    }

    public void brake() {
        wheelLeft.update(stopSpeedLeft);
        wheelRight.update(stopSpeedRight);
    }

    /**
     * @author Kerr
     *
     * Increase the speed (pulse width)  of the left wheel by a given amount.
     * @param speed the speed increase the wheel should receive. Can be negative in order to decrease speed (int).
     *
     */
    //TODO delete if Kerr is fine with it
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
    //TODO delete if Kerr is fine with it
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
    //TODO delete if Kerr is fine with it
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
    //TODO delete if Kerr is fine with it
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
    //TODO delete if Kerr is fine with it
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
    //TODO delete if Kerr is fine with it
    public int getCurrentSpeedRight() {
        return speedWheelRight;
    }
}