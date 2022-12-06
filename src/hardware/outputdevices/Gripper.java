package hardware.outputdevices;

import TI.Servo;
import hardware.PinRegistry;

//TODO make gripper grab objects slower

/**
 * Class for the gripper that is attached to the front of the BoeBot.
 * The BoeBot will be able to grab and release small objects with its gripper.
 */
public class Gripper {
    private Servo gripper;
    private boolean isOpen;

    public Gripper(int pinNumber) {
        PinRegistry.registerPins(new int[]{pinNumber}, new boolean[]{false});
        gripper = new Servo(pinNumber);
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void open() {
        gripper.update(1700);
        isOpen = true;
    }

    public void close() {
        gripper.update(1000);
        isOpen = false;
    }
}