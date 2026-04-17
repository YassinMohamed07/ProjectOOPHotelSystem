package models;

import database.HotelDatabase;

public class Amenity {
    private final String name;
    private double price; // dummy field added (for testing)

    public Amenity(String name) {

        this.name = name.toUpperCase();
        this.price = 50.0; // default dummy price
    }

    // Constructor with price dummy (for testing)
    public Amenity(String name, double price) {

        this.name = name;
        this.price = price;
    }

    public void setPrice(double price) { this.price = price; }

    public String getName() {
        return name;
    }

    // Dummy returns amenity price for Invoice calculation (for testing)
    public double getPrice() {
        return price;
    }
    @Override
    public String toString(){
        return " name: "+name+" its price: "+price;

    }
}
