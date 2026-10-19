package com.lovejoy777.boatlog.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.webkit.WebView;

import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.lovejoy777.boatlog.R;

public class MapActivity extends AppCompatActivity {

    private BottomNavigationView mBottomBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        setTheme(getTheme(prefs.getString("theme", "fresh")));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        String apiKey = prefs.getString("apiKey", getResources().getString(R.string.apiKey));

        float latitude = 0f;
        float longitude = 0f;
        try {
            latitude = prefs.getFloat("latitude", 0f);
        } catch (Exception e) {
            try {
                latitude = Float.parseFloat(prefs.getString("latitude", "0"));
            } catch (Exception ignored) {}
        }
        try {
            longitude = prefs.getFloat("longitude", 0f);
        } catch (Exception e) {
            try {
                longitude = Float.parseFloat(prefs.getString("longitude", "0"));
            } catch (Exception ignored) {}
        }

        final WebView webView = (WebView) findViewById(R.id.webView);
        if (webView != null) {
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setDomStorageEnabled(true);
            webView.getSettings().setAllowFileAccess(true);
            webView.getSettings().setAllowContentAccess(true);
            webView.loadUrl("file:///android_asset/map.html?lat=" + latitude + "&lon=" + longitude + "&appid=" + apiKey);
        }

        mBottomBar = findViewById(R.id.bottomBar);
        if (mBottomBar != null) {
            mBottomBar.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    if (webView == null) return false;
                    int tabId = item.getItemId();
                    if (tabId == R.id.map_rain) {
                        webView.loadUrl("javascript:map.removeLayer(windLayer);map.removeLayer(tempLayer);map.addLayer(rainLayer);");
                        return true;
                    } else if (tabId == R.id.map_wind) {
                        webView.loadUrl("javascript:map.removeLayer(rainLayer);map.removeLayer(tempLayer);map.addLayer(windLayer);");
                        return true;
                    } else if (tabId == R.id.map_temperature) {
                        webView.loadUrl("javascript:map.removeLayer(windLayer);map.removeLayer(rainLayer);map.addLayer(tempLayer);");
                        return true;
                    }
                    return false;
                }
            });
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.back2, R.anim.back1);
    }
}
