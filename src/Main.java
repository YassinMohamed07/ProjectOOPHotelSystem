import database.HotelDatabase;
import exceptions.InvalidCredentialException;
import exceptions.InvalidDateException;
import exceptions.WeakPwordException;
import models.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.ConsoleHandler;

public class Main {
    public static void main(String[] args) throws InvalidDateException, WeakPwordException, InvalidCredentialException {
        // 1. Boot up the database (This MUST be the first line of the whole project)
        HotelDatabase.initialize();
        boolean exitt=false;
        Scanner input = new Scanner(System.in);
        System.out.println("--- Welcome to the Desktop Hotel Reservation System ---");
        while (!exitt) {
            System.out.println("\n1. Login as Guest");
            System.out.println("2. Login as Admin/Receptionist");
            System.out.println("3. Register New Guest");
            System.out.println("4. Exit");
            System.out.print("Select an option: ");
            switch (input.nextInt()) {
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
                            Guest.login(username, password);

                            // If the line above doesn't throw an exception, login is successful
                            loggedIn = true;
                            guest=Guest.login(username,password);

                        } catch (InvalidCredentialException ex) {
                            // If an exception is caught, the loop runs again
                            System.out.println("An error occurred: " + ex.getMessage());
                            System.out.println("Please try again.");
                        }
                    }

                    System.out.println("\n--- Guest Menu ---");
                    System.out.println("1. Search & Book a Room");
                    System.out.println("2. View My Reservations");
                    System.out.println("3. Cancel a Reservation");
                    System.out.println("4. Checkout & Pay");
                    System.out.println("Select an option");
                    switch (input.nextInt()){
                        case 1: {
                            boolean searchSuccessful = false;
                            List<Room> availableRooms = null;
                            LocalDate checkIn=null;
                            LocalDate checkOut=null;
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


                                    // This calls your logic and prints the list
                                     availableRooms=  Guest.searchAvailableRooms(checkIn,checkOut,type,maxprice);

                                    // If we reach this line without an exception, the search worked!
                                    searchSuccessful = true;
                                    if (availableRooms.isEmpty()) {
                                        break;
                                    }

                                } catch (DateTimeParseException e) {
                                    System.out.println("Error: Invalid date format. Please use YYYY-MM-DD.");
                                } catch (IllegalArgumentException e) {
                                    System.out.println("Error: That room type does not exist.");
                                } catch (InvalidDateException e) {
                                    System.out.println("Error: " + e.getMessage());
                                }
                                }
                            System.out.println("\n--- Available Rooms found  ---");
                            if(availableRooms.isEmpty()){System.out.println("No rooms  matched this description"); break;}
                            for (int i = 0; i < availableRooms.size(); i++) {

                                // This uses the toString() you have in your Room class
                                System.out.println(i+1+ ". " + availableRooms.get(i));
                            }
                            System.out.print("\nEnter the number of the room you want to book: ");
                            int choice = input.nextInt();

                            // 3. Logic to "take" the room using the stored dates
                            if (choice >= 0 && choice <= availableRooms.size()) {
                                Room selectedRoom = availableRooms.get(choice);

                                try {
                                    // We use the 'checkIn' and 'checkOut' variables you defined at the start of Case 1
                                    guest.makeReservation(selectedRoom, checkIn, checkOut);

                                    System.out.println("Reservation successfully linked to your account!");
                                } catch (InvalidDateException e) {
                                    System.out.println("Error: " + e.getMessage());
                                }
                            } else {
                                System.out.println("Invalid selection. Returning to menu.");
                            }

                        }
                    }
                    break;
                }

                case 2: {
                    System.out.println("\n--- Staff Portal ---");
                    System.out.println("1. Login as Admin (Full CRUD)");
                    System.out.println("2. Login as Receptionist (Front Desk)");
                    System.out.print("Select your testing role (1-2): ");

                    int roleChoice = input.nextInt();

                    // ADMIN
                    if (roleChoice == 1) {
                        Admin mainAdmin = new Admin("Mohamed_Admin", "SecurePass123!", LocalDate.of(2007, 3, 8), 40);
                        System.out.println("\nBypassing login... Welcome, " + mainAdmin.getUsername() + " (ADMIN)");

                        boolean adminRunning = true;
                        while (adminRunning) {
                            System.out.println("\n--- ADMIN DASHBOARD ---");
                            System.out.println("1. View Data");
                            System.out.println("2. Add Data");
                            System.out.println("3. Update Data");
                            System.out.println("4. Delete Data");
                            System.out.println("5. Logout");
                            System.out.print("Select an option (1-5): ");

                            switch (input.nextInt()) {
                                case 1: mainAdmin.viewAll(); break;
                                case 2: mainAdmin.add(); break;
                                case 3: mainAdmin.update(); break;
                                case 4: mainAdmin.delete(); break;
                                case 5:
                                    System.out.println("Logging Admin out...");
                                    adminRunning = false;
                                    break;
                                default: System.out.println("Invalid choice.");
                            }
                        }
                    }
                    else if (roleChoice == 2) {
                        // dummy Receptionist
                        Receptionist frontDesk = new Receptionist("Yassin_Front", "SecurePass123!", LocalDate.of(2000, 5, 12), 40);
                        System.out.println("\nBypassing login... Welcome, " + frontDesk.getUsername() + " (RECEPTIONIST)");

                        boolean recRunning = true;
                        while (recRunning) {
                            System.out.println("\n--- FRONT DESK DASHBOARD ---");
                            System.out.println("1. View All Guests");
                            System.out.println("2. View All Rooms");
                            System.out.println("3. View All Reservations");
                            System.out.println("4. View All Invoices");
                            System.out.println("5. Check-In a Guest");
                            System.out.println("6. Check-Out a Guest & Process Payment");
                            System.out.println("7. Logout");
                            System.out.print("Select an option (1-7): ");

                            switch (input.nextInt()) {
                                case 1: frontDesk.viewAllGuests(); break;
                                case 2: frontDesk.viewAllRooms(); break;
                                case 3: frontDesk.viewAllReservations(); break;
                                case 4: frontDesk.viewAllInvoices(); break;

                                // CHECK-IN LOGIC
                                case 5: {
                                    System.out.print("\nEnter Guest Username to Check-In: ");
                                    String searchName = input.next();
                                    Reservation foundRes = null;

                                    // Find their reservation in the database
                                    for (Reservation r : HotelDatabase.reservations) {
                                        if (r.getGuest().getUsername().equalsIgnoreCase(searchName) && !r.isCancelled()) {
                                            foundRes = r;
                                            break;
                                        }
                                    }

                                    if (foundRes != null) {
                                        frontDesk.checkIn(foundRes);
                                    } else {
                                        System.out.println("Error: No active reservation found for username '" + searchName + "'.");
                                    }
                                    break;
                                }

                                // CHECK-OUT LOGIC
                                case 6: {
                                    System.out.print("\nEnter Guest Username to Check-Out: ");
                                    String searchName = input.next();
                                    Reservation foundRes = null;

                                    // Find the reservation where they are currently checked in
                                    for (Reservation r : HotelDatabase.reservations) {
                                        if (r.getGuest().getUsername().equalsIgnoreCase(searchName) && r.isCheckedIn() && !r.isCheckedOut()) {
                                            foundRes = r;
                                            break;
                                        }
                                    }

                                    if (foundRes != null) {
                                        // Process the checkout and generate the invoice
                                        Invoice generatedInvoice = frontDesk.checkOut(foundRes);

                                        // Process the payment using Receptionist method
                                        if (generatedInvoice != null) {
                                            System.out.print("\nEnter amount paid by guest: $");
                                            double payment = input.nextDouble();
                                            frontDesk.processCheckoutPayment(generatedInvoice, payment);
                                        }
                                    } else {
                                        System.out.println("Error: Guest '" + searchName + "' is not currently checked in.");
                                    }
                                    break;
                                }

                                case 7:
                                    System.out.println("Logging Receptionist out...");
                                    recRunning = false;
                                    break;
                                default:
                                    System.out.println("Invalid choice.");
                            }
                        }
                    }
                    else {
                        System.out.println("Invalid role selected. Returning to main menu.");
                    }
                    break;
                }

                case 3: {
                    System.out.println("\n[Registration System pending Teammate 3's logic]");
                    break;
                }

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
}