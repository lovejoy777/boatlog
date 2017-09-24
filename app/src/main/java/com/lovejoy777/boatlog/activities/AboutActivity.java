package com.lovejoy777.boatlog.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.lovejoy777.boatlog.R;
import com.lovejoy777.boatlog.adapters.CustomListAdapter;
import com.lovejoy777.boatlog.adapters.CustomRedListAdapter;

/**
 * Created by lovejoy777 on 14/11/13.
 */

public class AboutActivity extends AppCompatActivity {

    ListView list1;

    RelativeLayout MRL1;
    ScrollView scrollView1;
    RelativeLayout RL1;
    TextView tv_caption1;
    ListView listView_Developer1;
    TextView tv_caption2;
    ListView listView_link1;

    Integer[] developerImage1 = {
            R.drawable.about_steve,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        scrollView1 = (ScrollView) findViewById(R.id.scrollView1);
        MRL1 = (RelativeLayout) findViewById(R.id.MRL1);
        RL1 = (RelativeLayout) findViewById(R.id.RL1);
        tv_caption1 = (TextView) findViewById(R.id.tv_caption1);
        listView_Developer1 = (ListView) findViewById(R.id.listView_Developer1);
        tv_caption2 = (TextView) findViewById(R.id.tv_caption2);
        listView_link1 = (ListView) findViewById(R.id.listView_link1);


        String[] ListContent1 = {
                "itellu Development Team"
        };

        String[] Developer1 = {
                "Steve Lovejoy"
        };
        String[] AppDeveloper = {
                "App Developer"
        };

        //set Toolbar
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar4);
        toolbar.setNavigationIcon(R.drawable.ic_action_bak);
        setSupportActionBar(toolbar);

        SharedPreferences myPrefs = this.getSharedPreferences("myPrefs", MODE_PRIVATE);
        Boolean NightModeOn = myPrefs.getBoolean("switch1", false);

        if (NightModeOn) {
            CustomRedListAdapter adapter1 = new
                    CustomRedListAdapter(AboutActivity.this, Developer1, AppDeveloper, developerImage1);
            list1 = (ListView) findViewById(R.id.listView_Developer1);
            list1.setAdapter(adapter1);
            list1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://plus.google.com/u/0/+SteveLovejoy/posts")));
                }
            });
            NightMode();
        }

        if (!NightModeOn) {

            CustomListAdapter adapter1 = new
                    CustomListAdapter(AboutActivity.this, Developer1, AppDeveloper, developerImage1);
            list1 = (ListView) findViewById(R.id.listView_Developer1);
            list1.setAdapter(adapter1);
            list1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://plus.google.com/u/0/+SteveLovejoy/posts")));
                }
            });
        }

        //1


        //app version textView
        TextView tv_version = (TextView) findViewById(R.id.tv_Version);
        try {
            String versionName = AboutActivity.this.getPackageManager()
                    .getPackageInfo(AboutActivity.this.getPackageName(), 0).versionName;
            tv_version.setText("Version " + versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void NightMode() {

        MRL1.setBackgroundColor(getResources().getColor(R.color.card_background));
        scrollView1.setBackgroundColor(getResources().getColor(R.color.card_background));
        RL1.setBackgroundColor(getResources().getColor(R.color.card_background));

        tv_caption1.setTextColor(getResources().getColor(R.color.night_text));
        listView_Developer1.setBackgroundColor(getResources().getColor(R.color.card_background));

        tv_caption2.setTextColor(getResources().getColor(R.color.night_text));
        listView_link1.setBackgroundColor(getResources().getColor(R.color.card_background));

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.back2, R.anim.back1);
    }
}
