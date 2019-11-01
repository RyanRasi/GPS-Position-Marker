package com.rasi.ryan.gpsmarker;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.text.Editable;

import static com.rasi.ryan.gpsmarker.DatabaseHelper.deleteRecord;
import static com.rasi.ryan.gpsmarker.DatabaseHelper.insertRecord;
import static com.rasi.ryan.gpsmarker.DatabaseHelper.updateRecord;

public class edit_locations_main extends AppCompatActivity {

    private SQLiteDatabase db;

    public static final String EXTRA_CHARACTER_ID = "character_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_character);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Get the character id from the intent
        int id = (Integer)getIntent().getExtras().get(EXTRA_CHARACTER_ID);


        SQLiteOpenHelper databaseHelper = new DatabaseHelper(getApplicationContext());
        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        Cursor cursor = db.query("RECORD",
                new String[]{"_id", "NAME", "NOTES", "LATITUDE", "LONGITUDE", "ALTITUDE"},
                "_id = ?", new String[] {Integer.toString(id)}, null, null, null);

        String name = null;
        String notes = null;
        String latitude = null;
        String longitude = null;
        String altitude = null;

        if(cursor.getCount() != 0) {
            cursor.moveToFirst();
            name = cursor.getString(1);
            notes = cursor.getString(2);
            latitude = cursor.getString(3);
            longitude = cursor.getString(4);
            altitude = cursor.getString(5);
        } else {
            Log.e("getRecord", "Error retrieving record " + id + " from database.");
        }

        if(cursor != null) cursor.close();

        // Initialise the layout components

        TextView textView_name = (TextView)findViewById(R.id.textView_name2);
        textView_name.setText(name);

        TextView textView_notes = (TextView)findViewById(R.id.textView_notes2);
        textView_notes.setText(notes);

        TextView textView_longitude = (TextView)findViewById(R.id.textView_longitude2);
        textView_longitude.setText(longitude);

        TextView textView_latitude = (TextView)findViewById(R.id.textView_latitude2);
        textView_latitude.setText(latitude);

        TextView textView_altitude = (TextView)findViewById(R.id.textView_altitude2);
        textView_altitude.setText(altitude);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        SQLiteOpenHelper databaseHelper = new DatabaseHelper(getApplicationContext());
        final SQLiteDatabase db = databaseHelper.getReadableDatabase();

        EditText textView_name = findViewById(R.id.textView_name2);
        EditText textView_notes = findViewById(R.id.textView_notes2);
        TextView textView_lat= findViewById(R.id.textView_longitude2);
        TextView textView_lon = findViewById(R.id.textView_latitude2);
        TextView textView_alt = findViewById(R.id.textView_altitude2);

        final long drecord = (Integer)getIntent().getExtras().get(EXTRA_CHARACTER_ID);

        switch (item.getItemId()) {

            case R.id.mnuSave:
                updateRecord(db, drecord, textView_name.getText().toString(), textView_notes.getText().toString(), textView_lat.getText().toString(),
                        textView_lon.getText().toString(),
                        textView_alt.getText().toString());
                startActivity(new Intent(this, saved_locations_main.class));
                Toast.makeText(getApplicationContext(), "Saved changes to record " + drecord, Toast.LENGTH_SHORT).show();
                return true;
            case R.id.home:
                finish();
                return true;
            case R.id.mnuDelete:
                ////////////////////////////////////////

                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                // Add the buttons
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        deleteRecord(db, drecord);
                        startActivity(new Intent(edit_locations_main.this, saved_locations_main.class));
                        Toast.makeText(getApplicationContext(), "Record " + drecord + " deleted!", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(getApplicationContext(), "Cancelled", Toast.LENGTH_SHORT).show();
                    }
                });

                // Set other dialog properties...
                builder.setTitle("Warning");
                builder.setMessage("Are you sure you want to delete record " +drecord +"?");

                // Create the AlertDialog
                AlertDialog dialog = builder.create();

                dialog.show();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
