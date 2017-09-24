package com.lovejoy777.boatlog;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import static java.lang.Math.abs;
import static java.lang.Math.floor;

/**
 * Created by lovejoy777 on 14/10/15.
 */
public class GoToWaypoint extends AppCompatActivity implements LocationListener, SensorEventListener {

    private BoatLogDBHelper dbHelper;

    public static ImageView arrow;

    // record the compass picture angle turned
    private float currentDegreeNeedle = 0f;

    // GPS location
    private LocationManager locationManager;
    private String provider;

    // compass heading
    private float currentDegree = 0f;
    private SensorManager mSensorManager;

    Toolbar toolBar;
    TextView titleTextView;

    RelativeLayout MainRL;
    LinearLayout MLL1;
    LinearLayout MLL2;
    LinearLayout MLL3;

    //textViews
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

    int waypointID;
    String waypointName;
    double doublelat;
    double doublelong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_goto);

        waypointID = getIntent().getIntExtra(MainActivityWaypoint.KEY_EXTRA_WAYPOINT_ID, 0);
        waypointName = getIntent().getStringExtra(MainActivityWaypoint.KEY_EXTRA_WAYPOINT_NAME);

        // assign the views
        toolBar = (Toolbar) findViewById(R.id.toolbar);
        titleTextView = (TextView) findViewById(R.id.titleTextView);

        MainRL = (RelativeLayout) findViewById(R.id.MainRL);
        MLL1 = (LinearLayout) findViewById(R.id.MLL1);
        MLL2 = (LinearLayout) findViewById(R.id.MLL2);
        MLL3 = (LinearLayout) findViewById(R.id.MLL3);
        arrow = (ImageView) findViewById(R.id.needle);

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

        textViewGoTo = (TextView) findViewById(R.id.textViewGoTo);

        arrow.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.primary)));

        dbHelper = new BoatLogDBHelper(this);

        Cursor rs = dbHelper.getWaypoint(waypointID);
        rs.moveToFirst();
        final String waypointName = rs.getString(rs.getColumnIndex(BoatLogDBHelper.WAYPOINT_COLUMN_NAME));
        String waypointDescription = rs.getString(rs.getColumnIndex(BoatLogDBHelper.WAYPOINT_COLUMN_DESCRIPTION));
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

        textViewGoTo.setText(waypointName + " @ " + stringlatdms + ", " + stringlongdms);

        SharedPreferences myPrefs = this.getSharedPreferences("myPrefs", MODE_PRIVATE);
        Boolean NightModeOn = myPrefs.getBoolean("switch1", false);

        if (NightModeOn) {
            NightMode();
        }

        Boolean ScreenOn = myPrefs.getBoolean("switch2", false);

        if (ScreenOn) {
            screenOn();
        }

        // initialize your android device sensor capabilities
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

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
    }


    private void screenOn() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    }

    public double DMSToDecimal(double degres, double minutes, double secondes, String hemisphereOUmeridien) {
        double LatOrLon = 0;
        double signe = 1.0;

        if ((hemisphereOUmeridien.equals("W")) || (hemisphereOUmeridien.equals("S"))) {
            signe = -1.0;
        }
        LatOrLon = signe * (floor(degres) + floor(minutes) / 60.0 + secondes / 3600.0);

        return (LatOrLon);
    }

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

    // convert from meters per second to knots per hour
    public static float FormattedSpeed(float mps) {
        int mpsSped = (int) abs(mps * 1.943844f);
        return abs(mpsSped);
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
        locationManager.requestLocationUpdates(provider, 500, 1, this);
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_GAME);
    }

    /* Remove the locationlistener updates when Activity is paused */
    @Override
    protected void onPause() {
        super.onPause();
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
        locationManager.removeUpdates(this);
        // to stop the listener and save battery
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onLocationChanged(Location location) {

        // CURRENT POSITION
        textViewLat.setText(FormattedLocationLat(location.getLatitude()));
        textViewLon.setText(FormattedLocationLon(location.getLongitude()));


        // SPEED
        if (location.hasSpeed()) {
            float formattedSpeed = FormattedSpeed(location.getSpeed());
            textViewSpeed.setText("" + formattedSpeed + " KN");

            // process data
        }
        if (!location.hasSpeed()) {
            textViewSpeed.setText("0.0 KN");

        }

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

        // BEARING TO WAYPOINT
        float floatBearTo = location.bearingTo(locationB);
        int intBearTo = (int) floatBearTo;
        if (intBearTo < 0) {
            intBearTo = intBearTo + 360;
            //bearTo = -100 + 360  = 260;
        }
        String stringBearTo = String.valueOf(intBearTo);
        textViewCourseTo.setText(stringBearTo + (" T"));

        // CURRENT HEADING OVER GROUND
        float degree = location.getBearing();
        if (degree < 0) {
            degree = degree + 360;
            //bearTo = -100 + 360  = 260;
        }

        //float direction = degree - floatBearTo;

        textViewHeading.setText("" + degree + " T");

        if (degree > 0) {
            // create a rotation animation (reverse turn degree degrees)
            RotateAnimation ra = new RotateAnimation(
                    degree, // fromDegrees
                    floatBearTo, // toDegress
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f);

            // how long the animation will take place
            ra.setDuration(210);

            // set the animation after the end of the reservation status
            ra.setFillAfter(true);

            // Start the animation

            arrow.startAnimation(ra);
            degree = floatBearTo;
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

    @Override
    public void onSensorChanged(SensorEvent event) {

        // get the angle around the z-axis rotated
        float degree = Math.round(event.values[0]);


        String result = "0";
        if (degree == Math.floor(degree)) {
            result = Integer.toString((int) degree);
        } else {
            result = Float.toString(degree);
        }

        textViewCompass.setText("" + result + " M");

        // create a rotation animation (reverse turn degree degrees)
        RotateAnimation ra = new RotateAnimation(
                currentDegree,
                -degree,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f);

        // how long the animation will take place
        ra.setDuration(210);

        // set the animation after the end of the reservation status
        ra.setFillAfter(true);

        // Start the animation
        currentDegree = -degree;

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // not in use
    }

    private void NightMode() {

        toolBar.setBackgroundColor(getResources().getColor(R.color.card_background));
        MainRL.setBackgroundColor(getResources().getColor(R.color.card_background));
        MLL1.setBackgroundColor(getResources().getColor(R.color.card_background));
        MLL2.setBackgroundColor(getResources().getColor(R.color.card_background));
        MLL3.setBackgroundColor(getResources().getColor(R.color.card_background));

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

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.back2, R.anim.back1);
    }
}
