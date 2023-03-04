package model.lib;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class Migration {
    List<Model> classToMigrates = new ArrayList<>();
    public Migration(Model toMigrate) {
        this.classToMigrates.add(toMigrate);
    }

    public Migration(List<Model> toMigrate) {
        this.classToMigrates = toMigrate;
    }

    public boolean tableExists(String tableName) {
        String query = "SELECT EXISTS " +
                       "(SELECT 1 FROM information_schema.tables WHERE table_schema = ? AND table_name = ?)";

        try {
            Connection conn = Model.connectSQL();
            if (conn != null) {
                PreparedStatement pstmt = conn.prepareStatement(query);
                pstmt.setString(1, "BankingSystem");
                pstmt.setString(2, tableName);
                ResultSet rs = pstmt.executeQuery();
                rs.next();
                return rs.getBoolean(1);
            }
            return false;
        } catch (SQLException exception) {
            return false;
        }
    }

    public void executeMigrations(String query, String tableName) throws SQLException {
        try {
            Connection conn = Model.connectSQL();
            if (conn != null) {
                Statement stmt = conn.createStatement();
                stmt.executeUpdate(query);
                System.out.println("Table" + tableName + " Created.");
            }
        } catch (SQLException exception) {
            System.out.println("SQLException: " + exception.getMessage());
            System.out.println("SQLState: " + exception.getSQLState());
            System.out.println("VendorError: " + exception.getErrorCode());
        }
    }

    public String migrateData(String tableName, Field[] fields) {
        if (!tableExists(tableName)) {

            List<String> fieldData = new ArrayList<String>();

            for (Field field: fields) {
                fieldData.add(SQLBuilder.toSQLField(field));
            }

            return "CREATE TABLE IF NOT EXISTS " + tableName + " (" + String.join(", ", fieldData) + ");";
        } else {
            List<String> fieldData = new ArrayList<String>();

            for (Field field: fields) {
                fieldData.add(SQLBuilder.toSQLField(field));
            }
            return "ALTER TABLE " + tableName + "\n ALTER COLUMN(" + String.join(", ", fieldData) + ");";
        }
    }

    public void migrateFields() {
        for (Model classMigrate : this.classToMigrates) {
            Field[] fields = classMigrate.getClass().getDeclaredFields();
            try {
                String query = this.migrateData(classMigrate.getClass().getSimpleName(), fields);
                System.out.println("[+] Migrating: " + classMigrate.getClass().getSimpleName());
                if (!query.isBlank()) {
                    executeMigrations(query, classMigrate.getClass().getSimpleName());
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

}
