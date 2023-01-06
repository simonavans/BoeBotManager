package link.callbacks;

import hardware.inputdevices.sensor.Sensor;

/**
 * Interface that is called by any sensor and received by RobotMain.
 */
public interface UltrasonicCallback {
    void onSensorEvent(boolean isUnknownObject);
}
