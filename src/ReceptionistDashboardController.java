import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import models.*;
import database.HotelDatabase;
import exceptions.InvalidCredentialException;
import java.net.URL;
import java.time.temporal.ChronoUnit;
import java.util.ResourceBundle;

//Controller for the Receptionist (Front Desk) Dashboard.
//References Receptionist class methods for check-in, check-out,
//status updates, payment processing, and view-all operations.
public class ReceptionistDashboardController implements Initializable, StaffAware {
    @FXML private Label staffInfoLabel;

    // Reservations tab
    @FXML private TableView<Reservation> reservationsTable;
    @FXML private TableColumn<Reservation, String> colResGuest;
    @FXML private TableColumn<Reservation, String> colResRoom;
    @FXML private TableColumn<Reservation, String> colResCheckIn;
    @FXML private TableColumn<Reservation, String> colResCheckOut;
    @FXML private TableColumn<Reservation, String> colResStatus;
    @FXML private TableColumn<Reservation, String> colResCheckedIn;
    @FXML private TableColumn<Reservation, String> colResCheckedOut;
    @FXML private TableColumn<Reservation, String> colResPaid;
    @FXML private ComboBox<String> statusCombo;
    @FXML private Label resStatusLabel;

    // Invoices tab
    @FXML private TableView<Invoice> invoicesTable;
    @FXML private TableColumn<Invoice, String> colInvGuest;
    @FXML private TableColumn<Invoice, String> colInvRoom;
    @FXML private TableColumn<Invoice, String> colInvNights;
    @FXML private TableColumn<Invoice, String> colInvTotal;
    @FXML private TableColumn<Invoice, String> colInvPaid;
    @FXML private TableColumn<Invoice, String> colInvMethod;
    @FXML private TextArea invoiceDetailArea;
    @FXML private Label invStatusLabel;

    // View All tab
    @FXML private TableView<Guest> guestsTable;
    @FXML private TableColumn<Guest, String> colGuestName;
    @FXML private TableColumn<Guest, String> colGuestGender;
    @FXML private TableColumn<Guest, String> colGuestBal;
    @FXML private TableView<Room> allRoomsTable;
    @FXML private TableColumn<Room, String> colViewRoomNum;
    @FXML private TableColumn<Room, String> colViewRoomType;
    @FXML private TableColumn<Room, String> colViewRoomPrice;

