package models;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import exceptions.*;
import utils.ValidationUtil;
import database.HotelDatabase;

public class Guest {
    private String username;
    private String password;
    private LocalDate dateOfBirth;
    private Gender gender;
    private double balance;
    private String address;
    private String roomPreferences;


    public  Guest(String username, String password, LocalDate dateOfBirth, Gender gender, double balance, String address, String roomPreferences)
        throws WeakPwordException, InvalidDateException{

        ValidationUtil.validatePassword(password);
        ValidationUtil.validateDateOfBirth(dateOfBirth);
        ValidationUtil.validateUsername(username);

        this.username = username;
        this.password = password;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.balance = balance;
        this.address = address;
        this.roomPreferences = roomPreferences;

        }
    public String getUsername() { return username; }
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public Gender getGender() { return gender; }
    public double getBalance() { return balance; }
    public String getAddress() { return address; }
    public String getRoomPreferences() { return roomPreferences; }
    public void setAddress(String address) { this.address = address; }
    public void setRoomPreferences(String prefs) { this.roomPreferences = prefs; }
    public void setBalance(double balance) { this.balance = balance; }

    public boolean verifyPassword(String input) {
        return this.password.equals(input);

    }
    public int getAge() {
        // Period method is a method that calculates time between two dates
        return Period.between(dateOfBirth, LocalDate.now()).getYears();
    }

    @Override
    public String toString() {
        return "Guest[" + username + ", " + gender + ", Age:" + getAge() + ", Balance:$" + balance + "]";
    }
    public static Guest register(String username, String password, LocalDate dateOfBirth,
                                 Gender gender, double balance, String address,
                                 String roomPreferences)
            throws WeakPwordException, InvalidDateException {

        //1. Create new guest and the constructor handles validation
        Guest newGuest = new Guest(username, password, dateOfBirth, gender,
                balance, address, roomPreferences);

        //2. Add to database; HotelDatabase is Teammate 2's class
        if (HotelDatabase.guests != null) {
            HotelDatabase.guests.add(newGuest);
            System.out.println("Registration successful: " + username);
        }

        return newGuest;
    }public static Guest login(String username, String password)
            throws InvalidCredentialException {

         //Check if database is empty
        if (HotelDatabase.guests == null || HotelDatabase.guests.isEmpty()) {
            throw new InvalidCredentialException("No users in system");
        }

        // Search through all guests using linear search
        for (Guest g : HotelDatabase.guests) {
            if (g.getUsername().equals(username)) {
                if (g.verifyPassword(password)) {
                    System.out.println("Login successful: " + username);
                    return g; // Return the found guest object
                } else {
                    throw new InvalidCredentialException("Wrong password");
                }
            }
        }

        // If loop finishes without finding username
        throw new InvalidCredentialException("User '" + username + "' not found");
    }





    //Guest must be able to make reservations

    public Reservation makeReservation(Room room, LocalDate checkIn, LocalDate checkOut)
            throws InvalidDateException {
        // Validation
        if (room == null || checkIn == null || checkOut == null) {
            throw new InvalidDateException("Room and dates required");
        }
        if (checkIn.isAfter(checkOut)) {
            throw new InvalidDateException("Check-in must be before check-out");
        }

        Reservation newRes = new Reservation(this, room, checkIn, checkOut);
        HotelDatabase.reservations.add(newRes);
        System.out.println("Reservation created successfully for room " + room.getRoomNumber());
        return newRes;
    }


     //Guest must be able to view their reservations

    public java.util.List<Reservation> viewReservations() {
        List<Reservation> myReservations = new ArrayList<>();
        for (Reservation res : HotelDatabase.reservations) {
            if (res.getGuest().equals(this)) {
                myReservations.add(res);
            }
        }
        return myReservations;
    }


    // Guest must be able to cancel reservations

    public void cancelReservation(Reservation reservation)
            throws InvalidCredentialException {
        if (reservation == null) {
            throw new InvalidCredentialException("Reservation not found");
        }
        if (!reservation.getGuest().equals(this)) {
            throw new InvalidCredentialException("Not your reservation");
        }
        reservation.cancelReservation();
        System.out.println("Reservation cancelled successfully");
    }


    // SEARCH logic - finding available rooms based on criteria
    public static List<Room> searchAvailableRooms(LocalDate checkIn, LocalDate checkOut,
                                                  Roomtypee type, double maxPrice)
            throws InvalidDateException {

        // Validate dates
        if (checkIn == null || checkOut == null) {
            throw new InvalidDateException("Dates required");
        }
        if (checkIn.isAfter(checkOut)) {
            throw new InvalidDateException("Check-in must be before check-out");
        }
        if (checkIn.isBefore(LocalDate.now())) {
            throw new InvalidDateException("Cannot book in the past");
        }

        List<Room> available = new ArrayList<>();

        // Check each room in database
        for (Room room : HotelDatabase.rooms) {
            // Filtering by type
            if (type != null && !room.getType().getRoomType().equals(type)) {
                continue; // Skip!!!
            }

            // Filter by price
            if (maxPrice > 0 && room.totalRoomPricePerOneNight() > maxPrice) {
                continue;
            }

            // Imp.: Check if room is available for these dates
            if (isRoomAvailable(room, checkIn, checkOut)) {
                available.add(room);
            }
        }

        return available;
    }

    // helper method; Check if specific room is free for date range
    private static boolean isRoomAvailable(Room room, LocalDate checkIn, LocalDate checkOut) {
        // Check against all reservations (Teammate 4 creates Reservation class)
        for (Reservation res : HotelDatabase.reservations) {
            if (res.getRoom().equals(room) && !res.isCancelled()) {
                // DATE OVERLAP LOGIC
                // Two ranges overlap if: (StartA <= EndB) AND (EndA >= StartB)
                LocalDate existingCheckIn = res.getCheckInDate();
                LocalDate existingCheckOut = res.getCheckOutDate();

                boolean overlaps = !(existingCheckOut.isBefore(checkIn) ||
                        existingCheckIn.isAfter(checkOut));

                if (overlaps) return false; // Room is occupied
            }
        }
        return true; // No conflicts were found
    }


    // Checkout - integration with Teammate5's Invoice system
    public Invoice checkout(Reservation reservation) throws Exception {
        // Verify this guest owns this reservation
        if (!reservation.getGuest().equals(this)) {
            throw new InvalidCredentialException("This is not your reservation");
        }

        // Create invoice Teammate5 implements the invoice constructor
        Invoice invoice = new Invoice(reservation);

        // Payable interface methods Mohamed created that interface
        double total = invoice.calculateTotal();

        // Check the balance
        if (this.balance < total) {
            throw new InvalidCredentialException(
                    "Insufficient funds. Need: $" + total + ", Have: $" + balance
            );
        }

        // Process payment
        boolean success = invoice.processPayment(total);
        if (success) {
            this.balance -= total;
            System.out.println("Payment successful! Remaining balance: $" + this.balance);
            return invoice;
        } else {
            throw new InvalidCredentialException("Payment failed");
        }
    }

}
