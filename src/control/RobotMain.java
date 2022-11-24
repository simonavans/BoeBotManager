package control;

import TI.BoeBot;
import TI.PinMode;
import hardware.engine.DriveManager;
import hardware.gripper.Gripper;
import hardware.sensors.Antenna;

public class RobotMain {
    private static Antenna antennaL;
    private static Antenna antennaR;
    private static boolean isToggled;
    private static boolean state;

    public static void main(String[] args) {
        init();
    }

    private static void init() {
        antennaL = new Antenna((byte)7);
        antennaR = new Antenna((byte)8);
        isToggled = true;
        state = true;
        BoeBot.setMode(0, PinMode.Input);
        Gripper.open();
    }

    private static void runLoop() {
        while (true) {
            DriveManager.drive(20);
            if (!BoeBot.digitalRead(0)) {
                if (!isToggled) {
                    state = !state;
                    isToggled = true;
                }
            } else if (isToggled){
                isToggled = false;
            }

            if (state) {
                Gripper.open();
            } else {
                Gripper.close();
            }

            if (antennaR.getSensorValue() == 1) {
                ActionManager.bumpTurnLeft();
            }
            if (antennaL.getSensorValue() == 1) {
                ActionManager.bumpTurnRight();
            }
            BoeBot.wait(10);
        }
    }
}
