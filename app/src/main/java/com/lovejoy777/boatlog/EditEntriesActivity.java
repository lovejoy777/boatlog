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
 * Created by steve on 07/09/17.
 */

public class EditEntriesActivity extends AppCompatActivity {

    private BoatLogDBHelper dbHelper;

    private boolean fabExpanded = false;
    FloatingActionButton fabDeleteSave;
    FrameLayout fabFrame;
    private LinearLayout layoutFabDelete;
    private LinearLayout layoutFabSave;

    ScrollView scrollView1;
    RelativeLayout MRL1;
    Toolbar toolBar;

    TextView titleTextView, textViewName, textViewTime, textViewDate, textViewLocation;
    EditText nameEditText, timeEditText, dateEditText, locationEditText;
    TextView trip_idText;

    public final static String KEY_EXTRA_TRIPS_ID = "KEY_EXTRA_TRIPS_ID";
    public final static String KEY_EXTRA_TRIPS_NAME = "KEY_EXTRA_TRIPS_NAME";

    int entryID;
    int tripID;
    String tripName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_entries);

        entryID = getIntent().getIntExtra(MainActivityEntries.KEY_EXTRA_ENTRIES_ID, 0);
        tripID = getIntent().getIntExtra(MainActivityEntries.KEY_EXTRA_TRIPS_ID, 0);
        tripName = getIntent().getStringExtra(MainActivityEntries.KEY_EXTRA_TRIPS_NAME);

        scrollView1 = (ScrollView) findViewById(R.id.scrollView1);
        MRL1 = (RelativeLayout) findViewById(R.id.MRL1);
        fabFrame = (FrameLayout) findViewById(R.id.fabFrame);
        toolBar = (Toolbar) findViewById(R.id.toolbar);
        titleTextView = (TextView) findViewById(R.id.titleTextView);

        textViewName = (TextView) findViewById(R.id.textViewName);
        textViewTime = (TextView) findViewById(R.id.textViewTime);
        textViewDate = (TextView) findViewById(R.id.textViewDate);
        textViewLocation = (TextView) findViewById(R.id.textViewLocation);

        nameEditText = (EditText) findViewById(R.id.editTextName);
        timeEditText = (EditText) findViewById(R.id.editTextTime);
        dateEditText = (EditText) findViewById(R.id.editTextDate);
        locationEditText = (EditText) findViewById(R.id.editTextLocation);

        fabDeleteSave = (FloatingActionButton) this.findViewById(R.id.fabDeleteSave);
        layoutFabDelete = (LinearLayout) this.findViewById(R.id.layoutFabDelete);
        layoutFabSave = (LinearLayout) this.findViewById(R.id.layoutFabSave);

        trip_idText = (TextView) findViewById(R.id.TextViewTrip_ID);

        SharedPreferences myPrefs = this.getSharedPreferences("myPrefs", MODE_PRIVATE);
        Boolean NightModeOn = myPrefs.getBoolean("switch1", false);

        if (NightModeOn) {
            NightMode();
        }

        dbHelper = new BoatLogDBHelper(this);

        nameEditText.setFocusableInTouchMode(true);
        nameEditText.setClickable(true);

        timeEditText.setFocusableInTouchMode(true);
        timeEditText.setClickable(true);

        dateEditText.setFocusableInTouchMode(true);
        dateEditText.setClickable(true);

        locationEditText.setFocusableInTouchMode(true);
        locationEditText.setClickable(true);

        trip_idText.setFocusableInTouchMode(true);
        trip_idText.setClickable(true);

        Cursor rs = dbHelper.getEntry(entryID);
        rs.moveToFirst();
        final String entryName = rs.getString(rs.getColumnIndex(BoatLogDBHelper.ENTRY_COLUMN_NAME));
        String entryTime = rs.getString(rs.getColumnIndex(BoatLogDBHelper.ENTRY_COLUMN_TIME));
        String entryDate = rs.getString(rs.getColumnIndex(BoatLogDBHelper.ENTRY_COLUMN_DATE));
        String entryLocation = rs.getString(rs.getColumnIndex(BoatLogDBHelper.ENTRY_COLUMN_LOCATION));
        String entryTrip_ID = rs.getString(rs.getColumnIndex(BoatLogDBHelper.ENTRY_COLUMN_TRIP_ID));
        if (!rs.isClosed()) {
            rs.close();
        }

        titleTextView.setText("Edit " + entryName + "");
        nameEditText.setText(entryName);
        timeEditText.setText(entryTime);
        dateEditText.setText(entryDate);
        locationEditText.setText("" + entryLocation + "");
        trip_idText.setText(entryTrip_ID);

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
                    builder = new android.support.v7.app.AlertDialog.Builder(EditEntriesActivity.this, R.style.AlertDialogTheme);
                } else {
                    builder = new android.support.v7.app.AlertDialog.Builder(EditEntriesActivity.this, R.style.AlertDialogTheme);
                }
                builder.setTitle("Delete Entry?")
                        .setMessage(entryName)

                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dbHelper.deleteEntry(entryID);
                                Toast.makeText(getApplicationContext(), "Deleted Successfully", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), MainActivityEntries.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.putExtra(KEY_EXTRA_TRIPS_ID, tripID);
                                intent.putExtra(KEY_EXTRA_TRIPS_NAME, tripName);
                                Bundle bndlanimation =
                                        ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.anni1, R.anim.anni2).toBundle();
                                startActivity(intent, bndlanimation);
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

                fabDeleteSave.setImageResource(R.drawable.ic_action_menu);
                persistEntry();
            }
        });

        //Only main FAB is visible in the beginning
        closeSubMenusFabDeleteSave();
    }

    public void persistEntry() {

        Cursor rs = dbHelper.getEntry(entryID);
        rs.moveToFirst();
        String fab = rs.getString(rs.getColumnIndex(BoatLogDBHelper.ENTRY_COLUMN_FAV));
        if (!rs.isClosed()) {
            rs.close();
        }

        if (dbHelper.updateEntry(entryID, fab, nameEditText.getText().toString(),
                timeEditText.getText().toString(),
                dateEditText.getText().toString(),
                locationEditText.getText().toString(),
                trip_idText.getText().toString())) {
            Toast.makeText(getApplicationContext(), "Entry Edited Successful", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), MainActivityEntries.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra(KEY_EXTRA_TRIPS_NAME, tripName);
            intent.putExtra(KEY_EXTRA_TRIPS_ID, tripID);
            Bundle bndlanimation =
                    ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.anni1, R.anim.anni2).toBundle();
            startActivity(intent, bndlanimation);
        } else {
            Toast.makeText(getApplicationContext(), "Entry Edit Failed", Toast.LENGTH_SHORT).show();
        }

    }

    private void NightMode() {

        scrollView1.setBackgroundResource(R.color.card_background);
        MRL1.setBackgroundResource(R.color.card_background);
        toolBar.setBackgroundResource(R.color.card_background);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            titleTextView.setTextColor(getBaseContext().getResources().getColor(R.color.night_text, getBaseContext().getTheme()));
            textViewName.setTextColor(getBaseContext().getResources().getColor(R.color.night_text, getBaseContext().getTheme()));
            textViewTime.setTextColor(getBaseContext().getResources().getColor(R.color.night_text, getBaseContext().getTheme()));
            textViewDate.setTextColor(getBaseContext().getResources().getColor(R.color.night_text, getBaseContext().getTheme()));
            textViewLocation.setTextColor(getBaseContext().getResources().getColor(R.color.night_text, getBaseContext().getTheme()));

            nameEditText.setTextColor(getBaseContext().getResources().getColor(R.color.night_text, getBaseContext().getTheme()));
            timeEditText.setTextColor(getBaseContext().getResources().getColor(R.color.night_text, getBaseContext().getTheme()));
            dateEditText.setTextColor(getBaseContext().getResources().getColor(R.color.night_text, getBaseContext().getTheme()));
            locationEditText.setTextColor(getBaseContext().getResources().getColor(R.color.night_text, getBaseContext().getTheme()));

        }else {
            titleTextView.setTextColor(getResources().getColor(R.color.night_text));
            textViewName.setTextColor(getResources().getColor(R.color.night_text));
            textViewTime.setTextColor(getResources().getColor(R.color.night_text));
            textViewDate.setTextColor(getResources().getColor(R.color.night_text));
            textViewLocation.setTextColor(getResources().getColor(R.color.night_text));

            nameEditText.setTextColor(getResources().getColor(R.color.night_text));
            timeEditText.setTextColor(getResources().getColor(R.color.night_text));
            dateEditText.setTextColor(getResources().getColor(R.color.night_text));
            locationEditText.setTextColor(getResources().getColor(R.color.night_text));
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.back2, R.anim.back1);
    }
}