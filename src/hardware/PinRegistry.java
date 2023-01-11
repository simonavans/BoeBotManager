package hardware;

import TI.BoeBot;
import TI.PinMode;

import java.util.HashMap;

/**
 * Manages all pins in use by the hardware. It can detect hardware
 * being assigned to the same pin and check for valid pin numbers.
 * Apart from input and output pin numbers, it can also manage ADC
 * pins and Neopixel led numbers.
 */
public class PinRegistry {
    private static HashMap<String, String> pins = new HashMap<>();

    /**
     * Registers pins to use by hardware on the BoeBot.
     * @param pinNumbers an array of pin numbers to register.
     * @param pinModes an array of pin modes in the same order as the
     *                 pin numbers in the pinNumbers parameter.
     *
     * @author Simon
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

        // Loop through pin numbers and check them
        for (int i = 0; i < pinNumbers.length; i++) {
            pinNumber = pinNumbers[i];
            pinMode = pinModes[i];

            checkPin(pinNumber, pinMode);

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
     *
     * @author Simon
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
                pins.put("gpio" + pinNumber, pinMode);
                break;
            case "adc":
                if (pinNumber < 0 || pinNumber > 3) {
                    throw new IllegalArgumentException(
                            "Invalid pin number. ADC pin numbers must be between 0 and 3 (inclusive) but was: " + pinNumber
                    );
                }
                pins.put("adc" + pinNumber, "adc");
                break;
            case "neopixel":
                if (pinNumber < 0 || pinNumber > 5) {
                    throw new IllegalArgumentException(
                            "Invalid LED number. Neopixel LED numbers must be between 0 and 5 (inclusive) but was: " + pinNumber
                    );
                }
                pins.put("neopixel" + pinNumber, "neopixel");
                break;
            default:
                throw new IllegalArgumentException(
                        "Invalid pin mode: " + pinMode
                );
        }

        //todo fix this whole class basically :(
//        if (pins.containsKey("" + pinNumber) && pins.get("" + pinNumber).equals(pinMode)) {
//            throw new IllegalArgumentException(
//                    "Pin number " + pinNumber + " is already occupied with mode " + pinMode
//            );
//        }
    }
}