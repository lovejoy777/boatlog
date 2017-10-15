package com.lovejoy777.boatlog.util;


import com.lovejoy777.boatlog.BuildConfig;

/**
 * Created by steve on 15/10/17.
 */

public class Android {
    private Android() {
    }

    public static String getApplicationId() {
        return BuildConfig.APPLICATION_ID;
    }
}
