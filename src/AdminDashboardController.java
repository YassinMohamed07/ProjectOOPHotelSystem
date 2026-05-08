import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import models.*;
import database.HotelDatabase;
import exceptions.*;
import utils.StatusLabelHelper;
import utils.ValidationUtil;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class AdminDashboardController implements Initializable, StaffAware {

    @FXML private Label adminInfoLabel;

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

    @FXML private TextField amenityNameField;
    @FXML private TextField amenityPriceField;
    @FXML private TableView<Amenity> amenitiesTable;
    @FXML private TableColumn<Amenity, String> colAmenityName;
    @FXML private TableColumn<Amenity, String> colAmenityPrice;
    @FXML private Label amenityStatusLabel;

    @FXML private TableView<Guest> guestsTable;
    @FXML private TableColumn<Guest, String> colGuestUsername;
    @FXML private TableColumn<Guest, String> colGuestGender;
    @FXML private TableColumn<Guest, String> colGuestAge;
    @FXML private TableColumn<Guest, String> colGuestBalance;
    @FXML private TableColumn<Guest, String> colGuestAddress;
    @FXML private TableView<RoomType> roomTypesTable;
    @FXML private TableColumn<RoomType, String> colRTName;
    @FXML private TableColumn<RoomType, String> colRTPrice;

    @FXML private TextField staffRegUsername;
    @FXML private PasswordField staffRegPassword;
    @FXML private DatePicker staffRegDob;
    @FXML private ComboBox<Role> staffRegRole;
    @FXML private TextField staffRegHours;
    @FXML private Label staffRegStatusLabel;
    @FXML private TableView<Staff> staffTable;
    @FXML private TableColumn<Staff, String> colStaffUsername;
    @FXML private TableColumn<Staff, String> colStaffRole;
    @FXML private TableColumn<Staff, String> colStaffHours;
    @FXML private TableColumn<Staff, String> colStaffDob;

    private Staff currentStaff;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupRoomColumns();
        setupAmenityColumns();
        setupGuestColumns();
        setupRoomTypeColumns();
        setupStaffColumns();
        staffRegRole.getItems().addAll(Role.values());
        roomStatusLabel.setText("");
        amenityStatusLabel.setText("");
        staffRegStatusLabel.setText("");
    }
    private void setupRoomColumns() {
        colRoomNum.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().getRoomNumber())));
        colRoomTypeName.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getType().getTypeName()));
        colRoomBasePrice.setCellValueFactory(d -> new SimpleStringProperty("$" + String.format("%.2f", d.getValue().getType().getBasePrice())));
        colRoomAmenities.setCellValueFactory(d -> {
            String names = d.getValue().getAmenities().stream().map(Amenity::getName).collect(Collectors.joining(", "));
            return new SimpleStringProperty(names.isEmpty() ? "None" : names);});
        colRoomTotalPrice.setCellValueFactory(d -> new SimpleStringProperty("$" + String.format("%.2f", d.getValue().totalRoomPricePerOneNight())));
    }
    private void setupAmenityColumns() {
        colAmenityName.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getName()));
        colAmenityPrice.setCellValueFactory(d -> new SimpleStringProperty("$" + String.format("%.2f", d.getValue().getPrice())));
    }
    private void setupGuestColumns() {
        colGuestUsername.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getUsername()));
        colGuestGender.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getGender().toString()));
        colGuestAge.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().getAge())));
        colGuestBalance.setCellValueFactory(d -> new SimpleStringProperty("$" + String.format("%.2f", d.getValue().getBalance())));
        colGuestAddress.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getAddress()));
    }
    private void setupRoomTypeColumns() {
        colRTName.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getTypeName()));
        colRTPrice.setCellValueFactory(d -> new SimpleStringProperty("$" + String.format("%.2f", d.getValue().getBasePrice())));
    }
    private void setupStaffColumns() {
        colStaffUsername.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getUsername()));
        colStaffRole.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getRole().toString()));
        colStaffHours.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().getWorkingHours())));
        colStaffDob.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getDateOfBirth().toString()));
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
            roomTypeCombo.getItems().add(
                    rt.getTypeName() + " ($" + rt.getBasePrice() + ")");
        }
        roomAmenityCombo.getItems().clear();
        for (Amenity a : HotelDatabase.allAmenities) {
            roomAmenityCombo.getItems().add(a.getName());
        }
    }
    // Room Management
    @FXML
    private void handleAddRoom() {
        String numStr = roomNumField.getText().trim();
        if (numStr.isEmpty() || roomTypeCombo.getValue() == null) {
            StatusLabelHelper.set(roomStatusLabel, "Please enter room number and select a type.", true);
            return;
        }
        int roomNum;
        try {
            roomNum = Integer.parseInt(numStr);
        } catch (NumberFormatException e) {
            StatusLabelHelper.set(roomStatusLabel, "Room number must be a number.", true);
            return;
        }
        for (Room r : HotelDatabase.rooms) {
            if (r.getRoomNumber() == roomNum) {
                StatusLabelHelper.set(roomStatusLabel, "Room #" + roomNum + " already exists!", true);
                return;
            }
        }
        int idx = roomTypeCombo.getSelectionModel().getSelectedIndex();
        RoomType selectedType = HotelDatabase.roomTypes.get(idx);
        Room newRoom = new Room(roomNum, selectedType);
        HotelDatabase.addRoom(newRoom);
        StatusLabelHelper.set(roomStatusLabel, "Room #" + roomNum + " added as " + selectedType.getTypeName() + ".", false);
        refreshRooms();
        roomNumField.clear();
    }
    @FXML
    private void handleUpdateRoomType() {
        Room selected = roomsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            StatusLabelHelper.set(roomStatusLabel, "Select a room from the table first.", true);
            return;
        }
        if (roomTypeCombo.getValue() == null) {
            StatusLabelHelper.set(roomStatusLabel, "Select a new room type.", true);
            return;
        }
        int idx = roomTypeCombo.getSelectionModel().getSelectedIndex();
        RoomType newType = HotelDatabase.roomTypes.get(idx);
        selected.setType(newType);
        HotelDatabase.updateRoom(selected);

        StatusLabelHelper.set(roomStatusLabel, "Room #" + selected.getRoomNumber() + " updated to " + newType.getTypeName() + ".", false);
        refreshRooms();
    }
    @FXML
    private void handleAddAmenityToRoom() {
        Room selected = roomsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            StatusLabelHelper.set(roomStatusLabel, "Select a room from the table first.", true);
            return;
        }
        if (roomAmenityCombo.getValue() == null) {
            StatusLabelHelper.set(roomStatusLabel, "Select an amenity to add.", true);
            return;
        }
        String amenityName = roomAmenityCombo.getValue();
        for (Amenity a : selected.getAmenities()) {
            if (a.getName().equalsIgnoreCase(amenityName)) {
                StatusLabelHelper.set(roomStatusLabel, "Room already has " + amenityName + ".", true);
                return;
            }
        }
        for (Amenity a : HotelDatabase.allAmenities) {
            if (a.getName().equalsIgnoreCase(amenityName)) {
                selected.addAmenity(a);
                HotelDatabase.addAmenityToRoom(selected, a);
                StatusLabelHelper.set(roomStatusLabel, amenityName + " added to Room #" + selected.getRoomNumber() + ".", false);
                refreshRooms();
                return;
            }
        }
    }
    @FXML
    private void handleDeleteRoom() {
        Room selected = roomsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            StatusLabelHelper.set(roomStatusLabel, "Select a room to delete.", true);
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Delete Room #" + selected.getRoomNumber() + "?");
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) {
                HotelDatabase.deleteRoom(selected);
                StatusLabelHelper.set(roomStatusLabel,
                        "Room #" + selected.getRoomNumber() + " deleted.", false);
                refreshRooms();
            }
        });
    }
    // Amenity Management
    @FXML
    private void handleAddAmenity() {
        String name = amenityNameField.getText().trim();
        String priceStr = amenityPriceField.getText().trim();
        if (name.isEmpty() || priceStr.isEmpty()) {
            StatusLabelHelper.set(amenityStatusLabel, "Enter name and price.", true);
            return;
        }
        double price;
        try {
            price = Double.parseDouble(priceStr);
            if (price < 0) {
                StatusLabelHelper.set(amenityStatusLabel, "Price cannot be negative.", true);
                return;
            }
        } catch (NumberFormatException e) {
            StatusLabelHelper.set(amenityStatusLabel, "Invalid price.", true);
            return;
        }
        for (Amenity a : HotelDatabase.allAmenities) {
            if (a.getName().equalsIgnoreCase(name)) {
                StatusLabelHelper.set(amenityStatusLabel, "Amenity '" + name + "' already exists!", true);
                return;
            }
        }
        Amenity newAmenity = new Amenity(name, price);
        HotelDatabase.addAmenity(newAmenity);
        StatusLabelHelper.set(amenityStatusLabel, "Amenity '" + name + "' added ($" + String.format("%.2f", price) + ").", false);
        refreshAmenities();
        populateCombos();
        amenityNameField.clear();
        amenityPriceField.clear();
    }
    @FXML
    private void handleUpdateAmenity() {
        Amenity selected = amenitiesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            StatusLabelHelper.set(amenityStatusLabel, "Select an amenity from the table.", true);
            return;
        }
        String priceStr = amenityPriceField.getText().trim();
        if (priceStr.isEmpty()) {
            StatusLabelHelper.set(amenityStatusLabel, "Enter new price.", true);
            return;
        }
        double newPrice;
        try {
            newPrice = Double.parseDouble(priceStr);
            if (newPrice < 0) {
                StatusLabelHelper.set(amenityStatusLabel, "Price cannot be negative.", true);
                return;
            }
        } catch (NumberFormatException e) {
            StatusLabelHelper.set(amenityStatusLabel, "Invalid price.", true);
            return;
        }
        selected.setPrice(newPrice);
        HotelDatabase.updateAmenity(selected);
        StatusLabelHelper.set(amenityStatusLabel,
                "'" + selected.getName() + "' price updated to $" + String.format("%.2f", newPrice) + ".", false);
        refreshAmenities();
        amenityPriceField.clear();
    }
    @FXML
    private void handleDeleteAmenity() {
        Amenity selected = amenitiesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            StatusLabelHelper.set(amenityStatusLabel, "Select an amenity to delete.", true);
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Delete amenity '" + selected.getName() + "'?");
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) {
                HotelDatabase.deleteAmenity(selected);
                int affected = 0;
                for (Room r : HotelDatabase.rooms) {
                    if (r.getAmenities().remove(selected)) {
                        affected++;
                    }
                }
                StatusLabelHelper.set(amenityStatusLabel, "'" + selected.getName() + "' deleted. Removed from " + affected + " room(s).", false);
                refreshAmenities();
                populateCombos();
            }
        });
    }
    // Staff Registration
    @FXML
    private void handleStaffRegister() {
        String username = staffRegUsername.getText().trim();
        String password = staffRegPassword.getText();
        LocalDate dob = staffRegDob.getValue();
        Role role = staffRegRole.getValue();
        String hoursStr = staffRegHours.getText().trim();
        if (username.isEmpty() || password.isEmpty() || dob == null || role == null || hoursStr.isEmpty()) {
            StatusLabelHelper.set(staffRegStatusLabel, "Please fill in all fields.", true);
            return;
        }
        int hours;
        try {
            hours = Integer.parseInt(hoursStr);
            if (hours <= 0) {
                StatusLabelHelper.set(staffRegStatusLabel, "Working hours must be positive.", true);
                return;
            }
        } catch (NumberFormatException e) {
            StatusLabelHelper.set(staffRegStatusLabel, "Invalid working hours. Enter a number.", true);
            return;
        }
        try {
            ValidationUtil.validateUsername(username);
            ValidationUtil.validateDateOfBirth(dob);
            ValidationUtil.validatePassword(password);
            if (role == Role.ADMIN) {
                Admin newAdmin = new Admin(username, password, dob, hours);
                HotelDatabase.addStaff(newAdmin);
            } else {
                Receptionist newRec = new Receptionist(
                        username, password, dob, hours);
                HotelDatabase.addStaff(newRec);
            }
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Staff Registration Successful");
            alert.setHeaderText(role + ": " + username + " registered!");
            alert.setContentText("The staff member can now login.");
            alert.showAndWait();
            StatusLabelHelper.set(staffRegStatusLabel, "Staff registered successfully!", false);
            staffRegUsername.clear();
            staffRegPassword.clear();
            staffRegDob.setValue(null);
            staffRegRole.setValue(null);
            staffRegHours.clear();
            refreshStaff();

        } catch (WeakPwordException | InvalidDateException e) {StatusLabelHelper.set(staffRegStatusLabel, e.getMessage(), true);}
    }
    // View All & Refreshes
    @FXML
    private void handleRefreshAll() {
        refreshAll();
    }
    private void refreshAll() {
        refreshRooms();
        refreshAmenities();
        refreshGuests();
        refreshRoomTypes();
        refreshStaff();
    }
    private void refreshRooms() {
        roomsTable.setItems(FXCollections.observableArrayList(HotelDatabase.rooms));
        roomsTable.refresh();
    }
    private void refreshAmenities() {
        amenitiesTable.setItems(FXCollections.observableArrayList(HotelDatabase.allAmenities));
        amenitiesTable.refresh();
    }
    private void refreshGuests() {
        guestsTable.setItems(FXCollections.observableArrayList(HotelDatabase.guests));
        guestsTable.refresh();
    }
    private void refreshRoomTypes() {
        roomTypesTable.setItems(FXCollections.observableArrayList(HotelDatabase.roomTypes));
        roomTypesTable.refresh();
    }
    private void refreshStaff() {
        staffTable.setItems(FXCollections.observableArrayList(HotelDatabase.staff));
        staffTable.refresh();
    }
    @FXML
    private void handleLogout() {
        SceneNavigator.navigateTo("LoginRegister.fxml");
    }
}