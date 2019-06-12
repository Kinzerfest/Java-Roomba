import com.maschel.roomba.RoombaJSSC;
import com.maschel.roomba.RoombaJSSCSerial;

import java.io.IOException;

public class Main {
    public static void main(String...args){
        RoombaJSSC roomba = new RoombaJSSCSerial();

// Connect
        while(!roomba.connect("/dev/a/serial/port")){ // Use portList() to get available ports.
            String[] ports = roomba.portList(); // Get available serial port(s) (not mandatory)
            System.out.println("Number of ports: " + ports.length);
            for(String s:ports) System.out.println(s);
        }


// Make roomba ready for communication & control (safe mode)
        roomba.startup();

// Send commands
        roomba.clean(); // Roomba will start cleaning
        roomba.digitLedsAscii('H', 'E', 'Y', '!'); // Shows message on digit leds

// Read sensors (until key is pressed)
        while (true) {
            try {
                if (!(System.in.available() == 0)) break;
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println("Left wheel drop: " + roomba.wheelDropLeft() + " Right wheel drop: " + roomba.wheelDropRight());
            roomba.updateSensors(); // Read sensor values from roomba
            roomba.sleep(50); // Sleep for AT LEAST 50 ms

            // Read sensors
            if (roomba.wall())
                System.out.println("Wall detected!");
        }

// Return to normal (human control) mode
        roomba.stop();

// Close serial connection
        roomba.disconnect();
    }
}
