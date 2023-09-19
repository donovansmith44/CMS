package Interfaces;

import javafx.scene.control.Alert;

/**
 Provides a general alert functionality to implementing classes.
 */
public interface Alerts {
    /**
     Generates an alert of any AlertType and displays it with a custom title,
     header, and content/body, and finally shows the alert.
     */
    default void showAlert(Alert.AlertType alertType, String alertTitle, String alertContent) {
        Alert alert = new Alert(alertType);
        alert.setTitle(alertTitle);
        alert.setContentText(alertContent);
        alert.showAndWait();
    }
}
