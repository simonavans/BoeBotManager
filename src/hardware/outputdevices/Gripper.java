package hardware.outputdevices;

import TI.BoeBot;
import TI.Servo;
import hardware.PinRegistry;
import link.Hardware;

//TODO make gripper grab objects slower

/**
 * Class for the gripper that is attached to the front of the BoeBot.
 * The BoeBot will be able to grab and release small objects with its gripper.
 */
public class Gripper extends Hardware {
    private Servo gripper;
    private boolean isOpen;

    public Gripper(int pinNumber) {
        super(new int[]{pinNumber}, new String[]{"output"});
        gripper = new Servo(pinNumber);
        open();
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void open() {
        gripper.update(2200);
        isOpen = true;
    }

    public void close() {
        gripper.update(1250);
        isOpen = false;
    }
}