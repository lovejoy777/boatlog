package com.lovejoy777.boatlog;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * Created by steve on 30/09/17.
 */

public class Tutorial extends AppCompatActivity {

    RelativeLayout MRL1;
    ScrollView scrollView1;
    RelativeLayout RL1;
    ImageView screen_shot;
    TextView tv_version;

    int theme;

    final String youtube_path = "https://www.youtube.com/watch?v=p0vaeSra1Ns&index=1&list=PLqay3RH2Sx9oe8QXWPVK47GJrazXGqxs-";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Initialize the associated SharedPreferences file with default values
        PreferenceManager.setDefaultValues(this, R.xml.prefs, false);
        SharedPreferences prefs1 = PreferenceManager.getDefaultSharedPreferences(this);
        setTheme(theme = getTheme(prefs1.getString("theme", "fresh")));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_tutorial);

        scrollView1 = (ScrollView) findViewById(R.id.scrollView1);
        MRL1 = (RelativeLayout) findViewById(R.id.MRL1);
        RL1 = (RelativeLayout) findViewById(R.id.RL1);
        screen_shot = (ImageView) findViewById(R.id.screen_shot);
        tv_version = (TextView) findViewById(R.id.tv_Version);

        //set Toolbar
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar4);
        toolbar.setNavigationIcon(R.drawable.ic_action_bak);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //app version textView
        TextView tv_version = (TextView) findViewById(R.id.tv_Version);
        try {
            String versionName = Tutorial.this.getPackageManager()
                    .getPackageInfo(Tutorial.this.getPackageName(), 0).versionName;
            tv_version.setText("Version " + versionName + "");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        screen_shot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent bootanimactivity = new Intent(Intent.ACTION_VIEW, Uri.parse(youtube_path));
                Bundle bndlanimation =
                        ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.anni1, R.anim.anni2).toBundle();
                startActivity(bootanimactivity, bndlanimation);
            }
        });
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