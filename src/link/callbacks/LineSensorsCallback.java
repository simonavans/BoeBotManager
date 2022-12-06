package link.callbacks;

public interface LineSensorsCallback {
    void onDetectCrossroad();
    void onDeviate(boolean toLeft);
    void onDriveStraight();
}