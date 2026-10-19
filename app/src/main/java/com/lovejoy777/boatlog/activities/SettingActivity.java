package com.lovejoy777.boatlog.activities;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;

import com.lovejoy777.boatlog.AlarmReceiver;
import com.lovejoy777.boatlog.R;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SettingActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences prefs1 = PreferenceManager.getDefaultSharedPreferences(this);
        setTheme(getTheme(prefs1.getString("theme", "fresh")));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.action_settings);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, new SettingsPreferenceFragment())
                    .commit();
        }
    }

    public static class SettingsPreferenceFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
        Date SAMPLE_DATE = new Date(1452805200000l);

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.prefs);
            updateDateFormatList();
        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
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
            getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
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
            ListPreference preference = (ListPreference) findPreference(preferenceKey);
            if (preference != null && preference.getEntry() != null) {
                preference.setSummary(preference.getEntry());
            }
        }

        private void setCustomDateEnabled() {
            SharedPreferences sp = getPreferenceScreen().getSharedPreferences();
            Preference customDatePref = findPreference("dateFormatCustom");
            if (customDatePref != null) {
                customDatePref.setEnabled("custom".equals(sp.getString("dateFormat", "")));
            }
        }

        private void updateDateFormatList() {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
            Resources res = getResources();

            ListPreference dateFormatPref = (ListPreference) findPreference("dateFormat");
            if (dateFormatPref == null) return;
            String[] dateFormatsValues = res.getStringArray(R.array.dateFormatsValues);
            String[] dateFormatsEntries = new String[dateFormatsValues.length];

            EditTextPreference customDateFormatPref = (EditTextPreference) findPreference("dateFormatCustom");
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
                    sdformat.applyPattern(value);
                    dateFormatsEntries[i] = sdformat.format(SAMPLE_DATE);
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
        if (sp.getString(key, "").equals("")) {
            sp.edit().remove(key).apply();
        }
    }

    private int getTheme(String themePref) {
        switch (themePref) {
            case "dark":
                return R.style.AppTheme_NoActionBar_Dark;
            default:
                return R.style.Base_Theme_Toolbar;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.back2, R.anim.back1);
    }
}
