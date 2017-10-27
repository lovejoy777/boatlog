package com.lovejoy777.boatlog;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.akhgupta.easylocation.EasyLocationAppCompatActivity;
import com.akhgupta.easylocation.EasyLocationRequest;
import com.akhgupta.easylocation.EasyLocationRequestBuilder;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.lovejoy777.boatlog.activities.AboutActivity;
import com.lovejoy777.boatlog.activities.SettingsActivity;

import java.math.BigDecimal;
import java.util.Locale;

import static java.lang.Math.abs;


/**
 * Created by lovejoy777 on 07/10/15.
 */
public class MainActivityLog extends EasyLocationAppCompatActivity implements OnMapReadyCallback, SensorEventListener {

    // NAVIGATION DRAWER
    private DrawerLayout mDrawerLayout;
    private SwitchCompat switcher1, switcher2;

    // MAPS
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    final String TAG = "GPS";
    private GoogleMap mMap;
    long UPDATE_INTERVAL = 2 * 1000;  // 10 secs?
    long FASTEST_INTERVAL = 2000; // 2 sec
    long FALLBACK_INTERVAL = 4000; // 7 seconds
    // INDICATOR VALUES
    long INDICATOR1_INTERVAL = 15; // 1 seconds
    long INDICATOR2_INTERVAL = 40; // 3 seconds
    long INDICATORFALLBACK_INTERVAL = 400; // 4 seconds
    long INDICATORNOGPS_INTERVAL = 1000; // 7 seconds
    long DEVIDE_NUMBER = 10000000; //nano to tenths

    // ZOOM SEEKBAR
    private SeekBar zoomSeekbar;
    ImageView zoomBtn;
    View thumbView;

    // COMPASS
    private SensorManager mSensorManager;

    // TOOLBAR & TITLE TEXT VIEW
    Toolbar toolBar;
    TextView titleTextView;

    // MAIN LAYOUTS
    LinearLayout MLL1;
    LinearLayout MLL2;
    LinearLayout MLL3;

    // GRID OUTLINE LAYOUTS
    LinearLayout LLG1;
    LinearLayout LLG2;
    LinearLayout LLG3;
    LinearLayout LLG4;
    LinearLayout LLG5;
    LinearLayout LLG6;

    // TEXT VIEWS
    TextView textViewPos;
    TextView textViewSped;
    TextView textViewHead;
    TextView textViewComp;

    TextView textViewLat;
    TextView textViewLon;
    TextView textViewSpeed;
    TextView textViewHeading;
    TextView textViewCompass;
    TextView textViewGetTime;

