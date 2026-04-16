import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import models.Guest;
import exceptions.InvalidCredentialException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML
    private TextField username;
    @FXML
    private PasswordField password;
    @FXML
    private Label statusLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Clear status on startup
        statusLabel.setText("");
    }

    @FXML
    private void handleLogin() {
        String username1 = username.getText().trim();
        String password1 = password.getText();

        // Basic empty-field validation
        if (username1.isEmpty() || password1.isEmpty()) {
            statusLabel.setText("Please enter both username and password.");
            return;
        }

        try {
            Guest guest = Guest.login(username1, password1);
            // Login succeeded
            statusLabel.setText("Welcome, " + guest.getUsername() + "! Login successful.");
        } catch (InvalidCredentialException e) {
            // Login failed
            statusLabel.setText(e.getMessage());
        }
    }
}
