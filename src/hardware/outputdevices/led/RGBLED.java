package hardware.outputdevices.led;

import application.RobotMain;
import hardware.PinRegistry;
import link.Updatable;

/**
 * Class for a breadboard RGB LED (multi color, digital signal) on the BoeBot.
 */
public class RGBLED implements LED {
    private final byte pinNumber1;
    private final byte pinNumber2;
    private final RobotMain callback;

    public RGBLED(byte pinNumber1, byte pinNumber2, RobotMain callback) {
        PinRegistry.registerPin(pinNumber1, false);
        PinRegistry.registerPin(pinNumber2, false);
        this.pinNumber1 = pinNumber1;
        this.pinNumber2 = pinNumber2;
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
