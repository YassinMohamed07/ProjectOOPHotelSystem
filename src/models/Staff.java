package models;

import java.time.LocalDate;

public abstract class Staff {
    protected String username;
    protected String password;
    protected LocalDate dateOfBirth;
    protected int workingHours;

    public Staff(String username, String password, LocalDate dateOfBirth, int workingHours) {
        this.username = username;
        this.password = password;
        this.dateOfBirth = dateOfBirth;
        this.workingHours = workingHours;
    }

    public abstract void viewAllGuests();
    public abstract void viewAllRooms();
    public abstract void viewAllReservations();
}
