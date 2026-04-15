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
        return numberOfNights*roomPricePerOneNight;
    }
public boolean processPayment(double amountPaid){
        if(amountPaid>=calculateTotal()){
       System.out.println("Payment succeeded");
            reservation.setReservationStatus(ReservationStatus.CONFIRMED);
            return true;

        }

        reservation.setReservationStatus(ReservationStatus.PENDING);
        return false;

}
    @Override
    public String toString() {

        return "------------------------------------\n" +
                "          HOTEL INVOICE             \n" +
                "------------------------------------\n" +
                "Guest:         " + reservation.getGuest().getUsername() + "\n" +
                "Room:          #" + reservation.getRoom().getRoomNumber() + "\n" +
                "Nights:        " + numberOfNights + "\n" +
                "Room Rate:     $" + roomPricePerOneNight + " $\n" +
                "Amenities:     $" + reservation.getRoom().totalAmenitiesPrice() + " $\n" +
                "------------------------------------\n" +
                "TOTAL DUE:     $" + calculateTotal() + "$ \n" +
                "Status:        " + (reservation.isCheckedOut() ? "PAID" : "UNPAID") + "\n" +
                "Date:          " + transactionDate + "\n" +
                "------------------------------------";
    }


    public PaymentMethod getPaymentmethod() {
        return paymentmethod;
    }

    public void setPaymentmethod(PaymentMethod paymentmethod) {
        this.paymentmethod = paymentmethod;
    }
}






