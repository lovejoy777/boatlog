package com.lovejoy777.boatlog;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.lovejoy777.boatlog.activities.WeatherMainActivity;

import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;
import static java.lang.Math.abs;


/**
 * Created by steve on 17/10/17.
 */

public class WeatherFragment extends Fragment implements View.OnClickListener{

    Typeface weatherFont;

    TextView cityField;
    TextView updatedField;
    TextView detailsField;
    TextView currentTemperatureField;
    TextView weatherIcon;

    Handler handler;

    public WeatherFragment(){
        handler = new Handler();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_weather, container, false);
        cityField = (TextView)rootView.findViewById(R.id.city_field);
        updatedField = (TextView)rootView.findViewById(R.id.updated_field);
        detailsField = (TextView)rootView.findViewById(R.id.details_field);
        currentTemperatureField = (TextView)rootView.findViewById(R.id.current_temperature_field);
        weatherIcon = (TextView)rootView.findViewById(R.id.weather_icon);
        weatherIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("Onclick","Onclick");

                Bundle bndlanimation =
                        ActivityOptions.makeCustomAnimation(getActivity(), R.anim.anni1, R.anim.anni2).toBundle();
                Intent intent = new Intent(getActivity(), WeatherMainActivity.class);
                startActivity(intent, bndlanimation);
            }
        });

        SharedPreferences myPrefs = this.getActivity().getSharedPreferences("myPrefs", MODE_PRIVATE);
        Boolean NightModeOn = myPrefs.getBoolean("switch1", false);

        if (NightModeOn) {
            NightMode();
        }

        weatherIcon.setTypeface(weatherFont);


        return rootView;
    }

    public void onClick(View v) {
        switch (v.getId()){

            case R.id.weather_icon:


                break;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        weatherFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/weather.ttf");

        updateWeatherData(new CurrentLocationPreference(getActivity()).getcurrent_location());


    }

    private void NightMode() {

           //  MRL1.setBackgroundResource(R.color.card_background);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            cityField.setTextColor(getContext().getResources().getColor(R.color.night_text, getContext().getTheme()));
            updatedField.setTextColor(getContext().getResources().getColor(R.color.night_text, getContext().getTheme()));
            detailsField.setTextColor(getContext().getResources().getColor(R.color.night_text, getContext().getTheme()));
            currentTemperatureField.setTextColor(getContext().getResources().getColor(R.color.night_text, getContext().getTheme()));
            weatherIcon.setTextColor(getContext().getResources().getColor(R.color.night_text, getContext().getTheme()));
        }else {
            cityField.setTextColor(getResources().getColor(R.color.night_text));
            updatedField.setTextColor(getResources().getColor(R.color.night_text));
            detailsField.setTextColor(getResources().getColor(R.color.night_text));
            currentTemperatureField.setTextColor(getResources().getColor(R.color.night_text));
            weatherIcon.setTextColor(getResources().getColor(R.color.night_text));
        }
    }

    private void updateWeatherData(final String chosen_location){
        new Thread(){
            public void run(){
                final JSONObject json = RemoteFetch.getJSON(getActivity(), chosen_location);
                if(json == null){
                    handler.post(new Runnable(){
                        public void run(){
                            Toast.makeText(getActivity(),
                                    getActivity().getString(R.string.place_not_found),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    handler.post(new Runnable(){
                        public void run(){
                            renderWeather(json);
                        }
                    });
                }
            }
        }.start();
    }

    private void renderWeather(JSONObject json){
        try {
            cityField.setText(json.getString("name").toUpperCase(Locale.UK));

            JSONObject details = json.getJSONArray("weather").getJSONObject(0);
            JSONObject main = json.getJSONObject("main");

            // WIND
            JSONObject wind = json.getJSONObject("wind");

            // COVERT WIND SPEED FROM MPS TO KNOTS
            double doubleSpeed = Double.parseDouble(wind.getString("speed"));
            float fspeed = (float)doubleSpeed;
            float newspeed = FormattedSpeed(fspeed);
            BigDecimal result;
            result=round(newspeed,1);

            detailsField.setText(
                    details.getString("description").toUpperCase(Locale.UK) +
                            "\n" + "Humidity: " + main.getString("humidity") + " %" +
                            "\n" + "Pressure: " + main.getString("pressure") + " hPa" +
                            "\n" + "Wind Dir: " + wind.getString("deg") + " °" +
                            "\n" + "Wind Speed: " + result + " kts");
                           // "\n" + "Wind Gust: " + resultGust + " kts");


            currentTemperatureField.setText(
                    String.format(Locale.UK,"%.2f",main.getDouble("temp")) + " ℃");

            DateFormat df = DateFormat.getDateTimeInstance();
            String updatedOn = df.format(new Date(json.getLong("dt")*1000));
            updatedField.setText("Last update: " + updatedOn + "");

            setWeatherIcon(details.getInt("id"),
                    json.getJSONObject("sys").getLong("sunrise") * 1000,
                    json.getJSONObject("sys").getLong("sunset") * 1000);

        }catch(Exception e){
            Log.e("SimpleWeather", "One or more fields not found in the JSON data");
        }
    }

    // CONVERT FROM METERS PER SECOND TO KNOTS PER HOUR
    public static float FormattedSpeed(float mps) {
        float mpsSped = abs(mps * 1.943844f);
        return abs(mpsSped);
    }

    public static BigDecimal round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd;
    }

    private void setWeatherIcon(int actualId, long sunrise, long sunset){
        int id = actualId / 100;
        String icon = "";
        if(actualId == 800){
            long currentTime = new Date().getTime();
            if(currentTime>=sunrise && currentTime<sunset) {
                icon = getActivity().getString(R.string.weather_sunny);
            } else {
                icon = getActivity().getString(R.string.weather_clear_night);
            }
        } else {
            switch(id) {
                case 2 : icon = getActivity().getString(R.string.weather_thunder);
                    break;
                case 3 : icon = getActivity().getString(R.string.weather_drizzle);
                    break;
                case 7 : icon = getActivity().getString(R.string.weather_foggy);
                    break;
                case 8 : icon = getActivity().getString(R.string.weather_cloudy);
                    break;
                case 6 : icon = getActivity().getString(R.string.weather_snowy);
                    break;
                case 5 : icon = getActivity().getString(R.string.weather_rainy);
                    break;
            }
        }
        weatherIcon.setText(icon);
    }

    public void changeCity(String chosen_city){
        updateWeatherData(chosen_city);
    }

    public void changeLatLong(String chosen_latlong){
        updateWeatherData(chosen_latlong);
    }

}