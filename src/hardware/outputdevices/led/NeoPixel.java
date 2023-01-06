package hardware.outputdevices.led;

import TI.BoeBot;
import TI.Timer;
import hardware.PinRegistry;
import link.Updatable;

import java.awt.*;

/**
 * Class for a neopixel that is integrated in the BoeBot.
 *
 * @author Simon de Cock
 */
public class NeoPixel implements Updatable {
    private final int ledNumber;
    private Color color;

    private Timer blinkMethodTimer;
    private Timer blinkTimer;
    private boolean blinkState = false;
    private int blinkMilliseconds = 0;
    private int blinkTimes = 0;

    public NeoPixel(int ledNumber, Color color) {
        if (ledNumber < 0 || ledNumber > 5) {
            throw new IllegalArgumentException("Parameter ledNumber in Neopixel was not between 0 and 5 (inclusive) but was: " + ledNumber);
        }

        PinRegistry.registerPins(new int[]{ledNumber}, new String[]{"neopixel"});
        this.ledNumber = ledNumber;
        this.color = color;

        BoeBot.rgbSet(ledNumber, color);
        BoeBot.rgbShow();
    }

    public NeoPixel(int ledNumber) {
        if (ledNumber < 0 || ledNumber > 5) {
            throw new IllegalArgumentException("Parameter ledNumber in Neopixel was not between 0 and 5 (inclusive) but was: " + ledNumber);
        }

        PinRegistry.registerPins(new int[]{ledNumber}, new String[]{"neopixel"});
        this.ledNumber = ledNumber;
        this.color = new Color(0);
    }

    @Override
    public void update() {
        if (blinkMethodTimer == null) return;

        if (blinkMethodTimer.timeout()) {
            blinkMethodTimer = null;
            return;
        }

        if (blinkTimer == null) {
            blinkTimer = new Timer(blinkMilliseconds / (2 * blinkTimes));
            blinkTimer.mark();
            return;
        }

        if (blinkTimer.timeout()) {
            blinkState = !blinkState;

            if (blinkState) {
                this.turnOn();
            } else {
                this.turnOff();
            }
        }
    }

    public void turnOn(Color newColor) {
        this.color = newColor;
        turnOn();
    }

    private void turnOn() {
        BoeBot.rgbSet(this.ledNumber, this.color);
        BoeBot.rgbShow();
    }

    public void turnOff() {
        BoeBot.rgbSet(this.ledNumber, Color.BLACK);
        BoeBot.rgbShow();
    }

    /**
     * TODO comment
     *
     * @param color
     * @param milliseconds How long a full cycle (on and off time combined) should last.
     * @param times
     * @param highPriority
     * @author Simon de Cock
     */
    public void blink(Color color, int milliseconds, int times, boolean highPriority) {
        if (milliseconds <= 0) {
            throw new IllegalArgumentException("Blinking cannot take 0 milliseconds or less");
        }

        if (blinkMethodTimer == null || highPriority) {
            this.color = color;
            blinkMethodTimer = new Timer(milliseconds);
            blinkMethodTimer.mark();
            blinkMilliseconds = milliseconds;
            blinkTimes = times;

            if (this.color.getRGB() == 0) {
                System.out.println("Warning: neopixel on led number " + ledNumber + " is blinking, but its color is black!");
            }
        }
    }

    public void resetBlink() {
        blinkMethodTimer = null;
        this.turnOff();
    }
}