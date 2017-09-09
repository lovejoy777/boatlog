package com.lovejoy777.boatlog;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
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

import static com.lovejoy777.boatlog.R.id.tripName;

public class MainActivityTrips extends AppCompatActivity {
    public final static String KEY_EXTRA_TRIPS_ID = "KEY_EXTRA_TRIPS_ID";
    public final static String KEY_EXTRA_TRIPS_NAME = "KEY_EXTRA_TRIPS_NAME";

    private ListView listView;
    ExampleDBHelper dbHelper;

    RelativeLayout MRL1;
    Toolbar toolBar;

    private boolean fabExpanded = false;
    private FloatingActionButton fabTrips; //main
    private LinearLayout layoutFabAddNew; //sub2

    ListView listViewTrips;
    TextView titleTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_trips);

        MRL1 = (RelativeLayout) findViewById(R.id.MRL1);
        toolBar = (Toolbar) findViewById(R.id.toolbar);

        fabTrips = (FloatingActionButton) this.findViewById(R.id.fabTrips);
        layoutFabAddNew = (LinearLayout) this.findViewById(R.id.layoutFabAddNew);
        fabTrips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fabExpanded == true){
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
                Intent intent = new Intent(MainActivityTrips.this, CreateTripsActivity.class);
                intent.putExtra(KEY_EXTRA_TRIPS_ID, 0);
                startActivity(intent);
                closeSubMenusFab();
            }
        });

        //Only main FAB is visible in the beginning
        closeSubMenusFab();

        titleTextView = (TextView) findViewById(R.id.titleTextView);

        listViewTrips = (ListView) findViewById(R.id.listViewTrips);

        titleTextView.setText("Trips");

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
                                    int position, long id) {

                Cursor itemCursor = (Cursor) MainActivityTrips.this.listView.getItemAtPosition(position);
                int tripID = itemCursor.getInt(itemCursor.getColumnIndex(ExampleDBHelper.TRIPS_COLUMN_ID));
                String tripName = "" + itemCursor.getString(itemCursor.getColumnIndex(ExampleDBHelper.TRIPS_COLUMN_NAME));
                Intent intent = new Intent(getApplicationContext(), MainActivityEntries.class);
                intent.putExtra(KEY_EXTRA_TRIPS_ID, tripID);
                intent.putExtra(KEY_EXTRA_TRIPS_NAME, tripName);
                startActivity(intent);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> listView, View view,
                                           int position, long id) {
                Cursor itemCursor = (Cursor) MainActivityTrips.this.listView.getItemAtPosition(position);
                int tripID = itemCursor.getInt(itemCursor.getColumnIndex(ExampleDBHelper.TRIPS_COLUMN_ID));
                Intent intent = new Intent(getApplicationContext(), EditTripsActivity.class);
                intent.putExtra(KEY_EXTRA_TRIPS_ID, tripID);
                startActivity(intent);
                return true;
            }
        });

    }

    private void populateListView() {
        final Cursor cursor = dbHelper.getAllTrips();
        String [] columns = new String[] {ExampleDBHelper.TRIPS_COLUMN_ID, ExampleDBHelper.TRIPS_COLUMN_NAME};
        int [] widgets = new int[] {R.id.tripID, tripName};
        SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter(this, R.layout.trips_info, cursor, columns, widgets, 0);
        listView = (ListView)findViewById(R.id.listViewTrips);

        listView.setDivider(this.getResources().getDrawable(R.drawable.list_divide));
        listView.setDividerHeight(2);
        listView.setAdapter(cursorAdapter);
    }

    private void populateListViewRed() {
        final Cursor cursor = dbHelper.getAllTrips();
        String [] columns = new String[] {ExampleDBHelper.TRIPS_COLUMN_ID, ExampleDBHelper.TRIPS_COLUMN_NAME};
        int [] widgets = new int[] {R.id.tripID, tripName};
        SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter(this, R.layout.trips_info1, cursor, columns, widgets, 0);
        listView = (ListView)findViewById(R.id.listViewTrips);
        listView.setDivider(this.getResources().getDrawable(R.drawable.list_dividered));
        listView.setDividerHeight(2);
        listView.setAdapter(cursorAdapter);
    }

    private void NightMode() {

        MRL1.setBackgroundColor(Color.BLACK);
        toolBar.setBackgroundColor(Color.BLACK);
        titleTextView.setTextColor(Color.RED);
        listViewTrips.setBackgroundColor(Color.BLACK);

    }

    //closes FAB submenus
    private void closeSubMenusFab(){
        layoutFabAddNew.setVisibility(View.INVISIBLE);
        fabTrips.setImageResource(R.drawable.ic_menu_white);
        fabExpanded = false;
    }

    //Opens FAB submenus
    private void openSubMenusFab(){
        layoutFabAddNew.setVisibility(View.VISIBLE);
        //Change settings icon to 'X' icon
        fabTrips.setImageResource(R.drawable.ic_close_white);
        fabExpanded = true;
    }
}
