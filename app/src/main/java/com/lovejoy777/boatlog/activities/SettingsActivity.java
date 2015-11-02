package com.lovejoy777.boatlog.activities;

import android.content.ComponentName;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.lovejoy777.boatlog.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by lovejoy777 on 14/11/13.
 */

public class SettingsActivity extends PreferenceActivity implements

       SharedPreferences.OnSharedPreferenceChangeListener {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PreferenceManager prefMgr = getPreferenceManager();
        prefMgr.setSharedPreferencesName("myPrefs");
        addPreferencesFromResource(R.xml.settings);

        SharedPreferences myPrefs = this.getSharedPreferences("myPrefs", MODE_PRIVATE);
        Boolean NightModeOn = myPrefs.getBoolean("switch1", false);

        if (NightModeOn) {
            NightMode();
        }



    }

    private void NightMode() {

        getListView().setBackgroundColor(Color.BLACK);
        setTheme(R.style.DarkTheme);
               // Toast.makeText(SettingsActivity.this, "Night Mode Active", Toast.LENGTH_LONG).show();

    }



    private void screenOn() {

        //Toast.makeText(SettingsActivity.this, "Screen On", Toast.LENGTH_LONG).show();

    }






    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.back2, R.anim.back1);
    }



    @Override
    protected void onResume() {
        super.onResume();
        // Set up a listener whenever a key changes
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister the listener whenever a key changes
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,String key)
    {
        SharedPreferences myPrefs = this.getSharedPreferences("myPrefs", MODE_PRIVATE);
        Boolean NightModeOn = myPrefs.getBoolean("switch1", false);
        Boolean ScreenOn = myPrefs.getBoolean("switch2",false);


        if (NightModeOn) {
            NightMode();
            finish();
        }



        if (ScreenOn) {
            screenOn();
        }

    }

}
