import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import models.Guest;
import models.Invoice;
import models.Reservation;
import exceptions.InvalidCredentialException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

//Controller for the Reservation Management screen.
//Allows guests to view their booking history and cancel reservations.
public class ReservationController implements Initializable, GuestAware {

    @FXML private Label guestInfoLabel;
    @FXML private TableView<Reservation> reservationsTable;
    @FXML private TableColumn<Reservation, String> colRoomNum;
    @FXML private TableColumn<Reservation, String> colRoomType;
    @FXML private TableColumn<Reservation, String> colCheckIn;
    @FXML private TableColumn<Reservation, String> colCheckOut;
    @FXML private TableColumn<Reservation, String> colStatus;
    @FXML private TableColumn<Reservation, String> colPaid;
    @FXML private TableColumn<Reservation, String> colTotal;
    @FXML private Label statusLabel;

    private Guest currentGuest;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Set up table columns
        colRoomNum.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getRoom().getRoomNumber())));
        colRoomType.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getRoom().getType().getTypeName()));
        colCheckIn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCheckInDate().toString()));
        colCheckOut.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCheckOutDate().toString()));
        colStatus.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getReservationStatus().toString()));
        colPaid.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().isPaid() ? "Yes" : "No"));
        colTotal.setCellValueFactory(data -> {Invoice invoice = data.getValue().getInvoice();
            if (invoice != null) {
                return new SimpleStringProperty("$" + String.format("%.2f", invoice.calculateTotal()));
            }
            return new SimpleStringProperty("N/A");
        });
        statusLabel.setText("");
    }
    @Override
    public void setGuest(Guest guest) {
        this.currentGuest = guest;
        guestInfoLabel.setText("Logged in as: " + guest.getUsername());
        refreshTable();
    }
    //Refreshes the reservations table with the latest data.
    private void refreshTable() {
        List<Reservation> reservations = currentGuest.viewReservations();
        ObservableList<Reservation> observableReservations = FXCollections.observableArrayList(reservations);

        reservationsTable.setItems(observableReservations);
        reservationsTable.refresh(); // <--- Add this line

        if (reservations.isEmpty()) {
            statusLabel.setText("You have no reservations.");
            statusLabel.getStyleClass().setAll("label", "status-error");
        } else {
            statusLabel.setText(reservations.size() + " reservation(s) found.");
            statusLabel.getStyleClass().setAll("label", "status-success");
        }
    }
    @FXML
    private void handleRefresh() {
        refreshTable();
    }

    //Handles cancellation of the selected reservation.
    @FXML
    private void handleCancelReservation() {
        Reservation selected = reservationsTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection",
                    "Please select a reservation from the table to cancel.");
            return;
        }
        // Check if already cancelled
        if (selected.isCancelled()) {
            showAlert(Alert.AlertType.INFORMATION, "Already Cancelled",
                    "This reservation has already been cancelled.");
            return;
        }
        // Check if already paid
        if (selected.isPaid()) {
            showAlert(Alert.AlertType.WARNING, "Cannot Cancel",
                    "This reservation has already been paid and cannot be cancelled.");
            return;
        }
        // Confirmation dialog
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Cancellation");
        confirmAlert.setHeaderText("Cancel Reservation?");
        confirmAlert.setContentText("Are you sure you want to cancel your reservation for Room #" + selected.getRoom().getRoomNumber() + "?\n"
                + "Check-in: " + selected.getCheckInDate() + "\n"
                + "Check-out: " + selected.getCheckOutDate());

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                currentGuest.cancelReservation(selected);
                showAlert(Alert.AlertType.INFORMATION, "Cancellation Successful",
                        "Reservation for Room #" + selected.getRoom().getRoomNumber()
                                + " has been cancelled.");
                refreshTable();
            } catch (InvalidCredentialException e) {
                showAlert(Alert.AlertType.ERROR, "Cancellation Failed", e.getMessage());
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Cancellation Failed", e.getMessage());
            }
        }
    }
    //Navigate to Browse Rooms screen.
    @FXML
    private void handleBrowseRooms() {
        SceneNavigator.navigateTo("RoomBrowsing.fxml", currentGuest);
    }

    //Navigate back to the Guest Dashboard.
    @FXML
    private void handleBackToDashboard() {
        SceneNavigator.navigateTo("GuestDashboard.fxml", currentGuest);
    }

    //Helper to show alert dialogs.
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
