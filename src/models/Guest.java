// ... keep imports ...
package models;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import exceptions.*;
import utils.ValidationUtil;
import database.HotelDatabase;

public class Guest {
    // ... fields ...
    private String username; private String password; private LocalDate dateOfBirth; private Gender gender;
    private double balance; private String address; private String roomPreferences;

    public Guest(String username, String password, LocalDate dateOfBirth, Gender gender, double balance, String address, String roomPreferences) throws WeakPwordException, InvalidDateException {
        ValidationUtil.validatePassword(password); ValidationUtil.validateDateOfBirth(dateOfBirth); ValidationUtil.validateUsername(username);
        this.username = username; this.password = password; this.dateOfBirth = dateOfBirth; this.gender = gender; this.balance = balance; this.address = address; this.roomPreferences = roomPreferences;
    }
    public String getUsername() { return username; }
    public String getPassword() { return password; } // Added getPassword
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public Gender getGender() { return gender; }
    public double getBalance() { return balance; }
    public String getAddress() { return address; }
    public String getRoomPreferences() { return roomPreferences; }
    public void setAddress(String address) { this.address = address; }
    public void setRoomPreferences(String prefs) { this.roomPreferences = prefs; }
    public void setBalance(double balance) { this.balance = balance; }
    public boolean verifyPassword(String input) { return this.password.equals(input); }
    public int getAge() { return Period.between(dateOfBirth, LocalDate.now()).getYears(); }

    public static Guest register(String username, String password, LocalDate dateOfBirth, Gender gender, double balance, String address, String roomPreferences) throws WeakPwordException, InvalidDateException {
        Guest newGuest = new Guest(username, password, dateOfBirth, gender, balance, address, roomPreferences);
        HotelDatabase.addGuest(newGuest); // Replaced list.add
        return newGuest;
    }

    public static Guest login(String username, String password) throws InvalidCredentialException {
        for (Guest g : HotelDatabase.guests) { if (g.getUsername().equals(username)) { if (g.verifyPassword(password)) return g; else throw new InvalidCredentialException("Wrong password"); } }
        throw new InvalidCredentialException("User '" + username + "' not found");
    }

    public Reservation makeReservation(Room room, LocalDate checkIn, LocalDate checkOut) throws InvalidDateException {
        Reservation newRes = new Reservation(this, room, checkIn, checkOut);
        HotelDatabase.addReservation(newRes); // Replaced list.add
        return newRes;
    }

    public void cancelReservation(Reservation reservation) throws InvalidCredentialException {
        if (!reservation.getGuest().equals(this)) throw new InvalidCredentialException("Not your reservation");
        try {
            reservation.cancelReservation();
            HotelDatabase.updateReservation(reservation); // Added DB Sync
            System.out.println("Reservation cancelled successfully");
        } catch (InvalidDateException e) { System.out.println(e.getMessage()); }
    }

    public Invoice checkout(Reservation reservation) throws Exception, InvalidCredentialException {
        Scanner input = new Scanner(System.in);
        String method = input.next().toUpperCase();
        Invoice invoice = new Invoice(reservation);
        reservation.setInvoice(invoice);
        HotelDatabase.addInvoice(invoice); // Added DB sync

        double total = invoice.calculateTotal();
        reservation.getInvoice().setPaymentmethod(PaymentMethod.valueOf(method));
        if (this.balance < total) throw new InvalidCredentialException("Insufficient funds.");

        if (invoice.processPayment(total)) {
            this.balance -= total;
            HotelDatabase.updateGuest(this); // Added DB Sync
            HotelDatabase.updateReservation(reservation); // Added DB Sync
            HotelDatabase.updateInvoice(invoice); // Added DB Sync
            return invoice;
        } else throw new InvalidCredentialException("Payment failed");
    }
    // ... keep existing viewReservations and searchAvailableRooms identical to original ...
    public java.util.List<Reservation> viewReservations() {
        List<Reservation> myReservations = new ArrayList<>();
        for (Reservation res : HotelDatabase.reservations) { if (res.getGuest().equals(this)) { myReservations.add(res); } }
        return myReservations;
    }
    public static List<Room> searchAvailableRooms(LocalDate checkIn, LocalDate checkOut, String typename, double maxPrice) throws InvalidDateException {
        // Validation ... (keep original logic)
        if (checkIn == null || checkOut == null) throw new InvalidDateException("Dates required");
        if (checkIn.isAfter(checkOut)) throw new InvalidDateException("Check-in must be before check-out");
        if (checkIn.isBefore(LocalDate.now())) throw new InvalidDateException("Cannot book in the past");
        List<Room> available = new ArrayList<>();
        for (Room room : HotelDatabase.rooms) {
            if (typename != null && !room.getType().getTypeName().equalsIgnoreCase(typename)) continue;
            if (maxPrice > 0 && room.totalRoomPricePerOneNight() > maxPrice) continue;
            if (isRoomAvailable(room, checkIn, checkOut)) available.add(room);
        }
        return available;
    }
    private static boolean isRoomAvailable(Room room, LocalDate checkIn, LocalDate checkOut) {
        for (Reservation res : HotelDatabase.reservations) {
            if (res.getRoom().equals(room) && !res.isCancelled()) {
                if (!(res.getCheckOutDate().isBefore(checkIn) || res.getCheckInDate().isAfter(checkOut))) return false;
            }
        }
        return true;
    }
}