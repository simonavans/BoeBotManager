package link.callbacks;

/**
 * Used for communication between classes Engine and RobotMain
 *
 * @author Simon
 */
public interface EngineCallback {
    void onCompletedTurn();
    void onObjectCanBeDropped();
}
