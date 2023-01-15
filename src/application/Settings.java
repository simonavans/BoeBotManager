package application;

/**
 * Class containing of the settings that may vary from BoeBot to BoeBot.
 *
 * @author Timo and Simon
 */
class Settings {
    final int ROBOTMAIN_NUDGE_FORWARD_TIME;
    final int ULTRASONIC_CLOSE_THRESHOLD;
    final int ULTRASONIC_FAR_THRESHOLD;
    final int ENGINE_NEUTRAL_OFFSET_LEFT;
    final int ENGINE_NEUTRAL_OFFSET_RIGHT;
    final int ENGINE_DRIVE_SPEED;
    final int ENGINE_TURN_SPEED_FORWARD;
    final int ENGINE_TURN_SPEED_BACKWARD;
    final int ENGINE_ADJUST_DIRECTION_SPEED_FORWARD;
    final int ENGINE_ADJUST_DIRECTION_SPEED_BACKWARD;
    final int ENGINE_TURN_TIME;
    final int ENGINE_OBJECT_PLACEMENT_TIME;
    final int IR_RECEIVER_BIT_THRESHOLD;
    final int LINE_SENSOR_THRESHOLD;
    final int LINE_SENSOR_CEILING;
    final int LINE_SENSORS_WAIT_AFTER_DEVIATION;
    final int LINE_SENSORS_WAIT_BEFORE_CROSSROAD;
    final int LINE_SENSORS_WAIT_AFTER_DRIVING_BACKWARDS;
    final int BLUETOOTH_BAUDRATE;
    final int GRIPPER_OPEN_FREQUENCY;
    final int GRIPPER_CLOSE_FREQUENCY;
    final int ULTRASONIC_CLOSE_ECHO_PIN;
    final int ULTRASONIC_CLOSE_TRIGGER_PIN;
    final int ULTRASONIC_FAR_ECHO_PIN;
    final int ULTRASONIC_FAR_TRIGGER_PIN;
    final int LEFT_WHEEL_PIN;
    final int RIGHT_WHEEL_PIN;
    final int GRIPPER_PIN;
    final int IR_RECEIVER_PIN;
    final int BUZZER_PIN;
    final int BUTTON_PIN;
    final int[] LINE_SENSOR_ADC_PINS;

