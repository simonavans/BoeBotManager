package link;

import application.RobotMain;
import hardware.PinRegistry;

public abstract class Updatable extends Hardware {

    public Updatable(int[] pinNumbers, String[] pinModes) {
        super(pinNumbers, pinModes);
    }

    /**
     * The method used for letting the hardware execute its code once.
     */
    public abstract void update();
}
