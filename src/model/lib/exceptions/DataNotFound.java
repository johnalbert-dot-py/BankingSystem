package model.lib.exceptions;

import java.util.List;

public class DataNotFound extends Exception{
    public DataNotFound(String tableName) {
        super("\n\tNo data found on: " + tableName);
    }

    public DataNotFound(String tableName, List<String> conditions) {
        super("\n\tNo data found on: " + tableName + "\n\tConditions: " + String.join(", ", conditions) + "\n\t----------");
    }
}
