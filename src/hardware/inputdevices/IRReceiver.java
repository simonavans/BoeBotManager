package hardware.inputdevices;

import TI.BoeBot;
import application.RobotMain;
import hardware.PinRegistry;
import hardware.outputdevices.led.NeoPixel;
import link.Updatable;

import java.awt.*;

//todo check if timo is author of this class

/**
 * Class for the infrared sensor/receiver that picks up signals from a Vivanco remote.
 * The remote signals will only be picked up when the remote's device id is set to 1.
 */
public class IRReceiver implements Updatable {
    // More clarification for some of the variables in this class
    // can be found in the Settings class.

    private final int pinNumber;

    // The Neopixel which the IR receiver will use to indicate that a remote signal has
    // been received (the neopixel will flash white).
    private final NeoPixel irSignalNeoPixel;
    private final int bitThreshold;
    private final RobotMain callback;
    private boolean hasReceivedSignal;

    public IRReceiver(int pinNumber, NeoPixel irSignalNeoPixel, int bitThreshold, RobotMain callback) {
        PinRegistry.registerPins(new int[]{pinNumber}, new String[]{"input"});
        this.pinNumber = pinNumber;
        this.irSignalNeoPixel = irSignalNeoPixel;
        this.bitThreshold = bitThreshold;
        this.callback = callback;
    }

    /**
     * Method which returns code transmitted by the IR remote. The code is an integer.
     *
     * @author
     */
    @Override
    public void update() {
        // Start finding a pulse, specifically the length of the low signal of the pulse
        int pulseLen = BoeBot.pulseIn(pinNumber, false, 6000);

        if (pulseLen > 2000) {
            hasReceivedSignal = true;

            String pulseCode = "";

            // Loop through all bits in the code
            for (int i = 0; i < 12; i++) {
                if (BoeBot.pulseIn(pinNumber, false, 20000) < bitThreshold) {
                    pulseCode += "0";
                } else {
                    pulseCode += "1";
                }
            }

            // Split the code into a deviceID and a receiverCode and convert them to an integer
            int deviceID = Integer.parseInt(new StringBuilder(pulseCode.substring(7)).reverse().toString(), 2);
            int receiverCode = Integer.parseInt(new StringBuilder(pulseCode.substring(0, 7)).reverse().toString(), 2);

            if (deviceID != 1) return;

            irSignalNeoPixel.turnOn(new Color(100, 100, 100));
            callback.onIRReceiverEvent(receiverCode);
        }
        // If there is no signal, but the sensor has received something the previous time
        // that this method was called.
        else if (hasReceivedSignal) {
            irSignalNeoPixel.turnOff();
            hasReceivedSignal = false;
        }
    }
}
