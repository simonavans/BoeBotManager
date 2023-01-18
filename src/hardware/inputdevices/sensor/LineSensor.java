package hardware.inputdevices.sensor;

import TI.BoeBot;
import hardware.PinRegistry;

/**
 * Class for one line sensor, which can detect the brightness of a surface. The BoeBot uses three
 * of these sensors to be able to follow a black line on a white surface.
 */
public class LineSensor implements Sensor {
    private final int pinNumber;

    // If this sensor reads values higher than or equal to the threshold, it notifies the
    // LineSensors class.
    private int threshold;

    // The maximum value the line sensor can measure. It is used for when the BoeBot is picked up,
    // resulting in far higher measurement values than the value of a black line can be. To
    // prevent this, this variable is used.
    private int ceiling;

    public LineSensor(int pinNumber, int threshold, int ceiling) {
        PinRegistry.registerPins(new int[]{pinNumber}, new String[]{"adc"});
        this.pinNumber = pinNumber;
        this.threshold = threshold;
        this.ceiling = ceiling;
    }

    /**
     * @return whether the threshold was reached
     *
     * @author Kerr and Simon
     */
    @Override
    public boolean isOnOrOverThreshold() {
        return BoeBot.analogRead(pinNumber) >= threshold &&
                BoeBot.analogRead(pinNumber) < ceiling;
    }
}
