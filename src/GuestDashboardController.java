import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import models.Guest;
import models.Reservation;
import utils.TableColumnHelper;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import network.ChatClient;
import javafx.application.Platform;

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
    @FXML private TextArea chatTextArea;
    @FXML private TextField chatInputField;
    private ChatClient chatClient;

    private Guest currentGuest;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Set up common reservation columns
        TableColumnHelper.setupReservationColumns(columnRoomNum, columnRoomType, columnCheckIn, columnCheckOut, columnStatus, columnPaid);
    }
    @Override
    public void setGuest(Guest guest) {
        this.currentGuest = guest;
        populateDashboard();

        // Start Chat
        chatClient = new ChatClient("localhost", 8080, message -> {Platform.runLater(() -> chatTextArea.appendText(message + "\n"));});
        chatTextArea.appendText("Connected to Hotel Chat.\n");
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
        reservationsTable.refresh();
    }
    @FXML
    private void handleBrowseRooms() {
        SceneNavigator.navigateTo("RoomBrowsing.fxml", currentGuest);
    }
    @FXML
    private void handleMyReservations() {
        SceneNavigator.navigateTo("ReservationManagement.fxml", currentGuest);
    }
    @FXML
    private void handleCheckout() {
        SceneNavigator.navigateTo("Checkout.fxml", currentGuest);
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
            String formattedMessage = currentGuest.getUsername() + ": " + text;
            chatClient.sendMessage(formattedMessage);
            chatTextArea.appendText("Me: " + text + "\n");
            chatInputField.clear();
        }
    }
}
