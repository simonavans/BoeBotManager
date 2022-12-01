package hardware.outputdevices;

import TI.Servo;
import application.RobotMain;
import hardware.PinRegistry;
import link.Updatable;

//TODO make gripper grab objects slower

/**
 * Class for the gripper that is attached to the front of the BoeBot.
 * The BoeBot will be able to grab and release small objects with its gripper.
 */
public class Gripper {
    private final RobotMain callback;
    private Servo gripper;

    public Gripper(byte pinNumber, RobotMain callback) {
        PinRegistry.registerPin(pinNumber, false);
        this.callback = callback;
        gripper = new Servo(pinNumber);
    }

    public void open() {
        gripper.update(1700);
    }

    public void close() {
        gripper.update(1000);
    }
}
