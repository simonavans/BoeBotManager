package hardware.inputdevices;

import TI.BoeBot;
import TI.PinMode;
import application.RobotMain;
import hardware.outputdevices.Engine;
import hardware.outputdevices.Gripper;
import link.Updatable;

/**
 * Class for the infrared sensor/receiver that picks up signals
 * from a Vivanco remote.
 */
public class IRReceiver extends Updatable {
    private final byte pinNumber;

    public void Receiver() {
        Gripper gripper = new Gripper((byte) 0);
        Engine engine = new Engine((byte) 0, (byte) 0);

        BoeBot.setMode(0, PinMode.Input);
        BoeBot.setMode(9, PinMode.Output);

        System.out.println("Waiting for Commands....");

        while (true) {
            String command = "";
            int pulseLen = BoeBot.pulseIn(0, false, 6000);
            if (pulseLen > 2000) {
                int button[] = new int[12];
                for (int i = 0; i < 12; i++) {
                    if (BoeBot.pulseIn(0, false, 20000) < 800) {
                        button[i] = 0;
                    } else {
                        button[i] = 1;
                    }
                    command += Integer.toString(button[i]);
                }
                if (command.equals("0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0")) {
                    gripper.open();
                } else if (command.equals("0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0")) {
                    gripper.close();
                } else if (command.equals("0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0")) {
                    engine.drive(0);
                } else if (command.equals("0, 1, 1, 0, 1, 0, 0, 1, 0, 0, 0, 0")) {
                    engine.drive(-0);
                } else if (command.equals("1, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0")) {
                    engine.turnDegrees(90, 0);
                } else if (command.equals("0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0")) {
                    engine.turnDegrees(-90, 0);
                } else if (command.equals("1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0")) {
                    engine.brake();
                } else if (command.equals("1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0")) {
                    break;
                }
            }
        }
    }

    public IRReceiver(byte pinNumber, RobotMain callback) {
        super(pinNumber, true, callback);
        this.pinNumber = pinNumber;
    }

    @Override
    public void update() {
    }
}
