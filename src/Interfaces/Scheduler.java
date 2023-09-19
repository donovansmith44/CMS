package Interfaces;

import Database.Appointment;
import Database.Appointments;
import Database.JDBC;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 Provides helper methods to ensure proper scheduling of appointments to implementing classes.
 */
public interface Scheduler {
    /**
     Converts a Timestamp's value from one time zone to another.
     */
    default Timestamp convert(Timestamp t, ZoneId origin, ZoneId target) {
        LocalDateTime originDT = t.toLocalDateTime();
        ZonedDateTime originZonedDT = originDT.atZone(origin);
        ZonedDateTime targetZonedDT = originZonedDT.withZoneSameInstant(target);
        LocalDateTime targetDT = targetZonedDT.toLocalDateTime();
        return Timestamp.valueOf(targetDT);
    }

    /**
     Checks that an appointment's start time is before its end time for input validation.
     */
    default boolean startIsBeforeEnd(Timestamp startTime, Timestamp endTime) {
        return !startTime.after(endTime);
    }

    /**
     Checks that an appointment's time does not overlap with another appointment time.
     */
    default boolean badTime(Timestamp start1, Timestamp end1, Timestamp start2, Timestamp end2) {
        //s2 <= s1 < e2
        boolean startsDuringAnAppointment = !start1.before(start2) & start1.before(end2);
        //s2 < e1 <= e2
        boolean cutsIntoAnAppointment = end1.after(start2) & !end1.after(end2);

        if (startsDuringAnAppointment | cutsIntoAnAppointment) {
            return true;
        } else {
            return false;
        }
    }

    /**
     Checks that a customer does not have an overlapping appointment time.
     */
    default boolean customerHasScheduleConflicts(int customerId, Timestamp startTime, Timestamp endTime, ObservableList<Appointment> appointments, int appointmentId) {
        ObservableList<Appointment> customerAppointments =
                appointments.filtered(a -> a.getCustomerApptId() == customerId);

        for (Appointment a: customerAppointments) {
            if (badTime(startTime, endTime, a.getStartTime(), a.getEndTime())
                    && appointmentId != a.getId()) {
                return true;
            }
        }
        return false;
    }

    /**
     Ensures that an appointment's start and end time is within the business hours of the company.
     */
    default boolean scheduledWithinBusinessHours(Timestamp startTime, Timestamp endTime) {
        LocalDateTime startET =
                convert(startTime, ZoneId.systemDefault(), ZoneId.of("America/New_York")).toLocalDateTime();
        LocalDateTime endET =
                convert(endTime, ZoneId.systemDefault(), ZoneId.of("America/New_York")).toLocalDateTime();
        boolean startsTooEarly = 
                startET.toLocalTime().isBefore(LocalTime.of(8, 0));
        boolean startsTooLate = 
                startET.toLocalTime().isAfter(LocalTime.of(21, 59));
        boolean endsTooLate = 
                !endET.toLocalTime().isBefore(LocalTime.of(22, 00));
        boolean isTooLong = 
                startET.getDayOfWeek() != endET.getDayOfWeek();

        return !(startsTooEarly | startsTooLate | endsTooLate | isTooLong);
    }

    /**
     Ensures that an appointment has time values that will allow it to be added to the schedule without conflicts.
     */
    default boolean canSchedule(Timestamp startTime, Timestamp endTime, int customerId, ActionEvent actionEvent, int appointmentId) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Scheduling Error");

        if (!scheduledWithinBusinessHours(startTime, endTime)) {
            alert.setContentText("You must schedule within appropriate business hours (8AM-10PM EST)");
            alert.showAndWait();
            return false;
        } else if (customerHasScheduleConflicts(customerId, startTime, endTime, Appointments.get(), appointmentId)) {
            alert.setContentText("This customer has an overlapping appointment at this time!");
            alert.showAndWait();
            return false;
        } else if (!startIsBeforeEnd(startTime, endTime)) {
            alert.setContentText("Appointment start time must be after its end time!");
            alert.showAndWait();
            return false;
        } else {
            return true;
        }
    }

    /**
     Adds a new appointment to the schedule, updates the appointments table in the database, and returns true
     if the addition of the appointment was successful.
     */
    default boolean successfullyScheduled(Appointment newAppointment, Timestamp startTime, Timestamp endTime, ActionEvent actionEvent) {
        String sql =
                "INSERT INTO APPOINTMENTS " + newAppointment.getAttributesAsColumns() +
                        " VALUES " + newAppointment.getAttributesAsValues();

        if (canSchedule(startTime, endTime, newAppointment.getCustomerApptId(), actionEvent, newAppointment.getId())) {
            try {
                PreparedStatement ps = JDBC.connection.prepareStatement(sql);
                ps.executeUpdate();
                Appointments.add(newAppointment);
                return true;
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

        return false;
    }

    /**
     Updates an appointment which is already stored in the database with new attributes.
     */
    default void updateAppointment(Appointment appointment, ActionEvent actionEvent) {
        String sql =
                "UPDATE APPOINTMENTS " +
                        "SET TITLE = " + "'" + appointment.getTitle() + "', " +
                        "DESCRIPTION = " + "'" + appointment.getDescription() + "', " +
                        "LOCATION = " + "'" + appointment.getLocation() + "', " +
                        "CONTACT_ID = " + "" + appointment.getContactId() + ", " +
                        "TYPE = " + "'" + appointment.getType() + "', " +
                        "START = " + "'" + appointment.getUTCStartTime() + "', " +
                        "END = " + "'" + appointment.getUTCEndTime() + "', " +
                        "CUSTOMER_ID = " + appointment.getCustomerApptId() + ", " +
                        "USER_ID = " + appointment.getUserId() + " " +
                        "WHERE APPOINTMENT_ID = " + appointment.getId() + ";";

        try {
            PreparedStatement ps = JDBC.connection.prepareStatement(sql);
            ps.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

}
