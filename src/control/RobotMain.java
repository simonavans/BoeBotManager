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
    private static Scanner reader;

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
        reader = new Scanner(System.in);

        System.out.println("🤖💬 Bleep bloop. Hello! To operate me, " +
                "please select either option:\n" +
                "auto\t\t:\tIn this mode, I will drive regardless of " +
                "any user input.\n" +
                "interface\t:\tIn this mode, I will ask the user for operation.\n" +
                " > ");

        String command = reader.nextLine();

        while (true) {
            if (command.equals("auto")) {
                System.out.println("🤖💬 Running auto mode!");
                runLoop();
                break;
            } else if (command.equals("interface")) {
                System.out.println("🤖💬 Running interface mode!");
                runUI();
                break;
            } else {
                System.out.println("🤖💬 Please input either 'auto' or 'interface'!");
            }
        }
    }

    private static void printMenu() {
        System.out.println(
                "Options (input command):\n" +
                        "quit\t:\tQuits this interface and turns me off\n" +
                        "fwd\t\t:\tMoves me forward until I collide with an object\n" +
                        "tlf\t\t:\tTurns me 90 degrees to the left\n" +
                        "trg\t\t:\tTurns me 90 degrees to the right\n" +
                        "stop\t:\tStops me until other commands are given\n" +
                        "grab\t:\tLets me grab stuff with my gripper\n" +
                        "rls\t\t:\tLets me release objects I was holding before\n" +
                        "prnt\t:\tLets me print this help menu"
        );
    }

    private static void runUI() {
        String command = "";
        while(true) {
            System.out.print(" > ");
            command = reader.nextLine();

            if (command.equals("quit")) {
                break;
            } else if (command.equals("fwd")) {
                System.out.println("🤖💬 Moving forward!");
                DriveManager.drive(20);

            } else if (command.equals("tlf")) {
                System.out.println("🤖💬 Turning left!");
                ActionManager.bumpTurnLeft();

            } else if (command.equals("trg")) {
                System.out.println("🤖💬 Turning right!");
                ActionManager.bumpTurnRight();

            } else if (command.equals("stop")) {
                System.out.println("🤖💬 Stopping!");
                DriveManager.brake();

            } else if (command.equals("grab")) {
                System.out.println("🤖💬 Grabbing!");
                Gripper.close();

            } else if (command.equals("rls")) {
                System.out.println("🤖💬 Releasing!");
                Gripper.open();

            } else if (command.equals("prnt")) {
                printMenu();

            } else {
                System.out.println("🤖💬 I don't know that command!");
            }
        }
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
