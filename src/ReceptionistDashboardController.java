import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import models.*;
import database.HotelDatabase;
import exceptions.InvalidCredentialException;
import utils.StatusLabelHelper;
import java.net.URL;
import java.time.temporal.ChronoUnit;
import java.util.ResourceBundle;
import network.ChatClient;
import javafx.application.Platform;

//Controller for the Receptionist (Front Desk) Dashboard.
//References Receptionist class methods
public class ReceptionistDashboardController implements Initializable, StaffAware {
    @FXML private Label staffInfoLabel;

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

    @FXML private TableView<Invoice> invoicesTable;
    @FXML private TableColumn<Invoice, String> colInvGuest;
    @FXML private TableColumn<Invoice, String> colInvRoom;
    @FXML private TableColumn<Invoice, String> colInvNights;
    @FXML private TableColumn<Invoice, String> colInvTotal;
    @FXML private TableColumn<Invoice, String> colInvPaid;
    @FXML private TableColumn<Invoice, String> colInvMethod;
    @FXML private TextArea invoiceDetailArea;
    @FXML private Label invStatusLabel;

    @FXML private TableView<Guest> guestsTable;
    @FXML private TableColumn<Guest, String> colGuestName;
    @FXML private TableColumn<Guest, String> colGuestGender;
    @FXML private TableColumn<Guest, String> colGuestBal;
    @FXML private TableView<Room> allRoomsTable;
    @FXML private TableColumn<Room, String> colViewRoomNum;
    @FXML private TableColumn<Room, String> colViewRoomType;
    @FXML private TableColumn<Room, String> colViewRoomPrice;

    // chat variables
    @FXML private TextArea chatTextArea;
    @FXML private TextField chatInputField;
    private ChatClient chatClient;
    private Receptionist receptionist;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupReservationColumns();
        setupInvoiceColumns();
        setupViewAllColumns();

