package com.lovejoy777.boatlog;

import android.app.ActivityOptions;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
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

/**
 * Created by lovejoy777 on 13/10/15.
 */

public class MainActivityWaypoint extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;

    public final static String KEY_EXTRA_WAYPOINT_ID = "KEY_EXTRA_WAYPOINT_ID";
    public final static String KEY_EXTRA_WAYPOINT_NAME = "KEY_EXTRA_WAYPOINT_NAME";

    private ListView listView;
    BoatLogDBHelper dbHelper;

    ImageView button_createNewWaypoint;

    RelativeLayout MRL1;
    ListView listViewWaypoint;
    TextView titleTextView;

    int theme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Initialize the associated SharedPreferences file with default values
        PreferenceManager.setDefaultValues(this, R.xml.prefs, false);
        SharedPreferences prefs1 = PreferenceManager.getDefaultSharedPreferences(this);
        setTheme(theme = getTheme(prefs1.getString("theme", "fresh")));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_waypoint);

        loadToolbarNavDrawer();
        button_createNewWaypoint = (ImageView) findViewById(R.id.button_createNewWaypoint);

        MRL1 = (RelativeLayout) findViewById(R.id.MRL1);

        titleTextView = (TextView) findViewById(R.id.titleTextView);
        listViewWaypoint = (ListView) findViewById(R.id.listViewWaypoint);

        titleTextView.setText("Waypoints");

        dbHelper = new BoatLogDBHelper(this);

        button_createNewWaypoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createWaypoint();
            }
        });

        populateListView();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> listView, View view,
                                    final int position, long id) {

                Cursor itemCursor = (Cursor) MainActivityWaypoint.this.listView.getItemAtPosition(position);
                final int waypointID = itemCursor.getInt(itemCursor.getColumnIndex(BoatLogDBHelper.WAYPOINT_COLUMN_ID));
                final String waypointName = "" + itemCursor.getString(itemCursor.getColumnIndex(BoatLogDBHelper.WAYPOINT_COLUMN_NAME));

                android.support.v7.app.AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new android.support.v7.app.AlertDialog.Builder(MainActivityWaypoint.this, R.style.AlertDialogTheme);
                } else {
                    builder = new android.support.v7.app.AlertDialog.Builder(MainActivityWaypoint.this, R.style.AlertDialogTheme);
                }
                builder.setTitle("       GoTo Waypoint")
                        .setMessage(waypointName)

                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(getApplicationContext(), GoToWaypoint.class);
                                intent.putExtra(KEY_EXTRA_WAYPOINT_ID, waypointID);
                                intent.putExtra(KEY_EXTRA_WAYPOINT_NAME, waypointName);
                                Bundle bndlanimation =
                                        ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.anni1, R.anim.anni2).toBundle();
                                startActivity(intent, bndlanimation);
                            }
                        })

                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // cancelled by user
                            }
                        })
                        .setIcon(R.drawable.ic_location_on_white)
                        .show();

            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> listView, View view,
                                           int position, long id) {

                Cursor itemCursor = (Cursor) MainActivityWaypoint.this.listView.getItemAtPosition(position);
                int waypointID = itemCursor.getInt(itemCursor.getColumnIndex(BoatLogDBHelper.WAYPOINT_COLUMN_ID));
                Intent intent = new Intent(getApplicationContext(), EditWaypointActivity.class);
                intent.putExtra(KEY_EXTRA_WAYPOINT_ID, waypointID);
                Bundle bndlanimation =
                        ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.anni1, R.anim.anni2).toBundle();
                startActivity(intent, bndlanimation);
                return true;
            }
        });

    }

    private void populateListView() {
        final Cursor cursor = dbHelper.getAllWaypoint();
        String[] columns = new String[]{BoatLogDBHelper.WAYPOINT_COLUMN_ID, BoatLogDBHelper.WAYPOINT_COLUMN_NAME};
        int[] widgets = new int[]{R.id.waypointID, R.id.waypointName};
        SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter(this, R.layout.waypoint_info, cursor, columns, widgets, 0);
        listView = (ListView) findViewById(R.id.listViewWaypoint);
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
                            case R.id.nav_home_waypoints:
                                getSupportActionBar().setElevation(0);
                                mDrawerLayout.closeDrawers();
                                break;
                            case R.id.nav_create_waypoint:
                                createWaypoint();
                                break;

                        }
                        return false;
                    }
                }
        );
    }

    // Create a New Trip
    private void createWaypoint() {
        Intent intent = new Intent(MainActivityWaypoint.this, CreateWaypointActivity.class);
        intent.putExtra(KEY_EXTRA_WAYPOINT_ID, 0);
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
