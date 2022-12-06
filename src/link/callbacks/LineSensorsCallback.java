package link.callbacks;

public interface LineSensorsCallback {
    void onDetectCrossroad();
    void onDeviateLeft();
    void onDeviateRight();
    void onDriveStraight();
}