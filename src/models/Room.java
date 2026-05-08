package models;
import database.HotelDatabase;
import java.util.ArrayList;

public class Room {
    private int roomNumber;
    private RoomType type;
    private ArrayList<Amenity> amenities = new ArrayList<>();

    public Room(int roomNumber, RoomType type) {
        this.roomNumber = roomNumber;
        this.type = type;
        this.amenities = new ArrayList<>(getDefaultAmenities(type));
    }
    //Returns the default amenities list for the given room type.
    //Falls back to suite defaults for unknown types.
    private static ArrayList<Amenity> getDefaultAmenities(RoomType type) {
        String typeName = type.getTypeName();
        if (typeName.equalsIgnoreCase("Single")) {
            return HotelDatabase.singleDefaults;
        } else if (typeName.equalsIgnoreCase("Double")) {
            return HotelDatabase.doubleDefaults;
        } else {
            return HotelDatabase.suiteDefaults;
        }
    }
    public int getRoomNumber() { return this.roomNumber; }
    public RoomType getType() { return this.type; }
    public ArrayList<Amenity> getAmenities() { return this.amenities; }

    public void addAmenity(Amenity amenity) {
        this.amenities.add(amenity);
    }
    public void setType(RoomType type) {
        this.type = type;
    }
    //Calculates the total price of all amenities attached to this room.
    public double totalAmenitiesPrice() {
        double sum = 0;
        for (Amenity amenity : amenities) {
            sum += amenity.getPrice();
        }
        return sum;
    }
    //Calculates the total room price per night (base price + amenities).
    public double totalRoomPricePerOneNight() {
        return totalAmenitiesPrice() + type.getBasePrice();
    }
    public void printAmenities() {
        for (int i = 0; i < amenities.size(); i++) {System.out.println((i + 1) + " " + amenities.get(i).getName());}
    }
    @Override
    public String toString() {
        return "Room number: " + roomNumber
                + " Room Type: " + type
                + " Amenities in the room: " + amenities;
    }
    public void addAmenities() {
        java.util.Scanner input = new java.util.Scanner(System.in);
        System.out.println("---All Amenities---");
        for (int i = 0; i < HotelDatabase.allAmenities.size(); i++) {
            System.out.println(i + 1 + "\t" + HotelDatabase.allAmenities.get(i));
        }
        System.out.println("Enter the name of the amenitiy you want to add to this room: ");
        String amenity = input.nextLine().trim();
        for (int i = 0; i < HotelDatabase.allAmenities.size(); i++) {
            if (HotelDatabase.allAmenities.get(i).getName().equalsIgnoreCase(amenity)) {
                for (int j = 0; j < amenities.size(); j++) {
                    if (amenities.get(j).getName().equalsIgnoreCase(amenity)) {
                        System.out.println("this amenity is already in the room ");
                        return;
                    }
                }
                amenities.add(HotelDatabase.allAmenities.get(i));
                HotelDatabase.addAmenityToRoom(this, HotelDatabase.allAmenities.get(i));
                System.out.println(HotelDatabase.allAmenities.get(i).getName()
                        + " is successfuly added to Room " + this.roomNumber);
                return;
            }
        }
    }
}
