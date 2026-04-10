package models;

import database.HotelDatabase;
import interfaces.Payable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

public class Invoice implements Payable {

    private Reservation reservation;
    private PaymentMethod paymentmethod=PaymentMethod.CASH;
    private LocalDate transactionDate=LocalDate.now();
    public Invoice(){}
    public Invoice(Reservation reservation){
        this.reservation=reservation;
    }
   private final long numberOfNights=ChronoUnit.DAYS.between(reservation.getCheckInDate(),reservation.getCheckOutDate());
    private final Room room=reservation.getRoom();
    private final double roomPricePerOneNight=room.totalRoomPricePerOneNight();
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


}






