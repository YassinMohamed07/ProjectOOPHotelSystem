package database;

import models.*;
import java.util.ArrayList;
import java.time.LocalDate;
import exceptions.WeakPwordException;
import exceptions.InvalidDateException;



public class HotelDatabase {
    public static ArrayList<Guest> guests = new ArrayList<>();
    public static ArrayList<Room> rooms = new ArrayList<>();
    public static ArrayList<Reservation> reservations = new ArrayList<>();
    public static ArrayList<Invoice> invoices = new ArrayList<>();

    public static ArrayList<RoomType> roomTypes = new ArrayList<>();
    public static ArrayList<Amenity> allAmenities = new ArrayList<>();

    public static void initialize() {
        // 1. Setup Room Types
        RoomType single = new RoomType("Single", 500);
        RoomType suite = new RoomType("Suite", 2000);
        roomTypes.add(single);
        roomTypes.add(suite);

        // 2. Setup Amenities
        allAmenities.add(new Amenity("WiFi"));
        allAmenities.add(new Amenity("Mini-bar"));

        // 3. Setup Rooms
        rooms.add(new Room(85, single));
        rooms.add(new Room(45, suite));

        // 4. Setup dummy Guest
        try {
            guests.add(new Guest("admin_user", "Password123!",
                    LocalDate.of(1990, 1, 1),
                    Gender.MALE, 500.0, "123 Street", "None"));
        } catch (WeakPwordException | InvalidDateException e) {
            System.out.println("Error creating dummy guest: " + e.getMessage());
        }

        System.out.println("Database initialized with dummy data.");
    }
}