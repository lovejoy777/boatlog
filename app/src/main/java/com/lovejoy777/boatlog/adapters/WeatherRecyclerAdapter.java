package com.lovejoy777.boatlog.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lovejoy777.boatlog.R;
import com.lovejoy777.boatlog.activities.WeatherMainActivity;
import com.lovejoy777.boatlog.models.Weather;
import com.lovejoy777.boatlog.models.WeatherViewHolder;
import com.lovejoy777.boatlog.utils.UnitConvertor;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class WeatherRecyclerAdapter extends RecyclerView.Adapter<WeatherViewHolder> {
    final String TAG = "fragment ";
    private List<Weather> itemList;
    private Context context;

    public WeatherRecyclerAdapter(Context context, List<Weather> itemList) {
        this.itemList = itemList;
        this.context = context;
    }

    @Override
    public WeatherViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        try {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_row, null);

        WeatherViewHolder viewHolder = new WeatherViewHolder(view);
        return viewHolder;
        }catch (Exception e) {
            Log.e(TAG, "onCreateViewHolder", e);
            throw e;
        }
    }

    @Override
    public void onBindViewHolder(WeatherViewHolder customViewHolder, int i) {
        Weather weatherItem = itemList.get(i);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        // Temperature
        float temperature = 0;
        if (weatherItem.getTemperature() != null) {
            try {
                temperature = UnitConvertor.convertTemperature(Float.parseFloat(weatherItem.getTemperature()), sp);
            } catch (Exception e) {
                temperature = 0;
            }
        }
        if (sp.getBoolean("temperatureInteger", false)) {
            temperature = Math.round(temperature);
        }

        // Rain
        double rain = 0;
        if (weatherItem.getRain() != null) {
            try {
                rain = Double.parseDouble(weatherItem.getRain());
            } catch (Exception e) {
                rain = 0;
            }
        }
        String rainString = UnitConvertor.getRainString(rain, sp);

        // Wind
        double wind = 0;
        if (weatherItem.getWind() != null) {
            try {
                wind = Double.parseDouble(weatherItem.getWind());
            } catch (Exception e) {
                wind = 0;
            }
        }
        wind = UnitConvertor.convertWind(wind, sp);

        // Pressure
        double pressure = 0;
        if (weatherItem.getPressure() != null) {
            try {
                pressure = UnitConvertor.convertPressure((float) Double.parseDouble(weatherItem.getPressure()), sp);
            } catch (Exception e) {
                pressure = 0;
            }
        }

        TimeZone tz = TimeZone.getDefault();
        String defaultDateFormat = context.getResources().getStringArray(R.array.dateFormatsValues)[0];
        String dateFormat = sp.getString("dateFormat", defaultDateFormat);
        if ("custom".equals(dateFormat)) {
            dateFormat = sp.getString("dateFormatCustom", defaultDateFormat);
        }
        String dateString = "";
        if (weatherItem.getDate() != null) {
            try {
                SimpleDateFormat resultFormat = new SimpleDateFormat(dateFormat);
                resultFormat.setTimeZone(tz);
                dateString = resultFormat.format(weatherItem.getDate());
            } catch (Exception e) {
                dateString = context.getResources().getString(R.string.error_dateFormat);
            }
        }

        if (sp.getBoolean("differentiateDaysByTint", false)) {
            Date now = new Date();
            int color;
            if (weatherItem.getDate() != null && weatherItem.getNumDaysFrom(now) > 1) {
                TypedArray ta = context.obtainStyledAttributes(new int[]{R.attr.colorTintedBackground, R.attr.colorBackground});
                if (weatherItem.getNumDaysFrom(now) % 2 == 1) {
                    color = ta.getColor(0, context.getResources().getColor(R.color.colorTintedBackground));
                } else {
                    color = ta.getColor(0, context.getResources().getColor(R.color.colorBackground));
                }
                ta.recycle();
                customViewHolder.itemView.setBackgroundColor(color);
            }
        }

        customViewHolder.itemDate.setText(dateString);
        if (sp.getBoolean("displayDecimalZeroes", false)) {
            customViewHolder.itemTemperature.setText(new DecimalFormat("#.0").format(temperature) + " " + sp.getString("unit", "°C"));
        } else {
            customViewHolder.itemTemperature.setText(new DecimalFormat("#.#").format(temperature) + " " + sp.getString("unit", "°C"));
        }
        if (weatherItem.getDescription() != null && !weatherItem.getDescription().isEmpty()) {
            String desc = weatherItem.getDescription();
            customViewHolder.itemDescription.setText(desc.substring(0, 1).toUpperCase() + desc.substring(1) + rainString);
        } else {
            customViewHolder.itemDescription.setText(rainString);
        }
        Typeface weatherFont = Typeface.createFromAsset(context.getAssets(), "fonts/weather.ttf");
        customViewHolder.itemIcon.setTypeface(weatherFont);
        if (weatherItem.getIcon() != null) {
            customViewHolder.itemIcon.setText(weatherItem.getIcon());
        }
        if (sp.getString("speedUnit", "m/s").equals("bft")) {
            customViewHolder.itemyWind.setText(context.getString(R.string.wind) + ": " +
                    UnitConvertor.getBeaufortName((int) wind) + " " + WeatherMainActivity.getWindDirectionString(sp, context, weatherItem));
        } else {
            customViewHolder.itemyWind.setText(context.getString(R.string.wind) + ": " + new DecimalFormat("#.0").format(wind) + " " +
                    WeatherMainActivity.localize(sp, context, "speedUnit", "m/s")
                    + " " + WeatherMainActivity.getWindDirectionString(sp, context, weatherItem));
        }
        customViewHolder.itemPressure.setText(context.getString(R.string.pressure) + ": " + new DecimalFormat("#.0").format(pressure) + " " +
                WeatherMainActivity.localize(sp, context, "pressureUnit", "hPa"));
        if (weatherItem.getHumidity() != null) {
            customViewHolder.itemHumidity.setText(context.getString(R.string.humidity) + ": " + weatherItem.getHumidity() + " %");
        }
    }

    @Override
    public int getItemCount() {
        return (null != itemList ? itemList.size() : 0);
    }
}
