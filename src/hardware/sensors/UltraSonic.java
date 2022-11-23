package hardware.sensors;

import TI.BoeBot;
import TI.PinMode;
/**
 * The ultrasonic sensor is a type of sensor that, given an electronic signal, sends an ultrasonic signal into the space
 * it is in and waits for a reverberation signal. It then returns a signal that represents the distance between the
 * sensor and the object that caused the reverberation.
 */
public class UltraSonic implements Sensor {
    private final byte connectedInputPin;
    private final byte connectedOutputPin;
    private int measuredDistance;
    /**
     * Constructor creating the AntennaSensor
     *
     * @param connectedInputPin pin number to which the ultrasonic sensor's 'trigger' pin is connected to, between 0 - 15
     * @param connectedOutputPin pin number to which the ultrasonic sensor's 'echo' pin is connected to, between 0 - 15
     */
    public UltraSonic(byte connectedInputPin, byte connectedOutputPin) {
        this.connectedInputPin = connectedInputPin;
        this.connectedOutputPin = connectedOutputPin;
        BoeBot.setMode(this.connectedInputPin, PinMode.Input);
        BoeBot.setMode(this.connectedOutputPin, PinMode.Output);
    }

    //FIXME This following method does not seem to work
    //TODO prevent execution if previous execution was less than 100ms earlier
    /**
     * Returns the measured distance (in centimeter) of the ultrasonic sensor. Note: only use this method every 100 ms!
     *
     * @return distance (in centimeters) between the object that caused the reverberation and the sensor (in centimeter)
     */
    public Integer getSensorValue() {
        // Send a signal of 1ms to trigger the ultrasonic speaker
        BoeBot.digitalWrite(connectedOutputPin, true);
        BoeBot.wait(1);
        BoeBot.digitalWrite(connectedOutputPin, false);
        // Wait for receive the return pulse of the ultrasonic sensor
        int pulse = BoeBot.pulseIn(connectedInputPin, true, 10000); //TODO see if the 10s wait period causes issues
        // To convert the pulse into centimeters, the pulse has to be divided by 58 (integer division is intentional)
        return pulse / 58;
    }

    /**
     * returns if the distance between the ultrasonic sensor and the object which caused the reverberation is smaller
     * than ADD NUMBER centimeters, which is considered to be "right in front of it" //TODO determine threshold value
     *
     * @return true = distance is smaller than NUMBER centimeters, false = distance is larger //TODO determine threshold value
     */
    public boolean isOnThreshold() {
        return measuredDistance < 10; //TODO determine threshold value
    }
}
