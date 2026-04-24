package models;

import database.HotelDatabase;
import exceptions.InvalidDateException;
import exceptions.WeakPwordException;
import exceptions.InvalidCredentialException;
import utils.ValidationUtil;
import java.util.Scanner;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Receptionist extends Staff {

    public Receptionist(String username, String password, LocalDate dateOfBirth, int workingHours) throws WeakPwordException, InvalidDateException {
        super(username, password, dateOfBirth, Role.RECEPTIONIST, workingHours);
        ValidationUtil.validatePassword(password);
        ValidationUtil.validateDateOfBirth(dateOfBirth);
        ValidationUtil.validateUsername(username);
    }

    // Check-in operations
    public void checkIn(Reservation reservation) {
        if (reservation == null) {
            System.out.println("Error: No reservation provided for check-in.");
            return;
        }

        if (reservation.isCheckedIn()) {

            System.out.println("Notice: Guest '" + reservation.getGuest().getUsername()
                    + "' is already checked in to Room #" + reservation.getRoom().getRoomNumber() + ".");
            return;
        }
        LocalDate today = LocalDate.now();
        LocalDate checkInDate = reservation.getCheckInDate();

        if (today.isBefore(checkInDate)) {
            System.out.println("Error: Cannot check in. Reservation starts on " + checkInDate
                    + ". Today is " + today + ".");
            return;
        }

        if (today.isAfter(reservation.getCheckOutDate())) {
            System.out.println("Error: Cannot check in. Reservation already expired on "
                    + reservation.getCheckOutDate() + ".");
            return;
        }

        // Mark as checked in
        reservation.setCheckedIn(true);
        reservation.setReservationStatus(ReservationStatus.CONFIRMED);
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
            return reservation.getInvoice();
        }
        LocalDate today = LocalDate.now();
        if (today.isBefore(reservation.getCheckInDate())) {
            System.out.println("Error: Cannot check out. Reservation hasn't started yet (starts "
                    + reservation.getCheckInDate() + ").");
            return null;
        }
        // Mark as checked out
        reservation.setCheckedOut(true);
        reservation.setReservationStatus(ReservationStatus.CONFIRMED);
        // Generate the invoice
        Invoice invoice = reservation.getInvoice();
        if (invoice == null) {
            invoice = new Invoice(reservation);
            reservation.setInvoice(invoice);
            HotelDatabase.invoices.add(invoice);
        }

        System.out.println("== CHECK-OUT SUCCESSFUL ==");
        System.out.println("Guest: " + reservation.getGuest().getUsername());
                System.out.println("Room:  #" + reservation.getRoom().getRoomNumber());
        System.out.println("--Invoice Summary--");
        System.out.println(invoice);
        return invoice;
    }
    // Manual status update for edge cases
    public void updateReservationStatus(Reservation reservation, ReservationStatus newStatus)
            throws InvalidCredentialException {
        if (reservation == null) {
            throw new InvalidCredentialException("No reservation provided");
        }

        ReservationStatus current = reservation.getReservationStatus();
        if (current == ReservationStatus.CANCELLED && newStatus != ReservationStatus.CANCELLED) {
            throw new InvalidCredentialException("Cannot change status of CANCELLED reservation");
        }

        reservation.setReservationStatus(newStatus);
        System.out.println("Updated: " + current + " → " + newStatus);
    }

    // Auto-complete expired reservations
    public int finalizeCompletedReservations() {
        int count = 0;
        LocalDate today = LocalDate.now();

        for (Reservation res : HotelDatabase.reservations) {
            if (res.getCheckOutDate().isBefore(today) &&
                    res.isCheckedIn() &&
                    !res.isCheckedOut() &&
                    res.getReservationStatus() == ReservationStatus.CONFIRMED) {

                res.setCheckedOut(true);
                res.setReservationStatus(ReservationStatus.COMPLETED);

                if (res.getInvoice() == null) {
                    Invoice inv = new Invoice(res);
                    res.setInvoice(inv);
                    HotelDatabase.invoices.add(inv);
                }
                count++;
            }
        }
        System.out.println("Auto-completed " + count + " expired reservations");
        return count;
    }

    // Interactive menu for manual updates
    public void manualStatusUpdateMenu() {
        Scanner sc = new Scanner(System.in);
        System.out.print("\nEnter Guest Username: ");
        String username = sc.next();

        List<Reservation> guestReservations = new ArrayList<>();
        for (Reservation r : HotelDatabase.reservations) {
            if (r.getGuest().getUsername().equalsIgnoreCase(username)) {
                guestReservations.add(r);
            }
        }

        if (guestReservations.isEmpty()) {
            System.out.println("Error: No reservation found for '" + username + "'");
            return;
        }

        System.out.println("\n--- Reservations for " + username + " ---");
        for (int i = 0; i < guestReservations.size(); i++) {
            Reservation res = guestReservations.get(i);
            System.out.println((i+1) + ". Room #" + res.getRoom().getRoomNumber()
                    + " | " + res.getCheckInDate() + " to " + res.getCheckOutDate()
                    + " | Status: " + res.getReservationStatus());
        }

        System.out.print("\nSelect reservation number to update: ");
        int choice;
        try {
            choice = sc.nextInt();
            if (choice < 1 || choice > guestReservations.size()) {
                System.out.println("Invalid choice.");
                return;
            }
        } catch (Exception e) {
            System.out.println("Invalid input.");
            return;
        }

        Reservation found = guestReservations.get(choice - 1);
        System.out.println("Current: " + found.getReservationStatus());
        System.out.println("1.PENDING 2.CONFIRMED 3.CANCELLED 4.COMPLETED");
        System.out.print("Choice: ");

        try {
            int statusChoice = sc.nextInt();
            if (statusChoice < 1 || statusChoice > 4) {
                System.out.println("Invalid choice (1-4 only)");
                return;
            }
            ReservationStatus status = ReservationStatus.values()[statusChoice-1];
            updateReservationStatus(found, status);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
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
            Guest guest = invoice.getReservation().getGuest();
            double total = invoice.calculateTotal();
            guest.setBalance(guest.getBalance() - total);
            System.out.println("Guest balance updated. New balance: $" + String.format("%.2f", guest.getBalance()));
            // Ensure COMPLETED status after successful payment
            Reservation res = invoice.getReservation();
            if (res != null && res.isCheckedOut() && res.getReservationStatus() != ReservationStatus.COMPLETED) {
                res.setReservationStatus(ReservationStatus.COMPLETED);
            }
        } else {
            System.out.println("Payment failed. Amount $" + String.format("%.2f", amountPaid)
                    + " is insufficient. Total due: $" + String.format("%.2f", invoice.calculateTotal()));
        }
        return success;
    }

    public void viewAllGuests() {
        super.viewAllGuests();
    }

    public void viewAllRooms() {
        super.viewAllRooms();
    }

    public void viewAllReservations() {
        System.out.println("\n--- All Reservations ---");
        if (HotelDatabase.reservations.isEmpty()) {
            System.out.println("No active reservations.");
            return;
        }
        for (Reservation res : HotelDatabase.reservations) {
            String marker = "";
            if (res.getReservationStatus() == ReservationStatus.COMPLETED) marker = " [DONE]";
            else if (res.getReservationStatus() == ReservationStatus.CANCELLED) marker = " [CANCELLED]";

            System.out.println(res.getGuest().getUsername() + " | Room #" + res.getRoom().getRoomNumber()
                    + " | " + res.getCheckInDate() + " to " + res.getCheckOutDate()
                    + " | Status: " + res.getReservationStatus() + marker);
        }
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
