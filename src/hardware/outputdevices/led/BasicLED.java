package hardware.outputdevices.led;

import application.RobotMain;
import hardware.PinRegistry;
import link.Updatable;

/**
 * Class for a basic breadboard LED (single color, digital signal) on the BoeBot.
 */
public class BasicLED implements LED {
    private final byte pinNumber;
    private final RobotMain callback;

    public BasicLED(byte pinNumber, RobotMain callback) {
        PinRegistry.registerPin(pinNumber, false);
        this.pinNumber = pinNumber;
        this.callback = callback;
    }

    @Override
    public void turnOn() {

    }

    @Override
    public void turnOff() {

    }

    @Override
    public void dim(short strength) {

    }

    @Override
    public void blink(int milliSeconds) {

    }

    @Override
    public void fade(int milliSeconds) {

    }
}
