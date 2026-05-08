package models;
import database.HotelDatabase;
import exceptions.InvalidCredentialException;
import exceptions.InvalidDateException;
import exceptions.WeakPwordException;
import interfaces.Manageable;
import utils.ValidationUtil;

import java.time.LocalDate;
import java.util.Scanner;

public class Admin extends Staff implements Manageable {
    public Admin(String username, String password, LocalDate dateOfBirth, int workingHours) throws WeakPwordException, InvalidDateException {
        // Validation moved to top so it fails before calling super
        super(username, password, dateOfBirth, Role.ADMIN, workingHours);
        ValidationUtil.validatePassword(password);
        ValidationUtil.validateDateOfBirth(dateOfBirth);
        ValidationUtil.validateUsername(username);
    }
    @Override
    public void add() {
        Scanner sc = new Scanner(System.in);
        System.out.println("\n--- What would you like to ADD? ---");
        System.out.println("1. New Room");
        System.out.println("2. New Amenity");
        System.out.println("3. New Room type");
        System.out.print("Select an option (1-3): ");
        int choice = sc.nextInt();
        switch (choice) {
            case 1: addRoom(); break;
            case 2: addAmenity(); break;
            case 3: addRoomType(); break;
            default: System.out.println("Invalid choice. Returning to menu.");
        }
    }
    @Override
    public void update() {
        Scanner sc = new Scanner(System.in);
        System.out.println("\n--- What would you like to UPDATE? ---");
        System.out.println("1. Update a Room");
        System.out.println("2. Update a Room Type");
        System.out.println("3. Update an Amenity");
        System.out.print("Select an option (1-3): ");
        int choice = sc.nextInt();
        switch (choice) {
            case 1: updateRoom(); break;
            case 2: updateRoomType(); break;
            case 3: updateAmenity(); break;
            default: System.out.println("Invalid choice. Returning to menu.");
        }
    }
    @Override
    public void delete() {
        Scanner sc = new Scanner(System.in);
        System.out.println("\n--- What would you like to DELETE? ---");
        System.out.println("1. Delete a Room");
        System.out.println("2. Delete a Room Type");
        System.out.println("3. Delete an Amenity");
        System.out.print("Select an option (1-3): ");
        int choice = sc.nextInt();
        switch (choice) {
            case 1: deleteRoom(); break;
            case 2: deleteRoomType(); break;
            case 3: deleteAmenity(); break;
            default: System.out.println("Invalid choice. Returning to menu.");
        }
    }
    @Override
    public void viewAll() {
        Scanner sc = new Scanner(System.in);
        System.out.println("\n--- What would you like to VIEW? ---");
        System.out.println("1. View All Rooms");
        System.out.println("2. View All Room Types");
        System.out.println("3. View All Amenities");
        System.out.println("4. View All Guests");
        System.out.print("Select an option (1-4): ");
        int choice = sc.nextInt();
        switch (choice) {
            case 1: super.viewAllRooms(); break;
            case 2: viewAllRoomTypes(); break;
            case 3: viewAllAmenities(); break;
            case 4: super.viewAllGuests(); break;
            default: System.out.println("Invalid choice. Returning to menu.");
        }
    }
    private void addRoom() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\n--- Add New Room ---");
        System.out.print("Enter Room Number: ");
        int roomNum = scanner.nextInt();

