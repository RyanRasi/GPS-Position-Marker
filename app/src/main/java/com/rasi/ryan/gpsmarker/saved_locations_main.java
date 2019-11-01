package com.rasi.ryan.gpsmarker;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
import java.net.URL;

import static com.rasi.ryan.gpsmarker.DatabaseHelper.deleteAllRecords;
import static com.rasi.ryan.gpsmarker.DatabaseHelper.deleteRecord;

public class saved_locations_main extends AppCompatActivity {

    private SQLiteDatabase db;
    private Cursor cursor;
    private SimpleCursorAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);

        SQLiteOpenHelper databaseHelper = new DatabaseHelper(getApplicationContext());
        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        ListView listView = (ListView) findViewById(R.id.listView_characters);

        cursor = db.query("RECORD", new String[]{"_id", "NAME", "NOTES", "LONGITUDE", "LATITUDE", "ALTITUDE"}, null, null, null, null, null);

        listAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, cursor, new String[] {"NAME"}, new int[] {android.R.id.text1}, 0);

        listView.setAdapter(listAdapter);

        Log.i("Activity_main", "Cursor size=" + Integer.toString(cursor.getCount()));
        Log.i("Activity_main", "Database records:" + DatabaseHelper.getDatabaseContentsAsString(db));

        //Create the listener
        AdapterView.OnItemClickListener itemClickListener =
                new AdapterView.OnItemClickListener(){
                    @Override
                    public void onItemClick(AdapterView<?> listView,
                                            View itemView,
                                            int position,
                                            long id) {
                        //Pass the option the user clicks on to character activity.
                        Intent intent = new Intent(saved_locations_main.this, edit_locations_main.class);
                        intent.putExtra(edit_locations_main.EXTRA_CHARACTER_ID, (int) id);
                        startActivity(intent);
                    }
                };

        //Assign the listener to the list view
        listView.setOnItemClickListener(itemClickListener);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(cursor != null) cursor.close();
        if(db != null) db.close();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_saved,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        SQLiteOpenHelper databaseHelper = new DatabaseHelper(getApplicationContext());
        final SQLiteDatabase db = databaseHelper.getReadableDatabase();

        switch (item.getItemId()) {
            case R.id.mnuEmail:
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"recipient@example.com"});
                i.putExtra(Intent.EXTRA_SUBJECT, "Database records of GPSMarker app");
                i.putExtra(Intent.EXTRA_TEXT   , DatabaseHelper.getDatabaseContentsAsString(db));
                try {
                    startActivity(Intent.createChooser(i, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(saved_locations_main.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.home:
                finish();
                return true;
            case R.id.mnuDelete:
                // Add the buttons

                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        deleteAllRecords(db);
                        startActivity(new Intent(saved_locations_main.this, saved_locations_main.class));
                        Toast.makeText(getApplicationContext(), "All records deleted!", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(getApplicationContext(), "Cancelled", Toast.LENGTH_SHORT).show();
                    }
                });

                // Set other dialog properties...
                builder.setTitle("Warning");
                builder.setMessage("This will remove all database records!");

                // Create the AlertDialog
                AlertDialog dialog = builder.create();

                dialog.show();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
