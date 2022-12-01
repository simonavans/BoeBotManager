package hardware.outputdevices;

import application.RobotMain;
import hardware.PinRegistry;
import link.Updatable;

/**
 * Class for a buzzer on the BoeBot.
 */
public class Buzzer {
    private final byte pinNumber;
    private final RobotMain callback;

    public Buzzer(byte pinNumber, RobotMain callback) {
        PinRegistry.registerPin(pinNumber, false);
        this.pinNumber = pinNumber;
        this.callback = callback;
    }
}
