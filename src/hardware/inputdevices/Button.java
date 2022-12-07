package hardware.inputdevices;

import TI.BoeBot;
import application.RobotMain;
import link.Updatable;

/**
 * Class for a breadboard Button on the BoeBot.
 */
public class Button extends Updatable {
    private final int pinNumber;
    private final RobotMain callback;

    public Button(byte pinNumber, RobotMain callback) {
        super(new int[]{pinNumber}, new String[]{"input"});
        this.pinNumber = pinNumber;
        this.callback = callback;
    }

    @Override
    public void update() {
        if (!BoeBot.digitalRead(pinNumber)) {
            callback.onButtonEvent(this);
        }
    }
}