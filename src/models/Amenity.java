package models;

//Represents a hotel amenity with a name and price.

public class Amenity {
    private final String name;
    private double price;

    public Amenity(String name) {
        this.name = name.toUpperCase();
        this.price = 50.0; // default price
    }
    public Amenity(String name, double price) {
        this.name = name;
        this.price = price;
    }
    public void setPrice(double price) { this.price = price; }
    public String getName() { return name; }
    public double getPrice() { return price; }

    @Override
    public String toString() {
        return " name: " + name + " its price: " + price;
    }
}
