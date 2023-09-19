package Database;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Countries {
    private static final ObservableList<String> countries = FXCollections.observableArrayList();

    /**
     Loads each unique country from the countries table into an observable list.
     */
    public static void load() {
        try {
            countries.clear();
            PreparedStatement ps = JDBC.connection.prepareStatement("SELECT * FROM COUNTRIES");
            ResultSet selectedCountries = ps.executeQuery();
            while (selectedCountries.next()) {
                String country = selectedCountries.getString("COUNTRY");
                if (!countries.contains(country)) {
                    countries.add(country);
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     Returns an observable list of available countries.
     */
    public static ObservableList<String> get() { return countries; }
}
