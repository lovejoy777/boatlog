package com.lovejoy777.boatlog;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by steve on 14/09/17.
 */

public class MainActivityMaint extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_entries);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.back2, R.anim.back1);
    }
}
