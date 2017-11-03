package com.lovejoy777.boatlog;

import android.app.ActivityOptions;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by steve on 08/09/17.
 */

public class EditTripsActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;

    private BoatLogDBHelper dbHelper;

    ImageView button_saveTrip;
    ImageView button_deleteTrip;

    ScrollView scrollView1;
    RelativeLayout MRL1;

    TextView textViewName;
    TextView textViewDeparture;
    TextView textViewDestination;

    EditText nameEditText;
    EditText departureEditText;
    EditText destinationEditText;

    int tripID;
    String tripNam;

    int theme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Initialize the associated SharedPreferences file with default values
        PreferenceManager.setDefaultValues(this, R.xml.prefs, false);
        SharedPreferences prefs1 = PreferenceManager.getDefaultSharedPreferences(this);
        setTheme(theme = getTheme(prefs1.getString("theme", "fresh")));

        super.onCreate(savedInstanceState);

        tripID = getIntent().getIntExtra(MainActivityTrips.KEY_EXTRA_TRIPS_ID, 0);
        tripNam = getIntent().getStringExtra(MainActivityTrips.KEY_EXTRA_TRIPS_NAME);

        setContentView(R.layout.activity_edit_trips);

        loadToolbarNavDrawer(tripNam);
        button_saveTrip = (ImageView) findViewById(R.id.button_saveTrip);
        button_deleteTrip = (ImageView) findViewById(R.id.button_deleteTrip);
        TypedArray ta = obtainStyledAttributes(new int[]{R.attr.colorLightTextPrimary});
        button_saveTrip.setColorFilter(ta.getColor(0, Color.WHITE), PorterDuff.Mode.SRC_ATOP);
        button_deleteTrip.setColorFilter(ta.getColor(0, Color.WHITE), PorterDuff.Mode.SRC_ATOP);
        ta.recycle();

        scrollView1 = (ScrollView) findViewById(R.id.scrollView1);
        MRL1 = (RelativeLayout) findViewById(R.id.MRL1);

        textViewName = (TextView) findViewById(R.id.textViewName);
        textViewDeparture = (TextView) findViewById(R.id.textViewDeparture);
        textViewDestination = (TextView) findViewById(R.id.textViewDestination);

        nameEditText = (EditText) findViewById(R.id.editTextName);
        departureEditText = (EditText) findViewById(R.id.editTextDeparture);
        destinationEditText = (EditText) findViewById(R.id.editTextDestination);

        dbHelper = new BoatLogDBHelper(this);

        button_saveTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                persistTrip();
            }
        });

        button_deleteTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteTrip();
            }
        });

        nameEditText.setFocusableInTouchMode(true);
        nameEditText.setClickable(true);
        departureEditText.setFocusableInTouchMode(true);
        departureEditText.setClickable(true);
        destinationEditText.setFocusableInTouchMode(true);
        destinationEditText.setClickable(true);

        Cursor rs = dbHelper.getTrip(tripID);
        rs.moveToFirst();
        final String tripName = rs.getString(rs.getColumnIndex(BoatLogDBHelper.TRIPS_COLUMN_NAME));
        String tripDeparture = rs.getString(rs.getColumnIndex(BoatLogDBHelper.TRIPS_COLUMN_DEPARTURE));
        String tripDestination = rs.getString(rs.getColumnIndex(BoatLogDBHelper.TRIPS_COLUMN_DESTINATION));
        if (!rs.isClosed()) {
            rs.close();
        }

        nameEditText.setText(tripName);
        departureEditText.setText(tripDeparture);
        destinationEditText.setText("" + tripDestination + "");

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

    private void loadToolbarNavDrawer(String tripNam) {
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
        getSupportActionBar().setTitle("Edit " + tripNam + "");
        ta.recycle();
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
                        menuItem.setChecked(false);
                        int id = menuItem.getItemId();
                        switch (id) {
                            case R.id.nav_home_edit_trips:
                                getSupportActionBar().setElevation(0);
                                mDrawerLayout.closeDrawers();
                                break;
                            case R.id.nav_save_trip:
                                saveTrip();
                                break;
                            case R.id.nav_delete_trip:
                                deleteTrip();
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

    public void deleteTrip() {
        Cursor rs = dbHelper.getTrip(tripID);
        rs.moveToFirst();
        final String tripName = rs.getString(rs.getColumnIndex(BoatLogDBHelper.TRIPS_COLUMN_NAME));
        if (!rs.isClosed()) {
            rs.close();
        }

            AlertDialog.Builder builder;
            builder = new AlertDialog.Builder(EditTripsActivity.this);
            TypedArray ta = obtainStyledAttributes(new int[]{R.attr.colorTextPrimary});
            Drawable Btn = getResources().getDrawable(R.drawable.ic_delete_white_24dp);
            Btn.setColorFilter(ta.getColor(0, Color.WHITE), PorterDuff.Mode.SRC_ATOP);
            builder.setIcon(Btn);
            ta.recycle();
            builder.setTitle("Delete Trip?")
                    .setMessage(tripName)
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dbHelper.deleteTrip(tripID);
                            dbHelper.deleteAllTripEntries(tripID);
                            Toast.makeText(getApplicationContext(), "Deleted Successfully", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), MainActivityTrips.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            Bundle bndlanimation =
                                    ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.anni1, R.anim.anni2).toBundle();
                            startActivity(intent, bndlanimation);
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // cancelled by user
                            Intent intent = new Intent(getApplicationContext(), MainActivityTrips.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            Bundle bndlanimation =
                                    ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.anni1, R.anim.anni2).toBundle();
                            startActivity(intent, bndlanimation);
                            dialog.dismiss();
                        }
                    })
                    .show();
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
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.back2, R.anim.back1);
    }
}
