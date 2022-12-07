package hardware.inputdevices;

import TI.BoeBot;
import TI.PinMode;
import application.RobotMain;
import hardware.outputdevices.Engine;
import hardware.outputdevices.Gripper;
import link.Updatable;

import java.util.Arrays;

/**
 * Class for the infrared sensor/receiver that picks up signals
 * from a Vivanco remote.
 */
public class IRReceiver extends Updatable {
    private final byte pinNumber;
    private final RobotMain callback;

    public IRReceiver(byte pinNumber, RobotMain callback) {
        super(new int[]{pinNumber}, new String[]{"input"});
        this.pinNumber = pinNumber;
        this.callback = callback;
    }

    @Override
    public void update() {
        String command;
        int pulseLen = BoeBot.pulseIn(pinNumber, false, 6000);
        if (pulseLen > 2000) {
            int button[] = new int[12];
            for (int i = 0; i < 12; i++) {
                if (BoeBot.pulseIn(pinNumber, false, 20000) < 800) {
                    button[i] = 0;
                } else {
                    button[i] = 1;
                }
            }

            command = Arrays.toString(button).replace(",", "");
            command = command.replace("[", "").replace("]", "");
            command = command.replace(" ", "");

            callback.onIRReceiverEvent(command);
        }
    }
}
