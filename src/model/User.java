package model;

import annotations.ModelField;
import model.lib.Model;
import model.lib.enums.ParsedDataType;

import java.sql.Date;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class User extends Model {
    @ModelField(pk = true)
    private int id;

    @ModelField()
    private String firstName;

    @ModelField()
    private String lastName;

    @ModelField()
    private String middleName;

    @ModelField()
    private Date birthDate;

    @ModelField(isNull = true)
    private String streetAddress;

    @ModelField(isNull = true)
    private String cityAddress;

    @ModelField()
    private String regionAddress;

    @ModelField()
    private String countryAddress;

    @ModelField(foreignKey = "BankAccount", reference_to = "BankAccount(id)", foreignKeyType = ParsedDataType.INT)
    private BankAccount bankAccount;

    public User() {
    }

    public User(String firstName,
                String lastName,
                String middleName,
                String streetAddress,
                String cityAddress,
                Date birthDate,
                String regionAddress,
                String countryAddress) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.middleName = middleName;
        this.streetAddress = streetAddress;
        this.cityAddress = cityAddress;
        this.birthDate = birthDate;
        this.regionAddress = regionAddress;
        this.countryAddress = countryAddress;
    }
    
    public void createOne(String firstName, String middleName, String lastName, String birthDate, String regionAddress, String countryAddress) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy", Locale.ENGLISH);
        Date date = Date.valueOf(LocalDate.parse(birthDate, formatter));
        HashMap<String, Object> data = new HashMap<>();
        data.put("firstName", firstName);
        data.put("middleName", middleName);
        data.put("lastName", lastName);
        data.put("birthDate", date);
        data.put("regionAddress", regionAddress);
        data.put("countryAddress", countryAddress);
        insertData(data);
    }

    public User getOne(int id) {
        // This will get a User object on database via its ID.
        // This will also set the values of the fields of this object.t
        List<String> fields = new ArrayList<>();
        fields.add("*");
        List<String> where = new ArrayList<>();
        where.add("id = " + id);

        try {
            ResultSet result = super.getOne(fields, where, null);
            this.firstName = result.getString("firstName");
            this.lastName = result.getString("lastName");
            this.middleName = result.getString("middleName");
            this.streetAddress = result.getString("streetAddress");
            this.cityAddress = result.getString("cityAddress");
            this.birthDate = result.getDate("birthDate");
            this.regionAddress = result.getString("regionAddress");
            this.countryAddress = result.getString("countryAddress");
            return this;
        }catch (Exception e) {
            e.printStackTrace();
            return new User();
        }
    }

    public int getId() {
        return id;
    }

    public String getFirstName() { return firstName; }

    public String getLastName() {
        return lastName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public String getCityAddress() {
        return cityAddress;
    }

    public String getRegionAddress() {
        return regionAddress;
    }

    public String getCountryAddress() {
        return countryAddress;
    }

    public BankAccount getBankAccount() {
        return bankAccount;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public void setCityAddress(String cityAddress) {
        this.cityAddress = cityAddress;
    }

    public void setRegionAddress(String regionAddress) {
        this.regionAddress = regionAddress;
    }

    public void setCountryAddress(String countryAddress) {
        this.countryAddress = countryAddress;
    }

    public void setBankAccount(BankAccount bankAccount) {
        this.bankAccount = bankAccount;
    }

}