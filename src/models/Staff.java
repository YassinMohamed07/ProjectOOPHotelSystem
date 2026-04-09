package models;

import database.HotelDatabase;
import java.time.LocalDate;

public abstract class Staff {
    protected String username;
    protected String password;
    protected LocalDate dateOfBirth;
    protected Role role;
    protected int workingHours;

    public Staff(String username, String password, LocalDate dateOfBirth, Role role, int workingHours) {
        this.username = username;
        this.password = password;
        this.dateOfBirth = dateOfBirth;
        this.role = role;
        this.workingHours = workingHours;
    }

    public void viewAllGuests() {
        System.out.println("\n--- Hotel Guests ---");
        if (HotelDatabase.guests.isEmpty()) {
            System.out.println("No guests registered yet.");
            return;
        }
        for (Guest g : HotelDatabase.guests) {
            System.out.println("Guest Username: " + g.getUsername());
        }
    }
    public void viewAllRooms() {
        System.out.println("\n--- Room Inventory ---");
        if (HotelDatabase.rooms.isEmpty()) {
            System.out.println("No rooms available in the system.");
            return;
        }
        for (Room r : HotelDatabase.rooms) {
            System.out.println("Room #" + r.getRoomNumber() + " | Type: " + r.getType().getTypeName());
        }
    }
    public void viewAllReservations() {
        System.out.println("\n--- Current Reservations ---");
        if (HotelDatabase.reservations.isEmpty()) {
            System.out.println("No active reservations in the system.");
            return;
        }

        // The loop is perfectly written and ready to go.
        for (Reservation res : HotelDatabase.reservations) {
            // We use a placeholder string to prevent red compile errors until Teammate 4 finishes.
            System.out.println("Reservation found. -> [Guest Name and Dates pending Teammate 4's getters]");
        }
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public int getWorkingHours() {
        return workingHours;
    }

    public void setWorkingHours(int workingHours) {
        this.workingHours = workingHours;
    }
}

