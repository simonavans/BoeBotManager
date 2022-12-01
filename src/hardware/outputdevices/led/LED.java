package hardware.outputdevices.led;

public interface LED {
    void turnOn();
    void turnOff();
    void dim(short strength);
    void blink(int milliSeconds);
    void fade(int milliSeconds);
}
