package model.lib;

import annotations.ModelField;

import java.lang.reflect.Field;
import java.security.KeyException;
import java.sql.*;
import java.util.Arrays;
import java.util.HashMap;

public class Model {

    private final static String DATABASE = "BankingSystem";
    private final static String HOST = "mysql://localhost:3306/";
    private final static String USERNAME = "root";
    private final static String PASSWORD = "";

    public static Connection connectSQL() {
        String urlStructure = "jdbc:" + HOST + DATABASE;

        try {
            return DriverManager.getConnection(urlStructure, USERNAME, PASSWORD);
        } catch (SQLException exception) {
            System.out.println("SQLException: " + exception.getMessage());
            System.out.println("SQLState: " + exception.getSQLState());
            System.out.println("VendorError: " + exception.getErrorCode());
            return null;
        }
    }

    public boolean validateArguments(HashMap<String, Object> data) {
        Field[] fields = this.getClass().getDeclaredFields();

        try {
            for (Field field: fields) {
                ModelField modelField = field.getAnnotation(ModelField.class);
                if (!data.containsKey(field.getName()) && !modelField.isNull() && !modelField.pk() && modelField.foreignKey().isBlank()) {
                    throw new KeyException("Key Not Found: " + field.getName() + "\n----------\n" + String.join("\n", String.join(",\n",
                            java.util.Arrays.stream(fields)
                                    .map(Field::getName)
                                    .toArray(String[]::new))));
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            return false;
        }
        return false;
    }

    public void insertData(HashMap<String, Object> data) {
        if (validateArguments(data)) {
            String tableName = this.getClass().getSimpleName();
            String query = "INSERT INTO " + tableName + " " +
                    "(" + SQLBuilder.getFieldName(data) + ") " +
                    "VALUES (" + "?" + ",?".repeat(data.values().toArray().length - 1) + ")";

            try {
                Connection conn = connectSQL();
                if (conn != null) {
                    PreparedStatement pstmt = conn.prepareStatement(query);
                    SQLBuilder.setValues(data, pstmt);
                    pstmt.getParameterMetaData();
                    pstmt.executeUpdate();
                    System.out.println("Row Added!");
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }

            System.out.println(query);
        }


    }

}
