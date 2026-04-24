import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.layout.StackPane;
import models.Guest;
import models.Staff;

import java.net.URL;

//Utility class for navigating between screens using a shared StackPane root.
//Controllers that need to receive the logged-in Guest must implement GuestAware.
//Controllers that need to receive the logged-in Staff must implement StaffAware.

public class SceneNavigator {

    private static StackPane rootPane;

    public static void setRootPane(StackPane pane) {
        rootPane = pane;
    }

    //Navigate to a screen without passing data.
    public static void navigateTo(String fxmlFile) {
        loadAndShow(fxmlFile, null, null);
    }

    //Navigate to a screen and pass the logged-in Guest to the controller.
    public static void navigateTo(String fxmlFile, Guest guest) {
        loadAndShow(fxmlFile, guest, null);
    }

    //Navigate to a screen and pass the logged-in Staff to the controller.
    public static void navigateToStaff(String fxmlFile, Staff staff) {
        loadAndShow(fxmlFile, null, staff);
    }

    //Core loading method that handles both Guest and Staff navigation.
    private static void loadAndShow(String fxmlFile, Guest guest, Staff staff) {
        try {
            URL fxmlUrl = SceneNavigator.class.getResource(fxmlFile);
            if (fxmlUrl == null) {
                System.err.println("ERROR: FXML file not found in classpath: " + fxmlFile);
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Navigation Error");
                alert.setHeaderText("Screen not found");
                alert.setContentText("Could not find: " + fxmlFile + "\nPlease rebuild the project (Build > Rebuild Project).");
                alert.showAndWait();
                return;
            }

            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent view = loader.load();
            Object controller = loader.getController();

            // Pass Guest if provided
            if (guest != null && controller instanceof GuestAware) {
                ((GuestAware) controller).setGuest(guest);
            }

            // Pass Staff if provided
            if (staff != null && controller instanceof StaffAware) {
                ((StaffAware) controller).setStaff(staff);
            }

            rootPane.getChildren().setAll(view);

        } catch (Exception e) {
            System.err.println("Failed to load FXML: " + fxmlFile);
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Navigation Error");
            alert.setHeaderText("Failed to load screen: " + fxmlFile);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }
}
