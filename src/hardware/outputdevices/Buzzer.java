package hardware.outputdevices;

import TI.PWM;
import TI.Timer;
import hardware.PinRegistry;
import link.Updatable;

/**
 * Class for a breadboard buzzer on the BoeBot. The buzzer can beep repeatingly.
 */
public class Buzzer implements Updatable {
    // The Pulse Width Modulation that is sent to the buzzer. This will control
    // the volume of the beep.
    private PWM pwm;

    // A Timer for how long the repeating beeps should last in total
    private Timer repeatingBeepMethodTimer;

    // A Timer for how long a single beep should last
    private Timer repeatingBeepTimer;

    // Whether the buzzer should currently beep or not
    private boolean beepState;

    // How long a beep should last
    private int beepDurationMilliseconds = 0;

    public Buzzer(int pinNumber) {
        PinRegistry.registerPins(new int[]{pinNumber}, new String[]{"output"});

        // Create a new PWM and set the value to 0, meaning the buzzer is not
        // making any sound.
        pwm = new PWM(pinNumber, 0);
    }

    /**
     * This method makes sure that the buzzer beeps at the correct time.
     *
     * @author Simon
     */
    @Override
    public void update() {
        // If nothing called the repeatingBeep method, do nothing
        if (repeatingBeepMethodTimer == null) return;

        // If the beeping should stop
        if (repeatingBeepMethodTimer.timeout()) {
            repeatingBeepMethodTimer = null;
            repeatingBeepTimer = null;
            beepDurationMilliseconds = 0;
            return;
        }

        // Create a new repeatingBeepTimer. Every time this timer times
        // out, the beepState will invert its value, and depending on this
        // new value, the buzzer will be turned on or off.
        if (repeatingBeepTimer == null) {
            repeatingBeepTimer = new Timer(beepDurationMilliseconds);
            repeatingBeepTimer.mark();
        }

        if (repeatingBeepTimer.timeout()) {
            beepState = !beepState;

            // beepState true, then 128, else 0
            pwm.update(beepState ? 128 : 0);
        }
    }

    /**
     * Makes the buzzer beep repeatingly and asynchronously using the Timer class. In other
     * words, this method does not block the run method in RobotMain when called. Since Buzzer
     * is Updatable, which means that calling this method once will run repeatingBeep until
     * sufficient time has passed. Calling this method while the buzzer is already beeping on
     * repeat will override the previous repeating beep.
     *
     * @param milliseconds How long a full cycle (on and off time combined) should last.
     * @param times How many times the buzzer should beep in the given time frame.
     * @param highPriority if this buzzer is already beeping, true will always override
     *                     the current repeating beep. Used for the emergency sound.
     *
     * @author Simon
     */
    public void repeatingBeep(int milliseconds, int times, boolean highPriority) {
        if (milliseconds <= 1) {
            throw new IllegalArgumentException("Parameter milliseconds for repeatingBeep method must be greater than 1");
        }

        if (repeatingBeepMethodTimer == null || highPriority) {
            beepDurationMilliseconds = milliseconds / (times * 2);
            repeatingBeepMethodTimer = new Timer(milliseconds);
            repeatingBeepMethodTimer.mark();
        }
    }

    /**
     * Turns off beeping for this buzzer.
     *
     * @author Simon
     */
    public void resetRepeatingBeep() {
        repeatingBeepMethodTimer = null;
        pwm.update(0);
    }
}