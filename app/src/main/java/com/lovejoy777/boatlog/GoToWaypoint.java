package com.lovejoy777.boatlog;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by lovejoy777 on 14/10/15.
 */
public class GoToWaypoint extends AppCompatActivity implements LocationListener, SensorEventListener {

    // GPS location
    private LocationManager locationManager;
    private String provider;

    // compass heading
    private float currentDegree = 0f;
    private SensorManager mSensorManager;

    Toolbar toolBar;
    TextView titleTextView;

    LinearLayout MLL1;
    LinearLayout MLL2;
    LinearLayout MLL3;

    //textViews
    TextView textViewDest;
    TextView textViewSped;
    TextView textViewHead;
    TextView textViewComp;

    TextView textViewLat;
    TextView textViewLon;
    TextView textViewSpeed;
    TextView textViewHeading;
    TextView textViewCompass;
    TextView textViewCourseTo;
    TextView textViewDistance;
    TextView textViewGoTo;

    ImageView image;

    int waypointID;
    String waypointName;
    String waypointLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_goto);

        waypointID = getIntent().getIntExtra(MainActivityWaypoint.KEY_EXTRA_WAYPOINT_ID, 0);
        waypointName = getIntent().getStringExtra(MainActivityWaypoint.KEY_EXTRA_WAYPOINT_NAME);
        waypointLocation = getIntent().getStringExtra(MainActivityWaypoint.KEY_EXTRA_WAYPOINT_LOCATION);
        // assign the views

        String newway = waypointLocation;

       // float degpos = convertToDegree(waypointLocation);
       // double dec = DMSToDecimal("W", 79, 58, 55);
        Toast.makeText(getApplicationContext(), "" + newway, Toast.LENGTH_LONG).show();



        toolBar = (Toolbar) findViewById(R.id.toolbar);
        titleTextView = (TextView) findViewById(R.id.titleTextView);

        MLL1 = (LinearLayout) findViewById(R.id.MLL1);
        MLL2 = (LinearLayout) findViewById(R.id.MLL2);
        MLL3 = (LinearLayout) findViewById(R.id.MLL3);

        textViewLat = (TextView) findViewById(R.id.textViewLat);
        textViewLon = (TextView) findViewById(R.id.textViewLon);
        textViewSpeed = (TextView) findViewById(R.id.textViewSpeed);
        textViewHeading = (TextView) findViewById(R.id.textViewHeading);
        textViewCompass = (TextView) findViewById(R.id.textViewCompass);
        textViewDest = (TextView) findViewById(R.id.textViewDest);
        textViewSped = (TextView) findViewById(R.id.textViewSped);
        textViewHead = (TextView) findViewById(R.id.textViewHead);
        textViewComp = (TextView) findViewById(R.id.textViewComp);
        textViewDistance = (TextView) findViewById(R.id.textViewDistance);
        textViewCourseTo = (TextView) findViewById(R.id.textViewCourseTo);
        textViewGoTo = (TextView) findViewById(R.id.textViewGoTo);
        image = (ImageView) findViewById(R.id.imageViewCompass);

        textViewGoTo.setText("Heading To " + waypointName + " at " + waypointLocation);


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
        Location location = locationManager.getLastKnownLocation(provider);
        // Initialize the location fields
        if (location != null) {
            System.out.println("Provider " + provider + " has been selected.");
            onLocationChanged(location);
        } else {
            Toast.makeText(getApplicationContext(), "Lat Long unavailable ", Toast.LENGTH_SHORT).show();
        }
        //  Toast.makeText(getApplicationContext(), "" + formattedLocation, Toast.LENGTH_SHORT).show();
    }

    private void NightMode() {


        toolBar.setBackgroundColor(Color.BLACK);
        titleTextView.setTextColor(Color.RED);

        MLL1.setBackgroundColor(Color.BLACK);
        MLL2.setBackgroundColor(Color.BLACK);
        MLL3.setBackgroundColor(Color.BLACK);

        textViewLat.setTextColor(Color.RED);
        textViewLon.setTextColor(Color.RED);
        textViewSpeed.setTextColor(Color.RED);
        textViewHeading.setTextColor(Color.RED);
        textViewCompass.setTextColor(Color.RED);

        textViewDest.setTextColor(Color.RED);
        textViewSped.setTextColor(Color.RED);
        textViewHead.setTextColor(Color.RED);
        textViewComp.setTextColor(Color.RED);
        image.setImageResource(R.drawable.compassred);

        // Toast.makeText(MainActivityLog.this, "Night Mode", Toast.LENGTH_LONG).show();

    }


    private void screenOn() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        // Toast.makeText(MainActivityLog.this, "Night Mode", Toast.LENGTH_LONG).show();

    }

    public double DMSToDecimal(double degres,double minutes,double secondes, String hemisphereOUmeridien)
    {
        double LatOrLon=0;
        double signe=1.0;

        if((hemisphereOUmeridien.equals("W"))||(hemisphereOUmeridien.equals("S"))) {signe=-1.0;}
        LatOrLon = signe*(Math.floor(degres) + Math.floor(minutes)/60.0 + secondes/3600.0);

        return(LatOrLon);
    }




    public static String FormattedLocationDMSLat(double latitude) {
        try {
            int latSeconds = (int) Math.round(latitude * 3600);
            int latDegrees = latSeconds / 3600;
            latSeconds = Math.abs(latSeconds % 3600);
            int latMinutes = latSeconds / 60;
            latSeconds %= 60;


            String latDegree = latDegrees >= 0 ? "N" : "S";

            return Math.abs(latDegrees) + ":" + latMinutes + ":" + latSeconds
                    + " " + latDegree;
        } catch (Exception e) {

            return "" + String.format("%8.5f", latitude);
        }
    }


    public static String FormattedLocationDMSLon(double longitude) {
        try {
            int longSeconds = (int) Math.round(longitude * 3600);
            int longDegrees = longSeconds / 3600;
            longSeconds = Math.abs(longSeconds % 3600);
            int longMinutes = longSeconds / 60;
            longSeconds %= 60;
            String lonDegrees = longDegrees >= 0 ? "W" : "E";

            return Math.abs(longDegrees) + ":" + longMinutes
                    + ":" + longSeconds + ":" + lonDegrees;
        } catch (Exception e) {

            return "" + String.format("%8.5f", longitude);
        }
    }

    // convert from meters per second to knots per hour
    public static float FormattedSpeed(float mps) {
        int mpsSped = (int) Math.abs(mps * 1.943844f);
        return Math.abs(mpsSped);
    }

    /* Request updates at startup */
    @Override
    protected void onResume() {
        super.onResume();
        locationManager.requestLocationUpdates(provider, 500, 1, this);
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_GAME);
    }

    /* Remove the locationlistener updates when Activity is paused */
    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);
        // to stop the listener and save battery
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onLocationChanged(Location location) {






        String formattedLocationLat = FormattedLocationDMSLat(location.getLatitude());
        String formattedLocationLon = FormattedLocationDMSLon(location.getLongitude());
        //float formattedSpeed = FormattedSpeed(location.getSpeed());
        float degree = location.getBearing();


        textViewLat.setText("" + formattedLocationLat);
        textViewLon.setText("" + formattedLocationLon);

        textViewHeading.setText("" + degree + "     (T)");

        if (location.hasSpeed()) {
            float formattedSpeed = FormattedSpeed(location.getSpeed());
            textViewSpeed.setText("" + formattedSpeed + "    (Kn)");
            // process data
        }
        if (!location.hasSpeed()) {
            textViewSpeed.setText("" + 0.0f + "     ns(Kn)");
            // Speed information not available.

        }
        //Toast.makeText(this, "" + speedMeters + "     mps", Toast.LENGTH_LONG).show();
    }

    private Float convertToDegree(String stringDMS){
        Float result = null;
        String[] DMS = stringDMS.split(":", 3);


        Toast.makeText(this, "" + DMS, Toast.LENGTH_LONG).show();

     /**   String[] stringD = DMS[0].split("/", 2);
        Double D0 = new Double(stringD[0]);
        Double D1 = new Double(stringD[1]);
        Double FloatD = D0/D1;

        String[] stringM = DMS[1].split("/", 2);
        Double M0 = new Double(stringM[0]);
        Double M1 = new Double(stringM[1]);
        Double FloatM = M0/M1;

        String[] stringS = DMS[2].split("/", 2);
        Double S0 = new Double(stringS[0]);
        Double S1 = new Double(stringS[1]);
        Double FloatS = S0/S1;

      */
       // result = new Float(FloatD + (FloatM/60) + (FloatS/3600));

        return result;


    };


    private void Speedgt(Location location) {

        if (location.hasSpeed()) {
            float formattedSpeed = FormattedSpeed(location.getSpeed());
            textViewSpeed.setText("" + formattedSpeed + "    (Kn)");
            // process data
        }
        if (!location.hasSpeed()) {
            textViewSpeed.setText("0.0f    (Kn)");
            // Speed information not available.

        }


        // Toast.makeText(MainActivityLog.this, "Day Mode", Toast.LENGTH_LONG).show();


    }

    private void Speedlt() {

        textViewSpeed.setText("0.0    (Kn)");

        // Toast.makeText(MainActivityLog.this, "Day Mode", Toast.LENGTH_LONG).show();


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

        textViewCompass.setText("" + Float.toString(degree) + "     (M)");

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
        // image.startAnimation(ra);
        currentDegree = -degree;

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // not in use
    }
}
