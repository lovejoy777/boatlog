package com.lovejoy777.boatlog;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by lovejoy777 on 13/10/15.
 */
public class MainActivityWaypoint extends AppCompatActivity {
    public final static String KEY_EXTRA_WAYPOINT_ID = "KEY_EXTRA_WAYPOINT_ID";
    public final static String KEY_EXTRA_WAYPOINT_NAME = "KEY_EXTRA_WAYPOINT_NAME";
    public final static String KEY_EXTRA_WAYPOINT_LOCATION = "KEY_EXTRA_WAYPOINT_LOCATION";

    private ListView listView;
    ExampleDBHelper dbHelper;

    RelativeLayout MRL1;
    Toolbar toolBar;
    ListView listViewWaypoint;
    TextView titleTextView;

    Button button, button1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_waypoint);

        MRL1 = (RelativeLayout) findViewById(R.id.MRL1);
        toolBar = (Toolbar) findViewById(R.id.toolbar);
        titleTextView = (TextView) findViewById(R.id.titleTextView);

        listViewWaypoint = (ListView) findViewById(R.id.listViewWaypoint);

        titleTextView.setText("Waypoints");



        button = (Button) findViewById(R.id.addNew);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivityWaypoint.this, CreateOrEditWaypointActivity.class);
                intent.putExtra(KEY_EXTRA_WAYPOINT_ID, 0);
                startActivity(intent);
            }
        });

        dbHelper = new ExampleDBHelper(this);

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
                final int waypointID = itemCursor.getInt(itemCursor.getColumnIndex(ExampleDBHelper.WAYPOINT_COLUMN_ID));
                final String waypointName = "" + itemCursor.getString(itemCursor.getColumnIndex(ExampleDBHelper.WAYPOINT_COLUMN_NAME));
                final String waypointLocation = "" + itemCursor.getString(itemCursor.getColumnIndex(ExampleDBHelper.WAYPOINT_COLUMN_LOCATION));

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivityWaypoint.this);
                builder.setMessage(waypointName)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                Intent intent = new Intent(getApplicationContext(), GoToWaypoint.class);
                                intent.putExtra(KEY_EXTRA_WAYPOINT_ID, waypointID);
                                intent.putExtra(KEY_EXTRA_WAYPOINT_NAME, waypointName);
                                intent.putExtra(KEY_EXTRA_WAYPOINT_LOCATION, waypointLocation);

                                startActivity(intent);
                            }
                        })
                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                            }
                        });
                AlertDialog d = builder.create();
                d.setTitle("GoTo Waypoint ?");
                d.show();
                return;



            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> listView, View view,
                                           int position, long id) {

                Cursor itemCursor = (Cursor) MainActivityWaypoint.this.listView.getItemAtPosition(position);
                int waypointID = itemCursor.getInt(itemCursor.getColumnIndex(ExampleDBHelper.WAYPOINT_COLUMN_ID));
                Intent intent = new Intent(getApplicationContext(), CreateOrEditWaypointActivity.class);
                intent.putExtra(KEY_EXTRA_WAYPOINT_ID, waypointID);
                startActivity(intent);
                return true;
            }
        });

    }

    private void populateListView() {
        final Cursor cursor = dbHelper.getAllWaypoint();
        String [] columns = new String[] {ExampleDBHelper.WAYPOINT_COLUMN_ID, ExampleDBHelper.WAYPOINT_COLUMN_NAME, ExampleDBHelper.ENTRY_COLUMN_LOCATION};
        int [] widgets = new int[] {R.id.waypointID, R.id.waypointName, R.id.waypointLocation};
        SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter(this, R.layout.waypoint_info, cursor, columns, widgets, 0);
        listView = (ListView)findViewById(R.id.listViewWaypoint);
        listView.setDivider(this.getResources().getDrawable(R.drawable.list_divide));
        listView.setDividerHeight(2);
        listView.setAdapter(cursorAdapter);
    }

    private void populateListViewRed() {
        final Cursor cursor = dbHelper.getAllWaypoint();
        String [] columns = new String[] {ExampleDBHelper.WAYPOINT_COLUMN_ID, ExampleDBHelper.WAYPOINT_COLUMN_NAME, ExampleDBHelper.ENTRY_COLUMN_LOCATION};
        int [] widgets = new int[] {R.id.waypointID, R.id.waypointName, R.id.waypointLocation};
        SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter(this, R.layout.waypoint_info1, cursor, columns, widgets, 0);
        listView = (ListView)findViewById(R.id.listViewWaypoint);
        listView.setDivider(this.getResources().getDrawable(R.drawable.list_dividered));
        listView.setDividerHeight(2);
        listView.setAdapter(cursorAdapter);
    }

    private void NightMode() {


        MRL1.setBackgroundColor(Color.BLACK);
        toolBar.setBackgroundColor(Color.BLACK);
        titleTextView.setTextColor(Color.RED);

        button.setBackgroundResource(R.color.card_background);
        button.setTextColor(Color.RED);

        listViewWaypoint.setBackgroundColor(Color.BLACK);



        // Toast.makeText(MainActivityLog.this, "Night Mode", Toast.LENGTH_LONG).show();

    }

}
