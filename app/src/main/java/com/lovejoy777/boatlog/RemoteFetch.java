package com.lovejoy777.boatlog;

import android.content.Context;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by steve on 17/10/17.
 */

public class RemoteFetch {
    private static String BASE_URL = "https://api.openweathermap.org/data/2.5/weather?";
    private static String SEC_URL = "&units=metric";

    public static JSONObject getJSON(Context context, String chosen_location){
        try {
            String appId = context.getString(R.string.open_weather_maps_app_id);
            String locationParam = chosen_location;
            if (locationParam != null && !locationParam.startsWith("q=") && !locationParam.startsWith("lat=")) {
                locationParam = "q=" + locationParam;
            }
            String urlStr = BASE_URL + locationParam + SEC_URL + "&appid=" + appId;
            URL url = new URL(urlStr);
            HttpURLConnection connection =
                    (HttpURLConnection)url.openConnection();

            connection.addRequestProperty("x-api-key", appId);

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));

            StringBuffer json = new StringBuffer(1024);
            String tmp="";
            while((tmp=reader.readLine())!=null)
                json.append(tmp).append("\n");
            reader.close();

            JSONObject data = new JSONObject(json.toString());

            if(data.getInt("cod") != 200){
                return null;
            }

            return data;
        } catch(Exception e){
            Log.e("RemoteFetch", "Error fetching weather data", e);
            return null;
        }
    }
}
