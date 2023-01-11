package link.callbacks;

/**
 * Used for communication between classes Bluetooth and RobotMain
 *
 * @author Kerr
 */
public interface BluetoothCallback {
    void onBluetoothEvent(String command);
}
