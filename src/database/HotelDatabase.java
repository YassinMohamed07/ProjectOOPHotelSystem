package database;

import models.*;
import java.sql.*;
import java.util.ArrayList;

public class HotelDatabase {
    public static ArrayList<Staff> staff = new ArrayList<>();
    public static ArrayList<Guest> guests = new ArrayList<>();
    public static ArrayList<Room> rooms = new ArrayList<>();
    public static ArrayList<Reservation> reservations = new ArrayList<>();
    public static ArrayList<Invoice> invoices = new ArrayList<>();
    public static ArrayList<RoomType> roomTypes = new ArrayList<>();
    public static ArrayList<Amenity> allAmenities = new ArrayList<>();
    public static ArrayList<Amenity> singleDefaults = new ArrayList<>();
    public static ArrayList<Amenity> doubleDefaults = new ArrayList<>();
    public static ArrayList<Amenity> suiteDefaults = new ArrayList<>();
    public static ArrayList<Amenity> extraAmenities = new ArrayList<>();

    private static final String URL = "jdbc:mysql://127.0.0.1:3306/hotel_db?useSSL=false&serverTimezone=UTC";

    private static final String USER = "root";
    private static final String PASSWORD = "Seif.2007"; // <-- REMEMBER TO UPDATE YOUR PASSWORD HERE

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void initialize() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            loadAmenities();
            loadRoomTypes();
            loadRooms();
            loadUsers();
            loadReservations();
            loadInvoices();
        } catch (Exception e) {
            System.err.println("Database initialization failed!");
            e.printStackTrace();
        }
    }

    public static void loadAmenities() {
        allAmenities.clear(); singleDefaults.clear(); doubleDefaults.clear(); suiteDefaults.clear(); extraAmenities.clear();
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement("SELECT * FROM Amenity"); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) allAmenities.add(new Amenity(rs.getString("name"), rs.getDouble("price")));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        for (Amenity a : allAmenities) {
            if (a.getPrice() > 0) extraAmenities.add(a);
            else { singleDefaults.add(a); doubleDefaults.add(a); suiteDefaults.add(a); }
        }
        doubleDefaults.removeIf(a -> a.getName().equalsIgnoreCase("PREMIUM BATHROBES") || a.getName().equalsIgnoreCase("COMPLIMENTARY MINI-BAR") || a.getName().equalsIgnoreCase("SEPARATE LOUNGE AREA"));
        singleDefaults.removeIf(a -> a.getName().equalsIgnoreCase("COFFEE & TEA STATION") || a.getName().equalsIgnoreCase("PREMIUM BATHROBES") || a.getName().equalsIgnoreCase("COMPLIMENTARY MINI-BAR") || a.getName().equalsIgnoreCase("SEPARATE LOUNGE AREA"));
    }

    public static void loadRoomTypes() {
        roomTypes.clear();
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement("SELECT * FROM RoomType"); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) roomTypes.add(new RoomType(rs.getString("typeName"), rs.getDouble("basePrice")));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void loadRooms() {
        rooms.clear();
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement("SELECT * FROM Room"); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int rNum = rs.getInt("roomNumber");
                String tName = rs.getString("typeName"); // <-- FIXED: Extracted outside the lambda!

                RoomType type = roomTypes.stream().filter(rt -> rt.getTypeName().equals(tName)).findFirst().orElse(null);
                Room r = new Room(rNum, type);
                r.getAmenities().clear(); // Clear constructor defaults, load pure state from DB

                try (PreparedStatement ps2 = c.prepareStatement("SELECT amenityName FROM RoomAmenities WHERE roomNumber=?")) {
                    ps2.setInt(1, rNum);
                    try (ResultSet rs2 = ps2.executeQuery()) {
                        while (rs2.next()) {
                            String aName = rs2.getString("amenityName");
                            allAmenities.stream().filter(a -> a.getName().equals(aName)).findFirst().ifPresent(r::addAmenity);
                        }
                    }
                }
                rooms.add(r);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void loadUsers() {
        guests.clear(); staff.clear();
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement("SELECT * FROM Guest"); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) guests.add(new Guest(rs.getString("username"), rs.getString("password"), rs.getDate("dob").toLocalDate(), Gender.valueOf(rs.getString("gender")), rs.getDouble("balance"), rs.getString("address"), rs.getString("roomPreferences")));
        } catch (Exception e) {
            e.printStackTrace();
        }

        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement("SELECT * FROM Staff"); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Role role = Role.valueOf(rs.getString("role"));
                if (role == Role.ADMIN) staff.add(new Admin(rs.getString("username"), rs.getString("password"), rs.getDate("dob").toLocalDate(), rs.getInt("workingHours")));
                else staff.add(new Receptionist(rs.getString("username"), rs.getString("password"), rs.getDate("dob").toLocalDate(), rs.getInt("workingHours")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadReservations() {
        reservations.clear();
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement("SELECT * FROM Reservation"); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Reservation res = new Reservation();
                res.setId(rs.getInt("id"));
                String gName = rs.getString("guestUsername");
                res.setGuest(guests.stream().filter(g -> g.getUsername().equals(gName)).findFirst().orElse(null));
                int rNum = rs.getInt("roomNumber");
                res.setRoom(rooms.stream().filter(rm -> rm.getRoomNumber() == rNum).findFirst().orElse(null));
                res.forceSetCheckInDate(rs.getDate("checkInDate").toLocalDate());
                res.forceSetCheckOutDate(rs.getDate("checkOutDate").toLocalDate());
                res.setReservationStatus(ReservationStatus.valueOf(rs.getString("status")));
                res.setCheckedIn(rs.getBoolean("checkedIn"));
                res.setCheckedOut(rs.getBoolean("checkedOut"));
                res.setPaid(rs.getBoolean("isPaid"));

                try (PreparedStatement ps2 = c.prepareStatement("SELECT amenityName FROM ReservationAmenities WHERE reservationId=?")) {
                    ps2.setInt(1, res.getId());
                    try (ResultSet rs2 = ps2.executeQuery()) {
                        while (rs2.next()) {
                            String aName = rs2.getString("amenityName");
                            allAmenities.stream().filter(a -> a.getName().equals(aName)).findFirst().ifPresent(res::addChosenAmenity);
                        }
                    }
                }
                reservations.add(res);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void loadInvoices() {
        invoices.clear();
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement("SELECT * FROM Invoice"); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int resId = rs.getInt("reservationId");
                Reservation res = reservations.stream().filter(r -> r.getId() == resId).findFirst().orElse(null);
                if (res != null) {
                    Invoice inv = new Invoice(res);
                    inv.setPaymentmethod(PaymentMethod.valueOf(rs.getString("paymentMethod")));
                    res.setInvoice(inv);
                    invoices.add(inv);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // --- CRUD DB SYNC METHODS ---
    public static void addRoom(Room r) {
        rooms.add(r);
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement("INSERT INTO Room(roomNumber, typeName) VALUES(?,?)")) {
            ps.setInt(1, r.getRoomNumber()); ps.setString(2, r.getType().getTypeName()); ps.executeUpdate();
            for (Amenity a : r.getAmenities()) addAmenityToRoom(r, a);
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public static void updateRoom(Room r) {
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement("UPDATE Room SET typeName=? WHERE roomNumber=?")) {
            ps.setString(1, r.getType().getTypeName()); ps.setInt(2, r.getRoomNumber()); ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public static void deleteRoom(Room r) {
        rooms.remove(r);
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement("DELETE FROM Room WHERE roomNumber=?")) {
            ps.setInt(1, r.getRoomNumber()); ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public static void addAmenityToRoom(Room r, Amenity a) {
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement("INSERT IGNORE INTO RoomAmenities(roomNumber, amenityName) VALUES(?,?)")) {
            ps.setInt(1, r.getRoomNumber()); ps.setString(2, a.getName()); ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public static void removeAmenityFromRoom(Room r, Amenity a) {
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement("DELETE FROM RoomAmenities WHERE roomNumber=? AND amenityName=?")) {
            ps.setInt(1, r.getRoomNumber()); ps.setString(2, a.getName()); ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public static void addAmenity(Amenity a) {
        allAmenities.add(a);
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement("INSERT INTO Amenity(name, price) VALUES(?,?)")) {
            ps.setString(1, a.getName()); ps.setDouble(2, a.getPrice()); ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public static void updateAmenity(Amenity a) {
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement("UPDATE Amenity SET price=? WHERE name=?")) {
            ps.setDouble(1, a.getPrice()); ps.setString(2, a.getName()); ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public static void deleteAmenity(Amenity a) {
        allAmenities.remove(a);
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement("DELETE FROM Amenity WHERE name=?")) {
            ps.setString(1, a.getName()); ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public static void addRoomType(RoomType rt) {
        roomTypes.add(rt);
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement("INSERT INTO RoomType(typeName, basePrice) VALUES(?,?)")) {
            ps.setString(1, rt.getTypeName()); ps.setDouble(2, rt.getBasePrice()); ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public static void updateRoomType(RoomType rt) {
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement("UPDATE RoomType SET basePrice=? WHERE typeName=?")) {
            ps.setDouble(1, rt.getBasePrice()); ps.setString(2, rt.getTypeName()); ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public static void deleteRoomType(RoomType rt) {
        roomTypes.remove(rt);
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement("DELETE FROM RoomType WHERE typeName=?")) {
            ps.setString(1, rt.getTypeName()); ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public static void addGuest(Guest g) {
        guests.add(g);
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement("INSERT INTO Guest(username, password, dob, gender, balance, address, roomPreferences) VALUES(?,?,?,?,?,?,?)")) {
            ps.setString(1, g.getUsername()); ps.setString(2, g.getPassword()); ps.setDate(3, java.sql.Date.valueOf(g.getDateOfBirth())); ps.setString(4, g.getGender().toString()); ps.setDouble(5, g.getBalance()); ps.setString(6, g.getAddress()); ps.setString(7, g.getRoomPreferences()); ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public static void updateGuest(Guest g) {
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement("UPDATE Guest SET balance=?, address=?, roomPreferences=? WHERE username=?")) {
            ps.setDouble(1, g.getBalance()); ps.setString(2, g.getAddress()); ps.setString(3, g.getRoomPreferences()); ps.setString(4, g.getUsername()); ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public static void addStaff(Staff s) {
        staff.add(s);
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement("INSERT INTO Staff(username, password, dob, role, workingHours) VALUES(?,?,?,?,?)")) {
            ps.setString(1, s.getUsername()); ps.setString(2, s.getPassword()); ps.setDate(3, java.sql.Date.valueOf(s.getDateOfBirth())); ps.setString(4, s.getRole().toString()); ps.setInt(5, s.getWorkingHours()); ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public static void addReservation(Reservation r) {
        reservations.add(r);
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement("INSERT INTO Reservation(guestUsername, roomNumber, checkInDate, checkOutDate, status, checkedIn, checkedOut, isPaid) VALUES(?,?,?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, r.getGuest().getUsername()); ps.setInt(2, r.getRoom().getRoomNumber()); ps.setDate(3, java.sql.Date.valueOf(r.getCheckInDate())); ps.setDate(4, java.sql.Date.valueOf(r.getCheckOutDate())); ps.setString(5, r.getReservationStatus().toString()); ps.setBoolean(6, r.isCheckedIn()); ps.setBoolean(7, r.isCheckedOut()); ps.setBoolean(8, r.isPaid()); ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) { if (rs.next()) r.setId(rs.getInt(1)); }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public static void updateReservation(Reservation r) {
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement("UPDATE Reservation SET status=?, checkedIn=?, checkedOut=?, isPaid=? WHERE id=?")) {
            ps.setString(1, r.getReservationStatus().toString()); ps.setBoolean(2, r.isCheckedIn()); ps.setBoolean(3, r.isCheckedOut()); ps.setBoolean(4, r.isPaid()); ps.setInt(5, r.getId()); ps.executeUpdate();

            try (PreparedStatement del = c.prepareStatement("DELETE FROM ReservationAmenities WHERE reservationId=?")) {
                del.setInt(1, r.getId()); del.executeUpdate();
            }
            try (PreparedStatement ins = c.prepareStatement("INSERT INTO ReservationAmenities(reservationId, amenityName) VALUES(?,?)")) {
                for (Amenity a : r.getChosenAmenities()) { ins.setInt(1, r.getId()); ins.setString(2, a.getName()); ins.executeUpdate(); }
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public static void addInvoice(Invoice i) {
        if (!invoices.contains(i)) invoices.add(i);
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement("INSERT INTO Invoice(reservationId, paymentMethod) VALUES(?,?) ON DUPLICATE KEY UPDATE paymentMethod=?")) {
            ps.setInt(1, i.getReservation().getId()); ps.setString(2, i.getPaymentmethod().toString()); ps.setString(3, i.getPaymentmethod().toString()); ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public static void updateInvoice(Invoice i) {
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement("UPDATE Invoice SET paymentMethod=? WHERE reservationId=?")) {
            ps.setString(1, i.getPaymentmethod().toString()); ps.setInt(2, i.getReservation().getId()); ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }
}