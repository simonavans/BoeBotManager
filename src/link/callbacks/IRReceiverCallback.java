package link.callbacks;

/**
 * Used for communication between classes IRReceiver and RobotMain
 *
 * @author Simon
 */
public interface IRReceiverCallback {
    void onIRReceiverEvent(int receiverCode);
}
