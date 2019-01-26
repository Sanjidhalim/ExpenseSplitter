package com.sanjidhalim.expensesplitter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.List;

/**
 * Created by sanji on 6/3/2016.
 */
public class DbHelper extends SQLiteOpenHelper {
    private static DbHelper sInstance;

    private static final String DATABASE_NAME = "expense_splitter.db";
    private static final String CREATE_TRIP_TABLE = "create table trips"
            + "("
            + "_id" + " integer primary key autoincrement, "
            + "tripName" + " text not null, "
            + "personName" + " text not null " + ")";

    private static final String CREATE_EXPENSE_TABLE = "create table expenses"
            + "("
            + "_id" + " integer primary key autoincrement, "
            + "expenseName" + " text not null, "
            + "tripName" + " text not null, "
            + "expenseAmount" + " numeric, "
            + "personName" + " text not null " + ")";

    public static synchronized DbHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DbHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    private DbHelper (Context context){
        super(context, DATABASE_NAME, null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TRIP_TABLE);
        db.execSQL(CREATE_EXPENSE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //db.execSQL("DROP TABLE IF EXISTS trips");
        //db.execSQL("DROP TABLE IF EXISTS expenses");
        //onCreate(db);
    }

    public void insertTrip(String tripName, String[] participants){
        SQLiteDatabase db = this.getWritableDatabase();
        for (String participant:participants){
            ContentValues values = new ContentValues();
            values.put("tripName",tripName);
            values.put("personName",participant);
            db.insert("trips",null,values);
        }
    }

    public Cursor getAllTrips(){
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("select distinct tripName from trips", null);
    }

    public String[] getTripParticipants(String tripName){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor people= db.rawQuery("select distinct personName from trips where tripName='"+tripName+"'", null);
        people.moveToFirst();

        String[] peopleArray = new String[people.getCount()];
        int i=0;
        while (!people.isAfterLast()){
            peopleArray[i++]= people.getString(people.getColumnIndex("personName"));
            people.moveToNext();
        }
        return peopleArray;
    }

    public String[] getUniqueExpenses(String tripName){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor expense= db.rawQuery("select distinct expenseName from expenses where tripName='"+tripName+"'", null);
        expense.moveToFirst();

        String[] expenseArray = new String[expense.getCount()];
        int i=0;
        while (!expense.isAfterLast()){
           expenseArray[i++]= expense.getString(expense.getColumnIndex("expenseName"));
            expense.moveToNext();
        }
        return expenseArray;
    }

    public void saveExpenses(String tripName, String expenseName,String[] participants, double[] contributions){
        SQLiteDatabase db = this.getWritableDatabase();
        for (int i=0;i<participants.length;i++){
            ContentValues value = new ContentValues();
            value.put("expenseName",expenseName);
            value.put("tripName",tripName);
            value.put("expenseAmount",contributions[i]);
            value.put("personName",participants[i]);
            db.insert("expenses", null, value);
        }
    }

    public Cursor getTripExpenses(String trip){
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("select  expenseName,expenseAmount,personName from expenses where tripName='"+trip+"'", null);
    }

    public double totalSpentBy(String person, String tripname){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor total = db.rawQuery("select sum(expenseAmount) from expenses where tripname='"
                + tripname + "' and personName='" + person + "'", null);
        total.moveToFirst();
        return total.getDouble(0);
    }

    public void deleteExpense(String tripName,String expenseName){
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("delete from expenses where tripname='" + tripName + "'" +
                " and expenseName='" + expenseName + "'");
    }

    public void deleteTripAndExpenses(String tripName){
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("delete from expenses where tripname='"+tripName+"'");
        db.execSQL("delete from trips where tripname='"+tripName+"'");
    }
}
