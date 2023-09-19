package controller;

import Database.Appointments;
import Database.Contacts;
import Interfaces.Navigable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

/**
 * Controller for viewing appointments by contact.
 * Fulfills the requirements for the second report in 3.f.
 */
public class ContactSchedules implements Navigable, Initializable {
    @FXML
    ComboBox<String> contactComboBox;
    public TableView appointmentTable;
    public TableColumn appointmentIdCol;
    public TableColumn titleCol;
    public TableColumn descriptionCol;
    public TableColumn typeCol;
    public TableColumn startTimeCol;
    public TableColumn endTimeCol;
    public TableColumn customerApptIdCol;

    /**
     Loads all of the contacts available in the contacts table into a combo box's list of items.
     RUNTIME ERROR: double loaded contacts; changed the load functionality to only load unique names.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Contacts.load();
        contactComboBox.setItems(Contacts.get());
    }
    /**
     Displays all of the appointments associated with a selected contact name.
     RUNTIME ERROR: Accidentally set cellValueFactory for contact and user_Id when these aren't necessary. This
     mistake was problematic because it referenced columns that were not present in the appointment table, so
     I simply removed them.
     */
    public void onFilterByContact(ActionEvent actionEvent) {
        String selectedContact = contactComboBox.getValue();

        try {
            appointmentTable.setEditable(true);
            appointmentIdCol.setCellValueFactory(new PropertyValueFactory<>("Id"));
            titleCol.setCellValueFactory(new PropertyValueFactory<>("Title"));
            descriptionCol.setCellValueFactory(new PropertyValueFactory<>("Description"));
            typeCol.setCellValueFactory(new PropertyValueFactory<>("Type"));
            startTimeCol.setCellValueFactory(new PropertyValueFactory<>("StartTime"));
            endTimeCol.setCellValueFactory(new PropertyValueFactory<>("EndTime"));
            customerApptIdCol.setCellValueFactory(new PropertyValueFactory<>("CustomerApptId"));
            appointmentTable.setItems(Appointments.appointmentsByContact(selectedContact));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void onCancel(ActionEvent actionEvent) { goToLanding(actionEvent); }

}
