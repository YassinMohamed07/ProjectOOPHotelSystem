package models;

import database.HotelDatabase;
import exceptions.InvalidDateException;
import exceptions.WeakPwordException;
import utils.ValidationUtil;

import java.time.LocalDate;

public class Receptionist extends Staff {
final private Role myrole=Role.RECEPTIONIST;
    public Receptionist(String username, String password, LocalDate dateOfBirth, int workingHours) throws WeakPwordException, InvalidDateException {
        ValidationUtil.validatePassword(password);
        ValidationUtil.validateDateOfBirth(dateOfBirth);
        ValidationUtil.validateUsername(username);
        super(username, password, dateOfBirth, Role.RECEPTIONIST, workingHours);
    }

    // Check-in operations
    public void checkIn(Reservation reservation) {
        if (reservation == null) {
            System.out.println("Error: No reservation provided for check-in.");
        }

        if (reservation.isCheckedIn()) {

            System.out.println("Notice: Guest '" + reservation.getGuest().getUsername()
                    + "' is already checked in to Room #" + reservation.getRoom().getRoomNumber() + ".");
        }


        // Mark as checked in
        reservation.setCheckedIn(true);
        System.out.println("== CHECK-IN SUCCESSFUL ==");
        System.out.println("Guest: " + reservation.getGuest().getUsername());
        System.out.println("Room: #" + reservation.getRoom().getRoomNumber());
        System.out.println("Check-in Date: " + reservation.getCheckInDate());
        System.out.println("Check-out Date: " + reservation.getCheckOutDate());
    }

    // Check-out operations
    public Invoice checkOut(Reservation reservation) {
        if (reservation == null) {
            System.out.println("Error: No reservation provided for check-out.");
            return null;
        }

        if (!reservation.isCheckedIn()) {

            System.out.println("Error: Guest: " + reservation.getGuest().getUsername()
                    + " was never checked in. Cannot check out.");
            return null;
        }

        if (reservation.isCheckedOut()) {

            System.out.println("Notice: Guest: " + reservation.getGuest().getUsername()
                    + " has already been checked out.");
            return null;
        }

        // Mark as checked out
        reservation.setCheckedOut(true);

        // Generate the invoice
        Invoice invoice = new Invoice(reservation);
        HotelDatabase.invoices.add(invoice);

        System.out.println("== CHECK-OUT SUCCESSFUL ==");
        System.out.println("Guest: " + reservation.getGuest().getUsername());
                System.out.println("Room:  #" + reservation.getRoom().getRoomNumber());
        System.out.println("--Invoice Summary--");
        System.out.println(invoice);
        return invoice;
    }

    // Process payment for a checkout invoice.
    public boolean processCheckoutPayment(Invoice invoice, double amountPaid) {
        if (invoice == null) {
            System.out.println("Error: No invoice provided for payment.");
            return false;
        }

        boolean success = invoice.processPayment(amountPaid);
        if (success) {
            System.out.println("Payment of $" + String.format("%.2f", amountPaid) + " processed successfully.");
        } else {
            System.out.println("Payment failed. Amount $" + String.format("%.2f", amountPaid)
                    + " is insufficient. Total due: $" + String.format("%.2f", invoice.calculateTotal()));
        }
        return success;
    }

    @Override
    public void viewAllGuests() {
        super.viewAllGuests();
    }

    @Override
    public void viewAllRooms() {
        super.viewAllRooms();
    }

    @Override
    public void viewAllReservations() {
        super.viewAllReservations();
    }

    // View all invoices in the system.
    public void viewAllInvoices() {
        System.out.println("\n-- All Invoices --");
        if (HotelDatabase.invoices.isEmpty()) {
            System.out.println("No invoices generated yet.");
        }
        for (int i = 0; i < HotelDatabase.invoices.size(); i++) {
            System.out.println("Invoice #" + (i + 1));
            System.out.println(HotelDatabase.invoices.get(i));
        }
    }

    @Override
    public String toString() {
        return "Receptionist:" + username + ", Hours:" + workingHours;
    }
}
