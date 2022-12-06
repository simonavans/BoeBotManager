package hardware;

import TI.BoeBot;
import TI.PinMode;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Manages all pins in use by the hardware
 */
public class PinRegistry {
    private static ArrayList<Integer> connectedPins = new ArrayList<>();

    public static void registerPins(int[] pinNumbers, boolean[] pinModes) {
        if (pinModes.length == 0) return;

        if (pinNumbers.length != pinModes.length) {
            throw new IllegalArgumentException(
                    "Parameter pinNumbers must have the same amount of elements as pinModes. " +
                            "pinNumbers length was: " + pinNumbers.length +
                            ", pinModes length was: " + pinModes.length
            );
        }

        int pinNumber;

        for (int i=0; i<pinNumbers.length; i++) {
            pinNumber = pinNumbers[i];

            if (pinNumber < 0 || pinNumber > 15) {
                throw new IllegalArgumentException(
                        "Invalid pin number. Pin number must be between 0 and 15 (inclusive) but was: " + pinNumber
                );
            }

            if (connectedPins.contains(pinNumber)) {
                // TODO? handle exception
//                throw new IllegalArgumentException(
//                        "Pin number " + pinNumber + " is already occupied"
//                );
            }
            connectedPins.add(pinNumber);
            BoeBot.setMode(pinNumber, pinModes[i] ? PinMode.Input : PinMode.Output);
        }
    }
}