import database.HotelDatabase;
public static void main(String[] args) {
    HotelDatabase.initialize();
    System.out.println("First Room Number: " + HotelDatabase.rooms.get(0).roomNumber);
    System.out.println("Admin Username: " + HotelDatabase.guests.get(0).username);
}