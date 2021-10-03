package com.example.lab3_restapi.fragments;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.lab3_restapi.APIData;
import com.example.lab3_restapi.FetchData;
import com.example.lab3_restapi.UserPreferences;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class WeatherCollectionAdapter extends FragmentStateAdapter {
    private final ArrayList<Fragment> weather_fragments;
    private Timer update_timer;

    public WeatherCollectionAdapter(Fragment fragment) {
        super(fragment);

        weather_fragments = new ArrayList<>();
        weather_fragments.add(new WeatherLiveFragment());
        weather_fragments.add(new WeatherHourlyFragment());

        for (int forecast_days = 7; forecast_days > 0; forecast_days--)
            weather_fragments.add(new WeatherDailyFragment());

        // Setup live weather update
        update_timer = new Timer();
        restartUpdateTimer(fragment.getActivity(), fragment.getContext());
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

    public void restartUpdateTimer(Activity main, Context context) {
        update_timer.cancel();
        update_timer = new Timer();
        final long refresh_interval = new UserPreferences(context).getRefreshInterval(); // In minutes
        Log.d(getClass().getName(), "Restart timer with " + refresh_interval + " minutes interval !");
        update_timer.schedule(createUpdateTask(main, context), 60000 * refresh_interval, 60000 * refresh_interval); // Multiply milliseconds to minutes
    }

    private TimerTask createUpdateTask(Activity main, Context context) {
        return new TimerTask() {
            @Override
            public void run() {
                try {
                    final String city = new UserPreferences(context).getCity();
                    final JSONObject data = FetchData.fetchAPIData(context, city);
                    if (data != null) {
                        APIData api_data = new APIData(data, city);
                        ArrayList<APIData.WeatherData> new_data = new ArrayList<>();
                        new_data.add(api_data.getCurrentWeatherData());
                        main.runOnUiThread (() -> ((WeatherLiveFragment)(createFragment(0))).updateWeatherData(context, new_data));
                    }
                } catch (Exception e) {
                    Log.e(getClass().getName(), "Error in live weather data update Timer : " + e.getMessage());
                }
            }
        };
    }
}
