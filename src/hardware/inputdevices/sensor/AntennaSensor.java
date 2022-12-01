package hardware.inputdevices.sensor;

import TI.BoeBot;
import application.RobotMain;
import link.Updatable;

/**
 * The antenna sensor is a type of sensor that can be compared to a whisker.
 * It extends past the BoeBot and can sense objects in front of it.
 */
public class AntennaSensor extends Updatable implements Sensor<Boolean> {

    public AntennaSensor(byte pinNumber, RobotMain callback) {
        super(pinNumber, true, callback);
    }

    @Override
    public void update() {
        if (!BoeBot.digitalRead(pinNumber)) {
            callback.onSensorEvent(this);
        }
    }

    @Override
    public Boolean getSensorValue() {
        return isOnOrOverThreshold();
    }

    @Override
    public boolean isOnOrOverThreshold() {
        return !BoeBot.digitalRead(pinNumber);
    }
}
