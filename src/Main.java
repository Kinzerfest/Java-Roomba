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
        RoombaJSSC roomba = new RoombaJSSCSerial();

        //Start a server for Driver Station to connect to
        NetworkTableInstance.getDefault().startServer();
        NetworkTable table = NetworkTableInstance.getDefault().getTable("Roomba");

        NetworkTableEntry leftSpeed = table.getEntry("leftSpeed"),
                rightSpeed = table.getEntry("rightSpeed");

        leftSpeed.setDouble(0.0);
        rightSpeed.setDouble(0.0);

        // Connect to roomba
        while (!roomba.connect("/dev/ttyUSB0")) { // Use portList() to get available ports.
            String[] ports = roomba.portList(); // Get available serial port(s) (not mandatory)
            System.out.println("Number of ports: " + ports.length);
            for (String s : ports) System.out.println(s);
        }

        // Make roomba ready for communication & control (safe mode)
        roomba.startup();
        roomba.sleep(500);
        roomba.digitLedsAscii('G','N','U','C');

        roomba.song(0, new RoombaSongNote[]{
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

        roomba.play(0);

// Read sensors (until key is pressed)
        while (!roomba.buttonClockPressed()) {
            roomba.sleep(100);
            roomba.updateSensors();
            roomba.drivePWM((int)(rightSpeed.getDouble(0.0)*100), (int)(leftSpeed.getDouble(0.0)*100));
            System.out.println("leftSpeed: " + (int)(leftSpeed.getDouble(0.0)*100) + " rightSpeed: " + (int)(rightSpeed.getDouble(0.0)*100));
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
        roomba.stop();

// Close serial connection
        roomba.disconnect();

    }
}
