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

public class EditTripsActivity extends AppCompatActivity {

    private BoatLogDBHelper dbHelper;

    ScrollView scrollView1;
    RelativeLayout MRL1;
    Toolbar toolBar;

    private boolean fabExpanded = false;
    private FloatingActionButton fabDeleteSave;
    FrameLayout fabFrame;
    private LinearLayout layoutFabDelete;
    private LinearLayout layoutFabSave;

    TextView textViewName;
    TextView textViewDeparture;
    TextView textViewDestination;

    EditText nameEditText;
    EditText departureEditText;
    EditText destinationEditText;

    TextView titleTextView;

    int tripID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tripID = getIntent().getIntExtra(MainActivityTrips.KEY_EXTRA_TRIPS_ID, 0);

        setContentView(R.layout.activity_edit_trips);

        scrollView1 = (ScrollView) findViewById(R.id.scrollView1);
        MRL1 = (RelativeLayout) findViewById(R.id.MRL1);
        fabFrame = (FrameLayout) findViewById(R.id.fabFrame);
        toolBar = (Toolbar) findViewById(R.id.toolbar);

        titleTextView = (TextView) findViewById(R.id.titleTextView);
        textViewName = (TextView) findViewById(R.id.textViewName);
        textViewDeparture = (TextView) findViewById(R.id.textViewDeparture);
        textViewDestination = (TextView) findViewById(R.id.textViewDestination);


        nameEditText = (EditText) findViewById(R.id.editTextName);
        departureEditText = (EditText) findViewById(R.id.editTextDeparture);
        destinationEditText = (EditText) findViewById(R.id.editTextDestination);

        fabDeleteSave = (FloatingActionButton) this.findViewById(R.id.fabDeleteSave);
        layoutFabDelete = (LinearLayout) this.findViewById(R.id.layoutFabDelete);
        layoutFabSave = (LinearLayout) this.findViewById(R.id.layoutFabSave);

        titleTextView = (TextView) findViewById(R.id.titleTextView);

        SharedPreferences myPrefs = this.getSharedPreferences("myPrefs", MODE_PRIVATE);
        Boolean NightModeOn = myPrefs.getBoolean("switch1", false);

        if (NightModeOn) {
            NightMode();
        }

        dbHelper = new BoatLogDBHelper(this);

        nameEditText.setFocusableInTouchMode(true);
        nameEditText.setClickable(true);
        departureEditText.setFocusableInTouchMode(true);
        departureEditText.setClickable(true);
        destinationEditText.setFocusableInTouchMode(true);
        destinationEditText.setClickable(true);

        Cursor rs = dbHelper.getTrip(tripID);
        rs.moveToFirst();
        final String tripName = rs.getString(rs.getColumnIndex(BoatLogDBHelper.TRIPS_COLUMN_NAME));
        String tripDeparture = rs.getString(rs.getColumnIndex(BoatLogDBHelper.TRIPS_COLUMN_DEPARTURE));
        String tripDestination = rs.getString(rs.getColumnIndex(BoatLogDBHelper.TRIPS_COLUMN_DESTINATION));
        if (!rs.isClosed()) {
            rs.close();
        }

        titleTextView.setText("Edit " + tripName + "");
        nameEditText.setText(tripName);
        departureEditText.setText(tripDeparture);
        destinationEditText.setText("" + tripDestination + "");

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
                    builder = new android.support.v7.app.AlertDialog.Builder(EditTripsActivity.this, R.style.AlertDialogTheme);
                } else {
                    builder = new android.support.v7.app.AlertDialog.Builder(EditTripsActivity.this, R.style.AlertDialogTheme);
                }
                builder.setTitle("Delete Trip?")
                        .setMessage(tripName)

                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dbHelper.deleteTrip(tripID);
                                dbHelper.deleteAllTripEntries(tripID);
                                Toast.makeText(getApplicationContext(), "Deleted Successfully", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), MainActivityTrips.class);
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
                persistTrip();
            }
        });

        //Only main FAB is visible in the beginning
        closeSubMenusFabDeleteSave();

    }

    public void persistTrip() {
        if (tripID > 0) {
            if (dbHelper.updateTrip(tripID, nameEditText.getText().toString(),
                    departureEditText.getText().toString(),
                    destinationEditText.getText().toString())) {
                Toast.makeText(getApplicationContext(), "Trip Edited Successful", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), MainActivityTrips.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                Bundle bndlanimation =
                        ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.anni1, R.anim.anni2).toBundle();
                startActivity(intent, bndlanimation);
            } else {
                Toast.makeText(getApplicationContext(), "Trip Edit Failed", Toast.LENGTH_SHORT).show();
            }
        } else {
            if (dbHelper.insertTrip(nameEditText.getText().toString(),
                    departureEditText.getText().toString(),
                    destinationEditText.getText().toString())) {
                Toast.makeText(getApplicationContext(), "Trip Saved", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Could not Save trip", Toast.LENGTH_SHORT).show();
            }
            Intent intent = new Intent(getApplicationContext(), MainActivityTrips.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            Bundle bndlanimation =
                    ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.anni1, R.anim.anni2).toBundle();
            startActivity(intent, bndlanimation);
        }
    }

    private void NightMode() {

        scrollView1.setBackgroundResource(R.color.card_background);
        MRL1.setBackgroundResource(R.color.card_background);
        toolBar.setBackgroundResource(R.color.card_background);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            titleTextView.setTextColor(getBaseContext().getResources().getColor(R.color.night_text, getBaseContext().getTheme()));
            textViewName.setTextColor(getBaseContext().getResources().getColor(R.color.night_text, getBaseContext().getTheme()));
            textViewDeparture.setTextColor(getBaseContext().getResources().getColor(R.color.night_text, getBaseContext().getTheme()));
            textViewDestination.setTextColor(getBaseContext().getResources().getColor(R.color.night_text, getBaseContext().getTheme()));

            nameEditText.setTextColor(getBaseContext().getResources().getColor(R.color.night_text, getBaseContext().getTheme()));
            departureEditText.setTextColor(getBaseContext().getResources().getColor(R.color.night_text, getBaseContext().getTheme()));
            destinationEditText.setTextColor(getBaseContext().getResources().getColor(R.color.night_text, getBaseContext().getTheme()));

        }else {
            titleTextView.setTextColor(getResources().getColor(R.color.night_text));
            textViewName.setTextColor(getResources().getColor(R.color.night_text));
            textViewDeparture.setTextColor(getResources().getColor(R.color.night_text));
            textViewDestination.setTextColor(getResources().getColor(R.color.night_text));

            nameEditText.setTextColor(getResources().getColor(R.color.night_text));
            departureEditText.setTextColor(getResources().getColor(R.color.night_text));
            destinationEditText.setTextColor(getResources().getColor(R.color.night_text));
        }

    }

    //closes FAB submenus delete & edit
    private void closeSubMenusFabDeleteSave() {
        layoutFabDelete.setVisibility(View.INVISIBLE);
        layoutFabSave.setVisibility(View.INVISIBLE);
        fabDeleteSave.setImageResource(R.drawable.ic_action_menu);
        fabExpanded = false;
    }

    //Opens FAB submenus
    private void openSubMenusFabDeleteSave() {
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
