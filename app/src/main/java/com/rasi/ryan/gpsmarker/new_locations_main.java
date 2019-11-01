package com.rasi.ryan.gpsmarker;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;
import android.database.DatabaseUtils;

import static com.rasi.ryan.gpsmarker.DatabaseHelper.insertRecord;

public class new_locations_main extends AppCompatActivity {

    private SQLiteDatabase db;

    LocationManager locationManager;
    LocationListener locationListener = new MyLocationListener();  // The listener that responds to location updates.

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_locations);

        SQLiteOpenHelper databaseHelper = new DatabaseHelper(getApplicationContext());
        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);  // Create the location manager.

        try {
            //locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, locationListener);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, locationListener);  // Requesting a location broadcast every 1000ms and a minimum distance between locations of 1m.
        } catch (SecurityException e) {  // Checked exception (because the activity may not have been given persmission to use the GPS sensor).
            System.out.println(e.toString());
        }
    }


    // Inner class
    public class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {

            // The code in this method will be run every time there is a location broadcast by the location manager.

            final TextView Lattitude_distance = (TextView) findViewById(R.id.Lattitude_distance);
            final TextView Longitude_distance = (TextView) findViewById(R.id.Longitude_distance);
            final TextView Altitude_distance = (TextView) findViewById(R.id.Altitude_distance);

            String lattitudeText = location.getLatitude() + "\n";
            Lattitude_distance.setText(lattitudeText);

            String longitudeText = location.getLongitude() + "\n";
            Longitude_distance.setText(longitudeText);

            String altitudeText = location.getAltitude() + "\n";
            Altitude_distance.setText(altitudeText);
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {}

        public void onProviderEnabled(String provider) {}

        public void onProviderDisabled(String provider) {}

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationManager.removeUpdates(locationListener);  // Must stay here otherwise the GPS receiver will not stop working and will quickly consume the battery of the device.
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_location,menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        SQLiteOpenHelper databaseHelper = new DatabaseHelper(getApplicationContext());
        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        EditText editText_Name = findViewById(R.id.Name);
        EditText editText_Notes = findViewById(R.id.Notes);
        TextView textView_lat= findViewById(R.id.Lattitude_distance);
        TextView textView_lon = findViewById(R.id.Longitude_distance);
        TextView textView_alt = findViewById(R.id.Altitude_distance);


        switch (item.getItemId()) {
            case R.id.home:
                finish();
                return true;
            case R.id.mnuSave:

                if( TextUtils.isEmpty(editText_Name.getText())){
                    editText_Name.setError( "Name of location is required!" );
                }else{

                insertRecord(db, editText_Name.getText().toString(), editText_Notes.getText().toString(), textView_lat.getText().toString(),
                        textView_lon.getText().toString(),
                        textView_alt.getText().toString());
                startActivity(new Intent(this, MainActivity.class));
                long entries = DatabaseUtils.queryNumEntries(db, "RECORD");
                Toast.makeText(getApplicationContext(), "Saved Location " +entries, Toast.LENGTH_SHORT).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}

