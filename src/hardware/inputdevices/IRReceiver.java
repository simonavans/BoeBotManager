package hardware.inputdevices;

import TI.BoeBot;
import TI.PinMode;
import application.RobotMain;
import hardware.outputdevices.Engine;
import hardware.outputdevices.Gripper;
import link.Updatable;

import java.util.Arrays;

/**
 * Class for the infrared sensor/receiver that picks up signals
 * from a Vivanco remote.
 */
public class IRReceiver extends Updatable {
    private final byte pinNumber;

    public void Receiver() {
        Gripper gripper = new Gripper((byte) 0);
        Engine engine = new Engine((byte) 12, (byte) 13);

        BoeBot.setMode(3, PinMode.Input);

        System.out.println("Waiting for Commands....");

        while(true) {
            String command;
            int pulseLen = BoeBot.pulseIn(3, false, 6000);
            if (pulseLen > 2000) {
                int button[] = new int[12];
                for (int i = 0; i < 12; i++) {
                    if (BoeBot.pulseIn(3, false, 20000) < 800) {
                        button[i] = 0;
                    } else {
                        button[i] = 1;
                    }
                }

                command = Arrays.toString(button).replace(",", "");
                command = command.replace("[", "").replace("]", "");
                command = command.replace(" ", "");

                System.out.println("command: "+ command);

                if (command.equals("000000010000")) {
                    gripper.open();
                } else if (command.equals("010000010000")) {
                    gripper.close();
                } else if (command.equals("010010010000")) {
                    engine.turnDegrees(270, 50);
                } else if (command.equals("110010010000")) {
                    engine.turnDegrees(90, 50);
                } else if (command.equals("100010010000")) {
                    engine.drive(-50);
                } else if (command.equals("000010010000")) {
                    engine.drive(50);
                } else if (command.equals("100000010000")) {
                    engine.brake();
                } else if (command.equals("100100010000")) {
                    break;
                }

                engine.brake();
            }

            BoeBot.wait(10);
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
