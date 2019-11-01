package com.rasi.ryan.gpsmarker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.database.DatabaseUtils;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "GPS";

    //private static final int DB_VERSION = 1;
    private static final int DB_VERSION = 2;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE RECORD ("
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "NAME TEXT, "
                + "NOTES TEXT, "
                + "LATITUDE TEXT, "
                + "LONGITUDE TEXT, "
                + "ALTITUDE TEXT);"
        );


        insertRecord(db, "Home", "Location taken outside front door.", "52.345", "-2.345", "20.4");

        insertRecord(db, "Summit", "Climbed on 15/6/2017", "-1.9345", "-2.3", "10.1");

        insertRecord(db, "Car", "Park and ride", "53", "-2.034", "200.8");

        insertRecord(db, "Destination", "", "53", "-1.345", "109.2");

        insertRecord(db, "Cherry tree", "Japanese variety, name unknown", "51.8345", "-2.234", "123.0");

        insertRecord(db, "Junction", "Important landmark during trek on 2/1/2017", "50.934", "-1.6342", "4.0");

        insertRecord(db, "Keele University", "A lovely windy day", "53", "-2.3", "0");



    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        if((i == 1) && (i1 == 2)) {

            insertRecord(db, "Home","Good ol' newcastle", "45","67","0");

        }

        Log.i(">>> DatabaseHelper", "Database upgraded");
    }

    @Override
    public void onDowngrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        if((i == 2) && (i1 == 1)) {

            sqLiteDatabase.delete("RECORD", "NAME=?", new String[] {"Lucy"});

        }

        Log.i(">>> DatabaseHelper", "Database downgraded");
    }

    public static long insertRecord(SQLiteDatabase db, String name, String notes, String latitude, String longitude, String altitude) {

        ContentValues recordValues = new ContentValues();

        recordValues.put("NAME", name);
        recordValues.put("NOTES", notes);
        recordValues.put("LATITUDE", latitude);
        recordValues.put("LONGITUDE", longitude);
        recordValues.put("ALTITUDE", altitude);

        long newRecordID = db.insert("RECORD", null, recordValues);


        return newRecordID;
    }

    public static String getDatabaseContentsAsString(SQLiteDatabase db) {

        Cursor cursor = db.query("RECORD",
                new String[]{"_id", "NAME", "NOTES", "LATITUDE", "LONGITUDE", "ALTITUDE"},
                null, null, null, null, "_id ASC");

        String databaseAsString = System.getProperty("line.separator");

        if(cursor.getCount() != 0) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                for (int i=0; i < cursor.getColumnCount() - 1; i++) {
                    databaseAsString += cursor.getString(i) + "     ";
                }
                databaseAsString += System.getProperty("line.separator");
                cursor.moveToNext();
            }

            if(cursor != null) cursor.close();
        }

        return databaseAsString;
    }

    public static void updateRecord(SQLiteDatabase db, Long id, String name, String notes, String latitude, String longitude, String altitude) {

        ContentValues recordValues = new ContentValues();

        recordValues.put("NAME", name);
        recordValues.put("NOTES", notes);
        recordValues.put("LATITUDE", latitude);
        recordValues.put("LONGITUDE", longitude);
        recordValues.put("ALTITUDE", altitude);


        db.update("RECORD", recordValues, "_id = ?", new String[] {Long.toString(id)});
    }

    public static void deleteRecord(SQLiteDatabase db, Long id) {
        db.delete("RECORD", "_id=?", new String[] {Long.toString(id)});
    }

    public static void deleteAllRecords(SQLiteDatabase db) {
        db.delete("RECORD", null, null);
        db.delete("SQLITE_SEQUENCE","NAME = ?",new String[]{"RECORD"});  // To also reset the primary key. Comment out if not required.
    }


}
