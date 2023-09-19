package Database;

import Interfaces.Alerts;
import Interfaces.SQLQuery;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;

import java.sql.*;
import java.time.*;

public class Users implements SQLQuery, Alerts {
    private static final ObservableList<Integer> users = FXCollections.observableArrayList();

    /**
     Loads all of the available users from the database into an observable list of users.
     */
    public static void load() {
        try {
            PreparedStatement ps = JDBC.connection.prepareStatement("SELECT USER_ID FROM USERS");
            ResultSet selectedUsers = ps.executeQuery();
            while (selectedUsers.next()) {
                users.add(selectedUsers.getInt("USER_ID"));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     Returns an observable list of users.
     */
    public static ObservableList<Integer> get() { return users; }

    /**
     Converts a Timestamp's value from one time zone to another.
     */
    public static Timestamp convert(Timestamp t, ZoneId origin, ZoneId target) {
        LocalDateTime originDT = t.toLocalDateTime();
        ZonedDateTime originZonedDT = originDT.atZone(origin);
        ZonedDateTime targetZonedDT = originZonedDT.withZoneSameInstant(target);
        LocalDateTime targetDT = targetZonedDT.toLocalDateTime();
        return Timestamp.valueOf(targetDT);
    }

    /**
     Displays an alert if the logged-in user has an appointment scheduled within 15 minutes of login.

     RUNTIME ERROR: Appointment scheduled within 15 minutes wasn't triggering proper alert.
     getting the startTime from the DB was displaying in Local Zone, when I was expecting
     UTC. I learned that .getTimestamp() converts UTC to Local, so I called
     Timestamp.valueOf() on the String version of the timestamp instead.

     NOTE: If one logs in > 15 minutes before business hours (8AM-10PM EST), there will never be any alert
     of an upcoming appointment, because there can be no upcoming appointment sufficiently far outside
     those times.
     */
    public static void notifyUpcomingAppointments(String userName) {
        int userId = UserQuery.getUserId(userName);
        String sql = "SELECT * FROM APPOINTMENTS WHERE USER_ID = " + userId;
        Timestamp loginTime = Timestamp.valueOf(LocalDateTime.now());
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Appointment Info");
        String appointmentInfo = "You have no upcoming appointments.";

        try {
            PreparedStatement ps = JDBC.connection.prepareStatement(sql);
            ResultSet userAppts = ps.executeQuery();
            while (userAppts.next()) {
                Timestamp startTimeUTC = Timestamp.valueOf(userAppts.getString("START"));
                Timestamp startTimeEST = convert(startTimeUTC, ZoneId.of("UTC"), ZoneId.of("America/New_York"));
                Timestamp loginTimeEST = convert(loginTime, ZoneId.systemDefault(), ZoneId.of("America/New_York"));
                Timestamp startTimeLocal = convert(startTimeUTC, ZoneId.of("UTC"), ZoneId.systemDefault());
                long timeToAppt = Duration.between(loginTimeEST.toLocalDateTime(), startTimeEST.toLocalDateTime()).toMinutes();
                if (timeToAppt <= 15 && timeToAppt >= 0) {
                    String appointmentId = userAppts.getString("APPOINTMENT_ID");
                    appointmentInfo =
                            "You have an appointment! " +
                            "\n Appointment ID: " + appointmentId +
                            "\n Date: " + Date.valueOf(startTimeUTC.toLocalDateTime().toLocalDate()) +
                            "\n Time " + startTimeLocal.toLocalDateTime().toLocalTime();
                    alert.setContentText(appointmentInfo);
                    alert.showAndWait();
                    return;
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        alert.setContentText(appointmentInfo);
        alert.showAndWait();
    }
}
