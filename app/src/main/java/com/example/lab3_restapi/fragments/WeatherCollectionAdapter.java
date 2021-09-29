package com.example.lab3_restapi.fragments;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.lab3_restapi.APIData;
import com.example.lab3_restapi.CityPreferences;
import com.example.lab3_restapi.FetchData;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class WeatherCollectionAdapter extends FragmentStateAdapter {
    private final ArrayList<Fragment> weather_fragments;

    public WeatherCollectionAdapter(Fragment fragment) {
        super(fragment);

        weather_fragments = new ArrayList<>();
        weather_fragments.add(new WeatherLiveFragment());
        weather_fragments.add(new WeatherHourlyFragment());

        for (int forecast_days = 7; forecast_days > 0; forecast_days--)
            weather_fragments.add(new WeatherDailyFragment());

        // Setup live weather update
        Timer timer = new Timer();
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                try {
                    final Activity main = fragment.getActivity();
                    final String city = new CityPreferences(Objects.requireNonNull(main)).getCity();
                    final JSONObject data = FetchData.fetchAPIData(fragment.getContext(), city);
                    if (data != null) {
                        APIData api_data = new APIData(data, city);
                        ArrayList<APIData.WeatherData> new_data = new ArrayList<>();
                        new_data.add(api_data.getCurrentWeatherData());
                        main.runOnUiThread (() -> ((WeatherLiveFragment)(createFragment(0))).updateWeatherData(fragment.getContext(), new_data));
                    }
                } catch (Exception e) {
                    Log.e(getClass().getName(), "Error in live weather data update Timer : " + e.getMessage());
                }
            }
        };

        timer.schedule(doAsynchronousTask, 60000, 60000); // Run update every minute
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