        // Status combo
        for (ReservationStatus s : ReservationStatus.values()) {
            statusCombo.getItems().add(s.name());
        }
        resStatusLabel.setText("");
        invStatusLabel.setText("");
    }
    private void setupReservationColumns() {
        colResGuest.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getGuest().getUsername()));
        colResRoom.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().getRoom().getRoomNumber())));
        colResCheckIn.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getCheckInDate().toString()));
        colResCheckOut.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getCheckOutDate().toString()));
        colResStatus.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getReservationStatus().toString()));
        colResCheckedIn.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().isCheckedIn() ? "Yes" : "No"));
        colResCheckedOut.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().isCheckedOut() ? "Yes" : "No"));
        colResPaid.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().isPaid() ? "Yes" : "No"));
    }
    private void setupInvoiceColumns() {
        colInvGuest.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getReservation().getGuest().getUsername()));
        colInvRoom.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().getReservation().getRoom().getRoomNumber())));
        colInvNights.setCellValueFactory(d -> {long nights = ChronoUnit.DAYS.between(d.getValue().getReservation().getCheckInDate(), d.getValue().getReservation().getCheckOutDate());
            return new SimpleStringProperty(String.valueOf(nights));});
        colInvTotal.setCellValueFactory(d -> new SimpleStringProperty("$" + String.format("%.2f", d.getValue().calculateTotal())));
        colInvPaid.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getReservation().isPaid() ? "Yes" : "No"));
        colInvMethod.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getPaymentmethod().toString()));
    }
    private void setupViewAllColumns() {
        colGuestName.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getUsername()));
        colGuestGender.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getGender().toString()));
        colGuestBal.setCellValueFactory(d -> new SimpleStringProperty("$" + String.format("%.2f", d.getValue().getBalance())));
        colViewRoomNum.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().getRoomNumber())));
        colViewRoomType.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getType().getTypeName()));
        colViewRoomPrice.setCellValueFactory(d -> new SimpleStringProperty("$" + String.format("%.2f", d.getValue().totalRoomPricePerOneNight())));
    }
    @Override
    public void setStaff(Staff staff) {
        if (staff instanceof Receptionist) {this.receptionist = (Receptionist) staff;}
        staffInfoLabel.setText("Receptionist: " + staff.getUsername());
        refreshAll();
        // Start Chat
        chatClient = new ChatClient("localhost", 8080, message -> {Platform.runLater(() -> chatTextArea.appendText(message + "\n"));});
        chatTextArea.appendText("Connected to Guest Support Chat.\n");
    }
    // Reservation operations
    @FXML
    private void handleCheckIn() {
        Reservation selected = reservationsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            StatusLabelHelper.set(resStatusLabel, "Select a reservation to check in.", true);
            return;
        }
        if (selected.isCheckedIn()) {
            StatusLabelHelper.set(resStatusLabel, "Guest already checked in.", true);
            return;
        }
        if (selected.isCancelled()) {
            StatusLabelHelper.set(resStatusLabel, "Cannot check in a cancelled reservation.", true);
            return;
        }
        receptionist.checkIn(selected);
        if (selected.isCheckedIn()) {
            StatusLabelHelper.set(resStatusLabel, "Check-in successful for "
                    + selected.getGuest().getUsername()
                    + " - Room #"
                    + selected.getRoom().getRoomNumber(), false);
        } else {
            StatusLabelHelper.set(resStatusLabel, "Check-in failed. Check dates.", true);
        }
        refreshReservations();
    }
    @FXML
    private void handleCheckOut() {
        Reservation selected = reservationsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            StatusLabelHelper.set(resStatusLabel, "Select a reservation to check out.", true);
            return;
        }
        if (!selected.isCheckedIn()) {
            StatusLabelHelper.set(resStatusLabel, "Guest not checked in yet.", true);
            return;
        }
        if (selected.isCheckedOut()) {
            StatusLabelHelper.set(resStatusLabel, "Guest already checked out.", true);
            return;
        }
        Invoice invoice = receptionist.checkOut(selected);

        if (invoice != null) {
            StatusLabelHelper.set(resStatusLabel, "Check-out successful for "
                    + selected.getGuest().getUsername()
                    + ". Invoice generated.", false);
            refreshInvoices();
        } else {
            StatusLabelHelper.set(resStatusLabel, "Check-out failed.", true);
        }
        refreshReservations();
    }
    @FXML
    private void handleUpdateStatus() {
        Reservation selected = reservationsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            StatusLabelHelper.set(resStatusLabel, "Select a reservation.", true);
            return;
        }
        if (statusCombo.getValue() == null) {
            StatusLabelHelper.set(resStatusLabel, "Select a status.", true);
            return;
        }
        ReservationStatus newStatus = ReservationStatus.valueOf(statusCombo.getValue());
        try {
            receptionist.updateReservationStatus(selected, newStatus);
            StatusLabelHelper.set(resStatusLabel, "Status updated to " + newStatus + ".", false);
            refreshReservations();
        } catch (InvalidCredentialException e) {
            StatusLabelHelper.set(resStatusLabel, e.getMessage(), true);
        }
    }
    @FXML
    private void handleFinalizeExpired() {
        int count = receptionist.finalizeCompletedReservations();
        StatusLabelHelper.set(resStatusLabel, "Finalized " + count + " expired reservation(s).", false);
        refreshReservations();
        refreshInvoices();
    }
    @FXML
    private void handleRefreshReservations() {
        refreshReservations();
    }

    // Invoice operations
    @FXML
    private void handleViewInvoice() {
        Invoice selected = invoicesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            StatusLabelHelper.set(invStatusLabel, "Select an invoice to view.", true);
            return;
        }
        invoiceDetailArea.setText(selected.toString());
        StatusLabelHelper.set(invStatusLabel, "Invoice displayed.", false);
    }
    @FXML
    private void handleProcessPayment() {
        Invoice selected = invoicesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            StatusLabelHelper.set(invStatusLabel, "Select an invoice to process payment.", true);
            return;
        }
        if (selected.getReservation().isPaid()) {
            StatusLabelHelper.set(invStatusLabel, "This invoice is already paid.", true);
            return;
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
                    StatusLabelHelper.set(invStatusLabel, "Payment of $" + String.format("%.2f", total) + " processed. New balance: $" + String.format("%.2f", guest.getBalance()), false);
                    invoiceDetailArea.setText(selected.toString());
                } else {
                    StatusLabelHelper.set(invStatusLabel, "Payment failed. Insufficient funds.", true);
                }
                refreshInvoices();
                refreshReservations();
            }
        });
    }
    @FXML
    private void handleRefreshInvoices() {
        refreshInvoices();
    }

    @FXML
    private void handleRefreshViewAll() {
        refreshGuests();
        refreshRooms();
    }
    // Refresh
    private void refreshAll() {
        refreshReservations();
        refreshInvoices();
        refreshGuests();
        refreshRooms();
    }
    private void refreshReservations() {
        reservationsTable.setItems(FXCollections.observableArrayList(HotelDatabase.reservations));
        reservationsTable.refresh();
    }
    private void refreshInvoices() {
        invoicesTable.setItems(FXCollections.observableArrayList(HotelDatabase.invoices));
        invoicesTable.refresh();
    }
    private void refreshGuests() {
        guestsTable.setItems(FXCollections.observableArrayList(HotelDatabase.guests));
        guestsTable.refresh();
    }
    private void refreshRooms() {
        allRoomsTable.setItems(FXCollections.observableArrayList(HotelDatabase.rooms));
        allRoomsTable.refresh();
    }
    @FXML
    private void handleLogout() {
        SceneNavigator.navigateTo("LoginRegister.fxml");
    }
    // Chat send
    @FXML
    private void handleSendMessage() {
        String text = chatInputField.getText().trim();
        if (!text.isEmpty() && chatClient != null) {
            // Format: "Receptionist (Name): Message"
            String formattedMessage = "Receptionist (" + receptionist.getUsername() + "): " + text;
            chatClient.sendMessage(formattedMessage);
            chatTextArea.appendText("Me: " + text + "\n");
            chatInputField.clear();
        }
    }
}