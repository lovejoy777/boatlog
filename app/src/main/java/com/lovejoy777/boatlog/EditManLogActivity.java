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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import static java.lang.String.valueOf;

/**
 * Created by steve on 18/09/17.
 */

public class EditManLogActivity extends AppCompatActivity {


    private BoatLogDBHelper dbHelper;

    ScrollView scrollView1;
    RelativeLayout MRL1;
    Toolbar toolBar;

    private boolean fabExpanded = false;
    private FloatingActionButton fabDeleteSave; //fabMainDeleteEdit
    FrameLayout fabFrame;
    private LinearLayout layoutFabDelete;
    private LinearLayout layoutFabSave;

    TextView textViewName;
    TextView textViewDescription;
    TextView textViewParts;
    TextView textViewProgress;

    EditText nameEditText;
    EditText descriptionEditText;
    EditText partsEditText;
    private Spinner spinnerProgress;

    TextView titleTextView;

    int manlogID;
    String manlogProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        manlogID = getIntent().getIntExtra(MainActivityManLog.KEY_EXTRA_MANLOG_ID, 0);
        manlogProgress = getIntent().getStringExtra(MainActivityManLog.KEY_EXTRA_MANLOG_PROGRESS);

        setContentView(R.layout.activity_edit_manlog);

        scrollView1 = (ScrollView) findViewById(R.id.scrollView1);
        MRL1 = (RelativeLayout) findViewById(R.id.MRL1);
        fabFrame = (FrameLayout) findViewById(R.id.fabFrame);
        toolBar = (Toolbar) findViewById(R.id.toolbar);

        titleTextView = (TextView) findViewById(R.id.titleTextView);
        textViewName = (TextView) findViewById(R.id.textViewName);
        textViewDescription = (TextView) findViewById(R.id.textViewDescription);
        textViewParts = (TextView) findViewById(R.id.textViewParts);
        textViewProgress = (TextView) findViewById(R.id.textViewProgress);


        nameEditText = (EditText) findViewById(R.id.editTextName);
        descriptionEditText = (EditText) findViewById(R.id.editTextDescription);
        partsEditText = (EditText) findViewById(R.id.editTextParts);
        spinnerProgress = (Spinner) findViewById(R.id.spinnerProgress);

        fabDeleteSave = (FloatingActionButton) this.findViewById(R.id.fabDeleteSave);
        layoutFabDelete = (LinearLayout) this.findViewById(R.id.layoutFabDelete);
        layoutFabSave = (LinearLayout) this.findViewById(R.id.layoutFabSave);

        titleTextView = (TextView) findViewById(R.id.titleTextView);

        SharedPreferences myPrefs = this.getSharedPreferences("myPrefs", MODE_PRIVATE);
        Boolean NightModeOn = myPrefs.getBoolean("switch1", false);

        if (NightModeOn) {
            NightMode();
        }

