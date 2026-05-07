import database.HotelDatabase;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        StackPane rootPane = new StackPane();
        SceneNavigator.setRootPane(rootPane);

        Scene scene = new Scene(rootPane, 950, 700);

        // Load CSS (wrapped in try-catch just in case the file is moved)
        try {
            scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        } catch (NullPointerException e) {
            System.out.println("Warning: style.css not found. GUI will load without custom styles.");
        }

        // Load Icon (wrapped in try-catch just in case the file is missing)
        try {
            Image icon = new Image("hotel.png");
            primaryStage.getIcons().add(icon);
        } catch (Exception e) {
            System.out.println("Warning: hotel.png icon not found.");
        }

        primaryStage.setTitle("Hotel Reservation System");
        primaryStage.setScene(scene);

        // Navigate to the first screen
        SceneNavigator.navigateTo("LoginRegister.fxml");
        primaryStage.show();
    }

    public static void main(String[] args) {
        try {
            System.out.println("Connecting to MySQL Database...");

            // 1. Boot up the database (Pulls all data from MySQL into Java)
            HotelDatabase.initialize();

            System.out.println("Database connected and data loaded successfully!");
            System.out.println("Launching User Interface...");

            // 2. Launch the JavaFX Graphical User Interface
            launch(args);

        } catch (Exception e) {
            // If the database fails to connect, it will print the error here instead of crashing silently
            System.err.println("\nCRITICAL ERROR: Failed to connect to the MySQL database.");
            System.err.println("1. Is MySQL installed and running on your computer?");
            System.err.println("2. Did you put the correct password inside HotelDatabase.java?");
            System.err.println("3. Did you run the SQL script in MySQL Workbench to create the tables?");
            System.err.println("\nError Details: ");
            e.printStackTrace();
        }
    }
}