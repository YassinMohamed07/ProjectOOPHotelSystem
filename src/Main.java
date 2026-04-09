import database.HotelDatabase;
import models.Admin;
import java.time.LocalDate;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // 1. Boot up the database (This MUST be the first line of the whole project)
        HotelDatabase.initialize();

        Admin mainAdmin = new Admin("Mohamed_Admin", "securepass123", LocalDate.of(2007, 3, 8), 40);

        Scanner scanner = new Scanner(System.in);
        boolean isRunning = true;

        System.out.println("\n=================================");
        System.out.println("   WELCOME TO THE HOTEL SYSTEM");
        System.out.println("   Logged in as: " + mainAdmin.getUsername());
        System.out.println("=================================");

        while (isRunning) {
            System.out.println("\n--- ADMIN DASHBOARD ---");
            System.out.println("1. View All System Data (Rooms & Guests)");
            System.out.println("2. Add a New Room");
            System.out.println("3. Update a Room Type");
            System.out.println("4. Delete a Room");
            System.out.println("5. Exit System");
            System.out.print("Select an option (1-5): ");

            // Read what the user types in the terminal
            int choice = scanner.nextInt();

            // Route the choice to the exact methods you wrote in Admin.java
            switch (choice) {
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
                case 5:
                    System.out.println("Logging out... Goodbye!");
                    isRunning = false; // This breaks the while loop and ends the program
                    break;
                default:
                    System.out.println("Invalid choice. Please enter a number between 1 and 5.");
            }
        }

        // Always close your scanner to prevent memory leaks!
        scanner.close();
    }
}