package com.example.lab3_restapi.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.example.lab3_restapi.APIData;
import com.example.lab3_restapi.R;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.squareup.picasso.Picasso;

import java.util.List;

public class WeatherDailyFragment extends WeatherFragment {
    private APIData.WeatherData current;
    private ImageView weatherIcon;

    public WeatherDailyFragment() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.weather_daily, container, false);
        weatherIcon = (ImageView) rootView.findViewById(R.id.w_daily_icon);

        if (current != null)
            updateFields();

        return rootView;
    }

    @Override
    public void updateWeatherData(Context ctx, List<APIData.WeatherData> current_) {
        if (getView() != null) {
            current = current_.get(0);
            Log.d(this.getTag(), "Updated hourly weather data !");
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
        final String temp = current.temp + " °C (feels like " + current.feels_like + " °C)";
        final String update_time = "Last updated at " + timestampToDate(current.timestamp);
        final String weather_icon_url = "http://openweathermap.org/img/wn/" + current.icon_url + "@2x.png";
        final String details = current.description
                + "\nHumidity : " + current.humidity
                + "\nPressure : " + current.pressure
                + "\nSunrise : " + timestampToDate(current.sunrise)
                + "\nSunset : " + timestampToDate(current.sunset);

        Picasso.get().load(weather_icon_url).placeholder(new CircularProgressIndicator(getContext()).getIndeterminateDrawable()).into(weatherIcon);
    }
}
