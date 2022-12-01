package hardware.outputdevices.led;

import application.RobotMain;
import hardware.PinRegistry;
import link.Updatable;

/**
 * Class for a neopixel that is integrated in the BoeBot.
 */
public class NeoPixel implements LED {
    private final byte pinNumber;
    private final RobotMain callback;

    public NeoPixel(byte pinNumber, RobotMain callback) {
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
