package hardware.inputdevices;

import application.RobotMain;
import link.Updatable;

/**
 * Class for the infrared sensor/receiver that picks up signals
 * from a Vivanco remote.
 */
public class IRReceiver extends Updatable {
    private final byte pinNumber;

    public IRReceiver(byte pinNumber, RobotMain callback) {
        super(pinNumber, true, callback);
        this.pinNumber = pinNumber;
    }

    @Override
    public void update() {
    }
}
