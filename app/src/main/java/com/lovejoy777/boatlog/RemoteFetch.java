package com.lovejoy777.boatlog;

import android.content.Context;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by steve on 17/10/17.
 */

public class RemoteFetch {
    private static String BASE_URL = "http://api.openweathermap.org/data/2.5/weather?";
    private static String SEC_URL = "&units=metric";

   // http://api.openweathermap.org/data/2.5/weather?id=lat=52.96&lon=-0.86&APPID=641cc2423fd5c5ed715a35362fe1ff73

    public static JSONObject getJSON(Context context, String chosen_location){
        try {
            URL url = new URL(BASE_URL + chosen_location + SEC_URL);
            HttpURLConnection connection =
                    (HttpURLConnection)url.openConnection();

            connection.addRequestProperty("x-api-key",
                    context.getString(R.string.open_weather_maps_app_id));

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
        }catch(Exception e){
            return null;
        }
    }
}
