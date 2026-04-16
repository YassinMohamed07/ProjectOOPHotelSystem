package models;

import database.HotelDatabase;
import interfaces.Payable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

public class Invoice implements Payable {

        private Reservation reservation;
        private LocalDate checkInDate;
        private LocalDate checkOutDate;
        private PaymentMethod paymentmethod = PaymentMethod.CASH;
        private final LocalDate transactionDate = LocalDate.now();

        // Move these declarations here, but DON'T initialize them yet
        private long numberOfNights;
        private double roomPricePerOneNight;

        public Invoice(Reservation reservation) {
            if (reservation == null) {
                throw new IllegalArgumentException("Cannot create an invoice without a valid reservation.");
            }


            this.reservation = reservation;


            this.checkInDate = reservation.getCheckInDate();
            this.checkOutDate = reservation.getCheckOutDate();


            this.numberOfNights = ChronoUnit.DAYS.between(checkInDate, checkOutDate);
            this.roomPricePerOneNight = reservation.getRoom().totalRoomPricePerOneNight();
        }

        // ... rest of your methods (calculateTotal, processPayment, etc.)

    @Override
    public double calculateTotal(){
        return numberOfNights*roomPricePerOneNight+(0.14*numberOfNights*roomPricePerOneNight);
    }
public boolean processPayment(double amountPaid){
        if(amountPaid>=calculateTotal()){
            reservation.setPaid(true);
            reservation.setReservationStatus(ReservationStatus.CONFIRMED);
            return true;

        }

        reservation.setReservationStatus(ReservationStatus.PENDING);
        return false;

}
    @Override
    public String toString() {
        // Define a format for the rows: %-16s is a left-aligned 16-character column
        String rowFormat = "%-16s %s%n";
        double amenities = reservation.getRoom().totalAmenitiesPrice();
        double total = calculateTotal();

        // Determine status without calling a "process" method (use a getter instead)
        String status = reservation.isPaid() ? "PAID" : "UNPAID";

        StringBuilder sb = new StringBuilder();
        sb.append("------------------------------------\n");
        sb.append("          HOTEL INVOICE             \n");
        sb.append("------------------------------------\n");

        sb.append(String.format(rowFormat, "Guest:", reservation.getGuest().getUsername()));
        sb.append(String.format(rowFormat, "Room:", "#" + reservation.getRoom().getRoomNumber()));
        sb.append(String.format(rowFormat, "Nights:", numberOfNights));
        sb.append(String.format(rowFormat, "Room Rate:", "$" + String.format("%.2f", roomPricePerOneNight)));
        sb.append(String.format(rowFormat, "Amenities:", "$" + String.format("%.2f", amenities)));

        sb.append("------------------------------------\n");
        sb.append(String.format(rowFormat, "TOTAL DUE:", "$" + String.format("%.2f", total)));
        sb.append(String.format("%-16s %s%n", "", "(Including taxes)"));
        sb.append(String.format(rowFormat, "Status:", status));
        sb.append(String.format(rowFormat, "Date:", transactionDate));
        sb.append(String.format(rowFormat, "Payment Method:", this.getPaymentmethod()));
        sb.append("------------------------------------");

        return sb.toString();
    }


    public PaymentMethod getPaymentmethod() {
        return paymentmethod;
    }

    public void setPaymentmethod(PaymentMethod paymentmethod) {
        this.paymentmethod = paymentmethod;
    }
    public Reservation getReservation() {
        return reservation;
    }
}






