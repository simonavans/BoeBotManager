package link;

import hardware.PinRegistry;

public class Hardware {
    public Hardware(int[] pinNumbers, String[] pinModes) {
        PinRegistry.registerPins(pinNumbers, pinModes);
    }
}
