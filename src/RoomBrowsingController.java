import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import models.*;
import database.HotelDatabase;
import exceptions.InvalidDateException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.concurrent.Task;


//Controller for the Room Browsing screen.
//Allows guests to search/filter available rooms and book them.
public class RoomBrowsingController implements Initializable, GuestAware {

    @FXML private Label guestInfoLabel;
    @FXML private ComboBox<String> filterRoomType;
    @FXML private TextField filterMaxPrice;
    @FXML private DatePicker filterCheckIn;
    @FXML private DatePicker filterCheckOut;
    @FXML private FlowPane amenitiesPane;
    @FXML private Label resultCountLabel;
    @FXML private TableView<Room> roomsTable;
    @FXML private TableColumn<Room, String> columnRoomNum;
    @FXML private TableColumn<Room, String> columnRoomType;
    @FXML private TableColumn<Room, String> columnBasePrice;
    @FXML private TableColumn<Room, String> columnAmenities;
    @FXML private TableColumn<Room, String> columnTotalPrice;

    private Guest currentGuest;

    // Extra Amenities
    @FXML private ComboBox<String> extraAmenityCombo;
    @FXML private Label selectedExtrasLabel;
    private List<Amenity> selectedExtraAmenities = new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Set up room type filter options
        filterRoomType.getItems().add("All Types");
        for (Roomtypee type : Roomtypee.values()) {
            filterRoomType.getItems().add(type.name());
        }
        filterRoomType.setValue("All Types");

        // Listen for changes in Room Type to update the amenities display dynamically
        filterRoomType.valueProperty().addListener((obs, oldVal, newVal) -> updateAmenitiesDisplay(newVal));
        updateAmenitiesDisplay("All Types");

