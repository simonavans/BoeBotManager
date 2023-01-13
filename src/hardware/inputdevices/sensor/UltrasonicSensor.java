package hardware.inputdevices.sensor;

import TI.BoeBot;
import TI.Timer;
import application.RobotMain;
import hardware.PinRegistry;
import hardware.outputdevices.led.NeoPixel;
import link.Updatable;

import java.awt.*;

/**
 * The ultrasonic sensor is a type of sensor that, given an electronic signal,
 * sends an ultrasonic signal into the space it is in and waits for a
 * reverberation signal. It then returns a signal that represents the distance
 * between the sensor and the object that caused the reverberation.
 */
public class UltrasonicSensor implements Updatable, Sensor {
    private final int inputPinNumber;
    private final int outputPinNumber;

    // If this sensor reads values lower than or equal to the threshold, it notifies the
    // RobotMain class.
    private final int threshold;

    // The Neopixel which this sensor uses to notify whether it is enabled (green) or
    // disabled (red).
    private final NeoPixel ultrasonicPixel;
    private final RobotMain callback;

    private int measuredDistance;
    private boolean enabled;

    // By default, all ultrasonic sensors have a cooldown between measurements of
    // 100 milliseconds. This is to prevent any echoes from previous measurements
    // being picked up by the sensor, leading to incorrect measurements.
    private Timer measurementCooldown;

    public UltrasonicSensor(int inputPinNumber, int outputPinNumber, int threshold, NeoPixel ultrasonicPixel, RobotMain callback) {
        PinRegistry.registerPins(new int[]{inputPinNumber, outputPinNumber}, new String[]{"input", "output"});
        this.inputPinNumber = inputPinNumber;
        this.outputPinNumber = outputPinNumber;
        this.threshold = threshold;
        this.ultrasonicPixel = ultrasonicPixel;
        this.callback = callback;
        this.enabled = true;

        if (ultrasonicPixel != null) ultrasonicPixel.turnOn(new Color(100, 0, 0));
        measurementCooldown = new Timer(100);
        measurementCooldown.mark();
    }

    /**
     * Method used for enabling or disabling the sensor. Its assigned Neopixel will
     * turn green when enabled and red when disabled.
     * @param enabled whether the sensor should be enabled or disabled.
     *
     * @author Simon
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;

        if (ultrasonicPixel == null) return;

        if (enabled) {
            ultrasonicPixel.turnOn(new Color(0, 128, 0));
        } else {
            ultrasonicPixel.turnOn(new Color(128, 0, 0));
        }
    }

    /**
     * When enabled, this method sends its measurements to RobotMain using a
     * callback.
     *
     * @author Simon and Kerr
     */
    @Override
    public void update() {
        if (!enabled) return;

        if (isOnOrOverThreshold()) {
            callback.onUltrasonicSensorEvent(this);
        }
    }

    /**
     * Method for deciding whether or not the current measurement has reached
     * the threshold for taking action.
     * @return true if the distance between the ultrasonic sensor and the object
     * which caused the reverberation is smaller than or equal to the threshold
     * value, which is considered to be "right in front of it". For a correct
     * measurement, the value needs to be greater than 0.
     *
     * @author Kerr
     */
    @Override
    public boolean isOnOrOverThreshold() {
        Integer measuredValue = getSensorValue();
        return measuredValue > 0 && measuredValue <= threshold;
    }

    /**
     * Measures the current distance between the sensor and any obstructions.
     * @return the distance (in centimeters) between the object that caused the
     * reverberation and the sensor.
     *
     * @author Kerr
     */
    private Integer getSensorValue() {
        if (!measurementCooldown.timeout()) return measuredDistance;

        // Send a signal of 1 microsecond to trigger the ultrasonic sensor
        BoeBot.digitalWrite(outputPinNumber, true);
        BoeBot.uwait(1);
        BoeBot.digitalWrite(outputPinNumber, false);

        // Wait for the return pulse of the ultrasonic sensor
        int pulse = BoeBot.pulseIn(inputPinNumber, true, 10000);

        // To convert the pulse into centimeters, the pulse has to be divided by 58 (integer division is intentional)
        measuredDistance = pulse / 58;
        return measuredDistance;
    }
}
