package controller;

import Database.Appointment;
import Database.Appointments;
import Database.Customer;
import Database.Customers;
import Interfaces.Alerts;
import Interfaces.Navigable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for viewing customers and appointments tables from the client_schedule database
 * in JavaFX tableviews, and enables navigation to all other controllers.
 */
public class LandingPage implements Initializable, Navigable, Alerts {
    @FXML
    public TableView customerTable;
    public TableColumn customerIdCol;
    public TableColumn customerNameCol;
    public TableColumn customerAddressCol;
    public TableColumn customerZipCol;
    public TableColumn customerPhoneCol;
    public TableColumn customerDivisionCol;

    public TableView appointmentTable;
    public TableColumn appointmentIdCol;
    public TableColumn titleCol;
    public TableColumn descriptionCol;
    public TableColumn locationCol;
    public TableColumn contactCol;
    public TableColumn typeCol;
    public TableColumn startTimeCol;
    public TableColumn endTimeCol;
    public TableColumn customerApptIdCol;
    public TableColumn userIdCol;

    /**
     Loads all of the customers and appointments available in the `customers` and
     `appointments` database tables into their respective table views.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Customers.load();
        Appointments.load();

        customerTable.setEditable(true);
        customerIdCol.setCellValueFactory(new PropertyValueFactory<>("Id"));
        customerNameCol.setCellValueFactory(new PropertyValueFactory<>("Name"));
        customerAddressCol.setCellValueFactory(new PropertyValueFactory<>("Address"));
        customerPhoneCol.setCellValueFactory(new PropertyValueFactory<>("PhoneNumber"));
        customerZipCol.setCellValueFactory(new PropertyValueFactory<>("PostalCode"));
        customerDivisionCol.setCellValueFactory(new PropertyValueFactory<>("DivisionID"));

        appointmentTable.setEditable(true);
        appointmentIdCol.setCellValueFactory(new PropertyValueFactory<>("Id"));
        titleCol.setCellValueFactory(new PropertyValueFactory<>("Title"));
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("Description"));
        locationCol.setCellValueFactory(new PropertyValueFactory<>("Location"));
        contactCol.setCellValueFactory(new PropertyValueFactory<>("Contact"));
        typeCol.setCellValueFactory(new PropertyValueFactory<>("Type"));
        startTimeCol.setCellValueFactory(new PropertyValueFactory<>("StartTime"));
        endTimeCol.setCellValueFactory(new PropertyValueFactory<>("EndTime"));
        customerApptIdCol.setCellValueFactory(new PropertyValueFactory<>("CustomerApptId"));
        userIdCol.setCellValueFactory(new PropertyValueFactory<>("UserId"));

        customerTable.setItems(Customers.get());
        appointmentTable.setItems(Appointments.get());
    }

    /**
     Navigates to the `Add Customer` page.
     RUNTIME ERROR: I encountered no runtime errors in the development of this function.
     */
    @FXML
    public void onAddCustomer(ActionEvent actionEvent) {
        navigateTo(actionEvent, "/view/AddCustomer.fxml");
    }

