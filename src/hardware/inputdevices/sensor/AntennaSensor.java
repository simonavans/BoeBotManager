package hardware.inputdevices.sensor;

import TI.BoeBot;
import application.RobotMain;
import hardware.PinRegistry;
import link.Updatable;

/**
 * The antenna sensor is a type of sensor that can be compared to a whisker.
 * It extends past the BoeBot and can sense objects in front of it.
 */
public class AntennaSensor implements Updatable, Sensor<Boolean> {
    private final int pinNumber;
    private final RobotMain callback;

    public AntennaSensor(int pinNumber, RobotMain callback) {
        PinRegistry.registerPins(new int[]{pinNumber}, new String[]{"input"});
        this.pinNumber = pinNumber;
        this.callback = callback;
    }

    @Override
    public void update() {
        if (isOnOrOverThreshold()) {
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
