package link.callbacks;

/**
 * Used for communication between classes LineSensors and RobotMain.
 *
 * @author Simon
 */
public interface LineSensorsCallback {
    void onAlignedOnCrossroad();
    void onDeviate(boolean detectedByLeftSensor);
    void onDriveStraight();
}