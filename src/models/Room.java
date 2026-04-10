package models;
import java.util.ArrayList;

public class Room {
    private int roomNumber;
    private RoomType type;
    private ArrayList<Amenity> amenities;

    public Room(int roomNumber, RoomType type) {
        this.roomNumber = roomNumber;
        this.type = type;
        this.amenities = new ArrayList<>();
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
    public double totalAmenitiesPrice(){
        double sum=0;
        int length= amenities.size();
        for(int i=0;i<length;i++){

            sum+=amenities.get(i).getPrice();

        } return sum;
    }
    public double totalRoomPricePerOneNight(){
        return totalAmenitiesPrice() + type.getBasePrice();
    }
    public void printAmenities(){
        int length= amenities.size();

        for(int i=0;i<length;i++){

            System.out.println((i+1)+" "+amenities.get(i).getName());
        }

    }
}
