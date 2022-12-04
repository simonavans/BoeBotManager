package hardware.outputdevices.led;

public interface LED {
    void turnOn();
    void turnOff();
    void updateBlink(int milliSeconds);
}