        // Validation Check if the romm exists
        for (Room r : HotelDatabase.rooms) {
            if (r.getRoomNumber() == roomNum) {
                System.out.println("Error: Room #" + roomNum + " already exists in the system! Creation failed.");
                return;
            }
        }
        // Prevent adding a room if no Room Types exist
        if (HotelDatabase.roomTypes.isEmpty()) {
            System.out.println("Error: No room types exist. Please create a Room Type first.");
            return;
        }
        // Let the Admin choose the Room Type dynamically
        System.out.println("Select a Room Type:");
        for (int i = 0; i < HotelDatabase.roomTypes.size(); i++) {
            System.out.println((i + 1) + ". " + HotelDatabase.roomTypes.get(i).getTypeName() + " ($" + HotelDatabase.roomTypes.get(i).getBasePrice() + ")");
        }
        System.out.print("Choice: ");
        int typeChoice = scanner.nextInt();
        if (typeChoice > 0 && typeChoice <= HotelDatabase.roomTypes.size()) {
            RoomType selectedType = HotelDatabase.roomTypes.get(typeChoice - 1);
            Room newRoom = new Room(roomNum, selectedType);

            HotelDatabase.addRoom(newRoom); // SYNCED WITH DB
            System.out.println("Success: Room " + roomNum + " added to the database as a " + selectedType.getTypeName() + ".");
        } else {
            System.out.println("Error: Invalid room type selection. Room creation failed.");
        }
    }
    private void addRoomType() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\n--- Add New Room Type ---");
        System.out.print("Enter Type Name (e.g., Penthouse): ");
        String name = scanner.next();

        // Prevent duplicate Room Types
        for (RoomType rt : HotelDatabase.roomTypes) {
            if (rt.getTypeName().equalsIgnoreCase(name)) {
                System.out.println("Error: Room Type '" + name + "' already exists in the system!");
                return; // Early exit
            }
        }
        System.out.print("Enter Base Price per Night: $");
        double price = scanner.nextDouble();
        if (price < 0) {
            System.out.println("Error: Price cannot be negative. Creation failed.");
            return;
        }
        RoomType newType = new RoomType(name, price);
        HotelDatabase.addRoomType(newType); // SYNCED WITH DB
        System.out.println("Success: Room Type '" + name + "' created successfully!");
    }
    private void addAmenityToAROOM(int number){
        Room selectedroom;
        for(int i=0;i<HotelDatabase.rooms.size();i++){
            if(HotelDatabase.rooms.get(i).getRoomNumber()==number){
                selectedroom=HotelDatabase.rooms.get(i);
                selectedroom.addAmenities();
                return;
            }
        }
    }
    private void addAmenity() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\n--- Add New Amenity ---");
        System.out.print("Enter Amenity Name (e.g., Spa): ");
        String name = scanner.nextLine();

        for (Amenity a : HotelDatabase.allAmenities) {
            if (a.getName().equalsIgnoreCase(name)) {
                System.out.println("Error: Amenity '" + name + "' already exists in the system!");
                return;
            }
        }
        System.out.print("Enter Amenity Price: $");
        double price = scanner.nextDouble();
        scanner.nextLine();

        if (price < 0) {
            System.out.println("Error: Price cannot be negative. Creation failed.");
            return;
        }
        Amenity newAmenity = new Amenity(name, price);
        HotelDatabase.addAmenity(newAmenity); // SYNCED WITH DB
        System.out.println("Success: Amenity '" + name + "' added to the system!");
    }
    private void updateRoom() {
        Scanner sc = new Scanner(System.in);
        System.out.println("\n--- Update Room  ---");
        System.out.print("Enter Room Number to update: ");
        int num = sc.nextInt();
        System.out.println("1. Update Room type");
        System.out.println("2. Add an amenity");
        System.out.println("Select an option (1-2): ");
        switch(sc.nextInt()) {
            case 1: {
                Room roomToUpdate = null;
                for (Room r : HotelDatabase.rooms) {
                    if (r.getRoomNumber() == num) {roomToUpdate = r;
                        break;
                    }
                }
                if (roomToUpdate == null) {
                    System.out.println("Error: Room #" + num + " not found in the system.");
                    return;
                }
                System.out.println("Room found! Current Type: " + roomToUpdate.getType().getTypeName());
                System.out.println("Select a new Room Type:");
                for (int i = 0; i < HotelDatabase.roomTypes.size(); i++) {
                    System.out.println((i + 1) + ". " + HotelDatabase.roomTypes.get(i).getTypeName());
                }
                System.out.print("Choice: ");
                int choice = sc.nextInt();
                if (choice > 0 && choice <= HotelDatabase.roomTypes.size()) {
                    RoomType newType = HotelDatabase.roomTypes.get(choice - 1);
                    if (roomToUpdate.getType() == newType) {
                        System.out.println("Notice: Room #" + num + " is already a " + newType.getTypeName() + ". No changes made.");
                    } else {
                        roomToUpdate.setType(newType);
                        HotelDatabase.updateRoom(roomToUpdate); // SYNCED WITH DB
                        System.out.println("Success: Room #" + num + " updated to " + newType.getTypeName() + ".");
                    }
                } else {
                    System.out.println("Error: Invalid selection. Update cancelled.");
                }
                break;
            }
            case 2: {
                addAmenityToAROOM(num);
                break;
            }
            default: {
                System.out.println("Invalid choice.");
                System.out.println("Returning to main menu....");
            }
        }
    }
    private void updateRoomType() {
        Scanner sc = new Scanner(System.in);
        System.out.println("\n--- Update Room Type Price ---");
        System.out.print("Enter the Name of the Room Type to update (e.g., Suite): ");
        String name = sc.next();

        RoomType typeToUpdate = null;
        for (RoomType rt : HotelDatabase.roomTypes) {
            if (rt.getTypeName().equalsIgnoreCase(name)) {
                typeToUpdate = rt;
                break;
            }
        }
        if (typeToUpdate == null) {
            System.out.println("Error: Room Type '" + name + "' not found.");
            return;
        }
        System.out.println("Found '" + typeToUpdate.getTypeName() + "'. Current Price: $" + typeToUpdate.getBasePrice());
        System.out.print("Enter NEW Base Price: $");
        double newPrice = sc.nextDouble();
        if (newPrice < 0) {
            System.out.println("Error: Price cannot be negative. Update cancelled.");
        } else if (newPrice == typeToUpdate.getBasePrice()) {
            System.out.println("Notice: Price is exactly the same. No changes made.");
        } else {
            typeToUpdate.setBasePrice(newPrice);
            HotelDatabase.updateRoomType(typeToUpdate); // SYNCED WITH DB
            System.out.println("Success: '" + typeToUpdate.getTypeName() + "' price updated to $" + newPrice);
        }
    }
    private void updateAmenity() {
        Scanner sc = new Scanner(System.in);
        System.out.println("\n--- Update Amenity Price ---");
        System.out.print("Enter the Name of the Amenity to update: ");
        String name = sc.next();
        Amenity amenityToUpdate = null;
        for (Amenity a : HotelDatabase.allAmenities) {
            if (a.getName().equalsIgnoreCase(name)) {
                amenityToUpdate = a;
                break;
            }
        }
        if (amenityToUpdate == null) {
            System.out.println("Error: Amenity '" + name + "' not found.");
            return;
        }
        System.out.println("Found '" + amenityToUpdate.getName() + "'. Current Price: $" + amenityToUpdate.getPrice());
        System.out.print("Enter NEW Price: $");
        double newPrice = sc.nextDouble();

        if (newPrice < 0) {
            System.out.println("Error: Price cannot be negative. Update cancelled.");
        } else {
            amenityToUpdate.setPrice(newPrice);
            HotelDatabase.updateAmenity(amenityToUpdate); // SYNCED WITH DB
            System.out.println("Success: '" + amenityToUpdate.getName() + "' price updated to $" + newPrice);
        }
    }
    private void deleteRoom() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\n--- Delete Room ---");
        System.out.print("Enter Room Number to remove: ");
        int roomNum = scanner.nextInt();
        Room roomToRemove = null;
        for (Room r : HotelDatabase.rooms) {
            if (r.getRoomNumber() == roomNum) {
                roomToRemove = r;
                break;
            }
        }
        if (roomToRemove != null) {
            HotelDatabase.deleteRoom(roomToRemove); // SYNCED WITH DB
            System.out.println("Success: Room #" + roomNum + " has been permanently deleted.");
        } else {
            System.out.println("Error: Room #" + roomNum + " not found.");
        }
    }
    private void deleteRoomType() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\n--- Delete Room Type ---");
        System.out.print("Enter the Name of the Room Type to delete (e.g., Suite): ");
        String name = scanner.next();
        RoomType typeToRemove = null;
        for (RoomType rt : HotelDatabase.roomTypes) {
            if (rt.getTypeName().equalsIgnoreCase(name)) {
                typeToRemove = rt;
                break;
            }
        }
        if (typeToRemove == null) {
            System.out.println("Error: Room Type '" + name + "' not found.");
            return;
        }
        boolean isUsed = false;
        for (Room r : HotelDatabase.rooms) {
            if (r.getType() == typeToRemove) {
                isUsed = true;
                break;
            }
        }
        if (isUsed) {
            System.out.println("CRITICAL ERROR: Cannot delete '" + typeToRemove.getTypeName() + "'.");
            System.out.println("There are currently rooms using this type. You must update those rooms to a different type first!");
        } else {
            HotelDatabase.deleteRoomType(typeToRemove); // SYNCED WITH DB
            System.out.println("Success: Room Type '" + typeToRemove.getTypeName() + "' has been deleted.");
        }
    }
    private void deleteAmenity() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\n--- Delete Amenity ---");
        System.out.print("Enter the Name of the Amenity to delete: ");
        String name = scanner.next();

        Amenity amenityToRemove = null;
        for (Amenity a : HotelDatabase.allAmenities) {
            if (a.getName().equalsIgnoreCase(name)) {
                amenityToRemove = a;
                break;
            }
        }
        if (amenityToRemove != null) {
            HotelDatabase.deleteAmenity(amenityToRemove); // SYNCED WITH DB
            int affectedRooms = 0;
            // Also remove from local Java lists so it updates instantly without restarting app
            for (Room r : HotelDatabase.rooms) {
                if (r.getAmenities().contains(amenityToRemove)) {
                    r.getAmenities().remove(amenityToRemove);
                    affectedRooms++;
                }
            }
            System.out.println("Success: Amenity '" + amenityToRemove.getName() + "' has been permanently deleted.");
            if (affectedRooms > 0) {
                System.out.println("Notice: This amenity was automatically removed from " + affectedRooms + " room(s) to prevent ghost billing.");
            }

        } else {
            System.out.println("Error: Amenity '" + name + "' not found.");
        }
    }
    private void viewAllRoomTypes() {
        System.out.println("\n--- All Room Types ---");
        if (HotelDatabase.roomTypes.isEmpty()) {
            System.out.println("No room types in the system.");
            return;
        }
        for (RoomType rt : HotelDatabase.roomTypes) {
            System.out.println("- " + rt.getTypeName() + " (Base Price: $" + rt.getBasePrice() + ")");
        }
    }
    private void viewAllAmenities() {
        System.out.println("\n--- All System Amenities ---");
        if (HotelDatabase.allAmenities.isEmpty()) {
            System.out.println("No amenities in the system.");
            return;
        }
        for(int i=0;i<HotelDatabase.allAmenities.size();i++){
            System.out.println("- " + HotelDatabase.allAmenities.get(i).getName() + " ($" + HotelDatabase.allAmenities.get(i).getPrice() + ")");
        }
    }
    public void resgisterStaff() throws InvalidDateException ,WeakPwordException {
        Scanner input = new Scanner(System.in);
        System.out.println("Enter username (must be at least 3 characters): ");
        String username = input.next();
        System.out.println("Enter Password (must be at least 8 characters,contains at least 1 uppercase,1 lowercase, 1 digit , 1 special char): ");
        String pass = input.next();
        System.out.println("Enter date of birth (eg. 2007-10-14): ");
        String d = input.next();
        LocalDate dateofBirth = LocalDate.parse(d);
        ValidationUtil.validateUsername(username);
        ValidationUtil.validateDateOfBirth(dateofBirth);
        ValidationUtil.validatePassword(pass);
        System.out.println("Enter Role (ADMIN/RECEPTIONIST): ");
        String r = input.next().toUpperCase();
        Role role = Role.valueOf(r);
        System.out.println("Enter number of working hours: ");
        int hours = input.nextInt();
        if(role.equals(Role.ADMIN)){
            Admin newAdmin = new Admin(username,pass,dateofBirth,hours);
            HotelDatabase.addStaff(newAdmin); // SYNCED WITH DB
            System.out.println("Admin: "+username+" is added successfully to hotel system");
            return;
        }
        if(role.equals(Role.RECEPTIONIST)){
            Receptionist newRec = new Receptionist(username,pass,dateofBirth,hours);
            HotelDatabase.addStaff(newRec); // SYNCED WITH DB
            System.out.println("Receptionist: "+username+" is added successfully to hotel system");
            return;
        }
        System.out.println("Unable to register new staff");
    }
}