package hardware.inputdevices.sensor;

import TI.BoeBot;
import application.RobotMain;
import link.Updatable;

/**
 * Class for the line sensor, which can detect the brightness of a surface.
 * The BoeBot uses three of these sensors to be able to follow a black line
 * on a white surface.
 */
public class LineSensor extends Updatable implements Sensor<Integer> {
    private final int pinNumber;
    private final LineSensors callback;
    private int threshold;
    private int ceiling;

    public LineSensor(int pinNumber, int threshold, int ceiling, LineSensors callback) {
        super(new int[]{pinNumber}, new boolean[]{});
        this.pinNumber = pinNumber;
        this.threshold = threshold;
        this.ceiling = ceiling;
        this.callback = callback;
    }

    public void update() {
        if (isOnOrOverThreshold()) {
            callback.onLineDetectEvent(this);
        } else {
            callback.onLineUndetectEvent(this);
        }
    }

    @Override
    public Integer getSensorValue() {
        return BoeBot.analogRead(pinNumber);
    }

    @Override
    public boolean isOnOrOverThreshold() {
        return BoeBot.analogRead(pinNumber) >= threshold &&
                BoeBot.analogRead(pinNumber) < ceiling;
    }
}
