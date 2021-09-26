package com.example.lab3_restapi.fragments;

import android.content.Context;

import androidx.fragment.app.Fragment;

import com.example.lab3_restapi.APIData;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public abstract class WeatherFragment extends Fragment {
    public abstract void updateWeatherData(Context ctx, List<APIData.WeatherData> current);

    public String timestampToDate(long timestamp) {
        DateFormat d_format = DateFormat.getTimeInstance(DateFormat.SHORT);
        d_format.setTimeZone(TimeZone.getTimeZone("GMT")); // Set timezone to GMT because timestamp already accounts for the city's hour offset
        return d_format.format(new Date(timestamp));
    }
}
