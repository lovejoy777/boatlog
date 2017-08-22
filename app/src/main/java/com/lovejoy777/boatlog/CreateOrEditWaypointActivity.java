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

/**
 * Created by lovejoy777 on 13/10/15.
 */
public class CreateOrEditWaypointActivity extends AppCompatActivity implements View.OnClickListener, LocationListener {

    private LocationManager locationManager;
    private String provider;
    private ExampleDBHelper dbHelper ;

    ScrollView scrollView1;
    RelativeLayout MRL1;
    Toolbar toolBar;

    TextView textViewName;
    TextView textViewLocation;
    TextView textViewDescription;

    EditText nameEditText;
    EditText locationEditText;
    EditText descriptionEditText;

    Button saveButton;
    LinearLayout buttonLayout;
    Button editButton, deleteButton;

    TextView titleTextView;

    int waypointID;
    int enteriesID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        waypointID = getIntent().getIntExtra(MainActivityWaypoint.KEY_EXTRA_WAYPOINT_ID, 0);

        setContentView(R.layout.activity_edit_waypoint);

        scrollView1 = (ScrollView) findViewById(R.id.scrollView1);
        MRL1 = (RelativeLayout) findViewById(R.id.MRL1);
        toolBar = (Toolbar) findViewById(R.id.toolbar);

        titleTextView = (TextView) findViewById(R.id.titleTextView);
        textViewName = (TextView) findViewById(R.id.textViewName);
        textViewLocation = (TextView) findViewById(R.id.textViewLocation);
        textViewDescription = (TextView) findViewById(R.id.textViewDescription);


        nameEditText = (EditText) findViewById(R.id.editTextName);
        locationEditText = (EditText) findViewById(R.id.editTextLocation);
        descriptionEditText = (EditText) findViewById(R.id.editTextDescription);

        buttonLayout = (LinearLayout) findViewById(R.id.buttonLayout);
        saveButton = (Button) findViewById(R.id.saveButton);
        editButton = (Button) findViewById(R.id.editButton);
        deleteButton = (Button) findViewById(R.id.deleteButton);

        titleTextView.setText("Create");

        SharedPreferences myPrefs = this.getSharedPreferences("myPrefs", MODE_PRIVATE);
        Boolean NightModeOn = myPrefs.getBoolean("switch1", false);

        if (NightModeOn) {
            NightMode();
        }

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

        locationEditText.setText("" + formattedLocation);

        saveButton = (Button) findViewById(R.id.saveButton);
        saveButton.setOnClickListener(this);
        buttonLayout = (LinearLayout) findViewById(R.id.buttonLayout);
        editButton = (Button) findViewById(R.id.editButton);
        editButton.setOnClickListener(this);
        deleteButton = (Button) findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(this);

        dbHelper = new ExampleDBHelper(this);

        if(waypointID > 0) {
            saveButton.setVisibility(View.GONE);
            buttonLayout.setVisibility(View.VISIBLE);

            titleTextView = (TextView) findViewById(R.id.titleTextView);
            titleTextView.setText("Edit");

            Cursor rs = dbHelper.getWaypoint(waypointID);
            rs.moveToFirst();
            String waypointName = rs.getString(rs.getColumnIndex(ExampleDBHelper.WAYPOINT_COLUMN_NAME));
            String waypointLocation = rs.getString(rs.getColumnIndex(ExampleDBHelper.WAYPOINT_COLUMN_LOCATION));
            String waypointDescription = rs.getString(rs.getColumnIndex(ExampleDBHelper.WAYPOINT_COLUMN_DESCRIPTION));
            if (!rs.isClosed()) {
                rs.close();
            }

            nameEditText.setText(waypointName);
            nameEditText.setFocusable(false);
            nameEditText.setClickable(false);

            locationEditText.setText((CharSequence) waypointLocation);
            locationEditText.setFocusable(false);
            locationEditText.setClickable(false);

            descriptionEditText.setText((CharSequence) (waypointDescription + ""));
            descriptionEditText.setFocusable(false);
            descriptionEditText.setClickable(false);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.saveButton:
                persistTrip();
                return;
            case R.id.editButton:
                saveButton.setVisibility(View.VISIBLE);
                buttonLayout.setVisibility(View.GONE);
                nameEditText.setEnabled(true);
                nameEditText.setFocusableInTouchMode(true);
                nameEditText.setClickable(true);

                locationEditText.setEnabled(true);
                locationEditText.setFocusableInTouchMode(true);
                locationEditText.setClickable(true);

                descriptionEditText.setEnabled(true);
                descriptionEditText.setFocusableInTouchMode(true);
                descriptionEditText.setClickable(true);
                return;
            case R.id.deleteButton:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.deleteWaypoint)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dbHelper.deleteWaypoint(waypointID);
                                Toast.makeText(getApplicationContext(), "Deleted Successfully", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), MainActivityWaypoint.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                            }
                        });
                AlertDialog d = builder.create();
                d.setTitle("Delete Waypoint?");
                d.show();
                return;
        }
    }

    public void persistTrip() {
        if(waypointID > 0) {
            if(dbHelper.updateWaypoint(waypointID, nameEditText.getText().toString(),
                    locationEditText.getText().toString(),
                    descriptionEditText.getText().toString())) {
                Toast.makeText(getApplicationContext(), "Waypoint Update Successful", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), MainActivityWaypoint.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
            else {
                Toast.makeText(getApplicationContext(), "Waypoint Update Failed", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            if(dbHelper.insertWaypoint(nameEditText.getText().toString(),
                    locationEditText.getText().toString(),
                    descriptionEditText.getText().toString())){
                Toast.makeText(getApplicationContext(), "Waypoint Inserted", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(getApplicationContext(), "Could not Insert Waypoint", Toast.LENGTH_SHORT).show();
            }
            Intent intent = new Intent(getApplicationContext(), MainActivityWaypoint.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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

            return Math.abs(latDegrees) + ":" + latMinutes + ":" + latSeconds
                    + ":" + latDegree + "/" + Math.abs(longDegrees) + ":" + longMinutes
                    + ":" + longSeconds + ":" + lonDegrees;
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
        textViewLocation.setTextColor(Color.RED);
        textViewDescription.setTextColor(Color.RED);

        nameEditText.setTextColor(Color.RED);
        locationEditText.setTextColor(Color.RED);
        descriptionEditText.setTextColor(Color.RED);

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