package com.lovejoy777.boatlog;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import com.google.android.gms.location.LocationRequest;
import com.lovejoy777.boatlog.activities.AboutActivity;
import com.lovejoy777.boatlog.activities.SettingActivity;

import java.math.BigDecimal;
import java.util.Locale;

import static java.lang.Math.abs;
import static java.lang.Math.floor;

/**
 * Created by lovejoy777 on 14/10/15.
 */
public class GoToWaypoint extends EasyLocationAppCompatActivity implements SensorEventListener {

    private DrawerLayout mDrawerLayout;
    private SwitchCompat switcher1, switcher2;

    // SQLITE DATABASE
    BoatLogDBHelper dbHelper;

    // GOOGLE MAPS/LOCATION SERVICES
    final String TAG = "GPS";
    long UPDATE_INTERVAL = 2 * 1000;  // 10 secs?
    long FASTEST_INTERVAL = 2000; // 2 sec
    long FALLBACK_INTERVAL = 4000; // 7 seconds
    // INDICATOR VALUES
    long INDICATOR1_INTERVAL = 15; // 1 seconds
    long INDICATOR2_INTERVAL = 40; // 3 seconds
    long INDICATORFALLBACK_INTERVAL = 400; // 4 seconds
    long INDICATORNOGPS_INTERVAL = 1000; // 7 seconds
    long DEVIDE_NUMBER = 10000000; //nano to tenths

    // COMPASS MANAGER
    private SensorManager mSensorManager;

    // DIRECTION OF POINTER
    private float currentDegreeNeedle = 0f;

    // TOOLBAR & TITLE TEXT VIEW
    Toolbar toolBar;

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
    TextView textViewGetTimeAbbr;

    // IMAGEVIEWS
    ImageView imageViewAccu;
    ImageView arrow;

    int waypointID;
    String waypointName;
    double doublelat;
    double doublelong;

