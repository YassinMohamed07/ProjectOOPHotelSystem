package models;

public class RoomType {
    public String typeName;
    public double basePrice;

    public RoomType(String name, double price) {
        this.typeName = name;
        this.basePrice = price;
    }
}