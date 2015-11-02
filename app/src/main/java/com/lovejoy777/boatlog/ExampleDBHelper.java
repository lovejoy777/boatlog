package com.lovejoy777.boatlog;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by obaro on 02/04/2015.
 */
public class ExampleDBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "SQLiteExample.db";
    private static final int DATABASE_VERSION = 2;

    public static final String ENTRY_TABLE_NAME = "entries";
    public static final String ENTRY_COLUMN_ID = "_id";
    public static final String ENTRY_COLUMN_NAME = "name";
    public static final String ENTRY_COLUMN_TIME = "time";
    public static final String ENTRY_COLUMN_DATE = "date";
    public static final String ENTRY_COLUMN_LOCATION = "location";
    public static final String ENTRY_COLUMN_TRIP_ID = "trip_id";

    public static final String TRIPS_TABLE_NAME = "trips";
    public static final String TRIPS_COLUMN_ID = "_id";
    public static final String TRIPS_COLUMN_NAME = "name";
    public static final String TRIPS_COLUMN_DEPARTURE = "departure";
    public static final String TRIPS_COLUMN_DESTINATION = "destination";

    public static final String WAYPOINT_TABLE_NAME = "waypoint";
    public static final String WAYPOINT_COLUMN_ID = "_id";
    public static final String WAYPOINT_COLUMN_NAME = "name";
    public static final String WAYPOINT_COLUMN_LOCATION = "location";
    public static final String WAYPOINT_COLUMN_DESCRIPTION = "description";

    public ExampleDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // Create Enteries Table
        db.execSQL(
                "CREATE TABLE " + ENTRY_TABLE_NAME +
                        "(" + ENTRY_COLUMN_ID + " INTEGER PRIMARY KEY, " +
                        ENTRY_COLUMN_NAME + " TEXT, " +
                        ENTRY_COLUMN_TIME + " TEXT, " +
                        ENTRY_COLUMN_DATE + " TEXT, " +
                        ENTRY_COLUMN_LOCATION + " TEXT, " +
                        ENTRY_COLUMN_TRIP_ID + " INTEGER)"
        );

        // Create Trips Table
        db.execSQL(
                "CREATE TABLE " + TRIPS_TABLE_NAME +
                        "(" + TRIPS_COLUMN_ID + " INTEGER PRIMARY KEY, " +
                        TRIPS_COLUMN_NAME + " TEXT, " +
                        TRIPS_COLUMN_DEPARTURE + " TEXT, " +
                        TRIPS_COLUMN_DESTINATION + " TEXT)"
        );

        // Create Waypoint Table
        db.execSQL(
                "CREATE TABLE " + WAYPOINT_TABLE_NAME +
                        "(" + WAYPOINT_COLUMN_ID + " INTEGER PRIMARY KEY, " +
                        WAYPOINT_COLUMN_NAME + " TEXT, " +
                        WAYPOINT_COLUMN_LOCATION + " TEXT, " +
                        WAYPOINT_COLUMN_DESCRIPTION + " TEXT)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ENTRY_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TRIPS_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + WAYPOINT_TABLE_NAME);
        onCreate(db);
    }

    public boolean insertEntry(String name, String time, String date, String location, String trip_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(ENTRY_COLUMN_NAME, name);
        contentValues.put(ENTRY_COLUMN_TIME, time);
        contentValues.put(ENTRY_COLUMN_DATE, date);
        contentValues.put(ENTRY_COLUMN_LOCATION, location);
        contentValues.put(ENTRY_COLUMN_TRIP_ID, trip_id);

        db.insert(ENTRY_TABLE_NAME, null, contentValues);
        return true;
    }

    public boolean insertTrip(String name, String departure, String destination) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(TRIPS_COLUMN_NAME, name);
        contentValues.put(TRIPS_COLUMN_DEPARTURE, departure);
        contentValues.put(TRIPS_COLUMN_DESTINATION, destination);

        db.insert(TRIPS_TABLE_NAME, null, contentValues);
        return true;
    }

    public boolean insertWaypoint(String name, String location, String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(WAYPOINT_COLUMN_NAME, name);
        contentValues.put(WAYPOINT_COLUMN_LOCATION, location);
        contentValues.put(WAYPOINT_COLUMN_DESCRIPTION, description);

        db.insert(WAYPOINT_TABLE_NAME, null, contentValues);
        return true;
    }

    public int numberOfRows() {
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, ENTRY_TABLE_NAME);
        return numRows;
    }

    public int numberOfTripsRows() {
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, TRIPS_TABLE_NAME);
        return numRows;
    }

    public boolean updateEntry(Integer id, String name, String time, String date, String location, String trip_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ENTRY_COLUMN_NAME, name);
        contentValues.put(ENTRY_COLUMN_TIME, time);
        contentValues.put(ENTRY_COLUMN_DATE, date);
        contentValues.put(ENTRY_COLUMN_LOCATION, location);
        contentValues.put(ENTRY_COLUMN_TRIP_ID, trip_id);
        db.update(ENTRY_TABLE_NAME, contentValues, ENTRY_COLUMN_ID + " = ? ", new String[] { Integer.toString(id) } );
        return true;
    }

    public boolean updateTrip(Integer id, String name, String departure, String destination) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TRIPS_COLUMN_NAME, name);
        contentValues.put(TRIPS_COLUMN_DEPARTURE, departure);
        contentValues.put(TRIPS_COLUMN_DESTINATION, destination);
        db.update(TRIPS_TABLE_NAME, contentValues, TRIPS_COLUMN_ID + " = ? ", new String[] { Integer.toString(id) } );
        return true;
    }

    public boolean updateWaypoint(Integer id, String name, String location, String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(WAYPOINT_COLUMN_NAME, name);
        contentValues.put(WAYPOINT_COLUMN_LOCATION, location);
        contentValues.put(WAYPOINT_COLUMN_DESCRIPTION, description);
        db.update(WAYPOINT_TABLE_NAME, contentValues, WAYPOINT_COLUMN_ID + " = ? ", new String[] { Integer.toString(id) } );
        return true;
    }

    public Integer deleteEntry(Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(ENTRY_TABLE_NAME,
                ENTRY_COLUMN_ID + " = ? ",
                new String[] { Integer.toString(id) });
    }

    public Integer deleteTrip(Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TRIPS_TABLE_NAME,
                TRIPS_COLUMN_ID + " = ? ",
                new String[] { Integer.toString(id) });
    }

    public Integer deleteWaypoint(Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(WAYPOINT_TABLE_NAME,
                WAYPOINT_COLUMN_ID + " = ? ",
                new String[] { Integer.toString(id) });
    }

    public Integer deleteAllTripEntries(Integer id) {
      //  new String query = "Select * FROM " + ENTRY_TABLE_NAME + " WHERE " + ENTRY_COLUMN_TRIP_ID + " =  \"" + id + "\"";

        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(ENTRY_TABLE_NAME,
                 ENTRY_COLUMN_TRIP_ID + "=?", new String[] { Integer.toString(id) });
    }

    public Cursor getEntry(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("SELECT * FROM " + ENTRY_TABLE_NAME + " WHERE " +
                ENTRY_COLUMN_ID + "=?", new String[]{Integer.toString(id)});
        return res;
    }

    public Cursor getTrip(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("SELECT * FROM " + TRIPS_TABLE_NAME + " WHERE " +
                TRIPS_COLUMN_ID + "=?", new String[]{Integer.toString(id)});
        return res;
    }

    public Cursor getWaypoint(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("SELECT * FROM " + WAYPOINT_TABLE_NAME + " WHERE " +
                WAYPOINT_COLUMN_ID + "=?", new String[]{Integer.toString(id)});
        return res;
    }

    public Cursor getAllEntries() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "SELECT * FROM " + ENTRY_TABLE_NAME, null );
        return res;
    }

    public Cursor getAllTrips() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "SELECT * FROM " + TRIPS_TABLE_NAME, null );
        return res;
    }

    public Cursor getAllWaypoint() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "SELECT * FROM " + WAYPOINT_TABLE_NAME, null );
        return res;
    }

    public Cursor getTripEntry(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("SELECT * FROM " + ENTRY_TABLE_NAME + " WHERE " +
                ENTRY_COLUMN_TRIP_ID + "=?", new String[]{Integer.toString(id)});
        return res;
    }



}