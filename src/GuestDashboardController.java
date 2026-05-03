import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import models.Guest;
import models.Reservation;
import javafx.fxml.FXMLLoader;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class GuestDashboardController implements Initializable, GuestAware {

    @FXML private Label welcomeLabel;
    @FXML private Label profileUsername;
    @FXML private Label profileGender;
    @FXML private Label profileAge;
    @FXML private Label profileAddress;
    @FXML private Label profilePrefs;
    @FXML private Label balanceLabel;
    @FXML private TableView<Reservation> reservationsTable;
    @FXML private TableColumn<Reservation, String> columnRoomNum;
    @FXML private TableColumn<Reservation, String> columnRoomType;
    @FXML private TableColumn<Reservation, String> columnCheckIn;
    @FXML private TableColumn<Reservation, String> columnCheckOut;
    @FXML private TableColumn<Reservation, String> columnStatus;
    @FXML private TableColumn<Reservation, String> columnPaid;

    private Guest currentGuest;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        columnRoomNum.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getRoom().getRoomNumber())));
        columnRoomType.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getRoom().getType().getTypeName()));
        columnCheckIn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCheckInDate().toString()));
        columnCheckOut.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCheckOutDate().toString()));
        columnStatus.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getReservationStatus().toString()));
        columnPaid.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().isPaid() ? "Yes" : "No"));
    }

    @Override
    public void setGuest(Guest guest) {
        this.currentGuest = guest;
        populateDashboard();
    }

    //Populates all dashboard fields with the current guest's data.
    private void populateDashboard() {
        if (currentGuest == null) return;

        welcomeLabel.setText("Welcome, " + currentGuest.getUsername());
        profileUsername.setText(currentGuest.getUsername());
        profileGender.setText(currentGuest.getGender().toString());
        profileAge.setText(String.valueOf(currentGuest.getAge()));
        profileAddress.setText(currentGuest.getAddress());
        profilePrefs.setText(currentGuest.getRoomPreferences());
        balanceLabel.setText("$" + String.format("%.2f", currentGuest.getBalance()));

        refreshReservationsTable();
    }

    //Refreshes the reservations table with latest data.
    private void refreshReservationsTable() {
        List<Reservation> reservations = currentGuest.viewReservations();
        ObservableList<Reservation> observableReservations = FXCollections.observableArrayList(reservations);
        reservationsTable.setItems(observableReservations);
    }

    //Navigate to Room Browsing screen.
    @FXML
    private void handleBrowseRooms() {
        SceneNavigator.navigateTo("RoomBrowsing.fxml", currentGuest);
    }
    //Navigate to Reservation Management screen.
    @FXML
    private void handleMyReservations() {SceneNavigator.navigateTo("ReservationManagement.fxml", currentGuest);}
    //Navigate to Checkout & Payment screen.
    @FXML
    private void handleCheckout() {
        SceneNavigator.navigateTo("Checkout.fxml", currentGuest);
    }
    //Logout and return to Log-in/Register screen.
    @FXML
    private void handleLogout() {
        SceneNavigator.navigateTo("LoginRegister.fxml");
    }
}
