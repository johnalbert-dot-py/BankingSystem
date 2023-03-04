package model;

import annotations.ModelField;
import model.lib.Model;

public class CheckingAccount extends Model {
    @ModelField(pk=true)
    public int id;

    @ModelField()
    public double amount;
}