    private Receptionist receptionist;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Reservation table columns
        colResGuest.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getGuest().getUsername()));
        colResRoom.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().getRoom().getRoomNumber())));
        colResCheckIn.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getCheckInDate().toString()));
        colResCheckOut.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getCheckOutDate().toString()));
        colResStatus.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getReservationStatus().toString()));
        colResCheckedIn.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().isCheckedIn() ? "Yes" : "No"));
        colResCheckedOut.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().isCheckedOut() ? "Yes" : "No"));
        colResPaid.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().isPaid() ? "Yes" : "No"));

        // Invoice table columns
        colInvGuest.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getReservation().getGuest().getUsername()));
        colInvRoom.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().getReservation().getRoom().getRoomNumber())));
        colInvNights.setCellValueFactory(d -> {
            long nights = ChronoUnit.DAYS.between(d.getValue().getReservation().getCheckInDate(), d.getValue().getReservation().getCheckOutDate());
            return new SimpleStringProperty(String.valueOf(nights));
        });
        colInvTotal.setCellValueFactory(d -> new SimpleStringProperty("$" + String.format("%.2f", d.getValue().calculateTotal())));
        colInvPaid.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getReservation().isPaid() ? "Yes" : "No"));
        colInvMethod.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getPaymentmethod().toString()));

        // View All columns
        colGuestName.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getUsername()));
        colGuestGender.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getGender().toString()));
        colGuestBal.setCellValueFactory(d -> new SimpleStringProperty("$" + String.format("%.2f", d.getValue().getBalance())));
        colViewRoomNum.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().getRoomNumber())));
        colViewRoomType.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getType().getTypeName()));
        colViewRoomPrice.setCellValueFactory(d -> new SimpleStringProperty("$" + String.format("%.2f", d.getValue().totalRoomPricePerOneNight())));

        // Status combo
        for (ReservationStatus s : ReservationStatus.values()) {
            statusCombo.getItems().add(s.name());
        }
        resStatusLabel.setText("");
        invStatusLabel.setText("");
    }
    @Override
    public void setStaff(Staff staff) {
        if (staff instanceof Receptionist) {
            this.receptionist = (Receptionist) staff;
        }
        staffInfoLabel.setText("Receptionist: " + staff.getUsername());
        refreshAll();
    }
    // Reservation operations (references Receptionist.checkIn, checkOut, updateReservationStatus)
    @FXML
    private void handleCheckIn() {
        Reservation selected = reservationsTable.getSelectionModel().getSelectedItem();
        if (selected == null) { setResStatus("Select a reservation to check in.", true); return; }

        if (selected.isCheckedIn()) { setResStatus("Guest already checked in.", true); return; }
        if (selected.isCancelled()) { setResStatus("Cannot check in a cancelled reservation.", true); return; }

        // Call Receptionist.checkIn directly
        receptionist.checkIn(selected);

        if (selected.isCheckedIn()) {
            setResStatus("Check-in successful for " + selected.getGuest().getUsername() + " - Room #" + selected.getRoom().getRoomNumber(), false);
        } else {
            setResStatus("Check-in failed. Check dates.", true);
        }
        refreshReservations();
    }
    @FXML
    private void handleCheckOut() {
        Reservation selected = reservationsTable.getSelectionModel().getSelectedItem();
        if (selected == null) { setResStatus("Select a reservation to check out.", true); return; }

        if (!selected.isCheckedIn()) { setResStatus("Guest not checked in yet.", true); return; }
        if (selected.isCheckedOut()) { setResStatus("Guest already checked out.", true); return; }

        // Call Receptionist.checkOut directly
        Invoice invoice = receptionist.checkOut(selected);

        if (invoice != null) {
            setResStatus("Check-out successful for " + selected.getGuest().getUsername() + ". Invoice generated.", false);
            refreshInvoices();
        } else {
            setResStatus("Check-out failed.", true);
        }
        refreshReservations();
    }
    @FXML
    private void handleUpdateStatus() {
        Reservation selected = reservationsTable.getSelectionModel().getSelectedItem();
        if (selected == null) { setResStatus("Select a reservation.", true); return; }
        if (statusCombo.getValue() == null) { setResStatus("Select a status.", true); return; }

        ReservationStatus newStatus = ReservationStatus.valueOf(statusCombo.getValue());
        try {
            receptionist.updateReservationStatus(selected, newStatus);
            setResStatus("Status updated to " + newStatus + ".", false);
            refreshReservations();
        } catch (InvalidCredentialException e) {
            setResStatus(e.getMessage(), true);
        }
    }
    @FXML
    private void handleFinalizeExpired() {
        int count = receptionist.finalizeCompletedReservations();
        setResStatus("Finalized " + count + " expired reservation(s).", false);
        refreshReservations();
        refreshInvoices();
    }
    @FXML
    private void handleRefreshReservations() { refreshReservations(); }

    // Invoice operations (references Receptionist.processCheckoutPayment)

    @FXML
    private void handleViewInvoice() {
        Invoice selected = invoicesTable.getSelectionModel().getSelectedItem();
        if (selected == null) { setInvStatus("Select an invoice to view.", true); return; }
        invoiceDetailArea.setText(selected.toString());
        setInvStatus("Invoice displayed.", false);
    }
    @FXML
    private void handleProcessPayment() {
        Invoice selected = invoicesTable.getSelectionModel().getSelectedItem();
        if (selected == null) { setInvStatus("Select an invoice to process payment.", true); return; }

        if (selected.getReservation().isPaid()) {
            setInvStatus("This invoice is already paid.", true); return;
        }
        double total = selected.calculateTotal();
        Guest guest = selected.getReservation().getGuest();

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Process Payment");
        confirm.setHeaderText("Process payment of $" + String.format("%.2f", total) + "?");
        confirm.setContentText("Guest: " + guest.getUsername() + "\nBalance: $" + String.format("%.2f", guest.getBalance()));
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) {
                boolean success = receptionist.processCheckoutPayment(selected, total);
                if (success) {
                    setInvStatus("Payment of $" + String.format("%.2f", total) + " processed. New balance: $" + String.format("%.2f", guest.getBalance()), false);
                    invoiceDetailArea.setText(selected.toString());
                } else {
                    setInvStatus("Payment failed. Insufficient funds.", true);
                }
                refreshInvoices();
                refreshReservations();
            }
        });
    }
    @FXML
    private void handleRefreshInvoices() { refreshInvoices(); }
    @FXML
    private void handleRefreshViewAll() { refreshGuests(); refreshRooms(); }

    // refresh
    private void refreshAll() { refreshReservations(); refreshInvoices(); refreshGuests(); refreshRooms(); }
    private void refreshReservations() { reservationsTable.setItems(FXCollections.observableArrayList(HotelDatabase.reservations)); }
    private void refreshInvoices() { invoicesTable.setItems(FXCollections.observableArrayList(HotelDatabase.invoices)); }
    private void refreshGuests() { guestsTable.setItems(FXCollections.observableArrayList(HotelDatabase.guests)); }
    private void refreshRooms() { allRoomsTable.setItems(FXCollections.observableArrayList(HotelDatabase.rooms)); }

    @FXML
    private void handleLogout() { SceneNavigator.navigateTo("LoginRegister.fxml"); }

    private void setResStatus(String msg, boolean error) {
        resStatusLabel.setText(msg);
        resStatusLabel.getStyleClass().setAll("label", error ? "status-error" : "status-success");
    }
    private void setInvStatus(String msg, boolean error) {
        invStatusLabel.setText(msg);
        invStatusLabel.getStyleClass().setAll("label", error ? "status-error" : "status-success");
    }
}