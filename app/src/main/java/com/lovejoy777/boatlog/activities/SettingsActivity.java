package com.lovejoy777.boatlog.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

import com.lovejoy777.boatlog.R;


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

        getListView().setBackgroundColor(getResources().getColor(R.color.card_background));
        setTheme(R.style.DarkTheme);
        // Toast.makeText(SettingsActivity.this, "Night Mode Active", Toast.LENGTH_LONG).show();
    }

    private void screenOn() {

        //Toast.makeText(SettingsActivity.this, "Screen On", Toast.LENGTH_LONG).show();
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

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        SharedPreferences myPrefs = this.getSharedPreferences("myPrefs", MODE_PRIVATE);
        Boolean NightModeOn = myPrefs.getBoolean("switch1", false);
        Boolean ScreenOn = myPrefs.getBoolean("switch2", false);

        // Restart app to load day/night modes
        Intent restart = getBaseContext().getPackageManager()
                .getLaunchIntentForPackage(getBaseContext().getPackageName());
        restart.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(restart);

        if (NightModeOn) {
            NightMode();
            // finish();
        }

        if (ScreenOn) {
            screenOn();
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.back2, R.anim.back1);
    }
}
