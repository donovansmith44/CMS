package controller;

import Database.UserQuery;
import Database.Users;
import Interfaces.Alerts;
import Interfaces.Navigable;
import Interfaces.Logging;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Controller for user login, and access of the landing page.
 */
public class LoginScreen implements Initializable, Navigable, Alerts, Logging {
    public Button loginButton;
    public TextField userName;
    public TextField password;
    public Label userLocationLabel;
    public Label userLocation;
    public Label userNameLabel;
    public Label passwordLabel;
    public ResourceBundle rb1;
    public String defaultLanguage;
    public String frenchError;

    /**
     Initializes the Login Screen in accordance with user OS language settings.

     RUNTIME ERROR: I struggled to get the .getBundle() method to work, because
     I did not understand how to properly format a file path. `ResourceBundle/Login`
     wound up being correct.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        rb1 = ResourceBundle.getBundle("ResourceBundle/Login", Locale.getDefault());
        defaultLanguage = rb1.getLocale().getLanguage();
        String zoneId = ZoneId.systemDefault().getId();
        userLocation.setText(zoneId);

        if (defaultLanguage.equals("fr")) {
            loginButton.setText(rb1.getString("Login"));
            userLocationLabel.setText(rb1.getString("User Location"));
            userNameLabel.setText(rb1.getString("User Name"));
            passwordLabel.setText(rb1.getString("Password"));
            frenchError = rb1.getString("Wrong User Name or Password");

        } else {
            loginButton.setText("Login");
            userLocationLabel.setText("User Location");
            userNameLabel.setText("User Name");
            passwordLabel.setText("Password");
        }
    }

    /**
     Validates username/password combination.

     RUNTIME ERROR: In my first attempt to test out user input into the login form, I got an error that
     said that my userName and password TextFields were null, and unable to be edited. I solved this problem
     by making the TextField variables public, rather than private.
     There were also errors involved with logging in, because string comparisons were being performed improperly.
     I wound up replacing all the string comparisons that were done using '==' with comparisons using the '.equals()'
     method.
     */
    @FXML
    public void onLogin(ActionEvent actionEvent) throws SQLException {
        String inputUser = userName.getText();
        String inputPass = password.getText();
        String storedPass = UserQuery.getPasswordForUser(inputUser);

        if (inputPass.equals(storedPass)) {
            goToLanding(actionEvent);
            Users.notifyUpcomingAppointments(inputUser);
            reportLogin("login_activity.txt", inputUser, Timestamp.valueOf(LocalDateTime.now()), "SUCCESS");
        } else {
            if (defaultLanguage.equals("fr")) {
                showAlert(Alert.AlertType.ERROR,
                        rb1.getString("Login") + rb1.getString("Error"),
                        frenchError);
            } else {
                showAlert(Alert.AlertType.ERROR, "Login Error", "Wrong User Name or Password");
            }
            reportLogin("login_activity.txt", inputUser, Timestamp.valueOf(LocalDateTime.now()), "FAILURE");
        }
    }
}
