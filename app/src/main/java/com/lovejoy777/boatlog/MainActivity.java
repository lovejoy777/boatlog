package com.lovejoy777.boatlog;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lovejoy777.boatlog.activities.AboutActivity;
import com.lovejoy777.boatlog.activities.SettingsActivity;

/**
 * Created by lovejoy777 on 11/10/15.
 */
public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;


    Toolbar toolBar;
    TextView titleTextView;


    public RelativeLayout MRL1;
    public RelativeLayout RL1;
    public RelativeLayout RL2;
    public RelativeLayout RL3;

    public TextView textView1;
    public ImageView img_thumbnail1;
    public TextView textView2;
    public ImageView img_thumbnail2;
    public TextView textView3;
    public ImageView img_thumbnail3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       // setContentView(R.layout.activity_main);

       // loadToolbarNavDrawer();

        SharedPreferences myPrefs = this.getSharedPreferences("myPrefs", MODE_PRIVATE);
        Boolean NightModeOn = myPrefs.getBoolean("switch1",false);

        if (NightModeOn) {
            setContentView(R.layout.activity_main);
            loadToolbarNavDrawerRed();

            toolBar = (Toolbar) findViewById(R.id.toolbar);
            titleTextView = (TextView) findViewById(R.id.titleTextView);

            MRL1 = (RelativeLayout) findViewById(R.id.MRL1);
            RL1 = (RelativeLayout) findViewById(R.id.RL1);
            RL2 = (RelativeLayout) findViewById(R.id.RL2);
            RL3 = (RelativeLayout) findViewById(R.id.RL3);

            textView1 = (TextView) findViewById(R.id.textView1);
            img_thumbnail1 = (ImageView) findViewById(R.id.img_thumbnail1);
            textView2 = (TextView) findViewById(R.id.textView2);
            img_thumbnail2 = (ImageView) findViewById(R.id.img_thumbnail2);
            textView3 = (TextView) findViewById(R.id.textView3);
            img_thumbnail3 = (ImageView) findViewById(R.id.img_thumbnail3);

            NightMode();

        }

        if (!NightModeOn) {
            setContentView(R.layout.activity_main);
            loadToolbarNavDrawer();

            toolBar = (Toolbar) findViewById(R.id.toolbar);
            titleTextView = (TextView) findViewById(R.id.titleTextView);

            MRL1 = (RelativeLayout) findViewById(R.id.MRL1);
            RL1 = (RelativeLayout) findViewById(R.id.RL1);
            RL2 = (RelativeLayout) findViewById(R.id.RL2);
            RL3 = (RelativeLayout) findViewById(R.id.RL3);

            textView1 = (TextView) findViewById(R.id.textView1);
            img_thumbnail1 = (ImageView) findViewById(R.id.img_thumbnail1);
            textView2 = (TextView) findViewById(R.id.textView2);
            img_thumbnail2 = (ImageView) findViewById(R.id.img_thumbnail2);
            textView3 = (TextView) findViewById(R.id.textView3);
            img_thumbnail3 = (ImageView) findViewById(R.id.img_thumbnail3);

            RL1.setBackgroundColor(Color.WHITE);
            RL2.setBackgroundColor(Color.WHITE);
            RL3.setBackgroundColor(Color.WHITE);

            textView1.setText("LogBook");
            textView2.setText("Log");
            textView3.setText("Nav Aids");

            img_thumbnail1.setImageResource(R.drawable.book);
            img_thumbnail2.setImageResource(R.drawable.log);
            img_thumbnail3.setImageResource(R.drawable.navaids);

        }











      //  SharedPreferences myPrefs = this.getSharedPreferences("myPrefs", MODE_PRIVATE);
       // Boolean NightModeOn = myPrefs.getBoolean("switch1",false);

       // if (NightModeOn) {
       //     NightMode();
       // }

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
                Intent bootanimactivity = new Intent(MainActivity.this, MainActivityNavAids.class);

                Bundle bndlanimation =
                        ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.anni1, R.anim.anni2).toBundle();
                startActivity(bootanimactivity, bndlanimation);
            }
        });
    }

    private void NightMode() {


        toolBar.setBackgroundColor(Color.BLACK);
       // titleTextView.setTextColor(Color.RED);

        textView1.setText("LogBook");
        textView2.setText("Log");
        textView3.setText("Nav Aids");

        MRL1.setBackgroundColor(Color.BLACK);
        RL1.setBackgroundResource(R.color.card_background);
        RL2.setBackgroundResource(R.color.card_background);
        RL3.setBackgroundResource(R.color.card_background);

        textView1.setTextColor(Color.RED);
        textView2.setTextColor(Color.RED);
        textView3.setTextColor(Color.RED);

        img_thumbnail1.setImageResource(R.drawable.bookblack);
        img_thumbnail2.setImageResource(R.drawable.logblack);
        img_thumbnail3.setImageResource(R.drawable.navaidsblack);

        // Toast.makeText(MainActivityLog.this, "Night Mode", Toast.LENGTH_LONG).show();

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
            setupDrawerContent(navigationView);
           // navigationView.setBackgroundColor(Color.BLACK);

           // navigationView.setItemTextColor(Color.RED);
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
            setupDrawerContent(navigationView);
            navigationView.setItemTextColor(ColorStateList.valueOf(Color.RED));
            navigationView.setItemIconTintList(ColorStateList.valueOf(Color.RED));
            navigationView.setBackgroundColor(Color.BLACK);


            // navigationView.setItemTextColor(Color.RED);
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
                                mDrawerLayout.closeDrawers();
                                getSupportActionBar().setElevation(0);
                                break;
                            case R.id.nav_about:
                                Intent about = new Intent(MainActivity.this, AboutActivity.class);
                                startActivity(about, bndlanimation);
                                break;


                            case R.id.nav_settings:
                                Intent settings = new Intent(MainActivity.this, SettingsActivity.class);
                                startActivity(settings, bndlanimation);
                                break;
                        }
                        return false;
                    }
                });

    }


}