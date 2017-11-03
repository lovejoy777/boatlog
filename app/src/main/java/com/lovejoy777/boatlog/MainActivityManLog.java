package com.lovejoy777.boatlog;

import android.app.ActivityOptions;
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
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;

import static com.lovejoy777.boatlog.R.id.manlogName;
import static com.lovejoy777.boatlog.R.id.manlogProgress;

/**
 * Created by steve on 14/09/17.
 */

public class MainActivityManLog extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;

    public final static String KEY_EXTRA_MANLOG_ID = "KEY_EXTRA_MANLOG_ID";
    public final static String KEY_EXTRA_MANLOG_NAME = "KEY_EXTRA_MANLOG_NAME";
    public final static String KEY_EXTRA_MANLOG_PROGRESS = "KEY_EXTRA_MANLOG_PROGRESS";

    private ListView listView;
    BoatLogDBHelper dbHelper;

    ImageView button_createNewTask;

    RelativeLayout MRL1;

    ListView listViewManLog;

    int theme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        PreferenceManager.setDefaultValues(this, R.xml.prefs, false);
        SharedPreferences prefs1 = PreferenceManager.getDefaultSharedPreferences(this);
        setTheme(theme = getTheme(prefs1.getString("theme", "fresh")));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_manlog);

        loadToolbarNavDrawer();

        button_createNewTask = (ImageView) findViewById(R.id.button_createNewTask);
        TypedArray ta = obtainStyledAttributes(new int[]{R.attr.colorLightTextPrimary});
        button_createNewTask.setColorFilter(ta.getColor(0, Color.WHITE), PorterDuff.Mode.SRC_ATOP);
        ta.recycle();

        MRL1 = (RelativeLayout) findViewById(R.id.MRL1);

        listViewManLog = (ListView) findViewById(R.id.listViewManLog);

        dbHelper = new BoatLogDBHelper(this);

        button_createNewTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createTask();
            }
        });

        populateListView();

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
                String manlogName = itemCursor.getString(itemCursor.getColumnIndex(BoatLogDBHelper.MANLOG_COLUMN_NAME));
                String manlogProgress = "" + itemCursor.getString(itemCursor.getColumnIndex(BoatLogDBHelper.MANLOG_COLUMN_PROGRESS));
                Intent intent = new Intent(getApplicationContext(), EditManLogActivity.class);
                intent.putExtra(KEY_EXTRA_MANLOG_ID, manlogID);
                intent.putExtra(KEY_EXTRA_MANLOG_NAME, manlogName);
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
        listView.setDividerHeight(2);
        listView.setAdapter(cursorAdapter);
    }

    private void loadToolbarNavDrawer() {
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
        getSupportActionBar().setTitle("Tasks");
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
                            case R.id.nav_home_maintenance:
                                getSupportActionBar().setElevation(0);
                                mDrawerLayout.closeDrawers();
                                break;
                            case R.id.nav_create_maintenance:
                                createTask();
                                break;

                        }
                        return false;
                    }
                }
        );
    }

    // Create a New Task
    private void createTask() {
        Intent intent = new Intent(MainActivityManLog.this, CreateManLogActivity.class);
        intent.putExtra(KEY_EXTRA_MANLOG_ID, 0);
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
