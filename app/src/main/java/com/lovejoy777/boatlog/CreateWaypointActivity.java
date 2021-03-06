package com.lovejoy777.boatlog;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.akhgupta.easylocation.EasyLocationAppCompatActivity;
import com.akhgupta.easylocation.EasyLocationRequest;
import com.akhgupta.easylocation.EasyLocationRequestBuilder;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.LocationRequest;

import java.util.Locale;

/**
 * Created by steve on 08/09/17.
 */

public class CreateWaypointActivity extends EasyLocationAppCompatActivity {

    private BoatLogDBHelper dbHelper;

    // GOOGLE MAPS/LOCATION SERVICES
    final String TAG = "GPS";
    long UPDATE_INTERVAL = 2 * 1000;  // 10 secs?
    long FASTEST_INTERVAL = 2000; // 2 sec
    long FALLBACK_INTERVAL = 4000; // 7 seconds

    FloatingActionButton fabSave;
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

        scrollView1 = (ScrollView) findViewById(R.id.scrollView1);
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

        titleTextView.setText(R.string.create_waypoint);

        fabSave = (FloatingActionButton) this.findViewById(R.id.fabSave);

        SharedPreferences myPrefs = this.getSharedPreferences("myPrefs", MODE_PRIVATE);
        Boolean NightModeOn = myPrefs.getBoolean("switch1", false);

        if (NightModeOn) {
            NightMode();
        }

