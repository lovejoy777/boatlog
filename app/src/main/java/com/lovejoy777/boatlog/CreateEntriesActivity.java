package com.lovejoy777.boatlog;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by steve on 07/09/17.
 */

public class CreateEntriesActivity  extends AppCompatActivity implements LocationListener {


    private LocationManager locationManager;
    private String provider;
    private ExampleDBHelper dbHelper;

    private FloatingActionButton fabSave; //fabMainDeleteEditSave
    FrameLayout fabFrame;

    ScrollView scrollView1;
    RelativeLayout MRL1;
    LinearLayout MLL1;
    Toolbar toolBar;

    TextView titleTextView, textViewName, textViewTime, textViewDate, textViewLocation;
    EditText nameEditText, timeEditText, dateEditText, locationEditText;
    TextView trip_idText;

    public final static String KEY_EXTRA_ENTRIES_ID = "KEY_EXTRA_ENTRIES_ID";
    public final static String KEY_EXTRA_TRIPS_ID = "KEY_EXTRA_TRIPS_ID";
    public final static String KEY_EXTRA_TRIPS_NAME = "KEY_EXTRA_TRIPS_NAME";

    int entryID;
    int tripID;
    String tripName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_entries);

        // Toast.makeText(getApplicationContext(), "Trip used " + tripID, Toast.LENGTH_SHORT).show();
        entryID = getIntent().getIntExtra(MainActivityEntries.KEY_EXTRA_ENTRIES_ID, 0);
        tripID = getIntent().getIntExtra(MainActivityEntries.KEY_EXTRA_TRIPS_ID, 0);
        tripName = getIntent().getStringExtra(MainActivityEntries.KEY_EXTRA_TRIPS_NAME);

        scrollView1 = (ScrollView) findViewById(R.id.scrollView1);
        MRL1 = (RelativeLayout) findViewById(R.id.MRL1);
        fabFrame = (FrameLayout) findViewById(R.id.fabFrame);
        toolBar = (Toolbar) findViewById(R.id.toolbar);
        titleTextView = (TextView) findViewById(R.id.titleTextView);

        textViewName = (TextView) findViewById(R.id.textViewName);
        textViewTime = (TextView) findViewById(R.id.textViewTime);
        textViewDate = (TextView) findViewById(R.id.textViewDate);
        textViewLocation = (TextView) findViewById(R.id.textViewLocation);

        nameEditText = (EditText) findViewById(R.id.editTextName);
        timeEditText = (EditText) findViewById(R.id.editTextTime);
        dateEditText = (EditText) findViewById(R.id.editTextDate);
        locationEditText = (EditText) findViewById(R.id.editTextLocation);

        fabSave = (FloatingActionButton) this.findViewById(R.id.fabSave);

        trip_idText = (TextView) findViewById(R.id.TextViewTrip_ID);

        titleTextView.setText("Create New Entry");

        SharedPreferences myPrefs = this.getSharedPreferences("myPrefs", MODE_PRIVATE);
        Boolean NightModeOn = myPrefs.getBoolean("switch1", false);

        if (NightModeOn) {
            NightMode();
        }

        // Get Time and Date
        Calendar c = Calendar.getInstance();
        System.out.println("Current time => " + c.getTime());
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        String formattedDate = df.format(c.getTime());
        SimpleDateFormat dt = new SimpleDateFormat("HH:mm");
        String formattedTime = dt.format(c.getTime());

        // Get the location manager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // Define the criteria how to select the location provider -> use default
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = locationManager.getLastKnownLocation(provider);
        // Initialize the location fields
        if (location != null) {
            System.out.println("Provider " + provider + " has been selected.");
            onLocationChanged(location);
        } else {
            Toast.makeText(getApplicationContext(), "Lat Long unavailable ", Toast.LENGTH_SHORT).show();
        }

        String formattedLocation = null;
        if (location != null) {
            formattedLocation = FormattedLocation(location.getLatitude(), location.getLongitude());
        }

        // pre fill text fields

        trip_idText.setText("" + tripID);
        timeEditText.setText("" + formattedTime);
        dateEditText.setText("" + formattedDate);
        locationEditText.setText("" + formattedLocation);

        dbHelper = new ExampleDBHelper(this);

        fabSave.setImageResource(R.drawable.ic_save_white);
        fabSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                persistEntry();
            }
        });
    }


    public void persistEntry() {
        if (entryID > 0) {
            if (dbHelper.updateEntry(entryID, nameEditText.getText().toString(),
                    timeEditText.getText().toString(),
                    dateEditText.getText().toString(),
                    locationEditText.getText().toString(),
                    trip_idText.getText().toString())) {
                Toast.makeText(getApplicationContext(), "Entry Edited Successful", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), MainActivityEntries.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra(KEY_EXTRA_TRIPS_NAME, tripName);
                intent.putExtra(KEY_EXTRA_TRIPS_ID, tripID);
                startActivity(intent);
            } else {
                Toast.makeText(getApplicationContext(), "Entry Edit Failed", Toast.LENGTH_SHORT).show();
            }
        } else {
            if (dbHelper.insertEntry(nameEditText.getText().toString(),
                    timeEditText.getText().toString(),
                    dateEditText.getText().toString(),
                    locationEditText.getText().toString(),
                    trip_idText.getText().toString())) {
                Toast.makeText(getApplicationContext(), "Entry Saved", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Could not Save Entry", Toast.LENGTH_SHORT).show();
            }
            Intent intent = new Intent(getApplicationContext(), MainActivityEntries.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra(KEY_EXTRA_TRIPS_NAME, tripName);
            intent.putExtra(KEY_EXTRA_TRIPS_ID, tripID);
            startActivity(intent);
        }
    }

    public static String FormattedLocation(double latitude, double longitude) {
        try {
            int latSeconds = (int) Math.round(latitude * 3600);
            int latDegrees = latSeconds / 3600;
            latSeconds = Math.abs(latSeconds % 3600);
            int latMinutes = latSeconds / 60;
            latSeconds %= 60;

            int longSeconds = (int) Math.round(longitude * 3600);
            int longDegrees = longSeconds / 3600;
            longSeconds = Math.abs(longSeconds % 3600);
            int longMinutes = longSeconds / 60;
            longSeconds %= 60;
            String latDegree = latDegrees >= 0 ? "N" : "S";
            String lonDegrees = longDegrees >= 0 ? "W" : "E";

            return Math.abs(latDegrees) + "°" + latMinutes + "'" + latSeconds
                    + "\"" + latDegree + " " + Math.abs(longDegrees) + "°" + longMinutes
                    + "'" + longSeconds + "\"" + lonDegrees;
        } catch (Exception e) {

            return "" + String.format("%8.5f", latitude) + "  "
                    + String.format("%8.5f", longitude);
        }
    }

    /* Request updates at startup */
    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(provider, 400, 1, this);
    }

    /* Remove the locationlistener updates when Activity is paused */
    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        int lat = (int) (location.getLatitude());
        int lng = (int) (location.getLongitude());
        String strLongitude = location.convert(location.getLongitude(), location.FORMAT_SECONDS);
        String strLatitude = location.convert(location.getLatitude(), location.FORMAT_SECONDS);
        // Toast.makeText(this, "" + strLatitude + strLongitude, Toast.LENGTH_LONG).show();

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(this, "Enabled new provider " + provider,
                Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(this, "Disabled provider " + provider,
                Toast.LENGTH_SHORT).show();
    }

    private void NightMode() {

        scrollView1.setBackgroundColor(Color.BLACK);
        MRL1.setBackgroundColor(Color.BLACK);
        fabFrame.setBackgroundColor(Color.BLACK);
        toolBar.setBackgroundColor(Color.BLACK);
        titleTextView.setTextColor(Color.RED);

        textViewName.setTextColor(Color.RED);
        textViewTime.setTextColor(Color.RED);
        textViewDate.setTextColor(Color.RED);
        textViewLocation.setTextColor(Color.RED);

        nameEditText.setTextColor(Color.RED);
        timeEditText.setTextColor(Color.RED);
        dateEditText.setTextColor(Color.RED);
        locationEditText.setTextColor(Color.RED);
    }
}