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




    /**
     *  Guest must be able to make reservations
     * Stub: waiting for (teammate4)'s Reservation class
     */
    public Reservation makeReservation(Room room, LocalDate checkIn, LocalDate checkOut)
            throws InvalidDateException {
        // Validation
        if (room == null || checkIn == null || checkOut == null) {
            throw new InvalidDateException("Room and dates required");
        }
        if (checkIn.isAfter(checkOut)) {
            throw new InvalidDateException("Check-in must be before check-out");
        }

        /*
         STUB: fake code to make code runnable for now
         imp. Replace with "new Reservation(this, room, checkIn, checkOut)" when Teammate 4 is finished
        */
        System.out.println("[STUB] Reservation would be created here. Waiting for Teammate 4.");
        return null;
    }

    /**
     * Guest must be able to view their reservations
     * STUB: waiting for Teammate4
     */
    public java.util.List<Reservation> viewReservations() {
        System.out.println("[STUB] viewReservations() - returns an empty list until Teammate4 finishes phase 3");
        return new java.util.ArrayList<>(); // this will return an Empty list
    }

    /**
     * Guest must be able to cancel reservations
     * STUB waiting for Teammate 4
     */
    public void cancelReservation(Reservation reservation)
            throws InvalidCredentialException {
        if (reservation == null) {
            throw new InvalidCredentialException("Reservation not found");
        }
        System.out.println("[STUB] Reservation would be cancelled here. Waiting for Teammate 4.");
    }


    // search available rooms (STUB, phase 3):
    public static java.util.List<Room> searchAvailableRooms(LocalDate checkIn, LocalDate checkOut,
                                                            RoomType type, double maxPrice)
            throws InvalidDateException {
        System.out.println("[STUB] searchAvailableRooms() - Phase 3- Waiting for Teammate 4.");
        return new java.util.ArrayList<>(); // Empty list prevents crashes
    }


    // CHECKOUT (PHASE 3 - STUB)
    public Invoice checkout(Reservation reservation) throws Exception {
        System.out.println("STUB checkout() - Phase 3, Waiting for Teammate(5).");
        return null;
    }
}
