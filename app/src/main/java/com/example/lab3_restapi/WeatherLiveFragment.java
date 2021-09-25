package com.example.lab3_restapi;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.TimeZone;

public class WeatherLiveFragment extends Fragment {
    private TextView cityField;
    private ImageView weatherIcon;
    private TextView updatedField;
    private TextView currentTemperatureField;
    private TextView detailsField;

    Handler handler;

    public WeatherLiveFragment() {
        handler = new Handler();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.weather_live, container, false);
        updatedField = (TextView) rootView.findViewById(R.id.updated_field);
        cityField = (TextView) rootView.findViewById(R.id.city_field);
        currentTemperatureField = (TextView) rootView.findViewById(R.id.current_temperature_field);
        weatherIcon = (ImageView) rootView.findViewById(R.id.weather_icon);
        detailsField = (TextView) rootView.findViewById(R.id.details_field);

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        updateWeatherData(new CityPreferences(getActivity()).getCity());
    }

    public void updateWeatherData(final String city) {
        if (MainActivity.api_data != null) {
            APIData.WeatherData current = MainActivity.api_data.getCurrentWeatherData();
            // TODO : Check for empty weather data
            final String temp = current.temp + " °C (feels like " + current.feels_like + " °C)";
            final String update_time = "Last updated at " + timestampToDate(current.timestamp);
            final String weather_icon_url = "http://openweathermap.org/img/wn/" + current.icon_url + "@2x.png";
            final String details = current.description
                    + "\nHumidity : " + current.humidity
                    + "\nPressure : " + current.pressure
                    + "\nSunrise : " + timestampToDate(current.sunrise)
                    + "\nSunset : " + timestampToDate(current.sunset);

            System.out.println("url : " + weather_icon_url);
            cityField.setText(city); // TODO : Get postal code, country, ... if possible (modify 'getCityLocation' ?)
            Picasso.get().load(weather_icon_url).placeholder(new CircularProgressIndicator(getContext()).getIndeterminateDrawable()).into(weatherIcon);
            updatedField.setText(update_time);
            currentTemperatureField.setText(temp);
            detailsField.setText(details);
        } else { // TODO : Add placeholder animations
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    updateWeatherData(city);
                }
            }, getResources().getInteger(R.integer.api_retry_delay_ms));
        }
    }

    private String timestampToDate(long timestamp) {
        DateFormat d_format = DateFormat.getTimeInstance(DateFormat.SHORT);
        d_format.setTimeZone(TimeZone.getTimeZone("GMT")); // Set timezone to GMT because timestamp already accounts for the city's hour offset
        return d_format.format(new Date(timestamp));
    }
}