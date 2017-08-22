package com.lovejoy777.boatlog;

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

/**
 * Created by lovejoy777 on 03/10/15.
 */
public class MainActivityEntries extends AppCompatActivity {
    public final static String KEY_EXTRA_ENTRIES_ID = "KEY_EXTRA_ENTRIES_ID";
    public final static String KEY_EXTRA_TRIPS_ID = "KEY_EXTRA_TRIPS_ID";
    public final static String KEY_EXTRA_TRIPS_NAME = "KEY_EXTRA_TRIPS_NAME";

    private ListView listView;
    ExampleDBHelper dbHelper;

    RelativeLayout MRL1;
    Toolbar toolBar;
    ListView listViewEntries;

    Button button;

    TextView titleTextView;

    int tripID;
    String tripName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_entries);

        tripID = getIntent().getIntExtra(MainActivityTrips.KEY_EXTRA_TRIPS_ID, 0);
        tripName = getIntent().getStringExtra(MainActivityTrips.KEY_EXTRA_TRIPS_NAME);

        tripID = getIntent().getIntExtra(CreateOrEditEntriesActivity.KEY_EXTRA_TRIPS_ID, 0);
        tripName = getIntent().getStringExtra(CreateOrEditEntriesActivity.KEY_EXTRA_TRIPS_NAME);

        //Toast.makeText(getApplicationContext(), "Trip " + tripName, Toast.LENGTH_SHORT).show();

        MRL1 = (RelativeLayout) findViewById(R.id.MRL1);
        toolBar = (Toolbar) findViewById(R.id.toolbar);
        titleTextView = (TextView) findViewById(R.id.titleTextView);
        listViewEntries = (ListView) findViewById(R.id.listViewEntries);
        titleTextView.setText("" + tripName);

        button = (Button) findViewById(R.id.addNew);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivityEntries.this, CreateOrEditEntriesActivity.class);
                intent.putExtra(KEY_EXTRA_ENTRIES_ID, 0);
                intent.putExtra(KEY_EXTRA_TRIPS_ID, tripID);
                intent.putExtra(KEY_EXTRA_TRIPS_NAME, tripName);
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
                                    int position, long id) {
                Cursor itemCursor = (Cursor) MainActivityEntries.this.listView.getItemAtPosition(position);
                int entryID = itemCursor.getInt(itemCursor.getColumnIndex(ExampleDBHelper.ENTRY_COLUMN_ID));
                Intent intent = new Intent(getApplicationContext(), CreateOrEditEntriesActivity.class);
                intent.putExtra(KEY_EXTRA_ENTRIES_ID, entryID);
                startActivity(intent);
            }
        });


        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> listView, View view,
                                           int position, long id) {
                Cursor itemCursor = (Cursor) MainActivityEntries.this.listView.getItemAtPosition(position);
                int entryID = itemCursor.getInt(itemCursor.getColumnIndex(ExampleDBHelper.ENTRY_COLUMN_ID));
                Intent intent = new Intent(getApplicationContext(), CreateOrEditEntriesActivity.class);
                intent.putExtra(KEY_EXTRA_ENTRIES_ID, entryID);
                startActivity(intent);
                return true;
            }
        });


    }

    private void populateListView() {
        final Cursor cursor = dbHelper.getTripEntry(tripID);
        String [] columns = new String[] {
                ExampleDBHelper.ENTRY_COLUMN_ID,
                ExampleDBHelper.ENTRY_COLUMN_NAME,
                ExampleDBHelper.ENTRY_COLUMN_TIME,
                ExampleDBHelper.ENTRY_COLUMN_DATE,
                ExampleDBHelper.ENTRY_COLUMN_TRIP_ID

        };
        int [] widgets = new int[] {
                R.id.entryID,
                R.id.entryName,
                R.id.entryTime,
                R.id.entryDate
                // R.id.entryTrip_ID
        };

        SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter(this, R.layout.entries_info,
                cursor, columns, widgets, 0);
        listView = (ListView)findViewById(R.id.listViewEntries);
        listView.setDivider(this.getResources().getDrawable(R.drawable.list_divide));
        listView.setDividerHeight(2);
        listView.setAdapter(cursorAdapter);
    }

    private void populateListViewRed() {
        final Cursor cursor = dbHelper.getTripEntry(tripID);
        String [] columns = new String[] {
                ExampleDBHelper.ENTRY_COLUMN_ID,
                ExampleDBHelper.ENTRY_COLUMN_NAME,
                ExampleDBHelper.ENTRY_COLUMN_TIME,
                ExampleDBHelper.ENTRY_COLUMN_DATE,
                ExampleDBHelper.ENTRY_COLUMN_TRIP_ID

        };
        int [] widgets = new int[] {
                R.id.entryID,
                R.id.entryName,
                R.id.entryTime,
                R.id.entryDate
                // R.id.entryTrip_ID
        };

        SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter(this, R.layout.entries_info1,
                cursor, columns, widgets, 0);
        listView = (ListView)findViewById(R.id.listViewEntries);
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

        listViewEntries.setBackgroundColor(Color.BLACK);

        // Toast.makeText(MainActivityLog.this, "Night Mode", Toast.LENGTH_LONG).show();

    }

}
