package lk.ac.mrt.cse.dbs.simpleexpensemanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String ACCOUNT_TABLE = "ACCOUNT_TABLE";
    public static final String COLUMN_ACC_NO = "ACC_NO";
    public static final String COLUMN_BANK = "BANK";
    public static final String COLUMN_HOLDER_NAME = "HOLDER_NAME";
    public static final String COLUMN_BALANCE = "BALANCE";

    public static final String TRANSACTION_TABLE = "TRANSACTION_TABLE";
    public static final String COLUMN_DATE = "COLUMN_DATE";
    public static final String COLUMN_TYPE = "COLUMN_TYPE";
    public static final String COLUMN_AMOUNT = "COLUMN_AMOUNT";
    public static final DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");

    public DatabaseHelper(Context context) {
        super(context, "Account_database.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createTableStatement = "CREATE TABLE " + ACCOUNT_TABLE + " (" + COLUMN_ACC_NO + " TEXT PRIMARY KEY, " + COLUMN_BANK + " TEXT, " + COLUMN_HOLDER_NAME + " TEXT, " + COLUMN_BALANCE + " REAL)";
        sqLiteDatabase.execSQL(createTableStatement);
        createTableStatement = "CREATE TABLE " + TRANSACTION_TABLE + " ( " + COLUMN_DATE + " TEXT, " + COLUMN_ACC_NO + " TEXT, " + COLUMN_TYPE + " TEXT, " + COLUMN_AMOUNT + " REAL)";
        sqLiteDatabase.execSQL(createTableStatement);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void addAccount(Account account){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_ACC_NO, account.getAccountNo());
        cv.put(COLUMN_BANK, account.getBankName());
        cv.put(COLUMN_HOLDER_NAME, account.getAccountHolderName());
        cv.put(COLUMN_BALANCE, account.getBalance());

        db.insert(ACCOUNT_TABLE, null, cv);
        db.close();
    }

    public List<Account> getAllAccounts() {

        List<Account> allAccounts = new ArrayList<>();

        String query = "SELECT * FROM " + ACCOUNT_TABLE;

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(query,null);

        if (cursor.moveToFirst()) {

            do {
                String acc_no = cursor.getString(0);
                String bank = cursor.getString(1);
                String holder_name = cursor.getString(2);
                float balance = cursor.getFloat(3);

                Account account = new Account(acc_no, bank, holder_name, balance);
                allAccounts.add(account);
            } while (cursor.moveToNext());
        } else {
        }

        cursor.close();
        db.close();
        return allAccounts;
    }

    public List<String> getAllAccountNumbers() {

        List<String> allAccountNumbers = new ArrayList<>();

        String query = "SELECT " + COLUMN_ACC_NO + " FROM " + ACCOUNT_TABLE;

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(query,null);

        if (cursor.moveToFirst()) {

            do {
                String acc_no = cursor.getString(0);

                allAccountNumbers.add(acc_no);
            } while (cursor.moveToNext());
        } else {
        }

        cursor.close();
        db.close();
        return allAccountNumbers;
    }

    public Account getAccount(String accNo) {

        String query = "SELECT * FROM " + ACCOUNT_TABLE + " WHERE " + COLUMN_ACC_NO + " = " + accNo;

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(query,null);

        String acc_no = cursor.getString(0);
        String bank = cursor.getString(1);
        String holder_name = cursor.getString(2);
        float balance = cursor.getFloat(3);

        Account req_account = new Account(accNo, bank, holder_name,balance);

        cursor.close();
        db.close();
        return req_account;
    }

    public void delAccount(String accNo) {

        String query = "DELETE FROM " + ACCOUNT_TABLE + " WHERE " + COLUMN_ACC_NO + " = " + accNo;

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query,null);

        cursor.close();
        db.close();
    }

    public void update(String accountNo, ExpenseType expenseType, double amount){

        SQLiteDatabase db = this.getWritableDatabase();
        String query = "";
        String amount_str = amount + "";

        switch (expenseType){
            case EXPENSE:
                query = "UPDATE " + ACCOUNT_TABLE + " SET " + COLUMN_BALANCE + " = " + COLUMN_BALANCE + " - " + amount_str + " WHERE " + COLUMN_ACC_NO + " = " + accountNo;
                break;
            case INCOME:
                query = "UPDATE " + ACCOUNT_TABLE + " SET " + COLUMN_BALANCE + " = " + COLUMN_BALANCE + " + " + amount_str + " WHERE " + COLUMN_ACC_NO + " = " + accountNo;
                break;
        }

        Cursor cursor = db.rawQuery(query,null);

        cursor.close();
        db.close();
    }

    public void addLog(Date date, String accountNo, ExpenseType expenseType, double amount){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        String date_str = dateFormat.format(date);
        cv.put(COLUMN_DATE, date_str);

        cv.put(COLUMN_ACC_NO , accountNo);

        String type = "";

        switch (expenseType){
            case EXPENSE:
                type = "Expense";
                break;
            case INCOME:
                type = "Income";
                break;
        }

        cv.put(COLUMN_TYPE, type);
        cv.put(COLUMN_AMOUNT, amount);

        db.insert(TRANSACTION_TABLE, null, cv);
        db.close();
    }

    public List<Transaction> getAll() throws ParseException {

        List<Transaction> allTransactions = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TRANSACTION_TABLE;

        Cursor cursor = db.rawQuery(query, null);

        if(cursor.moveToFirst()){
            do {
                String date_str = cursor.getString(0);
                String accountNo = cursor.getString(1);
                String type = cursor.getString(2);
                double amount = cursor.getDouble(3);

                Date date = dateFormat.parse(date_str);
                ExpenseType expenseType;
                if (type.equals("Expense")) {
                    expenseType = ExpenseType.EXPENSE;
                } else {
                    expenseType = ExpenseType.INCOME;
                }

                Transaction transaction = new Transaction(date, accountNo, expenseType, amount);
                allTransactions.add(transaction);
            } while (cursor.moveToNext());
        }
        else {

        }

        db.close();
        cursor.close();
        return allTransactions;
    }

    public List<Transaction> getLimited(int limit) throws ParseException {

        List<Transaction> limitedTransactions = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TRANSACTION_TABLE;

        Cursor cursor = db.rawQuery(query, null);

        int index=0;

        if(cursor.moveToFirst()){
            do {
                index++;

                String date_str = cursor.getString(0);
                String accountNo = cursor.getString(1);
                String type = cursor.getString(2);
                double amount = cursor.getDouble(3);

                Date date = dateFormat.parse(date_str);
                ExpenseType expenseType;
                if (type.equals("Expense")) {
                    expenseType = ExpenseType.EXPENSE;
                } else {
                    expenseType = ExpenseType.INCOME;
                }

                Transaction transaction = new Transaction(date, accountNo, expenseType, amount);
                limitedTransactions.add(transaction);
            } while (cursor.moveToNext() && index<limit);
        }
        else {

        }

        db.close();
        cursor.close();
        return limitedTransactions;
    }
}
