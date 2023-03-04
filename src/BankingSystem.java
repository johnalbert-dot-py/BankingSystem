
import model.*;
import model.lib.Migration;
import model.lib.Model;

import java.util.ArrayList;
import java.util.List;

public class BankingSystem {

    public static void main(String[] args) {
        if (args.length > 0) {
            if (args[0].equals("--make-migrations")) {

                List<Model> toMigrate = new ArrayList<>();
                toMigrate.add(new BankAccount());
                toMigrate.add(new User());
                toMigrate.add(new CheckingAccount());
                toMigrate.add(new SavingsAccount());
                toMigrate.add(new Transaction());

                Migration mig = new Migration(toMigrate);
                mig.migrateFields();
            }
        } else {
            User user = new User();
            user.createOne("John Albert", "Bayog", "Flores", "08-19-2003", "Nueva Ecija", "Philippines");
        }
    }


}
