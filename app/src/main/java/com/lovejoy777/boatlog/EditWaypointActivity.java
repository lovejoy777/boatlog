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

        titleTextView.setText("Edit " + waypointName + "");
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

        fabDeleteSave.setImageResource(R.drawable.ic_action_menu);

        fabDeleteSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fabExpanded) {
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

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void NightMode() {

        scrollView1.setBackgroundResource(R.color.card_background);
        MRL1.setBackgroundResource(R.color.card_background);
        toolBar.setBackgroundResource(R.color.card_background);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            titleTextView.setTextColor(getBaseContext().getResources().getColor(R.color.night_text, getBaseContext().getTheme()));
            textViewName.setTextColor(getBaseContext().getResources().getColor(R.color.night_text, getBaseContext().getTheme()));
            textViewLocationLat.setTextColor(getBaseContext().getResources().getColor(R.color.night_text, getBaseContext().getTheme()));
            textViewLocationLong.setTextColor(getBaseContext().getResources().getColor(R.color.night_text, getBaseContext().getTheme()));
            textViewDescription.setTextColor(getBaseContext().getResources().getColor(R.color.night_text, getBaseContext().getTheme()));


            nameEditText.setTextColor(getBaseContext().getResources().getColor(R.color.night_text, getBaseContext().getTheme()));
            descriptionEditText.setTextColor(getBaseContext().getResources().getColor(R.color.night_text, getBaseContext().getTheme()));
            latdegEditText.setTextColor(getBaseContext().getResources().getColor(R.color.night_text, getBaseContext().getTheme()));
            latminEditText.setTextColor(getBaseContext().getResources().getColor(R.color.night_text, getBaseContext().getTheme()));
            latsecEditText.setTextColor(getBaseContext().getResources().getColor(R.color.night_text, getBaseContext().getTheme()));
            latnsEditText.setTextColor(getBaseContext().getResources().getColor(R.color.night_text, getBaseContext().getTheme()));
            longdegEditText.setTextColor(getBaseContext().getResources().getColor(R.color.night_text, getBaseContext().getTheme()));
            longminEditText.setTextColor(getBaseContext().getResources().getColor(R.color.night_text, getBaseContext().getTheme()));
            longsecEditText.setTextColor(getBaseContext().getResources().getColor(R.color.night_text, getBaseContext().getTheme()));
            longewEditText.setTextColor(getBaseContext().getResources().getColor(R.color.night_text, getBaseContext().getTheme()));

        }else {
            titleTextView.setTextColor(getResources().getColor(R.color.night_text));
            textViewName.setTextColor(getResources().getColor(R.color.night_text));
            textViewLocationLat.setTextColor(getResources().getColor(R.color.night_text));
            textViewLocationLong.setTextColor(getResources().getColor(R.color.night_text));
            textViewDescription.setTextColor(getResources().getColor(R.color.night_text));

            nameEditText.setTextColor(getResources().getColor(R.color.night_text));
            descriptionEditText.setTextColor(getResources().getColor(R.color.night_text));
            latdegEditText.setTextColor(getResources().getColor(R.color.night_text));
            latminEditText.setTextColor(getResources().getColor(R.color.night_text));
            latsecEditText.setTextColor(getResources().getColor(R.color.night_text));
            latnsEditText.setTextColor(getResources().getColor(R.color.night_text));
            longdegEditText.setTextColor(getResources().getColor(R.color.night_text));
            longminEditText.setTextColor(getResources().getColor(R.color.night_text));
            longsecEditText.setTextColor(getResources().getColor(R.color.night_text));
            longewEditText.setTextColor(getResources().getColor(R.color.night_text));
        }

    }

    private void closeSubMenusFabDeleteSave() {
        layoutFabDelete.setVisibility(View.INVISIBLE);
        layoutFabSave.setVisibility(View.INVISIBLE);
        fabDeleteSave.setImageResource(R.drawable.ic_action_menu);
        fabExpanded = false;
    }

    private void openSubMenusFabDeleteSave() {
        layoutFabDelete.setVisibility(View.VISIBLE);
        layoutFabSave.setVisibility(View.VISIBLE);
        fabDeleteSave.setImageResource(R.drawable.ic_action_menu);
        fabExpanded = true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.back2, R.anim.back1);
    }
}