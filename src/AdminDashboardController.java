import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import models.*;
import database.HotelDatabase;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

//Controller for the Admin Dashboard.
//Implements room CRUD, amenity CRUD, and view-all operations.
//References Admin class methods for business logic.
public class AdminDashboardController implements Initializable, StaffAware {

    @FXML private Label adminInfoLabel;

    // Room Management
    @FXML private TextField roomNumField;
    @FXML private ComboBox<String> roomTypeCombo;
    @FXML private ComboBox<String> roomAmenityCombo;
    @FXML private TableView<Room> roomsTable;
    @FXML private TableColumn<Room, String> colRoomNum;
    @FXML private TableColumn<Room, String> colRoomTypeName;
    @FXML private TableColumn<Room, String> colRoomBasePrice;
    @FXML private TableColumn<Room, String> colRoomAmenities;
    @FXML private TableColumn<Room, String> colRoomTotalPrice;
    @FXML private Label roomStatusLabel;

    // Amenity Management
    @FXML private TextField amenityNameField;
    @FXML private TextField amenityPriceField;
    @FXML private TableView<Amenity> amenitiesTable;
    @FXML private TableColumn<Amenity, String> colAmenityName;
    @FXML private TableColumn<Amenity, String> colAmenityPrice;
    @FXML private Label amenityStatusLabel;

    // View All
    @FXML private TableView<Guest> guestsTable;
    @FXML private TableColumn<Guest, String> colGuestUsername;
    @FXML private TableColumn<Guest, String> colGuestGender;
    @FXML private TableColumn<Guest, String> colGuestAge;
    @FXML private TableColumn<Guest, String> colGuestBalance;
    @FXML private TableColumn<Guest, String> colGuestAddress;

    @FXML private TableView<RoomType> roomTypesTable;
    @FXML private TableColumn<RoomType, String> colRTName;
    @FXML private TableColumn<RoomType, String> colRTPrice;

