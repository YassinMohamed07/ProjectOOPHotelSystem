package models;
import database.HotelDatabase;
import exceptions.InvalidDateException;

import java.time.LocalDate;


public class Reservation  {
   private Guest guest;
   private Room room;
   private LocalDate checkInDate =LocalDate.now();
   private LocalDate checkOutDate=LocalDate.now().plusDays(3);
   private ReservationStatus reservationStatus=ReservationStatus.PENDING;
   private Invoice invoice;
   private boolean checkedIn=false;
   private boolean checkedOut=false;
   private boolean isPaid=false;
    public Reservation(){}
    public Reservation(Guest guest,Room room,LocalDate checkIn,LocalDate checkOut) throws InvalidDateException {
        if (checkIn.isAfter(checkOut) || checkIn.equals(checkOut)) {
            throw new InvalidDateException("Error: Check-out must be after check-in.");
        }
int length = HotelDatabase.reservations.size();
        for(int i=0;i<length;i++) {
if ((HotelDatabase.reservations.get(i).getRoom().getRoomNumber()==room.getRoomNumber())&&(HotelDatabase.reservations.get(i)).checkInDate.equals(checkIn))
{throw new InvalidDateException("Error: Room #" + room.getRoomNumber() +
        " is already reserved for " + checkIn);}
        }

        this.guest=guest;
    this.room=room;
    this.checkInDate=checkIn;
    this.checkOutDate=checkOut;

   }
    public boolean isCancelled() {
        return this.reservationStatus == ReservationStatus.CANCELLED;
    }

    public ReservationStatus getReservationStatus() {
        return reservationStatus;
    }
    public Room getRoom(){
       if(this.room==null){
        throw new NullPointerException("There isn't a room assigned to this reservation");

       }


        return this.room;
}
public Guest getGuest(){
       return guest;
}

public LocalDate getCheckInDate() {
        if(checkInDate==null){
            return LocalDate.now();
        }


        return checkInDate;
    }
    public LocalDate getCheckOutDate(){
        if(checkOutDate==null){
            return LocalDate.now().plusDays(3);
        }
       return checkOutDate;
    }
    public void setGuest(Guest guest){
        this.guest=guest;
    }
    public void setRoom(Room room){
        this.room=room;
    }
    public void setCheckInDate(LocalDate date) throws InvalidDateException{
LocalDate today=LocalDate.now();
if(today.isBefore(date)){
        this.checkInDate=date;
System.out.println("Check in date is changed");
}
else  throw new InvalidDateException("\"Check out Date cant be changed. The check-in date has already arrived or passed.\"");

    }
    public void setCheckOutDate(LocalDate date) throws InvalidDateException {
        LocalDate today = LocalDate.now();
        if (today.isBefore(date)) {
            this.checkOutDate = date;
            System.out.println("Check out date has changed");
        } else throw new InvalidDateException("\"Check out Date cant be changed. The check-in date has already arrived or passed.\"");
    }
    public boolean isCheckedIn() {
        return checkedIn;
    }

    public boolean isCheckedOut() {
        return checkedOut;
    }

    public void setCheckedIn(boolean checkedIn) {
        this.checkedIn = checkedIn;
    }

    public void setCheckedOut(boolean checkedOut) {
        this.checkedOut = checkedOut;
    }

    public void setReservationStatus(ReservationStatus status){
     this.reservationStatus=status;
    }

    public Invoice getInvoice() {
        return invoice;
    }
    public void cancelReservation() throws InvalidDateException{
        LocalDate today=LocalDate.now();
        if(today.isBefore(checkInDate)){
        this.setReservationStatus(ReservationStatus.CANCELLED);
 }
        else throw new InvalidDateException("This reservation cant be cancelled. The checkin date has already arrived or passed  ");
    }
    @Override
public String toString(){


       return "Room number: "+room.getRoomNumber()+"\t checkin date: "+checkInDate+"\t checkout date: "+checkOutDate+"\t Reservation status: "+reservationStatus;


}

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

    public boolean isPaid() {
        return isPaid;
    }

    public void setPaid(boolean paid) {
        isPaid = paid;
    }
}



