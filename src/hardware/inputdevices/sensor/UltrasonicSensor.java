package hardware.inputdevices.sensor;

import TI.BoeBot;
import TI.Timer;
import application.RobotMain;
import link.Updatable;

/**
 * The ultrasonic sensor is a type of sensor that, given an electronic signal,
 * sends an ultrasonic signal into the space it is in and waits for a
 * reverberation signal. It then returns a signal that represents the distance
 * between the sensor and the object that caused the reverberation.
 */
public class UltrasonicSensor extends Updatable implements Sensor<Integer> {
    private final int inputPinNumber;
    private final int outputPinNumber;
    private final RobotMain callback;
    private int measuredDistance;
    private int threshold = 9000;
    private boolean enabled = true;
    private Timer clock = new Timer(100);

    public UltrasonicSensor(int inputPinNumber, int outputPinNumber, RobotMain callback) {
        super(new int[]{inputPinNumber, outputPinNumber}, new String[]{"input", "output"});
        this.inputPinNumber = inputPinNumber;
        this.outputPinNumber = outputPinNumber;
        this.callback = callback;
        clock.mark();
    }

    public void enable() {
        enabled = true;
    }

    public void disable() {
        enabled = false;
    }

    @Override
    public void update() {
        if (isOnOrOverThreshold() && enabled) {
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
        if (!clock.timeout()) return measuredDistance;

        // Send a signal of 1ms to trigger the ultrasonic speaker
        BoeBot.digitalWrite(outputPinNumber, true);
        BoeBot.uwait(1);
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
        return getSensorValue() <= 6 && getSensorValue() > 0;
    }
}