    private Staff currentStaff;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Room table columns
        colRoomNum.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().getRoomNumber())));
        colRoomTypeName.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getType().getTypeName()));
        colRoomBasePrice.setCellValueFactory(d -> new SimpleStringProperty("$" + String.format("%.2f", d.getValue().getType().getBasePrice())));
        colRoomAmenities.setCellValueFactory(d -> {String names = d.getValue().getAmenities().stream().map(Amenity::getName).collect(Collectors.joining(", "));return new SimpleStringProperty(names.isEmpty() ? "None" : names);});
        colRoomTotalPrice.setCellValueFactory(d -> new SimpleStringProperty("$" + String.format("%.2f", d.getValue().totalRoomPricePerOneNight())));
        colAmenityName.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getName()));
        colAmenityPrice.setCellValueFactory(d -> new SimpleStringProperty("$" + String.format("%.2f", d.getValue().getPrice())));
        colGuestUsername.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getUsername()));
        colGuestGender.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getGender().toString()));
        colGuestAge.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().getAge())));
        colGuestBalance.setCellValueFactory(d -> new SimpleStringProperty("$" + String.format("%.2f", d.getValue().getBalance())));
        colGuestAddress.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getAddress()));
        colRTName.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getTypeName()));
        colRTPrice.setCellValueFactory(d -> new SimpleStringProperty("$" + String.format("%.2f", d.getValue().getBasePrice())));
        roomStatusLabel.setText("");
        amenityStatusLabel.setText("");
    }
    @Override
    public void setStaff(Staff staff) {
        this.currentStaff = staff;
        adminInfoLabel.setText("Admin: " + staff.getUsername());
        populateCombos();
        refreshAll();
    }
    private void populateCombos() {
        roomTypeCombo.getItems().clear();
        for (RoomType rt : HotelDatabase.roomTypes) {
            roomTypeCombo.getItems().add(rt.getTypeName() + " ($" + rt.getBasePrice() + ")");
        }
        roomAmenityCombo.getItems().clear();
        for (Amenity a : HotelDatabase.allAmenities) {
            roomAmenityCombo.getItems().add(a.getName());
        }
    }
    // Room Management (references Admin.addRoom, deleteRoom, updateRoom)
    @FXML
    private void handleAddRoom() {
        String numStr = roomNumField.getText().trim();
        if (numStr.isEmpty() || roomTypeCombo.getValue() == null) {
            setRoomStatus("Please enter room number and select a type.", true);
            return;
        }
        int roomNum;
        try { roomNum = Integer.parseInt(numStr); }
        catch (NumberFormatException e) { setRoomStatus("Room number must be a number.", true); return; }
        for (Room r : HotelDatabase.rooms) {
            if (r.getRoomNumber() == roomNum) {setRoomStatus("Room #" + roomNum + " already exists!", true); return;
            }
        }
        int idx = roomTypeCombo.getSelectionModel().getSelectedIndex();
        RoomType selectedType = HotelDatabase.roomTypes.get(idx);
        Room newRoom = new Room(roomNum, selectedType);
        HotelDatabase.rooms.add(newRoom);
        setRoomStatus("Room #" + roomNum + " added as " + selectedType.getTypeName() + ".", false);
        refreshRooms(); roomNumField.clear();
    }
    @FXML
    private void handleUpdateRoomType() {
        Room selected = roomsTable.getSelectionModel().getSelectedItem();
        if (selected == null) { setRoomStatus("Select a room from the table first.", true); return; }
        if (roomTypeCombo.getValue() == null) { setRoomStatus("Select a new room type.", true); return; }

        int idx = roomTypeCombo.getSelectionModel().getSelectedIndex();
        RoomType newType = HotelDatabase.roomTypes.get(idx);
        selected.setType(newType);
        setRoomStatus("Room #" + selected.getRoomNumber() + " updated to " + newType.getTypeName() + ".", false);
        refreshRooms();
    }
    @FXML
    private void handleAddAmenityToRoom() {
        Room selected = roomsTable.getSelectionModel().getSelectedItem();
        if (selected == null) { setRoomStatus("Select a room from the table first.", true); return; }
        if (roomAmenityCombo.getValue() == null) { setRoomStatus("Select an amenity to add.", true); return; }

        String amenityName = roomAmenityCombo.getValue();
        // Check if already has this amenity
        for (Amenity a : selected.getAmenities()) {
            if (a.getName().equalsIgnoreCase(amenityName)) {setRoomStatus("Room already has " + amenityName + ".", true); return;
            }
        }
        for (Amenity a : HotelDatabase.allAmenities) {
            if (a.getName().equalsIgnoreCase(amenityName)) {
                selected.addAmenity(a);
                setRoomStatus(amenityName + " added to Room #" + selected.getRoomNumber() + ".", false);
                refreshRooms(); return;
            }
        }
    }
    @FXML
    private void handleDeleteRoom() {
        Room selected = roomsTable.getSelectionModel().getSelectedItem();
        if (selected == null) { setRoomStatus("Select a room to delete.", true); return; }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Delete Room #" + selected.getRoomNumber() + "?");
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) {
                HotelDatabase.rooms.remove(selected);
                setRoomStatus("Room #" + selected.getRoomNumber() + " deleted.", false);
                refreshRooms();
            }
        });
    }
    // Amenity management (references Admin.addAmenity, updateAmenity, deleteAmenity) ===
    @FXML
    private void handleAddAmenity() {
        String name = amenityNameField.getText().trim();
        String priceStr = amenityPriceField.getText().trim();
        if (name.isEmpty() || priceStr.isEmpty()) { setAmenityStatus("Enter name and price.", true); return; }

        double price;
        try { price = Double.parseDouble(priceStr); if (price < 0) { setAmenityStatus("Price cannot be negative.", true); return; } }
        catch (NumberFormatException e) { setAmenityStatus("Invalid price.", true); return; }

        for (Amenity a : HotelDatabase.allAmenities) {
            if (a.getName().equalsIgnoreCase(name)) { setAmenityStatus("Amenity '" + name + "' already exists!", true); return; }
        }
        HotelDatabase.allAmenities.add(new Amenity(name, price));
        setAmenityStatus("Amenity '" + name + "' added ($" + String.format("%.2f", price) + ").", false);
        refreshAmenities(); populateCombos();
        amenityNameField.clear(); amenityPriceField.clear();
    }
    @FXML
    private void handleUpdateAmenity() {
        Amenity selected = amenitiesTable.getSelectionModel().getSelectedItem();
        if (selected == null) { setAmenityStatus("Select an amenity from the table.", true); return; }
        String priceStr = amenityPriceField.getText().trim();
        if (priceStr.isEmpty()) { setAmenityStatus("Enter new price.", true); return; }

        double newPrice;
        try { newPrice = Double.parseDouble(priceStr); if (newPrice < 0) { setAmenityStatus("Price cannot be negative.", true); return; } }
        catch (NumberFormatException e) { setAmenityStatus("Invalid price.", true); return; }

        selected.setPrice(newPrice);
        setAmenityStatus("'" + selected.getName() + "' price updated to $" + String.format("%.2f", newPrice) + ".", false);
        refreshAmenities(); amenityPriceField.clear();
    }
    @FXML
    private void handleDeleteAmenity() {
        Amenity selected = amenitiesTable.getSelectionModel().getSelectedItem();
        if (selected == null) { setAmenityStatus("Select an amenity to delete.", true); return; }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Delete amenity '" + selected.getName() + "'?");
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) {
                HotelDatabase.allAmenities.remove(selected);
                int affected = 0;
                for (Room r : HotelDatabase.rooms) {
                    if (r.getAmenities().remove(selected)) affected++;
                }
                setAmenityStatus("'" + selected.getName() + "' deleted. Removed from " + affected + " room(s).", false);
                refreshAmenities(); populateCombos();
            }
        });
    }
    // View all / refresh
    @FXML
    private void handleRefreshAll() { refreshAll(); }
    private void refreshAll() { refreshRooms(); refreshAmenities(); refreshGuests(); refreshRoomTypes(); }
    private void refreshRooms() { roomsTable.setItems(FXCollections.observableArrayList(HotelDatabase.rooms)); }
    private void refreshAmenities() { amenitiesTable.setItems(FXCollections.observableArrayList(HotelDatabase.allAmenities)); }
    private void refreshGuests() { guestsTable.setItems(FXCollections.observableArrayList(HotelDatabase.guests)); }
    private void refreshRoomTypes() { roomTypesTable.setItems(FXCollections.observableArrayList(HotelDatabase.roomTypes)); }
    @FXML
    private void handleLogout() { SceneNavigator.navigateTo("LoginRegister.fxml"); }

    private void setRoomStatus(String msg, boolean error) {
        roomStatusLabel.setText(msg);
        roomStatusLabel.getStyleClass().setAll("label", error ? "status-error" : "status-success");
    }
    private void setAmenityStatus(String msg, boolean error) {
        amenityStatusLabel.setText(msg);
        amenityStatusLabel.getStyleClass().setAll("label", error ? "status-error" : "status-success");
    }
}