        if (manlogProgress.equals("Not Started")) {
            String[] progressArray = {
                    "Not Started",
                    "In Progress",
                    "Completed"
            };

            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                    R.array.progress_array, R.layout.progress_spinner_item);
            adapter.setDropDownViewResource(R.layout.progress_spinner_dropdown_item);
            spinnerProgress.setAdapter(adapter);

        } else if (manlogProgress.equals("In Progress")) {
            String[] progressArray = {
                    "In Progress",
                    "Not Started",
                    "Completed"
            };
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                    R.array.progress_array, R.layout.progress_spinner_item);
            adapter.setDropDownViewResource(R.layout.progress_spinner_dropdown_item);
            spinnerProgress.setAdapter(adapter);
        } else {
            String[] progressArray = {
                    "Completed",
                    "In Progress",
                    "Not Started"
            };
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                    R.array.progress_array, R.layout.progress_spinner_item);
            adapter.setDropDownViewResource(R.layout.progress_spinner_dropdown_item);
            spinnerProgress.setAdapter(adapter);
        }


        dbHelper = new BoatLogDBHelper(this);

        nameEditText.setFocusableInTouchMode(true);
        nameEditText.setClickable(true);

        descriptionEditText.setFocusableInTouchMode(true);
        descriptionEditText.setClickable(true);

        partsEditText.setFocusableInTouchMode(true);
        partsEditText.setClickable(true);

        Cursor rs = dbHelper.getManLog(manlogID);
        rs.moveToFirst();
        final String manlogName = rs.getString(rs.getColumnIndex(BoatLogDBHelper.MANLOG_COLUMN_NAME));
        String manlogDescription = rs.getString(rs.getColumnIndex(BoatLogDBHelper.MANLOG_COLUMN_DESCRIPTION));
        String manlogParts = rs.getString(rs.getColumnIndex(BoatLogDBHelper.MANLOG_COLUMN_PARTS_ID));
        if (!rs.isClosed()) {
            rs.close();
        }

        titleTextView.setText("Edit " + manlogName);
        nameEditText.setText(manlogName);
        descriptionEditText.setText(manlogDescription);
        partsEditText.setText(manlogParts);
        spinnerProgress.setPrompt("Progress");

        fabDeleteSave.setImageResource(R.drawable.ic_action_menu);

        fabDeleteSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fabExpanded == true) {
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
                    builder = new android.support.v7.app.AlertDialog.Builder(EditManLogActivity.this, R.style.AlertDialogTheme);
                } else {
                    builder = new android.support.v7.app.AlertDialog.Builder(EditManLogActivity.this, R.style.AlertDialogTheme);
                }
                builder.setTitle("Delete Entry?")
                        .setMessage(manlogName)

                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dbHelper.deleteManLog(manlogID);
                                Toast.makeText(getApplicationContext(), "Deleted Successfully", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), MainActivityManLog.class);
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
        String progressString = valueOf(spinnerProgress.getSelectedItem());
        if (manlogID > 0) {
            if (dbHelper.updateManLog(manlogID,
                    nameEditText.getText().toString(),
                    descriptionEditText.getText().toString(),
                    partsEditText.getText().toString(),
                    progressString)) {
                Toast.makeText(getApplicationContext(), "Entry Edited Successful", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), MainActivityManLog.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                Bundle bndlanimation =
                        ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.anni1, R.anim.anni2).toBundle();
                startActivity(intent, bndlanimation);
            } else {
                Toast.makeText(getApplicationContext(), "Entry Edit Failed", Toast.LENGTH_SHORT).show();
            }
        } else {
            if (dbHelper.insertManLog(nameEditText.getText().toString(),
                    descriptionEditText.getText().toString(),
                    partsEditText.getText().toString(),
                    progressString)) {
                Toast.makeText(getApplicationContext(), "Entry Saved", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Could not Save Entry", Toast.LENGTH_SHORT).show();
            }
            Intent intent = new Intent(getApplicationContext(), MainActivityManLog.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            Bundle bndlanimation =
                    ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.anni1, R.anim.anni2).toBundle();
            startActivity(intent, bndlanimation);
        }
    }

    private void NightMode() {


        scrollView1.setBackgroundColor(getResources().getColor(R.color.card_background));
        MRL1.setBackgroundColor(getResources().getColor(R.color.card_background));
        toolBar.setBackgroundColor(getResources().getColor(R.color.card_background));

        titleTextView.setTextColor(getResources().getColor(R.color.night_text));

        textViewName.setTextColor(getResources().getColor(R.color.night_text));
        textViewDescription.setTextColor(getResources().getColor(R.color.night_text));
        textViewParts.setTextColor(getResources().getColor(R.color.night_text));
        textViewProgress.setTextColor(getResources().getColor(R.color.night_text));

        nameEditText.setTextColor(getResources().getColor(R.color.night_text));
        descriptionEditText.setTextColor(getResources().getColor(R.color.night_text));
        partsEditText.setTextColor(getResources().getColor(R.color.night_text));

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