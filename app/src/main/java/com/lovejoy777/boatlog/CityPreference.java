package com.lovejoy777.boatlog;

import android.app.Activity;
import android.content.SharedPreferences;

/**
 * Created by steve on 17/10/17.
 */

public class CityPreference {

    SharedPreferences myPrefs;

    public CityPreference(Activity activity){
        myPrefs = activity.getSharedPreferences("myPrefs", Activity.MODE_PRIVATE);
    }

    String getCity(){
        return myPrefs.getString("city", "Nottingham, GB");
    }

    void setCity(String city){
        myPrefs.edit()
                .putString("city", city)
                .apply();
    }

}
