package models;

public enum roomtypee {

        // These call the constructor below
        SINGLE(500),
        DOUBLE(800),
        TRIPLE(1100),
        SUITE(2500);

        // This field stores the price for each type
        private final double pricePerNight;

        // The constructor sets the price when the enum is initialized
        roomtypee(double pricePerNight) {
            this.pricePerNight = pricePerNight;
        }

        // A getter method to retrieve the price
        public double getPricePerNight() {
            return pricePerNight;
        }
    }

