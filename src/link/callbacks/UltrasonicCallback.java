package link.callbacks;

import hardware.inputdevices.sensor.UltrasonicSensor;

/**
 * Used for communication between classes UltrasonicSensor and RobotMain
 *
 * @author Simon
 */
public interface UltrasonicCallback {
    void onUltrasonicSensorEvent(UltrasonicSensor source);
}
