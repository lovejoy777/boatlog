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
public class BoatLogDBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "SQLiteBoatLog.db";
    private static final int DATABASE_VERSION = 6;

    public static final String ENTRY_TABLE_NAME = "entries";
    public static final String ENTRY_COLUMN_ID = "_id";
    public static final String ENTRY_COLUMN_FAV = "fav";
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
    public static final String WAYPOINT_COLUMN_DESCRIPTION = "description";
    public static final String WAYPOINT_COLUMN_LATDEG = "latdeg";
    public static final String WAYPOINT_COLUMN_LATMIN = "latmin";
    public static final String WAYPOINT_COLUMN_LATSEC = "latsec";
    public static final String WAYPOINT_COLUMN_LATNS = "latns";
    public static final String WAYPOINT_COLUMN_LONGDEG = "longdeg";
    public static final String WAYPOINT_COLUMN_LONGMIN = "longmin";
    public static final String WAYPOINT_COLUMN_LONGSEC = "longsec";
    public static final String WAYPOINT_COLUMN_LONGEW = "longew";

    public static final String MANLOG_TABLE_NAME = "manlog";
    public static final String MANLOG_COLUMN_ID = "_id";
    public static final String MANLOG_COLUMN_NAME = "name";
    public static final String MANLOG_COLUMN_DESCRIPTION = "description";
    public static final String MANLOG_COLUMN_PARTS_ID = "parts_id";
    public static final String MANLOG_COLUMN_PROGRESS = "progress";

    public static final String PARTS_TABLE_NAME = "parts";
    public static final String PARTS_COLUMN_ID = "_id";
    public static final String PARTS_COLUMN_NAME = "name";
    public static final String PARTS_COLUMN_PART = "part";

    public static final String FAVENTRY_TABLE_NAME = "faventry";
    public static final String FAVENTRY_COLUMN_ID = "_id";
    public static final String FAVENTRY_COLUMN_NAME = "name";

    public BoatLogDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // Create Enteries Table
        db.execSQL(
                "CREATE TABLE " + ENTRY_TABLE_NAME +
                        "(" + ENTRY_COLUMN_ID + " INTEGER PRIMARY KEY, " +
                        ENTRY_COLUMN_FAV + " TEXT, " +
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
                        WAYPOINT_COLUMN_DESCRIPTION + " TEXT, " +
                        WAYPOINT_COLUMN_LATDEG + " TEXT, " +
                        WAYPOINT_COLUMN_LATMIN + " TEXT, " +
                        WAYPOINT_COLUMN_LATSEC + " TEXT, " +
                        WAYPOINT_COLUMN_LATNS + " TEXT, " +
                        WAYPOINT_COLUMN_LONGDEG + " TEXT, " +
                        WAYPOINT_COLUMN_LONGMIN + " TEXT, " +
                        WAYPOINT_COLUMN_LONGSEC + " TEXT, " +
                        WAYPOINT_COLUMN_LONGEW + " TEXT)"
        );

        db.execSQL(
                "CREATE TABLE " + MANLOG_TABLE_NAME +
                        "(" + MANLOG_COLUMN_ID + " INTEGER PRIMARY KEY, " +
                        MANLOG_COLUMN_NAME + " TEXT, " +
                        MANLOG_COLUMN_DESCRIPTION + " TEXT, " +
                        MANLOG_COLUMN_PARTS_ID + " TEXT, " +
                        MANLOG_COLUMN_PROGRESS + " TEXT)"
        );

        db.execSQL(
                "CREATE TABLE " + PARTS_TABLE_NAME +
                        "(" + PARTS_COLUMN_ID + " INTEGER PRIMARY KEY, " +
                        PARTS_COLUMN_NAME + " TEXT, " +
                        PARTS_COLUMN_PART + " TEXT)"
        );

        db.execSQL(
                "CREATE TABLE " + FAVENTRY_TABLE_NAME +
                        "(" + FAVENTRY_COLUMN_ID + " INTEGER PRIMARY KEY, " +
                        FAVENTRY_COLUMN_NAME + " TEXT)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ENTRY_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TRIPS_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + WAYPOINT_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MANLOG_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PARTS_TABLE_NAME);
        onCreate(db);
    }

    public boolean insertEntry(String fav, String name, String time, String date, String location, String trip_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(ENTRY_COLUMN_FAV, fav);
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

    public boolean insertWaypoint(
            String name,
            String description,
            String latdeg,
            String latmin,
            String latsec,
            String latns,
            String longdeg,
            String longmin,
            String longsec,
            String longew
    ) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(WAYPOINT_COLUMN_NAME, name);
        contentValues.put(WAYPOINT_COLUMN_DESCRIPTION, description);
        contentValues.put(WAYPOINT_COLUMN_LATDEG, latdeg);
        contentValues.put(WAYPOINT_COLUMN_LATMIN, latmin);
        contentValues.put(WAYPOINT_COLUMN_LATSEC, latsec);
        contentValues.put(WAYPOINT_COLUMN_LATNS, latns);
        contentValues.put(WAYPOINT_COLUMN_LONGDEG, longdeg);
        contentValues.put(WAYPOINT_COLUMN_LONGMIN, longmin);
        contentValues.put(WAYPOINT_COLUMN_LONGSEC, longsec);
        contentValues.put(WAYPOINT_COLUMN_LONGEW, longew);

        db.insert(WAYPOINT_TABLE_NAME, null, contentValues);
        return true;
    }

    public boolean insertManLog(String name, String description, String parts_id, String progress) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(MANLOG_COLUMN_NAME, name);
        contentValues.put(MANLOG_COLUMN_DESCRIPTION, description);
        contentValues.put(MANLOG_COLUMN_PARTS_ID, parts_id);
        contentValues.put(MANLOG_COLUMN_PROGRESS, progress);

        db.insert(MANLOG_TABLE_NAME, null, contentValues);
        return true;
    }

    public boolean insertParts(String name, String part) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(PARTS_COLUMN_NAME, name);
        contentValues.put(PARTS_COLUMN_PART, part);

        db.insert(PARTS_TABLE_NAME, null, contentValues);
        return true;
    }

    public boolean insertFavEntry(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(FAVENTRY_COLUMN_NAME, name);

        db.insert(FAVENTRY_TABLE_NAME, null, contentValues);
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

    public boolean updateEntry(Integer id, String fav, String name, String time, String date, String location, String trip_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ENTRY_COLUMN_FAV, fav);
        contentValues.put(ENTRY_COLUMN_NAME, name);
        contentValues.put(ENTRY_COLUMN_TIME, time);
        contentValues.put(ENTRY_COLUMN_DATE, date);
        contentValues.put(ENTRY_COLUMN_LOCATION, location);
        contentValues.put(ENTRY_COLUMN_TRIP_ID, trip_id);
        db.update(ENTRY_TABLE_NAME, contentValues, ENTRY_COLUMN_ID + " = ? ", new String[]{Integer.toString(id)});
        return true;
    }

    public boolean updateTrip(Integer id, String name, String departure, String destination) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TRIPS_COLUMN_NAME, name);
        contentValues.put(TRIPS_COLUMN_DEPARTURE, departure);
        contentValues.put(TRIPS_COLUMN_DESTINATION, destination);
        db.update(TRIPS_TABLE_NAME, contentValues, TRIPS_COLUMN_ID + " = ? ", new String[]{Integer.toString(id)});
        return true;
    }

    public boolean updateWaypoint(
            Integer id,
            String name,
            String description,
            String latdeg,
            String latmin,
            String latsec,
            String latns,
            String longdeg,
            String longmin,
            String longsec,
            String longew
    ) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(WAYPOINT_COLUMN_NAME, name);
        contentValues.put(WAYPOINT_COLUMN_DESCRIPTION, description);
        contentValues.put(WAYPOINT_COLUMN_LATDEG, latdeg);
        contentValues.put(WAYPOINT_COLUMN_LATMIN, latmin);
        contentValues.put(WAYPOINT_COLUMN_LATSEC, latsec);
        contentValues.put(WAYPOINT_COLUMN_LATNS, latns);
        contentValues.put(WAYPOINT_COLUMN_LONGDEG, longdeg);
        contentValues.put(WAYPOINT_COLUMN_LONGMIN, longmin);
        contentValues.put(WAYPOINT_COLUMN_LONGSEC, longsec);
        contentValues.put(WAYPOINT_COLUMN_LONGEW, longew);

        db.update(WAYPOINT_TABLE_NAME, contentValues, WAYPOINT_COLUMN_ID + " = ? ", new String[]{Integer.toString(id)});
        return true;
    }


    public boolean updateManLog(Integer id, String name, String description, String parts_id, String progress) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MANLOG_COLUMN_NAME, name);
        contentValues.put(MANLOG_COLUMN_DESCRIPTION, description);
        contentValues.put(MANLOG_COLUMN_PARTS_ID, parts_id);
        contentValues.put(MANLOG_COLUMN_PROGRESS, progress);
        db.update(MANLOG_TABLE_NAME, contentValues, MANLOG_COLUMN_ID + " = ? ", new String[]{Integer.toString(id)});
        return true;
    }


    public boolean updateParts(Integer id, String name, String part) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(PARTS_COLUMN_NAME, name);
        contentValues.put(PARTS_COLUMN_PART, part);
        db.update(PARTS_TABLE_NAME, contentValues, PARTS_COLUMN_ID + " = ? ", new String[]{Integer.toString(id)});
        return true;
    }

    public boolean updateFavEntry(Integer id, String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(FAVENTRY_COLUMN_NAME, name);
        db.update(FAVENTRY_TABLE_NAME, contentValues, FAVENTRY_COLUMN_ID + " = ? ", new String[]{Integer.toString(id)});
        return true;
    }

    public Integer deleteEntry(Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(ENTRY_TABLE_NAME,
                ENTRY_COLUMN_ID + " = ? ",
                new String[]{Integer.toString(id)});
    }

    public Integer deleteTrip(Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TRIPS_TABLE_NAME,
                TRIPS_COLUMN_ID + " = ? ",
                new String[]{Integer.toString(id)});
    }

    public Integer deleteWaypoint(Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(WAYPOINT_TABLE_NAME,
                WAYPOINT_COLUMN_ID + " = ? ",
                new String[]{Integer.toString(id)});
    }

    public Integer deleteManLog(Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(MANLOG_TABLE_NAME,
                MANLOG_COLUMN_ID + " = ? ",
                new String[]{Integer.toString(id)});
    }

    public Integer deleteFavEntry(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(FAVENTRY_TABLE_NAME,
                FAVENTRY_COLUMN_NAME + " = ? ",
                new String[]{id});
    }

    public Long getIdFromFavName(String myName) {
        String query = "SELECT rowid" +
                " FROM " + FAVENTRY_TABLE_NAME +
                " WHERE " + FAVENTRY_COLUMN_NAME + " = ?;";
        SQLiteDatabase db = this.getReadableDatabase();
        return DatabaseUtils.longForQuery(db, query, new String[]{myName});
    }

    public Integer deleteAllTripEntries(Integer id) {
        //  new String query = "Select * FROM " + ENTRY_TABLE_NAME + " WHERE " + ENTRY_COLUMN_TRIP_ID + " =  \"" + id + "\"";

        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(ENTRY_TABLE_NAME,
                ENTRY_COLUMN_TRIP_ID + "=?", new String[]{Integer.toString(id)});
    }


    public Cursor getEntry(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + ENTRY_TABLE_NAME + " WHERE " +
                ENTRY_COLUMN_ID + "=?", new String[]{Integer.toString(id)});
        return res;
    }

    public Cursor getFavEntries(String fav) {

        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {ENTRY_COLUMN_NAME};
        Cursor res = db.query(ENTRY_TABLE_NAME, columns, ENTRY_COLUMN_FAV + " = '" + fav + "'", null, null, null, null);

        return res;
    }

    public Cursor getTrip(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + TRIPS_TABLE_NAME + " WHERE " +
                TRIPS_COLUMN_ID + "=?", new String[]{Integer.toString(id)});
        return res;
    }

    public Cursor getFavID(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + ENTRY_TABLE_NAME + " WHERE " +
                FAVENTRY_COLUMN_NAME + "=?", new String[]{id});
        return res;
    }

    public Cursor getWaypoint(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + WAYPOINT_TABLE_NAME + " WHERE " +
                WAYPOINT_COLUMN_ID + "=?", new String[]{Integer.toString(id)});
        return res;
    }

    public Cursor getManLog(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + MANLOG_TABLE_NAME + " WHERE " +
                MANLOG_COLUMN_ID + "=?", new String[]{Integer.toString(id)});
        return res;
    }

    public Cursor getFavEntry(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + FAVENTRY_TABLE_NAME + " WHERE " +
                FAVENTRY_COLUMN_ID + "=?", new String[]{Integer.toString(id)});
        return res;
    }

    public Cursor getAllEntries() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + ENTRY_TABLE_NAME, null);
        return res;
    }

    public Cursor getAllTrips() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + TRIPS_TABLE_NAME, null);
        return res;
    }

    public Cursor getAllWaypoint() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + WAYPOINT_TABLE_NAME, null);
        return res;
    }

    public Cursor getAllManLog() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + MANLOG_TABLE_NAME, null);
        return res;
    }

    public Cursor getAllFavEntry() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + FAVENTRY_TABLE_NAME, null);
        return res;
    }

    public Cursor getTripEntry(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + ENTRY_TABLE_NAME + " WHERE " +
                ENTRY_COLUMN_TRIP_ID + "=?", new String[]{Integer.toString(id)});
        return res;
    }


}