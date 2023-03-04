package model;

import annotations.ModelField;
import model.lib.Model;

public class SavingsAccount extends Model {
    @ModelField(pk=true)
    public int id;

    @ModelField()
    public double amount;
}
