import com.maschel.roomba.RoombaJSSC;
import com.maschel.roomba.RoombaJSSCSerial;
import com.maschel.roomba.song.RoombaNote;
import com.maschel.roomba.song.RoombaNoteDuration;
import com.maschel.roomba.song.RoombaSongNote;
import edu.wpi.first.networktables.NetworkTable;

import java.io.IOException;

public class Main {
    public static void main(String... args) {
        RoombaJSSC roomba = new RoombaJSSCSerial();



// Connect
        while (!roomba.connect("/dev/ttyUSB0")) { // Use portList() to get available ports.
            String[] ports = roomba.portList(); // Get available serial port(s) (not mandatory)
            System.out.println("Number of ports: " + ports.length);
            for (String s : ports) System.out.println(s);
        }

// Make roomba ready for communication & control (safe mode)
        roomba.startup();
        roomba.fullMode();
        roomba.sleep(500);

        roomba.digitLedsAscii('G','N','U','C');

        RoombaSongNote[] notes = {
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
                        new RoombaSongNote(RoombaNote.E1, RoombaNoteDuration.EightNote)
        };
// Save to song number 0, tempo (in BPM) 125
        roomba.song(0, notes, 125);
// Play song 0
        roomba.play(0);

// Read sensors (until key is pressed)
        while (!roomba.buttonClockPressed()) {
            roomba.sleep(100);
            roomba.updateSensors();

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


            if (roomba.buttonCleanPressed())roomba.drivePWM(80, 80);
             else if(roomba.buttonDayPressed()) roomba.drivePWM(60,-60);
             else if(roomba.buttonMinutePressed()) roomba.drivePWM(-60, 60);
             else if(roomba.buttonSchedulePressed()) roomba.play(0);
             else roomba.drivePWM(0, 0);

        }

// Return to normal (human control) mode
        roomba.stop();

// Close serial connection
        roomba.disconnect();

    }
}
