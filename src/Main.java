import com.diozero.api.DigitalOutputDevice;
import com.diozero.api.GpioPullUpDown;
import com.diozero.devices.Button;
import com.diozero.devices.HCSR04;
import com.diozero.devices.LED;
import com.diozero.util.SleepUtil;
import com.maschel.roomba.RoombaJSSC;
import com.maschel.roomba.RoombaJSSCSerial;
import com.maschel.roomba.song.RoombaNote;
import com.maschel.roomba.song.RoombaNoteDuration;
import com.maschel.roomba.song.RoombaSongNote;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

public class Main {
    public static void main(String... args) {
        /*
        System.out.println("LED running");
        DigitalOutputDevice a = new DigitalOutputDevice(4);
        for (; ; ) {
            a.toggle();
            SleepUtil.sleepSeconds(0.2);
        }

         */

        tankDrive();
    }


    private static void tankDrive() {
        RoombaJSSC r = new RoombaJSSCSerial();

        //Start a server for Driver Station to connect to
        NetworkTableInstance.getDefault().startServer();
        NetworkTable table = NetworkTableInstance.getDefault().getTable("Roomba");

        NetworkTableEntry leftMotor = table.getEntry("leftMotor"),
                rightMotor = table.getEntry("rightMotor"),
                leftRate = table.getEntry("leftRate"),
                rightRate = table.getEntry("rightRate"),
                motors = table.getEntry("motors");

        leftMotor.setDouble(0.0);
        rightMotor.setDouble(0.0);

        // Connect to roomba
        while (!r.connect("/dev/ttyUSB0")) { // Use portList() to get available ports.
            String[] ports = r.portList(); // Get available serial port(s) (not mandatory)
            System.out.println("Number of ports: " + ports.length);
            for (String s : ports) System.out.println(s);
        }


        // Make roomba ready for communication & control (safe mode)
        r.startup();
        r.sleep(500);
        r.digitLedsAscii('R','U','S','T');

        r.song(0, new RoombaSongNote[]{
                new RoombaSongNote(RoombaNote.E2, RoombaNoteDuration.EightNote),
                new RoombaSongNote(RoombaNote.D2Sharp, RoombaNoteDuration.EightNote),
                new RoombaSongNote(RoombaNote.E2, RoombaNoteDuration.EightNote),
                new RoombaSongNote(RoombaNote.D2Sharp, RoombaNoteDuration.EightNote),
                new RoombaSongNote(RoombaNote.E2, RoombaNoteDuration.EightNote),
                new RoombaSongNote(RoombaNote.B1, RoombaNoteDuration.EightNote),
                new RoombaSongNote(RoombaNote.D2, RoombaNoteDuration.EightNote),
                new RoombaSongNote(RoombaNote.C2, RoombaNoteDuration.EightNote),
                new RoombaSongNote(RoombaNote.A1, RoombaNoteDuration.QuarterNote),
                new RoombaSongNote(RoombaNote.Pause, RoombaNoteDuration.EightNote),
                new RoombaSongNote(RoombaNote.C1, RoombaNoteDuration.EightNote),
                new RoombaSongNote(RoombaNote.E1, RoombaNoteDuration.EightNote),
                new RoombaSongNote(RoombaNote.A1, RoombaNoteDuration.EightNote),
                new RoombaSongNote(RoombaNote.B1, RoombaNoteDuration.QuarterNote),
                new RoombaSongNote(RoombaNote.Pause, RoombaNoteDuration.EightNote),
                new RoombaSongNote(RoombaNote.E1, RoombaNoteDuration.EightNote)}, 125);

        r.play(0);

        Derivative leftSpeed = new Derivative(r.encoderCountsLeft()),
                rightSpeed = new Derivative(r.encoderCountsRight());

// Read sensors (until key is pressed)
        while (!r.buttonClockPressed()) {
            r.sleep(50);
            r.updateSensors();
            r.drivePWM(convert(rightMotor.getDouble(0.0)), convert(leftMotor.getDouble(0.0)));
            leftRate.setDouble(leftSpeed.calculate(r.encoderCountsLeft()));
            rightRate.setDouble(rightSpeed.calculate(r.encoderCountsRight()));


            r.motors(
                    motors.getBooleanArray(new boolean[5])[0],
                    motors.getBooleanArray(new boolean[5])[1],
                    motors.getBooleanArray(new boolean[5])[2],
                    motors.getBooleanArray(new boolean[5])[3],
                    motors.getBooleanArray(new boolean[5])[4]
            );


            /*
            if(roomba.bumpLeft()) System.out.println("Bumped left");
            if(roomba.bumpRight()) System.out.println("Bumped right");
            if(roomba.wall()) System.out.println("Hit wall");
            if(roomba.cliffLeft()) System.out.println("Cliff left");
            if(roomba.cliffRight()) System.out.println("Cliff right");
            if(roomba.cliffFrontLeft()) System.out.println("Cliff front left");
            if(roomba.cliffFrontRight()) System.out.println("Cliff front right");
            System.out.println("Encoder left: " + roomba.encoderCountsLeft() + " Encoder right: " + roomba.encoderCountsRight());
            */
        }

// Return to normal (human control) mode
        r.stop();

// Close serial connection
        r.disconnect();
    }

    private static int convert(double value) {
        return Math.max(-100, Math.min((int) (value * 100), 100));
    }
}

class Derivative {
    private double prevValue;

    Derivative(double startingValue) {
        prevValue = startingValue;
    }

    double calculate(double newValue, double dt) {
        double pv = prevValue;
        prevValue = newValue;
        return ((newValue - pv) / dt);
    }

    double calculate(double newValue) {
        return calculate(newValue, 0.05);
    }
}
