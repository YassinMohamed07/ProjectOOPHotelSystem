package utils;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TableColumn;
import models.Invoice;
import models.Reservation;

//Shared utility for configuring common TableView column cell value factories.
//Eliminates the identical reservation column setup duplicated across
//GuestDashboardController, ReservationController, CheckoutController,
//and ReceptionistDashboardController.

public class TableColumnHelper {

    public static void setupReservationColumns(
            TableColumn<Reservation, String> colRoomNum,
            TableColumn<Reservation, String> colRoomType,
            TableColumn<Reservation, String> colCheckIn,
            TableColumn<Reservation, String> colCheckOut,
            TableColumn<Reservation, String> colStatus,
            TableColumn<Reservation, String> colPaid) {
            colRoomNum.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getRoom().getRoomNumber())));
            colRoomType.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getRoom().getType().getTypeName()));
            colCheckIn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCheckInDate().toString()));
            colCheckOut.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCheckOutDate().toString()));
            colStatus.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getReservationStatus().toString()));
            colPaid.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().isPaid() ? "Yes" : "No"));
    }
    public static void setupInvoiceTotalColumn(TableColumn<Reservation, String> colTotal) {
        colTotal.setCellValueFactory(data -> {
            Invoice invoice = data.getValue().getInvoice();
            if (invoice != null) {return new SimpleStringProperty("$" + String.format("%.2f", invoice.calculateTotal()));
            }
            return new SimpleStringProperty("N/A");
        });
    }
}
