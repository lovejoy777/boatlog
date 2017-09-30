package com.lovejoy777.boatlog;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
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

import static com.lovejoy777.boatlog.R.id.manlogName;
import static com.lovejoy777.boatlog.R.id.manlogProgress;

/**
 * Created by steve on 14/09/17.
 */

public class MainActivityManLog extends AppCompatActivity {
    public final static String KEY_EXTRA_MANLOG_ID = "KEY_EXTRA_MANLOG_ID";
    public final static String KEY_EXTRA_MANLOG_NAME = "KEY_EXTRA_MANLOG_NAME";
    public final static String KEY_EXTRA_MANLOG_PROGRESS = "KEY_EXTRA_MANLOG_PROGRESS";

    private ListView listView;
    BoatLogDBHelper dbHelper;

    RelativeLayout MRL1;
    Toolbar toolBar;

    private boolean fabExpanded = false;
    private FloatingActionButton fabManLog; //main
    private LinearLayout layoutFabAddNew; //sub2

    ListView listViewManLog;
    TextView titleTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_manlog);

        MRL1 = (RelativeLayout) findViewById(R.id.MRL1);
        toolBar = (Toolbar) findViewById(R.id.toolbar);

        fabManLog = (FloatingActionButton) this.findViewById(R.id.fabManLog);
        layoutFabAddNew = (LinearLayout) this.findViewById(R.id.layoutFabAddNew);
        fabManLog.setOnClickListener(new View.OnClickListener() {
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
                Intent intent = new Intent(MainActivityManLog.this, CreateManLogActivity.class);
                intent.putExtra(KEY_EXTRA_MANLOG_ID, 0);
                Bundle bndlanimation =
                        ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.anni1, R.anim.anni2).toBundle();
                startActivity(intent, bndlanimation);
                closeSubMenusFab();
            }
        });

        //Only main FAB is visible in the beginning
        closeSubMenusFab();

        titleTextView = (TextView) findViewById(R.id.titleTextView);

        listViewManLog = (ListView) findViewById(R.id.listViewManLog);

        titleTextView.setText("Maintenance Log");

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
                                    int position, long id) {

                Cursor itemCursor = (Cursor) MainActivityManLog.this.listView.getItemAtPosition(position);
                int manlogID = itemCursor.getInt(itemCursor.getColumnIndex(BoatLogDBHelper.MANLOG_COLUMN_ID));
                String manlogProgress = "" + itemCursor.getString(itemCursor.getColumnIndex(BoatLogDBHelper.MANLOG_COLUMN_PROGRESS));
                Intent intent = new Intent(getApplicationContext(), EditManLogActivity.class);
                intent.putExtra(KEY_EXTRA_MANLOG_ID, manlogID);
                intent.putExtra(KEY_EXTRA_MANLOG_PROGRESS, manlogProgress);
                Bundle bndlanimation =
                        ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.anni1, R.anim.anni2).toBundle();
                startActivity(intent, bndlanimation);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> listView, View view,
                                           int position, long id) {
                Cursor itemCursor = (Cursor) MainActivityManLog.this.listView.getItemAtPosition(position);
                int manlogID = itemCursor.getInt(itemCursor.getColumnIndex(BoatLogDBHelper.MANLOG_COLUMN_ID));
                String manlogProgress = "" + itemCursor.getString(itemCursor.getColumnIndex(BoatLogDBHelper.MANLOG_COLUMN_PROGRESS));
                Intent intent = new Intent(getApplicationContext(), EditManLogActivity.class);
                intent.putExtra(KEY_EXTRA_MANLOG_ID, manlogID);
                intent.putExtra(KEY_EXTRA_MANLOG_PROGRESS, manlogProgress);
                Bundle bndlanimation =
                        ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.anni1, R.anim.anni2).toBundle();
                startActivity(intent, bndlanimation);
                return true;
            }
        });

    }

    private void populateListView() {

        final Cursor cursor = dbHelper.getAllManLog();
        String[] columns = new String[]{BoatLogDBHelper.MANLOG_COLUMN_ID, BoatLogDBHelper.MANLOG_COLUMN_NAME, BoatLogDBHelper.MANLOG_COLUMN_PROGRESS};
        int[] widgets = new int[]{R.id.manlogID, manlogName, manlogProgress};
        SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter(this, R.layout.manlog_info, cursor, columns, widgets, 0);
        listView = (ListView) findViewById(R.id.listViewManLog);

        listView.setDivider(this.getResources().getDrawable(R.drawable.list_divide));
        listView.setDividerHeight(2);
        listView.setAdapter(cursorAdapter);
    }

    private void populateListViewRed() {
        final Cursor cursor = dbHelper.getAllManLog();
        String[] columns = new String[]{BoatLogDBHelper.MANLOG_COLUMN_ID, BoatLogDBHelper.MANLOG_COLUMN_NAME, BoatLogDBHelper.MANLOG_COLUMN_PROGRESS};
        int[] widgets = new int[]{R.id.manlogID, manlogName, manlogProgress};
        SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter(this, R.layout.manlog_info1, cursor, columns, widgets, 0);
        listView = (ListView) findViewById(R.id.listViewManLog);
        listView.setDivider(this.getResources().getDrawable(R.drawable.list_dividered));
        listView.setDividerHeight(2);
        listView.setAdapter(cursorAdapter);
    }

    private void NightMode() {

        MRL1.setBackgroundColor(getResources().getColor(R.color.card_background));
        toolBar.setBackgroundColor(getResources().getColor(R.color.card_background));
        titleTextView.setTextColor(getResources().getColor(R.color.night_text));
        listViewManLog.setBackgroundColor(getResources().getColor(R.color.card_background));
        fabManLog.setBackgroundColor(getResources().getColor(R.color.night_text));

    }

    //closes FAB submenus
    private void closeSubMenusFab() {
        layoutFabAddNew.setVisibility(View.INVISIBLE);
        fabManLog.setImageResource(R.drawable.ic_menu_white);
        fabExpanded = false;
    }

    //Opens FAB submenus
    private void openSubMenusFab() {
        layoutFabAddNew.setVisibility(View.VISIBLE);
        fabManLog.setImageResource(R.drawable.ic_close_white);
        fabExpanded = true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.back2, R.anim.back1);
    }
}
