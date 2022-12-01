package hardware.inputdevices.sensor;

import TI.BoeBot;
import application.RobotMain;
import link.Updatable;

/**
 * The ultrasonic sensor is a type of sensor that, given an electronic signal,
 * sends an ultrasonic signal into the space it is in and waits for a
 * reverberation signal. It then returns a signal that represents the distance
 * between the sensor and the object that caused the reverberation.
 */
public class UltrasonicSensor extends Updatable implements Sensor<Integer> {
    private final byte inputPinNumber;
    private final byte outputPinNumber;
    private int measuredDistance;
    private int threshold; //TODO set threshold

    public UltrasonicSensor(byte inputPinNumber, byte outputPinNumber, RobotMain callback) {
        super(inputPinNumber, true, outputPinNumber, false, callback);
        this.inputPinNumber = inputPinNumber;
        this.outputPinNumber = outputPinNumber;
    }

    @Override
    public void update() {
        if (isOnOrOverThreshold()) {
            callback.onSensorEvent(this);
        }
    }

    //FIXME This following method does not seem to work
    //TODO prevent execution if previous execution was less than 100ms earlier
    /**
     * Returns the measured distance (in centimeter) of the ultrasonic sensor.
     * @return distance (in centimeters) between the object that caused the
     * reverberation and the sensor (in centimeter)
     */
    public Integer getSensorValue() {
        // Send a signal of 1ms to trigger the ultrasonic speaker
        BoeBot.digitalWrite(outputPinNumber, true);
        BoeBot.wait(1);
        BoeBot.digitalWrite(outputPinNumber, false);
        // Wait for the return pulse of the ultrasonic sensor
        int pulse = BoeBot.pulseIn(inputPinNumber, true, 10000); //TODO see if the 10s wait period causes issues
        // To convert the pulse into centimeters, the pulse has to be divided by 58 (integer division is intentional)
        measuredDistance = pulse / 58;
        return measuredDistance;
    }

    /**
     * Returns true if the distance between the ultrasonic sensor and the
     * object which caused the reverberation is greater than or equal to
     * the threshold value, which is considered to be "right in front of it"
     * @return true if distance is greater than or equal to the threshold value,
     * false if distance is smaller than the threshold value
     */
    @Override
    public boolean isOnOrOverThreshold() {
        return measuredDistance >= threshold;
    }
}
