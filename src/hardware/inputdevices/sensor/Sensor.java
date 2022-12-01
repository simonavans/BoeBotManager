package hardware.inputdevices.sensor;

/**
 * Interface implemented by all sensor classes.
 * @param <T> the type in which the sensor measures, e.g. an Integer or Byte
 */
public interface Sensor<T> {
    // TODO decide whether to exclude getSensorValue from Sensor interface
    T getSensorValue();
    boolean isOnOrOverThreshold();
}

