package com.lovejoy777.boatlog;

import android.app.Activity;
import android.content.SharedPreferences;

/**
 * Created by steve on 17/10/17.
 */

public class CurrentLocationPreference {


    SharedPreferences myPrefs;

    public CurrentLocationPreference(Activity activity){
        myPrefs = activity.getSharedPreferences("myPrefs", Activity.MODE_PRIVATE);
    }

    String getcurrent_location(){
        return myPrefs.getString("current_location", "lat=52.96&lon=-0.85");
    }

    void setcurrent_location(String current_location){
        myPrefs.edit()
                .putString("current_location", current_location)
                .apply();
    }
}
