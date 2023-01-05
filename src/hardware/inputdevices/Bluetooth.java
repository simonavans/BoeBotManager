package hardware.inputdevices;

import TI.SerialConnection;
import application.RobotMain;
import link.Updatable;

/**
 * Class for the bluetooth chip that picks up signals
 * from a different application.
 */
public class Bluetooth implements Updatable {

    private SerialConnection serial;
    private final RobotMain callback;
    private String receivedCommand = "";

    /**
     * Constructor for creating a bluetooth connection with another application.
     * @param baudrate the baudrate used by the bluetooth module
     * @param callback callback to the main class
     *
     * @author Kerr
     */
    public Bluetooth(int baudrate, RobotMain callback) {
        this.serial = new SerialConnection(baudrate);
        this.callback = callback;
    }

    /**
     * Holds on to a command until no new data is received after which the command is send to the callback
     *
     * @author Kerr
     */
    @Override
    public void update() {
        // Check if a new character is available and if so, add it to the command
        if (serial.available() > 0) {
            receivedCommand += (char) serial.readByte();
        } else if (!receivedCommand.equals("")) {
            // If no new character is available the gathered command is send and the command String is reset
            callback.onBluetoothEvent(receivedCommand);
            receivedCommand = "";
        }
    }

    /**
     * Sends a command over bluetooth to another application.
     * @param command the command that will be send to the application.
     *
     * @author Kerr
     */
    public void transmitCommand(String command) {
        // Send the given String character by character
        for (int i = 0; i < command.length(); i++) {
            serial.writeByte(command.charAt(i));
        }
        // Add a "*" to the end signaling the end of a command
        serial.writeByte(42);
    }

}

