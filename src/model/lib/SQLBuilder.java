package model.lib;

import annotations.ModelField;
import model.lib.enums.ParsedDataType;

import java.lang.reflect.Field;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;

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
}
