package Interfaces;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

/**
 Provides inter-controller navigation capabilities to all controller classes.
 */
public interface Navigable {
    /**
     Takes an ActionEvent object and a path to an FXML file, and changes the stage to reflect
     the new file.
     */
    @FXML
    default void navigateTo(ActionEvent actionEvent, String resource) {
        try {
            Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
            Parent scene = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(resource)));
            stage.setScene(new Scene(scene));
            stage.show();
        } catch (IOException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     Takes an ActionEvent object and a path to an FXML file, and changes the stage to the Landing Page.
     */
    @FXML
    default void goToLanding(ActionEvent actionEvent) {
        navigateTo(actionEvent, "/view/LandingPage.fxml");
    }
}
