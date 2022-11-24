package control;

import TI.BoeBot;
import TI.PinMode;
import hardware.engine.DriveManager;
import hardware.gripper.Gripper;
import hardware.sensors.Antenna;

import java.util.Scanner;

public class RobotMain {
    private static Antenna antennaL;
    private static Antenna antennaR;
    private static boolean isToggled;
    private static boolean state;

    public static void main(String[] args) {
        init();
        runLoop();
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
            DriveManager.drive(30);
//            if (lineRight.getSensorValue() > 1400 && lineRight.getSensorValue() < 1600) {
//                DriveManager.wheelRight(60);
//                BoeBot.wait(700);
//            }
//
//            if (lineLeft.getSensorValue() > 1400 && lineLeft.getSensorValue() < 1600) {
//                DriveManager.wheelLeft(60);
//                BoeBot.wait(700);
//            }
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
