import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import models.Guest;
import java.io.IOException;

 //Utility class for navigating between screens using a shared StackPane root.
 //Controllers that need to receive the logged-in Guest must implement the
 //{setGuest(Guest)} method.

public class SceneNavigator {

    private static StackPane rootPane;
    //Sets the root StackPane container (called once from Main).

    public static void setRootPane(StackPane pane) {
        rootPane = pane;
    }

    //Navigate to a screen without passing data.
    public static void navigateTo(String fxmlFile) {
        navigateTo(fxmlFile, null);
    }

     //Navigate to a screen and pass the logged in Guest to the controller.
     //The controller must have a public {setGuest(Guest guest)} method.
    public static void navigateTo(String fxmlFile, Guest guest) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneNavigator.class.getResource(fxmlFile));
            Parent view = loader.load();

            // If a guest is provided, try to pass it to the controller
            if (guest != null) {
                Object controller = loader.getController();
                if (controller instanceof GuestAware) {
                    ((GuestAware) controller).setGuest(guest);
                }
            }
            rootPane.getChildren().setAll(view);
        } catch (IOException e) {
            System.err.println("Failed to load FXML: " + fxmlFile);
            e.printStackTrace();
        }
    }
}
