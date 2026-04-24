import database.HotelDatabase;
import exceptions.InvalidCredentialException;
import exceptions.InvalidDateException;
import exceptions.WeakPwordException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import models.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.ConsoleHandler;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {

        Parent root = FXMLLoader.load(getClass().getResource("LoginRegister.fxml"));
        Scene scene = new Scene(root);
        Image icon = new Image("hotel.png");
        primaryStage.getIcons().add(icon);
        primaryStage.setTitle("Hotel Reservation System");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) throws InvalidDateException, WeakPwordException, InvalidCredentialException {
        // 1. Boot up the database (This MUST be the first line of the whole project)
        HotelDatabase.initialize();
        launch(args);
        boolean exitt=false;
        Scanner input = new Scanner(System.in);
        System.out.println("--- Welcome to the Desktop Hotel Reservation System ---");
        while (!exitt) {
            System.out.println("\n1. Login as Guest");
            System.out.println("2. Login as Staff");
            System.out.println("3. Register New Guest");
            System.out.println("4. Exit");
            System.out.print("Select an option (1-4): ");
            switch (getValidIntInput(input, 1, 4)) {
                case 1:{
                    boolean loggedIn = false; // Flag to track success
                    Guest guest=null;
                    while (!loggedIn) {
                        System.out.println("Enter Username: ");
                        String username = input.next();
                        System.out.println("Enter Password: ");
                        String password = input.next();
                        try {
                            // Attempt login
                            guest=Guest.login(username, password);

                            // If the line above doesn't throw an exception, login is successful
                            loggedIn = true;

                        } catch (InvalidCredentialException ex) {
                            // If an exception is caught, the loop runs again
                            System.out.println("An error occurred: " + ex.getMessage());
                            System.out.println("Please try again.");
                        }
                    }
                    boolean exitGuest=false;
                    while(!exitGuest){
                        System.out.println("\n--- Guest Menu ---");
                        System.out.println("1. Search & Book a Room");
                        System.out.println("2. View My Reservations");
                        System.out.println("3. Cancel a Reservation");
                        System.out.println("4. Checkout & Pay");
                        System.out.println("5. Exit ");
                        System.out.println("Select an option (1-5): ");
                        switch (getValidIntInput(input, 1, 5)){
                            case 1: {
                                boolean searchSuccessful = false;
                                List<Room> availableRooms = null;
                                LocalDate checkIn = null;
                                LocalDate checkOut = null;
                                while (!searchSuccessful) {
                                    try {
                                        System.out.println("Enter wanted checkin date (e.g., 2026-04-26):");
                                        String checkInStr = input.next();
                                        checkIn = LocalDate.parse(checkInStr);
                                        System.out.println("Enter wanted checkout date:");
                                        String checkOutStr = input.next();
                                        checkOut = LocalDate.parse(checkOutStr);

                                    System.out.println("Enter Room type (SINGLE, DOUBLE, SUITE):");
                                    String typeStr = input.next().toUpperCase();
                                    Roomtypee type = Roomtypee.valueOf(typeStr);

                                    System.out.println("Enter your maximum budget per one night:");
                                    double maxprice = input.nextDouble();

                                    availableRooms = Guest.searchAvailableRooms(checkIn, checkOut, type, maxprice);
                                    searchSuccessful = true;

                                } catch (DateTimeParseException e) {
                                    System.out.println("Error: Invalid date format. Please use YYYY-MM-DD.");
                                } catch (IllegalArgumentException e) {
                                    System.out.println("Error: That room type does not exist.");
                                } catch (InvalidDateException e) {
                                    System.out.println("Error: " + e.getMessage());
                                }
                            }

                            if (availableRooms == null || availableRooms.isEmpty()) {
                                System.out.println("No rooms matched this description.");
                                break;
                            }

                            System.out.println("\n--- Available Rooms found ---");
                            for (int i = 0; i < availableRooms.size(); i++) {
                                System.out.println(i + 1 + ". " + availableRooms.get(i));
                            }
                            System.out.print("\nWhich room do you want to book: ");
                            int choice = input.nextInt();

                            if (choice < 1 || choice > availableRooms.size()) {
                                System.out.println("Invalid selection.");
                                break;
                            }
                            Room selectedRoom = availableRooms.get(choice - 1);

                            try {
                                // 1. Create the reservation
                                Reservation newRes = guest.makeReservation(selectedRoom, checkIn, checkOut);
                                System.out.println("Room reserved successfully!");

                                // 2. AMENITY SELECTION LOGIC
                                System.out.print("\nWould you like to add extra amenities? (y/n): ");
                                if (input.next().equalsIgnoreCase("y")) {
                                    boolean adding = true;
                                    while (adding) {
                                        System.out.println("\n--- Extra Amenities Menu ---");
                                        for (int i = 0; i < HotelDatabase.allAmenities.size(); i++) {
                                            Amenity a = HotelDatabase.allAmenities.get(i);
                                            System.out.println((i + 1) + ". " + a.getName() + " ($" + a.getPrice() + "/night)");
                                        }
                                        System.out.print("Select amenity number (or 0 to finish): ");
                                        int amChoice = input.nextInt();

                                        if (amChoice == 0) {
                                            adding = false;
                                        } else if (amChoice > 0 && amChoice <= HotelDatabase.allAmenities.size()) {
                                            Amenity selected = HotelDatabase.allAmenities.get(amChoice - 1);
                                            newRes.addChosenAmenity(selected); // Link to the reservation
                                            System.out.println(">> " + selected.getName() + " added to your stay.");
                                        } else {
                                            System.out.println("Invalid choice.");
                                        }
                                    }
                                }

                                // 3. Generate the invoice immediately so the guest sees the total
                                Invoice inv = new Invoice(newRes);
                                newRes.setInvoice(inv);
                                System.out.println("\nBooking Process Complete!");
                                System.out.println("Total Estimate (including extras & tax): $" + String.format("%.2f", inv.calculateTotal()));

                            } catch (InvalidDateException e) {
                                System.out.println("Error: " + e.getMessage());
                            }
                            break;
                        }
                                 case 2: {
                                   List<Reservation> myReservations=guest.viewReservations();
                            System.out.println("---My reservations---");
                               if(myReservations.isEmpty()){
                                 System.out.println("No reservations found.");
                                 }
                               for(int i=0;i<myReservations.size();i++){
                                  System.out.println(i+1+" "+myReservations.get(i));
                                  }

                            break;
                        }


                        case 3: {

                            List<Reservation> myReservations=guest.viewReservations();
                            System.out.println("---My reservations---");
                            if(myReservations.isEmpty()){
                                System.out.println("No reservations found.");
                                break;
                            }
                            for(int i=0;i<myReservations.size();i++){
                                System.out.println(i+1+" "+myReservations.get(i));
                            }

                                System.out.println("Enter the number of reservation you want to cancel: ");
                                 int choice = input.nextInt();
                            if (choice < 1 || choice > myReservations.size()) {
                                System.out.println("Invalid selection. Must be 1-" + myReservations.size());
                                break;
                            }
                                 guest.cancelReservation(myReservations.get(--choice));

                              break;                }
                        case 4:{ List<Reservation> myReservations=guest.viewReservations();
                            System.out.println("---My reservations---");
                            if(myReservations.isEmpty()){
                                System.out.println("No reservations found.");
                                break;
                            }
                            for(int i=0;i<myReservations.size();i++){
                                System.out.println(i+1+" "+myReservations.get(i));
                            }

                            System.out.println("Enter the number of reservation you want to pay for : ");
int choice= input.nextInt();
if (choice < 1 || choice > myReservations.size()) {
    System.out.println("Invalid selection. Must be 1-" + myReservations.size());
    break;
                            }
if(myReservations.get(choice-1).isPaid()){

    System.out.println("This reservation is already paid");
    System.out.println(myReservations.get(choice-1).getInvoice());
    break;
}
try{System.out.println(guest.checkout(myReservations.get(--choice)));}
catch(Exception e ){
    System.out.println(e.getMessage());
}

      break; }


                        case 5:{ System.out.println("Returning to main menu......");
                            exitGuest=true; break;}

                        default:{  System.out.println("Invalid input"); break;
                        }

                    }

                }





             break;   }

                case 2: {
                    boolean exitstaff = false;
                    while (!exitstaff) {
                        System.out.println("\n--- Staff Portal ---");
                        System.out.println("1. Login as Admin ");
                        System.out.println("2. Login as Receptionist ");
                        System.out.println("3. Return to main menu");
                        System.out.print("Select your testing role (1-3): ");

                        int roleChoice =  getValidIntInput(input, 1, 3);

                        // ADMIN
                        switch(roleChoice){
                    case 1:    { boolean adminRunning = false;
                            Admin mainAdmin = null;
                        String pass=null;
                        String username=null;
                            boolean loggedIn=false;
                            while(!loggedIn) {
                                System.out.println("Enter username: ");
                                username = input.next();
                                System.out.println("Enter password: ");
                                 pass = input.next();
                                try {
                                    if (Staff.login(username, pass) instanceof Receptionist) {
                                        System.out.println("You are not an admin");
break;
                          }
                                    mainAdmin=(Admin)Staff.login(username,pass);
loggedIn=true;
adminRunning=true;
System.out.println("Log in successful: "+mainAdmin.getUsername());
                                } catch (InvalidCredentialException e) {
                                    System.out.println(e.getMessage());
                                    System.out.println("Try again");
adminRunning=false;


                                } catch (Exception e) {

                                    System.out.println("You are not an admin");
                                    break;
                                }

                            }



                            while (adminRunning) {

                                System.out.println("\n--- ADMIN DASHBOARD ---");
                                System.out.println("1. View Data");
                                System.out.println("2. Add Data");
                                System.out.println("3. Update Data");
                                System.out.println("4. Delete Data");
                                System.out.println("5. Register new staff");
                                System.out.println("6. Logout");
                                System.out.print("Select an option (1-6): ");

                                switch (getValidIntInput(input, 1, 6)) {
                                    case 1:
                                        mainAdmin.viewAll();
                                        break;
                                    case 2:
                                        mainAdmin.add();
                                        break;
                                    case 3:
                                        mainAdmin.update();
                                        break;
                                    case 4:
                                        mainAdmin.delete();
                                        break;
                                    case 5:{
                                        try {
                                            mainAdmin.resgisterStaff();

                                        } catch (InvalidDateException e) {
                                            System.out.println(e.getMessage());
                                        } catch (WeakPwordException e) {
                                            System.out.println(e.getMessage());
                                        } catch (Exception e) {
                                            System.out.println(e.getMessage());
                                        }





                                 break;   }
                                    case 6:
                                        System.out.println("Logging Admin out...");
                                        adminRunning = false;
                                        break;
                                    default:
                                        System.out.println("Invalid choice.");
                                        break;
                                }
                            }
                   break;     } case 2: {
                                boolean recRunning = false;
                                boolean loggedIn = false;
                                Receptionist frontDesk = null;
                                while (!loggedIn) {
                                    System.out.println("Enter username: ");
                                    String username = input.next();
                                    System.out.println("Enter password: ");
                                    String pass = input.next();
                                    try {
                                        if (Staff.login(username, pass) instanceof Admin) {
                                            System.out.println("You are not a receptionist.");
                                            recRunning=false;
                                            break;
                                        }
                                        frontDesk = (Receptionist) Staff.login(username, pass);
                                        loggedIn = true;
                                        recRunning=true;
                                        System.out.println("Log in successful: "+frontDesk.getUsername());

                                    }
                                    catch (InvalidCredentialException e){
                                        System.out.println(e.getMessage());
                                        System.out.println("Try again");
                                    }
                                }



                            while (recRunning) {
                                System.out.println("\n--- FRONT DESK DASHBOARD ---");
                                System.out.println("1. View All Guests");
                                System.out.println("2. View All Rooms");
                                System.out.println("3. View All Reservations");
                                System.out.println("4. View All Invoices");
                                System.out.println("5. Check-In a Guest");
                                System.out.println("6. Check-Out a Guest & Process Payment");
                                System.out.println("7. Update Reservation Status");
                                System.out.println("8. Finalize Expired Reservations");
                                System.out.println("9. Logout");
                                System.out.print("Select an option (1-9): ");

                                switch (getValidIntInput(input, 1, 9)) {
                                    case 1:
                                        frontDesk.viewAllGuests();
                                        break;
                                    case 2:
                                        frontDesk.viewAllRooms();
                                        break;
                                    case 3:
                                        frontDesk.viewAllReservations();
                                        break;
                                    case 4:
                                        frontDesk.viewAllInvoices();
                                        break;

                                    // CHECK-IN LOGIC
                                    case 5: {
                                        System.out.print("\nEnter Guest Username to Check-In: ");
                                        String searchName = input.next();

                                        // Collect ALL active reservations for this guest
                                        List<Reservation> guestReservations = new ArrayList<>();
                                        for (Reservation r : HotelDatabase.reservations) {
                                            if (r.getGuest().getUsername().equalsIgnoreCase(searchName) && !r.isCancelled()) {
                                                guestReservations.add(r);
                                            }
                                        }

                                        if (guestReservations.isEmpty()) {
                                            System.out.println("Error: No active reservation found for username '" + searchName + "'.");
                                            break;
                                        }

                                        // If only one reservation, use it directly
                                        if (guestReservations.size() == 1) {
                                            frontDesk.checkIn(guestReservations.get(0));
                                            break;
                                        }

                                        // Multiple reservations — show numbered list
                                        System.out.println("\n--- Active Reservations for " + searchName + " ---");
                                        for (int i = 0; i < guestReservations.size(); i++) {
                                            Reservation res = guestReservations.get(i);
                                            System.out.println((i + 1) + ". Room #" + res.getRoom().getRoomNumber()
                                                    + " | " + res.getCheckInDate() + " to " + res.getCheckOutDate()
                                                    + " | Status: " + res.getReservationStatus());
                                        }
                                        System.out.print("\nSelect reservation number to check in (0 to cancel): ");

                                        int choice = getValidIntInput(input, 0, guestReservations.size());
                                        if (choice == 0) {
                                            System.out.println("Check-in cancelled.");
                                            break;
                                        }

                                        frontDesk.checkIn(guestReservations.get(choice - 1));
                                        break;
                                    }

                                    // CHECK-OUT LOGIC
                                    case 6: {
                                        System.out.print("\nEnter Guest Username to Check-Out: ");
                                        String searchName = input.next();

                                        // Collect ALL checked-in, not-checked-out reservations
                                        List<Reservation> guestReservations = new ArrayList<>();
                                        for (Reservation r : HotelDatabase.reservations) {
                                            if (r.getGuest().getUsername().equalsIgnoreCase(searchName)
                                                    && r.isCheckedIn()
                                                    && !r.isPaid()) {
                                                guestReservations.add(r);
                                            }
                                        }

                                        if (guestReservations.isEmpty()) {
                                            System.out.println("Error: Guest '" + searchName + "' is not currently checked in.");
                                            break;
                                        }

                                        if (guestReservations.size() == 1) {
                                            Invoice generatedInvoice = frontDesk.checkOut(guestReservations.get(0));
                                            if (generatedInvoice != null) {
                                                System.out.print("\nEnter amount paid by guest: $");
                                                double payment = input.nextDouble();
                                                frontDesk.processCheckoutPayment(generatedInvoice, payment);
                                            }
                                            break;
                                        }

                                        // Multiple reservations — show list
                                        System.out.println("\n--- Checked-In Reservations for " + searchName + " ---");
                                        for (int i = 0; i < guestReservations.size(); i++) {
                                            Reservation res = guestReservations.get(i);
                                            System.out.println((i + 1) + ". Room #" + res.getRoom().getRoomNumber()
                                                    + " | " + res.getCheckInDate() + " to " + res.getCheckOutDate());
                                        }
                                        System.out.print("\nSelect reservation number to check out (0 to cancel): ");

                                        int choice = getValidIntInput(input, 0, guestReservations.size());
                                        if (choice == 0) {
                                            System.out.println("Check-out cancelled.");
                                            break;
                                        }

                                        Invoice generatedInvoice = frontDesk.checkOut(guestReservations.get(choice - 1));
                                        if (generatedInvoice != null) {
                                            System.out.print("\nEnter amount paid by guest: $");
                                            double payment = input.nextDouble();
                                            frontDesk.processCheckoutPayment(generatedInvoice, payment);
                                        }
                                        break;
                                    }
                                    case 7:
                                        frontDesk.manualStatusUpdateMenu();
                                        break;
                                    case 8:
                                        frontDesk.finalizeCompletedReservations();
                                        break;
                                    case 9:
                                        System.out.println("Logging Receptionist out...");
                                        recRunning = false;
                                        break;
                                    default:
                                        System.out.println("Invalid choice.");
                                }
                            }
                     break;   } case 3: {
                            System.out.println("Returning to main menu....");
                            exitstaff=true; break;  }

                            default: {
                            System.out.println("Invalid role selected. Returning to main menu.");
                        }
                        break;

                        }
                  break;  }
                 break; }


                case 3: {
           boolean regsucc=false;
     while(!regsucc) {
         System.out.println("Enter username (must be 3+ characters): ");
         String username = input.next();
         System.out.println("Enter password (Must contain at least 8 characters,one uppercase,one lowercase,one digit,one special char):  ");
         String pass = input.next();
         System.out.println("Enter Date of birth (eg. 2007-05-05): ");
         String birth = input.next();
         LocalDate dateOfBirth = LocalDate.parse(birth);
         System.out.println("Enter your address: ");
         String address = input.next();
         System.out.println("Enter your balance: ");
         double balance = input.nextDouble();
         input.nextLine();
         System.out.println("Enter your room prefrences: ");
         String pref = input.nextLine();
         System.out.println("Enter Gender (MAlE/FEMALE): ");
         String gen= input.next().toUpperCase();
         Gender gender=Gender.valueOf(gen);
         try { Guest.register(username,pass,dateOfBirth,gender,balance,address,pref);
          regsucc=true;
          break;
         }
         catch (WeakPwordException e){
             System.out.println(e.getMessage());
         } catch (InvalidDateException e) {
             System.out.println(e.getMessage());
         }
     }     break; }

                case 4: {
                    System.out.println("Shutting down the Hotel System. Goodbye!");
                    exitt = true;
                    break;
                }

                default:
                    System.out.println("Invalid option. Please choose 1-4.");
                    break;
            }
        }
    }
    private static int getValidIntInput(Scanner input, int min, int max) {
        while (true) {
            try {
                int choice = input.nextInt();
                if (choice >= min && choice <= max) return choice;
                System.out.print("Invalid range. Try again: ");
            } catch (Exception e) {
                System.out.print("Invalid input. Enter number: ");
                input.next(); // clear invalid input
            }
        }
    }
}