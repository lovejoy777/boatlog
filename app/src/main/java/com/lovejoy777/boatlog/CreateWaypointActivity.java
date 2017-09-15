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
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by steve on 08/09/17.
 */

public class CreateWaypointActivity extends AppCompatActivity implements LocationListener {

    private LocationManager locationManager;
    private String provider;
    private BoatLogDBHelper dbHelper ;

    private boolean fabExpanded = false;
    private FloatingActionButton fabSave; //fabMainDeleteEditSave
    FrameLayout fabFrame;

    ScrollView scrollView1;
    RelativeLayout MRL1;
    Toolbar toolBar;

    TextView textViewName;
    TextView textViewDescription;
    TextView textViewLocationLat;
    TextView textViewLocationLong;

    EditText nameEditText;
    EditText descriptionEditText;
    EditText latdegEditText;
    EditText latminEditText;
    EditText latsecEditText;
    EditText latnsEditText;
    EditText longdegEditText;
    EditText longminEditText;
    EditText longsecEditText;
    EditText longewEditText;


    TextView titleTextView;

    int waypointID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        waypointID = getIntent().getIntExtra(MainActivityWaypoint.KEY_EXTRA_WAYPOINT_ID, 0);

        setContentView(R.layout.activity_create_waypoint);

        // scrollView1 = (ScrollView) findViewById(R.id.scrollView1);
        MRL1 = (RelativeLayout) findViewById(R.id.MRL1);
        fabFrame = (FrameLayout) findViewById(R.id.fabFrame);
        toolBar = (Toolbar) findViewById(R.id.toolbar);

        titleTextView = (TextView) findViewById(R.id.titleTextView);
        textViewName = (TextView) findViewById(R.id.textViewName);
        textViewDescription = (TextView) findViewById(R.id.textViewDescription);
        textViewLocationLat = (TextView) findViewById(R.id.textViewLocationLat);
        textViewLocationLong = (TextView) findViewById(R.id.textViewLocationLong);



        nameEditText = (EditText) findViewById(R.id.editTextName);
        descriptionEditText = (EditText) findViewById(R.id.editTextDescription);
        latdegEditText = (EditText) findViewById(R.id.editTextLatDeg);
        latminEditText = (EditText) findViewById(R.id.editTextLatMin);
        latsecEditText = (EditText) findViewById(R.id.editTextLatSec);
        latnsEditText = (EditText) findViewById(R.id.editTextLatNS);
        longdegEditText = (EditText) findViewById(R.id.editTextLongDeg);
        longminEditText = (EditText) findViewById(R.id.editTextLongMin);
        longsecEditText = (EditText) findViewById(R.id.editTextLongSec);
        longewEditText = (EditText) findViewById(R.id.editTextLongEW);

        titleTextView.setText("Create New Waypoint");

        fabSave = (FloatingActionButton) this.findViewById(R.id.fabSave);

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



        // pre fill text fields

       // locationEditText.setText("" + formattedLocation);

        dbHelper = new BoatLogDBHelper(this);

