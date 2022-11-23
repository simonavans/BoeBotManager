package hardware.sensors;

import TI.BoeBot;
import TI.PinMode;
/**
 * The antenna sensor is a type of sensor that can be compared to a whisker. It extends past the BoeBot and can sense
 * objects in front of it.
 */
public class Antenna implements Sensor {
    private final byte connectedPin;
    /**
     * Constructor creating the AntennaSensor
     * @param connectedPin pin number the antenna is connected to, between 0 - 15
     */
    public Antenna(byte connectedPin) {
        this.connectedPin = connectedPin;
        BoeBot.setMode(connectedPin, PinMode.Input);
    }

    /**
     * Returns the measured value of the Antenna.
     * @return 1 = the antenna is pressed, 0 = the antenna is not pressed
     */
    public Integer getSensorValue() {
        if (!BoeBot.digitalRead(connectedPin)) {
            return 1;
        }
        return 0;
    }

    /**
     * returns if the antenna is pressed
     * @return true = antenna is pressed, false = the antenna
     */
    public boolean isOnThreshold() {
        return !BoeBot.digitalRead(connectedPin);
    }
}