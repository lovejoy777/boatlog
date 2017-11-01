package com.lovejoy777.boatlog;

import android.app.ActivityOptions;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
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
 * Created by steve on 07/09/17.
 */

public class EditEntriesActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;

    private BoatLogDBHelper dbHelper;

    ImageView button_saveEntry;
    ImageView button_deleteEntry;

    ScrollView scrollView1;
    RelativeLayout MRL1;

    TextView textViewName, textViewTime, textViewDate, textViewLocation;
    EditText nameEditText, timeEditText, dateEditText, locationEditText;
    TextView trip_idText;

    public final static String KEY_EXTRA_TRIPS_ID = "KEY_EXTRA_TRIPS_ID";
    public final static String KEY_EXTRA_TRIPS_NAME = "KEY_EXTRA_TRIPS_NAME";

    int entryID;
    String entryNam;
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
        setContentView(R.layout.activity_edit_entries);

        entryID = getIntent().getIntExtra(MainActivityEntries.KEY_EXTRA_ENTRIES_ID, 0);
        entryNam = getIntent().getStringExtra(MainActivityEntries.KEY_EXTRA_ENTRY_NAME);
        tripID = getIntent().getIntExtra(MainActivityEntries.KEY_EXTRA_TRIPS_ID, 0);
        tripName = getIntent().getStringExtra(MainActivityEntries.KEY_EXTRA_TRIPS_NAME);

        loadToolbarNavDrawer(entryNam);
        button_saveEntry = (ImageView) findViewById(R.id.button_saveEntry);
        button_deleteEntry = (ImageView) findViewById(R.id.button_deleteEntry);
        SharedPreferences myPrefs = this.getSharedPreferences("myPrefs", MODE_PRIVATE);
        final Boolean NightModeOn = myPrefs.getBoolean("switch1", false);
        if (NightModeOn) {
            button_saveEntry.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
            button_deleteEntry.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
        }

        scrollView1 = (ScrollView) findViewById(R.id.scrollView1);
        MRL1 = (RelativeLayout) findViewById(R.id.MRL1);

        textViewName = (TextView) findViewById(R.id.textViewName);
        textViewTime = (TextView) findViewById(R.id.textViewTime);
        textViewDate = (TextView) findViewById(R.id.textViewDate);
        textViewLocation = (TextView) findViewById(R.id.textViewLocation);

        nameEditText = (EditText) findViewById(R.id.editTextName);
        timeEditText = (EditText) findViewById(R.id.editTextTime);
        dateEditText = (EditText) findViewById(R.id.editTextDate);
        locationEditText = (EditText) findViewById(R.id.editTextLocation);

        trip_idText = (TextView) findViewById(R.id.TextViewTrip_ID);

        dbHelper = new BoatLogDBHelper(this);

        nameEditText.setFocusableInTouchMode(true);
        nameEditText.setClickable(true);

        timeEditText.setFocusableInTouchMode(true);
        timeEditText.setClickable(true);

        dateEditText.setFocusableInTouchMode(true);
        dateEditText.setClickable(true);

        locationEditText.setFocusableInTouchMode(true);
        locationEditText.setClickable(true);

        trip_idText.setFocusableInTouchMode(true);
        trip_idText.setClickable(true);

        Cursor rs = dbHelper.getEntry(entryID);
        rs.moveToFirst();
        final String entryName = rs.getString(rs.getColumnIndex(BoatLogDBHelper.ENTRY_COLUMN_NAME));
        String entryTime = rs.getString(rs.getColumnIndex(BoatLogDBHelper.ENTRY_COLUMN_TIME));
        String entryDate = rs.getString(rs.getColumnIndex(BoatLogDBHelper.ENTRY_COLUMN_DATE));
        String entryLocation = rs.getString(rs.getColumnIndex(BoatLogDBHelper.ENTRY_COLUMN_LOCATION));
        String entryTrip_ID = rs.getString(rs.getColumnIndex(BoatLogDBHelper.ENTRY_COLUMN_TRIP_ID));
        if (!rs.isClosed()) {
            rs.close();
        }

        nameEditText.setText(entryName);
        timeEditText.setText(entryTime);
        dateEditText.setText(entryDate);
        locationEditText.setText("" + entryLocation + "");
        trip_idText.setText(entryTrip_ID);

        button_saveEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                persistEntry();
            }
        });

        button_deleteEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteEntry();
            }
        });

    }

    public void persistEntry() {

        Cursor rs = dbHelper.getEntry(entryID);
        rs.moveToFirst();
        String fab = rs.getString(rs.getColumnIndex(BoatLogDBHelper.ENTRY_COLUMN_FAV));
        if (!rs.isClosed()) {
            rs.close();
        }

        if (dbHelper.updateEntry(entryID, fab, nameEditText.getText().toString(),
                timeEditText.getText().toString(),
                dateEditText.getText().toString(),
                locationEditText.getText().toString(),
                trip_idText.getText().toString())) {
            Toast.makeText(getApplicationContext(), "Entry Edited Successful", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), MainActivityEntries.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra(KEY_EXTRA_TRIPS_NAME, tripName);
            intent.putExtra(KEY_EXTRA_TRIPS_ID, tripID);
            Bundle bndlanimation =
                    ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.anni1, R.anim.anni2).toBundle();
            startActivity(intent, bndlanimation);
        } else {
            Toast.makeText(getApplicationContext(), "Entry Edit Failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadToolbarNavDrawer(String entryNam) {
        //set Toolbar
        final android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(6);
        SharedPreferences myNightPref = this.getSharedPreferences("myPrefs", MODE_PRIVATE);
        final Boolean NightModeOn = myNightPref.getBoolean("switch1", false);
        if (NightModeOn) {
            final Drawable menuBtn = getResources().getDrawable(R.drawable.ic_action_menu);
            menuBtn.setColorFilter(getResources().getColor(R.color.accent), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setHomeAsUpIndicator(menuBtn);
            toolbar.setTitleTextColor(getResources().getColor(R.color.accent));
        } else {
            final Drawable menuBtn = getResources().getDrawable(R.drawable.ic_action_menu);
            menuBtn.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setHomeAsUpIndicator(menuBtn);
            toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        }
        getSupportActionBar().setTitle("Edit " + entryNam + "");
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
                            case R.id.nav_home_edit_entries:
                                getSupportActionBar().setElevation(0);
                                mDrawerLayout.closeDrawers();
                                break;
                            case R.id.nav_save_entry:
                                saveEntry();
                                break;
                            case R.id.nav_delete_entry:
                                deleteEntry();
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

    public void deleteEntry() {
        Cursor rs = dbHelper.getEntry(entryID);
        rs.moveToFirst();
        final String entryName = rs.getString(rs.getColumnIndex(BoatLogDBHelper.ENTRY_COLUMN_NAME));
        if (!rs.isClosed()) {
            rs.close();
        }
        android.support.v7.app.AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new android.support.v7.app.AlertDialog.Builder(EditEntriesActivity.this, R.style.AlertDialogTheme);
        } else {
            builder = new android.support.v7.app.AlertDialog.Builder(EditEntriesActivity.this, R.style.AlertDialogTheme);
        }
        builder.setTitle("Delete Entry?")
                .setMessage(entryName)

                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dbHelper.deleteEntry(entryID);
                        Toast.makeText(getApplicationContext(), "Deleted Successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), MainActivityEntries.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra(KEY_EXTRA_TRIPS_ID, tripID);
                        intent.putExtra(KEY_EXTRA_TRIPS_NAME, tripName);
                        Bundle bndlanimation =
                                ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.anni1, R.anim.anni2).toBundle();
                        startActivity(intent, bndlanimation);
                        startActivity(intent);
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
    }
}