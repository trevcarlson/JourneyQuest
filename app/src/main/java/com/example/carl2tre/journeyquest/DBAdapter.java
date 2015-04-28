package com.example.carl2tre.journeyquest;

/**
 * Created by mattdavey on 4/8/15.
 */
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class DBAdapter {

    // Trip Table
    static final String DATABASE_TRIP_TABLE = "trips";
    static final String KEY_TRIP_ID = "tripID";  //Give constant names to the rows
    static final String KEY_TRIP_NAME = "tripName";

    // Event Table
    static final String DATABASE_EVENT_TABLE = "events";
    static final String KEY_EVENT_ID = "eventID";
    static final String KEY_EVENT_NAME = "eventName";
    static final String KEY_EVENT_TRANSPORTATION_TYPE = "transportationType";
    static final String KEY_EVENT_DATE = "eventDate";
    static final String KEY_EVENT_NOTES = "eventNotes";

    static final String TAG = "DBAdapter";
    static final String DATABASE_NAME = "MyDB";



    static final int DATABASE_VERSION = 8;

    static final String DATABASE_CREATE =  //SQL commands are a pain, so make a string constant to do it
            "create table trips (tripID integer primary key autoincrement, "
            + "tripName text not null);";

    static final String DATABASE_EVENTS_CREATE =
            "create table events (eventID integer primary key autoincrement, "
            + "tripID long not null, eventName text not null, transportationType text not null, "
            + "eventDate text not null, eventNotes text not null);";

    final Context context;

    DatabaseHelper DBHelper;
    SQLiteDatabase db;

    public DBAdapter(Context ctx) //SQLiteOpenHelper requires a Context to create it, so we need one, too
    {
        this.context = ctx;
        DBHelper = new DatabaseHelper(context);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper
    {
        DatabaseHelper(Context context)
        {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db)
        {
            try {
                db.execSQL(DATABASE_CREATE);

            } catch (SQLException e) {
                e.printStackTrace();
            }

            try {
                db.execSQL(DATABASE_EVENTS_CREATE);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS contacts");
            onCreate(db);
        }
    }

    //---opens the database---
    public DBAdapter open() throws SQLException
    {
        db = DBHelper.getWritableDatabase();
        return this;
    }

    //---closes the database---
    public void close()
    {
        DBHelper.close();
    }

    //---insert a trip into the database---
    public long insertTrip(String name)
    {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_TRIP_NAME, name);
        return db.insert(DATABASE_TRIP_TABLE, null, initialValues);
    }

    //---insert an event into the database---
    public long insertEvent(long tripID, String eventName, String eventTransportationType, String eventDate, String eventNotes)
    {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_TRIP_ID, tripID);
        initialValues.put(KEY_EVENT_NAME, eventName);
        initialValues.put(KEY_EVENT_TRANSPORTATION_TYPE, eventTransportationType);
        initialValues.put(KEY_EVENT_DATE, eventDate);
        initialValues.put(KEY_EVENT_NOTES, eventNotes);
        return db.insert(DATABASE_EVENT_TABLE, null, initialValues);
    }

    //---deletes a particular trip---
    public boolean deleteTrip(long rowId)
    {
        return db.delete(DATABASE_TRIP_TABLE, KEY_TRIP_ID + "=" + rowId, null) > 0;
    }

    //---retrieves all the trips---
    public Cursor getAllTrips()
    {
        return db.query(DATABASE_TRIP_TABLE, new String[] {KEY_TRIP_ID, KEY_TRIP_NAME}, null, null, null, null, null);
    }

    public Cursor getAllEvents()
    {
        return db.query(DATABASE_EVENT_TABLE, new String[] {KEY_TRIP_ID, KEY_EVENT_NAME,
                KEY_EVENT_TRANSPORTATION_TYPE, KEY_EVENT_DATE, KEY_EVENT_NOTES} ,
                null, null, null, null, null);
    }

    public Cursor getAllEventsSorted(long tripID)
    {
        Log.d("DBAdapter", "getAllEvents Function");
        return db.query(DATABASE_EVENT_TABLE, new String[] {KEY_EVENT_ID, KEY_TRIP_ID, KEY_EVENT_NAME, KEY_EVENT_TRANSPORTATION_TYPE,
        KEY_EVENT_DATE, KEY_EVENT_NOTES}, KEY_TRIP_ID + "=" + tripID, null, null, null, null);
    }

    //---retrieves a particular trip---
    public Cursor getTrip(long rowId) throws SQLException
    {
        Cursor mCursor =
                db.query(true, DATABASE_TRIP_TABLE, new String[] {KEY_TRIP_ID,
                                KEY_TRIP_NAME}, KEY_TRIP_ID + "=" + rowId, null,
                        null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    public Cursor getEvent(long rowId) throws SQLiteException
    {
        Cursor mCursor =
                db.query(true, DATABASE_EVENT_TABLE, new String[] {KEY_EVENT_ID,
                        KEY_EVENT_NAME, KEY_EVENT_TRANSPORTATION_TYPE, KEY_EVENT_DATE, KEY_EVENT_NOTES}, KEY_EVENT_ID + "=" + rowId, null,
                        null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    //---updates a trip---
    public boolean updateTrip(long rowId, String name)
    {
        ContentValues args = new ContentValues();
        args.put(KEY_TRIP_NAME, name);
        return db.update(DATABASE_TRIP_TABLE, args, KEY_TRIP_ID + "=" + rowId, null) > 0;
    }


}