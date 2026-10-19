package com.lovejoy777.boatlog.activities;

import android.Manifest;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.lovejoy777.boatlog.AlarmReceiver;
import com.lovejoy777.boatlog.MainActivity;
import com.lovejoy777.boatlog.MainActivityLog;
import com.lovejoy777.boatlog.R;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SettingActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {


    int theme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        PreferenceManager.setDefaultValues(this, R.xml.prefs, false);
        SharedPreferences prefs1 = PreferenceManager.getDefaultSharedPreferences(this);
        setTheme(theme = getCustomTheme(prefs1.getString("theme", "fresh")));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        loadToolbarNavDrawer();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, new SettingsPreferenceFragment())
                    .commit();
        }
    }

    private void loadToolbarNavDrawer() {

        //set Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar5);

        if (toolbar != null) {
            toolbar.setNavigationIcon(R.drawable.ic_action_bak);
            setSupportActionBar(toolbar);

            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setElevation(4);
            TypedArray ta = obtainStyledAttributes(new int[]{R.attr.colorLightTextPrimary});

            if (toolbar != null) {
                toolbar.setTitleTextColor(ta.getColor(0, Color.WHITE));
            }

            getSupportActionBar().setTitle("Weather Settings");
            ta.recycle();
        }
    }


    public static class SettingsPreferenceFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {
        Date SAMPLE_DATE = new Date(1452805200000L);

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.prefs, rootKey);
            updateDateFormatList();
        }

        @Override
        public void onResume() {
            super.onResume();
            if (getPreferenceManager() != null && getPreferenceManager().getSharedPreferences() != null) {
                getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
            }
            setListPreferenceSummary("unit");
            setListPreferenceSummary("lengthUnit");
            setListPreferenceSummary("speedUnit");
            setListPreferenceSummary("pressureUnit");
            setListPreferenceSummary("refreshInterval");
            setListPreferenceSummary("windDirectionFormat");
            setCustomDateEnabled();
        }

        @Override
        public void onPause() {
            super.onPause();
            if (getPreferenceManager() != null && getPreferenceManager().getSharedPreferences() != null) {
                getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
            }
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key == null) return;
            switch (key) {
                case "unit":
                case "lengthUnit":
                case "speedUnit":
                case "pressureUnit":
                case "windDirectionFormat":
                case "refreshInterval":
                    setListPreferenceSummary(key);
                    break;
                case "dateFormat":
                    setCustomDateEnabled();
                    setListPreferenceSummary(key);
                    break;
                case "dateFormatCustom":
                    updateDateFormatList();
                    break;
            }
        }

        private void setListPreferenceSummary(String preferenceKey) {
            ListPreference preference = findPreference(preferenceKey);
            if (preference != null && preference.getEntry() != null) {
                preference.setSummary(preference.getEntry());
            }
        }

        private void setCustomDateEnabled() {
            SharedPreferences sp = getPreferenceManager() != null ? getPreferenceManager().getSharedPreferences() : null;
            EditTextPreference customDatePref = findPreference("dateFormatCustom");
            if (customDatePref != null && sp != null) {
                customDatePref.setEnabled("custom".equals(sp.getString("dateFormat", "")));
            }
        }

        private void updateDateFormatList() {
            if (!isAdded()) return;
            SharedPreferences sp = getPreferenceManager() != null ? getPreferenceManager().getSharedPreferences() : null;
            if (sp == null) return;
            Resources res = getResources();

            ListPreference dateFormatPref = findPreference("dateFormat");
            if (dateFormatPref == null) return;
            String[] dateFormatsValues = res.getStringArray(R.array.dateFormatsValues);
            String[] dateFormatsEntries = new String[dateFormatsValues.length];

            EditTextPreference customDateFormatPref = findPreference("dateFormatCustom");
            if (customDateFormatPref != null) {
                customDateFormatPref.setDefaultValue(dateFormatsValues[0]);
            }

            SimpleDateFormat sdformat = new SimpleDateFormat();
            for (int i = 0; i < dateFormatsValues.length; i++) {
                String value = dateFormatsValues[i];
                if ("custom".equals(value)) {
                    String renderedCustom;
                    try {
                        sdformat.applyPattern(sp.getString("dateFormatCustom", dateFormatsValues[0]));
                        renderedCustom = sdformat.format(SAMPLE_DATE);
                    } catch (IllegalArgumentException e) {
                        renderedCustom = res.getString(R.string.error_dateFormat);
                    }
                    dateFormatsEntries[i] = String.format("%s:\n%s",
                            res.getString(R.string.setting_dateFormatCustom),
                            renderedCustom);
                } else {
                    try {
                        sdformat.applyPattern(value);
                        dateFormatsEntries[i] = sdformat.format(SAMPLE_DATE);
                    } catch (IllegalArgumentException e) {
                        dateFormatsEntries[i] = value;
                    }
                }
            }

            dateFormatPref.setDefaultValue(dateFormatsValues[0]);
            dateFormatPref.setEntries(dateFormatsEntries);

            setListPreferenceSummary("dateFormat");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key == null) return;
        switch (key) {
            case "refreshInterval":
                AlarmReceiver.setRecurringAlarm(this);
                break;
            case "updateLocationAutomatically":
                if (sharedPreferences.getBoolean(key, false)) {
                    requestReadLocationPermission();
                }
                break;
            case "apiKey":
                checkKey(key);
                break;
        }
    }

    private void requestReadLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        WeatherMainActivity.MY_PERMISSIONS_ACCESS_FINE_LOCATION);
            }
        }
    }

    private void checkKey(String key) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        if (sp.getString(key, "").isEmpty()) {
            sp.edit().remove(key).apply();
        }
    }

    private int getCustomTheme(String themePref) {
        if ("dark".equals(themePref)) {
            return R.style.AppTheme_NoActionBar_Dark;
        }
        return R.style.AppTheme_NoActionBar;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.back2, R.anim.back1);
    }
}

