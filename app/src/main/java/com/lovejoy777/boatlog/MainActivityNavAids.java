package com.lovejoy777.boatlog;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


/**
 * Created by lovejoy777 on 13/10/15.
 */
public class MainActivityNavAids extends AppCompatActivity {

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

        SharedPreferences myPrefs = this.getSharedPreferences("myPrefs", MODE_PRIVATE);
        Boolean NightModeOn = myPrefs.getBoolean("switch1", false);

        if (NightModeOn) {
            setContentView(R.layout.activity_main_navaids);

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
            setContentView(R.layout.activity_main_navaids);

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

            textView1.setText("Waypoints");
            textView2.setText("test1");
            textView3.setText("test2");

            img_thumbnail1.setImageResource(R.drawable.waypoints);
            img_thumbnail2.setImageResource(R.drawable.test1);
            img_thumbnail3.setImageResource(R.drawable.test2);

        }


        RL1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent bootanimactivity = new Intent(MainActivityNavAids.this, MainActivityWaypoint.class);

                Bundle bndlanimation =
                        ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.anni1, R.anim.anni2).toBundle();
                startActivity(bootanimactivity, bndlanimation);
            }
        });

        RL2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Coming Soon!!", Toast.LENGTH_SHORT).show();
            }
        });

        RL3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Coming Soon!!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void NightMode() {


        toolBar.setBackgroundColor(getResources().getColor(R.color.card_background));
        // titleTextView.setTextColor(Color.RED);

        textView1.setText("Waypoints");
        textView2.setText("test1");
        textView3.setText("test2");

        MRL1.setBackgroundColor(getResources().getColor(R.color.card_background));
        RL1.setBackgroundResource(R.color.card_background);
        RL2.setBackgroundResource(R.color.card_background);
        RL3.setBackgroundResource(R.color.card_background);

        textView1.setTextColor(getResources().getColor(R.color.night_text));
        textView2.setTextColor(getResources().getColor(R.color.night_text));
        textView3.setTextColor(getResources().getColor(R.color.night_text));

        img_thumbnail1.setImageResource(R.drawable.waypoints);
        img_thumbnail2.setImageResource(R.drawable.test1);
        img_thumbnail3.setImageResource(R.drawable.test2);

        // Toast.makeText(MainActivityLog.this, "Night Mode", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.back2, R.anim.back1);
    }

}