    /**
     Passes the selected customer to the `UpdateCustomer` controller, and navigates to the
     `UpdateCustomer` page.
     RUNTIME ERROR:
     */
    public void onUpdateCustomer(ActionEvent actionEvent) {
        Customer selectedCustomer = (Customer) customerTable.getSelectionModel().getSelectedItem();
        if (selectedCustomer == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "You must first select a customer.");
        } else {
            UpdateCustomer.loadCustomer(selectedCustomer);
            navigateTo(actionEvent, "/view/UpdateCustomer.fxml");
        }
    }

    /**
     Removes the selected customer from the CustomerList, the TableView dependent thereupon, and the
     associated database table.
     RUNTIME ERROR:
     */
    public void onDeleteCustomer(ActionEvent actionEvent) {
        Customer selectedCustomer = (Customer) customerTable.getSelectionModel().getSelectedItem();
        if (Customers.canDelete(selectedCustomer, actionEvent)) {
            Customers.remove(selectedCustomer);
            goToLanding(actionEvent);
        }
    }

    /**
     Navigates to the `Add Appointment` page.
     RUNTIME ERROR: I encountered no runtime errors in the development of this function.
     */
    public void onAddAppointment(ActionEvent actionEvent) {
        navigateTo(actionEvent, "/view/AddAppointment.fxml");
    }

    /**
     Passes the selected appointment to the `UpdateAppointment` controller, and navigates to the
     `UpdateAppointment` page.
     RUNTIME ERROR:
     */
    public void onUpdateAppointment(ActionEvent actionEvent) {
        Appointment selectedAppointment = (Appointment) appointmentTable.getSelectionModel().getSelectedItem();
        if (selectedAppointment == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "You must first select an appointment.");
        } else {
            UpdateAppointment.loadAppointment(selectedAppointment);
            navigateTo(actionEvent, "/view/UpdateAppointment.fxml");
        }
    }

    /**
     Removes the selected appointment from the `Appointments` ObservableList, the TableView dependent thereupon,
     and from the associated database table.
     RUNTIME ERROR:
     */
    public void onDeleteAppointment(ActionEvent actionEvent) {
        Appointment selectedAppointment = (Appointment) appointmentTable.getSelectionModel().getSelectedItem();
        if (selectedAppointment == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "You must first select an appointment.");
        } else {
            Appointments.remove(selectedAppointment);
            String infoMessage =
                    "The appointment with ID: " + selectedAppointment.getId() +
                            " and Type: " + selectedAppointment.getType() +
                            " has been deleted.";
            showAlert(Alert.AlertType.INFORMATION, "Appointment Deleted",infoMessage);
            goToLanding(actionEvent);
        }
    }

    /**
     Displays all available appointments inthe `Appointments` table.
     RUNTIME ERROR: I encountered no runtime errors in the development of this function.
     */
    public void onNoFilter(ActionEvent actionEvent) {
        appointmentTable.setItems(Appointments.get());
    }

    /**
     Filters the appointments displayed in the `Appointments` table by the current month.
     RUNTIME ERROR: I encountered no runtime errors in the development of this function.
     */
    public void onFilterByMonth(ActionEvent actionEvent) {
        appointmentTable.setItems(Appointments.thisMonth());
    }

    /**
     Filters the appointments displayed in the `Appointments` table by the current week.
     RUNTIME ERROR: At first, selecting the 'This Week' radio button had no effect. I'd
     forgotten to set the `Appointments` table items to the new observable list, and fixed the problem
     by doing just that.
     */
    public void onFilterByWeek(ActionEvent actionEvent) {
        appointmentTable.setItems(Appointments.thisWeek());
    }

    /**
     Navigates from the Landing Page to the Daily Schedule Report.
     RUNTIME ERROR: I encountered no runtime errors in the development of this function.
     */
    public void viewDailySchedule(ActionEvent actionEvent) { navigateTo(actionEvent, "/view/DailySchedule.fxml"); }

    /**
     Navigates from the Landing Page to the Customer Appointments Report.
     RUNTIME ERROR: I encountered no runtime errors in the development of this function.
     */
    public void viewCustomerAppointments(ActionEvent actionEvent) { navigateTo(actionEvent, "/view/CustomerAppointments.fxml"); }

    /**
     Navigates from the Landing Page to the Contact Schedules Report.
     RUNTIME ERROR: I encountered no runtime errors in the development of this function.
     */
    public void viewContactSchedules(ActionEvent actionEvent) { navigateTo(actionEvent, "/view/ContactSchedules.fxml"); }

    /**
     Navigates from the Landing Page to the Login Screen.
     RUNTIME ERROR: I encountered no runtime errors in the development of this function.
     */
    public void onLogout(ActionEvent actionEvent) {
        navigateTo(actionEvent, "/view/LoginScreen.fxml");
    }
}
