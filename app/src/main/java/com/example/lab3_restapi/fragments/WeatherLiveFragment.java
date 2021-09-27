package com.example.lab3_restapi.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.lab3_restapi.APIData;
import com.example.lab3_restapi.R;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

public class WeatherLiveFragment extends WeatherFragment {
    private APIData.WeatherData current;
    private TextView city;
    private TextView local_time;
    private ImageView weather_icon;
    private TextView weather_desc;
    private TextView current_temperature;
    private TextView feels_temperature;
    private TextView wind_dir;
    private TextView wind_speed;
    private TextView humidity;
    private TextView sunrise;
    private TextView sunset;
    private TextView updated;

    public WeatherLiveFragment() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.weather_live, container, false);

        city = (TextView) rootView.findViewById(R.id.w_live_city);
        local_time = (TextView) rootView.findViewById(R.id.w_live_local_time);
        weather_icon = (ImageView) rootView.findViewById(R.id.w_live_weather_icon);
        weather_desc = (TextView) rootView.findViewById(R.id.w_live_weather_desc);
        current_temperature = (TextView) rootView.findViewById(R.id.w_live_current_temperature);
        feels_temperature = (TextView) rootView.findViewById(R.id.w_live_feels_temperature);
        wind_dir = (TextView) rootView.findViewById(R.id.w_live_wind_dir);
        wind_speed = (TextView) rootView.findViewById(R.id.w_live_wind_speed);
        humidity = (TextView) rootView.findViewById(R.id.w_live_humidity);
        sunrise = (TextView) rootView.findViewById(R.id.w_live_sunrise);
        sunset = (TextView) rootView.findViewById(R.id.w_live_sunset);
        updated = (TextView) rootView.findViewById(R.id.w_live_updated);

        if (current != null)
            updateFields();

        return rootView;
    }

    @Override
    public void updateWeatherData(Context ctx, List<APIData.WeatherData> current_) { // TODO : Call this every minute for live data refresh
        if (getView() != null) {
            current = current_.get(0);
            Log.d(this.getTag(), "Updated live weather data !");
            updateFields();
        } else { // TODO : Add placeholder animations
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    updateWeatherData(ctx, current_);
                }
            }, ctx.getResources().getInteger(R.integer.api_retry_delay_ms));
        }
    }

    private void updateFields() {
        // TODO : Check for empty weather data
        // TODO : Add constant strings to xml file

        final int icon_id = getIconID(getContext(), current.icon_url);
        final String temp = new DecimalFormat("0.#").format(current.temp); // TODO : Make °C / °F user preferences
        final String feels_temp = "°C\nfeels like " + formatDecimal(current.feels_like) + "°C";
        final String wind_direction = getDirectionFromAngle(current.wind_deg);
        // TODO : Load wind direction icon (arrow vector and apply rotation)
        final String device_local_time = DateFormat.getTimeInstance(DateFormat.SHORT).format(new Date().getTime());
        final String update_time = "Last updated at " + device_local_time;

        city.setText(current.city);
        local_time.setText("Local time : " + timestampToDate(current.timestamp));
        weather_icon.setImageResource(icon_id != 0 ? icon_id : R.drawable.ic_launcher_foreground);
        weather_desc.setText(current.description);
        current_temperature.setText(temp);
        feels_temperature.setText(feels_temp);
        wind_dir.setText(wind_direction);
        wind_speed.setText(formatDecimal(current.wind_speed * 3.6f) + " km/h");
        humidity.setText(formatDecimal(current.humidity) + " %");
        sunrise.setText(timestampToDate(current.sunrise));
        sunset.setText(timestampToDate(current.sunset));
        updated.setText(update_time);
    }

    // From @Reverend Gonzo (https://stackoverflow.com/a/2131294)
    private String getDirectionFromAngle(final double angle) {
        String directions[] = {"N", "NE", "E", "SE", "S", "SW", "W", "NW"};
        return directions[(int)Math.round(((angle % 360) / 45)) % 8];
    }

    private String formatDecimal(final double d) {
        return new DecimalFormat("0.#").format(d);
    }

    private int getIconID(Context context, String iconRef) {
        if (iconRef.contains("03") || iconRef.contains("04"))
            iconRef = iconRef.substring(0, 2) + "dn";
        return context.getResources().getIdentifier("ic_" + iconRef, "drawable", context.getPackageName());
    }
}