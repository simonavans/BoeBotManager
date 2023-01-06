package hardware;

import TI.BoeBot;
import TI.PinMode;

import java.util.HashMap;

/**
 * Manages all pins in use by the hardware
 */
public class PinRegistry {
    private static HashMap<Integer, String> pins = new HashMap<>();

    /**
     * Registers pins to use by hardware on the BoeBot.
     * @param pinNumbers an array of pin numbers to register.
     * @param pinModes an array of pin modes in the same order as the
     *                 pin numbers in the pinNumbers parameter
     */
    public static void registerPins(int[] pinNumbers, String[] pinModes) {
        if (pinNumbers.length != pinModes.length) {
            throw new IllegalArgumentException(
                    "Parameter pinNumbers must have the same amount of elements as pinModes. " +
                            "pinNumbers length was: " + pinNumbers.length +
                            ", pinModes length was: " + pinModes.length
            );
        }
        if (pinNumbers.length == 0) return;

        int pinNumber;
        String pinMode;

        for (int i = 0; i < pinNumbers.length; i++) {
            pinNumber = pinNumbers[i];
            pinMode = pinModes[i];

            checkPin(pinNumber, pinMode);
            // Pins are all correct by this point
            pins.put(pinNumber, pinMode);

            // Check GPio input pins
            if (pinMode.equalsIgnoreCase("input")) {
                BoeBot.setMode(pinNumber, PinMode.Input);
            }
            // Check GPio output pins
            else if (pinMode.equalsIgnoreCase("output")) {
                BoeBot.setMode(pinNumber, PinMode.Output);
            }
        }
    }

    /**
     * Checks pins for any duplicate pin numbers and if the pin number exists
     * on the BoeBot.
     * @param pinNumber the pin number to check.
     * @param pinMode the pin mode to check.
     */
    private static void checkPin(int pinNumber, String pinMode) {
        switch(pinMode) {
            case "input":
            case "output":
                if (pinNumber < 0 || pinNumber > 15) {
                    throw new IllegalArgumentException(
                            "Invalid pin number. Input and output pin numbers must be between 0 and 15 (inclusive) but was: " + pinNumber
                    );
                }
                break;
            case "adc":
                if (pinNumber < 0 || pinNumber > 3) {
                    throw new IllegalArgumentException(
                            "Invalid pin number. ADC pin numbers must be between 0 and 3 (inclusive) but was: " + pinNumber
                    );
                }
                break;
            case "neopixel":
                if (pinNumber < 0 || pinNumber > 5) {
                    throw new IllegalArgumentException(
                            "Invalid LED number. Neopixel LED numbers must be between 0 and 5 (inclusive) but was: " + pinNumber
                    );
                }
                break;
            default:
                throw new IllegalArgumentException(
                        "Invalid pin mode: " + pinMode
                );
        }

        if (pins.containsKey(pinNumber) && pins.get(pinNumber).equals(pinMode)) {
            throw new IllegalArgumentException(
                    "Pin number " + pinNumber + " is already occupied with mode " + pinMode
            );
        }
    }
}