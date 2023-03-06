package model.lib;

import annotations.ModelField;
import model.lib.exceptions.DataNotFound;

import java.lang.reflect.Field;
import java.security.KeyException;
import java.sql.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


public class Model {

    private final static String DATABASE = "BankingSystem";
    private final static String HOST = "mysql://localhost:3306/";
    private final static String USERNAME = "root";
    private final static String PASSWORD = System.getenv("MYSQL_PASSWORD");

    enum Operator {
        AND,
        OR
    }

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

    public void validateArguments(HashMap<String, Object> data) throws Exception {
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
        }
    }

    public void insertData(HashMap<String, Object> data) {
        try {
            validateArguments(data);
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
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ResultSet getData(List<String> fields, List<String> where, Operator operator, List<String> orderBy, List<String> groupBy, int limit) {
        /* *
        *
        * Example for each argument for this method:
        * fields: username, firstName, lastName
        * where: username = 'test'
        * operator: AND, OR
        *
        * */
        String tableName = this.getClass().getSimpleName();
        String sql = SQLBuilder.buildSelectStatement(tableName, operator, fields, where, orderBy, groupBy, limit);
        try {
            return executeSQL(sql);
        } catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }
    }

    public ResultSet getOne(List<String> fields, List<String> where, Operator operator) throws Exception {
        ResultSet result = getData(fields, where, operator, null, null, 1);
        try {
            if (result.next()) {
                return result;
            } else {
                throw new DataNotFound(this.getClass().getSimpleName(), where);
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
            throw new DataNotFound(this.getClass().getSimpleName(), where);
        }
    }

    public ResultSet getAll(List<String> fields, List<String> where, Operator operator) {
        return getData(fields, where, operator, null, null, 0);
    }

    public ResultSet executeSQL(String query) {
        try {
            Connection conn = connectSQL();
            if (conn != null) {
                PreparedStatement pstmt = conn.prepareStatement(query);
                return pstmt.executeQuery();
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return null;
    }

}
