package models;

public class RoomType {
    private String typeName;
    private double basePrice;
    private Roomtypee type;

    public RoomType(String name, double price) {
        this.typeName = name;
        this.basePrice = price;
        this.type = mapNameToEnum(name);
    }
    public RoomType(Roomtypee typee) {
        this.type = typee;
    }

    //Maps a room type name string to the corresponding Roomtypee enum value.
    //Returns null if the name doesn't match any known type.

    private static Roomtypee mapNameToEnum(String name) {
        if (name.equalsIgnoreCase("single")) {
            return Roomtypee.SINGLE;
        } else if (name.equalsIgnoreCase("double")) {
            return Roomtypee.DOUBLE;
        } else if (name.equalsIgnoreCase("suite")) {
            return Roomtypee.SUITE;
        }
        return null;
    }
    public String getTypeName() { return this.typeName; }
    public double getBasePrice() { return this.basePrice; }
    public Roomtypee getRoomType() { return this.type; }
    public void setBasePrice(double basePrice) {
        this.basePrice = basePrice;
    }
    @Override
    public String toString() {
        return "Type name: " + typeName + " basePrice: " + basePrice;
    }
}