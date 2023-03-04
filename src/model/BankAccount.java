package model;

import annotations.ModelField;
import model.lib.Model;

public class BankAccount extends Model {
    @ModelField(pk=true)
    public int id;

    @ModelField()
    public String accountNumber;
}
