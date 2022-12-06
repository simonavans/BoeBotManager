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
    private final int threshold = 1400; //TODO set threshold

    public LineSensor(byte pinNumber, RobotMain callback) {
        super(pinNumber, true, callback);
    }

    @Override
    public void update() {
        if (isOnOrOverThreshold()) {
            callback.onSensorEvent(this);
        }
    }

    @Override
    public Integer getSensorValue() {
        return BoeBot.analogRead(pinNumber);
    }

    @Override
    public boolean isOnOrOverThreshold() {
        return false;
    }
}