    // todo comment on parameters
    /**
     * Please note that all time units are in milliseconds.
     *
     * @param ROBOTMAIN_NUDGE_FORWARD_TIME when the BoeBot's line sensors are right above a crossroad, this would be
     *                                     the time in milliseconds it takes for the bot to move forward just a bit,
     *                                     so that its body is centered on the crossroad. This setting is exclusively
     *                                     used for when the bot is driving backwards toward a previous crossroad.
     *
     * @param ULTRASONIC_CLOSE_THRESHOLD the threshold value at which the ultrasonic sensor responsible for grabbing
     *                                   objects should notify that it is safe to close the gripper and grab the object.
     *
     * @param ULTRASONIC_FAR_THRESHOLD the threshold value at which the ultrasonic sensor responsible for detecting
     *                                 unknown objects should notify that it sees an object. IMPORTANT: this sensor
     *                                 should only detect objects when there is no crossroad in between the BoeBot's
     *                                 line sensors and the detected object!
     *
     * @param ENGINE_NEUTRAL_OFFSET_LEFT the speed at which the left wheel is completely motionless. This is usually at
     *                                   value 1500, but may differ from wheel to wheel.
     *
     * @param ENGINE_NEUTRAL_OFFSET_RIGHT the speed at which the right wheel is completely motionless. This is usually
     *                                    at value 1500, but may differ from wheel to wheel.
     *
     * @param ENGINE_DRIVE_SPEED the speed at which the engine drives forward and backward. It is also the speed at
     *                             which either one of the wheels turns at. For example, when the BoeBot makes a left
     *                             turn, the right wheel has this speed.
     *
     * @param ENGINE_TURN_SPEED_FORWARD
     *
     * @param ENGINE_TURN_SPEED_BACKWARD the speed at which either one of the wheels turns at. For example, when this is
     *                                set to 50 and the BoeBot makes a left turn, the left wheel is set to have this
     *                                speed, but rotates backwards (so 50 in the opposite direction of the other wheel,
     *                                or -50).
     *
     * @param ENGINE_ADJUST_DIRECTION_SPEED_FORWARD
     *
     * @param ENGINE_ADJUST_DIRECTION_SPEED_BACKWARD
     *
     * @param ENGINE_TURN_TIME the time in milliseconds it takes the engine to make a 90 degree turn, using the forward
     *                         speed and back steer speed variables mentioned in this constructor.
     *
     * @param ENGINE_OBJECT_PLACEMENT_TIME when the BoeBot is positioned on a crossroad and wants to drop an object,
     *                                     it first needs to drive backwards to drop the object in a way that it is
     *                                     right on the crossroad. This is the time in milliseconds it takes for the
     *                                     bot to drop the object.
     *
     * @param IR_RECEIVER_BIT_THRESHOLD the IR receiver interprets ones and zeroes using the pulse length of the
     *                                  received command. The threshold is the border where, below this value, the
     *                                  incoming bit is defined as a zero, and equal to or above this value, it is
     *                                  interpreted as a one.
     *
     * @param LINE_SENSOR_THRESHOLD the threshold value at which the line sensors make the distinction between a white
     *                              surface and a black line. Every measurement below this threshold is interpreted
     *                              as white, and equal to or above this value is interpreted as black.
     *
     * @param LINE_SENSOR_CEILING the ceiling value that, when reached, are perceived as too high and therefore
     *                            incorrect. This value should be higher than the highest value measured when measuring
     *                            a black line.
     *
     * @param LINE_SENSORS_WAIT_AFTER_DEVIATION how long the LineSensors class should wait with defining a deviation as
     *                                          a true deviation. Setting this value too low can lead to detecting a
     *                                          deviation, when in reality the line sensors are detecting part of a
     *                                          crossroad. This may result in the BoeBot losing track of the line.
     *                                          Setting this value too high may result in too infrequent deviation
     *                                          detection, also meaning losing track of the line.
     *
     * @param LINE_SENSORS_WAIT_BEFORE_CROSSROAD when the BoeBot's line sensors detect a crossroad, the BoeBot cannot
     *                                           stop yet. If it would, it would end up slightly before the crossroad,
     *                                           meaning that there would be a greater chance that the bot would lose
     *                                           track of the line. To prevent this, the bot needs to wait before stopping
     *                                           to end up exactly at the crossroad.
     *
     * @param LINE_SENSORS_WAIT_AFTER_DRIVING_BACKWARDS how long the line sensors should wait after enabling when the
     *                                                  BoeBot starts driving backwards. When the bot wants to place
     *                                                  an object, its position is on a crossroad. To prevent detecting
     *                                                  the crossroad that the BoeBot is already on when driving
     *                                                  backwards, the line sensors need a delay long enough so that
     *                                                  the line sensors only activate when the crossroad it was on has
     *                                                  been passed.
     *
     * @param BLUETOOTH_BAUDRATE the baudrate used by the bluetooth module.
     *
     * @param GRIPPER_OPEN_FREQUENCY the frequency at which the gripper opens widely.
     *
     * @param GRIPPER_CLOSE_FREQUENCY the frequency at which the gripper closes.
     *
     * @param ULTRASONIC_CLOSE_ECHO_PIN input pin of the ultrasonic sensor responsible for detecting objects that have
     *                                   to be grabbed.
     *
     * @param ULTRASONIC_CLOSE_TRIGGER_PIN output pin of the ultrasonic sensor responsible for detecting objects that
     *                                    have to be grabbed.
     *
     * @param ULTRASONIC_FAR_ECHO_PIN input pin of the ultrasonic sensor responsible for detecting unknown objects.
     *
     * @param ULTRASONIC_FAR_TRIGGER_PIN output pin of the ultrasonic sensor responsible for detecting unknown objects.
     *
     * @param LEFT_WHEEL_PIN the pin used for controlling the left wheel.
     *
     * @param RIGHT_WHEEL_PIN the pin used for controlling the right wheel.
     *
     * @param GRIPPER_PIN the pin used for controlling the gripper.
     *
     * @param IR_RECEIVER_PIN the pin used for controlling the IR receiver.
     *
     * @param BUZZER_PIN the pin used for controlling the buzzer.
     *
     * @param BUTTON_PIN
     *
     * @param LINE_SENSOR_ADC_PINS the pin used for controlling the line sensors.
     */
    Settings(
            // RobotMain
            int ROBOTMAIN_NUDGE_FORWARD_TIME,
            // Ultrasonic
            int ULTRASONIC_CLOSE_THRESHOLD, int ULTRASONIC_FAR_THRESHOLD,
            // Engine
            int ENGINE_NEUTRAL_OFFSET_LEFT, int ENGINE_NEUTRAL_OFFSET_RIGHT, int ENGINE_DRIVE_SPEED,
            int ENGINE_TURN_SPEED_FORWARD, int ENGINE_TURN_SPEED_BACKWARD,
            int ENGINE_ADJUST_DIRECTION_SPEED_FORWARD, int ENGINE_ADJUST_DIRECTION_SPEED_BACKWARD,
            int ENGINE_TURN_TIME, int ENGINE_OBJECT_PLACEMENT_TIME,
            // IR Receiver
            int IR_RECEIVER_BIT_THRESHOLD,
            // Line Sensor(s)
            int LINE_SENSOR_THRESHOLD, int LINE_SENSOR_CEILING, int LINE_SENSORS_WAIT_AFTER_DEVIATION, int LINE_SENSORS_WAIT_BEFORE_CROSSROAD, int LINE_SENSORS_WAIT_AFTER_DRIVING_BACKWARDS,
            // Bluetooth
            int BLUETOOTH_BAUDRATE,
            // Gripper
            int GRIPPER_OPEN_FREQUENCY, int GRIPPER_CLOSE_FREQUENCY,
            // Pins
            int ULTRASONIC_CLOSE_ECHO_PIN, int ULTRASONIC_CLOSE_TRIGGER_PIN, int ULTRASONIC_FAR_ECHO_PIN,
            int ULTRASONIC_FAR_TRIGGER_PIN, int LEFT_WHEEL_PIN, int RIGHT_WHEEL_PIN, int GRIPPER_PIN,
            int IR_RECEIVER_PIN, int BUZZER_PIN, int BUTTON_PIN, int[] LINE_SENSOR_ADC_PINS
    ) {
        this.ROBOTMAIN_NUDGE_FORWARD_TIME = ROBOTMAIN_NUDGE_FORWARD_TIME;

        this.ULTRASONIC_CLOSE_THRESHOLD = ULTRASONIC_CLOSE_THRESHOLD;
        this.ULTRASONIC_FAR_THRESHOLD = ULTRASONIC_FAR_THRESHOLD;

        this.ENGINE_NEUTRAL_OFFSET_LEFT = ENGINE_NEUTRAL_OFFSET_LEFT;
        this.ENGINE_NEUTRAL_OFFSET_RIGHT = ENGINE_NEUTRAL_OFFSET_RIGHT;
        this.ENGINE_DRIVE_SPEED = ENGINE_DRIVE_SPEED;
        this.ENGINE_TURN_SPEED_FORWARD = ENGINE_TURN_SPEED_FORWARD;
        this.ENGINE_TURN_SPEED_BACKWARD = ENGINE_TURN_SPEED_BACKWARD;
        this.ENGINE_ADJUST_DIRECTION_SPEED_FORWARD = ENGINE_ADJUST_DIRECTION_SPEED_FORWARD;
        this.ENGINE_ADJUST_DIRECTION_SPEED_BACKWARD = ENGINE_ADJUST_DIRECTION_SPEED_BACKWARD;
        this.ENGINE_TURN_TIME = ENGINE_TURN_TIME;
        this.ENGINE_OBJECT_PLACEMENT_TIME = ENGINE_OBJECT_PLACEMENT_TIME;

        this.IR_RECEIVER_BIT_THRESHOLD = IR_RECEIVER_BIT_THRESHOLD;

        this.LINE_SENSOR_THRESHOLD = LINE_SENSOR_THRESHOLD;
        this.LINE_SENSOR_CEILING = LINE_SENSOR_CEILING;
        this.LINE_SENSORS_WAIT_AFTER_DEVIATION = LINE_SENSORS_WAIT_AFTER_DEVIATION;
        this.LINE_SENSORS_WAIT_BEFORE_CROSSROAD = LINE_SENSORS_WAIT_BEFORE_CROSSROAD;
        this.LINE_SENSORS_WAIT_AFTER_DRIVING_BACKWARDS = LINE_SENSORS_WAIT_AFTER_DRIVING_BACKWARDS;

        this.BLUETOOTH_BAUDRATE = BLUETOOTH_BAUDRATE;

        this.GRIPPER_OPEN_FREQUENCY = GRIPPER_OPEN_FREQUENCY;
        this.GRIPPER_CLOSE_FREQUENCY = GRIPPER_CLOSE_FREQUENCY;

        this.ULTRASONIC_CLOSE_ECHO_PIN = ULTRASONIC_CLOSE_ECHO_PIN;
        this.ULTRASONIC_CLOSE_TRIGGER_PIN = ULTRASONIC_CLOSE_TRIGGER_PIN;
        this.ULTRASONIC_FAR_ECHO_PIN = ULTRASONIC_FAR_ECHO_PIN;
        this.ULTRASONIC_FAR_TRIGGER_PIN = ULTRASONIC_FAR_TRIGGER_PIN;
        this.LEFT_WHEEL_PIN = LEFT_WHEEL_PIN;
        this.RIGHT_WHEEL_PIN = RIGHT_WHEEL_PIN;
        this.GRIPPER_PIN = GRIPPER_PIN;
        this.IR_RECEIVER_PIN = IR_RECEIVER_PIN;
        this.BUZZER_PIN = BUZZER_PIN;
        this.BUTTON_PIN = BUTTON_PIN;
        this.LINE_SENSOR_ADC_PINS = LINE_SENSOR_ADC_PINS;
    }
}
