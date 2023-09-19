package Database;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Types {
    private static final ObservableList<String> types = FXCollections.observableArrayList();

    /**
     Loads each unique appointment type from the appointments table into an observable list.
     */
    public static void load() {
        types.clear();

        for (Appointment a:
             Appointments.get()) {
            if (!types.contains(a.getType())) {
                types.add(a.getType());
            }
        }
    }

    /**
     Returns a list of the appointment types from the appointments table.
     */
    public static ObservableList<String> get() { return types; }
}
