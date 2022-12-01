package hardware;

import TI.BoeBot;
import TI.PinMode;

import java.util.ArrayList;

/**
 * Manages all pins in use by the hardware
 */
public class PinRegistry {
    private static ArrayList<Byte> connectedPins = new ArrayList<>();

    /**
     * Registers new pins and makes sure that the same pin is never used twice.
     * If a pin is registered, this class will also handle the pin mode.
     * @param pinNumber which pin number to occupy.
     * @param isInputPin whether the pin should be set to input or output.
     */
    public static void registerPin(byte pinNumber, boolean isInputPin) {
        if (connectedPins.contains(pinNumber)) {
            // TODO? handle exception
            throw new IllegalArgumentException(
                    "Pin number " + pinNumber + " is already occupied"
            );
        }
        connectedPins.add(pinNumber);
        BoeBot.setMode(pinNumber, isInputPin ? PinMode.Input : PinMode.Output);
    }
}
