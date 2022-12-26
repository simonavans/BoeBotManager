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
    int openFrequency;
    int closeFrequency;
    private boolean isOpen;

    public Gripper(int pinNumber, int openFrequency, int closeFrequency) {
        PinRegistry.registerPins(new int[]{pinNumber}, new String[]{"output"});
        gripper = new Servo(pinNumber);
        open();
    }

    public void open() {
        gripper.update(openFrequency);
        isOpen = true;
    }

    public void close() {
        gripper.update(closeFrequency);
        isOpen = false;
    }
}