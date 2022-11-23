package hardware.engine;

import TI.BoeBot;
import TI.Servo;

//TODO fix wheel discrepancy
public class DriveManager {
    public static Servo wheelLeft = new Servo(13);
    public static Servo wheelRight = new Servo(12);
    public static final int restFrequencyLeft = 1500;
    public static final int restFrequencyRight = 1504;

    public static void drive(int speed) {
        wheelLeft.update(restFrequencyLeft + speed);
        wheelRight.update(restFrequencyRight - speed);
        //todo je fixt helemaal niks!
    }

    public static void turn(int degree, int speed) {
        int wheelSpeed;
        if (speed < 0) {
            wheelSpeed = -speed;
        } else {
            wheelSpeed = speed;
        }
        int speedRatio = (int) ((wheelSpeed / 5) * 1.9);
        int turn90Degrees = 16000 / speedRatio;
        int askedTurn = (turn90Degrees / 90) * degree;
        wheelLeft.update(restFrequencyLeft - speed);
        wheelRight.update(restFrequencyRight - speed);
        BoeBot.wait(askedTurn);
        brake();
    }

    public static void brake() {
        wheelLeft.update(restFrequencyLeft);
        wheelRight.update(restFrequencyRight);
    }
}
