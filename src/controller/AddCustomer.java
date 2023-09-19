package controller;

import Database.Countries;
import Database.Customer;
import Database.Customers;
import Database.FirstLevelDivisions;
import Interfaces.*;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for adding new customers to the client_schedule.customers database table
 * and displaying them on the landing page.
 */
public class AddCustomer implements Navigable, Initializable, SQLQuery {
    public TextField idTF;
    public TextField nameTF;
    public TextField addressTF;
    public TextField postalCodeTF;
    public TextField phoneTF;
    public Label firstLevelLabel;
    public ComboBox<String> countryComboBox;
    public ComboBox<String> divisionComboBox;

    /**
     Loads all available countries for selection into a combo box for user selection.

     RUNTIME ERROR: Every time I went to the 'Add Customer' page, there would be a duplication
     of the countries available to choose from in the combo box dropdown. I fixed this by
     changing the logic that added countries to the observable list to only add unique values.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Countries.load();
        countryComboBox.setEditable(true);
        countryComboBox.setItems(Countries.get());
    }

    /**
     Saves a new customer, and updates the database to reflect the changes.

     RUNTIME ERROR: SELECT statement returned error. Wound up making change to include "'" before and after
     all string values added thereto.
     */
    public void onSaveNewCustomer(ActionEvent actionEvent) {
        int id = Customers.generateUniqueCustomerId();
        String name = nameTF.getText();
        String address = addressTF.getText();
        String postalCode = postalCodeTF.getText();
        String phone = phoneTF.getText();
        String selectedDivision = divisionComboBox.getSelectionModel().getSelectedItem();
        int divisionId = getDivisionId(selectedDivision);

        Customer newCustomer =
                new Customer(
                        id,
                        name,
                        address,
                        postalCode,
                        phone,
                        divisionId);

        if (successfullyAdded(newCustomer)) {
            goToLanding(actionEvent);
        }
    }

    /**
     Filters the First Level Divisions selection options by the country that's selected.

     RUNTIME ERROR: First level divisions were not lining up with country selections
     until I cleared the divisions list whenever a country was selected.
     */
    public void onCountrySelected(ActionEvent actionEvent) {
        String selectedCountry = countryComboBox.getSelectionModel().getSelectedItem();
        int countryId = 0;

        divisionComboBox.setEditable(true);

        switch (selectedCountry) {
            case "U.S" :
                countryId = 1;
                firstLevelLabel.setText("State");
                break;
            case "UK" :
                countryId = 2;
                firstLevelLabel.setText("Constituent Country");
                break;
            case "Canada" :
                countryId = 3;
                firstLevelLabel.setText("Province");
                break;
        }

        FirstLevelDivisions.load(countryId);
        divisionComboBox.setItems(FirstLevelDivisions.get());
    }

    public void onCancel(ActionEvent actionEvent) {
        goToLanding(actionEvent);
    }
}
