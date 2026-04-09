package models;

public class RoomType {
    private String typeName;
    private double basePrice;

    public RoomType(String name, double price) {
        this.typeName = name;
        this.basePrice = price;
    }

    public String getTypeName() { return this.typeName; }
    public double getBasePrice() { return this.basePrice; }
}