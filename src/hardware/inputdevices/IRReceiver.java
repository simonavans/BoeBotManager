package hardware.inputdevices;

import TI.BoeBot;
import application.RobotMain;
import hardware.PinRegistry;
import link.Updatable;

/**
 * Class for the infrared sensor/receiver that picks up signals
 * from a Vivanco remote.
 */
public class IRReceiver implements Updatable {
    private final int pinNumber;
    private final RobotMain callback;
    private boolean receivedSignal;

    public IRReceiver(int pinNumber, RobotMain callback) {
        PinRegistry.registerPins(new int[]{pinNumber}, new String[]{"input"});
        this.pinNumber = pinNumber;
        this.callback = callback;
    }

    @Override
    public void update() {
        int pulseLen = BoeBot.pulseIn(pinNumber, false, 6000);
        if (pulseLen > 2000) {
            receivedSignal = true;

            String pulseCode = "";
            for (int i = 0; i < 12; i++) {
                if (BoeBot.pulseIn(pinNumber, false, 20000) < 800) {
                    pulseCode += "0";
                } else {
                    pulseCode += "1";
                }
            }

            int deviceID = Integer.parseInt(new StringBuilder(pulseCode.substring(7)).reverse().toString(), 2);
            int receiverCode = Integer.parseInt(new StringBuilder(pulseCode.substring(0, 7)).reverse().toString(), 2);

            if (deviceID != 1) return;

            callback.onIRReceiverEvent(receiverCode);
        } else if (receivedSignal) {
            callback.onStopReceiving();
            receivedSignal = false;
        }
    }
}
