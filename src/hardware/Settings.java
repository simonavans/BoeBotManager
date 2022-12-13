package hardware;

public class Settings {

    /**
     * @author Timo
     */
    //Variables
    private int speed;
    private int stopspeed;
    private int treshold;
    private int ceiling;
    private int gripperPin;
    private int leftWheelPin;
    private int rightWheelPin;
    private int ultraSonicInputPin;
    private int ultraSonicOutputPin;
    private int irReceiverPin;
    private int[] lineSensorPins;
    private int bautrate;

    public Settings(int speed, int stopspeed, int treshold, int ceiling, int gripperPin, int leftWheelPin, int rightWheelPin, int ultraSonicInputPin, int ultraSonicOutputPin, int irReceiverPin, int[] lineSensorPins, int bautrate) {
        this.speed = speed;
        this.stopspeed = stopspeed;
        this.treshold = treshold;
        this.ceiling = ceiling;
        this.gripperPin = gripperPin;
        this.leftWheelPin = leftWheelPin;
        this.rightWheelPin = rightWheelPin;
        this.ultraSonicInputPin = ultraSonicInputPin;
        this.ultraSonicOutputPin = ultraSonicOutputPin;
        this.irReceiverPin = irReceiverPin;
        this.lineSensorPins = lineSensorPins;
        this.bautrate = bautrate;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getStopspeed() {
        return stopspeed;
    }

    public void setStopspeed(int stopspeed) {
        this.stopspeed = stopspeed;
    }

    public int getTreshold() {
        return treshold;
    }

    public void setTreshold(int treshold) {
        this.treshold = treshold;
    }

    public int getCeiling() {
        return ceiling;
    }

    public void setCeiling(int ceiling) {
        this.ceiling = ceiling;
    }

    public int getGripperPin() {
        return gripperPin;
    }

    public void setGripperPin(int gripperPin) {
        this.gripperPin = gripperPin;
    }

    public int getLeftWheelPin() {
        return leftWheelPin;
    }

    public void setLeftWheelPin(int leftWheelPin) {
        this.leftWheelPin = leftWheelPin;
    }

    public int getRightWheelPin() {
        return rightWheelPin;
    }

    public void setRightWheelPin(int rightWheelPin) {
        this.rightWheelPin = rightWheelPin;
    }

    public int getUltraSonicInputPin() {
        return ultraSonicInputPin;
    }

    public void setUltraSonicInputPin(int ultraSonicInputPin) {
        this.ultraSonicInputPin = ultraSonicInputPin;
    }

    public int getUltraSonicOutputPin() {
        return ultraSonicOutputPin;
    }

    public void setUltraSonicOutputPin(int ultraSonicOutputPin) {
        this.ultraSonicOutputPin = ultraSonicOutputPin;
    }

    public int getIrReceiverPin() {
        return irReceiverPin;
    }

    public void setIrReceiverPin(int irReceiverPin) {
        this.irReceiverPin = irReceiverPin;
    }

    public int[] getLineSensorPins() {
        return lineSensorPins;
    }

    public void setLineSensorPins(int[] lineSensorPins) {
        this.lineSensorPins = lineSensorPins;
    }

    public int getBautrate() {
        return bautrate;
    }

    public void setBautrate(int bautrate) {
        this.bautrate = bautrate;
    }
}
