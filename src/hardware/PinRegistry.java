package hardware;

import TI.BoeBot;
import TI.PinMode;

import java.util.ArrayList;

/**
 * Manages all pins in use by the hardware
 */
public class PinRegistry {
    private static ArrayList<Integer> gpioPins = new ArrayList<>();
    private static ArrayList<Integer> adcPins = new ArrayList<>();

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

        for (int i=0; i<pinNumbers.length; i++) {
            pinNumber = pinNumbers[i];
            pinMode = pinModes[i];

            if (pinNumber < 0 || pinNumber > 15) {
                throw new IllegalArgumentException(
                        "Invalid pin number. Pin number must be between 0 and 15 (inclusive) but was: " + pinNumber
                );
            }

            // Check GPio pins
            if (pinMode.equalsIgnoreCase("input")) {
                if (gpioPins.contains(pinNumber)) {
                    // TODO? handle exception
                    throw new IllegalArgumentException(
                            "Pin number " + pinNumber + " is already occupied"
                    );
                }
                gpioPins.add(pinNumber);
                BoeBot.setMode(pinNumber, PinMode.Input);
            } else if (pinMode.equalsIgnoreCase("output")) {
                if (gpioPins.contains(pinNumber)) {
                    // TODO? handle exception
                    throw new IllegalArgumentException(
                            "Pin number " + pinNumber + " is already occupied"
                    );
                }
                gpioPins.add(pinNumber);
                BoeBot.setMode(pinNumber, PinMode.Output);
            } else if (pinMode.equalsIgnoreCase("pwm")) {
                if (gpioPins.contains(pinNumber)) {
                    // TODO? handle exception
                    throw new IllegalArgumentException(
                            "Pin number " + pinNumber + " is already occupied"
                    );
                }
                gpioPins.add(pinNumber);
                BoeBot.setMode(pinNumber, PinMode.PWM);
            }
            // Check ADC pins
            else if (pinMode.equalsIgnoreCase("adc")) {
                if (adcPins.contains(pinNumber)) {
                    // TODO? handle exception
                    throw new IllegalArgumentException(
                            "Pin number " + pinNumber + " is already occupied"
                    );
                }
                adcPins.add(pinNumber);
            } else {
                throw new IllegalArgumentException(
                        "Invalid pin mode: " + pinMode
                );
            }
        }
    }
}