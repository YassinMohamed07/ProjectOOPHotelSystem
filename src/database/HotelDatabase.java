package database;

import models.*;
import java.util.ArrayList;
import java.time.LocalDate;
import exceptions.WeakPwordException;
import exceptions.InvalidDateException;

public class HotelDatabase {
    public static ArrayList<Staff> staff= new ArrayList<>();
    public static ArrayList<Guest> guests = new ArrayList<>();
    public static ArrayList<Room> rooms = new ArrayList<>();
    public static ArrayList<Reservation> reservations = new ArrayList<>();
    public static ArrayList<Invoice> invoices = new ArrayList<>();
    public static ArrayList<RoomType> roomTypes = new ArrayList<>();
    public static ArrayList<Amenity> allAmenities = new ArrayList<>();
   public static  ArrayList<Amenity> singleDefaults = new ArrayList<>();
    public static ArrayList<Amenity> doubleDefaults= new ArrayList<>();
   public static  ArrayList<Amenity> suiteDefaults = new ArrayList<>();
    public static void initialize() throws InvalidDateException, WeakPwordException {
        // --- 1. DEFINE STANDARD AMENITIES (Price = 0.0) ---
        Amenity basicWifi = new Amenity("Basic Wi-Fi", 0.0);
        Amenity smartTv = new Amenity("Standard Smart TV", 0.0);
        Amenity ac = new Amenity("Air Conditioning & Heating", 0.0);
        Amenity safe = new Amenity("In-Room Safe", 0.0);
        Amenity housekeeping = new Amenity("Daily Housekeeping", 0.0);
        Amenity workDesk = new Amenity("Work Desk & Chair", 0.0);
        Amenity coffeeStation = new Amenity("Coffee & Tea Station", 0.0);
        Amenity bathrobes = new Amenity("Premium Bathrobes", 0.0);
        Amenity freeMiniBar = new Amenity("Complimentary Mini-Bar", 0.0);
        Amenity loungeArea = new Amenity("Separate Lounge Area", 0.0);

        // --- 2. DEFINE PREMIUM AMENITIES (Paid Extras) ---
        Amenity fiberWifi = new Amenity("High-Speed Fiber Wi-Fi", 15.0);
        Amenity lateCheckout = new Amenity("Late Checkout (2:00 PM)", 30.0);
        Amenity spaAccess = new Amenity("Spa & Sauna Access", 75.0);
        Amenity parking = new Amenity("Premium Valet Parking", 25.0);
        Amenity sportsChannels = new Amenity("BeIN Sports Channel Access", 20.0);
        Amenity streaming = new Amenity("Premium Streaming (HBO/Netflix)", 15.0);
        Amenity massage = new Amenity("In-Room Massage Therapy", 120.0);
        Amenity airportTransfer = new Amenity("Airport Limousine Transfer", 80.0);
        Amenity champagne = new Amenity("Champagne & Fruit Platter", 50.0);
        Amenity laundry = new Amenity("Same-Day Laundry Service", 40.0);

        // Add ALL to the master database list so the system recognizes them
        allAmenities.add(basicWifi);
        allAmenities.add(smartTv);
        allAmenities.add(ac);
        allAmenities.add(safe);
        allAmenities.add(housekeeping);
        allAmenities.add(workDesk);
        allAmenities.add(coffeeStation);
        allAmenities.add(bathrobes);
        allAmenities.add(freeMiniBar);
        allAmenities.add(loungeArea);
        allAmenities.add(fiberWifi);
        allAmenities.add(lateCheckout);
        allAmenities.add(spaAccess);
        allAmenities.add(parking);
        allAmenities.add(sportsChannels);
        allAmenities.add(streaming);
        allAmenities.add(massage);
        allAmenities.add(airportTransfer);
        allAmenities.add(champagne);
        allAmenities.add(laundry);

        // --- 3. BUILD DEFAULT LISTS FOR ROOM TYPES ---

        // Single Room Defaults (Base + Desk)

        singleDefaults.add(basicWifi);
        singleDefaults.add(smartTv);
        singleDefaults.add(ac);
        singleDefaults.add(safe);
        singleDefaults.add(housekeeping);
        singleDefaults.add(workDesk);

        // Double Room Defaults (Inherits Single, adds Coffee Station)
        doubleDefaults = new ArrayList<>(singleDefaults);
        doubleDefaults.add(coffeeStation);

        // Suite Defaults (Inherits Double, adds Bathrobes, Free Minibar, Lounge)
         suiteDefaults= new ArrayList<>(doubleDefaults);
        suiteDefaults.add(bathrobes);
        suiteDefaults.add(freeMiniBar);
        suiteDefaults.add(loungeArea);

        // --- 4. CREATE DATABASE ENTITIES ---

        // Guests
        guests.add(new Guest("Karim","KarimIsmail.2007",LocalDate.of(2007,2,20),Gender.MALE,20000000,"zahra2 el maadi","A room with strong wifi"));
        guests.add(new Guest("Abdullah","Abdalaa.2007",LocalDate.of(2007,3,4),Gender.MALE,500,"Madinaty","A suite room with a mini bar"));
        guests.add(new Guest("Ali","Aliatef.2007",LocalDate.of(2007,10,10),Gender.MALE,200000,"Mokatam","A double room with big bed"));

        // Staff
        staff.add(new Admin("Seif","Seif.2007",LocalDate.of(2007,10,14),9));
        staff.add(new Receptionist("Yassin","Yassin.2007",LocalDate.of(2007,10,10),9));
        staff.add(new Admin("Mohamed","Moh.2007",LocalDate.of(2007,11,11),9));

        // Room Types
        roomTypes.add(new RoomType("Single", 2000));
        roomTypes.add(new RoomType("Double", 3500));
        roomTypes.add(new RoomType("Suite", 4500));

        // Rooms (Injecting the specific default amenity lists here)
        rooms.add(new Room(202, roomTypes.get(0)));
        rooms.add(new Room(102, roomTypes.get(1)));
        rooms.add(new Room(115, roomTypes.get(2)));

        // Reservations
        reservations.add(new Reservation(guests.get(0), rooms.get(0), LocalDate.of(2026,6,15), LocalDate.of(2026,6,17)));
        reservations.add(new Reservation(guests.get(1), rooms.get(1), LocalDate.of(2026,4,25), LocalDate.of(2026,4,27)));

        // Invoices
        invoices.add(new Invoice(reservations.get(0)));
        invoices.add(new Invoice(reservations.get(1)));
        reservations.get(0).setInvoice(invoices.get(0));
        reservations.get(1).setInvoice(invoices.get(1));
    }
}
