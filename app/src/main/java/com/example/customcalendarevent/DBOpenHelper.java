package com.example.customcalendarevent;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBOpenHelper extends SQLiteOpenHelper {

    private static final String CREATE_TABLE = "CREATE TABLE "+ DBStructure.EVENT_TABLE + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, " + DBStructure.EVENT +" TEXT, "
            + DBStructure.TIME + " TEXT," + DBStructure.DATE + " TEXT, "+ DBStructure.MONTH +" TEXT, " + DBStructure.YEAR +" TEXT, " + DBStructure.IMAGE +" TEXT)";


    private  static final String DROP_TABLE = "DROP TABLE IF EXISTS "+DBStructure.EVENT_TABLE;


    public DBOpenHelper(@Nullable Context context) {
        super(context, DBStructure.DB_NAME, null, DBStructure.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE);
        onCreate(db);
    }

    public void saveEvent(String event, String time, String date, String month, String year,String image, SQLiteDatabase database){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBStructure.EVENT,event);
        contentValues.put(DBStructure.TIME,time);
        contentValues.put(DBStructure.DATE,date);
        contentValues.put(DBStructure.MONTH,month);
        contentValues.put(DBStructure.YEAR,year);
        //contentValues.put(DBStructure.IMAGE,image);

        database.insert(DBStructure.EVENT_TABLE,null,contentValues);
    }

    public Cursor readEvents(String date, SQLiteDatabase database){
        String [] Columns = {DBStructure.EVENT, DBStructure.TIME, DBStructure.DATE, DBStructure.MONTH, DBStructure.YEAR, DBStructure.IMAGE};
        String Selection = DBStructure.DATE+"=?";
        String [] SelectionArgs = {date};

        return database.query(DBStructure.EVENT_TABLE,Columns,Selection,SelectionArgs,null,null,null);

    }

    public Cursor readEventsPerMonth(String month,String year, SQLiteDatabase database){
        String [] Columns = {DBStructure.EVENT, DBStructure.TIME, DBStructure.DATE, DBStructure.MONTH, DBStructure.YEAR, DBStructure.IMAGE};
        String Selection = DBStructure.MONTH +"=? and " +DBStructure.YEAR+"=?";
        String [] SelectionArgs = {month,year};

        return database.query(DBStructure.EVENT_TABLE,Columns,Selection,SelectionArgs,null,null,null);

    }
    public void deleteEvent(String event, String date, String time, SQLiteDatabase database){
        String selection = DBStructure.EVENT+"=? and "+DBStructure.DATE+"=? and "+DBStructure.TIME+"=?";
        String[] selectionArg = {event,date,time};
        database.delete(DBStructure.EVENT_TABLE,selection,selectionArg);
    }

}
