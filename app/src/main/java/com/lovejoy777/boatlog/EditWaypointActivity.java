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

public class EditWaypointActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;

    private BoatLogDBHelper dbHelper;

    ImageView button_saveWaypoint;
    ImageView button_deleteWaypoint;

    ScrollView scrollView1;
    RelativeLayout MRL1;

    TextView textViewName;
    TextView textViewDescription;
    TextView textViewLocationLat;
    TextView textViewLocationLong;

    EditText nameEditText;
    EditText descriptionEditText;
    EditText latdegEditText;
    EditText latminEditText;
    EditText latsecEditText;
    EditText latnsEditText;
    EditText longdegEditText;
    EditText longminEditText;
    EditText longsecEditText;
    EditText longewEditText;

    int waypointID;
    String waypointNam;

    int theme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Initialize the associated SharedPreferences file with default values
        PreferenceManager.setDefaultValues(this, R.xml.prefs, false);
        SharedPreferences prefs1 = PreferenceManager.getDefaultSharedPreferences(this);
        setTheme(theme = getTheme(prefs1.getString("theme", "fresh")));

        super.onCreate(savedInstanceState);

        waypointID = getIntent().getIntExtra(MainActivityWaypoint.KEY_EXTRA_WAYPOINT_ID, 0);
        waypointNam = getIntent().getStringExtra(MainActivityWaypoint.KEY_EXTRA_WAYPOINT_NAME);

        setContentView(R.layout.activity_edit_waypoint);

        loadToolbarNavDrawer(waypointNam);

        button_saveWaypoint = (ImageView) findViewById(R.id.button_saveWaypoint);
        button_deleteWaypoint = (ImageView) findViewById(R.id.button_deleteWaypoint);
        TypedArray ta = obtainStyledAttributes(new int[]{R.attr.colorLightTextPrimary});
        button_saveWaypoint.setColorFilter(ta.getColor(0, Color.WHITE), PorterDuff.Mode.SRC_ATOP);
        button_deleteWaypoint.setColorFilter(ta.getColor(0, Color.WHITE), PorterDuff.Mode.SRC_ATOP);
        ta.recycle();

        scrollView1 = (ScrollView) findViewById(R.id.scrollView1);
        MRL1 = (RelativeLayout) findViewById(R.id.MRL1);

        textViewName = (TextView) findViewById(R.id.textViewName);
        textViewDescription = (TextView) findViewById(R.id.textViewDescription);
        textViewLocationLat = (TextView) findViewById(R.id.textViewLocationLat);
        textViewLocationLong = (TextView) findViewById(R.id.textViewLocationLong);

        nameEditText = (EditText) findViewById(R.id.editTextName);
        descriptionEditText = (EditText) findViewById(R.id.editTextDescription);
        latdegEditText = (EditText) findViewById(R.id.editTextLatDeg);
        latminEditText = (EditText) findViewById(R.id.editTextLatMin);
        latsecEditText = (EditText) findViewById(R.id.editTextLatSec);
        latnsEditText = (EditText) findViewById(R.id.editTextLatNS);
        longdegEditText = (EditText) findViewById(R.id.editTextLongDeg);
        longminEditText = (EditText) findViewById(R.id.editTextLongMin);
        longsecEditText = (EditText) findViewById(R.id.editTextLongSec);
        longewEditText = (EditText) findViewById(R.id.editTextLongEW);

        dbHelper = new BoatLogDBHelper(this);

        button_saveWaypoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                persistWaypoint();
            }
        });

        button_deleteWaypoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteWaypoint();
            }
        });

        Cursor rs = dbHelper.getWaypoint(waypointID);
        rs.moveToFirst();
        String waypointName = rs.getString(rs.getColumnIndex(BoatLogDBHelper.WAYPOINT_COLUMN_NAME));
        String waypointDescription = rs.getString(rs.getColumnIndex(BoatLogDBHelper.WAYPOINT_COLUMN_DESCRIPTION));
        String waypointLatDeg = rs.getString(rs.getColumnIndex(BoatLogDBHelper.WAYPOINT_COLUMN_LATDEG));
        String waypointLatMin = rs.getString(rs.getColumnIndex(BoatLogDBHelper.WAYPOINT_COLUMN_LATMIN));
        String waypointLatSec = rs.getString(rs.getColumnIndex(BoatLogDBHelper.WAYPOINT_COLUMN_LATSEC));
        String waypointLatNS = rs.getString(rs.getColumnIndex(BoatLogDBHelper.WAYPOINT_COLUMN_LATNS));
        String waypointLongDeg = rs.getString(rs.getColumnIndex(BoatLogDBHelper.WAYPOINT_COLUMN_LONGDEG));
        String waypointLongMin = rs.getString(rs.getColumnIndex(BoatLogDBHelper.WAYPOINT_COLUMN_LONGMIN));
        String waypointLongSec = rs.getString(rs.getColumnIndex(BoatLogDBHelper.WAYPOINT_COLUMN_LONGSEC));
        String waypointLongEW = rs.getString(rs.getColumnIndex(BoatLogDBHelper.WAYPOINT_COLUMN_LONGEW));
        if (!rs.isClosed()) {
            rs.close();
        }

        nameEditText.setText(waypointName);
        descriptionEditText.setText(waypointDescription);
        latdegEditText.setText(waypointLatDeg);
        latminEditText.setText(waypointLatMin);
        latsecEditText.setText(waypointLatSec);
        latnsEditText.setText(waypointLatNS);
        longdegEditText.setText(waypointLongDeg);
        longminEditText.setText(waypointLongMin);
        longsecEditText.setText(waypointLongSec);
        longewEditText.setText(waypointLongEW);
    }

    public void persistWaypoint() {
        if (dbHelper.updateWaypoint(
                waypointID,
                nameEditText.getText().toString(),
                descriptionEditText.getText().toString(),
                latdegEditText.getText().toString(),
                latminEditText.getText().toString(),
                latsecEditText.getText().toString(),
                latnsEditText.getText().toString(),
                longdegEditText.getText().toString(),
                longminEditText.getText().toString(),
                longsecEditText.getText().toString(),
                longewEditText.getText().toString()
        )) {
            Toast.makeText(getApplicationContext(), "Waypoint Edited Successful", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), MainActivityWaypoint.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            Bundle bndlanimation =
                    ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.anni1, R.anim.anni2).toBundle();
            startActivity(intent, bndlanimation);
        } else {
            Toast.makeText(getApplicationContext(), "Waypoint Edit Failed", Toast.LENGTH_SHORT).show();
        }
    }


    private void loadToolbarNavDrawer(String waypointNam) {
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
        getSupportActionBar().setTitle("Edit " + waypointNam + "");
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
                            case R.id.nav_home_edit_waypoints:
                                getSupportActionBar().setElevation(0);
                                mDrawerLayout.closeDrawers();
                                break;
                            case R.id.nav_save_waypoint:
                                saveWaypoint();
                                break;
                            case R.id.nav_delete_waypoint:
                                deleteWaypoint();
                                break;
                        }
                        return false;
                    }
                }
        );
    }

    public void saveWaypoint() {
        persistWaypoint();
    }

    public void deleteWaypoint() {
        Cursor rs = dbHelper.getWaypoint(waypointID);
        rs.moveToFirst();
        final String waypointName = rs.getString(rs.getColumnIndex(BoatLogDBHelper.WAYPOINT_COLUMN_NAME));
        if (!rs.isClosed()) {
            rs.close();
        }
            AlertDialog.Builder builder;
            builder = new AlertDialog.Builder(EditWaypointActivity.this);
            TypedArray ta = obtainStyledAttributes(new int[]{R.attr.colorTextPrimary});
            Drawable Btn = getResources().getDrawable(R.drawable.ic_delete_white_24dp);
            Btn.setColorFilter(ta.getColor(0, Color.WHITE), PorterDuff.Mode.SRC_ATOP);
            builder.setIcon(Btn);
            ta.recycle();
            builder.setTitle("Delete Waypoint?")
                    .setMessage(waypointName)
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dbHelper.deleteWaypoint(waypointID);
                            Toast.makeText(getApplicationContext(), "Deleted Successfully", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), MainActivityWaypoint.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            Bundle bndlanimation =
                                    ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.anni1, R.anim.anni2).toBundle();
                            startActivity(intent, bndlanimation);
                        }
                    })
                    .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // cancelled by user
                            Intent intent = new Intent(getApplicationContext(), MainActivityWaypoint.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            Bundle bndlanimation =
                                    ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.anni1, R.anim.anni2).toBundle();
                            startActivity(intent, bndlanimation);
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
    protected void onResume() {
        super.onResume();
    }

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