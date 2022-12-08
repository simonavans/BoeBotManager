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

    public Bluetooth(RobotMain callback, int baudrate) {
        this.serial = new SerialConnection(baudrate);
        this.callback = callback;
    }

    @Override
    public void update() {
        if (serial.available() > 0) {
            int data = serial.readByte();
            System.out.println(data);
            callback.onBluetoothEvent(data);
        }
    }
}

