import database.HotelDatabase;
import models.Room;
import models.RoomType;
public class Main {
    public static void main(String[] args) {
        // 1. Boot up the database (This MUST be the first line of the whole project)
        HotelDatabase.initialize();

        // 2. Abdallah's QA System Test
        System.out.println("\n--- PHASE 1 SYSTEM TEST ---");

        // Test if the Guests list works and test the getUsername() getter
        System.out.println("Total Guests in memory: " + HotelDatabase.guests.size());
        System.out.println("Dummy Guest Account: " + HotelDatabase.guests.get(0).getUsername());

        // Test if the Rooms list works and test the nested getters
        System.out.println("Total Rooms in memory: " + HotelDatabase.rooms.size());

        Room testRoom = HotelDatabase.rooms.get(0);
        System.out.println("Room " + testRoom.getRoomNumber() + " is a " + testRoom.getType().getTypeName() + " room.");
        System.out.println("It costs $" + testRoom.getType().getBasePrice() + " per night.");

    }
}