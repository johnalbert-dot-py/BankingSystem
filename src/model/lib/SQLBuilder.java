package model.lib;

import annotations.ModelField;
import model.lib.enums.ParsedDataType;

import java.lang.reflect.Field;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

public class SQLBuilder {
    public static String buildConstraints(ModelField modelField, String structure, Field field, ParsedDataType type)  {

        // constraints
        if (!modelField.isNull() && modelField.foreignKey().isBlank()) {
            structure += " NOT NULL";
        }

        if (modelField.ai()) {
            structure += " AUTO_INCREMENT";
        }

        // PRIMARY KEY CONSTRAINT
        if (modelField.pk()) {
            structure += " AUTO_INCREMENT, PRIMARY KEY(" + field.getName() + ")";
        }

        // DEFAULT VALUE CONSTRAINT
        if (!modelField.value().isBlank()) {
            if (type.equals(ParsedDataType.VARCHAR)) {
                structure += "DEFAULT '" + modelField.value() + "'";
            } else {
                structure += "DEFAULT " + modelField.value() + "";
            }
        }

        // FOREIGN KEY CONSTRAINT
        if (!modelField.foreignKey().isBlank()) {
            structure += ", FOREIGN KEY(" + field.getName() + ") REFERENCES " + modelField.reference_to();
        }


        return structure;
    }
    public static String toSQLField(Field field) {

        String structure = field.getName();
        String dataType = field.getAnnotatedType().toString();
        ParsedDataType parsedDataType = ParsedDataType.VARCHAR;
        ModelField modelField = field.getAnnotation(ModelField.class);

        if (dataType.contains("int")) {
            structure += " INT";
            parsedDataType = ParsedDataType.INT;
        }

        if (dataType.toLowerCase().contains("double")) {
            structure += " DOUBLE";
            parsedDataType = ParsedDataType.DOUBLE;
        }

        if (dataType.toLowerCase().contains("float")) {
            structure += " FLOAT";
            parsedDataType = ParsedDataType.FLOAT;
        }

        if (dataType.toLowerCase().contains("string")) {
            structure += " VARCHAR(" + modelField.maxLength() + ")";
            parsedDataType = ParsedDataType.VARCHAR;
        }

        if (dataType.toLowerCase().contains("date")) {
            structure += " DATE";
            parsedDataType = ParsedDataType.DATE;
        }

        if (dataType.contains("model.") && !dataType.contains("model.lib")) {
            if (modelField.foreignKeyType().equals(ParsedDataType.INT)) {
                structure += " INT";
            }
            parsedDataType = ParsedDataType.INT;
        }

        if (dataType.contains("model.") && dataType.contains("model.lib.enums")) {
            structure += " VARCHAR(" + modelField.maxLength() + ")";
            parsedDataType = ParsedDataType.VARCHAR;
        }

        if (field.isAnnotationPresent(ModelField.class)) {
            structure = buildConstraints(modelField, structure, field, parsedDataType);
        }

        return structure;
    }

    public static String getFieldName(HashMap<String, Object> data) {
        return String.join(", ", data.keySet());
    }

    public static void setValues(HashMap<String, Object> data, PreparedStatement pstmt) {
        int index = 1;
        for (Object value: data.values()) {
            try {
                pstmt.setObject(index, value);
            } catch (Exception ignored) {}
            index++;
        }
    }

    public static String buildSelectStatement(String table,
                                              Model.Operator operation,
                                              List<String> fields,
                                              List<String> where,
                                              List<String> order,
                                              List<String> group,
                                              int limit) {
        /* *
         * SELECT * FROM Users;
         * SELECT firstName, lastName FROM Users;
         * SELECT firstName, lastName FROM Users WHERE id = 2;
         * SELECT firstName, lastName FROM Users WHERE id = 2 AND username = 'test';
         * SELECT firstName, lastName FROM Users WHERE id = 2 AND username = 'test' ORDER BY firstName ASC;
         * SELECT lastName, COUNT(*) FROM Users GROUP BY firstName;
         * */

        if (fields.size() == 0) {
            fields.add("*");
        }

        String select = "SELECT " + String.join(", ", fields) + " FROM " + table;

        if (where != null && order != null && order.size() > 0) {
            // should add the ORDER BY clause
            where.add(" ORDER BY " + String.join(", ", order));
        }

        if (where != null && group != null && group.size() > 0) {
            // should add the GROUP BY clause
            where.add(" GROUP BY " + String.join(", ", group));
        }

        // should add the WHERE clause
        if (where != null && where.size() > 0){
            if (where.size() > 1) {
                select += " WHERE " + String.join(" " + operation.toString() + " ", where);
            } else {
                select += " WHERE " + String.join(" ", where);
            }
        }

        if (limit > 0) {
            select += " LIMIT " + limit;
        }

        return select;
    }
}
