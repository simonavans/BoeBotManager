package hardware.sensors;

/**
 * This interface defines basic methods applicable for
 * any sensor that is mountable to the BoeBot.
 */
public interface Sensor<T> {

    /**
     * Return the (converted to real world units) value of a sensor.
     * @return sensorValue (int)
     */
    T getSensorValue();
    /**
     * Return if a sensor passes a threshold value.
     * @return if over threshold (boolean)
     */
    boolean isOnThreshold();
}
