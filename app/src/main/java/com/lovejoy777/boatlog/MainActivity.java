package com.lovejoy777.boatlog;

import android.Manifest;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.akhgupta.easylocation.EasyLocationAppCompatActivity;
import com.akhgupta.easylocation.EasyLocationRequest;
import com.akhgupta.easylocation.EasyLocationRequestBuilder;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.LocationRequest;
import com.lovejoy777.boatlog.activities.AboutActivity;
import com.lovejoy777.boatlog.activities.SettingActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by lovejoy777 on 11/10/15.
 */
public class MainActivity extends EasyLocationAppCompatActivity {

    // GOOGLE MAPS/LOCATION SERVICES
    final String TAG = "GPS";
    long UPDATE_INTERVAL = 2 * 2000;  // 10 secs?
    long FASTEST_INTERVAL = 4000; // 2 sec
    long FALLBACK_INTERVAL = 8000; // 7 seconds

    private DrawerLayout mDrawerLayout;
    private SwitchCompat switcher1, switcher2, switcher3;

    private int WRITE_EXTERNAL_STORAGE_CODE = 25;
    int ACCESS_FINE_LOCATION_CODE = 23;

    Toolbar toolBar;
    ImageView button_refresh;

    public RelativeLayout MRL1;
    public RelativeLayout RL1;
    public RelativeLayout RL2;
    public RelativeLayout RL3;
    public RelativeLayout RL4;

    public TextView textView1;
    public ImageView img_thumbnail1;
    public TextView textView2;
    public ImageView img_thumbnail2;
    public TextView textView3;
    public ImageView img_thumbnail3;
    public TextView textView4;
    public ImageView img_thumbnail4;

    String Directory = "/boatLog/backups";
    String[] mFileList;

    int theme;

    View appView;

    // ON CREATE
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Initialize the associated SharedPreferences file with default values
        PreferenceManager.setDefaultValues(this, R.xml.prefs, false);
        SharedPreferences prefs1 = PreferenceManager.getDefaultSharedPreferences(this);
        setTheme(theme = getTheme(prefs1.getString("theme", "fresh")));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        appView = findViewById(R.id.viewApp);

        SharedPreferences prefs = this.getSharedPreferences("myPrefs", MODE_PRIVATE);
        final String newloc = prefs.getString("current_locations", "Dover");

        toolBar = (Toolbar) findViewById(R.id.toolbar);
        //loadToolbarNavDrawer();
        button_refresh = (ImageView) findViewById(R.id.button_refresh);

        TypedArray ta = obtainStyledAttributes(new int[]{R.attr.colorLightTextPrimary});
        button_refresh.setColorFilter(ta.getColor(0, Color.WHITE), PorterDuff.Mode.SRC_ATOP);
        ta.recycle();

