package com.lovejoy777.boatlog;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;



public class CreateOrEditTripsActivity extends AppCompatActivity implements View.OnClickListener {


    private ExampleDBHelper dbHelper ;

    ScrollView scrollView1;
    RelativeLayout MRL1;
    Toolbar toolBar;

    TextView textViewName;
    TextView textViewDeparture;
    TextView textViewDestination;
    TextView textViewImage_Path;

    EditText nameEditText;
    EditText departureEditText;
    EditText destinationEditText;

    Button saveButton;
    LinearLayout buttonLayout;
    Button editButton, deleteButton,printButton;

    TextView titleTextView;

    int tripID;
    int enteriesID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tripID = getIntent().getIntExtra(MainActivityTrips.KEY_EXTRA_TRIPS_ID, 0);

        setContentView(R.layout.activity_edit_trips);

        scrollView1 = (ScrollView) findViewById(R.id.scrollView1);
        MRL1 = (RelativeLayout) findViewById(R.id.MRL1);
        toolBar = (Toolbar) findViewById(R.id.toolbar);

        titleTextView = (TextView) findViewById(R.id.titleTextView);
        textViewName = (TextView) findViewById(R.id.textViewName);
        textViewDeparture = (TextView) findViewById(R.id.textViewDeparture);
        textViewDestination = (TextView) findViewById(R.id.textViewDestination);

        titleTextView.setText("Create");

        nameEditText = (EditText) findViewById(R.id.editTextName);
        departureEditText = (EditText) findViewById(R.id.editTextDeparture);
        destinationEditText = (EditText) findViewById(R.id.editTextDestination);

        saveButton = (Button) findViewById(R.id.saveButton);
        saveButton.setOnClickListener(this);
        buttonLayout = (LinearLayout) findViewById(R.id.buttonLayout);
        editButton = (Button) findViewById(R.id.editButton);
        editButton.setOnClickListener(this);
        deleteButton = (Button) findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(this);


        dbHelper = new ExampleDBHelper(this);

        SharedPreferences myPrefs = this.getSharedPreferences("myPrefs", MODE_PRIVATE);
        Boolean NightModeOn = myPrefs.getBoolean("switch1", false);

        if (NightModeOn) {
            NightMode();
        }

        if(tripID > 0) {
            saveButton.setVisibility(View.GONE);
            buttonLayout.setVisibility(View.VISIBLE);

            titleTextView = (TextView) findViewById(R.id.titleTextView);
            titleTextView.setText("Edit");

            Cursor rs = dbHelper.getTrip(tripID);
            rs.moveToFirst();
            String tripName = rs.getString(rs.getColumnIndex(ExampleDBHelper.TRIPS_COLUMN_NAME));
            String tripDeparture = rs.getString(rs.getColumnIndex(ExampleDBHelper.TRIPS_COLUMN_DEPARTURE));
            String tripDestination = rs.getString(rs.getColumnIndex(ExampleDBHelper.TRIPS_COLUMN_DESTINATION));
            if (!rs.isClosed()) {
                rs.close();
            }

            nameEditText.setText(tripName);
            nameEditText.setFocusable(false);
            nameEditText.setClickable(false);

            departureEditText.setText((CharSequence) tripDeparture);
            departureEditText.setFocusable(false);
            departureEditText.setClickable(false);

            destinationEditText.setText((CharSequence) (tripDestination + ""));
            destinationEditText.setFocusable(false);
            destinationEditText.setClickable(false);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.saveButton:
                persistTrip();
                return;
            case R.id.editButton:
                saveButton.setVisibility(View.VISIBLE);
                buttonLayout.setVisibility(View.GONE);
                nameEditText.setEnabled(true);
                nameEditText.setFocusableInTouchMode(true);
                nameEditText.setClickable(true);

                departureEditText.setEnabled(true);
                departureEditText.setFocusableInTouchMode(true);
                departureEditText.setClickable(true);

                destinationEditText.setEnabled(true);
                destinationEditText.setFocusableInTouchMode(true);
                destinationEditText.setClickable(true);
                return;
            case R.id.deleteButton:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.deleteTrips)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dbHelper.deleteTrip(tripID);
                                dbHelper.deleteAllTripEntries(tripID);
                                Toast.makeText(getApplicationContext(), "Deleted Successfully", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), MainActivityTrips.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                            }
                        });
                AlertDialog d = builder.create();
                d.setTitle("Delete Trip?");
                d.show();
                return;


        }
    }



    public void persistTrip() {
        if(tripID > 0) {
            if(dbHelper.updateTrip(tripID, nameEditText.getText().toString(),
                    departureEditText.getText().toString(),
                    destinationEditText.getText().toString())) {
                Toast.makeText(getApplicationContext(), "Trip Update Successful", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), MainActivityTrips.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
            else {
                Toast.makeText(getApplicationContext(), "Trip Update Failed", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            if(dbHelper.insertTrip(nameEditText.getText().toString(),
                    departureEditText.getText().toString(),
                    destinationEditText.getText().toString())){
                Toast.makeText(getApplicationContext(), "Trip Inserted", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(getApplicationContext(), "Could not Insert trip", Toast.LENGTH_SHORT).show();
            }
            Intent intent = new Intent(getApplicationContext(), MainActivityTrips.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    private void NightMode() {


        scrollView1.setBackgroundColor(Color.BLACK);
        MRL1.setBackgroundColor(Color.BLACK);
        toolBar.setBackgroundColor(Color.BLACK);
        titleTextView.setTextColor(Color.RED);

        textViewName.setTextColor(Color.RED);
        textViewDeparture.setTextColor(Color.RED);
        textViewDestination.setTextColor(Color.RED);

        nameEditText.setTextColor(Color.RED);
        departureEditText.setTextColor(Color.RED);
        destinationEditText.setTextColor(Color.RED);

        buttonLayout.setBackgroundColor(Color.BLACK);

        saveButton.setBackgroundResource(R.color.card_background);
        saveButton.setTextColor(Color.RED);
        editButton.setBackgroundResource(R.color.card_background);
        editButton.setTextColor(Color.RED);
        deleteButton.setBackgroundResource(R.color.card_background);
        deleteButton.setTextColor(Color.RED);



        // Toast.makeText(MainActivityLog.this, "Night Mode", Toast.LENGTH_LONG).show();

    }
}
