package com.lovejoy777.boatlog;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by steve on 07/09/17.
 */

public class CreateEntriesActivity extends EasyLocationAppCompatActivity {

    private DrawerLayout mDrawerLayout;

    private BoatLogDBHelper dbHelper;

    ImageView button_saveEntry;
    ImageView button_useFavourite;

    // GOOGLE MAPS/LOCATION SERVICES
    final String TAG = "GPS";
    long UPDATE_INTERVAL = 2 * 1000;  // 10 secs?
    long FASTEST_INTERVAL = 2000; // 2 sec
    long FALLBACK_INTERVAL = 4000; // 7 seconds

    ScrollView scrollView1;
    RelativeLayout MRL1;

    TextView titleTextView, textViewName, textViewTime, textViewDate, textViewLocation;
    EditText nameEditText, timeEditText, dateEditText, locationEditText;
    TextView trip_idText;

    public final static String KEY_EXTRA_TRIPS_ID = "KEY_EXTRA_TRIPS_ID";
    public final static String KEY_EXTRA_TRIPS_NAME = "KEY_EXTRA_TRIPS_NAME";

    int entryID;
    String entryName;
    int tripID;
    String tripName;

    int theme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Initialize the associated SharedPreferences file with default values
        PreferenceManager.setDefaultValues(this, R.xml.prefs, false);
        SharedPreferences prefs1 = PreferenceManager.getDefaultSharedPreferences(this);
        setTheme(theme = getTheme(prefs1.getString("theme", "fresh")));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_entries);

        loadToolbarNavDrawer();
        button_saveEntry = (ImageView) findViewById(R.id.button_saveEntry);
        button_useFavourite = (ImageView) findViewById(R.id.button_useFavourite);

        entryID = getIntent().getIntExtra(MainActivityEntries.KEY_EXTRA_ENTRIES_ID, 0);
        entryName = getIntent().getStringExtra(MainActivityEntries.KEY_EXTRA_ENTRY_NAME);
        tripID = getIntent().getIntExtra(MainActivityEntries.KEY_EXTRA_TRIPS_ID, 0);
        tripName = getIntent().getStringExtra(MainActivityEntries.KEY_EXTRA_TRIPS_NAME);

        scrollView1 = (ScrollView) findViewById(R.id.scrollView1);
        MRL1 = (RelativeLayout) findViewById(R.id.MRL1);

        titleTextView = (TextView) findViewById(R.id.titleTextView);

        textViewName = (TextView) findViewById(R.id.textViewName);
        textViewTime = (TextView) findViewById(R.id.textViewTime);
        textViewDate = (TextView) findViewById(R.id.textViewDate);
        textViewLocation = (TextView) findViewById(R.id.textViewLocation);

        nameEditText = (EditText) findViewById(R.id.editTextName);
        timeEditText = (EditText) findViewById(R.id.editTextTime);
        dateEditText = (EditText) findViewById(R.id.editTextDate);
        locationEditText = (EditText) findViewById(R.id.editTextLocation);

        trip_idText = (TextView) findViewById(R.id.TextViewTrip_ID);
        titleTextView.setText(R.string.create_entry);

        // Get Time and Date
        Calendar c = Calendar.getInstance();
        System.out.println("Current time => " + c.getTime());
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.UK);
        String formattedDate = df.format(c.getTime());
        SimpleDateFormat dt = new SimpleDateFormat("HH:mm", Locale.UK);
        String formattedTime = dt.format(c.getTime());

        // PERMISSIONS
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }

        // IS GOOGLE PLAY SERVICES AVAILIBLE
        isGooglePlayServicesAvailable();
        // if (!isLocationEnabled())
        //   showAlert();

        dbHelper = new BoatLogDBHelper(this);

        button_saveEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                persistEntry();
            }
        });

        button_useFavourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFavourite();
            }
        });

        // pre fill text fields
        trip_idText.setText("" + tripID + "");
        if (entryName != null && !entryName.isEmpty()) {
            nameEditText.setText("" + entryName + "");
        }

        timeEditText.setText("" + formattedTime + "");
        dateEditText.setText("" + formattedDate + "");
        locationEditText.setText(R.string.no_gps);

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
        String formattedLocation;
        if (location != null) {
            formattedLocation = FormattedLocation(location.getLatitude(), location.getLongitude());
            locationEditText.setText("" + formattedLocation + "");
        }
    }

    public void persistEntry() {

        String fav = "off";
        if (dbHelper.insertEntry(fav,
                nameEditText.getText().toString(),
                timeEditText.getText().toString(),
                dateEditText.getText().toString(),
                locationEditText.getText().toString(),
                trip_idText.getText().toString())) {
            Toast.makeText(getApplicationContext(), "Entry Saved", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "Could not Save Entry", Toast.LENGTH_SHORT).show();
        }
        Intent intent = new Intent(getApplicationContext(), MainActivityEntries.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(KEY_EXTRA_TRIPS_NAME, tripName);
        intent.putExtra(KEY_EXTRA_TRIPS_ID, tripID);
        Bundle bndlanimation =
                ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.anni1, R.anim.anni2).toBundle();
        startActivity(intent, bndlanimation);

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
            String latDegree = latitude >= 0 ? "N" : "S";
            String lonDegrees = longitude >= 0 ? "E" : "W";

            return Math.abs(latDegrees) + "°" + latMinutes + "'" + latSeconds
                    + "\"" + latDegree + " " + Math.abs(longDegrees) + "°" + longMinutes
                    + "'" + longSeconds + "\"" + lonDegrees;
        } catch (Exception e) {

            return "" + String.format("%8.5f", latitude) + "  "
                    + String.format("%8.5f", longitude);
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
            builder = new android.support.v7.app.AlertDialog.Builder(CreateEntriesActivity.this, R.style.AlertDialogTheme);
        } else {
            builder = new android.support.v7.app.AlertDialog.Builder(CreateEntriesActivity.this, R.style.AlertDialogTheme);
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

    private void loadToolbarNavDrawer() {
        //set Toolbar
        final android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //set NavigationDrawer
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            setupDrawerContent(navigationView);
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
                        menuItem.setChecked(true);
                        int id = menuItem.getItemId();
                        switch (id) {
                            case R.id.nav_home_create_entries:
                                getSupportActionBar().setElevation(0);
                                mDrawerLayout.closeDrawers();
                                break;
                            case R.id.nav_save_entry:
                                saveEntry();
                                break;
                            case R.id.nav_add_favourite:
                                addFavourite();
                                break;
                            case R.id.nav_create_favourites:
                                createFavourite();
                                break;
                            case R.id.nav_delete_favourites:
                                deleteFavourite();
                                break;
                        }
                        return false;
                    }
                }
        );
    }

    public void saveEntry() {
        persistEntry();
    }

    public void addFavourite() {
        Cursor rs = dbHelper.getAllFavEntry();
        ArrayList<String> favArray = new ArrayList<>();
        while (rs.moveToNext()) {
            String fav = rs.getString(rs.getColumnIndex(BoatLogDBHelper.FAVENTRY_COLUMN_NAME));
            favArray.add(fav);
        }
        if (!rs.isClosed()) {
            rs.close();
        }
        final String[] favnames = favArray.toArray(new String[favArray.size()]);

        android.support.v7.app.AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new android.support.v7.app.AlertDialog.Builder(CreateEntriesActivity.this, R.style.AlertDialogTheme);
        } else {
            builder = new android.support.v7.app.AlertDialog.Builder(CreateEntriesActivity.this, R.style.AlertDialogTheme);
        }
        builder.setTitle("      Select a Favourite");
        builder.setIcon(R.drawable.ic_favorite_border);
        if (favnames == null) {
            builder.create();
        }
        builder.setItems(favnames, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String mChosenFavourite = favnames[which];
                nameEditText.setText(mChosenFavourite);
            }
        });

        builder.show();
    }

    // Create Favourite
    private void createFavourite() {
        android.support.v7.app.AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new android.support.v7.app.AlertDialog.Builder(CreateEntriesActivity.this, R.style.AlertDialogTheme);
        } else {
            builder = new android.support.v7.app.AlertDialog.Builder(CreateEntriesActivity.this, R.style.AlertDialogTheme);
        }

        builder.setTitle("      Create a Favourite");
        final EditText input = new EditText(CreateEntriesActivity.this);
        input.setTextColor(getResources().getColor(R.color.white));
        input.setTextSize(20);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        builder.setView(input);
        builder.setIcon(R.drawable.ic_favorite_border);
        builder.setPositiveButton("SAVE",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (dbHelper.insertFavEntry(input.getText().toString())) {
                            Toast.makeText(getApplicationContext(), "Favourite Saved", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Could not Save Favourite", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        builder.setNegativeButton("CANCEL",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        builder.show();
    }

    // Delete Favourite
    private void deleteFavourite() {
        Cursor rs = dbHelper.getAllFavEntry();
        ArrayList<String> favArray = new ArrayList<String>();
        while (rs.moveToNext()) {
            String fav = rs.getString(rs.getColumnIndex(BoatLogDBHelper.FAVENTRY_COLUMN_NAME));
            favArray.add(fav);
        }
        if (!rs.isClosed()) {
            rs.close();
        }
        final String[] favnames = favArray.toArray(new String[favArray.size()]);

        android.support.v7.app.AlertDialog.Builder builder1;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder1 = new android.support.v7.app.AlertDialog.Builder(CreateEntriesActivity.this, R.style.AlertDialogTheme);
        } else {
            builder1 = new android.support.v7.app.AlertDialog.Builder(CreateEntriesActivity.this, R.style.AlertDialogTheme);
        }

        builder1.setTitle("      Select a Favourite");
        builder1.setIcon(R.drawable.ic_favorite_border);
        if (favnames == null) {
            builder1.create();
        }

        builder1.setItems(favnames, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                final String mChosenFavourite = favnames[which];
                android.support.v7.app.AlertDialog.Builder builder2;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder2 = new android.support.v7.app.AlertDialog.Builder(CreateEntriesActivity.this, R.style.AlertDialogTheme);
                } else {
                    builder2 = new android.support.v7.app.AlertDialog.Builder(CreateEntriesActivity.this, R.style.AlertDialogTheme);
                }
                builder2.setTitle("   Delete Favourite")
                        .setMessage("    " + mChosenFavourite)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dbHelper.deleteFavEntry(mChosenFavourite);
                                Toast.makeText(getApplicationContext(), "Deleted Successfully", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // cancelled by user
                            }
                        })
                        .setIcon(R.drawable.ic_delete_white)
                        .show();
            }
        });
        builder1.show();
    }

    private int getTheme(String themePref) {
        switch (themePref) {
            case "dark":
                return R.style.AppTheme_NoActionBar_Dark;
            default:
                return R.style.AppTheme_NoActionBar;
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
    }

    /* Remove the locationlistener updates when Activity is paused */
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