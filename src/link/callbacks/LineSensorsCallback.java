package link.callbacks;

/**
 * Used for communication between classes LineSensors and RobotMain
 *
 * @author Simon
 */
public interface LineSensorsCallback {
    void onDetectCrossroad();
    void onDeviate(boolean toLeft);
    void onDriveStraight();
}