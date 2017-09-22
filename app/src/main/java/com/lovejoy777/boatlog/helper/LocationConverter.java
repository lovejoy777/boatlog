package com.lovejoy777.boatlog.helper;

import android.location.Location;
import android.support.annotation.NonNull;

/**
 * Created by steve on 11/09/17.
 */

public class LocationConverter {

    public static String getLatitudeAsDMS(Location location, int decimalPlace) {
        String strLatitude = Location.convert(location.getLatitude(), Location.FORMAT_SECONDS);
        strLatitude = replaceDelimiters(strLatitude, decimalPlace);
        strLatitude = strLatitude + " N";
        return strLatitude;
    }

    public static String getLongitudeAsDMS(Location location, int decimalPlace) {
        String strLongitude = Location.convert(location.getLongitude(), Location.FORMAT_SECONDS);
        strLongitude = replaceDelimiters(strLongitude, decimalPlace);
        strLongitude = strLongitude + " W";
        return strLongitude;
    }

    @NonNull
    private static String replaceDelimiters(String str, int decimalPlace) {
        str = str.replaceFirst(":", "°");
        str = str.replaceFirst(":", "'");
        int pointIndex = str.indexOf(".");
        int endIndex = pointIndex + 1 + decimalPlace;
        if (endIndex < str.length()) {
            str = str.substring(0, endIndex);
        }
        str = str + "\"";
        return str;
    }
}