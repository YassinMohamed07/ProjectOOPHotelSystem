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

public Room getRoom(){
       return this.room;
}
public Guest getGuest(){
       return guest;
}

public LocalDate getCheckInDate() {
        return checkInDate;
    }
    public LocalDate getCheckOutDate(){
       return checkOutDate;
    }
    public void setGuest(Guest guest){
        this.guest=guest;
    }
    public void setRoom(Room room){
        this.room=room;
    }
    public void setCheckInDate(LocalDate date){
LocalDate today=LocalDate.now();
if(today.isBefore(date)){
        this.checkInDate=date;
System.out.println("Check in date is changed");
}
else System.out.println("Check in Date cant be changed. The check-in date has already arrived or passed.");
    }
    public void setCheckOutDate(LocalDate date) {
        LocalDate today = LocalDate.now();
        if (today.isBefore(date)) {
            this.checkOutDate = date;
            System.out.println("Check out date has changed");
        } else System.out.println("Check out date cant be changed. The check-out date has already arrived or passed.");
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
    public void cancelReservation(){
        LocalDate today=LocalDate.now();
        if(today.isBefore(checkInDate)){
        this.setReservationStatus(ReservationStatus.CANCELLED);
        System.out.println("Reservation is Cancelled");}
        else System.out.println("Reservation cant be cancelled. The check-in date has already arrived or passed.");
    }
public void viewReservation(){
    System.out.println("\n=== RESERVATION DETAILS ===");

    // 1. If the guest hasn't checked out yet, the invoice is null
    if (invoice == null) {
        System.out.println("Status: PENDING / STAYING");
        System.out.println("Guest UserName: " + guest.getUsername());
        System.out.println("Room No: " + room.getRoomNumber());

        RoomType roomtype = room.getType();
        System.out.println("Room Type: " + roomtype.getTypeName());

        System.out.println("Amenities in the room:");
        this.getRoom().printAmenities();
    }
    // 2. If the guest checked out, the invoice exists.
    // Just print the invoice—it already has all the info!
    else {
        System.out.println(invoice);
    }
}

}



