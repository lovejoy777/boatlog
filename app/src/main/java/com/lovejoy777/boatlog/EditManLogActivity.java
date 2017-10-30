package com.lovejoy777.boatlog;

import android.app.ActivityOptions;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
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

    private DrawerLayout mDrawerLayout;

    private BoatLogDBHelper dbHelper;

    ImageView button_saveTask;
    ImageView button_deleteTask;

    ScrollView scrollView1;
    RelativeLayout MRL1;

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

    int theme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Initialize the associated SharedPreferences file with default values
        PreferenceManager.setDefaultValues(this, R.xml.prefs, false);
        SharedPreferences prefs1 = PreferenceManager.getDefaultSharedPreferences(this);
        setTheme(theme = getTheme(prefs1.getString("theme", "fresh")));

        super.onCreate(savedInstanceState);

        manlogID = getIntent().getIntExtra(MainActivityManLog.KEY_EXTRA_MANLOG_ID, 0);
        manlogProgress = getIntent().getStringExtra(MainActivityManLog.KEY_EXTRA_MANLOG_PROGRESS);

        setContentView(R.layout.activity_edit_manlog);

        loadToolbarNavDrawer();
        button_saveTask = (ImageView) findViewById(R.id.button_saveTask);
        button_deleteTask = (ImageView) findViewById(R.id.button_deleteTask);

        scrollView1 = (ScrollView) findViewById(R.id.scrollView1);
        MRL1 = (RelativeLayout) findViewById(R.id.MRL1);

        titleTextView = (TextView) findViewById(R.id.titleTextView);
        textViewName = (TextView) findViewById(R.id.textViewName);
        textViewDescription = (TextView) findViewById(R.id.textViewDescription);
        textViewParts = (TextView) findViewById(R.id.textViewParts);
        textViewProgress = (TextView) findViewById(R.id.textViewProgress);

        nameEditText = (EditText) findViewById(R.id.editTextName);
        descriptionEditText = (EditText) findViewById(R.id.editTextDescription);
        partsEditText = (EditText) findViewById(R.id.editTextParts);
        spinnerProgress = (Spinner) findViewById(R.id.spinnerProgress);

        titleTextView = (TextView) findViewById(R.id.titleTextView);

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

        button_saveTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                persistTask();
            }
        });

        button_deleteTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteTask();
            }
        });


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

        titleTextView.setText("Edit " + manlogName + "");
        nameEditText.setText(manlogName);
        descriptionEditText.setText(manlogDescription);
        partsEditText.setText(manlogParts);
        spinnerProgress.setPrompt("Progress");

    }

    public void persistTask() {
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

    private void loadToolbarNavDrawer() {
        //set Toolbar
        final android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
                        menuItem.setChecked(true);
                        int id = menuItem.getItemId();
                        switch (id) {
                            case R.id.nav_home_edit_maintenance:
                                getSupportActionBar().setElevation(0);
                                mDrawerLayout.closeDrawers();
                                break;
                            case R.id.nav_save_maintenance:
                                saveTask();
                                break;
                            case R.id.nav_delete_maintenance:
                                deleteTask();
                                break;
                        }
                        return false;
                    }
                }
        );
    }

    public void saveTask() {
        persistTask();
    }

    public void deleteTask() {
        Cursor rs = dbHelper.getManLog(manlogID);
        rs.moveToFirst();
        final String manlogName = rs.getString(rs.getColumnIndex(BoatLogDBHelper.MANLOG_COLUMN_NAME));
        if (!rs.isClosed()) {
            rs.close();
        }
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