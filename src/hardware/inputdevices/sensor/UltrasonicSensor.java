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
public class UltrasonicSensor implements Updatable, Sensor<Integer> {
    private final int inputPinNumber;
    private final int outputPinNumber;
    private final int grabThreshold;
    private final int unknownObjectThreshold;
    private final NeoPixel ultrasonicPixel;
    private final RobotMain callback;

    private int measuredDistance;
    private boolean enabled = true;
    private Timer measurementCooldown;

    public UltrasonicSensor(int inputPinNumber, int outputPinNumber, int grabThreshold, int unknownObjectThreshold, NeoPixel ultrasonicPixel, RobotMain callback) {
        PinRegistry.registerPins(new int[]{inputPinNumber, outputPinNumber}, new String[]{"input", "output"});
        this.inputPinNumber = inputPinNumber;
        this.outputPinNumber = outputPinNumber;
        this.grabThreshold = grabThreshold;
        this.unknownObjectThreshold = unknownObjectThreshold;
        this.ultrasonicPixel = ultrasonicPixel;
        this.callback = callback;

        measurementCooldown = new Timer(100);
        measurementCooldown.mark();
    }

    public void enable() {
        enabled = true;
        ultrasonicPixel.turnOn(new Color(0, 128, 0));
    }

    @Override
    public void update() {
        if (enabled) {
            int currentSensorValue = getSensorValue();

            if (currentSensorValue < 1) return;

            if (currentSensorValue <= grabThreshold) {
                callback.onSensorEvent(false);
                enabled = false;
            } else if (currentSensorValue <= unknownObjectThreshold) {
                callback.onSensorEvent(true);
            }
        }
    }

    /**
     * Returns the measured distance (in centimeters) of the ultrasonic sensor.
     * @return distance (in centimeters) between the object that caused the
     * reverberation and the sensor (in centimeters)
     */
    public Integer getSensorValue() {
        if (!measurementCooldown.timeout()) return measuredDistance;

        // Send a signal of 1ms to trigger the ultrasonic speaker
        BoeBot.digitalWrite(outputPinNumber, true);
        BoeBot.uwait(1);
        BoeBot.digitalWrite(outputPinNumber, false);
        // Wait for the return pulse of the ultrasonic sensor
        int pulse = BoeBot.pulseIn(inputPinNumber, true, 10000);
        // To convert the pulse into centimeters, the pulse has to be divided by 58 (integer division is intentional)
        measuredDistance = pulse / 58;
        return measuredDistance;
    }

    /**
     * Returns true if the distance between the ultrasonic sensor and the
     * object which caused the reverberation is smaller than or equal to
     * the grabThreshold value, which is considered to be "right in front of it".
     * @return true if distance is greater than or equal to the grabThreshold value,
     * false if distance is smaller than the grabThreshold value.
     */
    @Override
    public boolean isOnOrOverThreshold() {
        Integer measuredValue = getSensorValue();
        return measuredValue > 0 && measuredValue <= grabThreshold;
    }
}
