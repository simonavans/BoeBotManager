package hardware.outputdevices;

import TI.Servo;
import TI.Timer;
import hardware.PinRegistry;
import link.Updatable;

public class Engine implements Updatable {
    private Servo wheelLeft;
    private Servo wheelRight;
    private final int stopPulseLengthLeft = 1500;
    private final int stopPulseLengthRight = 1497;
    private int speedWheelLeft = 1500;
    private int speedWheelRight = 1500;
    private Timer turnTimer;

    public Engine(int leftWheelPin, int rightWheelPin) {
        PinRegistry.registerPins(new int[]{leftWheelPin, rightWheelPin}, new String[]{"output", "output"});
        wheelLeft = new Servo(leftWheelPin);
        wheelRight = new Servo(rightWheelPin);
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
        int frequencyLeft = stopPulseLengthLeft + speed;
        int frequencyRight = stopPulseLengthRight - speed;

        if (wheelLeft.getPulseWidth() != frequencyLeft) {
            wheelLeft.update(frequencyLeft);
        }
        if (wheelRight.getPulseWidth() != frequencyRight) {
            wheelRight.update(frequencyRight);
        }
    }

    public void turnSpeed(int leftWheelSpeed, int rightWheelSpeed) {
        wheelLeft.update(stopPulseLengthLeft + leftWheelSpeed);
        wheelRight.update(stopPulseLengthRight - rightWheelSpeed);
    }

    public void turn90(boolean toLeft) {
        if (turnTimer == null) {
            int leftWheelSpeed = toLeft ? -50 : 25;
            int rightWheelSpeed = toLeft ? 25 : -50;

            wheelLeft.update(stopPulseLengthLeft + leftWheelSpeed);
            wheelRight.update(stopPulseLengthRight - rightWheelSpeed);
            turnTimer = new Timer(1000);
            turnTimer.mark();
        }
    }

    public void brake() {
        wheelLeft.update(stopPulseLengthLeft);
        wheelRight.update(stopPulseLengthRight);
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