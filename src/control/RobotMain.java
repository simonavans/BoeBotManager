package control;

import TI.BoeBot;
import TI.PinMode;
import hardware.gripper.Gripper;
import hardware.sensors.Antenna;
import hardware.sensors.LineDetector;

public class RobotMain {

    public static void main(String[] args) {

        boolean isToggled = true;
        boolean state = true;
        Antenna antennaL = new Antenna((byte)7);
        Antenna antennaR = new Antenna((byte)8);
        LineDetector lineRight = new LineDetector((byte)0);
        LineDetector lineMiddle = new LineDetector((byte)1);
        LineDetector lineLeft = new LineDetector((byte)2);
        BoeBot.setMode(0, PinMode.Input);

        Gripper.open();

        while (true) {
//            DriveManager.drive(20);
//            System.out.print("left:" + lineLeft.getSensorValue());
//            System.out.print(" middle:" + lineMiddle.getSensorValue());
//            System.out.println(" right:" + lineRight.getSensorValue());
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
