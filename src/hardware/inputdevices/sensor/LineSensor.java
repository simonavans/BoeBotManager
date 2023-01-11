package hardware.inputdevices.sensor;

import TI.BoeBot;
import hardware.PinRegistry;
import link.LineSensors;
import link.Updatable;

/**
 * Class for one line sensor, which can detect the brightness of a surface. The BoeBot uses three
 * of these sensors to be able to follow a black line on a white surface.
 */
public class LineSensor implements Updatable, Sensor {
    private final int pinNumber;
    private final LineSensors callback;

    // If this sensor reads values higher than or equal to the threshold, it notifies the
    // LineSensors class.
    private int threshold;

    // The maximum value the line sensor can measure. It is used for when the BoeBot is picked up,
    // resulting in far higher measurement values than the value of a black line can be. To
    // prevent this, this variable is used.
    private int ceiling;

    // Whether the line sensor previously saw a line, so that it does not spam the callback class
    // with the same state over and over again.
    private boolean previouslyDetectedLine;

    public LineSensor(int pinNumber, int threshold, int ceiling, LineSensors callback) {
        PinRegistry.registerPins(new int[]{pinNumber}, new String[]{"adc"});
        this.pinNumber = pinNumber;
        this.threshold = threshold;
        this.ceiling = ceiling;
        this.callback = callback;

        this.previouslyDetectedLine = false;
    }

    /**
     * Checks whether this line sensor's threshold has been overcome, and calls back to
     * the class LineSensors
     *
     * @author Simon
     */
    @Override
    public void update() {
        if (isOnOrOverThreshold() && !previouslyDetectedLine) {
            previouslyDetectedLine = true;
            callback.onLineDetectedEvent(this);
        } else if (previouslyDetectedLine) {
            previouslyDetectedLine = false;
            callback.onNoLineDetectedEvent(this);
        }
    }

    /**
     * @return whether the threshold was reached
     *
     * @author Simon
     */
    @Override
    public boolean isOnOrOverThreshold() {
        return BoeBot.analogRead(pinNumber) >= threshold &&
                BoeBot.analogRead(pinNumber) < ceiling;
    }
}
