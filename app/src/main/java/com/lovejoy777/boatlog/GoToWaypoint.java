package com.lovejoy777.boatlog;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.akhgupta.easylocation.EasyLocationAppCompatActivity;
import com.akhgupta.easylocation.EasyLocationRequest;
import com.akhgupta.easylocation.EasyLocationRequestBuilder;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

import java.math.BigDecimal;

import static java.lang.Math.abs;
import static java.lang.Math.floor;

/**
 * Created by lovejoy777 on 14/10/15.
 */
public class GoToWaypoint extends EasyLocationAppCompatActivity implements LocationListener,
        SensorEventListener {

    // SQLITE DATABASE
    BoatLogDBHelper dbHelper;

    // GOOGLE MAPS/LOCATION SERVICES
    final String TAG = "GPS";
    long UPDATE_INTERVAL = 2 * 1000;  /* 10 secs */
    long FASTEST_INTERVAL = 2000; /* 2 sec */
    long FALLBACK_INTERVAL = 20000; // 2 seconds
    long INDICATOR_INTERVAL = 200; // .2 seconds

    // COMPASS MANAGER
    private SensorManager mSensorManager;

    // DIRECTION OF POINTER
    private float currentDegreeNeedle = 0f;

    Toolbar toolBar;
    TextView titleTextView;

    // MAIN LAYOUTS
    RelativeLayout MainRL;
    LinearLayout MLL1;
    LinearLayout MLL2;
    LinearLayout MLL3;

    // GRID OUTLINES LAYOUTS
    LinearLayout LLG;
    LinearLayout LLG0;
    LinearLayout LLG1;
    LinearLayout LLG2;
    LinearLayout LLG3;
    LinearLayout LLG4;
    LinearLayout LLG5;
    LinearLayout LLG6;
    LinearLayout LLG7;
    LinearLayout LLG8;
    LinearLayout LLG9;
    LinearLayout LLG10;

    // TEXTVIEWS
    TextView textViewDest;
    TextView textViewSped;
    TextView textViewHead;
    TextView textViewComp;
    TextView textViewDist;
    TextView textViewCourse;

    TextView textViewLat;
    TextView textViewLon;
    TextView textViewSpeed;
    TextView textViewHeading;
    TextView textViewCompass;
    TextView textViewCourseTo;
    TextView textViewDistance;
    TextView textViewGoTo;
    TextView textViewGetTime;

    // IMAGEVIEWS
    ImageView imageViewAccu;
    ImageView arrow;

    int waypointID;
    String waypointName;
    double doublelat;
    double doublelong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_goto);

        // GET WAYPOINT DATA FROM MAINACTIVITYWAYPOINT.CLASS
        waypointID = getIntent().getIntExtra(MainActivityWaypoint.KEY_EXTRA_WAYPOINT_ID, 0);
        waypointName = getIntent().getStringExtra(MainActivityWaypoint.KEY_EXTRA_WAYPOINT_NAME);

        toolBar = (Toolbar) findViewById(R.id.toolbar);
        titleTextView = (TextView) findViewById(R.id.titleTextView);

        MainRL = (RelativeLayout) findViewById(R.id.MainRL);
        MLL1 = (LinearLayout) findViewById(R.id.MLL1);
        MLL2 = (LinearLayout) findViewById(R.id.MLL2);
        MLL3 = (LinearLayout) findViewById(R.id.MLL3);

        // GRID OUTLINE LAYOUTS
        LLG = (LinearLayout) findViewById(R.id.LLG);
        LLG0 = (LinearLayout) findViewById(R.id.LLG0);
        LLG1 = (LinearLayout) findViewById(R.id.LLG1);
        LLG2 = (LinearLayout) findViewById(R.id.LLG2);
        LLG3 = (LinearLayout) findViewById(R.id.LLG3);
        LLG4 = (LinearLayout) findViewById(R.id.LLG4);
        LLG5 = (LinearLayout) findViewById(R.id.LLG5);
        LLG6 = (LinearLayout) findViewById(R.id.LLG6);
        LLG7 = (LinearLayout) findViewById(R.id.LLG7);
        LLG8 = (LinearLayout) findViewById(R.id.LLG8);
        LLG9 = (LinearLayout) findViewById(R.id.LLG9);
        LLG10 = (LinearLayout) findViewById(R.id.LLG10);

        // arrow = DIRECTION POINTER
        arrow = (ImageView) findViewById(R.id.needle);
        arrow.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.primary)));

        textViewLat = (TextView) findViewById(R.id.textViewLat);
        textViewLon = (TextView) findViewById(R.id.textViewLon);
        textViewSpeed = (TextView) findViewById(R.id.textViewSpeed);
        textViewHeading = (TextView) findViewById(R.id.textViewHeading);
        textViewCompass = (TextView) findViewById(R.id.textViewCompass);
        textViewDistance = (TextView) findViewById(R.id.textViewDistance);
        textViewCourseTo = (TextView) findViewById(R.id.textViewCourseTo);
        textViewDest = (TextView) findViewById(R.id.textViewDest);
        textViewSped = (TextView) findViewById(R.id.textViewSped);
        textViewHead = (TextView) findViewById(R.id.textViewHead);
        textViewComp = (TextView) findViewById(R.id.textViewComp);
        textViewCourse = (TextView) findViewById(R.id.textViewCourse);
        textViewDist = (TextView) findViewById(R.id.textViewDist);
        imageViewAccu = (ImageView) findViewById(R.id.imageViewAccu);

        textViewGoTo = (TextView) findViewById(R.id.textViewGoTo);
        textViewGetTime = (TextView) findViewById(R.id.textViewGetTime);

        // GET WAYPOINT DATA FROM DATABASE
        dbHelper = new BoatLogDBHelper(this);

        Cursor rs = dbHelper.getWaypoint(waypointID);
        rs.moveToFirst();
        final String waypointName = rs.getString(rs.getColumnIndex(BoatLogDBHelper.WAYPOINT_COLUMN_NAME));
        //String waypointDescription = rs.getString(rs.getColumnIndex(BoatLogDBHelper.WAYPOINT_COLUMN_DESCRIPTION));
        String waypointLatDeg = rs.getString(rs.getColumnIndex(BoatLogDBHelper.WAYPOINT_COLUMN_LATDEG));
        String waypointLatMin = rs.getString(rs.getColumnIndex(BoatLogDBHelper.WAYPOINT_COLUMN_LATMIN));
        String waypointLatSec = rs.getString(rs.getColumnIndex(BoatLogDBHelper.WAYPOINT_COLUMN_LATSEC));
        String waypointLatNS = rs.getString(rs.getColumnIndex(BoatLogDBHelper.WAYPOINT_COLUMN_LATNS));
        String waypointLongDeg = rs.getString(rs.getColumnIndex(BoatLogDBHelper.WAYPOINT_COLUMN_LONGDEG));
        String waypointLongMin = rs.getString(rs.getColumnIndex(BoatLogDBHelper.WAYPOINT_COLUMN_LONGMIN));
        String waypointLongSec = rs.getString(rs.getColumnIndex(BoatLogDBHelper.WAYPOINT_COLUMN_LONGSEC));
        String waypointLongEW = rs.getString(rs.getColumnIndex(BoatLogDBHelper.WAYPOINT_COLUMN_LONGEW));
        if (!rs.isClosed()) {
            rs.close();
        }
        double intLatDeg = Double.parseDouble(waypointLatDeg);
        double intLatMin = Double.parseDouble(waypointLatMin);
        double intLatSec = Double.parseDouble(waypointLatSec);
        double intLongDeg = Double.parseDouble(waypointLongDeg);
        double intLongMin = Double.parseDouble(waypointLongMin);
        double intLongSec = Double.parseDouble(waypointLongSec);

        doublelat = DMSToDecimal(intLatDeg, intLatMin, intLatSec, waypointLatNS);
        doublelong = DMSToDecimal(intLongDeg, intLongMin, intLongSec, waypointLongEW);

        String stringlatdms = waypointLatDeg + "°" + waypointLatMin + "'" + waypointLatSec + "\"" + waypointLatNS;
        String stringlongdms = waypointLongDeg + "°" + waypointLongMin + "'" + waypointLongSec + "\"" + waypointLongEW;

        // PREFILL TEXT FIELDS
        textViewGoTo.setText(waypointName + " @ " + stringlatdms + ", " + stringlongdms);
        textViewLat.setText("searching");
        textViewLon.setText("for gps");
        textViewDistance.setText("0.0 NM");
        textViewSpeed.setText("0.0 KN");
        textViewHeading.setText("00 T");
        textViewCourseTo.setText("00 T");

        imageViewAccu.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));

        SharedPreferences myPrefs = this.getSharedPreferences("myPrefs", MODE_PRIVATE);
        final Boolean NightModeOn = myPrefs.getBoolean("switch1", false);

        if (NightModeOn) {
            NightMode();
        }

        Boolean ScreenOn = myPrefs.getBoolean("switch2", false);

        if (ScreenOn) {
            screenOn();
        }

        // COMPASS SENSOR MANAGER
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }

        isGooglePlayServicesAvailable();

       // if (!isLocationEnabled())
         //   showAlert();

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

        if (location != null) {
            long locationAge = System.currentTimeMillis() - location.getTime();
            long newLocationAge = locationAge;
            String locAge = String.valueOf(newLocationAge);
            textViewGetTime.setText(locAge);
            // showToast(locAge);
            if (newLocationAge < INDICATOR_INTERVAL) {
                updateUI(location);

            } else {
                textViewLat.setText("searching");
                textViewLon.setText("for gps");
                textViewDistance.setText("0.0 NM");
                textViewSpeed.setText("0.0 KN");
                textViewHeading.setText("00 T");
                textViewCourseTo.setText("00 T");

                SharedPreferences myPrefs = this.getSharedPreferences("myPrefs", MODE_PRIVATE);
                final Boolean NightModeOn = myPrefs.getBoolean("switch1", false);
                arrow.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
                imageViewAccu.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.card_background)));
                if (NightModeOn) {
                    arrow.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.black)));
                    imageViewAccu.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.black)));
                }

            }
        }
    }

    // UPDATE UI
    private void updateUI(Location location) {
        Log.d(TAG, "updateUI");
        // PRESET TEXT VIEWS
        SharedPreferences myPrefs = this.getSharedPreferences("myPrefs", MODE_PRIVATE);
        final Boolean NightModeOn = myPrefs.getBoolean("switch1", false);

        arrow.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.primary)));
        imageViewAccu.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.light_green_500)));

        if (NightModeOn) {
            arrow.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.night_text)));
            imageViewAccu.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.light_green_500)));

        }

        // SET TEXT VIEWS LATITUDE & LONGITUDE WITH LOCATION
        textViewLat.setText(FormattedLocationLat(location.getLatitude()));
        textViewLon.setText(FormattedLocationLon(location.getLongitude()));

            // DISTANCE TO WAYPOINT
            Location locationA = new Location("point A");
            locationA.setLatitude(location.getLatitude());
            locationA.setLongitude(location.getLongitude());

            Location locationB = new Location("point B");
            locationB.setLatitude(doublelat);
            locationB.setLongitude(doublelong);

            float[] results = new float[1];
            Location.distanceBetween(
                    location.getLatitude(), location.getLongitude(),
                    doublelat, doublelong, results);
            String sttotal = String.valueOf(results[0]);
            double doubleDistTo = Double.parseDouble(sttotal) / 1852; // 1852m per NM //nm to meters
            String stringDistTo = String.format("%.2f", doubleDistTo);
            textViewDistance.setText(stringDistTo + (" NM"));

        // HAS SPEED
            if (location.hasSpeed()) {
                float formattedSpeed = FormattedSpeed(location.getSpeed());
                BigDecimal result;
                result=round(formattedSpeed,2);
                System.out.println(result);
                textViewSpeed.setText("" + result + " KN");

            }

        // CURRENT TRACK OVER GROUND
        float heading = location.getBearing();
        int intheading = (int) heading;
        if (intheading < 0) {
            intheading = intheading + 360;
        }

        // BEARING TO WAYPOINT
        float BearTo = location.bearingTo(locationB);
        int intBearTo = (int) BearTo;
        if (intBearTo < 0) {
            intBearTo = intBearTo + 360;
        }

        // GET POINTER ANGLE
        if (heading < 0) {
            heading = 360 + heading;
        }
        if (BearTo < 0) {
            BearTo = 360 + BearTo;
        }

        // FILL TEXTVIEW TRACK OVER GROUND
        String stringHeading = String.valueOf(intheading);
        textViewHeading.setText(stringHeading + " T");

        float direction = (BearTo - heading) - 360;

        // ROTATE POINTER
        RotateAnimation ra = new RotateAnimation(
                currentDegreeNeedle,
                direction,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        // how long the animation will take place
        ra.setDuration(210);
        // set the animation after the end of the reservation status
        ra.setFillAfter(true);
        arrow.startAnimation(ra);
        // Start the animation
        currentDegreeNeedle = direction;
        arrow.startAnimation(ra);

        // FILL TEXTVIEW BEARING TO WAYPOINT
            String stringBearTo = String.valueOf(intBearTo);
            textViewCourseTo.setText(stringBearTo + (" T"));

    }

    public static BigDecimal round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd;
    }

    // CONVERT FROM METERS PER SECOND TO KNOTS PER HOUR
    public static float FormattedSpeed(float mps) {
        float mpsSped = abs(mps * 1.943844f);
        return abs(mpsSped);
    }

    // CONVERT DMS TO DECIMAL
    public double DMSToDecimal(double degres, double minutes, double secondes, String hemisphereOUmeridien) {
        double LatOrLon = 0;
        double signe = 1.0;

        if ((hemisphereOUmeridien.equals("W")) || (hemisphereOUmeridien.equals("S"))) {
            signe = -1.0;
        }
        LatOrLon = signe * (floor(degres) + floor(minutes) / 60.0 + secondes / 3600.0);

        return (LatOrLon);
    }

    // CONVERT LATITUDE TO A STRING
    public static String FormattedLocationLat(double latitude) {
        try {
            int latSeconds = (int) Math.round(latitude * 3600);
            int latDegrees = latSeconds / 3600;
            latSeconds = abs(latSeconds % 3600);
            int latMinutes = latSeconds / 60;
            latSeconds %= 60;

            String latDegree = latitude >= 0 ? "N" : "S";

            return abs(latDegrees) + "°" + latMinutes + "'" + latSeconds
                    + "\"" + latDegree;
        } catch (Exception e) {

            return "" + String.format("%8.5f", latitude);
        }
    }

    // CONVERT LONGITUDE TO A STRING
    public static String FormattedLocationLon(double longitude) {
        try {
            int longSeconds = (int) Math.round(longitude * 3600);
            int longDegrees = longSeconds / 3600;
            longSeconds = abs(longSeconds % 3600);
            int longMinutes = longSeconds / 60;
            longSeconds %= 60;
            String lonDegrees = longitude >= 0 ? "E" : "W";

            return abs(longDegrees) + "°" + longMinutes
                    + "'" + longSeconds + "\"" + lonDegrees;
        } catch (Exception e) {

            return "" + String.format("%8.5f", longitude);
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


    @Override
    public void onLocationChanged(Location location) {

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
            builder = new android.support.v7.app.AlertDialog.Builder(GoToWaypoint.this, R.style.AlertDialogTheme);
        } else {
            builder = new android.support.v7.app.AlertDialog.Builder(GoToWaypoint.this, R.style.AlertDialogTheme);
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

    // PERMISSIONS CHECK
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

    // COMPASS SENSOR CHANGED
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        // get the angle around the z-axis rotated
        float degree = Math.round(sensorEvent.values[0]);

        //  int deg = degree.intValue();
        String result = "0";
        if (degree == Math.floor(degree)) {
            result = Integer.toString((int) degree);
        } else {
            result = Float.toString(degree);
        }

        textViewCompass.setText("" + result + " M");

    }

    // COMPASS ACCURACY CHANGE
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        // not in use
    }

    // NIGHT MODE
    private void NightMode() {

        toolBar.setBackgroundColor(getResources().getColor(R.color.card_background));
        MainRL.setBackgroundColor(getResources().getColor(R.color.card_background));
        MLL1.setBackgroundColor(getResources().getColor(R.color.card_background));
        MLL2.setBackgroundColor(getResources().getColor(R.color.card_background));
        MLL3.setBackgroundColor(getResources().getColor(R.color.card_background));

        LLG.setBackgroundColor(getResources().getColor(R.color.grid_outline));
        LLG0.setBackgroundColor(getResources().getColor(R.color.grid_outline));
        LLG1.setBackgroundColor(getResources().getColor(R.color.grid_outline));
        LLG2.setBackgroundColor(getResources().getColor(R.color.grid_outline));
        LLG3.setBackgroundColor(getResources().getColor(R.color.grid_outline));
        LLG4.setBackgroundColor(getResources().getColor(R.color.grid_outline));
        LLG5.setBackgroundColor(getResources().getColor(R.color.grid_outline));
        LLG6.setBackgroundColor(getResources().getColor(R.color.grid_outline));
        LLG7.setBackgroundColor(getResources().getColor(R.color.grid_outline));
        LLG8.setBackgroundColor(getResources().getColor(R.color.grid_outline));
        LLG9.setBackgroundColor(getResources().getColor(R.color.grid_outline));
        LLG10.setBackgroundColor(getResources().getColor(R.color.grid_outline));

        textViewGetTime.setTextColor(getResources().getColor(R.color.night_text));
        titleTextView.setTextColor(getResources().getColor(R.color.night_text));
        textViewGoTo.setTextColor(getResources().getColor(R.color.night_text));
        textViewLat.setTextColor(getResources().getColor(R.color.night_text));
        textViewLon.setTextColor(getResources().getColor(R.color.night_text));
        textViewSpeed.setTextColor(getResources().getColor(R.color.night_text));
        textViewHeading.setTextColor(getResources().getColor(R.color.night_text));
        textViewCompass.setTextColor(getResources().getColor(R.color.night_text));
        textViewCourseTo.setTextColor(getResources().getColor(R.color.night_text));
        textViewDistance.setTextColor(getResources().getColor(R.color.night_text));

        textViewDest.setTextColor(getResources().getColor(R.color.night_text));
        textViewSped.setTextColor(getResources().getColor(R.color.night_text));
        textViewHead.setTextColor(getResources().getColor(R.color.night_text));
        textViewComp.setTextColor(getResources().getColor(R.color.night_text));
        textViewCourse.setTextColor(getResources().getColor(R.color.night_text));
        textViewDist.setTextColor(getResources().getColor(R.color.night_text));

        arrow.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.night_text)));
        imageViewAccu.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.night_text)));

    }

    // SCREEN WAKELOCK
    private void screenOn() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    }

    // COMPASS REQUEST UPDATES AT STARTUP
    @Override
    protected void onResume() {

        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_GAME);
        super.onResume();

    }

    // COMPASS UNREGESTER LISTENER
    @Override
    protected void onPause() {
        stopLocationUpdates();
        mSensorManager.unregisterListener(this);
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        stopLocationUpdates();
        mSensorManager.unregisterListener(this);
        super.onBackPressed();
        overridePendingTransition(R.anim.back2, R.anim.back1);
    }

}

