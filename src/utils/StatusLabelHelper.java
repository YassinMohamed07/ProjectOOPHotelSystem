package utils;
import javafx.scene.control.Label;

public class StatusLabelHelper {
    public static void set(Label label, String message, boolean isError) {
        label.setText(message);
        label.getStyleClass().setAll("label", isError ? "status-error" : "status-success");
    }
}
