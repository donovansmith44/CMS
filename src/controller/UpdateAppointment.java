package controller;

import Database.Appointment;
import Database.Appointments;
import Database.Contacts;
import Interfaces.*;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

/**
 * Controller for updating pre-existing appointments from the client_schedule.appointments database table
 * and displaying them on the landing page.
 */
public class UpdateAppointment implements Navigable, Initializable, SQLQuery, Scheduler, Alerts {
    public static Appointment appointment;
    public TextField idTF;
    public TextField titleTF;
    public TextField descriptionTF;
    public TextField locationTF;
    public TextField typeTF;
    public TextField startTimeTF;
    public TextField endTimeTF;
    public TextField customerIdTF;
    public TextField userIdTF;
    public ComboBox<String> contactComboBox;

    /**
     Loads all of an appointment's attributes into text fields to be updated.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        idTF.setText(String.valueOf(appointment.getId()));
        titleTF.setText(appointment.getTitle());
        descriptionTF.setText(appointment.getDescription());
        locationTF.setText(appointment.getLocation());
        typeTF.setText(appointment.getType());
        startTimeTF.setText(appointment.getStartTime().toString());
        endTimeTF.setText(appointment.getEndTime().toString());
        customerIdTF.setText(String.valueOf(appointment.getCustomerApptId()));
        userIdTF.setText(String.valueOf(appointment.getUserId()));
        contactComboBox.setValue(appointment.getContact());

        Contacts.load();
        contactComboBox.setItems(Contacts.get());
    }

    /**
     Accepts an appointment object loaded from the Landing Page for an update.

     RUNTIME ERROR: I encountered no runtime errors in the development of this method.
     */
    public static void loadAppointment(Appointment a) { appointment = a; }

    /**
     Saves updated appointments with attributes written into their respective text fields by the user.

     RUNTIME ERROR: Setting the contactID from the database failed, due to not realizing that ResultSet
     objects must have the method '.next()' called upon themselves to be able to access selected row data.
     This was resolved by setting a condition that would only set the contactId variable if there was a
     contact in the ResultSet; which was a determination made by making the condition be the return value
     of the .next() function.
     */
    public void onSaveUpdatedAppointment(ActionEvent actionEvent) {
        String title = titleTF.getText();
        String description = descriptionTF.getText();
        String location = descriptionTF.getText();
        String contact = contactComboBox.getValue();
        String type = typeTF.getText();
        Timestamp startTime = Timestamp.valueOf(startTimeTF.getText());
        Timestamp endTime = Timestamp.valueOf(endTimeTF.getText());
        int customerId = Integer.parseInt(customerIdTF.getText());
        int userId = Integer.parseInt(userIdTF.getText());
        if (canSchedule(startTime, endTime, customerId, actionEvent, appointment.getId())) {
            Appointments.update(appointment, title, description, location, contact, type, startTime, endTime, customerId, userId);
            updateAppointment(appointment, actionEvent);
            goToLanding(actionEvent);
        }
    }

    public void onCancel(ActionEvent actionEvent) {
        goToLanding(actionEvent);
    }
}
