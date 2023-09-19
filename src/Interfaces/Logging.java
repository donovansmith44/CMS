package Interfaces;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Date;
import java.sql.Timestamp;

/**
 Provides logging capabilities of user logins to the LoginScreen controller.
 */
public interface Logging {
    /**
     Writes a username, date, timestamp, and status to a text file upon a user login attempt.

     RUNTIME ERROR: The writer was not appending to the login_activity.txt file, and was
     overwriting the file upon each login, instead. This was fixed by using the BufferedWriter
     and PrintWriter classes to work with the FileWriter object, rather than using the FileWriter
     object alone.
     */
    default void reportLogin(String fileName, String userName, Timestamp loginTime, String loginStatus) {
        try (FileWriter writer = new FileWriter(fileName, true);
            BufferedWriter bw = new BufferedWriter(writer);
            PrintWriter pw = new PrintWriter(bw))
        {
            pw.println("---LOGIN ATTEMPT---");
            pw.println("User Name: " + userName);
            pw.println("Login Date: " + Date.valueOf(loginTime.toLocalDateTime().toLocalDate()));
            pw.println("Timestamp: " + loginTime);
            pw.println("Login status: " + loginStatus);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
