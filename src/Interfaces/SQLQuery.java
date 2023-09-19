package Interfaces;

import Database.Customer;
import Database.Customers;
import Database.JDBC;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 Provides several SQL querying function templates to implementing classes to
 simplify access of the client_schedule database.
 */
public interface SQLQuery {
    /**
     Takes a SQL SELECT command as a string and returns a result set of the selected items.
     */
    default ResultSet doSelect(String sql) throws SQLException {
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        return rs;
    }

    /**
     Returns true if a customer's data is successfully inserted into the database.
     */
    default boolean successfullyAdded(Customer newCustomer) {
        String sql = "INSERT INTO CUSTOMERS " + newCustomer.getAttributesAsColumns() +
                " VALUES " + newCustomer.getAttributesAsValues();
            try {
                PreparedStatement ps = JDBC.connection.prepareStatement(sql);
                ps.executeUpdate();
                Customers.add(newCustomer);
                return true;
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            return false;
    }

    /**
     Returns the division ID associated with a particular division, and 0 if the division does not exist in
     the database.
     */
    default int getDivisionId(String division) {
        try {
            ResultSet selectedDivisionId = doSelect("SELECT DIVISION_ID FROM FIRST_LEVEL_DIVISIONS WHERE DIVISION = '" + division + "'");
            if (selectedDivisionId.next()) {
                return selectedDivisionId.getInt("DIVISION_ID");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return 0;
    }
}
