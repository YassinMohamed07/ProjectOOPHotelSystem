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

    @FXML private TextField loginUsername;
    @FXML private PasswordField loginPassword;
    @FXML private TextField staffUsername;
    @FXML private PasswordField staffPassword;
    @FXML private Label loginStatusLabel;

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
    //Handles guest login button
    @FXML
    private void handleGuestLogin() {
        String username = loginUsername.getText().trim();
        String password = loginPassword.getText();

        if (username.isEmpty() || password.isEmpty()) {
            setLoginError("Please enter both username and password.");
            return;
        }
        try {
            Guest guest = Guest.login(username, password);
            SceneNavigator.navigateTo("GuestDashboard.fxml", guest);
        } catch (InvalidCredentialException e) {
            setLoginError(e.getMessage());
        }
    }
    //Handles staff login button
    @FXML
    private void handleStaffLogin() {
        String username = staffUsername.getText().trim();
        String password = staffPassword.getText();

        if (username.isEmpty() || password.isEmpty()) {
            setLoginError("Please enter both staff username and password.");
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
            setLoginError(e.getMessage());
        }
    }
    //Handles new guest registration
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
                || balanceStr.isEmpty() || address.isEmpty()) {setRegisterError("Please fill in all required fields.");
            return;
        }
        double balance;
        try {
            balance = Double.parseDouble(balanceStr);
            if (balance < 0) {setRegisterError("Balance cannot be negative.");
                return;
            }
        } catch (NumberFormatException e) {setRegisterError("Invalid balance amount. Enter a number.");
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
            clearRegistrationForm();
            tabPane.getSelectionModel().select(0);

        } catch (WeakPwordException | InvalidDateException e) {
            setRegisterError(e.getMessage());
        }
    }
    private void setLoginError(String message) {
        loginStatusLabel.setText(message);
        loginStatusLabel.getStyleClass().setAll("label", "status-error");
    }
    private void setRegisterError(String message) {
        registerStatusLabel.setText(message);
        registerStatusLabel.getStyleClass().setAll("label", "status-error");
    }
    private void clearRegistrationForm() {
        regUsername.clear();
        regPassword.clear();
        regDob.setValue(null);
        regGender.setValue(null);
        regBalance.clear();
        regAddress.clear();
        regPreferences.clear();
    }
}