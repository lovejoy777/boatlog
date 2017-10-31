package com.lovejoy777.boatlog;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import static com.lovejoy777.boatlog.R.id.tripName;

public class MainActivityTrips extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;

    public final static String KEY_EXTRA_TRIPS_ID = "KEY_EXTRA_TRIPS_ID";
    public final static String KEY_EXTRA_TRIPS_NAME = "KEY_EXTRA_TRIPS_NAME";

    private ListView listView;
    BoatLogDBHelper dbHelper;

    ImageView button_createNewTrip;

    RelativeLayout MRL1;

    ListView listViewTrips;
    TextView titleTextView;

    int theme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Initialize the associated SharedPreferences file with default values
        PreferenceManager.setDefaultValues(this, R.xml.prefs, false);
        SharedPreferences prefs1 = PreferenceManager.getDefaultSharedPreferences(this);
        setTheme(theme = getTheme(prefs1.getString("theme", "fresh")));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_trips);

        loadToolbarNavDrawer();
        button_createNewTrip = (ImageView) findViewById(R.id.button_createNewTrip);
        SharedPreferences myPrefs = this.getSharedPreferences("myPrefs", MODE_PRIVATE);
        final Boolean NightModeOn = myPrefs.getBoolean("switch1", false);
        if (NightModeOn) {
            button_createNewTrip.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
        }

        MRL1 = (RelativeLayout) findViewById(R.id.MRL1);

        titleTextView = (TextView) findViewById(R.id.titleTextView);
        listViewTrips = (ListView) findViewById(R.id.listViewTrips);
        titleTextView.setText("Trips");

        dbHelper = new BoatLogDBHelper(this);

        button_createNewTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createTrip();
            }
        });

        populateListView();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> listView, View view,
                                    int position, long id) {

                Cursor itemCursor = (Cursor) MainActivityTrips.this.listView.getItemAtPosition(position);
                int tripID = itemCursor.getInt(itemCursor.getColumnIndex(BoatLogDBHelper.TRIPS_COLUMN_ID));
                String tripName = "" + itemCursor.getString(itemCursor.getColumnIndex(BoatLogDBHelper.TRIPS_COLUMN_NAME));
                Intent intent = new Intent(getApplicationContext(), MainActivityEntries.class);
                intent.putExtra(KEY_EXTRA_TRIPS_ID, tripID);
                intent.putExtra(KEY_EXTRA_TRIPS_NAME, tripName);
                Bundle bndlanimation =
                        ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.anni1, R.anim.anni2).toBundle();
                startActivity(intent, bndlanimation);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> listView, View view,
                                           int position, long id) {
                Cursor itemCursor = (Cursor) MainActivityTrips.this.listView.getItemAtPosition(position);
                int tripID = itemCursor.getInt(itemCursor.getColumnIndex(BoatLogDBHelper.TRIPS_COLUMN_ID));
                Intent intent = new Intent(getApplicationContext(), EditTripsActivity.class);
                intent.putExtra(KEY_EXTRA_TRIPS_ID, tripID);
                Bundle bndlanimation =
                        ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.anni1, R.anim.anni2).toBundle();
                startActivity(intent, bndlanimation);
                return true;
            }
        });

    }

    private void populateListView() {
        final Cursor cursor = dbHelper.getAllTrips();
        String[] columns = new String[]{BoatLogDBHelper.TRIPS_COLUMN_ID, BoatLogDBHelper.TRIPS_COLUMN_NAME};
        int[] widgets = new int[]{R.id.tripID, tripName};
        SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter(this, R.layout.trips_info, cursor, columns, widgets, 0);
        listView = (ListView) findViewById(R.id.listViewTrips);
        listView.setDividerHeight(2);
        listView.setAdapter(cursorAdapter);
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
                            case R.id.nav_home_trips:
                                getSupportActionBar().setElevation(0);
                                mDrawerLayout.closeDrawers();
                                break;
                            case R.id.nav_create_trip:
                                createTrip();
                                break;

                        }
                        return false;
                    }
                }
        );
    }

    // Create a New Trip
    private void createTrip() {
        Intent intent = new Intent(MainActivityTrips.this, CreateTripsActivity.class);
        intent.putExtra(KEY_EXTRA_TRIPS_ID, 0);
        Bundle bndlanimation =
                ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.anni1, R.anim.anni2).toBundle();
        startActivity(intent, bndlanimation);
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
