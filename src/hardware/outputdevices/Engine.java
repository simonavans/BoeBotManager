package hardware.outputdevices;

import TI.BoeBot;
import TI.Servo;
import application.RobotMain;
import hardware.PinRegistry;
import link.Updatable;

public class Engine {
//    private final RobotMain callback;
    private Servo wheelLeft;
    private Servo wheelRight;
    private final int stopPulseLengthLeft = 1500;
    private final int stopPulseLengthRight = 1504;

    public Engine(byte leftWheelPin, byte rightWheelPin) {
        PinRegistry.registerPin(leftWheelPin, false);
        PinRegistry.registerPin(rightWheelPin, false);
//        this.callback = callback;

        wheelLeft = new Servo((int) leftWheelPin);
        wheelRight = new Servo((int) rightWheelPin);
    }

    public void drive(int speed) {
        int frequencyLeft = stopPulseLengthLeft - speed;
        int frequencyRight = stopPulseLengthRight + speed;

        wheelLeft.update(frequencyLeft);
        wheelRight.update(frequencyRight);
        BoeBot.wait(1000);
        brake();
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
        } else {
            wheelSpeed = speed;
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
    }
}
