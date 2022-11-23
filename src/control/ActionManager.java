package control;

import TI.BoeBot;
import hardware.engine.DriveManager;

public class ActionManager {

    public static void bumpTurnLeft() {
        DriveManager.brake();
        BoeBot.wait(1000);
        DriveManager.turn(80, 50);
        BoeBot.wait(350);
    }

    public static void bumpTurnRight() {
        DriveManager.brake();
        BoeBot.wait(1000);
        DriveManager.turn(85, -50);
        BoeBot.wait(350);
    }
}
