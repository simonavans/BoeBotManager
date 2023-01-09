package link.callbacks;

import hardware.inputdevices.sensor.UltrasonicSensor;

/**
 * Interface that is called by any sensor and received by RobotMain.
 */
public interface UltrasonicCallback {
    void onUltrasonicSensorEvent(UltrasonicSensor source);
}
