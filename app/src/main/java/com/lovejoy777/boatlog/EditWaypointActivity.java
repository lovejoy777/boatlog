package com.lovejoy777.boatlog;

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

    private ExampleDBHelper dbHelper;

    private boolean fabExpanded = false;
    private FloatingActionButton fabDeleteSave; //fabMainDeleteSave
    FrameLayout fabFrame;
    private LinearLayout layoutFabDelete;
    private LinearLayout layoutFabSave;

    ScrollView scrollView1;
    RelativeLayout MRL1;
    Toolbar toolBar;

    TextView textViewName;
    TextView textViewLocation;
    TextView textViewDescription;

    EditText nameEditText;
    EditText locationEditText;
    EditText descriptionEditText;

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
        textViewLocation = (TextView) findViewById(R.id.textViewLocation);
        textViewDescription = (TextView) findViewById(R.id.textViewDescription);


        nameEditText = (EditText) findViewById(R.id.editTextName);
        locationEditText = (EditText) findViewById(R.id.editTextLocation);
        descriptionEditText = (EditText) findViewById(R.id.editTextDescription);

        titleTextView.setText("Create");

        fabDeleteSave = (FloatingActionButton) this.findViewById(R.id.fabDeleteSave);
        layoutFabDelete = (LinearLayout) this.findViewById(R.id.layoutFabDelete);
        layoutFabSave = (LinearLayout) this.findViewById(R.id.layoutFabSave);

        SharedPreferences myPrefs = this.getSharedPreferences("myPrefs", MODE_PRIVATE);
        Boolean NightModeOn = myPrefs.getBoolean("switch1", false);

        if (NightModeOn) {
            NightMode();
        }

        titleTextView = (TextView) findViewById(R.id.titleTextView);


        dbHelper = new ExampleDBHelper(this);

        //nameEditText.setEnabled(true);
        nameEditText.setFocusableInTouchMode(true);
        nameEditText.setClickable(true);

        //locationEditText.setEnabled(true);
        locationEditText.setFocusableInTouchMode(true);
        locationEditText.setClickable(true);

        //descriptionEditText.setEnabled(true);
        descriptionEditText.setFocusableInTouchMode(true);
        descriptionEditText.setClickable(true);

        Cursor rs = dbHelper.getWaypoint(waypointID);
        rs.moveToFirst();
        final String waypointName = rs.getString(rs.getColumnIndex(ExampleDBHelper.WAYPOINT_COLUMN_NAME));
        String waypointLocation = rs.getString(rs.getColumnIndex(ExampleDBHelper.WAYPOINT_COLUMN_LOCATION));
        String waypointDescription = rs.getString(rs.getColumnIndex(ExampleDBHelper.WAYPOINT_COLUMN_DESCRIPTION));
        if (!rs.isClosed()) {
            rs.close();
        }

        titleTextView.setText("Edit " + waypointName);
        nameEditText.setText(waypointName);
        locationEditText.setText(waypointLocation);
        descriptionEditText.setText(waypointDescription);

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
                    builder = new android.support.v7.app.AlertDialog.Builder(EditWaypointActivity.this, android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new android.support.v7.app.AlertDialog.Builder(EditWaypointActivity.this);
                }
                builder.setTitle("Delete Waypoint?")
                        .setMessage(waypointName)

                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dbHelper.deleteWaypoint(waypointID);
                                Toast.makeText(getApplicationContext(), "Deleted Successfully", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), MainActivityWaypoint.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
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
                // closeSubMenusFabSave();
            }
        });

        //Only main FAB is visible in the beginning
        closeSubMenusFabDeleteSave();

    }


    public void persistWaypoint() {
        if (waypointID > 0) {
            if (dbHelper.updateWaypoint(waypointID, nameEditText.getText().toString(),
                    locationEditText.getText().toString(),
                    descriptionEditText.getText().toString())) {
                Toast.makeText(getApplicationContext(), "Waypoint Edited Successful", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), MainActivityWaypoint.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            } else {
                Toast.makeText(getApplicationContext(), "Waypoint Edit Failed", Toast.LENGTH_SHORT).show();
            }
        } else {
            if (dbHelper.insertWaypoint(nameEditText.getText().toString(),
                    locationEditText.getText().toString(),
                    descriptionEditText.getText().toString())) {
                Toast.makeText(getApplicationContext(), "Waypoint Saved", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Could not Save Waypoint", Toast.LENGTH_SHORT).show();
            }
            Intent intent = new Intent(getApplicationContext(), MainActivityWaypoint.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
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
        fabFrame.setBackgroundColor(Color.BLACK);
        toolBar.setBackgroundColor(Color.BLACK);
        titleTextView.setTextColor(Color.RED);

        textViewName.setTextColor(Color.RED);
        textViewLocation.setTextColor(Color.RED);
        textViewDescription.setTextColor(Color.RED);

        nameEditText.setTextColor(Color.RED);
        locationEditText.setTextColor(Color.RED);
        descriptionEditText.setTextColor(Color.RED);
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
        //Change settings icon to 'X' icon
        fabDeleteSave.setImageResource(R.drawable.ic_close_white);
        fabExpanded = true;
    }
}