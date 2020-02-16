package com.hackthevalley.htv2;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
public class DatabaseHelper extends SQLiteOpenHelper{
    public static final String DATABASE_NAME = "locations.db";
    // public static final String TABLE_NAME = "locations.db";
    public static final String ID = "ID";
    public static final String LAT = "LATITUDE";
    public static final String LON = "LONGITUDE";
    public static final String TIME = "TIME";

    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, 1);
        SQLiteDatabase db = this.getWritableDatabase(); // added
    }

    @Override
    public void onCreate(SQLiteDatabase db)  {
        db.execSQL("create table places(ID integer, LATITUDE numeric, LONGITUDE numeric, TIME numeric PRIMARY KEY)");

        // ID INTEGER, LAT DECIMAL(18, 15), LON DECIMAL(18, 15), TIME DECIMAL(20, 10)
        // insertData(-1, 0.0, 0.0, 0.0);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)  {
        //db.execSQL("DROP")
        db.execSQL("drop table if exists places");
    }

    public boolean insertData (int id, double lat, double lon, double time){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ID, id);
        contentValues.put(LAT, lat);
        contentValues.put(LON, lon);
        contentValues.put(TIME, time);
        long result = db.insert("places", null, contentValues);

        if (result==-1) {
            return false;
        }
        else {
            return true;
        }
//        return result !=-1;
    }

}

