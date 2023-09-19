package Database;

import Interfaces.SQLQuery;
import Interfaces.Scheduler;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 Enables the creation and manipulation of appointment objects, and their data members.
 */
public class Appointment implements SQLQuery, Scheduler {
    private final int id;
    private String title;
    private String description;
    private String location;
    private String contact;
    private int contactId;
    private String type;
    private Timestamp startTime;
    private Timestamp endTime;
    private Timestamp UTCStartTime;
    private Timestamp UTCEndTime;
    private int customerApptId;
    private int userId;
    private final String insertColumns =
            "(APPOINTMENT_ID, TITLE, DESCRIPTION, LOCATION, CONTACT_ID, TYPE, START, END, CUSTOMER_ID, USER_ID)";

    private static final ObservableList<String> months =
            FXCollections.observableArrayList(
                    "January",
                    "February",
                    "March",
                    "April",
                    "May",
                    "June",
                    "July",
                    "August",
                    "September",
                    "October",
                    "November",
                    "December");

    private static final ObservableList<String> weekdays =
            FXCollections.observableArrayList(
                    "Monday",
                    "Tuesday",
                    "Wednesday",
                    "Thursday",
                    "Friday",
                    "Saturday",
                    "Sunday");

    public Appointment(int id, String title, String description, String location, String type, Timestamp startTime, Timestamp endTime, int customerApptId, int userId, String contact) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.location = location;
        this.contact = contact;
        try {
            ResultSet selectedContact = doSelect("SELECT CONTACT_ID FROM CONTACTS WHERE CONTACT_NAME = '" + this.contact + "'");
            if (selectedContact.next()) {
                this.contactId = selectedContact.getInt("CONTACT_ID");
            }
        } catch (SQLException s){
            s.printStackTrace();
        }
        this.type = type;
        this.startTime = startTime;
        this.UTCStartTime = convert(startTime, ZoneId.systemDefault(), ZoneId.of("UTC"));
        this.endTime = endTime;
        this.UTCEndTime = convert(endTime, ZoneId.systemDefault(), ZoneId.of("UTC"));
        this.customerApptId = customerApptId;
        this.userId = userId;
    }

    /**
     Returns an appointment id, associated with Appointment_ID attribute in the Appointments table.
     */
    public int getId() { return id; }

    /**
     Returns an appointment title, associated with Title attribute in the Appointments table.
     */
    public String getTitle() { return title; }

    /**
     Updates an appointment title, associated with Title attribute in the Appointments table.
     */
    public void setTitle(String title) { this.title = title; }

    /**
     Returns an appointment description, associated with Description attribute in the Appointments table.
     */
    public String getDescription() { return description; }

    /**
     Updates an appointment description, associated with Description attribute in the Appointments table.
     */
    public void setDescription(String description) { this.description = description; }

    /**
     Returns an appointment location, associated with Location attribute in the Appointments table.
     */
    public String getLocation() { return location; }

    /**
     Updates an appointment location, associated with Location attribute in the Appointments table.
     */
    public void setLocation(String location) { this.location = location; }

    /**
     Returns the name of a contact associated with an appointment.
     */
    public String getContact() { return contact; }

    /**
     Updates the name of a contact associated with an appointment.
     */
    public void setContact(String contact) { this.contact = contact; }

    /**
     Returns an appointment type, associated with Type attribute in the Appointments table.
     */
    public String getType() { return type; }

    /**
     Updates an appointment type, associated with Type attribute in the Appointments table.
     */
    public void setType(String type) { this.type = type; }

    /**
     Returns an appointment start time, associated with Start attribute in the Appointments table.
     */
    public Timestamp getStartTime() { return startTime; }

    /**
     Updates an appointment start time with both local and UTC values options.
     */
    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
        UTCStartTime = convert(startTime, ZoneId.systemDefault(), ZoneId.of("UTC"));
    }

    /**
     Returns an appointment end time, associated with End attribute in the Appointments table.
     */
    public Timestamp getEndTime() { return endTime; }

    /**
     Updates an appointment start time with both local and UTC values options.
     RUNTIME ERROR: Was failing to update appointment endTime because I passed
     this.startTime to the convert method by mistake, and didn't update UTCEndTime.
     */
    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
        UTCEndTime = convert(endTime, ZoneId.systemDefault(), ZoneId.of("UTC"));
    }

    /**
     Returns a customer id, associated with Customer_ID attribute in the Appointments table.
     */
    public int getCustomerApptId() { return customerApptId; }

    /**
     Updates a customer id, associated with Customer_ID attribute in the Appointments table.
     */
    public void setCustomerApptId(int customerApptId) { this.customerApptId = customerApptId; }

    /**
     Returns a user id, associated with User_ID attribute in the Appointments table.
     */
    public int getUserId() { return userId; }

    /**
     Updates a user id, associated with User_ID attribute in the Appointments table.
     */
    public void setUserId(int userId) { this.userId = userId; }

    /**
     Returns a contact id, associated with Contact_ID attribute in the Appointments table.
     */
    public int getContactId() { return this.contactId; }

    /**
     Returns an appointment's start time in UTC time zone.
     */
    public Timestamp getUTCStartTime() { return UTCStartTime; }

    /**
     Returns an appointment's end time in UTC time zone.
     */
    public Timestamp getUTCEndTime() { return UTCEndTime; }

    /**
     Returns a list of appointment attributes to be sent as values in a SQL update.
     */
    public List<Object> getAllAttributes() {
        return Arrays.asList(
                this.id,
                this.title,
                this.description,
                this.location,
                this.contactId,
                this.type,
                UTCStartTime,
                UTCEndTime,
                this.customerApptId,
                this.userId
        );
    }

    /**
     Returns the concatenation of a list of values into a format that can be used in a SQL query.
     */
    public String getAttributesAsValues() {
        List<String> sqlStrings = new ArrayList<>();

        for (Object o: this.getAllAttributes()) {
            if (o.getClass().equals(String.class) || o.getClass().equals(Timestamp.class)) {
                sqlStrings.add("'" + o + "'");
            } else {
                sqlStrings.add(String.valueOf(o));
            }
        }

        String sqlString = sqlStrings.stream().collect(Collectors.joining(", "));

        return "(" + sqlString + ");";
    }

    /**
     Returns the appointment columns to be updated.
     */
    public String getAttributesAsColumns() { return insertColumns; }

    /**
     Returns an observable list of the 12 months of the year as strings.
     */
    public static ObservableList<String> getMonths() { return months; }

    /**
     Returns an observable list of the seven days of the week as strings.
     */
    public static ObservableList<String> getWeekdays() { return weekdays; }
}