    int theme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Initialize the associated SharedPreferences file with default values
        PreferenceManager.setDefaultValues(this, R.xml.prefs, false);
        SharedPreferences prefs1 = PreferenceManager.getDefaultSharedPreferences(this);
        setTheme(theme = getTheme(prefs1.getString("theme", "fresh")));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_goto);

        loadToolbarNavDrawer();

        waypointID = getIntent().getIntExtra(MainActivityWaypoint.KEY_EXTRA_WAYPOINT_ID, 0);
        waypointName = getIntent().getStringExtra(MainActivityWaypoint.KEY_EXTRA_WAYPOINT_NAME);

        toolBar = (Toolbar) findViewById(R.id.toolbar);

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

        arrow = (ImageView) findViewById(R.id.arrow);
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
        textViewGetTimeAbbr = (TextView) findViewById(R.id.textViewGetTimeAbbr);

        // GET WAYPOINT DATA FROM DATABASE
        dbHelper = new BoatLogDBHelper(this);

        Cursor rs = dbHelper.getWaypoint(waypointID);
        rs.moveToFirst();
        final String waypointName = rs.getString(rs.getColumnIndex(BoatLogDBHelper.WAYPOINT_COLUMN_NAME));
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

        arrow.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
        imageViewAccu.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.primary)));
        SharedPreferences myPrefs = this.getSharedPreferences("myPrefs", MODE_PRIVATE);
        Boolean ScreenOn = myPrefs.getBoolean("switch2", false);
        final Boolean NightModeOn = myPrefs.getBoolean("switch1", false);
        if (NightModeOn) {
            arrow.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
            imageViewAccu.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.card_background)));
        }



        if (ScreenOn) {
            screenOn();
        }

        // COMPASS SENSOR MANAGER
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        // PERMISSIONS
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }

        // IS GOOGLE PLAY SERVICES AVAILIBLE
        isGooglePlayServicesAvailable();
         // if (!isLocationEnabled())
         //   showAlert();

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

        if (location != null) {
            long locationAge = SystemClock.elapsedRealtimeNanos() - location.getElapsedRealtimeNanos();
            long newLocationAge = locationAge / DEVIDE_NUMBER;
            String locAge = String.valueOf(newLocationAge);
            textViewGetTime.setText("" + locAge);
            textViewGetTimeAbbr.setText(" 10th\\s");

            //GREEN < 1 sec
            if (newLocationAge < INDICATOR1_INTERVAL) {
                updateUI(location);
                imageViewAccu.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.light_green_500)));
            } else
                // AMBER > 1 sec < 3
                if (newLocationAge > INDICATOR1_INTERVAL  && newLocationAge < INDICATOR2_INTERVAL) {
                    updateUI(location);
                    imageViewAccu.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.amber_500)));
                } else
                    // ORANGE < 4 > 3
                    if (newLocationAge < INDICATORFALLBACK_INTERVAL  && newLocationAge > INDICATOR2_INTERVAL) {
                        updateUI(location);
                        imageViewAccu.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.orange_500)));
                    } else
                        // RED > 4 < 7            LAST KNOWN LOCATION CALL
            if (newLocationAge > INDICATORFALLBACK_INTERVAL && newLocationAge < INDICATORNOGPS_INTERVAL) {
                updateUI(location);
                showToast("WARNING !!!!! \nLost Satellites, now using your\nLast Known Location");
                SharedPreferences myPrefs = this.getSharedPreferences("myPrefs", MODE_PRIVATE);
                final Boolean NightModeOn = myPrefs.getBoolean("switch1", false);
                arrow.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.night_text)));
                imageViewAccu.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.night_text)));
                if (NightModeOn) {
                    arrow.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
                }
            } else if (newLocationAge > INDICATORNOGPS_INTERVAL) {
                showToast("WARNING !!!!! \nNO LOCATION FOUND");
                textViewLat.setText("searching");
                textViewLon.setText("for gps");
                textViewDistance.setText("0.0 NM");
                textViewSpeed.setText("0.0 KN");
                textViewHeading.setText("00 T");
                textViewCourseTo.setText("00 T");



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

        if (NightModeOn) {
            arrow.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.night_text)));

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
                result=round(formattedSpeed,1);
                System.out.println(result);
                textViewSpeed.setText("" + result + " KN");
            }

        if (!location.hasSpeed()) {
            textViewSpeed.setText("" + 0.0f + " Kn");
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

            return "" + String.format(Locale.UK,"%8.5f", latitude);
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

            return "" + String.format(Locale.UK,"%8.5f", longitude);
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

        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(GoToWaypoint.this);
        TypedArray ta = obtainStyledAttributes(new int[]{R.attr.colorTextPrimary});
        Drawable Btn = getResources().getDrawable(R.drawable.ic_location_on_white);
        Btn.setColorFilter(ta.getColor(0, Color.WHITE), PorterDuff.Mode.SRC_ATOP);
        builder.setIcon(Btn);
        ta.recycle();
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

    // COMPASS SENSOR CHANGED
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        // get the angle around the z-axis rotated
        float degree = Math.round(sensorEvent.values[0]);
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

    private void loadToolbarNavDrawer() {
        //set Toolbar
        final android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(4);
        TypedArray ta = obtainStyledAttributes(new int[]{R.attr.colorLightTextPrimary});
        Drawable Btn = getResources().getDrawable(R.drawable.ic_action_menu);
        Btn.setColorFilter(ta.getColor(0, Color.WHITE), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(Btn);
        toolbar.setTitleTextColor(ta.getColor(0, Color.WHITE));
        getSupportActionBar().setTitle("GoTo");
        ta.recycle();

        //set NavigationDrawer
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        if (navigationView != null) {

            SharedPreferences myPrefs = this.getSharedPreferences("myPrefs", MODE_PRIVATE);
            Boolean switch1 = myPrefs.getBoolean("switch1", false);
            Boolean switch2 = myPrefs.getBoolean("switch2", false);

            setupDrawerContent(navigationView);
            Menu menu = navigationView.getMenu();

            MenuItem nightSw = menu.findItem(R.id.nav_night_switch);
            View actionViewNightSw = MenuItemCompat.getActionView(nightSw);

            switcher1 = (SwitchCompat) actionViewNightSw.findViewById(R.id.switcher1);
            switcher1.setChecked(switch1);
            switcher1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if ((switcher1.isChecked())) {
                        SharedPreferences myPrefss = PreferenceManager.getDefaultSharedPreferences(GoToWaypoint.this);
                        SharedPreferences.Editor myPref = myPrefss.edit();
                        myPref.putString("theme", "dark");
                        myPref.apply();

                        SharedPreferences myPrefs = GoToWaypoint.this.getSharedPreferences("myPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor myPrefse = myPrefs.edit();
                        myPrefse.putBoolean("switch1", true);
                        myPrefse.apply();
                    } else {
                        SharedPreferences myPrefss = PreferenceManager.getDefaultSharedPreferences(GoToWaypoint.this);
                        SharedPreferences.Editor myPref = myPrefss.edit();
                        myPref.putString("theme", "fresh");
                        myPref.apply();

                        SharedPreferences myPrefs = GoToWaypoint.this.getSharedPreferences("myPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor myPrefse = myPrefs.edit();
                        myPrefse.putBoolean("switch1", false);
                        myPrefse.apply();

                    }
                    // Restart app to load day/night modes
                    Intent intent = new Intent(GoToWaypoint.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    Bundle bndlanimation =
                            ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.anni1, R.anim.anni2).toBundle();
                    startActivity(intent, bndlanimation);
                    startActivity(intent);

                    Snackbar.make(v, (switcher1.isChecked()) ? "Night Mode is now On" : "Night Mode is now Off", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                }
            });

            MenuItem screenOnSw = menu.findItem(R.id.nav_screen_on_switch);
            View actionViewScreenOnSw = MenuItemCompat.getActionView(screenOnSw);

            switcher2 = (SwitchCompat) actionViewScreenOnSw.findViewById(R.id.switcher1);
            switcher2.setChecked(switch2);
            switcher2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if ((switcher2.isChecked())) {
                        SharedPreferences myPrefs = GoToWaypoint.this.getSharedPreferences("myPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor myPrefse = myPrefs.edit();
                        myPrefse.putBoolean("switch2", true);
                        myPrefse.apply();
                    } else {
                        SharedPreferences myPrefs = GoToWaypoint.this.getSharedPreferences("myPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor myPrefse = myPrefs.edit();
                        myPrefse.putBoolean("switch2", false);
                        myPrefse.apply();
                    }


                    Snackbar.make(v, (switcher2.isChecked()) ? "Screen Wake is now On" : "Screen Wake is now Off", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                }
            });

        }
    }

    //navigationDrawerIcon Onclick
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                mDrawerLayout.openDrawer(GravityCompat.START);

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //set NavigationDrawerContent
    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        mDrawerLayout.closeDrawers();
                        menuItem.setChecked(false);
                        Bundle bndlanimation =
                                ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.anni1, R.anim.anni2).toBundle();
                        int id = menuItem.getItemId();
                        switch (id) {
                            case R.id.nav_home:
                                mDrawerLayout.closeDrawers();
                                getSupportActionBar().setElevation(0);
                                mDrawerLayout.closeDrawers();
                                break;

                            case R.id.nav_night_switch:
                                // Toast.makeText(WeatherMainActivity.this, "Night Mode" , Toast.LENGTH_LONG).show();
                                break;

                            case R.id.nav_screen_on_switch:
                                // Toast.makeText(WeatherMainActivity.this, "Screen on Mode" , Toast.LENGTH_LONG).show();
                                break;

                            case R.id.nav_tutorial:
                                Intent tutorial = new Intent(GoToWaypoint.this, Tutorial.class);
                                startActivity(tutorial, bndlanimation);
                                break;

                            case R.id.nav_about:
                                Intent about = new Intent(GoToWaypoint.this, AboutActivity.class);
                                startActivity(about, bndlanimation);
                                break;

                            case R.id.nav_settings:
                                Intent settings = new Intent(GoToWaypoint.this, SettingActivity.class);
                                startActivity(settings, bndlanimation);
                                break;

                        }
                        return false;
                    }
                });

    }

    private int getTheme(String themePref) {
        switch (themePref) {
            case "dark":
                return R.style.AppTheme_NoActionBar_Dark;
            default:
                return R.style.AppTheme_NoActionBar;
        }
    }


    // SCREEN WAKELOCK
    private void screenOn() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    }

    // COMPASS REQUEST UPDATES AT STARTUP
    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_GAME);
       // requestLocationUpdates(easyLocationRequest);

    }

    // COMPASS UNREGESTER LISTENER
    @Override
    protected void onPause() {
        super.onPause();
        //stopLocationUpdates();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onBackPressed() {
        stopLocationUpdates();
        mSensorManager.unregisterListener(this);
        super.onBackPressed();
        overridePendingTransition(R.anim.back2, R.anim.back1);
    }

}

