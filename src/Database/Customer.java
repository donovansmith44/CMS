package Database;

import Interfaces.Alerts;
import Interfaces.SQLQuery;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 Enables the creation and manipulation of customer objects, and their data members.
 */
public class Customer implements SQLQuery, Alerts {
    private final int id;
    private String name;
    private String address;
    private String postalCode;
    private String phoneNumber;
    private int divisionID;
    private final String insertColumns =
            "(CUSTOMER_ID, CUSTOMER_NAME, ADDRESS, POSTAL_CODE, PHONE, DIVISION_ID)";

    public Customer(int id, String name, String address, String postalCode, String phoneNumber, int divisionID) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.postalCode = postalCode;
        this.phoneNumber = phoneNumber;
        this.divisionID = divisionID;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getDivisionID() {
        return divisionID;
    }

    public void setDivisionID(int divisionID) {
        this.divisionID = divisionID;
    }

    public String getDivision(int divisionId) {
        try {
            ResultSet selectedDivision = doSelect("SELECT DIVISION FROM FIRST_LEVEL_DIVISIONS WHERE DIVISION_ID = " + divisionId);
            if (selectedDivision.next()) {
                return selectedDivision.getString("DIVISION");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return "";
    }

    public int getCountryId() {
        try {
            ResultSet selectedCountryId = doSelect("SELECT COUNTRY_ID FROM FIRST_LEVEL_DIVISIONS WHERE DIVISION_ID = " + this.divisionID);
            if (selectedCountryId.next()) {
                return selectedCountryId.getInt("COUNTRY_ID");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return 0;
    }

    public String getCountry() {
        try {
            ResultSet selectedCountry = doSelect("SELECT COUNTRY FROM COUNTRIES WHERE COUNTRY_ID = " + getCountryId());
            if (selectedCountry.next()) {
                return selectedCountry.getString("COUNTRY");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return "";
    }

    public List<Object> getAllAttributes() {
        return Arrays.asList(
                this.id,
                this.name,
                this.address,
                this.postalCode,
                this.phoneNumber,
                this.divisionID
        );
    }

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

    public String getAttributesAsColumns() { return insertColumns; }

}
