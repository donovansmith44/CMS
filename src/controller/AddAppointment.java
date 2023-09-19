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
import java.sql.Timestamp;
import java.util.ResourceBundle;

/**
 * Controller for adding new appointments to the client_schedule.appointments database table
 * and displaying them on the landing page.
 */
public class AddAppointment implements Navigable, Initializable, Alerts, SQLQuery, Scheduler {
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
     Loads all of the available contacts in the database into the 'Contact' Combo Box.

     RUNTIME ERROR: The combo box was not showing anything, until I realized that I was setting its
     items to a value that was, in some cases, null. I created the Contacts class to deal with this issue,
     that would always return a non-null list of contacts, if there were available contacts in the database.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Contacts.load();
        contactComboBox.setItems(Contacts.get());
    }

    /**
     Saves a new appointment and updates the database to reflect the changes.

     RUNTIME ERROR: The appointments were failing to save because of there being
     duplicate Appointment_ids, which is a PK in the appointments table. I created
     the Appointments.generateUniqueIds() method to ensure that a unique Id
     would be associated with every new appointment.
     */
    public void onSaveNewAppointment(ActionEvent actionEvent) {
        int id = Appointments.generateUniqueAppointmentId();
        String title = titleTF.getText();
        String description = descriptionTF.getText();
        String location = locationTF.getText();
        String type = typeTF.getText();
        int customerId = Integer.parseInt(customerIdTF.getText());
        int userId = Integer.parseInt(userIdTF.getText());
        Timestamp startTime = Timestamp.valueOf(startTimeTF.getText());
        Timestamp endTime = Timestamp.valueOf(endTimeTF.getText());
        String selectedContactName = contactComboBox.getValue();

        Appointment newAppointment =
                new Appointment(
                        id,
                        title,
                        description,
                        location,
                        type,
                        startTime,
                        endTime,
                        customerId,
                        userId,
                        selectedContactName
                );

        if(successfullyScheduled(newAppointment, startTime, endTime, actionEvent)) {
            goToLanding(actionEvent);
        }
    }

    public void onCancel(ActionEvent actionEvent) {
        goToLanding(actionEvent);
    }
}
