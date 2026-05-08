import database.HotelDatabase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import models.*;
import utils.AlertHelper;
import utils.TableColumnHelper;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

//Controller for the Checkout & Payment screen.
//Allows guests to select unpaid reservations, view invoices, choose
//a payment method, and confirm payment.
public class CheckoutController implements Initializable, GuestAware {

    @FXML private Label balanceInfoLabel;
    @FXML private TableView<Reservation> unpaidTable;
    @FXML private TableColumn<Reservation, String> colRoomNum;
    @FXML private TableColumn<Reservation, String> colRoomType;
    @FXML private TableColumn<Reservation, String> colCheckIn;
    @FXML private TableColumn<Reservation, String> colCheckOut;
    @FXML private TableColumn<Reservation, String> colStatus;
    @FXML private TableColumn<Reservation, String> colTotal;
    @FXML private ComboBox<String> paymentMethodCombo;
    @FXML private TextArea invoiceSummary;
    @FXML private Label statusLabel;

    private Guest currentGuest;
    private Invoice currentInvoice;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colRoomNum.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(String.valueOf(data.getValue().getRoom().getRoomNumber())));
        colRoomType.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getRoom().getType().getTypeName()));
        colCheckIn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getCheckInDate().toString()));
        colCheckOut.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getCheckOutDate().toString()));
        colStatus.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getReservationStatus().toString()));
        TableColumnHelper.setupInvoiceTotalColumn(colTotal);

        // Set up payment method dropdown
        for (PaymentMethod method : PaymentMethod.values()) {paymentMethodCombo.getItems().add(method.name());}
        statusLabel.setText("");
    }
    @Override
    public void setGuest(Guest guest) {
        this.currentGuest = guest;
        updateBalanceLabel();
        loadUnpaidReservations();
    }
    private void updateBalanceLabel() {balanceInfoLabel.setText("Balance: $" + String.format("%.2f", currentGuest.getBalance()));}
    private void loadUnpaidReservations() {
        List<Reservation> allReservations = currentGuest.viewReservations();
        List<Reservation> unpaid = new ArrayList<>();
        for (Reservation res : allReservations) {
            if (!res.isPaid() && !res.isCancelled()) {unpaid.add(res);}
        }
        ObservableList<Reservation> observableUnpaid = FXCollections.observableArrayList(unpaid);
        unpaidTable.setItems(observableUnpaid);
        unpaidTable.refresh();
        if (unpaid.isEmpty()) {
            statusLabel.setText("All reservations are paid. Nothing to checkout.");
            statusLabel.getStyleClass().setAll("label", "status-success");
        } else {
            statusLabel.setText(unpaid.size() + " unpaid reservation(s).");
            statusLabel.getStyleClass().setAll("label", "info-value-light");
        }
    }
    //Generates and displays the invoice for the selected reservation.
    @FXML
    private void handleGenerateInvoice() {
        Reservation selected = unpaidTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            AlertHelper.showAlert(Alert.AlertType.WARNING, "No Selection",
                    "Please select a reservation to generate an invoice.");
            return;
        }
        Invoice invoice = selected.getInvoice();
        if (invoice == null) {
            invoice = new Invoice(selected);
            selected.setInvoice(invoice);
        }
        String methodStr = paymentMethodCombo.getValue();
        if (methodStr != null && !methodStr.isEmpty()) {
            PaymentMethod method = PaymentMethod.valueOf(methodStr);
            invoice.setPaymentmethod(method);
        }
        currentInvoice = invoice;

        // Display the invoice summary
        invoiceSummary.setText(invoice.toString());
        statusLabel.setText("Invoice generated for Room #" + selected.getRoom().getRoomNumber());
        statusLabel.getStyleClass().setAll("label", "status-success");
    }
    //Confirms payment for the current invoice.
    @FXML
    private void handleConfirmPayment() {
        if (currentInvoice == null) {
            AlertHelper.showAlert(Alert.AlertType.WARNING, "No Invoice", "Please generate an invoice first by selecting a reservation\n" + "and clicking 'Generate Invoice'.");
            return;
        }
        String methodStr = paymentMethodCombo.getValue();
        if (methodStr == null || methodStr.isEmpty()) {AlertHelper.showAlert(Alert.AlertType.WARNING, "No Payment Method", "Please select a payment method.");
            return;
        }
        Reservation reservation = currentInvoice.getReservation();
        if (reservation.isPaid()) {AlertHelper.showAlert(Alert.AlertType.INFORMATION, "Already Paid", "This reservation has already been paid.");
            return;
        }
        double total = currentInvoice.calculateTotal();
        if (currentGuest.getBalance() < total) {AlertHelper.showAlert(Alert.AlertType.ERROR, "Insufficient Funds", "You do not have enough balance.\n\n"
                    + "Total Due: $" + String.format("%.2f", total) + "\n"
                    + "Your Balance: $" + String.format("%.2f", currentGuest.getBalance()));
            return;
        }
        // Set payment method on invoice
        PaymentMethod method = PaymentMethod.valueOf(methodStr);
        currentInvoice.setPaymentmethod(method);
        // Process the payment
        boolean success = currentInvoice.processPayment(total);
        if (success) {
            // Deduct from guest balance
            currentGuest.setBalance(currentGuest.getBalance() - total);
            HotelDatabase.updateGuest(currentGuest);
            HotelDatabase.updateReservation(currentInvoice.getReservation());
            HotelDatabase.updateInvoice(currentInvoice);
            AlertHelper.showAlert(Alert.AlertType.INFORMATION, "Payment Successful!", "Payment of $" + String.format("%.2f", total) + " processed successfully!\n\n"
                    + "Payment Method: " + method + "\n"
                    + "Remaining Balance: $" + String.format("%.2f", currentGuest.getBalance()));

            // Refresh UI
            updateBalanceLabel();
            loadUnpaidReservations();
            invoiceSummary.setText(currentInvoice.toString());
            currentInvoice = null;

            statusLabel.setText("Payment confirmed successfully!");
            statusLabel.getStyleClass().setAll("label", "status-success");
        } else {
            AlertHelper.showAlert(Alert.AlertType.ERROR, "Payment Failed", "Payment processing failed. Please try again.");
        }
    }
    @FXML
    private void handleBackToDashboard() {
        SceneNavigator.navigateTo("GuestDashboard.fxml", currentGuest);
    }
}
