package link;

import application.RobotMain;
import hardware.PinRegistry;

public abstract class Updatable {
    protected final byte pinNumber;
    protected final RobotMain callback;

    /**
     * Constructor used for single-pin hardware (e.g. gripper and antenna sensor).
     * @param pinNumber the pin to map the hardware to.
     * @param isInputPin whether the hardware requires the pin to be an input or an output.
     * @param callback which class the Updatable should call back to (usually the class
     *                 which initialized the constructor)
     */
    public Updatable(byte pinNumber, boolean isInputPin, RobotMain callback) {
        PinRegistry.registerPin(pinNumber, isInputPin);
        this.pinNumber = pinNumber;
        this.callback = callback;
    }

    /**
     * Constructor used for multi-pin hardware (e.g. ultrasonic and engine)
     * @param firstPinNumber the first pin to map the hardware to.
     * @param isFirstPinInput whether the hardware requires the first pin to be an input or an output.
     * @param secondPinNumber the second pin to map the hardware to.
     * @param isSecondPinInput whether the hardware requires the second pin to be an input or an output.
     * @param callback which class the Updatable should call back to (usually the class
     *                 which initialized the constructor)
     */
    public Updatable(byte firstPinNumber, boolean isFirstPinInput, byte secondPinNumber, boolean isSecondPinInput, RobotMain callback) {
        PinRegistry.registerPin(firstPinNumber, isFirstPinInput);
        PinRegistry.registerPin(secondPinNumber, isSecondPinInput);

        /* Note: when a class calls this constructor, it should not access the
        pinNumber variable. These classes should have their own instance
        variables set for their pin numbers, because it makes using them easier
        (e.g. a class can set pin numbers as pinLeft and pinRight instead of keeping
        them as pinNumber and secondPinNumber. Here, pinNumber is set as to avoid
        NullPointerExceptions. */
        this.pinNumber = firstPinNumber;
        this.callback = callback;
    }

    /**
     * The method used for letting the hardware execute its code once.
     */
    public abstract void update();
}
