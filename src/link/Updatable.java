package link;

import application.RobotMain;
import hardware.PinRegistry;

public abstract class Updatable {
    protected final boolean[] pinModes;

    public Updatable(int[] pinNumbers, boolean[] pinModes) {
        PinRegistry.registerPins(pinNumbers, pinModes);
        this.pinModes = pinModes;
    }

    /**
     * The method used for letting the hardware execute its code once.
     */
    public abstract void update();
}
