package controller;

import Database.Appointment;
import Database.Appointments;
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
 * Controller for viewing appointments associated with a particular day of the week.
 * Fulfills the requirements for the third (customer) report in 3.f.
 */
public class DailySchedule implements Navigable, Initializable {
    @FXML
    public ComboBox<String> weekdayComboBox;
    public TableView appointmentTable;
    public TableColumn appointmentIdCol;
    public TableColumn titleCol;
    public TableColumn descriptionCol;
    public TableColumn typeCol;
    public TableColumn startTimeCol;
    public TableColumn endTimeCol;
    public TableColumn customerApptIdCol;

    /**
     Loads every weekday into a combo box.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        weekdayComboBox.setItems(Appointment.getWeekdays());
    }

    /**
     Displays every appointment which occurs on a selected day of the week in a TableView.
     */
    public void onFilterByWeekday(ActionEvent actionEvent) {
        String selectedWeekday = weekdayComboBox.getValue();
        try {
            appointmentTable.setEditable(true);
            appointmentIdCol.setCellValueFactory(new PropertyValueFactory<>("Id"));
            titleCol.setCellValueFactory(new PropertyValueFactory<>("Title"));
            descriptionCol.setCellValueFactory(new PropertyValueFactory<>("Description"));
            typeCol.setCellValueFactory(new PropertyValueFactory<>("Type"));
            startTimeCol.setCellValueFactory(new PropertyValueFactory<>("StartTime"));
            endTimeCol.setCellValueFactory(new PropertyValueFactory<>("EndTime"));
            customerApptIdCol.setCellValueFactory(new PropertyValueFactory<>("CustomerApptId"));
            appointmentTable.setItems(Appointments.appointmentsByWeekday(selectedWeekday));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void onCancel(ActionEvent actionEvent) {
        goToLanding(actionEvent);
    }
}
