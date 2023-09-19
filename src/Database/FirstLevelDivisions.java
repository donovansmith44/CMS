package Database;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FirstLevelDivisions {
    private static final ObservableList<String> divisions = FXCollections.observableArrayList();
    /**
     Loads all the first level divisions for a given country into an observable list.
     */
    public static void load(int countryId) {
        divisions.clear();
        try {
            PreparedStatement ps = JDBC.connection.prepareStatement("SELECT DIVISION FROM FIRST_LEVEL_DIVISIONS WHERE COUNTRY_ID = " + countryId);
            ResultSet divisionsForCountry = ps.executeQuery();
            while (divisionsForCountry.next()) {
                divisions.add(divisionsForCountry.getString("DIVISION"));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     Returns the first level divisions for pre-selected country.
     */
    public static ObservableList<String> get() { return divisions; }
}
