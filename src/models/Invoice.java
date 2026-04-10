package models;

import interfaces.Payable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

public class Invoice implements Payable {

    private Reservation reservation;
    private String guestName;
    private int roomNumber;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private long numberOfNights;
    private double roomBasePrice;
    private double roomTotal;
    private double amenityTotal;
    private double totalAmount;
    private boolean isPaid;
    private double amountPaid;
    private LocalDate invoiceDate;

    public Invoice(Reservation reservation) {
        this.reservation = reservation;
        this.guestName = reservation.getGuestName();
        this.roomNumber = reservation.getRoomNumber();
        this.checkInDate = reservation.getCheckInDate();
        this.checkOutDate = reservation.getCheckOutDate();
        this.invoiceDate = LocalDate.now();
        this.isPaid = false;
        this.amountPaid = 0.0;

        // calculate number of nights
        this.numberOfNights = ChronoUnit.DAYS.between(checkInDate, checkOutDate);
        if (this.numberOfNights <= 0) {
            this.numberOfNights = 1; // Min 1 night
        }
        // calculate room cost
        this.roomBasePrice = reservation.getRoomBasePrice();
        this.roomTotal = roomBasePrice * numberOfNights;

        // calculate amenity cost
        this.amenityTotal = calculateAmenityTotal(reservation);

        // calculate final total
        this.totalAmount = roomTotal + amenityTotal;
    }

    // calculate amenity total
    private double calculateAmenityTotal(Reservation reservation) {
        ArrayList<Amenity> amenities = reservation.getRoomAmenities();
        if (amenities == null || amenities.isEmpty()) {
            return 0.0;
        }

        double total = 0.0;
        for (Amenity amenity : amenities) {
            total += amenity.getPrice(); // dummy getter
        }
        return total;
    }

    @Override
    public double calculateTotal() {
        return this.totalAmount;
    }

    // process payment
    @Override
    public boolean processPayment(double amountPaid) {
        if (amountPaid < this.totalAmount) {
            System.out.println("[PAYMENT FAILED] Insufficient amount. Paid: $"
                    + String.format("%.2f", amountPaid)
                    + " | Due: $" + String.format("%.2f", totalAmount));
            return false;
        }

        this.isPaid = true;
        this.amountPaid = amountPaid;
        double change = amountPaid - totalAmount;

        System.out.println("[PAYMENT SUCCESS] $" + String.format("%.2f", amountPaid) + " received.");
        if (change > 0) {
            System.out.println("Change returned: $" + String.format("%.2f", change));
        }
        return true;
    }

    public Reservation getReservation() {
        return reservation;
    }

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

    public long getNumberOfNights() {
        return numberOfNights;
    }

    public double getRoomBasePrice() {
        return roomBasePrice;
    }

    public double getRoomTotal() {
        return roomTotal;
    }

    public double getAmenityTotal() {
        return amenityTotal;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public boolean isPaid() {
        return isPaid;
    }

    public double getAmountPaid() {
        return amountPaid;
    }

    public LocalDate getInvoiceDate() {
        return invoiceDate;
    }

    @Override
    public String toString() {
        return "        HOTEL INVOICE       \n"
                + "  ====================\n"
                + "Invoice Date: " + invoiceDate + "\n"
                + "Guest:        " + guestName + "\n"
                + "Room #:       " + roomNumber + "\n"
                + "Check-in:     " + checkInDate + "\n"
                + "Check-out:    " + checkOutDate + "\n"
                + "\n"
                + "Nights:       " + numberOfNights + "\n"
                + "Room Rate:    $" + String.format("%.2f", roomBasePrice) + "/night\n"
                + "Room Total:   $" + String.format("%.2f", roomTotal) + "\n"
                + "Amenities:    $" + String.format("%.2f", amenityTotal) + "\n"
                + "\n"
                + "TOTAL DUE:    $" + String.format("%.2f", totalAmount) + "\n"
                + "Status:       " + (isPaid ? "PAID" : "UNPAID") + "\n";

    }
}