        // PERMISSIONS
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }

        // IS GOOGLE PLAY SERVICES AVAILIBLE
        isGooglePlayServicesAvailable();
        // if (!isLocationEnabled())
        //   showAlert();

        dbHelper = new BoatLogDBHelper(this);
        fabSave.setImageResource(R.drawable.ic_save_white);
        fabSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                persistWaypoint();
            }
        });

        // EASYLOCATION SETUP
        LocationRequest locationRequest = new LocationRequest()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL);
        EasyLocationRequest easyLocationRequest = new EasyLocationRequestBuilder()
                .setLocationRequest(locationRequest)
                .setFallBackToLastLocationTime(FALLBACK_INTERVAL)
                .build();
        requestLocationUpdates(easyLocationRequest);

    }

    @Override
    public void onLocationReceived(Location location) {
        String dms;
        float nDegrees;
        float eDegrees;
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

                nDegrees = nDegree + (float) nMinute / 60 + (float) nSecond / 3600;
                String nResult = Float.toString(nDegrees).substring(0, 10);

                eDegrees = eDegree + (float) eMinute / 60 + (float) eSecond / 3600;
                String eResult = Float.toString(eDegrees).substring(0, 10);

                System.out.println(nResult);
                System.out.println(eResult);
            } catch (Exception e) {
                System.out.println("Exeption e");
            }

        }
    }

    public void persistWaypoint() {

        if (dbHelper.insertWaypoint(
                nameEditText.getText().toString(),
                descriptionEditText.getText().toString(),
                latdegEditText.getText().toString(),
                latminEditText.getText().toString(),
                latsecEditText.getText().toString(),
                latnsEditText.getText().toString(),
                longdegEditText.getText().toString(),
                longminEditText.getText().toString(),
                longsecEditText.getText().toString(),
                longewEditText.getText().toString())) {
            Toast.makeText(getApplicationContext(), "Waypoint Saved", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "Could not Save Waypoint", Toast.LENGTH_SHORT).show();
        }
        Intent intent = new Intent(getApplicationContext(), MainActivityWaypoint.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Bundle bndlanimation =
                ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.anni1, R.anim.anni2).toBundle();
        startActivity(intent, bndlanimation);
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
            String latDegree = latitude >= 0 ? "N" : "S";
            String lonDegree = longitude >= 0 ? "E" : "W";

            return Math.abs(latDegrees) + "." + latMinutes + "." + latSeconds
                    + " " + latDegree + "/" + Math.abs(longDegrees) + "." + longMinutes
                    + "." + longSeconds + " " + lonDegree;
        } catch (Exception e) {

            return "" + String.format(Locale.UK,"%8.5f", latitude) + "  "
                    + String.format(Locale.UK,"%8.5f", longitude);
        }
    }

    // EASYLOCATION LIB METHODS
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLocationPermissionGranted() {
        showToast("Location permission granted");
    }

    @Override
    public void onLocationPermissionDenied() {
        showToast("Location permission denied");
    }

    @Override
    public void onLocationProviderEnabled() {
        showToast("Location services are now ON");
    }

    @Override
    public void onLocationProviderDisabled() {
        showToast("Location services are still Off");
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private boolean isGooglePlayServicesAvailable() {
        final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.d(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        Log.d(TAG, "This device is supported.");
        return true;
    }

    private void showAlert() {
        android.support.v7.app.AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new android.support.v7.app.AlertDialog.Builder(CreateWaypointActivity.this, R.style.AlertDialogTheme);
        } else {
            builder = new android.support.v7.app.AlertDialog.Builder(CreateWaypointActivity.this, R.style.AlertDialogTheme);
        }
        builder.setTitle("Enable Location Services")
                .setMessage("Your Locations Settings is set to 'Off'.\nPlease Enable Location to " +
                        "use this app")
                .setPositiveButton("Enable", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                        Bundle bndlanimation =
                                ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.anni1, R.anim.anni2).toBundle();
                        startActivity(myIntent, bndlanimation);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // cancelled by user
                    }
                })
                .setIcon(R.drawable.ic_location_on_white)
                .show();
    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    private void NightMode() {

        scrollView1.setBackgroundResource(R.color.card_background);
        MRL1.setBackgroundResource(R.color.card_background);
        toolBar.setBackgroundResource(R.color.card_background);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            titleTextView.setTextColor(getBaseContext().getResources().getColor(R.color.night_text, getBaseContext().getTheme()));
            textViewName.setTextColor(getBaseContext().getResources().getColor(R.color.night_text, getBaseContext().getTheme()));
            textViewDescription.setTextColor(getBaseContext().getResources().getColor(R.color.night_text, getBaseContext().getTheme()));
            textViewLocationLat.setTextColor(getBaseContext().getResources().getColor(R.color.night_text, getBaseContext().getTheme()));
            textViewLocationLong.setTextColor(getBaseContext().getResources().getColor(R.color.night_text, getBaseContext().getTheme()));

            nameEditText.setTextColor(getBaseContext().getResources().getColor(R.color.night_text, getBaseContext().getTheme()));
            descriptionEditText.setTextColor(getBaseContext().getResources().getColor(R.color.night_text, getBaseContext().getTheme()));
            latdegEditText.setTextColor(getBaseContext().getResources().getColor(R.color.night_text, getBaseContext().getTheme()));
            latminEditText.setTextColor(getBaseContext().getResources().getColor(R.color.night_text, getBaseContext().getTheme()));
            latsecEditText.setTextColor(getBaseContext().getResources().getColor(R.color.night_text, getBaseContext().getTheme()));
            latnsEditText.setTextColor(getBaseContext().getResources().getColor(R.color.night_text, getBaseContext().getTheme()));
            longdegEditText.setTextColor(getBaseContext().getResources().getColor(R.color.night_text, getBaseContext().getTheme()));
            longminEditText.setTextColor(getBaseContext().getResources().getColor(R.color.night_text, getBaseContext().getTheme()));
            longsecEditText.setTextColor(getBaseContext().getResources().getColor(R.color.night_text, getBaseContext().getTheme()));
            longewEditText.setTextColor(getBaseContext().getResources().getColor(R.color.night_text, getBaseContext().getTheme()));

        }else {
            titleTextView.setTextColor(getResources().getColor(R.color.night_text));
            textViewName.setTextColor(getResources().getColor(R.color.night_text));
            textViewDescription.setTextColor(getResources().getColor(R.color.night_text));
            textViewLocationLat.setTextColor(getResources().getColor(R.color.night_text));
            textViewLocationLong.setTextColor(getResources().getColor(R.color.night_text));

            nameEditText.setTextColor(getResources().getColor(R.color.night_text));
            descriptionEditText.setTextColor(getResources().getColor(R.color.night_text));
            latdegEditText.setTextColor(getResources().getColor(R.color.night_text));
            latminEditText.setTextColor(getResources().getColor(R.color.night_text));
            latsecEditText.setTextColor(getResources().getColor(R.color.night_text));
            latnsEditText.setTextColor(getResources().getColor(R.color.night_text));
            longdegEditText.setTextColor(getResources().getColor(R.color.night_text));
            longminEditText.setTextColor(getResources().getColor(R.color.night_text));
            longsecEditText.setTextColor(getResources().getColor(R.color.night_text));
            longewEditText.setTextColor(getResources().getColor(R.color.night_text));
        }

    }

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
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.back2, R.anim.back1);
        stopLocationUpdates();
    }

}