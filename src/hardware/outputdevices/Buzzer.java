package hardware.outputdevices;

import TI.BoeBot;
import TI.PWM;
import TI.Timer;
import application.RobotMain;
import hardware.PinRegistry;
import link.Hardware;
import link.Updatable;

/**
 * Class for a buzzer on the BoeBot.
 * @author Simon de Cock
 */
public class Buzzer extends Hardware {
    private PWM pwm;
    private Timer repeatingBeepMethodTimer;
    private Timer repeatingBeepTimer;
    private boolean beepState;

    public Buzzer(byte pinNumber) {
        super(new int[]{pinNumber}, new String[]{"output"});

        // Constructor of PWM sets pinNumber's PinMode to PWM instead of Output
        pwm = new PWM(pinNumber, 0);
    }

    /**
     * Makes the buzzer beep repeatingly and asynchronously using the Timer class. In other
     * words, this method does not block the run method in RobotMain when called. Rather,
     * this method relies on being continuously called in the run method to check when it
     * should beep or not.
     * @param milliseconds How long a full cycle (on and off time combined) should last.
     * @author Simon de Cock
     */
    public void repeatingBeep(int milliseconds) {
        if (repeatingBeepMethodTimer == null) {
            repeatingBeepMethodTimer = new Timer(milliseconds);
        }

        if (!repeatingBeepMethodTimer.timeout()) {
            if (repeatingBeepTimer == null) {
                repeatingBeepTimer = new Timer(milliseconds / 2);
            }

            if (repeatingBeepTimer.timeout()) {
                beepState = !beepState;

                if (beepState) {
                    pwm.update(128);
                } else {
                    pwm.update(0);
                }
            }
        } else {
            repeatingBeepMethodTimer = null;
        }
    }
}
