package hardware.inputdevices;

import TI.BoeBot;
import application.RobotMain;
import link.Updatable;

/**
 * Class for a breadboard Button on the BoeBot.
 */
public class Button extends Updatable {
    public Button(byte pinNumber, RobotMain callback) {
        super(pinNumber, true, callback);
    }

    @Override
    public void update() {
        if (!BoeBot.digitalRead(pinNumber)) {
            callback.onButtonEvent(this);
        }
    }
}
