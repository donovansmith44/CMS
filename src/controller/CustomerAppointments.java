package controller;

import Database.Appointment;
import Database.Appointments;
import Database.Customers;
import Database.Types;
import Interfaces.Navigable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

import java.net.URL;
import java.sql.SQLException;
import java.time.Month;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * Controller for viewing the number of appointments for each customer,
 * and allowing their counts to be filtered by appointment month or type.
 * Fulfills the requirements for the first report in 3.f.
 */
public class CustomerAppointments implements Navigable, Initializable {
    @FXML
    public ComboBox<String> customerComboBox;
    public ComboBox<String> typeComboBox;
    public ComboBox<String> monthComboBox;
    public Label monthApptCountLabel;
    public Label typeApptCountLabel;
    public static ObservableList<Appointment> appointmentsByCustomer = FXCollections.observableArrayList();
    public static ObservableList<Appointment> monthlyAppointmentsByCustomer = FXCollections.observableArrayList();
    public static ObservableList<Appointment> typedAppointmentsByCustomer = FXCollections.observableArrayList();

    /**
     Loads all of the customers and types available in the customers and types tables into
     their respective combo box's list of items.

     RUNTIME ERROR: Customer names were duplicating in the combo box until I
     cleared the customer names each initialization (handled in the Customers/Types classes' load methods).
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Customers.load();
        Types.load();

        customerComboBox.setItems(Customers.getNames());
        typeComboBox.setItems(Types.get());
        monthComboBox.setItems(Appointment.getMonths());
    }

    /**
     Displays the number of appointments associated with each customer in the database by appointment type,
     and by appointment month.

     RUNTIME ERROR:
     */
    public void onFilterByCustomer(ActionEvent actionEvent) {
            String selectedCustomer = customerComboBox.getValue();

            try {
                appointmentsByCustomer = Appointments.appointmentsByCustomer(selectedCustomer);
                typeApptCountLabel.setText(String.valueOf(appointmentsByCustomer.size()));
                monthApptCountLabel.setText(String.valueOf(appointmentsByCustomer.size()));
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }


    }

    /**
     Displays the number of appointments a customer has scheduled on a selected month.

     RUNTIME ERROR: Had to convert the selectedMonth to uppercase since the month enum
     constant is uppercase.
     */
    public void onFilterByMonth(ActionEvent actionEvent) {
        if (!Objects.isNull(customerComboBox.getValue()) && !Objects.isNull(monthComboBox.getValue())) {
            String selectedMonth = monthComboBox.getValue();
            monthlyAppointmentsByCustomer = Appointments.filterByMonth(appointmentsByCustomer, selectedMonth);
            int numMonthlyAppointments = monthlyAppointmentsByCustomer.size();
            monthApptCountLabel.setText(String.valueOf(numMonthlyAppointments));
        }
    }

    /**
     Displays the number of appointments a customer has of a selected type.

     RUNTIME ERROR: Type combo box showed duplicate types. Refactored the Types.load()
     method to only load in distinct types.
     */
    public void onFilterByType(ActionEvent actionEvent) {
        if (!Objects.isNull(customerComboBox.getValue())) {
            String selectedType = typeComboBox.getValue();
            typedAppointmentsByCustomer = Appointments.filterByType(appointmentsByCustomer, selectedType);
            int numTypedAppointments = typedAppointmentsByCustomer.size();
            typeApptCountLabel.setText(String.valueOf(numTypedAppointments));
        }
    }

    public void onCancel(ActionEvent actionEvent) { goToLanding(actionEvent); }
}
