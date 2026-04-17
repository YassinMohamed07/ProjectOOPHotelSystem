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

    public static void initialize() throws InvalidDateException, WeakPwordException {
        guests.add(new Guest("Karim","KarimIsmail.2007",LocalDate.of(2007,2,20),Gender.MALE,2,"zahra2 el maadi","A room with strong wifi"));
        guests.add(new Guest("Abdullah","Abdalaa.2007",LocalDate.of(2007,3,4),Gender.MALE,500,"Madinaty","A suite  room with a mini bar"));
        guests.add(new Guest("Ali","Aliatef.2007",LocalDate.of(2007,10,10),Gender.MALE,200000,"Mokatam","A double room with big bed"));
staff.add(new Admin("Seif","Seif.2007",LocalDate.of(2007,10,14),9));
staff.add(new Receptionist("Yassin","Yassin.2007",LocalDate.of(2007,10,10),9));
staff.add(new Admin("Mohamed","Moh.2007",LocalDate.of(2007,11,11),9));
roomTypes.add(new RoomType("Single",2000));
roomTypes.add(new RoomType("Double",3500));
roomTypes.add(new RoomType("Suite",4500));
allAmenities.add(new Amenity("Wifi",200));
allAmenities.add(new Amenity("Mini Bar",250));
rooms.add(new Room(202,new RoomType("Single",2000),allAmenities,Roomtypee.SINGLE));
rooms.add(new Room(115,new RoomType("Suite",4500),allAmenities,Roomtypee.SUITE));
reservations.add(new Reservation(guests.get(0),rooms.get(0),LocalDate.of(2026,6,15),LocalDate.of(2026,6,17)));
reservations.add(new Reservation(guests.get(1),rooms.get(1),LocalDate.of(2026,4,25),LocalDate.of(2026,4,27)));
invoices.add(new Invoice(reservations.get(0)));
invoices.add(new Invoice(reservations.get(1)));
reservations.get(0).setInvoice(invoices.get(0));
        reservations.get(1).setInvoice(invoices.get(1));

    }
}
