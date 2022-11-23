package hardware.gripper;

import TI.Servo;

public class Gripper {
    private static Servo gripper = new Servo(15);

    public static void open() {
        gripper.update(1700);
    }

    public static void close() {
        gripper.update(1000);
    }
}

