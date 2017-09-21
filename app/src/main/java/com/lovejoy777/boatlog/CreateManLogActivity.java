package com.lovejoy777.boatlog;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import static java.lang.String.valueOf;

/**
 * Created by steve on 18/09/17.
 */

public class CreateManLogActivity extends AppCompatActivity {


    private BoatLogDBHelper dbHelper;

    ScrollView scrollView1;
    RelativeLayout MRL1;
    Toolbar toolBar;

    private FloatingActionButton fabSave; //fabMainSave
    FrameLayout fabFrame;

    TextView textViewName;
    TextView textViewDescription;
    TextView textViewParts;
    TextView textViewProgress;

    EditText nameEditText;
    EditText descriptionEditText;
    EditText partsEditText;
    //EditText progressEditText;

    TextView titleTextView;

    private Spinner spinnerProgress;

    int manlogID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_manlog);

        scrollView1 = (ScrollView) findViewById(R.id.scrollView1);
        MRL1 = (RelativeLayout) findViewById(R.id.MRL1);
        fabFrame = (FrameLayout) findViewById(R.id.fabFrame);
        toolBar = (Toolbar) findViewById(R.id.toolbar);

        titleTextView = (TextView) findViewById(R.id.titleTextView);
        textViewName = (TextView) findViewById(R.id.textViewName);
        textViewDescription = (TextView) findViewById(R.id.textViewDescription);
        textViewParts = (TextView) findViewById(R.id.textViewParts);
        textViewProgress = (TextView) findViewById(R.id.textViewProgress);

        titleTextView.setText("Create New Entry");

        nameEditText = (EditText) findViewById(R.id.editTextName);
        descriptionEditText = (EditText) findViewById(R.id.editTextDescription);
        partsEditText = (EditText) findViewById(R.id.editTextParts);
        // progressEditText = (EditText) findViewById(R.id.editTextProgress);
        // spinnerProgress = (Spinner) findViewById(R.id.spinnerProgress);

        fabSave = (FloatingActionButton) this.findViewById(R.id.fabSave);

        dbHelper = new BoatLogDBHelper(this);

        SharedPreferences myPrefs = this.getSharedPreferences("myPrefs", MODE_PRIVATE);
        Boolean NightModeOn = myPrefs.getBoolean("switch1", false);

        if (NightModeOn) {
            NightMode();
        }

        spinnerProgress = (Spinner) findViewById(R.id.spinnerProgress);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.progress_array, R.layout.progress_spinner_item);
        adapter.setDropDownViewResource(R.layout.progress_spinner_dropdown_item);
        spinnerProgress.setAdapter(adapter);

        dbHelper = new BoatLogDBHelper(this);

        fabSave.setImageResource(R.drawable.ic_save_white);
        fabSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                persistTrip();
                // closeSubMenusFabSave();
            }
        });
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
                Intent intent = new Intent(getApplicationContext(), MainActivityTrips.class);
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
                Toast.makeText(getApplicationContext(), progressString, Toast.LENGTH_SHORT).show();
                //  Toast.makeText(getApplicationContext(), "Entry Saved", Toast.LENGTH_SHORT).show();
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

    /* Request updates at startup */
    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    private void NightMode() {

        scrollView1.setBackgroundColor(Color.BLACK);
        MRL1.setBackgroundColor(Color.BLACK);
        // fabFrame.setBackgroundColor(Color.BLACK);
        toolBar.setBackgroundColor(Color.BLACK);
        titleTextView.setTextColor(Color.RED);

        textViewName.setTextColor(Color.RED);
        textViewDescription.setTextColor(Color.RED);
        textViewParts.setTextColor(Color.RED);
        textViewProgress.setTextColor(Color.RED);

        nameEditText.setTextColor(Color.RED);
        descriptionEditText.setTextColor(Color.RED);
        partsEditText.setTextColor(Color.RED);
        //progressEditText.setTextColor(Color.RED);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.back2, R.anim.back1);
    }

}