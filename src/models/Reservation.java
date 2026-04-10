package models;

import java.time.LocalDate;
import java.util.ArrayList;

public class Reservation {

    // Dummy fields (for testing)

    private String guestName = "DummyGuest";
    private int roomNumber = 0;
    private LocalDate checkInDate = LocalDate.now();
    private LocalDate checkOutDate = LocalDate.now().plusDays(2);
    private double roomBasePrice = 500.0;
    private ArrayList<Amenity> roomAmenities = new ArrayList<>();
    private boolean checkedIn = false;
    private boolean checkedOut = false;

    // Dummy Getters (for testing)

    public String getGuestName() {
        return guestName;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public LocalDate getCheckInDate() {
        return checkInDate;
    }

    public LocalDate getCheckOutDate() {
        return checkOutDate;
    }

    public double getRoomBasePrice() {
        return roomBasePrice;
    }

    public ArrayList<Amenity> getRoomAmenities() {
        return roomAmenities;
    }

    public boolean isCheckedIn() {
        return checkedIn;
    }

    public void setCheckedIn(boolean checkedIn) {
        this.checkedIn = checkedIn;
    }

    public boolean isCheckedOut() {
        return checkedOut;
    }

    public void setCheckedOut(boolean checkedOut) {
        this.checkedOut = checkedOut;
    }

    // Dummy Setters (for testing)

    public void setGuestName(String guestName) {
        this.guestName = guestName;
    }

    public void setRoomNumber(int roomNumber) {
        this.roomNumber = roomNumber;
    }

    public void setCheckInDate(LocalDate checkInDate) {
        this.checkInDate = checkInDate;
    }

    public void setCheckOutDate(LocalDate checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    public void setRoomBasePrice(double roomBasePrice) {
        this.roomBasePrice = roomBasePrice;
    }

    public void setRoomAmenities(ArrayList<Amenity> roomAmenities) {
        this.roomAmenities = roomAmenities;
    }
}
