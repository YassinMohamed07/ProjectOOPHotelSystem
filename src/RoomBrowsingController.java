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
    private List<CheckBox> amenityCheckBoxes = new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Set up room type filter options
        filterRoomType.getItems().add("All Types");
        for (Roomtypee type : Roomtypee.values()) {
            filterRoomType.getItems().add(type.name());
        }
        filterRoomType.setValue("All Types");

        // Set up amenity checkboxes dynamically from database
        for (Amenity amenity : HotelDatabase.allAmenities) {
            CheckBox cb = new CheckBox(amenity.getName());
            amenityCheckBoxes.add(cb);
            amenitiesPane.getChildren().add(cb);
        }
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
    }
    @Override
    public void setGuest(Guest guest) {
        this.currentGuest = guest;
        guestInfoLabel.setText("Logged in as: " + guest.getUsername());
    }
    //Handles the Search button — filters rooms based on criteria.
    @FXML
    private void handleSearch() {
        // Validate dates are provided
        LocalDate checkIn = filterCheckIn.getValue();
        LocalDate checkOut = filterCheckOut.getValue();

        if (checkIn == null || checkOut == null) {
            showAlert(Alert.AlertType.WARNING, "Missing Dates",
                    "Please select both check-in and check-out dates.");
            return;
        }
        // Parse max price
        double maxPrice = 0;
        String priceText = filterMaxPrice.getText().trim();
        if (!priceText.isEmpty()) {
            try {
                maxPrice = Double.parseDouble(priceText);
                if (maxPrice < 0) {
                    showAlert(Alert.AlertType.WARNING, "Invalid Price",
                            "Max price cannot be negative.");
                    return;
                }
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.WARNING, "Invalid Price",
                        "Please enter a valid number for max price.");
                return;
            }
        }
        // Determine room type filter
        Roomtypee selectedType = null;
        String typeStr = filterRoomType.getValue();
        if (typeStr != null && !typeStr.equals("All Types")) {
            selectedType = Roomtypee.valueOf(typeStr);
        }
        // Search using the Guest model's existing method
        try {
            List<Room> results = Guest.searchAvailableRooms(checkIn, checkOut, selectedType, maxPrice);

            // Apply amenity filter (additional client-side filtering)
            List<String> selectedAmenities = new ArrayList<>();
            for (CheckBox cb : amenityCheckBoxes) {
                if (cb.isSelected()) {
                    selectedAmenities.add(cb.getText());
                }
            }

            if (!selectedAmenities.isEmpty()) {
                results = results.stream().filter(room -> {
                    List<String> roomAmenityNames = room.getAmenities().stream()
                            .map(Amenity::getName)
                            .collect(Collectors.toList());
                    return roomAmenityNames.containsAll(selectedAmenities);
                }).collect(Collectors.toList());
            }

            // Update table
            ObservableList<Room> observableRooms = FXCollections.observableArrayList(results);
            roomsTable.setItems(observableRooms);

            // Update result count
            if (results.isEmpty()) {
                resultCountLabel.setText("No rooms found matching your criteria.");
            } else {
                resultCountLabel.setText(results.size() + " room(s) found");
            }

        } catch (InvalidDateException e) {
            showAlert(Alert.AlertType.ERROR, "Date Error", e.getMessage());
        }
    }
    //Clears all filter fields.
    @FXML
    private void handleClearFilters() {
        filterRoomType.setValue("All Types");
        filterMaxPrice.clear();
        filterCheckIn.setValue(null);
        filterCheckOut.setValue(null);
        for (CheckBox cb : amenityCheckBoxes) {
            cb.setSelected(false);
        }
        roomsTable.getItems().clear();
        resultCountLabel.setText("");
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

            // Generate invoice immediately
            Invoice inv = new Invoice(newRes);
            newRes.setInvoice(inv);

            double totalEstimate = inv.calculateTotal();

            showAlert(Alert.AlertType.INFORMATION, "Booking Successful!",
                    "Room #" + selectedRoom.getRoomNumber() + " has been reserved!\n\n"
                            + "Check-in: " + checkIn + "\n"
                            + "Check-out: " + checkOut + "\n"
                            + "Estimated Total (incl. tax): $" + String.format("%.2f", totalEstimate));

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
}