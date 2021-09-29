package com.example.lab3_restapi.fragments;

import android.content.Context;

import androidx.fragment.app.Fragment;

import com.example.lab3_restapi.APIData;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

public abstract class WeatherFragment extends Fragment {
    public abstract void updateWeatherData(Context ctx, ArrayList<APIData.WeatherData> current);

    public static String timestampToDate(long timestamp) {
        DateFormat d_format = DateFormat.getTimeInstance(DateFormat.SHORT);
        d_format.setTimeZone(TimeZone.getTimeZone("GMT")); // Set timezone to GMT because timestamp already accounts for the city's hour offset
        return d_format.format(new Date(timestamp * 1000L)); // Multiply by 1000 to convert timestamp to milliseconds
    }
}
