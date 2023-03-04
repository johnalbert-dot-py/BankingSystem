package model;
import annotations.ModelField;
import model.lib.Model;
import model.lib.enums.TransactionType;

import java.util.Date;

public class Transaction extends Model{
    @ModelField(pk = true)
    public int id;
    @ModelField()
    public Date date;
    @ModelField()
    public double amount;
    @ModelField()
    public TransactionType status;
}
