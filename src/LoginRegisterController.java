import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import models.*;
import exceptions.*;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

//Controller for the Login / Register screen.
//Handles guest login, staff login, guest registration
public class LoginRegisterController implements Initializable {

    //Login Tab Fields
    @FXML private TextField loginUsername;
    @FXML private PasswordField loginPassword;
    @FXML private TextField staffUsername;
    @FXML private PasswordField staffPassword;
    @FXML private Label loginStatusLabel;

    //Guest Register Tab Fields
    @FXML private TextField regUsername;
    @FXML private PasswordField regPassword;
    @FXML private DatePicker regDob;
    @FXML private ComboBox<Gender> regGender;
    @FXML private TextField regBalance;
    @FXML private TextField regAddress;
    @FXML private TextField regPreferences;
    @FXML private Label registerStatusLabel;
    @FXML private TabPane tabPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        regGender.getItems().addAll(Gender.values());
        loginStatusLabel.setText("");
        registerStatusLabel.setText("");
    }
    //Handles guest login button click.
    @FXML
    private void handleGuestLogin() {
        String username = loginUsername.getText().trim();
        String password = loginPassword.getText();

        if (username.isEmpty() || password.isEmpty()) {
            loginStatusLabel.setText("Please enter both username and password.");
            loginStatusLabel.getStyleClass().setAll("label", "status-error");
            return;
        }
        try {
            Guest guest = Guest.login(username, password);
            SceneNavigator.navigateTo("GuestDashboard.fxml", guest);
        } catch (InvalidCredentialException e) {
            loginStatusLabel.setText(e.getMessage());
            loginStatusLabel.getStyleClass().setAll("label", "status-error");
        }
    }
    //Handles staff login
    @FXML
    private void handleStaffLogin() {
        String username = staffUsername.getText().trim();
        String password = staffPassword.getText();

        if (username.isEmpty() || password.isEmpty()) {
            loginStatusLabel.setText("Please enter both staff username and password.");
            loginStatusLabel.getStyleClass().setAll("label", "status-error");
            return;
        }
        try {
            Staff staffMember = Staff.login(username, password);

            // Navigate based on role
            if (staffMember instanceof Admin) {
                SceneNavigator.navigateToStaff("AdminDashboard.fxml", staffMember);
            } else if (staffMember instanceof Receptionist) {
                SceneNavigator.navigateToStaff("ReceptionistDashboard.fxml", staffMember);
            }
        } catch (InvalidCredentialException e) {
            loginStatusLabel.setText(e.getMessage());
            loginStatusLabel.getStyleClass().setAll("label", "status-error");
        }
    }
    //Handles new guest registration.
    @FXML
    private void handleRegister() {
        String username = regUsername.getText().trim();
        String password = regPassword.getText();
        LocalDate dob = regDob.getValue();
        Gender gender = regGender.getValue();
        String balanceStr = regBalance.getText().trim();
        String address = regAddress.getText().trim();
        String preferences = regPreferences.getText().trim();

        if (username.isEmpty() || password.isEmpty() || dob == null || gender == null
                || balanceStr.isEmpty() || address.isEmpty()) {
            registerStatusLabel.setText("Please fill in all required fields.");
            registerStatusLabel.getStyleClass().setAll("label", "status-error");
            return;
        }
        double balance;
        try {
            balance = Double.parseDouble(balanceStr);
            if (balance < 0) {
                registerStatusLabel.setText("Balance cannot be negative.");
                registerStatusLabel.getStyleClass().setAll("label", "status-error");
                return;
            }
        } catch (NumberFormatException e) {
            registerStatusLabel.setText("Invalid balance amount. Enter a number.");
            registerStatusLabel.getStyleClass().setAll("label", "status-error");
            return;
        }
        try {
            Guest newGuest = Guest.register(username, password, dob, gender, balance, address, preferences);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Registration Successful");
            alert.setHeaderText("Welcome, " + newGuest.getUsername() + "!");
            alert.setContentText("Your account has been created. You can now login.");
            alert.showAndWait();

            registerStatusLabel.setText("Registration successful! Switch to Login tab.");
            registerStatusLabel.getStyleClass().setAll("label", "status-success");

            regUsername.clear(); regPassword.clear(); regDob.setValue(null);
            regGender.setValue(null); regBalance.clear(); regAddress.clear(); regPreferences.clear();
            tabPane.getSelectionModel().select(0);

        } catch (WeakPwordException | InvalidDateException e) {
            registerStatusLabel.setText(e.getMessage());
            registerStatusLabel.getStyleClass().setAll("label", "status-error");
        }
    }
}