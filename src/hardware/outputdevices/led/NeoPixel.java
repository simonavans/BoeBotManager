package hardware.outputdevices.led;

import TI.BoeBot;
import TI.Timer;
import hardware.PinRegistry;
import link.Updatable;

import java.awt.*;

/**
 * Class for a neopixel that is integrated in the BoeBot.
 */
public class NeoPixel implements Updatable {
    private final int ledNumber;
    private Color color;

    // The Timer that indicates how long blinking should last
    private Timer blinkMethodTimer;

    // The Timer that indicates the time it takes to turn a neopixel on or off while blinking
    private Timer blinkTimer;

    // Whether the neopixel should now be turned on or off depending on the blink time
    private boolean blinkState = false;

    // The time it takes to turn a neopixel on or off while blinking
    private int blinkMilliseconds = 0;

    /**
     * @param ledNumber the ID of the neopixel represented by a number between 0 and 5, inclusive. Pixels 0, 1 and 2
     *                  are located on the back of the BoeBot, while pixels 3, 4 and 5 are on the front.
     * @param color the start color of the neopixel
     *
     * @author Simon
     */
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

    /**
     * @param ledNumber the ID of the neopixel represented by a number between 0 and 5, inclusive. Pixels 0, 1 and 2
     *                  are located on the back of the BoeBot, while pixels 3, 4 and 5 are on the front.
     *
     * @author Simon
     */
    public NeoPixel(int ledNumber) {
        if (ledNumber < 0 || ledNumber > 5) {
            throw new IllegalArgumentException("Parameter ledNumber in Neopixel was not between 0 and 5 (inclusive) but was: " + ledNumber);
        }

        PinRegistry.registerPins(new int[]{ledNumber}, new String[]{"neopixel"});
        this.ledNumber = ledNumber;
        this.color = new Color(0);
    }

    /**
     * This method makes sure that the neopixel blinks at the correct time.
     *
     * @author Simon
     */
    @Override
    public void update() {
        // If nothing called the blink method, do nothing
        if (blinkMethodTimer == null) return;

        // If the blinking should stop
        if (blinkMethodTimer.timeout()) {
            blinkMethodTimer = null;
            return;
        }

        // Create a new blinkTimer. Every time this timer times out, the
        // blinkState will invert its value, and depending on this new
        // value, the neopixel will be turned on or off.
        if (blinkTimer == null) {
            blinkTimer = new Timer(blinkMilliseconds);
            blinkTimer.mark();
            return;
        }

        if (blinkTimer.timeout()) {
            // Invert the value
            blinkState = !blinkState;

            if (blinkState) {
                this.turnOn();
            } else {
                this.turnOff();
            }
        }
    }

    /**
     * Turns on the neopixel and assigns a new color to it
     * @param newColor the color for the neopixel to display
     *
     * @author Simon
     */
    public void turnOn(Color newColor) {
        this.color = newColor;
        turnOn();
    }

    /**
     * Turns on the neopixel without assigning a new color.
     * It will simply display the old color that it was
     * assigned before.
     *
     * @author Simon
     */
    private void turnOn() {
        BoeBot.rgbSet(this.ledNumber, this.color);
        BoeBot.rgbShow();
    }

    /**
     * Turns off the neopixel. This is equivalent to setting
     * the color to black and updating the neopixel.
     *
     * @author Simon
     */
    public void turnOff() {
        BoeBot.rgbSet(this.ledNumber, Color.BLACK);
        BoeBot.rgbShow();
    }

    /**
     * Initializes the neopixel's blinking
     * @param color which color to blink with
     * @param milliseconds how long a full cycle (on and off time combined) should last.
     * @param times the amount of times the neopixel will blink
     * @param highPriority if this neopixel is already blinking, true will always override
     *                     the current blink. Used for the emergency lights.
     *
     * @author Simon
     */
    public void blink(Color color, int milliseconds, int times, boolean highPriority) {
        if (milliseconds <= 0) {
            throw new IllegalArgumentException("Blinking cannot take 0 milliseconds or less");
        }

        if (blinkMethodTimer == null || highPriority) {
            this.color = color;
            blinkMethodTimer = new Timer(milliseconds);
            blinkMethodTimer.mark();
            blinkMilliseconds = milliseconds / (times * 2);

            if (this.color.getRGB() == 0) {
                System.out.println("Warning: neopixel on led number " + ledNumber + " is blinking, but its color is black!");
            }
        }
    }

    /**
     * Turns off blinking for this neopixel.
     *
     * @author Simon
     */
    public void resetBlink() {
        blinkMethodTimer = null;
        this.turnOff();
    }
}