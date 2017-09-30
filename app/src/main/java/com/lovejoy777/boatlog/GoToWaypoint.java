package com.lovejoy777.boatlog;

import android.Manifest;
import android.app.ProgressDialog;
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
import android.os.AsyncTask;
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

    // GPS location
    private LocationManager locationManager;
    private String provider;

    private float currentDegree = 0f;
    private SensorManager mSensorManager;
    // record the arrow drawable angle turned
    private float currentDegreeNeedle = 0f;

    Toolbar toolBar;
    TextView titleTextView;

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
    ImageView imageViewAccu;

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

        // GET WAYPOINT DATA FROM DATABASE
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

        // PREFILL TEXT FIELDS
        textViewGoTo.setText(waypointName + " @ " + stringlatdms + ", " + stringlongdms);
        textViewLat.setText("No GPS");
        textViewLon.setText("No GPS");
        textViewDistance.setText("0 NM");
        textViewSpeed.setText("0 KN");
        textViewHeading.setText("00 T");
        textViewCourseTo.setText("00 T");
        //imageViewAccu.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.night_text)));
        //textViewAccu.setText("bad");


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


            //  location.

            onLocationChanged(location);



        } else {
            Toast.makeText(getApplicationContext(), "Waiting for GPS ", Toast.LENGTH_LONG).show();
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


        float accu = location.getAccuracy();
        int intaccu = (int) accu;

        if (intaccu > 25) {
            location.reset();
            //BackgroundTaskGpsSignal taskGpsSig = new BackgroundTaskGpsSignal(GoToWaypoint.this);
            //taskGpsSig.execute();
            imageViewAccu.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.card_background)));
            //Toast.makeText(getApplicationContext(), "Waiting for GPS ", Toast.LENGTH_LONG).show();
            SharedPreferences myPrefs = this.getSharedPreferences("myPrefs", MODE_PRIVATE);
            Boolean NightModeOn = myPrefs.getBoolean("switch1", false);
            imageViewAccu.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.primary)));
            if (NightModeOn) {
                imageViewAccu.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.card_background)));
            }
        }
        if (intaccu < 25) {

            SharedPreferences myPrefs = this.getSharedPreferences("myPrefs", MODE_PRIVATE);
            Boolean NightModeOn = myPrefs.getBoolean("switch1", false);
            imageViewAccu.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
            if (NightModeOn) {
                imageViewAccu.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.night_text)));
            }
            //imageViewAccu.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
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

            // SPEED
            if (location.hasSpeed()) {

                float formattedSpeed = FormattedSpeed(location.getSpeed());
                textViewSpeed.setText("" + formattedSpeed + " KN");

                //arrow.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.primary)));

            }
            // process data
            if (!location.hasSpeed()) {
                // arrow.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.night_text)));
                textViewSpeed.setText("0.0 KN");
                textViewHeading.setText("00 T");
                //arrow.setRotation(0);
            }

            // CURRENT TRACK OVER GROUND
            float heading = location.getBearing();
            int intheading = (int) heading;
            if (intheading < 0) {
                intheading = intheading + 360;
            }
            // FILL TEXTVIEW TRACK OVER GROUND
            String stringHeading = String.valueOf(intheading);
            textViewHeading.setText(stringHeading + " T");

            // BEARING TO WAYPOINT
            float BearTo = location.bearingTo(locationB);
            int intBearTo = (int) BearTo;
            if (intBearTo < 0) {
                intBearTo = intBearTo + 360;
            }

            // FILL TEXTVIEW BEARING TO
            String stringBearTo = String.valueOf(intBearTo);
            textViewCourseTo.setText(stringBearTo + (" T"));
            // If the bearTo is smaller than 0, add 360 to get the rotation clockwise.
            //This is where we choose to point it
            //float direction = (BearTo - heading) - 360;
            // If the direction is smaller than 0, add 360 to get the rotation clockwise.

            // GET POINTER ANGLE
            if (heading < 0) {
                heading = 360 + heading;
            }
            if (BearTo < 0) {
                BearTo = 360 + BearTo;
            }
            float direction = (BearTo - heading) - 360;


            // ROTATE POINTER
            // create a rotation animation (reverse turn degree degrees)
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
            // Toast.makeText(getApplicationContext(), "accuracy " + accu, Toast.LENGTH_SHORT).show();
        }


        // CURRENT POSITION



    }

    // CREATE PDF WITH IMAGE BACKGROUND TASK WITH PROGRESS SPINNER
    private class BackgroundTaskGpsSignal extends AsyncTask<Void, Void, Void> {
        private ProgressDialog dialog;

        public BackgroundTaskGpsSignal(GoToWaypoint activity) {
            dialog = new ProgressDialog(activity, R.style.StyledDialog);
        }

        @Override
        protected void onPreExecute() {
            dialog.setTitle("Weak or no Gps");
            dialog.setMessage("please wait");
            dialog.setIcon(R.drawable.ic_gps_fixed);
            dialog.show();
        }

        @Override
        protected void onPostExecute(Void result) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }

        @Override
        protected Void doInBackground(Void... params) {


            return null;
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

        // COMPASS
        // get the angle around the z-axis rotated
        float degree = Math.round(event.values[0]);
        String result = "0";
        if (degree == Math.floor(degree)) {
            result = Integer.toString((int) degree);
        } else {
            result = Float.toString(degree);
        }

        textViewCompass.setText("" + result + " M");

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

        imageViewAccu.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.night_text)));
        arrow.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.night_text)));

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.back2, R.anim.back1);
        locationManager.removeUpdates(this);
        // to stop the listener and save battery
        mSensorManager.unregisterListener(this);
    }

}

