package controller;

import Database.*;
import Interfaces.Navigable;
import Interfaces.SQLQuery;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Controller for updating pre-existing customers from the client_schedule.customers database table
 * and displaying them on the landing page.
 */
public class UpdateCustomer implements Initializable, Navigable, SQLQuery{
    public static Customer customer;
    public TextField idTF;
    public TextField nameTF;
    public TextField addressTF;
    public TextField postalCodeTF;
    public TextField phoneTF;
    public ComboBox<String> countryComboBox;
    public ComboBox<String> divisionComboBox;

    /**
     Loads all of an customer's attributes into text fields to be updated.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        int customerCountryId = customer.getCountryId();
        idTF.setText(String.valueOf(customer.getId()));
        nameTF.setText(customer.getName());
        addressTF.setText(customer.getAddress());
        postalCodeTF.setText(customer.getPostalCode());
        phoneTF.setText(customer.getPhoneNumber());
        countryComboBox.setEditable(true);
        divisionComboBox.setEditable(true);

        countryComboBox.setValue(customer.getCountry());
        divisionComboBox.setValue(customer.getDivision(customer.getDivisionID()));

        Countries.load();
        FirstLevelDivisions.load(customerCountryId);

        countryComboBox.setItems(Countries.get());
        divisionComboBox.setItems(FirstLevelDivisions.get());
    }

    /**
     Accepts a customer object loaded from the Landing Page for an update.

     RUNTIME ERROR: I encountered no runtime errors in the development of this method.
     */
    public static void loadCustomer(Customer c) { customer = c; }

    /**
     Saves a previously existing customer with new attributes, and accordingly updates the database.

     RUNTIME ERROR: I encountered no runtime errors in the development of this method.
     */
    public void onSaveUpdatedCustomer(ActionEvent actionEvent) {
        String name = nameTF.getText();
        String address = addressTF.getText();
        String postalCode = postalCodeTF.getText();
        String phone = phoneTF.getText();
        String division = divisionComboBox.getSelectionModel().getSelectedItem();

        Customers.update(customer, name, address, postalCode, phone, division);
        goToLanding(actionEvent);
    }

    /**
     Filters the First Level Divisions selection options by the country that's selected.

     RUNTIME ERROR: First level divisions were not lining up with country selections
     until I cleared the divisions list whenever a country was selected.
     */
    public void onCountrySelected(ActionEvent actionEvent) {
        String selectedCountry = countryComboBox.getValue();
        int countryId;

        divisionComboBox.setEditable(true);
        if (!Objects.isNull(selectedCountry)) {
            if (selectedCountry.equals("U.S")) {
                countryId = 1;
            } else if (selectedCountry.equals("UK")) {
                countryId = 2;
            } else if (selectedCountry.equals("Canada")) {
                countryId = 3;
            } else {
                countryId = 0;
            }
            FirstLevelDivisions.load(countryId);
        }
        divisionComboBox.setItems(FirstLevelDivisions.get());
    }

    public void onCancel(ActionEvent actionEvent) {
        goToLanding(actionEvent);
    }
}
