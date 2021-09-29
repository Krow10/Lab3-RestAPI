package com.example.lab3_restapi.fragments;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.lab3_restapi.APIData;

import java.util.ArrayList;

public class WeatherCollectionAdapter extends FragmentStateAdapter {
    private final ArrayList<Fragment> weather_fragments;

    public WeatherCollectionAdapter(Fragment fragment) {
        super(fragment);

        weather_fragments = new ArrayList<>();
        weather_fragments.add(new WeatherLiveFragment());
        weather_fragments.add(new WeatherHourlyFragment());

        for (int forecast_days = 7; forecast_days > 0; forecast_days--)
            weather_fragments.add(new WeatherDailyFragment());
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return weather_fragments.get(position);
    }

    @Override
    public int getItemCount() {
        return weather_fragments.size();
    }

    public void refreshFragmentsData(Context ctx, APIData data) {
        for (int i = 0; i < getItemCount(); ++i) {
            Fragment f = weather_fragments.get(i);

            if (f instanceof WeatherLiveFragment) {
                ArrayList<APIData.WeatherData> new_data = new ArrayList<>();
                new_data.add(data.getCurrentWeatherData());
                ((WeatherLiveFragment) (f)).updateWeatherData(ctx, new_data);
            } else if (f instanceof WeatherHourlyFragment && i == 1) {
                ((WeatherHourlyFragment) (f)).updateWeatherData(ctx, data.getHourlyWeatherData());
            } else {
                ArrayList<APIData.WeatherData> new_data = new ArrayList<>();
                new_data.add(data.getDailyWeatherData(i - 1));
                assert (f) instanceof WeatherDailyFragment;
                ((WeatherDailyFragment) (f)).updateWeatherData(ctx, new_data);
            }
        }
    }
}
