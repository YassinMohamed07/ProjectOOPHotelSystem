package models;

public class Amenity {
    private String name;
    private double price; // dummy field added (for testing)

    public Amenity(String name) {
        this.name = name;
        this.price = 50.0; // default dummy price
    }

    // Constructor with price dummy (for testing)
    public Amenity(String name, double price) {
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    // Dummy returns amenity price for Invoice calculation (for testing)
    public double getPrice() {
        return price;
    }
}
