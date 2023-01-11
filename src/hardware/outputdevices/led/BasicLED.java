package hardware.outputdevices.led;

import TI.BoeBot;
import TI.PWM;
import TI.Timer;
import hardware.PinRegistry;

/**
 * Class for a basic breadboard LED (single color, digital signal) on the BoeBot.
 * TODO remove this class?
 */
public class BasicLED {
    private final int pinNumber;
    private PWM dimPWM;
    private Timer blinkMethodTimer;
    private Timer blinkTimer;
    private boolean blinkState = false;

    public BasicLED(int pinNumber) {
        PinRegistry.registerPins(new int[]{pinNumber}, new String[]{"output"});
        this.pinNumber = pinNumber;
    }

    /**
     * Turns on the LED.
     *
     * @author Simon
     */
    public void turnOn() {
        BoeBot.digitalWrite(pinNumber, true);
    }

    /**
     * Turns off the LED.
     *
     * @author Simon
     */
    public void turnOff() {
        BoeBot.digitalWrite(pinNumber, false);
    }

    /**
     * Makes the LED blink asynchronously using the Timer class. In other words, this method
     * does not block the run method in RobotMain when called. Rather, this method relies on
     * being continuously called in the run method to check when it should turn on or off.
     * @param milliseconds How long a full cycle (on and off time combined) should last.
     *
     * @author Simon
     */
    public void blink(int milliseconds) {
        if (blinkMethodTimer == null) {
            blinkMethodTimer = new Timer(milliseconds);
            dimPWM = null;
        }

        if (!blinkMethodTimer.timeout()) {
            if (blinkTimer == null) {
                blinkTimer = new Timer(milliseconds / 2);
            }

            if (blinkTimer.timeout()) {
                blinkState = !blinkState;

                if (blinkState) {
                    turnOn();
                } else {
                    turnOff();
                }
            }
        } else {
            blinkMethodTimer = null;
        }
    }

    /**
     * Dims the LED using pulse width modulation (PWM).
     * @param strength How much the LED should be dimmed. Goes from 0 (off) to 255 (fully on).
     *
     * @author Simon
     */
    public void dim(int strength) {
        if (strength < 0 || strength > 255) {
            throw new IllegalArgumentException(
                    "Parameter 'strength' in method dim was not between 0 and 255" +
                            "(inclusive) but was:" + strength
            );
        }

        if (blinkMethodTimer == null) {
            if (dimPWM == null) {
                dimPWM = new PWM(pinNumber, strength);
            } else if (dimPWM.getDutyCycle() != strength) {
                dimPWM.update(strength);
            }
        }
    }
}
