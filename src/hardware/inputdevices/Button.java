package hardware.inputdevices;

import TI.BoeBot;
import application.RobotMain;
import hardware.PinRegistry;
import link.Updatable;

/**
 * Class for a breadboard button on the BoeBot.
 */
public class Button implements Updatable {
    private final int pinNumber;
    private final RobotMain callback;

    private boolean isPressed = false;

    public Button(int pinNumber, RobotMain callback) {
        PinRegistry.registerPins(new int[]{pinNumber}, new String[]{"input"});
        this.pinNumber = pinNumber;
        this.callback = callback;
    }

    /**
     * Only when the button is toggled on or of, this method will send a callback
     * to RobotMain.
     *
     * @author Simon
     */
    @Override
    public void update() {
        if (!BoeBot.digitalRead(pinNumber)) {
            if (!isPressed) {
                isPressed = true;
                callback.onButtonEvent();
            }
        } else if (isPressed) {
            isPressed = false;
        }
    }
}