        // Set up table columns
        columnRoomNum.setCellValueFactory(data -> new SimpleStringProperty(
                String.valueOf(data.getValue().getRoomNumber())));
        columnRoomType.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getType().getTypeName()));
        columnBasePrice.setCellValueFactory(data -> new SimpleStringProperty(
                "$" + String.format("%.2f", data.getValue().getType().getBasePrice())));
        columnAmenities.setCellValueFactory(data -> {
            ArrayList<Amenity> amenities = data.getValue().getAmenities();
            if (amenities.isEmpty()) return new SimpleStringProperty("None");
            String names = amenities.stream()
                    .map(Amenity::getName)
                    .collect(Collectors.joining(", "));
            return new SimpleStringProperty(names);
        });
        columnTotalPrice.setCellValueFactory(data -> new SimpleStringProperty(
                "$" + String.format("%.2f", data.getValue().totalRoomPricePerOneNight())));

        // Populate Extra Amenities combo from the database's paid extras list
        for (Amenity amenity : HotelDatabase.extraAmenities) {
            extraAmenityCombo.getItems().add(amenity.getName() + " ($" + String.format("%.2f", amenity.getPrice()) + ")");
        }
    }

    @Override
    public void setGuest(Guest guest) {
        this.currentGuest = guest;
        guestInfoLabel.setText("Logged in as: " + guest.getUsername());
    }

    //Dynamically updates the Amenities pane to show what is included in the selected room type
    private void updateAmenitiesDisplay(String roomType) {
        amenitiesPane.getChildren().clear();

        List<Amenity> toDisplay = new ArrayList<>();

        if (roomType == null || roomType.equals("All Types")) {
            Label placeholder = new Label("Select a Room Type to view its included amenities.");
            placeholder.setStyle("-fx-text-fill: #94a3b8; -fx-font-style: italic;");
            amenitiesPane.getChildren().add(placeholder);
            return;
        }

        if (roomType.equalsIgnoreCase("SINGLE")) {
            toDisplay = HotelDatabase.singleDefaults;
        } else if (roomType.equalsIgnoreCase("DOUBLE")) {
            toDisplay = HotelDatabase.doubleDefaults;
        } else if (roomType.equalsIgnoreCase("SUITE")) {
            toDisplay = HotelDatabase.suiteDefaults;
        }

        for (Amenity amenity : toDisplay) {
            CheckBox cb = new CheckBox(amenity.getName());
            cb.setSelected(true); // Show as included
            cb.setMouseTransparent(true); // Prevent user from unchecking it
            cb.setFocusTraversable(false);
            cb.setStyle("-fx-opacity: 1; -fx-text-fill: #f8fafc;"); // Keep text bright
            amenitiesPane.getChildren().add(cb);
        }
    }

    //Handles the Search button — filters rooms based on criteria.
    @FXML
    private void handleSearch() {
        LocalDate checkIn = filterCheckIn.getValue();
        LocalDate checkOut = filterCheckOut.getValue();
        if (checkIn == null || checkOut == null) return;

        String selectedType = filterRoomType.getValue().equals("All Types") ? null : filterRoomType.getValue();
        double maxPrice = filterMaxPrice.getText().isEmpty() ? 0 : Double.parseDouble(filterMaxPrice.getText());

        resultCountLabel.setText("Searching available rooms...");

        // Run search on background thread
        Task<List<Room>> searchTask = new Task<>() {
            @Override
            protected List<Room> call() throws Exception {
                return Guest.searchAvailableRooms(checkIn, checkOut, selectedType, maxPrice);
            }
        };

        searchTask.setOnSucceeded(e -> {
            List<Room> results = searchTask.getValue();
            roomsTable.setItems(FXCollections.observableArrayList(results));
            resultCountLabel.setText(results.size() + " room(s) found");
        });

        Thread thread = new Thread(searchTask);
        thread.setDaemon(true);
        thread.start();
    }

    //Clears all filter fields.
    @FXML
    private void handleClearFilters() {
        filterRoomType.setValue("All Types");
        filterMaxPrice.clear();
        filterCheckIn.setValue(null);
        filterCheckOut.setValue(null);
        roomsTable.getItems().clear();
        resultCountLabel.setText("");

        // Clear extra amenities selection
        selectedExtraAmenities.clear();
        extraAmenityCombo.setValue(null);
        updateSelectedExtrasLabel();
    }

    //Books the selected room for the current guest.
    @FXML
    private void handleBookRoom() {
        Room selectedRoom = roomsTable.getSelectionModel().getSelectedItem();
        if (selectedRoom == null) {
            showAlert(Alert.AlertType.WARNING, "No Room Selected",
                    "Please select a room from the table to book.");
            return;
        }
        LocalDate checkIn = filterCheckIn.getValue();
        LocalDate checkOut = filterCheckOut.getValue();

        if (checkIn == null || checkOut == null) {
            showAlert(Alert.AlertType.WARNING, "Missing Dates",
                    "Check-in and check-out dates are required to book.");
            return;
        }
        try {
            // Create the reservation using the Guest model's method
            Reservation newRes = currentGuest.makeReservation(selectedRoom, checkIn, checkOut);

            // Add selected extra amenities to the reservation
            for (Amenity extra : selectedExtraAmenities) {
                newRes.addChosenAmenity(extra);
            }

            // Generate invoice immediately (it will include the extras via calculateTotal)
            Invoice inv = new Invoice(newRes);
            newRes.setInvoice(inv);

            double totalEstimate = inv.calculateTotal();

            // Build extras summary for the confirmation message
            String extrasSummary = "";
            if (!selectedExtraAmenities.isEmpty()) {
                extrasSummary = "\nExtras: " + selectedExtraAmenities.stream()
                        .map(Amenity::getName)
                        .collect(Collectors.joining(", "));
            }

            showAlert(Alert.AlertType.INFORMATION, "Booking Successful!",
                    "Room #" + selectedRoom.getRoomNumber() + " has been reserved!\n\n"
                            + "Check-in: " + checkIn + "\n"
                            + "Check-out: " + checkOut
                            + extrasSummary + "\n"
                            + "Estimated Total (incl. tax): $" + String.format("%.2f", totalEstimate));

            // Clear extras after booking
            selectedExtraAmenities.clear();
            extraAmenityCombo.setValue(null);
            updateSelectedExtrasLabel();

            // Refresh the search to remove the now-booked room
            handleSearch();

        } catch (InvalidDateException e) {
            showAlert(Alert.AlertType.ERROR, "Booking Failed", e.getMessage());
        }
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

    //Handles adding an extra amenity to the booking
    @FXML
    private void handleAddExtraAmenity() {
        Room selectedRoom = roomsTable.getSelectionModel().getSelectedItem();
        if (selectedRoom == null) {
            showAlert(Alert.AlertType.WARNING, "No Room Selected",
                    "Please select a room from the table first, then add extras.");
            return;
        }
        if (extraAmenityCombo.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "No Amenity Selected",
                    "Please select an extra amenity from the dropdown.");
            return;
        }
        int idx = extraAmenityCombo.getSelectionModel().getSelectedIndex();
        Amenity selectedAmenity = HotelDatabase.extraAmenities.get(idx);

        // Check for duplicates in selected extras
        for (Amenity a : selectedExtraAmenities) {
            if (a.getName().equalsIgnoreCase(selectedAmenity.getName())) {
                showAlert(Alert.AlertType.INFORMATION, "Already Added",
                        "'" + selectedAmenity.getName() + "' is already in your extras list.");
                return;
            }
        }
        // Check if room already has this amenity as a default
        for (Amenity a : selectedRoom.getAmenities()) {
            if (a.getName().equalsIgnoreCase(selectedAmenity.getName())) {
                showAlert(Alert.AlertType.INFORMATION, "Already Included",
                        "'" + selectedAmenity.getName() + "' is already included with this room.");
                return;
            }
        }

        selectedExtraAmenities.add(selectedAmenity);
        updateSelectedExtrasLabel();
    }

    //Handles clearing all selected extra amenities
    @FXML
    private void handleClearExtras() {
        selectedExtraAmenities.clear();
        extraAmenityCombo.setValue(null);
        updateSelectedExtrasLabel();
    }

    //Updates the label showing currently selected extra amenities
    private void updateSelectedExtrasLabel() {
        if (selectedExtraAmenities.isEmpty()) {
            selectedExtrasLabel.setText("No extras selected");
            selectedExtrasLabel.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 13px;");
        } else {
            double totalExtra = 0;
            StringBuilder sb = new StringBuilder("Selected: ");
            for (int i = 0; i < selectedExtraAmenities.size(); i++) {
                Amenity a = selectedExtraAmenities.get(i);
                if (i > 0) sb.append(", ");
                sb.append(a.getName()).append(" ($").append(String.format("%.2f", a.getPrice())).append("/night)");
                totalExtra += a.getPrice();
            }
            sb.append("  |  Extra cost: $").append(String.format("%.2f", totalExtra)).append("/night");
            selectedExtrasLabel.setText(sb.toString());
            selectedExtrasLabel.setStyle("-fx-text-fill: #f39c12; -fx-font-size: 13px;");
        }
    }
}