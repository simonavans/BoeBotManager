package hardware.outputdevices;

import TI.PWM;
import TI.Timer;
import hardware.PinRegistry;
import link.Updatable;

/**
 * Class for a buzzer on the BoeBot.
 *
 * @author Simon de Cock
 */
public class Buzzer implements Updatable {
    private PWM pwm;
    private Timer repeatingBeepMethodTimer;
    private Timer repeatingBeepTimer;
    private boolean beepState;
    private int beepDuration = 0;

    public Buzzer(byte pinNumber) {
        PinRegistry.registerPins(new int[]{pinNumber}, new String[]{"output"});

        // Constructor of PWM sets pinNumber's PinMode to PWM instead of Output
        pwm = new PWM(pinNumber, 0);
    }

    @Override
    public void update() {
        if (repeatingBeepMethodTimer == null) return;

        if (repeatingBeepMethodTimer.timeout()) {
            repeatingBeepMethodTimer = null;
            repeatingBeepTimer = null;
            beepDuration = 0;
            return;
        }

        if (repeatingBeepTimer == null) {
            repeatingBeepTimer = new Timer(beepDuration);
            repeatingBeepTimer.mark();
        }

        if (repeatingBeepTimer.timeout()) {
            beepState = !beepState;
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
     * @author Simon de Cock
     */
    public void repeatingBeep(int milliseconds, int times) {
        if (milliseconds > 1) {
            beepDuration = milliseconds / (times * 2);
            repeatingBeepMethodTimer = new Timer(milliseconds);
            repeatingBeepMethodTimer.mark();
        } else {
            System.out.println("Warning: parameter milliseconds for repeatingBeep method must be greater than 1");
        }
    }
}
