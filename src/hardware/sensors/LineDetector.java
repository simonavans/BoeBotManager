package hardware.sensors;

import TI.BoeBot;

//TODO make this class work
public class LineDetector implements Sensor{
    private final byte pin;

    public LineDetector(byte pin) {
        this.pin = pin;
    }
    @Override
    public Integer getSensorValue() {
        return BoeBot.analogRead(this.pin);
    }

    @Override
    public boolean isOnThreshold() {
        return false;
    }
}

