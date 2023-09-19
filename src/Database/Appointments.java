package Database;

import Interfaces.SQLQuery;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 Maintains an observable list of appointments, and enables robust functionality thereupon.
 */
public class Appointments implements SQLQuery {
    private static final ObservableList<Appointment> appointments = FXCollections.observableArrayList();

    /**
     Adds an appointment to the current list of appointments.

     RUNTIME ERROR: .contains() was not returning true when passed a duplicate appointment,
     so I implemented a simple for loop to verify whether an appointment was present before adding it.
     */
    public static void add(Appointment appointment) {
        boolean newAppointment = true;

        for (Appointment a: appointments) {
            if (a.getId() == appointment.getId()) {
                newAppointment = false;
                break;
            }
        }

        if (newAppointment) {
            appointments.add(appointment);
        }
    }

    /**
     Removes an appointment from the list of appointments, and updates the database accordingly.
     */
    public static void remove(Appointment appointment) {
        String sql = "DELETE FROM APPOINTMENTS WHERE APPOINTMENT_ID = " + appointment.getId();
        try {
            PreparedStatement ps = JDBC.connection.prepareStatement(sql);
            ps.executeUpdate();
            appointments.remove(appointment);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     Updates an appointment in the list of appointments, and updates the database accordingly.
     */
    public static void update(Appointment appointmentToUpdate, String title, String description, String location, String contact, String type, Timestamp startTime, Timestamp endTime, int customerApptId, int userId) {
        appointmentToUpdate.setTitle(title);
        appointmentToUpdate.setDescription(description);
        appointmentToUpdate.setLocation(location);
        appointmentToUpdate.setContact(contact);
        appointmentToUpdate.setType(type);
        appointmentToUpdate.setStartTime(startTime);
        appointmentToUpdate.setEndTime(endTime);
        appointmentToUpdate.setCustomerApptId(customerApptId);
        appointmentToUpdate.setUserId(userId);
    }

    /**
     Returns the list of appointments in the database.
     */
    public static ObservableList<Appointment> get() { return appointments; }

    /**
     Determines if there are appointments associated with a particular customer ID.
     */
    public static boolean customerHasAppointments(int customerId) {
        for (Appointment a: appointments) {
            if (customerId == a.getCustomerApptId()) {
                return true;
            }
        }
        return false;
    }

    /**
     Loads all of the appointments in the database into an observable list.
     */
    public static void load() {
        try {
            PreparedStatement ps = JDBC.connection.prepareStatement("SELECT * FROM APPOINTMENTS");
            ResultSet appointmentsInDB = ps.executeQuery();
            while (appointmentsInDB.next()) {
                Appointments.add(new Appointment(
                        Integer.parseInt(appointmentsInDB.getString("APPOINTMENT_ID")),
                        appointmentsInDB.getString("TITLE"),
                        appointmentsInDB.getString("DESCRIPTION"),
                        appointmentsInDB.getString("LOCATION"),
                        appointmentsInDB.getString("TYPE"),
                        appointmentsInDB.getTimestamp("START"),
                        appointmentsInDB.getTimestamp("END"),
                        Integer.parseInt(appointmentsInDB.getString("CUSTOMER_ID")),
                        Integer.parseInt(appointmentsInDB.getString("USER_ID")),
                        getContactName(appointmentsInDB.getInt("CONTACT_ID"))));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     Queries the database to get the name associated with a contact ID.
     */
    static String getContactName(int contactId) {
        try {
            PreparedStatement ps = JDBC.connection.prepareStatement("SELECT CONTACT_NAME FROM CONTACTS WHERE CONTACT_ID = " + contactId);
            ResultSet selectedContactName = ps.executeQuery();
            if (selectedContactName.next()) {
                return selectedContactName.getString("CONTACT_NAME");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return "";
    }

    /**
     Returns an observable list of appointments associated with a chosen contact's name.

     RUNTIME ERROR: Accidentally selected from contacts table by CUSTOMER_ID rather than CONTACT_ID
     */
    public static ObservableList<Appointment> appointmentsByContact(String contactName) throws SQLException {
        String sql = "SELECT CONTACT_ID FROM CONTACTS WHERE CONTACT_NAME = '" + contactName + "'";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ResultSet selectedContactId = ps.executeQuery();
        if (selectedContactId.next()) {
            final int contactId = selectedContactId.getInt("CONTACT_ID");

            ObservableList<Appointment> appointmentsForContact =
                    Appointments.get()
                            .stream()
                            .filter(appointment -> appointment.getContactId() == contactId)
                            .collect(Collectors.toCollection(FXCollections::observableArrayList));
            return appointmentsForContact;
        }
        return null;
    }

    /**
     Returns an observable list of appointments associated with a chosen day of the week.
     */
    public static ObservableList<Appointment> appointmentsByWeekday(String weekday) throws SQLException {
      ObservableList<Appointment> appointmentsForWeekday =
                    Appointments.get()
                            .stream()
                            .filter(appointment -> appointment.getStartTime().toLocalDateTime().getDayOfWeek() == DayOfWeek.valueOf(weekday.toUpperCase()))
                            .collect(Collectors.toCollection(FXCollections::observableArrayList));
            return appointmentsForWeekday;
    }

    /**
     Returns an observable list of appointments associated with a chosen customer's name.
     */
    public static ObservableList<Appointment> appointmentsByCustomer(String customerName) throws SQLException {
        String sql = "SELECT CUSTOMER_ID FROM CUSTOMERS WHERE CUSTOMER_NAME = '" + customerName + "'";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ResultSet selectedCustomerId = ps.executeQuery();
        if (selectedCustomerId.next()) {
            final int customerId = selectedCustomerId.getInt("CUSTOMER_ID");

            ObservableList<Appointment> appointmentsForCustomer =
                    Appointments.get()
                            .stream()
                            .filter(appointment -> appointment.getCustomerApptId() == customerId)
                            .collect(Collectors.toCollection(FXCollections::observableArrayList));
            return appointmentsForCustomer;
        }

        return null;
    }

    /**
     Returns an observable list of appointments scheduled this month.

     LAMBDA JUSTIFICATION: The scheduledThisMonth lambda was developed solely
     for the sake of increasing clarity and readability in the generation of
     the appointments for the month. The operation is simple, but having several
     lines of code passed as an argument for a stream binder is cumbersome.
     */
    public static ObservableList<Appointment> thisMonth() {
        Function<Appointment, Boolean> scheduledThisMonth = appointment -> {
            Month appointmentMonth = appointment.getStartTime().toLocalDateTime().getMonth();
            Month currentMonth = LocalDateTime.now().getMonth();
            return appointmentMonth.equals(currentMonth);
        };

        ObservableList<Appointment> appointmentsThisMonth =
                Appointments.get()
                        .stream()
                        .filter(appointment -> scheduledThisMonth.apply(appointment))
                        .collect(Collectors.toCollection(FXCollections::observableArrayList));

        return appointmentsThisMonth;
    }

    /**
     Returns an observable list of appointments scheduled for this week.

     LAMBDA JUSTIFICATION: The scheduledThisWeek lambda was developed solely
     for the sake of increasing clarity and readability in the generation of
     the appointments for the week. The operation is simple, but having several
     lines of code passed as an argument for a stream binder is cumbersome.
     */
    public static ObservableList<Appointment> thisWeek() {
        Function<Appointment, Boolean> scheduledThisWeek = appointment -> {
            LocalDateTime appointmentDT = appointment.getStartTime().toLocalDateTime();
            LocalDateTime now = LocalDateTime.now();
            long daysUntilAppointment = Duration.between(now, appointmentDT).toDays();

            //if appointmentDT is more than 7 days beyond today, or appointmentDT passed, appointment not upcoming this week.
            if (daysUntilAppointment >= 7 || daysUntilAppointment < 0) {
                return false;
            } else {
                return appointmentDT.getDayOfWeek().compareTo(now.getDayOfWeek()) >= 0;
            }
        };

        ObservableList<Appointment> appointmentsThisWeek =
                Appointments.get()
                        .stream()
                        .filter(appointment -> scheduledThisWeek.apply(appointment))
                        .collect(Collectors.toCollection(FXCollections::observableArrayList));

        return appointmentsThisWeek;
    }

    /**
     Returns a unique appointment id to prevent duplication of primary key values.
     */
    public static int generateUniqueAppointmentId() {
        int highestId = 0;

        for (Appointment a: appointments) {
            highestId = Integer.max(highestId, a.getId());
        }

        return highestId + 1;
    }

    /**
     Returns an observable list of appointments scheduled on a selected month (neglecting the year).
     */
    public static ObservableList<Appointment> filterByMonth(ObservableList<Appointment> appointments, String month) {
        int selectedMonth = Month.valueOf(month.toUpperCase()).getValue();
        return appointments.stream()
                .filter(appointment -> {
                        int appointmentMonth = appointment.getStartTime().toLocalDateTime().getMonth().getValue();
                        return appointmentMonth == selectedMonth ? true: false; })
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
    }

    /**
     Returns an observable list of appointments associated with a selected type.
     */
    public static ObservableList<Appointment> filterByType(ObservableList<Appointment> appointments, String selectedType) {
        return appointments.stream()
                .filter(appointment -> appointment.getType() == selectedType)
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
    }
}
