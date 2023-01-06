package hardware.outputdevices;

import TI.Servo;
import hardware.PinRegistry;

/**
 * Class for the gripper that is attached to the front of the BoeBot.
 * The BoeBot will be able to grab and release small objects with its gripper.
 */
public class Gripper {
    private Servo gripper;
    private final int openFrequency;
    private final int closeFrequency;

    public Gripper(int pinNumber, int openFrequency, int closeFrequency) {
        PinRegistry.registerPins(new int[]{pinNumber}, new String[]{"output"});
        gripper = new Servo(pinNumber);
        this.openFrequency = openFrequency;
        this.closeFrequency = closeFrequency;

        open();
    }

    public void open() {
        gripper.update(openFrequency);
    }

    public void close() {
        gripper.update(closeFrequency);
    }
}