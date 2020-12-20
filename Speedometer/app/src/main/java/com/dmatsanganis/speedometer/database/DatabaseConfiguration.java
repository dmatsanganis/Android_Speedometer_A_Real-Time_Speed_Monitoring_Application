package com.dmatsanganis.speedometer.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.dmatsanganis.speedometer.object.ViolationObject;

// Database Configuration.
public class DatabaseConfiguration extends SQLiteOpenHelper {

    // Debug's Purpose Tag.
    private static final String DATABASE_NAME = "Speedometer_db";
    private static final int DATABASE_VERSION = 1;

    private static final String CREATE_TABLE =
            "create table " + SpeedViolationObject.SpeedViolationEntity.TABLE_NAME + "("
                    + SpeedViolationObject.SpeedViolationEntity.ID + " integer primary key autoincrement,"
                    + SpeedViolationObject.SpeedViolationEntity.LONGITUDE + " real,"
                    + SpeedViolationObject.SpeedViolationEntity.LATITUDE + " real,"
                    + SpeedViolationObject.SpeedViolationEntity.SPEED + " integer not null,"
                    + SpeedViolationObject.SpeedViolationEntity.TIMESTAMP + " text);";

    private static final String DROP_TABLE = "drop table if exists " + SpeedViolationObject.SpeedViolationEntity.TABLE_NAME;

    private static final String SELECT_ALL = "select * from " + SpeedViolationObject.SpeedViolationEntity.TABLE_NAME + " order by timestamp desc";

    private static final String SELECT_ALL_BY_WEEK = "select * from " + SpeedViolationObject.SpeedViolationEntity.TABLE_NAME +
            " where timestamp > (select datetime('now', '-6 day')) order by timestamp desc";

    public DatabaseConfiguration(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
        Log.d("Database Configuration", "Database is created...");
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Create DB Table.
        sqLiteDatabase.execSQL(CREATE_TABLE);
        Log.d("Database Operations", "Table is created...");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(DROP_TABLE);
        Log.d("Database Operations", "Table is removed...");
        sqLiteDatabase.execSQL(CREATE_TABLE);
        Log.d("Database Operations", "Table is created...");
    }

    // Add new record
    public void addViolation(ViolationObject violationObject) {
        ContentValues values = new ContentValues();
        values.put(SpeedViolationObject.SpeedViolationEntity.LONGITUDE, violationObject.getLongitude());
        values.put(SpeedViolationObject.SpeedViolationEntity.LATITUDE, violationObject.getLatitude());
        values.put(SpeedViolationObject.SpeedViolationEntity.SPEED, violationObject.getSpeed());
        values.put(SpeedViolationObject.SpeedViolationEntity.TIMESTAMP, violationObject.getTimestamp().toString());

        SQLiteDatabase db = getWritableDatabase();
        db.insert(SpeedViolationObject.SpeedViolationEntity.TABLE_NAME, null, values);
        System.out.println(values.toString());
        db.close();
    }

    // Get all records from the Database.
    public List<ViolationObject> getViolations() {
        List<ViolationObject> result = new ArrayList<>();

        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery(SELECT_ALL, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            ViolationObject violationObject = new ViolationObject();

            violationObject.setId(cursor.getInt(cursor.getColumnIndex("id")));
            violationObject.setLongitude(cursor.getDouble(cursor.getColumnIndex("longitude")));
            violationObject.setLatitude(cursor.getDouble(cursor.getColumnIndex("latitude")));
            violationObject.setSpeed(cursor.getDouble(cursor.getColumnIndex("speed")));
            violationObject.setTimestamp(Timestamp.valueOf(cursor.getString(cursor.getColumnIndex("timestamp"))));

            result.add(violationObject);
            cursor.moveToNext();
        }
        db.close();
        return result;
    }

    // Get all records from last week.
    public List<ViolationObject> getViolationsByWeek() {

        List<ViolationObject> result = new ArrayList<>();

        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery(SELECT_ALL_BY_WEEK, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            ViolationObject violationObject = new ViolationObject();

            violationObject.setId(cursor.getInt(cursor.getColumnIndex("id")));
            violationObject.setLongitude(cursor.getDouble(cursor.getColumnIndex("longitude")));
            violationObject.setLatitude(cursor.getDouble(cursor.getColumnIndex("latitude")));
            violationObject.setSpeed(cursor.getDouble(cursor.getColumnIndex("speed")));
            violationObject.setTimestamp(Timestamp.valueOf(cursor.getString(cursor.getColumnIndex("timestamp"))));

            result.add(violationObject);
            cursor.moveToNext();
        }
        db.close();
        return result;
    }

    // Get records based on custom timestamp.
    public List<ViolationObject> getViolationsByTimestamp(String from, String to) {

        String SELECT_ALL_BY_TIMESTAMP = "select * from " + SpeedViolationObject.SpeedViolationEntity.TABLE_NAME +
                " where timestamp between '" + from + "' and '" + to + "' order by timestamp desc ";

        List<ViolationObject> result = new ArrayList<>();

        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery(SELECT_ALL_BY_TIMESTAMP, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            ViolationObject violationObject = new ViolationObject();

            violationObject.setId(cursor.getInt(cursor.getColumnIndex("id")));
            violationObject.setLongitude(cursor.getDouble(cursor.getColumnIndex("longitude")));
            violationObject.setLatitude(cursor.getDouble(cursor.getColumnIndex("latitude")));
            violationObject.setSpeed(cursor.getDouble(cursor.getColumnIndex("speed")));
            violationObject.setTimestamp(Timestamp.valueOf(cursor.getString(cursor.getColumnIndex("timestamp"))));

            result.add(violationObject);
            cursor.moveToNext();
        }
        db.close();
        return result;
    }
}
