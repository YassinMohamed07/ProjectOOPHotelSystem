import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import models.Guest;
import models.Reservation;
import exceptions.InvalidCredentialException;
import utils.AlertHelper;
import utils.TableColumnHelper;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.concurrent.Task;

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
        // Set up common reservation columns via shared helper
        TableColumnHelper.setupReservationColumns(colRoomNum, colRoomType, colCheckIn, colCheckOut, colStatus, colPaid);
        TableColumnHelper.setupInvoiceTotalColumn(colTotal);
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
        statusLabel.setText("Loading reservations in background...");
        statusLabel.getStyleClass().setAll("label", "info-value-light");
        // Simulate network/database delay
        Task<List<Reservation>> loadTask = new Task<>() {
            @Override
            protected List<Reservation> call() throws Exception {Thread.sleep(800);
                return currentGuest.viewReservations();
            }
        };
        // when the thread succeeds
        loadTask.setOnSucceeded(e -> {
            List<Reservation> reservations = loadTask.getValue();
            ObservableList<Reservation> observableReservations = FXCollections.observableArrayList(reservations);
            reservationsTable.setItems(observableReservations);
            reservationsTable.refresh();
            if (reservations.isEmpty()) {
                statusLabel.setText("You have no reservations.");
                statusLabel.getStyleClass().setAll("label", "status-error");
            } else {
                statusLabel.setText(reservations.size() + " reservation(s) found.");
                statusLabel.getStyleClass().setAll("label", "status-success");}});
        // if the thread fails
        loadTask.setOnFailed(e -> {statusLabel.setText("Error loading reservations.");
            statusLabel.getStyleClass().setAll("label", "status-error");});
        // Start the thread
        Thread backgroundThread = new Thread(loadTask);
        backgroundThread.setDaemon(true); // Ensures thread closes when app closes
        backgroundThread.start();
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
            AlertHelper.showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a reservation from the table to cancel.");
            return;
        }
        if (selected.isCancelled()) {
            AlertHelper.showAlert(Alert.AlertType.INFORMATION, "Already Cancelled", "This reservation has already been cancelled.");
            return;
        }
        if (selected.isPaid()) {
            AlertHelper.showAlert(Alert.AlertType.WARNING, "Cannot Cancel", "This reservation has already been paid and cannot be cancelled.");
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
                AlertHelper.showAlert(Alert.AlertType.INFORMATION, "Cancellation Successful", "Reservation for Room #" + selected.getRoom().getRoomNumber() + " has been cancelled.");
                refreshTable();
            } catch (InvalidCredentialException e) {
                AlertHelper.showAlert(Alert.AlertType.ERROR, "Cancellation Failed", e.getMessage());
            } catch (Exception e) {
                AlertHelper.showAlert(Alert.AlertType.ERROR, "Cancellation Failed", e.getMessage());
            }
        }
    }
    @FXML
    private void handleBrowseRooms() {
        SceneNavigator.navigateTo("RoomBrowsing.fxml", currentGuest);
    }

    @FXML
    private void handleBackToDashboard() {
        SceneNavigator.navigateTo("GuestDashboard.fxml", currentGuest);
    }
}
