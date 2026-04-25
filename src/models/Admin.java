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
        ValidationUtil.validatePassword(password);
        ValidationUtil.validateDateOfBirth(dateOfBirth);
        ValidationUtil.validateUsername(username);
        super(username, password, dateOfBirth, Role.ADMIN, workingHours);
    }

    @Override
    public void add() {
        Scanner sc = new Scanner(System.in);
        System.out.println("\n--- What would you like to ADD? ---");
        System.out.println("1. New Room");
        System.out.println("2. New Amenity");
        System.out.print("Select an option (1-2): ");
        int choice = sc.nextInt();

        switch (choice) {
            case 1: addRoom(); break;
            case 2: addAmenity(); break;
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

        // Validation Check: Does this room already exist?
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

        // Safe array access
        if (typeChoice > 0 && typeChoice <= HotelDatabase.roomTypes.size()) {
            RoomType selectedType = HotelDatabase.roomTypes.get(typeChoice - 1);
            Room newRoom = new Room(roomNum, selectedType);

            HotelDatabase.rooms.add(newRoom);
            System.out.println("Success: Room " + roomNum + " added to the database as a " + selectedType.getTypeName() + ".");
        } else {
            System.out.println("Error: Invalid room type selection. Room creation failed.");
        }
    }

    // ADD ROOM TYPE
    private void addRoomType() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\n--- Add New Room Type ---");
        System.out.print("Enter Type Name (e.g., Penthouse): ");
        String name = scanner.next();

        // Prevent duplicate Room Types (ignoring uppercase/lowercase)
        for (RoomType rt : HotelDatabase.roomTypes) {
            if (rt.getTypeName().equalsIgnoreCase(name)) {
                System.out.println("Error: Room Type '" + name + "' already exists in the system!");
                return; // Early exit
            }
        }

        System.out.print("Enter Base Price per Night: $");
        double price = scanner.nextDouble();

        // Prevent negative prices
        if (price < 0) {
            System.out.println("Error: Price cannot be negative. Creation failed.");
            return;
        }

        RoomType newType = new RoomType(name, price);
        HotelDatabase.roomTypes.add(newType);
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
    // ADD AMENITY
    private void addAmenity() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\n--- Add New Amenity ---");
        System.out.print("Enter Amenity Name (e.g., Spa): ");
        String name = scanner.nextLine();

        // Prevent duplicate Amenities
        for (Amenity a : HotelDatabase.allAmenities) {
            if (a.getName().equalsIgnoreCase(name)) {
                System.out.println("Error: Amenity '" + name + "' already exists in the system!");
                return; // Early exit
            }
        }

        System.out.print("Enter Amenity Price: $");
        double price = scanner.nextDouble();
scanner.nextLine();
        // Prevent negative prices
        if (price < 0) {
            System.out.println("Error: Price cannot be negative. Creation failed.");
            return;
        }

        Amenity newAmenity = new Amenity(name, price);
        HotelDatabase.allAmenities.add(newAmenity);
        System.out.println("Success: Amenity '" + name + "' added to the system!");
    }

    // UPDATE ROOM
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
            // Find the room
            Room roomToUpdate = null;
            for (Room r : HotelDatabase.rooms) {
                if (r.getRoomNumber() == num) {
                    roomToUpdate = r;
                    break;
                }
            }

            if (roomToUpdate == null) {
                System.out.println("Error: Room #" + num + " not found in the system.");
                return;
            }

            // State Visibility
            System.out.println("Room found! Current Type: " + roomToUpdate.getType().getTypeName());

            // Dynamic Menu for new types
            System.out.println("Select a new Room Type:");
            for (int i = 0; i < HotelDatabase.roomTypes.size(); i++) {
                System.out.println((i + 1) + ". " + HotelDatabase.roomTypes.get(i).getTypeName());
            }
            System.out.print("Choice: ");
            int choice = sc.nextInt();

            // Safe Array Access & Redundancy Check
            if (choice > 0 && choice <= HotelDatabase.roomTypes.size()) {
                RoomType newType = HotelDatabase.roomTypes.get(choice - 1);

                if (roomToUpdate.getType() == newType) {
                    System.out.println("Notice: Room #" + num + " is already a " + newType.getTypeName() + ". No changes made.");
                } else {
                    roomToUpdate.setType(newType);
                    System.out.println("Success: Room #" + num + " updated to " + newType.getTypeName() + ".");
                }
            } else {
                System.out.println("Error: Invalid selection. Update cancelled.");
            }  break; }
    case 2: {  addAmenityToAROOM(num); break;  }

    default :{   System.out.println("Invalid choice.");
        System.out.println("Returning to main menu....");
        }
    }}

    // UPDATE ROOM TYPE
    private void updateRoomType() {
        Scanner sc = new Scanner(System.in);
        System.out.println("\n--- Update Room Type Price ---");
        System.out.print("Enter the Name of the Room Type to update (e.g., Suite): ");
        String name = sc.next();

        // Find the Room Type (Case Insensitive)
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

        // Show current state and ask for update
        System.out.println("Found '" + typeToUpdate.getTypeName() + "'. Current Price: $" + typeToUpdate.getBasePrice());
        System.out.print("Enter NEW Base Price: $");
        double newPrice = sc.nextDouble();

        if (newPrice < 0) {
            System.out.println("Error: Price cannot be negative. Update cancelled.");
        } else if (newPrice == typeToUpdate.getBasePrice()) {
            System.out.println("Notice: Price is exactly the same. No changes made.");
        } else {
            typeToUpdate.setBasePrice(newPrice);
            System.out.println("Success: '" + typeToUpdate.getTypeName() + "' price updated to $" + newPrice);
        }
    }

    // UPDATE AMENITY
    private void updateAmenity() {
        Scanner sc = new Scanner(System.in);
        System.out.println("\n--- Update Amenity Price ---");
        System.out.print("Enter the Name of the Amenity to update: ");
        String name = sc.next();

        // Find the Amenity
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

        // Show current state and ask for update
        System.out.println("Found '" + amenityToUpdate.getName() + "'. Current Price: $" + amenityToUpdate.getPrice());
        System.out.print("Enter NEW Price: $");
        double newPrice = sc.nextDouble();

        if (newPrice < 0) {
            System.out.println("Error: Price cannot be negative. Update cancelled.");
        } else {
            amenityToUpdate.setPrice(newPrice);
            System.out.println("Success: '" + amenityToUpdate.getName() + "' price updated to $" + newPrice);
        }
    }

    // DELETE ROOM
    private void deleteRoom() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\n--- Delete Room ---");
        System.out.print("Enter Room Number to remove: ");
        int roomNum = scanner.nextInt();

        // Find the room
        Room roomToRemove = null;
        for (Room r : HotelDatabase.rooms) {
            if (r.getRoomNumber() == roomNum) {
                roomToRemove = r;
                break;
            }
        }

        // Safely remove it
        if (roomToRemove != null) {
            HotelDatabase.rooms.remove(roomToRemove);
            System.out.println("Success: Room #" + roomNum + " has been permanently deleted.");
        } else {
            System.out.println("Error: Room #" + roomNum + " not found.");
        }
    }

    // DELETE ROOM TYPE
    private void deleteRoomType() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\n--- Delete Room Type ---");
        System.out.print("Enter the Name of the Room Type to delete (e.g., Suite): ");
        String name = scanner.next();

        // Find the Room Type
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

        //
        // We cannot delete a Room Type if a Room is currently using it
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
            HotelDatabase.roomTypes.remove(typeToRemove);
            System.out.println("Success: Room Type '" + typeToRemove.getTypeName() + "' has been deleted.");
        }
    }

    // DELETE AMENITY
    private void deleteAmenity() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\n--- Delete Amenity ---");
        System.out.print("Enter the Name of the Amenity to delete: ");
        String name = scanner.next();

        // Find the Amenity in the master list
        Amenity amenityToRemove = null;
        for (Amenity a : HotelDatabase.allAmenities) {
            if (a.getName().equalsIgnoreCase(name)) {
                amenityToRemove = a;
                break;
            }
        }

        // Process the Deletion
        if (amenityToRemove != null) {
            // Step A: Remove from the master database
            HotelDatabase.allAmenities.remove(amenityToRemove);

            // (Remove from all rooms)
            int affectedRooms = 0;
            for (Room r : HotelDatabase.rooms) {
                // If the room has this amenity, remove it!
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

    // VIEW METHODS
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
public  void resgisterStaff() throws InvalidDateException ,WeakPwordException {
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
    HotelDatabase.staff.add(new Admin(username,pass,dateofBirth,hours));
    System.out.println("Admin: "+username+" is added successfully to hotel system");
    return;
}
if(role.equals(Role.RECEPTIONIST)){
    HotelDatabase.staff.add(new Receptionist(username,pass,dateofBirth,hours));
    System.out.println("Receptionist: "+username+" is added successfully to hotel system");
    return;
}
 System.out.println("Unable to register new staff");
return;


}


}
