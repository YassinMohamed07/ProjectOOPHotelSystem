package models;
import java.util.ArrayList;

public class Room {
    public int roomNumber;
    public RoomType type;
    public ArrayList<Amenity> amenities = new ArrayList<>();

    public Room(int roomNumber, RoomType type) {
        this.roomNumber = roomNumber;
        this.type = type;
    }
}