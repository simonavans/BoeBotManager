package link.callbacks;

import hardware.inputdevices.sensor.LineSensor;

/**
 * Used for communication between classes LineSensor and LineSensors
 *
 * @author Simon
 */
public interface LineSensorCallback {
    void onLineDetectedEvent(LineSensor source);
    void onNoLineDetectedEvent(LineSensor source);
}