        loadToolbarNavDrawer();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new WeatherFragment())
                    .commit();
        }

        MRL1 = (RelativeLayout) findViewById(R.id.MRL1);
        RL1 = (RelativeLayout) findViewById(R.id.RL1);
        RL2 = (RelativeLayout) findViewById(R.id.RL2);
        RL3 = (RelativeLayout) findViewById(R.id.RL3);
        RL4 = (RelativeLayout) findViewById(R.id.RL4);

        textView1 = (TextView) findViewById(R.id.textView1);
        img_thumbnail1 = (ImageView) findViewById(R.id.img_thumbnail1);
        textView2 = (TextView) findViewById(R.id.textView2);
        img_thumbnail2 = (ImageView) findViewById(R.id.img_thumbnail2);
        textView3 = (TextView) findViewById(R.id.textView3);
        img_thumbnail3 = (ImageView) findViewById(R.id.img_thumbnail3);
        textView4 = (TextView) findViewById(R.id.textView4);
        img_thumbnail4 = (ImageView) findViewById(R.id.img_thumbnail4);

        img_thumbnail1.setImageResource(R.drawable.book);
        img_thumbnail2.setImageResource(R.drawable.log);
        img_thumbnail3.setImageResource(R.drawable.waypoints);
        img_thumbnail4.setImageResource(R.drawable.test1);

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_CODE);
        }



        RL1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent bootanimactivity = new Intent(MainActivity.this, MainActivityTrips.class);
                Bundle bndlanimation =
                        ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.anni1, R.anim.anni2).toBundle();
                startActivity(bootanimactivity, bndlanimation);
            }
        });

        RL2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent bootanimactivity = new Intent(MainActivity.this, MainActivityLog.class);
                Bundle bndlanimation =
                        ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.anni1, R.anim.anni2).toBundle();
                startActivity(bootanimactivity, bndlanimation);
            }
        });

        RL3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent bootanimactivity = new Intent(MainActivity.this, MainActivityWaypoint.class);
                Bundle bndlanimation =
                        ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.anni1, R.anim.anni2).toBundle();
                startActivity(bootanimactivity, bndlanimation);
            }
        });

        RL4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent bootanimactivity = new Intent(MainActivity.this, MainActivityManLog.class);
                Bundle bndlanimation =
                        ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.anni1, R.anim.anni2).toBundle();
                startActivity(bootanimactivity, bndlanimation);
            }
        });

        // PERMISSIONS
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }

        // IS GOOGLE PLAY SERVICES AVAILIBLE
        isGooglePlayServicesAvailable();
        // if (!isLocationEnabled())
        //   showAlert();

        // EASYLOCATION SETUP
        LocationRequest locationRequest = new LocationRequest()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL);
        final EasyLocationRequest easyLocationRequest = new EasyLocationRequestBuilder()
                .setLocationRequest(locationRequest)
                .setFallBackToLastLocationTime(FALLBACK_INTERVAL)
                .build();
        //requestLocationUpdates(easyLocationRequest);
        requestSingleLocationFix(easyLocationRequest);

        button_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkAvailable()) {
                    requestSingleLocationFix(easyLocationRequest);
                } else {
                    Snackbar.make(appView, getString(R.string.msg_connection_not_available), Snackbar.LENGTH_LONG).show();
                }
            }
        });

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onLocationReceived(Location location) {
        String weatherformattedLocation;
        if (location != null) {
            weatherformattedLocation = WeatherFormattedLocation(location.getLatitude(), location.getLongitude());
            SharedPreferences myPrefs = this.getSharedPreferences("myPrefs", MODE_PRIVATE);
            Boolean LNL = myPrefs.getBoolean("switch3", false);
            if (LNL) {
                WeatherLastKnownLocation(weatherformattedLocation);
            }
        }
    }

    public static String WeatherFormattedLocation(double latitude, double longitude) {
        try {
            String finalLat = String.format(Locale.UK,"%.2f", latitude);
            String finalLong = String.format(Locale.UK,"%.2f", longitude);

            //lat=35&lon=139
            String finalString = "lat=" + finalLat + "&lon=" + finalLong;

            return finalString;
        } catch (Exception e) {

            return "" + String.format(Locale.UK,"%8.5f", latitude) + "  "
                    + String.format(Locale.UK,"%8.5f", longitude);
        }
    }

    // RESTORE
    private void WeatherLastKnownLocation(String weatherlocation) {
        changeLKL(weatherlocation);
        //Toast.makeText(WeatherMainActivity.this, "LKL is: " + weatherlocation, Toast.LENGTH_LONG).show();
    }

    // TOOLBAR
    private void loadToolbarNavDrawer() {
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
        getSupportActionBar().setTitle("Boat Log");
        ta.recycle();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        setTheme(theme = getTheme(prefs.getString("theme", "fresh")));
        boolean darkTheme = theme == R.style.AppTheme_NoActionBar_Dark;

        if (darkTheme) {
            toolBar.setPopupTheme(R.style.AppTheme_PopupOverlay_Dark);
        }

        //set NavigationDrawer
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        if (navigationView != null) {
            SharedPreferences myPrefs = this.getSharedPreferences("myPrefs", MODE_PRIVATE);
            Boolean switch1 = myPrefs.getBoolean("switch1", false);
            Boolean switch2 = myPrefs.getBoolean("switch2", false);
            Boolean switch3 = myPrefs.getBoolean("switch3", false);

            setupDrawerContent(navigationView);
            Menu menu = navigationView.getMenu();

            MenuItem nightSw = menu.findItem(R.id.nav_night_switch);
            View actionViewNightSw = MenuItemCompat.getActionView(nightSw);

            switcher1 = (SwitchCompat) actionViewNightSw.findViewById(R.id.switcher1);
            switcher1.setChecked(switch1);
            switcher1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if ((switcher1.isChecked())) {
                        SharedPreferences myPrefss = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                        SharedPreferences.Editor myPref = myPrefss.edit();
                        myPref.putString("theme", "dark");
                        myPref.apply();

                        SharedPreferences myPrefs = MainActivity.this.getSharedPreferences("myPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor myPrefse = myPrefs.edit();
                        myPrefse.putBoolean("switch1", true);
                        myPrefse.apply();
                    } else {
                        SharedPreferences myPrefss = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                        SharedPreferences.Editor myPref = myPrefss.edit();
                        myPref.putString("theme", "fresh");
                        myPref.apply();

                        SharedPreferences myPrefs = MainActivity.this.getSharedPreferences("myPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor myPrefse = myPrefs.edit();
                        myPrefse.putBoolean("switch1", false);
                        myPrefse.apply();

                    }
                    // Restart app to load day/night modes
                    Intent intent = new Intent(MainActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    Bundle bndlanimation =
                            ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.anni1, R.anim.anni2).toBundle();
                    startActivity(intent, bndlanimation);
                    startActivity(intent);

                    Snackbar.make(v, (switcher1.isChecked()) ? "Night Mode is now On" : "Night Mode is now Off", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                }
            });

            MenuItem screenOnSw = menu.findItem(R.id.nav_screen_on_switch);
            View actionViewScreenOnSw = MenuItemCompat.getActionView(screenOnSw);

            switcher2 = (SwitchCompat) actionViewScreenOnSw.findViewById(R.id.switcher1);
            switcher2.setChecked(switch2);
            switcher2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if ((switcher2.isChecked())) {
                        SharedPreferences myPrefs = MainActivity.this.getSharedPreferences("myPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor myPrefse = myPrefs.edit();
                        myPrefse.putBoolean("switch2", true);
                        myPrefse.apply();
                    } else {
                        SharedPreferences myPrefs = MainActivity.this.getSharedPreferences("myPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor myPrefse = myPrefs.edit();
                        myPrefse.putBoolean("switch2", false);
                        myPrefse.apply();
                    }


                    Snackbar.make(v, (switcher2.isChecked()) ? "Screen Wake is now On" : "Screen Wake is now Off", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                }
            });

            MenuItem weatherSw = menu.findItem(R.id.nav_weather_lkl);
            View actionViewWeatherSw = MenuItemCompat.getActionView(weatherSw);

            switcher3 = (SwitchCompat) actionViewWeatherSw.findViewById(R.id.switcher1);
            switcher3.setChecked(switch3);
            switcher3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if ((switcher3.isChecked())) {
                        SharedPreferences myPrefs = MainActivity.this.getSharedPreferences("myPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor myPrefse = myPrefs.edit();
                        myPrefse.putBoolean("switch3", true);
                        myPrefse.apply();
                    } else {
                        SharedPreferences myPrefs = MainActivity.this.getSharedPreferences("myPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor myPrefse = myPrefs.edit();
                        myPrefse.putBoolean("switch3", false);
                        myPrefse.apply();
                    }

                    Snackbar.make(v, (switcher3.isChecked()) ? "Last Known Location is now On" : "Last Known Location is now Off", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                }
            });

        }

    }

    // NAV DRAWER ONCLICK
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                mDrawerLayout.openDrawer(GravityCompat.START);

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // SET NAV DRAWER
    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        mDrawerLayout.closeDrawers();
                        menuItem.setChecked(false);
                        Bundle bndlanimation =
                                ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.anni1, R.anim.anni2).toBundle();

                        int id = menuItem.getItemId();
                        switch (id) {
                            case R.id.nav_home:
                                // mDrawerLayout.closeDrawers();
                                getSupportActionBar().setElevation(0);
                                mDrawerLayout.closeDrawers();
                                break;

                            case R.id.nav_weather_name:
                                showNameInputDialog();
                                break;

                            case R.id.nav_weather_latlong:
                                showLatLongInputDialog();
                                break;

                            case R.id.nav_weather_lkl:
                                break;

                          case R.id.nav_drive:
                              Intent drive = new Intent(MainActivity.this, BackupActivity.class);
                              startActivity(drive, bndlanimation);
                                break;

                            case R.id.nav_night_switch:
                                break;

                            case R.id.nav_screen_on_switch:
                                break;

                            case R.id.nav_tutorial:
                                Intent tutorial = new Intent(MainActivity.this, Tutorial.class);
                                startActivity(tutorial, bndlanimation);
                                break;

                            case R.id.nav_about:
                                Intent about = new Intent(MainActivity.this, AboutActivity.class);
                                startActivity(about, bndlanimation);
                                break;

                            case R.id.nav_settings:
                                Intent settings = new Intent(MainActivity.this, SettingActivity.class);
                                startActivity(settings, bndlanimation);
                                break;

                            case R.id.nav_backup:
                                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE_CODE);
                                }
                                Backup();
                                break;

                            case R.id.nav_restore:
                                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE_CODE);
                                }

                                getRestoreFile();
                                //  return true;
                                break;
                        }
                        return false;
                    }
                });

    }

    // BACKUP
    public void Backup() {

                AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(MainActivity.this);
        TypedArray ta = obtainStyledAttributes(new int[]{R.attr.colorTextPrimary});
        Drawable Btn = getResources().getDrawable(R.drawable.ic_save_white);
        Btn.setColorFilter(ta.getColor(0, Color.WHITE), PorterDuff.Mode.SRC_ATOP);
        builder.setIcon(Btn);
        ta.recycle();
                builder.setTitle("Backup")
                        .setMessage("boatlog database")
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                BackupTask();
                            }
                        })
                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // cancelled by user
                            }
                        })
                        .show();
    }
    // BACKUP
    private void BackupTask() {
        // Get Time and Date
        Calendar c = Calendar.getInstance();
        System.out.println("Current time => " + c.getTime());
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        String formattedDate = df.format(c.getTime());
        SimpleDateFormat dt = new SimpleDateFormat("HH:mm");
        String formattedTime = dt.format(c.getTime());
        String fileDate = "_" + formattedDate;
        String fileTime = "_" + formattedTime;

        final String inFileName = "/data/data/com.lovejoy777.boatlog/databases/SQLiteBoatLog.db";
        File dbFile = new File(inFileName);

        if (dbFile.exists()) {

            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/boatLog/backups";
            File dir = new File(path);
            if (!dir.exists())
                dir.mkdirs();

            // Local database
            InputStream input = null;
            try {
                input = new FileInputStream(dbFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            // Path to the external backup
            OutputStream output = null;
            try {
                output = new FileOutputStream(path + "/SQLiteBoatLog" + fileDate + fileTime + ".db");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            // transfer bytes from the Input File to the Output File
            byte[] buffer = new byte[1024];
            int length;
            try {
                while ((length = input.read(buffer)) > 0) {
                    output.write(buffer, 0, length);
                }

                output.flush();
                output.close();
                input.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Toast.makeText(MainActivity.this, "backup completed", Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(MainActivity.this, "No database to backup?", Toast.LENGTH_SHORT).show();
        }

    }

    private void getRestoreFile() {
        File dir = new File(Environment.getExternalStorageDirectory() + Directory);
        mFileList = dir.list();

        AlertDialog.Builder builder;
            builder = new AlertDialog.Builder(MainActivity.this);
            TypedArray ta = obtainStyledAttributes(new int[]{R.attr.colorTextPrimary});
            Drawable Btn = getResources().getDrawable(R.drawable.ic_restore);
            Btn.setColorFilter(ta.getColor(0, Color.WHITE), PorterDuff.Mode.SRC_ATOP);
            builder.setIcon(Btn);
        ta.recycle();
        builder.setTitle("      Select a File to Restore");
            if (mFileList == null) {
                builder.create();
            }
            builder.setItems(mFileList, new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    String mChosenFile = mFileList[which];
                    RestoreDialog(mChosenFile);
                }
            });
            builder.show();

    }

    // RESTORE
    private void RestoreDialog(final String dbFileName) {
        AlertDialog.Builder builder;

            builder = new AlertDialog.Builder(MainActivity.this);
            TypedArray ta = obtainStyledAttributes(new int[]{R.attr.colorTextPrimary});
            Drawable Btn = getResources().getDrawable(R.drawable.ic_restore);
            Btn.setColorFilter(ta.getColor(0, Color.WHITE), PorterDuff.Mode.SRC_ATOP);
            builder.setIcon(Btn);
            ta.recycle();
            builder.setTitle("Restore")
            .setMessage(dbFileName)
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            RestoreTask(dbFileName);
                        }
                    })
                    .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // cancelled by user
                        }
                    })
                    .show();

    }

    private void RestoreTask(final String dbFileName) {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/boatLog/backups";
        File dir = new File(path);
        if (!dir.exists())
            dir.mkdirs();
        File dbOutFile = new File("/data/data/com.lovejoy777.boatlog/databases/SQLiteBoatLog.db");
        File dbFile = new File(path + "/" + dbFileName);
        String outFilePath = "/data/data/com.lovejoy777.boatlog/databases/SQLiteBoatLog.db";
        if (dbOutFile.exists()) {
            if (dbFile.exists()) {
                // Local database
                InputStream input = null;
                try {
                    input = new FileInputStream(path + "/" + dbFileName);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                // Path to the external backup
                OutputStream output = null;
                try {
                    output = new FileOutputStream(outFilePath);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                // transfer bytes from the Input File to the Output File
                byte[] buffer = new byte[1024];
                int length;
                try {
                    while ((length = input.read(buffer)) > 0) {
                        output.write(buffer, 0, length);
                    }
                    output.flush();
                    output.close();
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Toast.makeText(MainActivity.this, "restore completed", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "nothing to restore", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(MainActivity.this, "Please make at least one entry before restoring a backup", Toast.LENGTH_SHORT).show();
        }
    }

    // WEATHER
    private void showNameInputDialog(){
        // WEATHER
        AlertDialog.Builder builder;

        builder = new AlertDialog.Builder(MainActivity.this);
        TypedArray ta = obtainStyledAttributes(new int[]{R.attr.colorTextPrimary});
        Drawable Btn = getResources().getDrawable(R.drawable.ic_label_outline_white_48dp);
        Btn.setColorFilter(ta.getColor(0, Color.WHITE), PorterDuff.Mode.SRC_ATOP);
        builder.setIcon(Btn);

            builder.setTitle("Enter a Place Name");
            builder.setMessage("example:\nLondon");
            final EditText input = new EditText(this);
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            input.setTextColor(ta.getColor(0, Color.WHITE));
            input.setTextSize(20);
            builder.setView(input);
        ta.recycle();

            builder.setPositiveButton("Fetch Weather", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    String tempString = "q=" + (input.getText().toString());
                    changeCity(tempString);

                }
            })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // cancel
                        }
                    })
                    .show();


    }

    // WEATHER
    private void showLatLongInputDialog(){
        AlertDialog.Builder builder;

        builder = new AlertDialog.Builder(MainActivity.this);
        TypedArray ta = obtainStyledAttributes(new int[]{R.attr.colorTextPrimary});
        Drawable restoreBtn = getResources().getDrawable(R.drawable.ic_public_white_48dp);
        restoreBtn.setColorFilter(ta.getColor(0, Color.WHITE), PorterDuff.Mode.SRC_ATOP);
        builder.setIcon(restoreBtn);

        builder.setTitle("Enter Lat/Long");
        builder.setMessage("example:\nlat=52.95&lon=-0.84");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setTextColor(ta.getColor(0, Color.WHITE));
        input.setTextSize(20);
        builder.setView(input);
        ta.recycle();

        builder.setPositiveButton("Fetch Weather", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                changeLatLong(input.getText().toString());

            }
        })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // cancel
                    }
                })
                .show();

    }

    // WEATHER
    public void changeLatLong(String latlong){
        WeatherFragment wf = (WeatherFragment)getSupportFragmentManager()
                .findFragmentById(R.id.container);
        wf.changeLatLong(latlong);
        new CurrentLocationPreference(this).setcurrent_location(latlong);
    }

    // WEATHER
    public void changeCity(String city){
        WeatherFragment wf = (WeatherFragment)getSupportFragmentManager()
                .findFragmentById(R.id.container);
        wf.changeCity(city);
        new CurrentLocationPreference(this).setcurrent_location(city);
    }

    // WEATHER
    public void changeLKL(String lnl){
        WeatherFragment wf = (WeatherFragment)getSupportFragmentManager()
                .findFragmentById(R.id.container);
        wf.changeLatLong(lnl);
        new CurrentLocationPreference(this).setcurrent_location(lnl);
    }

    // EASYLOCATION LIB METHODS
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLocationPermissionGranted() {
        showToast("Location permission granted");
    }

    @Override
    public void onLocationPermissionDenied() {
        showToast("Location permission denied");
    }

    @Override
    public void onLocationProviderEnabled() {
        showToast("Location services are now ON");
    }

    @Override
    public void onLocationProviderDisabled() {
        showToast("Location services are still Off");
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private boolean isGooglePlayServicesAvailable() {
        final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.d(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        Log.d(TAG, "This device is supported.");
        return true;
    }

    private void showAlert() {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(MainActivity.this);
        TypedArray ta = obtainStyledAttributes(new int[]{R.attr.colorTextPrimary});
        Drawable Btn = getResources().getDrawable(R.drawable.ic_location_on_white);
        Btn.setColorFilter(ta.getColor(0, Color.WHITE), PorterDuff.Mode.SRC_ATOP);
        builder.setIcon(Btn);
        ta.recycle();
        builder.setTitle("Enable Location Services")
                .setMessage("Your Locations Settings is set to 'Off'.\nPlease Enable Location to " +
                        "use this app")
                .setPositiveButton("Enable", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                        Bundle bndlanimation =
                                ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.anni1, R.anim.anni2).toBundle();
                        startActivity(myIntent, bndlanimation);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // cancelled by user
                    }
                })
                .show();
    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    private int getTheme(String themePref) {
        switch (themePref) {
            case "dark":
                return R.style.AppTheme_NoActionBar_Dark;
            default:
                return R.style.AppTheme_NoActionBar;
        }
    }

    // ON BACK PRESSED
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.back2, R.anim.back1);
        stopLocationUpdates();
    }

}