        fabSave.setImageResource(R.drawable.ic_save_white);
        fabSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                persistWaypoint();
            }
        });

    }
    public void persistWaypoint() {
        if(waypointID > 0) {
            if(dbHelper.updateWaypoint(
                    waypointID,
                    nameEditText.getText().toString(),
                    descriptionEditText.getText().toString(),
                    latdegEditText.getText().toString(),
                    latminEditText.getText().toString(),
                    latsecEditText.getText().toString(),
                    latnsEditText.getText().toString(),
                    longdegEditText.getText().toString(),
                    longminEditText.getText().toString(),
                    longsecEditText.getText().toString(),
                    longewEditText.getText().toString()))
            {
                Toast.makeText(getApplicationContext(), "Waypoint Edited Successful", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), MainActivityWaypoint.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
            else {
                Toast.makeText(getApplicationContext(), "Waypoint Edit Failed", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            if(dbHelper.insertWaypoint(
                    nameEditText.getText().toString(),
                    descriptionEditText.getText().toString(),
                    latdegEditText.getText().toString(),
                    latminEditText.getText().toString(),
                    latsecEditText.getText().toString(),
                    latnsEditText.getText().toString(),
                    longdegEditText.getText().toString(),
                    longminEditText.getText().toString(),
                    longsecEditText.getText().toString(),
                    longewEditText.getText().toString()))
            {
                Toast.makeText(getApplicationContext(), "Waypoint Saved", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(getApplicationContext(), "Could not Save Waypoint", Toast.LENGTH_SHORT).show();
            }
            Intent intent = new Intent(getApplicationContext(), MainActivityWaypoint.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    public static String DDtoDMS(double latitude, double longitude) {
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
            String lonDegree = longDegrees >= 0 ? "W" : "E";

            return Math.abs(latDegrees) + "." + latMinutes + "." + latSeconds
                    + " " + latDegree + "/" + Math.abs(longDegrees) + "." + longMinutes
                    + "." + longSeconds + " " + lonDegree;
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

        // prefill lat long text fields
        String dms = null;
        float nDegrees = 1;
        float eDegrees = 1;
        if (location != null) {

            dms = DDtoDMS(location.getLatitude(), location.getLongitude());

            try {
                String inputst = dms.replaceAll("[^A-Za-z0-9]", " ");
                String[] array = inputst.split(" ");

                int nDegree = Integer.parseInt(array[0]);
                int nMinute = Integer.parseInt(array[1]);
                int nSecond = Integer.parseInt(array[2]);
                String latDegrees = nDegree >= 0 ? "N" : "S";

                int eDegree = Integer.parseInt(array[4]);
                int eMinute = Integer.parseInt(array[5]);
                int eSecond = Integer.parseInt(array[6]);
                String longDegrees = eDegree >= 0 ? "W" : "E";

                latdegEditText.setText(String.valueOf(nDegree));
                latminEditText.setText(String.valueOf(nMinute));
                latsecEditText.setText(String.valueOf(nSecond));
                latnsEditText.setText(latDegrees);

                longdegEditText.setText(String.valueOf(eDegree));
                longminEditText.setText(String.valueOf(eMinute));
                longsecEditText.setText(String.valueOf(eSecond));
                longewEditText.setText(longDegrees);

                nDegrees = nDegree + (float) nMinute/60 + (float) nSecond/3600;
                String nResult = Float.toString(nDegrees).substring(0,10);

                eDegrees = eDegree + (float) eMinute/60 + (float) eSecond/3600;
                String eResult = Float.toString(eDegrees).substring(0,10);

                System.out.println(nResult);
                System.out.println(eResult);
            } catch (Exception e) {

            }

        }

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

        // scrollView1.setBackgroundColor(Color.BLACK);
        MRL1.setBackgroundColor(Color.BLACK);
        //fabFrame.setBackgroundColor(Color.BLACK);
        toolBar.setBackgroundColor(Color.BLACK);
        titleTextView.setTextColor(Color.RED);

        textViewName.setTextColor(Color.RED);
        textViewDescription.setTextColor(Color.RED);
        textViewLocationLat.setTextColor(Color.RED);
        textViewLocationLong.setTextColor(Color.RED);


        nameEditText.setTextColor(Color.RED);
        descriptionEditText.setTextColor(Color.RED);
        latdegEditText.setTextColor(Color.RED);
        latminEditText.setTextColor(Color.RED);
        latsecEditText.setTextColor(Color.RED);
        latnsEditText.setTextColor(Color.RED);
        longdegEditText.setTextColor(Color.RED);
        longminEditText.setTextColor(Color.RED);
        longsecEditText.setTextColor(Color.RED);
        longewEditText.setTextColor(Color.RED);
    }

}

/**
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

            return Math.abs(latDegrees) + "." + latMinutes + "." + latSeconds
                    + " " + latDegree + "/" + Math.abs(longDegrees) + "." + longMinutes
                    + "." + longSeconds + " " + lonDegrees;
        } catch (Exception e) {

            return "" + String.format("%8.5f", latitude) + "  "
                    + String.format("%8.5f", longitude);
        }
    }
 */