    // GPS INDICATOR
    ImageView imageViewAccu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_logs);

        loadToolbarNavDrawer();

        toolBar = (Toolbar) findViewById(R.id.toolbar);
        titleTextView = (TextView) findViewById(R.id.titleTextView);

        // MAPS ZOOM CONTROLS
        zoomBtn = (ImageView) findViewById(R.id.zoomBtn);
        zoomSeekbar = (SeekBar) findViewById(R.id.zoomSeekbar);
        zoomSeekbar.setOnSeekBarChangeListener(zoomSeekbarOnSeekBarChangeListener);
        thumbView = LayoutInflater.from(MainActivityLog.this).inflate(R.layout.layout_seekbar_thumb, null, false);
        zoomSeekbar.setVisibility(View.INVISIBLE);
        zoomBtn.setVisibility(View.VISIBLE);
        zoomBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (zoomSeekbar.getVisibility() == View.VISIBLE) {
                    zoomSeekbar.setVisibility(View.INVISIBLE);
                } else {
                    int intZoomValue = getzoomValue();
                    zoomSeekbar.setProgress(intZoomValue);
                    zoomSeekbar.setThumb(getThumb(intZoomValue));
                    zoomSeekbar.setVisibility(View.VISIBLE);
                }
            }
        });

        MLL1 = (LinearLayout) findViewById(R.id.MLL1);
        MLL2 = (LinearLayout) findViewById(R.id.MLL2);
        MLL3 = (LinearLayout) findViewById(R.id.MLL3);

        LLG1 = (LinearLayout) findViewById(R.id.LLG1);
        LLG2 = (LinearLayout) findViewById(R.id.LLG2);
        LLG3 = (LinearLayout) findViewById(R.id.LLG3);
        LLG4 = (LinearLayout) findViewById(R.id.LLG4);
        LLG5 = (LinearLayout) findViewById(R.id.LLG5);
        LLG6 = (LinearLayout) findViewById(R.id.LLG6);

        textViewLat = (TextView) findViewById(R.id.textViewLat);
        textViewLon = (TextView) findViewById(R.id.textViewLon);
        textViewSpeed = (TextView) findViewById(R.id.textViewSpeed);
        textViewHeading = (TextView) findViewById(R.id.textViewHeading);
        textViewCompass = (TextView) findViewById(R.id.textViewCompass);
        textViewPos = (TextView) findViewById(R.id.textViewPos);
        textViewSped = (TextView) findViewById(R.id.textViewSped);
        textViewHead = (TextView) findViewById(R.id.textViewHead);
        textViewComp = (TextView) findViewById(R.id.textViewComp);

        textViewGetTime = (TextView) findViewById(R.id.textViewGetTime);
        imageViewAccu = (ImageView) findViewById(R.id.imageViewAccu);
        imageViewAccu.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.card_background)));

        // NIGHT MODE CALL
        SharedPreferences myPrefs = this.getSharedPreferences("myPrefs", MODE_PRIVATE);
        final Boolean NightModeOn = myPrefs.getBoolean("switch1", false);

        if (NightModeOn) {
            NightMode();
        }

        // SCREEN WAKELOCK
        Boolean ScreenOn = myPrefs.getBoolean("switch2", false);
        if (ScreenOn) {
            screenOn();
        }

        // COMPASS initialize your android device sensor capabilities
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        // RUNTIME PERMISSIONS CHECK
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }

        // IS GOOGLE SERVICES AVAILIBLE
        isGooglePlayServicesAvailable();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    // GET ZOOM VALUE FROM SHARED PREFS
    public int getzoomValue() {
        SharedPreferences myPrefs = this.getSharedPreferences("myPrefs", MODE_PRIVATE);
        final int gotZoomValue = myPrefs.getInt("zoomValue", 18);
        return gotZoomValue;
    }

    // THUMB IS USED FOR THE PROGRESS TEXT BACKGROUND
    public Drawable getThumb(int progress) {
        ((TextView) thumbView.findViewById(R.id.tvProgress)).setText("" + progress + "");
        thumbView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        Bitmap bitmap = Bitmap.createBitmap(thumbView.getMeasuredWidth(), thumbView.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        thumbView.layout(0, 0, thumbView.getMeasuredWidth(), thumbView.getMeasuredHeight());
        thumbView.draw(canvas);
        return new BitmapDrawable(getResources(), bitmap);
    }

    // ZOOM SEEKBAR CHANGED LISTENER
    private final SeekBar.OnSeekBarChangeListener zoomSeekbarOnSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(final SeekBar seekBar, final int progress, final boolean fromUser) {
            if (fromUser) {
                seekBar.setThumb(getThumb(progress));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(seekBar.getProgress()), 500, null);
            }
        }
        @Override
        public void onStartTrackingTouch(final SeekBar seekBar) {
        }
        @Override
        public void onStopTrackingTouch(final SeekBar seekBar) {
            SharedPreferences myPrefs = MainActivityLog.this.getSharedPreferences("myPrefs", MODE_PRIVATE);
            SharedPreferences.Editor myPrefse = myPrefs.edit();
            myPrefse.putInt("zoomValue", seekBar.getProgress());
            myPrefse.apply();
            zoomSeekbar.setVisibility(View.INVISIBLE);
        }
    };

    // GOOGLE MAPS API METHODS
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildEasyLocationClient();
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setCompassEnabled(true);

            }
        } else {
            buildEasyLocationClient();
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setCompassEnabled(true);
        }
    }

    // EASYLOCATION METHODS
    protected synchronized void buildEasyLocationClient() {
       // Toast.makeText(this, "Loading Map", Toast.LENGTH_SHORT).show();
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
            textViewGetTime.setText(locAge);

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
                imageViewAccu.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.night_text)));
            } else
                // NONE > 7              NO LOCATION
                if (newLocationAge > INDICATORNOGPS_INTERVAL) {
                showToast("WARNING !!!!! \nNO LOCATION FOUND");
                textViewLat.setText("searching");
                textViewLon.setText("for gps");
                textViewSpeed.setText("0.0 KN");
                textViewHeading.setText("00 T");
                // NO INDICATOR
                SharedPreferences myPrefs = this.getSharedPreferences("myPrefs", MODE_PRIVATE);
                final Boolean NightModeOn = myPrefs.getBoolean("switch1", false);
                imageViewAccu.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.primary)));
                if (NightModeOn) {
                    imageViewAccu.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.card_background)));
                }
            }
        }
    }

    private void updateUI(Location location) {
        Log.d(TAG, "updateUI");

        SharedPreferences myPrefs = this.getSharedPreferences("myPrefs", MODE_PRIVATE);
        int zoomValue = myPrefs.getInt("zoomValue", 18);

        // GET LAT LONG STRINGS
        String formattedLocationLat = FormattedLocationLat(location.getLatitude());
        String formattedLocationLon = FormattedLocationLon(location.getLongitude());

        // FILL LAT LONG TEXT VIEWS
        textViewLat.setText(formattedLocationLat);
        textViewLon.setText(formattedLocationLon);

        // CURRENT TRACK OVER GROUND
        float heading = location.getBearing();
        int intheading = (int) heading;
        if (intheading < 0) {
            intheading = intheading + 360;
        }
        // FILL TEXTVIEW TRACK OVER GROUND
        String stringHeading = String.valueOf(intheading);
        textViewHeading.setText("" + stringHeading + " T");

        // MOVE & POSITION THE CAMERA
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(zoomValue).bearing(heading).build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        // SPEED OVER GROUND
        if (location.hasSpeed()) {
            float formattedSpeed = FormattedSpeed(location.getSpeed());
            BigDecimal result;
            result=round(formattedSpeed,1);
            System.out.println("SPEED IS" + result);
            textViewSpeed.setText("" + result + " KN");
        }
        if (!location.hasSpeed()) {
            textViewSpeed.setText("" + 0.0f + " Kn");
        }
    }

    public static BigDecimal round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd;
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

    // CONVERT FROM METERS PER SECOND TO KNOTS PER HOUR
    public static float FormattedSpeed(float mps) {
        float mpsSped = abs(mps * 1.943844f);
        return abs(mpsSped);
    }

    public static String FormattedLocationLat(double latitude) {
        try {
            int latSeconds = (int) Math.round(latitude * 3600);
            int latDegrees = latSeconds / 3600;
            latSeconds = Math.abs(latSeconds % 3600);
            int latMinutes = latSeconds / 60;
            latSeconds %= 60;
            String latDegree = latitude >= 0 ? "N" : "S";
            return Math.abs(latDegrees) + "°" + latMinutes + "'" + latSeconds
                    + "\"" + latDegree;
        } catch (Exception e) {
            return "" + String.format(Locale.UK,"%8.5f", latitude);
        }
    }

    public static String FormattedLocationLon(double longitude) {
        try {
            int longSeconds = (int) Math.round(longitude * 3600);
            int longDegrees = longSeconds / 3600;
            longSeconds = Math.abs(longSeconds % 3600);
            int longMinutes = longSeconds / 60;
            longSeconds %= 60;
            String lonDegrees = longitude >= 0 ? "E" : "W";
            return Math.abs(longDegrees) + "°" + longMinutes
                    + "'" + longSeconds + "\"" + lonDegrees;
        } catch (Exception e) {
            return "" + String.format(Locale.UK,"%8.5f", longitude);
        }
    }

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

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        // get the angle around the z-axis rotated
        float degree = Math.round(sensorEvent.values[0]);
        String result;
        if (degree == Math.floor(degree)) {
            result = Integer.toString((int) degree);
        } else {
            result = Float.toString(degree);
        }
        textViewCompass.setText("" + result + " M");
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        // not in use
    }

    private void loadToolbarNavDrawer() {
        //set Toolbar
        final android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


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
                        SharedPreferences myPrefs = MainActivityLog.this.getSharedPreferences("myPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor myPrefse = myPrefs.edit();
                        myPrefse.putBoolean("switch1", true);
                        myPrefse.apply();
                    } else {
                        SharedPreferences myPrefs = MainActivityLog.this.getSharedPreferences("myPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor myPrefse = myPrefs.edit();
                        myPrefse.putBoolean("switch1", false);
                        myPrefse.apply();
                    }
                    // Restart app to load day/night modes
                    Intent intent = new Intent(MainActivityLog.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    Bundle bndlanimation =
                            ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.anni1, R.anim.anni2).toBundle();
                    startActivity(intent, bndlanimation);
                    startActivity(intent);

                    Snackbar.make(v, (switcher1.isChecked()) ? "Night Mode is now On" : "Night Mode is now Off", Snackbar.LENGTH_LONG).setAction("Action", null).show();
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
                        SharedPreferences myPrefs = MainActivityLog.this.getSharedPreferences("myPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor myPrefse = myPrefs.edit();
                        myPrefse.putBoolean("switch2", true);
                        myPrefse.apply();
                    } else {
                        SharedPreferences myPrefs = MainActivityLog.this.getSharedPreferences("myPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor myPrefse = myPrefs.edit();
                        myPrefse.putBoolean("switch2", false);
                        myPrefse.apply();
                    }


                    Snackbar.make(v, (switcher2.isChecked()) ? "Screen Wake is now On" : "Screen Wake is now Off", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
            });

            SharedPreferences myPrefse = this.getSharedPreferences("myPrefs", MODE_PRIVATE);
            Boolean NightModeOn = myPrefse.getBoolean("switch1", false);

            if (NightModeOn) {
                navigationView.setItemTextColor(ColorStateList.valueOf(getResources().getColor(R.color.night_text)));
                navigationView.setItemIconTintList(ColorStateList.valueOf(getResources().getColor(R.color.night_text)));
                navigationView.setBackgroundColor(getResources().getColor(R.color.card_background));
            } else {
                navigationView.setItemTextColor(ColorStateList.valueOf(Color.DKGRAY));
                navigationView.setItemIconTintList(ColorStateList.valueOf(Color.DKGRAY));
            }
        }
    }

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
                        menuItem.setChecked(true);
                        Bundle bndlanimation =
                                ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.anni1, R.anim.anni2).toBundle();
                        int id = menuItem.getItemId();
                        switch (id) {
                            case R.id.nav_home:
                                // mDrawerLayout.closeDrawers();
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
                                Intent tutorial = new Intent(MainActivityLog.this, Tutorial.class);
                                startActivity(tutorial, bndlanimation);
                                break;

                            case R.id.nav_about:
                                Intent about = new Intent(MainActivityLog.this, AboutActivity.class);
                                startActivity(about, bndlanimation);
                                break;

                            case R.id.nav_settings:
                                Intent settings = new Intent(MainActivityLog.this, SettingsActivity.class);
                                startActivity(settings, bndlanimation);
                                break;

                        }
                        return false;
                    }
                });

    }

    private void NightMode() {

        toolBar.setBackgroundResource(R.color.card_background);
        MLL1.setBackgroundResource(R.color.card_background);
        MLL2.setBackgroundResource(R.color.card_background);
        MLL3.setBackgroundResource(R.color.card_background);

        LLG1.setBackgroundResource(R.color.grid_outline);
        LLG2.setBackgroundResource(R.color.grid_outline);
        LLG3.setBackgroundResource(R.color.grid_outline);
        LLG4.setBackgroundResource(R.color.grid_outline);
        LLG5.setBackgroundResource(R.color.grid_outline);
        LLG6.setBackgroundResource(R.color.grid_outline);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            titleTextView.setTextColor(getBaseContext().getResources().getColor(R.color.night_text, getBaseContext().getTheme()));
            textViewGetTime.setTextColor(getBaseContext().getResources().getColor(R.color.night_text, getBaseContext().getTheme()));
            textViewLat.setTextColor(getBaseContext().getResources().getColor(R.color.night_text, getBaseContext().getTheme()));
            textViewLon.setTextColor(getBaseContext().getResources().getColor(R.color.night_text, getBaseContext().getTheme()));
            textViewSpeed.setTextColor(getBaseContext().getResources().getColor(R.color.night_text, getBaseContext().getTheme()));
            textViewHeading.setTextColor(getBaseContext().getResources().getColor(R.color.night_text, getBaseContext().getTheme()));
            textViewCompass.setTextColor(getBaseContext().getResources().getColor(R.color.night_text, getBaseContext().getTheme()));

            textViewPos.setTextColor(getBaseContext().getResources().getColor(R.color.night_text, getBaseContext().getTheme()));
            textViewSped.setTextColor(getBaseContext().getResources().getColor(R.color.night_text, getBaseContext().getTheme()));
            textViewHead.setTextColor(getBaseContext().getResources().getColor(R.color.night_text, getBaseContext().getTheme()));
            textViewComp.setTextColor(getBaseContext().getResources().getColor(R.color.night_text, getBaseContext().getTheme()));

        }else {
            titleTextView.setTextColor(getResources().getColor(R.color.night_text));
            textViewGetTime.setTextColor(getResources().getColor(R.color.night_text));
            textViewLat.setTextColor(getResources().getColor(R.color.night_text));
            textViewLon.setTextColor(getResources().getColor(R.color.night_text));
            textViewSpeed.setTextColor(getResources().getColor(R.color.night_text));
            textViewHeading.setTextColor(getResources().getColor(R.color.night_text));
            textViewCompass.setTextColor(getResources().getColor(R.color.night_text));

            textViewPos.setTextColor(getResources().getColor(R.color.night_text));
            textViewSped.setTextColor(getResources().getColor(R.color.night_text));
            textViewHead.setTextColor(getResources().getColor(R.color.night_text));
            textViewComp.setTextColor(getResources().getColor(R.color.night_text));
        }

    }

    private void screenOn() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

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
        // buildEasyLocationClient();
        // LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_GAME);
    }

    /* Remove the locationlistener updates when Activity is paused */
    @Override
    protected void onPause() {
        super.onPause();
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
        // to stop the listener and save battery
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.back2, R.anim.back1);
        stopLocationUpdates();
        mSensorManager.unregisterListener(this);
    }

}