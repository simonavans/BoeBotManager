package hardware.outputdevices.led;

import TI.BoeBot;
import TI.Timer;
import hardware.PinRegistry;

import java.awt.*;

/**
 * Class for a neopixel that is integrated in the BoeBot.
 * @author Simon de Cock
 */
public class NeoPixel implements LED {
    private final int ledNumber;
    private Color color;

    private Timer blinkMethodTimer;
    private Timer blinkTimer;
    private boolean blinkState = false;

    public NeoPixel(int ledNumber, Color color) {
        PinRegistry.registerPins(new int[]{ledNumber}, new String[]{"neopixel"});
        this.ledNumber = ledNumber;
        this.color = color;

        BoeBot.rgbSet(ledNumber, color);
        BoeBot.rgbShow();
    }

    public NeoPixel(int ledNumber) {
        PinRegistry.registerPins(new int[]{ledNumber}, new String[]{"neopixel"});
        this.ledNumber = ledNumber;
        this.color = new Color(0);
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
     * Makes the NeoPixel blink asynchronously using the Timer class. In other words, this
     * method does not block the run method in RobotMain when called. Rather, this method
     * relies on being continuously called in the run method to check when it should turn
     * on or off.
     * @param milliseconds How long a full cycle (on and off time combined) should last.
     * @author Simon de Cock
     */
    @Override
    public void updateBlink(int milliseconds) {
        if (blinkMethodTimer == null) {
            blinkMethodTimer = new Timer(milliseconds);
        }

        if (!blinkMethodTimer.timeout()) {
            if (blinkTimer == null) {
                blinkTimer = new Timer(milliseconds / 2);
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
        }
    }
}