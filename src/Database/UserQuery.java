package Database;

import Interfaces.SQLQuery;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class UserQuery implements SQLQuery {

    /**
     Returns the password associated with a particular user name.
     */
    public static String getPasswordForUser(String userName) throws SQLException {
        String sql = "SELECT * FROM USERS WHERE User_Name = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setString(1, userName);
        ResultSet rs = ps.executeQuery();
        String password = "";

        if (rs.next()) {
            password = rs.getString("Password");
            return password;
        } else {
            return "";
        }
    }

    /**
     Returns the user id associated with a particular user name.
     */
    public static int getUserId(String userName) {
        String sql = "SELECT USER_ID FROM USERS WHERE USER_NAME = '" + userName + "'";
        try {
            PreparedStatement ps = JDBC.connection.prepareStatement(sql);
            ResultSet selectedUserId = ps.executeQuery();
            if (selectedUserId.next()) {
                return selectedUserId.getInt("USER_ID");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return 0;
    }
}
