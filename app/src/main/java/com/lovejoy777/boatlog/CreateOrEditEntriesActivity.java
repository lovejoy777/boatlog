package com.lovejoy777.boatlog;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by lovejoy777 on 03/10/15.
 */
public class CreateOrEditEntriesActivity extends AppCompatActivity implements View.OnClickListener, LocationListener {


    private LocationManager locationManager;
    private String provider;
    private ExampleDBHelper dbHelper;

    ScrollView scrollView1;
    RelativeLayout MRL1;
    Toolbar toolBar;

    TextView titleTextView, textViewName, textViewTime, textViewDate, textViewLocation;
    EditText nameEditText, timeEditText, dateEditText, locationEditText;
    TextView trip_idText;

    LinearLayout buttonLayout;
    Button saveButton, editButton, deleteButton;

    public final static String KEY_EXTRA_ENTRIES_ID = "KEY_EXTRA_ENTRIES_ID";
    public final static String KEY_EXTRA_TRIPS_ID = "KEY_EXTRA_TRIPS_ID";
    public final static String KEY_EXTRA_TRIPS_NAME = "KEY_EXTRA_TRIPS_NAME";

    int entryID;
    int tripID;
    String tripName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_entries);

        // Toast.makeText(getApplicationContext(), "Trip used " + tripID, Toast.LENGTH_SHORT).show();
        entryID = getIntent().getIntExtra(MainActivityEntries.KEY_EXTRA_ENTRIES_ID, 0);
        tripID = getIntent().getIntExtra(MainActivityEntries.KEY_EXTRA_TRIPS_ID, 0);
        tripName = getIntent().getStringExtra(MainActivityEntries.KEY_EXTRA_TRIPS_NAME);

        scrollView1 = (ScrollView) findViewById(R.id.scrollView1);
        MRL1 = (RelativeLayout) findViewById(R.id.MRL1);
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

        buttonLayout = (LinearLayout) findViewById(R.id.buttonLayout);
        saveButton = (Button) findViewById(R.id.saveButton);
        editButton = (Button) findViewById(R.id.editButton);
        deleteButton = (Button) findViewById(R.id.deleteButton);

        trip_idText = (TextView) findViewById(R.id.TextViewTrip_ID);

        titleTextView.setText("Create");

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
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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

        saveButton = (Button) findViewById(R.id.saveButton);
        saveButton.setOnClickListener(this);
        buttonLayout = (LinearLayout) findViewById(R.id.buttonLayout);
        editButton = (Button) findViewById(R.id.editButton);
        editButton.setOnClickListener(this);
        deleteButton = (Button) findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(this);

        dbHelper = new ExampleDBHelper(this);

        if (entryID > 0) {
            saveButton.setVisibility(View.GONE);
            buttonLayout.setVisibility(View.VISIBLE);

            titleTextView = (TextView) findViewById(R.id.titleTextView);
            titleTextView.setText("Edit");

            Cursor rs = dbHelper.getEntry(entryID);
            rs.moveToFirst();
            String entryName = rs.getString(rs.getColumnIndex(ExampleDBHelper.ENTRY_COLUMN_NAME));
            String entryTime = rs.getString(rs.getColumnIndex(ExampleDBHelper.ENTRY_COLUMN_TIME));
            String entryDate = rs.getString(rs.getColumnIndex(ExampleDBHelper.ENTRY_COLUMN_DATE));
            String entryLocation = rs.getString(rs.getColumnIndex(ExampleDBHelper.ENTRY_COLUMN_LOCATION));
            String entryTrip_ID = rs.getString(rs.getColumnIndex(ExampleDBHelper.ENTRY_COLUMN_TRIP_ID));
            if (!rs.isClosed()) {
                rs.close();
            }

            nameEditText.setText(entryName);
            nameEditText.setFocusable(false);
            nameEditText.setClickable(false);

            timeEditText.setText((CharSequence) entryTime);
            timeEditText.setFocusable(false);
            timeEditText.setClickable(false);

            dateEditText.setText((CharSequence) entryDate);
            dateEditText.setFocusable(false);
            dateEditText.setClickable(false);

            locationEditText.setText((CharSequence) (entryLocation + ""));
            locationEditText.setFocusable(false);
            locationEditText.setClickable(false);

            trip_idText.setText((CharSequence) entryTrip_ID);
            trip_idText.setFocusable(false);
            trip_idText.setClickable(false);

        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.saveButton:
                persistEntry();
                return;
            case R.id.editButton:
                saveButton.setVisibility(View.VISIBLE);
                buttonLayout.setVisibility(View.GONE);
                nameEditText.setEnabled(true);
                nameEditText.setFocusableInTouchMode(true);
                nameEditText.setClickable(true);

                timeEditText.setEnabled(true);
                timeEditText.setFocusableInTouchMode(true);
                timeEditText.setClickable(true);

                dateEditText.setEnabled(true);
                dateEditText.setFocusableInTouchMode(true);
                dateEditText.setClickable(true);

                locationEditText.setEnabled(true);
                locationEditText.setFocusableInTouchMode(true);
                locationEditText.setClickable(true);

                trip_idText.setEnabled(true);
                trip_idText.setFocusableInTouchMode(true);
                trip_idText.setClickable(true);
                return;
            case R.id.deleteButton:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.deleteEntry)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dbHelper.deleteEntry(entryID);
                                Toast.makeText(getApplicationContext(), "Deleted Successfully", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), MainActivityEntries.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.putExtra(KEY_EXTRA_TRIPS_ID, tripID);
                                intent.putExtra(KEY_EXTRA_TRIPS_NAME, tripName);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                            }
                        });
                AlertDialog d = builder.create();
                d.setTitle("Delete Entry?");
                d.show();
                return;
        }
    }

    public void persistEntry() {
        if (entryID > 0) {
            if (dbHelper.updateEntry(entryID, nameEditText.getText().toString(),
                    timeEditText.getText().toString(),
                    dateEditText.getText().toString(),
                    locationEditText.getText().toString(),
                    trip_idText.getText().toString())) {
                Toast.makeText(getApplicationContext(), "Entry Update Successful", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), MainActivityEntries.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra(KEY_EXTRA_TRIPS_NAME, tripName);
                intent.putExtra(KEY_EXTRA_TRIPS_ID, tripID);
                startActivity(intent);
            } else {
                Toast.makeText(getApplicationContext(), "Entry Update Failed", Toast.LENGTH_SHORT).show();
            }
        } else {
            if (dbHelper.insertEntry(nameEditText.getText().toString(),
                    timeEditText.getText().toString(),
                    dateEditText.getText().toString(),
                    locationEditText.getText().toString(),
                    trip_idText.getText().toString())) {
                Toast.makeText(getApplicationContext(), "Entry Inserted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Could not Insert Entry", Toast.LENGTH_SHORT).show();
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
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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

        buttonLayout.setBackgroundColor(Color.BLACK);

        saveButton.setBackgroundResource(R.color.card_background);
        saveButton.setTextColor(Color.RED);
        editButton.setBackgroundResource(R.color.card_background);
        editButton.setTextColor(Color.RED);
        deleteButton.setBackgroundResource(R.color.card_background);
        deleteButton.setTextColor(Color.RED);

        // Toast.makeText(MainActivityLog.this, "Night Mode", Toast.LENGTH_LONG).show();

    }
}