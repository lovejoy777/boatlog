package com.lovejoy777.boatlog;

import android.app.ActivityOptions;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

/**
 * Created by lovejoy777 on 13/10/15.
 */
public class MainActivityWaypoint extends AppCompatActivity {

    public final static String KEY_EXTRA_WAYPOINT_ID = "KEY_EXTRA_WAYPOINT_ID";
    public final static String KEY_EXTRA_WAYPOINT_NAME = "KEY_EXTRA_WAYPOINT_NAME";

    private ListView listView;
    BoatLogDBHelper dbHelper;

    private boolean fabExpanded = false;
    private FloatingActionButton fabWaypoints; //main
    private LinearLayout layoutFabAddNew; //sub2

    RelativeLayout MRL1;
    Toolbar toolBar;
    ListView listViewWaypoint;
    TextView titleTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_waypoint);

        MRL1 = (RelativeLayout) findViewById(R.id.MRL1);
        toolBar = (Toolbar) findViewById(R.id.toolbar);

        fabWaypoints = (FloatingActionButton) this.findViewById(R.id.fabWaypoints);
        layoutFabAddNew = (LinearLayout) this.findViewById(R.id.layoutFabAddNew);
        fabWaypoints.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fabExpanded == true) {
                    closeSubMenusFab();
                } else {
                    openSubMenusFab();
                }
            }
        });

        // ADD NEW TRIP subFab button
        layoutFabAddNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivityWaypoint.this, CreateWaypointActivity.class);
                intent.putExtra(KEY_EXTRA_WAYPOINT_ID, 0);
                Bundle bndlanimation =
                        ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.anni1, R.anim.anni2).toBundle();
                startActivity(intent, bndlanimation);
                //Only main FAB is visible in the beginning
                closeSubMenusFab();
            }
        });

        //Only main FAB is visible in the beginning
        closeSubMenusFab();


        titleTextView = (TextView) findViewById(R.id.titleTextView);
        listViewWaypoint = (ListView) findViewById(R.id.listViewWaypoint);

        titleTextView.setText("Waypoints");

        dbHelper = new BoatLogDBHelper(this);

        populateListView();

        SharedPreferences myPrefs = this.getSharedPreferences("myPrefs", MODE_PRIVATE);
        Boolean NightModeOn = myPrefs.getBoolean("switch1", false);

        if (NightModeOn) {
            NightMode();
            populateListViewRed();
        }

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
        listView.setDivider(this.getResources().getDrawable(R.drawable.list_divide));
        listView.setDividerHeight(2);
        listView.setAdapter(cursorAdapter);
    }

    private void populateListViewRed() {
        final Cursor cursor = dbHelper.getAllWaypoint();
        String[] columns = new String[]{BoatLogDBHelper.WAYPOINT_COLUMN_ID, BoatLogDBHelper.WAYPOINT_COLUMN_NAME};
        int[] widgets = new int[]{R.id.waypointID, R.id.waypointName};
        SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter(this, R.layout.waypoint_info1, cursor, columns, widgets, 0);
        listView = (ListView) findViewById(R.id.listViewWaypoint);
        listView.setDivider(this.getResources().getDrawable(R.drawable.list_dividered));
        listView.setDividerHeight(2);
        listView.setAdapter(cursorAdapter);
    }

    private void NightMode() {

        MRL1.setBackgroundColor(getResources().getColor(R.color.card_background));
        toolBar.setBackgroundColor(getResources().getColor(R.color.card_background));
        titleTextView.setTextColor(getResources().getColor(R.color.night_text));
        listViewWaypoint.setBackgroundColor(getResources().getColor(R.color.card_background));

    }

    //closes FAB submenus
    private void closeSubMenusFab() {
        layoutFabAddNew.setVisibility(View.INVISIBLE);
        fabWaypoints.setImageResource(R.drawable.ic_menu_white);
        fabExpanded = false;
    }

    //Opens FAB submenus
    private void openSubMenusFab() {
        layoutFabAddNew.setVisibility(View.VISIBLE);
        fabWaypoints.setImageResource(R.drawable.ic_close_white);
        fabExpanded = true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.back2, R.anim.back1);
    }

}
