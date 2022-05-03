package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.Context;

import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.DatabaseHelper;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

public class PersistentAccountDAO implements AccountDAO {

    DatabaseHelper accountDatabaseHelper;

    public PersistentAccountDAO(Context context){
        accountDatabaseHelper = new DatabaseHelper(context);
    }

    @Override
    public void addAccount(Account account){
        accountDatabaseHelper.addAccount(account);
    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        accountDatabaseHelper.delAccount(accountNo);
    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        accountDatabaseHelper.update(accountNo, expenseType, amount);
    }

    @Override
    public List<String> getAccountNumbersList() {
        return accountDatabaseHelper.getAllAccountNumbers();
    }

    @Override
    public List<Account> getAccountsList() {
        return accountDatabaseHelper.getAllAccounts();
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
        return accountDatabaseHelper.getAccount(accountNo);
    }
}
