package com.example.lab3_restapi.fragments;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.lab3_restapi.APIData;

import java.util.ArrayList;
import java.util.Arrays;

public class WeatherCollectionAdapter extends FragmentStateAdapter {
    private ArrayList<Fragment> weather_fragments;

    public WeatherCollectionAdapter(Fragment fragment) {
        super(fragment);

        weather_fragments = new ArrayList<>();
        weather_fragments.add(new WeatherLiveFragment());

        for (int forecast_days = 7; forecast_days > 0; forecast_days--)
            weather_fragments.add(new WeatherForecastFragment());
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment = weather_fragments.get(position);
        return fragment;
    }

    @Override
    public int getItemCount() {
        return weather_fragments.size();
    }

    public void addFragment(Fragment fragment) { // TODO : Use this method to add week forecast tabs
        weather_fragments.add(fragment);
    }

    public void refreshFragmentsData(Context ctx, APIData data) {
        for (int i = 0; i < getItemCount(); ++i) {
            Fragment f = weather_fragments.get(i);
            if (f instanceof WeatherLiveFragment)
                ((WeatherLiveFragment)(f)).updateWeatherData(ctx, Arrays.asList(data.getCurrentWeatherData()));
            else if (f instanceof WeatherForecastFragment && i == 1)
                ((WeatherForecastFragment)(f)).updateWeatherData(ctx, data.getHourlyWeatherData());
            else
                ((WeatherForecastFragment)(f)).updateWeatherData(ctx, data.getDailyWeatherData());
        }
    }
}
