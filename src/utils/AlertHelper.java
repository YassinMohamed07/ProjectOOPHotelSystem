package utils;
import javafx.scene.control.Alert;

//Shared utility for displaying alert dialogs across controllers.
//Eliminates the duplicated showAlert method from RoomBrowsingController,
//ReservationController, and CheckoutController.

public class AlertHelper {

    public static void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}