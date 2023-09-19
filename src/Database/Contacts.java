package Database;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Contacts {
    private static final ObservableList<String> contacts = FXCollections.observableArrayList();

    /**
     Loads each unique contact name from the contacts table into an observable list.
     */
    public static void load() {
        try {
            PreparedStatement ps = JDBC.connection.prepareStatement("SELECT CONTACT_NAME FROM CONTACTS");
            ResultSet selectedContacts = ps.executeQuery();
            while (selectedContacts.next()) {
                String contact = selectedContacts.getString("CONTACT_NAME");
                if (!contacts.contains(contact)) {
                    contacts.add(contact);
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     Returns an observable list of available contacts.
     */
    public static ObservableList<String> get() { return contacts; }
}
