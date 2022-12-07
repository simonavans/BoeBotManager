package link.callbacks;

import hardware.inputdevices.sensor.LineSensor;

public interface LineSensorCallback {
    void onLineDetectedEvent(LineSensor source);
    void onLineUndetectedEvent(LineSensor source);
}
