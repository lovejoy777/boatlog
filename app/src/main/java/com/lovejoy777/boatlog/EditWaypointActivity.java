package com.lovejoy777.boatlog;

import android.app.ActivityOptions;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by steve on 08/09/17.
 */

public class EditWaypointActivity extends AppCompatActivity {

    private BoatLogDBHelper dbHelper;

    private boolean fabExpanded = false;
    private FloatingActionButton fabDeleteSave; //fabMainDeleteSave
    FrameLayout fabFrame;
    private LinearLayout layoutFabDelete;
    private LinearLayout layoutFabSave;

    ScrollView scrollView1;
    RelativeLayout MRL1;
    Toolbar toolBar;

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

    TextView titleTextView;

    int waypointID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        waypointID = getIntent().getIntExtra(MainActivityWaypoint.KEY_EXTRA_WAYPOINT_ID, 0);

        setContentView(R.layout.activity_edit_waypoint);

        scrollView1 = (ScrollView) findViewById(R.id.scrollView1);
        MRL1 = (RelativeLayout) findViewById(R.id.MRL1);
        fabFrame = (FrameLayout) findViewById(R.id.fabFrame);
        toolBar = (Toolbar) findViewById(R.id.toolbar);

        titleTextView = (TextView) findViewById(R.id.titleTextView);
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

        fabDeleteSave = (FloatingActionButton) this.findViewById(R.id.fabDeleteSave);
        layoutFabDelete = (LinearLayout) this.findViewById(R.id.layoutFabDelete);
        layoutFabSave = (LinearLayout) this.findViewById(R.id.layoutFabSave);

        SharedPreferences myPrefs = this.getSharedPreferences("myPrefs", MODE_PRIVATE);
        Boolean NightModeOn = myPrefs.getBoolean("switch1", false);

        if (NightModeOn) {
            NightMode();
        }

        dbHelper = new BoatLogDBHelper(this);

   Cursor rs = dbHelper.getWaypoint(waypointID);
        rs.moveToFirst();
        final String waypointName = rs.getString(rs.getColumnIndex(BoatLogDBHelper.WAYPOINT_COLUMN_NAME));
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

        titleTextView.setText("Edit " + waypointName);
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

        fabDeleteSave.setImageResource(R.drawable.ic_menu_white);

        fabDeleteSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fabExpanded == true){
                    closeSubMenusFabDeleteSave();
                } else {
                    openSubMenusFabDeleteSave();
                }
            }
        });

        // DELETE subFab button
        layoutFabDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                android.support.v7.app.AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new android.support.v7.app.AlertDialog.Builder(EditWaypointActivity.this, R.style.AlertDialogTheme);
                } else {
                    builder = new android.support.v7.app.AlertDialog.Builder(EditWaypointActivity.this, R.style.AlertDialogTheme);
                }
                builder.setTitle("Delete Waypoint?")
                       // .setMessage(waypointName)

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
                            }
                        })

                        .setIcon(R.drawable.ic_delete_white)
                        .show();

            }
        });

        // ADD NEW subFab button
        layoutFabSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                fabDeleteSave.setImageResource(R.drawable.ic_menu_white);
                persistWaypoint();
            }
        });

        //Only main FAB is visible in the beginning
        closeSubMenusFabDeleteSave();

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

    private void NightMode() {

        scrollView1.setBackgroundColor(Color.BLACK);
        MRL1.setBackgroundColor(Color.BLACK);
        toolBar.setBackgroundColor(Color.BLACK);
        titleTextView.setTextColor(Color.RED);

        nameEditText.setTextColor(Color.RED);
        descriptionEditText.setTextColor(Color.RED);
        textViewName.setTextColor(Color.RED);
        textViewLocationLat.setTextColor(Color.RED);
        textViewLocationLong.setTextColor(Color.RED);
        textViewDescription.setTextColor(Color.RED);

        nameEditText.setTextColor(Color.RED);
        descriptionEditText.setTextColor(Color.RED);
        latdegEditText.setTextColor(Color.RED);
        latminEditText.setTextColor(Color.RED);
        latsecEditText.setTextColor(Color.RED);
        latnsEditText.setTextColor(Color.RED);
        longdegEditText.setTextColor(Color.RED);
        longminEditText.setTextColor(Color.RED);
        longsecEditText.setTextColor(Color.RED);
        longewEditText.setTextColor(Color.RED);
    }

    //closes FAB submenus delete & edit
    private void closeSubMenusFabDeleteSave(){
        layoutFabDelete.setVisibility(View.INVISIBLE);
        layoutFabSave.setVisibility(View.INVISIBLE);
        fabDeleteSave.setImageResource(R.drawable.ic_menu_white);
        fabExpanded = false;
    }

    //Opens FAB submenus
    private void openSubMenusFabDeleteSave(){
        layoutFabDelete.setVisibility(View.VISIBLE);
        layoutFabSave.setVisibility(View.VISIBLE);
        fabDeleteSave.setImageResource(R.drawable.ic_close_white);
        fabExpanded = true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.back2, R.anim.back1);
    }
}