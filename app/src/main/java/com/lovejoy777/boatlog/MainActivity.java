package com.lovejoy777.boatlog;

import android.Manifest;
import android.app.ActivityOptions;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.lovejoy777.boatlog.activities.AboutActivity;
import com.lovejoy777.boatlog.activities.SettingsActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;


/**
 * Created by lovejoy777 on 11/10/15.
 */
public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private SwitchCompat switcher1;
    private SwitchCompat switcher2;

    private int WRITE_EXTERNAL_STORAGE_CODE = 25;
    private int ACCESS_FINE_LOCATION_CODE = 23;


    Toolbar toolBar;
    TextView titleTextView;

    ScrollView scrollView1;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolBar = (Toolbar) findViewById(R.id.toolbar);
        titleTextView = (TextView) findViewById(R.id.titleTextView);

        scrollView1 = (ScrollView) findViewById(R.id.scrollView1);
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

        textView1.setText("Ships LogBook");
        textView2.setText("Log");
        textView3.setText("Navigation");
        textView4.setText("Maintenance Log");

        img_thumbnail1.setImageResource(R.drawable.book);
        img_thumbnail2.setImageResource(R.drawable.log);
        img_thumbnail3.setImageResource(R.drawable.waypoints);
        img_thumbnail4.setImageResource(R.drawable.test1);

        RL1.setBackgroundResource(R.color.white);
        RL2.setBackgroundResource(R.color.white);
        RL3.setBackgroundResource(R.color.white);
        RL4.setBackgroundResource(R.color.white);

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_CODE);
        }

        SharedPreferences myPrefs = this.getSharedPreferences("myPrefs", MODE_PRIVATE);
        Boolean NightModeOn = myPrefs.getBoolean("switch1", false);

        if (NightModeOn) {
            loadToolbarNavDrawerRed();
            NightMode();
        }

        if (!NightModeOn) {
            loadToolbarNavDrawer();

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


    }

    private void loadToolbarNavDrawer() {
        //set Toolbar
        final android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //set NavigationDrawer
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        if (navigationView != null) {

            SharedPreferences myPrefs = this.getSharedPreferences("myPrefs", MODE_PRIVATE);
            Boolean switch1 = myPrefs.getBoolean("switch1", false);
            Boolean switch2 = myPrefs.getBoolean("switch2", false);

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
                        SharedPreferences myPrefs = MainActivity.this.getSharedPreferences("myPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor myPrefse = myPrefs.edit();
                        myPrefse.putBoolean("switch1", true);
                        myPrefse.apply();
                    } else {
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

                    Snackbar.make(v, (switcher1.isChecked()) ? "Night Mode is now On" : "Night Mode is now Off", Snackbar.LENGTH_LONG).setAction("Action", null).show();
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


                    Snackbar.make(v, (switcher2.isChecked()) ? "Screen Wake is now On" : "Screen Wake is now Off", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
            });


            navigationView.setItemTextColor(ColorStateList.valueOf(Color.DKGRAY));
            navigationView.setItemIconTintList(ColorStateList.valueOf(Color.DKGRAY));
            //navigationView.setBackgroundColor(Color.BLACK);
        }
    }

    private void loadToolbarNavDrawerRed() {
        //set Toolbar
        final android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //set NavigationDrawer
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        if (navigationView != null) {

            SharedPreferences myPrefs = this.getSharedPreferences("myPrefs", MODE_PRIVATE);
            Boolean switch1 = myPrefs.getBoolean("switch1", false);
            Boolean switch2 = myPrefs.getBoolean("switch2", false);

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
                        SharedPreferences myPrefs = MainActivity.this.getSharedPreferences("myPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor myPrefse = myPrefs.edit();
                        myPrefse.putBoolean("switch1", true);
                        myPrefse.apply();
                    } else {
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

                    Snackbar.make(v, (switcher1.isChecked()) ? "Night Mode is now On" : "Night Mode is now Off", Snackbar.LENGTH_LONG).setAction("Action", null).show();
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


                    Snackbar.make(v, (switcher2.isChecked()) ? "Screen Wake is now On" : "Screen Wake is now Off", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
            });

            navigationView.setItemTextColor(ColorStateList.valueOf(getResources().getColor(R.color.night_text)));
            navigationView.setItemIconTintList(ColorStateList.valueOf(getResources().getColor(R.color.night_text)));
            navigationView.setBackgroundColor(getResources().getColor(R.color.card_background));
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
                        Bundle bndlanimation =
                                ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.anni1, R.anim.anni2).toBundle();
                        int id = menuItem.getItemId();
                        switch (id) {
                            case R.id.nav_home:
                                // mDrawerLayout.closeDrawers();
                                getSupportActionBar().setElevation(0);
                                mDrawerLayout.closeDrawers();
                                break;

                            case R.id.nav_night_switch:
                                // Toast.makeText(MainActivity.this, "Night Mode" , Toast.LENGTH_LONG).show();
                                break;

                            case R.id.nav_screen_on_switch:
                                // Toast.makeText(MainActivity.this, "Screen on Mode" , Toast.LENGTH_LONG).show();
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
                                Intent settings = new Intent(MainActivity.this, SettingsActivity.class);
                                startActivity(settings, bndlanimation);
                                break;

                            case R.id.nav_backup:
                                if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE_CODE);
                                }
                                Backup();
                                break;

                            case R.id.nav_restore:
                                if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE_CODE);
                                }

                                File dir = new File(Environment.getExternalStorageDirectory() + Directory);

                                mFileList = dir.list();

                                android.support.v7.app.AlertDialog.Builder builder;
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    builder = new android.support.v7.app.AlertDialog.Builder(MainActivity.this, R.style.AlertDialogTheme);
                                } else {
                                    builder = new android.support.v7.app.AlertDialog.Builder(MainActivity.this, R.style.AlertDialogTheme);
                                }

                                builder.setTitle("      Select a File to Restore");
                                builder.setIcon(R.drawable.ic_save_white);
                                if (mFileList == null) {
                                    builder.create();
                                }
                                builder.setItems(mFileList, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        String mChosenFile = mFileList[which];
                                        Restore(mChosenFile);
                                        // Toast.makeText(MainActivity.this, mChosenFile , Toast.LENGTH_LONG).show();
                                    }
                                });

                                builder.show();
                                //  return true;
                                break;
                        }
                        return false;
                    }
                });

    }


    public void Backup() {

        android.support.v7.app.AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new android.support.v7.app.AlertDialog.Builder(MainActivity.this, R.style.AlertDialogTheme);
        } else {
            builder = new android.support.v7.app.AlertDialog.Builder(MainActivity.this, R.style.AlertDialogTheme);
        }
        builder.setTitle("Backup")
                .setMessage("boatlog database")

                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

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

                        Toast.makeText(MainActivity.this, "backup completed", Toast.LENGTH_LONG).show();
                    }
                })

                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // cancelled by user
                    }
                })

                .setIcon(R.drawable.ic_save_white)
                .show();

    }

    private void Restore(final String dbFileName) {

        android.support.v7.app.AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new android.support.v7.app.AlertDialog.Builder(MainActivity.this, R.style.AlertDialogTheme);
        } else {
            builder = new android.support.v7.app.AlertDialog.Builder(MainActivity.this, R.style.AlertDialogTheme);
        }
        builder.setTitle("Restore")
                .setMessage(dbFileName)

                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/boatLog/backups";
                        File dir = new File(path);
                        if (!dir.exists())
                            dir.mkdirs();


                        File dbFile = new File(path + "/" + dbFileName);

                        String outFilePath = "/data/data/com.lovejoy777.boatlog/databases/SQLiteBoatLog.db";

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

                            Toast.makeText(MainActivity.this, "restore completed", Toast.LENGTH_LONG).show();

                        } else {
                            Toast.makeText(MainActivity.this, "nothing to restore", Toast.LENGTH_LONG).show();
                        }

                    }
                })

                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // cancelled by user
                    }
                })

                .setIcon(R.drawable.ic_save_white)
                .show();

    }

    private void NightMode() {
        toolBar.setBackgroundColor(getResources().getColor(R.color.card_background));
        scrollView1.setBackgroundColor(getResources().getColor(R.color.card_background));
        // titleTextView.setTextColor(Color.RED);

        textView1.setText("Ships LogBook");
        textView2.setText("Log");
        textView3.setText("Navigation");
        textView4.setText("Maintenance Log");

        MRL1.setBackgroundColor(getResources().getColor(R.color.card_background));
        RL1.setBackgroundResource(R.color.card_background);
        RL2.setBackgroundResource(R.color.card_background);
        RL3.setBackgroundResource(R.color.card_background);
        RL4.setBackgroundResource(R.color.card_background);

        textView1.setTextColor(getResources().getColor(R.color.night_text));
        textView2.setTextColor(getResources().getColor(R.color.night_text));
        textView3.setTextColor(getResources().getColor(R.color.night_text));
        textView4.setTextColor(getResources().getColor(R.color.night_text));

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.back2, R.anim.back1);
    }

}