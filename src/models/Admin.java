package models;

import database.HotelDatabase;
import interfaces.Manageable;
import java.time.LocalDate;
import java.util.Scanner;

public class Admin extends Staff implements Manageable {
    public Admin(String username, String password, LocalDate dateOfBirth, int workingHours) {
        super(username, password, dateOfBirth, Role.ADMIN, workingHours);
    }

    @Override
    public void add() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\n--- Add New Room ---");
        System.out.print("Enter Room Number: ");
        int roomNum = scanner.nextInt();

        for (Room r : HotelDatabase.rooms) {
            if (r.getRoomNumber() == roomNum) {
                System.out.println("Error: Room #" + roomNum + " already exists in the system! Creation failed.");
                return; // This keyword instantly exits the add() method so the room is NOT created.
            }
        }

        RoomType defaultType = HotelDatabase.roomTypes.get(0);
        Room newRoom = new Room(roomNum, defaultType);

        HotelDatabase.rooms.add(newRoom);
        System.out.println("Success: Room " + roomNum + " added to the database.");
    }

    @Override
    public void update() {
        Scanner sc = new Scanner(System.in);
        System.out.println("\n--- Admin: Update Room Type ---");
        System.out.print("Enter Room Number to update: ");
        int num = sc.nextInt();

        Room roomToUpdate = null;
        for (Room r : HotelDatabase.rooms) {
            if (r.getRoomNumber() == num) {
                roomToUpdate = r;
                break;
            }
        }

        if (roomToUpdate != null) {
            System.out.println("Room found! Current Type: " + roomToUpdate.getType().getTypeName());
            System.out.println("Select new Type: 0 for Single, 1 for Suite");
            int choice = sc.nextInt();

            if (choice >= 0 && choice < HotelDatabase.roomTypes.size()) {

                RoomType newType = HotelDatabase.roomTypes.get(choice);

                if (roomToUpdate.getType() == newType) {
                    System.out.println("Notice: Room is already a " + newType.getTypeName() + ". No changes made.");
                } else {
                    roomToUpdate.setType(newType);
                    System.out.println("Success: Room " + num + " updated to " + newType.getTypeName());
                }

            } else {
                System.out.println("Error: Invalid room type selection. Update cancelled.");
            }
        } else {
            System.out.println("Error: Room number not found.");
        }
    }

    @Override
    public void delete() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\n--- Delete Room ---");
        System.out.print("Enter Room Number to remove: ");
        int roomNum = scanner.nextInt();

        // Using Abdallah's getter to find the right room
        Room roomToRemove = null;
        for (Room r : HotelDatabase.rooms) {
            if (r.getRoomNumber() == roomNum) {
                roomToRemove = r;
                break;
            }
        }

        if (roomToRemove != null) {
            HotelDatabase.rooms.remove(roomToRemove);
            System.out.println("Success: Room " + roomNum + " has been deleted.");
        } else {
            System.out.println("Error: Room " + roomNum + " not found.");
        }
    }

    @Override
    public void viewAll() {
        super.viewAllGuests();
        super.viewAllRooms();
    }
}
