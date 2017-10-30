package com.lovejoy777.boatlog;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by steve on 08/09/17.
 */

public class CreateTripsActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;

    private BoatLogDBHelper dbHelper;

    ScrollView scrollView1;
    RelativeLayout MRL1;

    TextView titleTextView;
    TextView textViewName;
    TextView textViewDeparture;
    TextView textViewDestination;

    EditText nameEditText;
    EditText departureEditText;
    EditText destinationEditText;

    int tripID;

    int theme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Initialize the associated SharedPreferences file with default values
        PreferenceManager.setDefaultValues(this, R.xml.prefs, false);
        SharedPreferences prefs1 = PreferenceManager.getDefaultSharedPreferences(this);
        setTheme(theme = getTheme(prefs1.getString("theme", "fresh")));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_trips);

        loadToolbarNavDrawer();

        scrollView1 = (ScrollView) findViewById(R.id.scrollView1);
        MRL1 = (RelativeLayout) findViewById(R.id.MRL1);

        titleTextView = (TextView) findViewById(R.id.titleTextView);
        textViewName = (TextView) findViewById(R.id.textViewName);
        textViewDeparture = (TextView) findViewById(R.id.textViewDeparture);
        textViewDestination = (TextView) findViewById(R.id.textViewDestination);

        titleTextView.setText(R.string.create_trip);

        nameEditText = (EditText) findViewById(R.id.editTextName);
        departureEditText = (EditText) findViewById(R.id.editTextDeparture);
        destinationEditText = (EditText) findViewById(R.id.editTextDestination);

        dbHelper = new BoatLogDBHelper(this);

        dbHelper = new BoatLogDBHelper(this);

    }

    public void persistTrip() {
        if (tripID > 0) {
            if (dbHelper.updateTrip(tripID, nameEditText.getText().toString(),
                    departureEditText.getText().toString(),
                    destinationEditText.getText().toString())) {
                Toast.makeText(getApplicationContext(), "Trip Edited Successful", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), MainActivityTrips.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                Bundle bndlanimation =
                        ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.anni1, R.anim.anni2).toBundle();
                startActivity(intent, bndlanimation);
            } else {
                Toast.makeText(getApplicationContext(), "Trip Edit Failed", Toast.LENGTH_SHORT).show();
            }
        } else {
            if (dbHelper.insertTrip(nameEditText.getText().toString(),
                    departureEditText.getText().toString(),
                    destinationEditText.getText().toString())) {
                Toast.makeText(getApplicationContext(), "Trip Saved", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Could not Save trip", Toast.LENGTH_SHORT).show();
            }
            Intent intent = new Intent(getApplicationContext(), MainActivityTrips.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            Bundle bndlanimation =
                    ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.anni1, R.anim.anni2).toBundle();
            startActivity(intent, bndlanimation);
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
                            case R.id.nav_home_create_trips:
                                getSupportActionBar().setElevation(0);
                                mDrawerLayout.closeDrawers();
                                break;
                            case R.id.nav_save_trip:
                                saveTrip();
                                break;
                        }
                        return false;
                    }
                }
        );
    }

    public void saveTrip() {
        persistTrip();
    }

    private int getTheme(String themePref) {
        switch (themePref) {
            case "dark":
                return R.style.AppTheme_NoActionBar_Dark;
            default:
                return R.style.AppTheme_NoActionBar;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.back2, R.anim.back1);
    }

}