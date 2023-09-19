package Database;

import Interfaces.Alerts;
import Interfaces.SQLQuery;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

/**
 Maintains an observable list of appointments, and enables robust functionality thereupon.
 */
public class Customers implements SQLQuery, Alerts {
    private static final ObservableList<Customer> customers = FXCollections.observableArrayList();
    private static final ObservableList<String> customerNames = FXCollections.observableArrayList();

    /**
     Adds a selected customer into the observable list of customers.
     RUNTIME ERROR: .contains() wasn't behaving as expected.
     wrote a simple loop to determine if the customer being added was already present in the list instead.
     */
    public static void add(Customer customer) {
        boolean newCustomer = true;

        for (Customer c : customers) {
            if (c.getId() == customer.getId()) {
                newCustomer = false;
                break;
            }
        }

        if (newCustomer) {
            customers.add(customer);
        }
    }

    /**
     Removes a selected customer from the observable list of customers.
     */
    public static void remove(Customer customer) {
        String sql = "DELETE FROM CUSTOMERS WHERE CUSTOMER_ID = " + customer.getId();
        try {
            PreparedStatement ps = JDBC.connection.prepareStatement(sql);
            ps.executeUpdate();
            customers.remove(customer);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     Updates a selected customer in the observable list of customers, and accordingly updates the database.
     */
    public static void update(Customer customer, String name, String address, String postalCode, String phoneNumber, String division) {
        int divisionId = divisionToDivisionId(division);
        String sql =
                "UPDATE CUSTOMERS " +
                        "SET CUSTOMER_NAME = " + "'" + name + "', " +
                        "ADDRESS = " + "'" +           address + "', " +
                        "POSTAL_CODE = " + "'" +       postalCode + "', " +
                        "PHONE = "  + "'" +            phoneNumber + "', " +
                        "DIVISION_ID = " + divisionId + " " +
                        "WHERE CUSTOMER_ID = " +       customer.getId() + ";";
        try {
            PreparedStatement ps = JDBC.connection.prepareStatement(sql);
            ps.executeUpdate();
            customer.setName(name);
            customer.setAddress(address);
            customer.setPostalCode(postalCode);
            customer.setPhoneNumber(phoneNumber);
            customer.setDivisionID(divisionId);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     Returns the division ID associated with a selected division.
     */
    public static int divisionToDivisionId(String division) {
        String sql = "SELECT DIVISION_ID FROM FIRST_LEVEL_DIVISIONS WHERE DIVISION = '" + division + "'";

        try {
            PreparedStatement ps = JDBC.connection.prepareStatement(sql);
            ResultSet selectedDivisionID = ps.executeQuery();
            if (selectedDivisionID.next()) {
                return selectedDivisionID.getInt("DIVISION_ID");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return 0;
    }

    /**
     Returns the list of available customers.
     */
    public static ObservableList<Customer> get() { return customers; }

    /**
     Returns the list of the names of available customers.

     RUNTIME ERROR: Every time this method was called, the location to which the list
     was displayed would duplicate the list of customer names, so I fixed this issue by
     returning an observable list with only distinct names. One problem with this solution
     is that it presumes that customers cannot have the same name.
     */
    public static ObservableList<String> getNames () {
        for (Customer c: customers) {
            customerNames.add(c.getName());
        }
        return customerNames
                .stream()
                .distinct()
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
    }

    /**
     Loads all of the customers in the database into an observable list.
     */
    public static void load() {
        try {
            PreparedStatement ps = JDBC.connection.prepareStatement("SELECT * FROM CUSTOMERS");
            ResultSet customersInDB = ps.executeQuery();
            while (customersInDB.next()) {
                Customers.add(new Customer(
                        Integer.parseInt(customersInDB.getString("CUSTOMER_ID")),
                        customersInDB.getString("CUSTOMER_NAME"),
                        customersInDB.getString("ADDRESS"),
                        customersInDB.getString("POSTAL_CODE"),
                        customersInDB.getString("PHONE"),
                        Integer.parseInt(customersInDB.getString("DIVISION_ID"))));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     Returns a unique customer id to prevent duplication of primary key values.
     */
    public static int generateUniqueCustomerId() {
        int highestId = 0;

        for (Customer c: customers) {
            highestId = Integer.max(highestId, c.getId());
        }

        return highestId + 1;
    }

    /**
     Confirms that a customer exists, and if they do, returns true and allows customer
     deletion if and only if they have no scheduled appointments.

     RUNTIME ERROR: The alerts were not triggering at the expected times. I fixed this
     by passing an ActionEvent as a parameter.

     LAMBDA JUSTIFICATION: The displayAlert lambda was developed to eliminate the
     unnecessary boilerplate code that set the content text of an alert, showed the
     alert, and waited for a response by the user. This wound up improving my code
     by decreasing the amount of repetition therein, and clarifying what is being done
     in a declarative fashion.
     */
    public static boolean canDelete(Customer customer, ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");

        BiFunction<Alert, String, Optional> displayAlert = (a, s) -> {
            a.setContentText(s);
            return a.showAndWait();};

        if (Objects.isNull(customer)) {
            displayAlert.apply(alert, "You must first select a customer.");
        } else if (Appointments.customerHasAppointments(customer.getId())) {
            displayAlert.apply(alert, "This customer has appointments, and cannot be deleted.");
        } else {
            alert.setAlertType(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation");
            Optional<ButtonType> result =
                    displayAlert.apply(alert, "Are you sure you want to delete this customer?");

            if (result.get() == ButtonType.OK) {
                return true;
            }
        }
        return false;
    }
}
