package application;

import java.util.Arrays;

public class NavigationManager {
    private static Integer[] destination = new Integer[]{null, null};
    private static int[] location = new int[]{0, 0};
    private static String direction = "East";

    public static String nextCommandAndUpdate() {
        System.out.println(Arrays.toString(location));
        if (direction.equals("East")) {
            location[0]++;
            if (location[0] < destination[0]) {
                return "";
            } else if(location[0] == destination[0]) {
                direction = "North";
                return "turn";
            }
        } else if (direction.equals("North")) {
            location[1]++;
            if (location[1] < destination[1]) {
                return "";
            } else if(location[1] == destination[1]) {
                return "brake";
            }
        }
        return "brake";
    }

    public static void setX(Integer x) {
        destination[0] = x;
    }

    public static void setY(Integer y) {
        destination[1] = y;
    }

    public static void resetDestination() {
        location = new int[]{0, 0};
        destination = new Integer[]{null, null};
        direction = "East";
    }

    public static Integer[] getDestination() {
        return destination;
    }

    public static boolean isTurnNext() {
        return direction.equals("East") && location[0] + 1 == destination[0];
    }
}
