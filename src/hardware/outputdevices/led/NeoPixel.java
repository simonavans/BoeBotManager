package hardware.outputdevices.led;

import TI.BoeBot;
import TI.Timer;
import hardware.outputdevices.Engine;
import link.Updatable;

import java.awt.*;

/**
 * Class for a neopixel that is integrated in the BoeBot.
 * @author Simon de Cock
 */
public class NeoPixel implements Updatable, LED {
    private final int ledNumber;
    private Color color;
    private Timer blinkMethodTimer;
    private Timer blinkTimer;
    private boolean blinkState = false;
    private boolean blinkMode = false;
    private int blinkMilliseconds = 0;
    protected static boolean driveBack = true;
    protected static Timer Blinker = new Timer(500);

    public NeoPixel(int ledNumber, Color color) {
        if (ledNumber < 0 || ledNumber > 6) {
            throw new IllegalArgumentException(
                    "Parameter ledNumber in NeoPixel was not between 0 and 5 (inclusive), but was: " + ledNumber
            );
        }

        if (color == null) {
            color = new Color(0);
        }

        this.ledNumber = ledNumber;
        this.color = color;
        BoeBot.rgbSet(ledNumber, color);
        BoeBot.rgbShow();
    }

    @Override
    public void update() {
        if (blinkMode) {
            if (blinkMethodTimer == null) {
                blinkMethodTimer = new Timer(blinkMilliseconds);
            }

            if (!blinkMethodTimer.timeout()) {
                if (blinkTimer == null) {
                    blinkTimer = new Timer(blinkMilliseconds / 2);
                }

                if (blinkTimer.timeout()) {
                    blinkState = !blinkState;

                    if (blinkState) {
                        turnOn();
                    } else {
                        turnOff();
                    }
                }
            } else {
                blinkMethodTimer = null;
                blinkMode = false;
            }
        }
    }

    public Color getColor() {
        return color;
    }

    public void setColorAndTurnOn(Color newColor) {
        color = newColor;
        turnOn();
    }

    @Override
    public void turnOn() {
        BoeBot.rgbSet(ledNumber, color);
        BoeBot.rgbShow();
    }

    @Override
    public void turnOff() {
        BoeBot.rgbSet(ledNumber, Color.BLACK);
        BoeBot.rgbShow();
    }

    /**
     * TODO comment
     * @param milliseconds How long a full cycle (on and off time combined) should last.
     * @author Simon de Cock
     */
    @Override
    public void updateBlink(int milliseconds) {
        if (milliseconds <= 0) {
            throw new IllegalArgumentException("Blinking cannot take 0 milliseconds or less");
        }

        blinkMode = true;
        blinkMilliseconds = milliseconds;


        if (color.getRGB() == 0) {
            System.out.println("Warning: neopixel on LED number "+ledNumber+" is blinking, but its color is black!");
        }
    }
    public static void rightBlinker(){
        BoeBot.rgbSet(0, Color.BLACK);
        BoeBot.rgbShow();
        if (Blinker.timeout()){
            BoeBot.rgbSet(0, Color.ORANGE);
            BoeBot.rgbShow();
        }

    }

    public static void leftBlinker(){
        BoeBot.rgbSet(5, Color.BLACK);
        BoeBot.rgbShow();
        if (Blinker.timeout()){
            BoeBot.rgbSet(5, Color.ORANGE);
            BoeBot.rgbShow();
        }

    }
    public static void backLights(){
        BoeBot.rgbSet(0, Color.WHITE);
        BoeBot.rgbSet(5, Color.WHITE);
        BoeBot.rgbShow();
    }
    public static void brakeLights(){
        BoeBot.rgbSet(0, Color.RED);
        BoeBot.rgbSet(5, Color.RED);
        BoeBot.rgbShow();
    }

}
