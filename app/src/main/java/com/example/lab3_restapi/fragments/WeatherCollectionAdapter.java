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

/**
 * This class holds all the weather fragments used to display the forecasts' information for the different tabs.
 *
 * @see WeatherLiveFragment
 * @see WeatherHourlyFragment
 * @see WeatherDailyFragment
 */
public class WeatherCollectionAdapter extends FragmentStateAdapter {
    private final ArrayList<Fragment> weather_fragments;
    private Timer update_timer;

    /**
     * Default constructor creating a {@link WeatherLiveFragment}, a {@link WeatherHourlyFragment} and seven {@link WeatherDailyFragment}.
     * @param fragment the <code>ViewPager2</code>'s parent fragment
     */
    public WeatherCollectionAdapter(Fragment fragment) {
        super(fragment);

        weather_fragments = new ArrayList<>();
        weather_fragments.add(new WeatherLiveFragment());
        weather_fragments.add(new WeatherHourlyFragment());

        for (int forecast_days = 7; forecast_days > 0; forecast_days--)
            weather_fragments.add(new WeatherDailyFragment());

        // Setup live weather update
        update_timer = new Timer();
        restartUpdateTimer(fragment.requireActivity());
    }

    /**
     * Inherited method to create a new fragment for the <code>ViewPager2</code>.
     * @param position the position of the fragment within the <code>ViewPager2</code>
     * @return The fragment to display.
     */
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return weather_fragments.get(position);
    }

    /**
     * Inherited method to get the number of fragments currently held.
     * @return The number of fragments.
     */
    @Override
    public int getItemCount() {
        return weather_fragments.size();
    }

    /**
     * Send the relevant weather forecast data to each child fragments using the {@link WeatherFragment#updateWeatherData(Context, ArrayList)} method.
     * @param ctx context passed to the children fragments to successfully update
     * @param data the new API data
     *
     * @see APIData#getCurrentWeatherData()
     * @see APIData#getHourlyWeatherData(int)
     * @see APIData#getDailyWeatherData(int)
     */
    public void refreshFragmentsData(Context ctx, APIData data) {
        for (int i = 0; i < getItemCount(); ++i) {
            Fragment f = weather_fragments.get(i);

            if (f instanceof WeatherLiveFragment) {
                ArrayList<APIData.WeatherData> new_data = new ArrayList<>();
                new_data.add(data.getCurrentWeatherData());
                ((WeatherLiveFragment) (f)).updateWeatherData(ctx, new_data);
            } else if (f instanceof WeatherHourlyFragment && i == 1) {
                ((WeatherHourlyFragment) (f)).updateWeatherData(ctx, data.getHourlyWeatherData(new UserPreferences(ctx).getMaxHourlyForecast()));
            } else {
                ArrayList<APIData.WeatherData> new_data = new ArrayList<>();
                new_data.add(data.getDailyWeatherData(i - 1));
                assert (f) instanceof WeatherDailyFragment;
                ((WeatherDailyFragment) (f)).updateWeatherData(ctx, new_data);
            }
        }
    }

    /**
     * Reset the update timer period using the new user preference.
     * @param main a reference to the {@link com.example.lab3_restapi.MainActivity}
     *
     * @see UserPreferences#getRefreshInterval()
     */
    public void restartUpdateTimer(Activity main) {
        update_timer.cancel();
        update_timer = new Timer();
        final long refresh_interval = new UserPreferences(main.getApplicationContext()).getRefreshInterval(); // In minutes
        update_timer.schedule(createUpdateTask(main), 60000 * refresh_interval, 60000 * refresh_interval); // Multiply milliseconds to minutes
    }

    /**
     * Create a {@link TimerTask} for updating the <code>Live</code> tab data.
     * @param main used to run the update on the UI thread
     * @return The new {@link TimerTask}.
     */
    private TimerTask createUpdateTask(Activity main) {
        return new TimerTask() {
            @Override
            public void run() {
                try {
                    final String city = new UserPreferences(main.getApplicationContext()).getCity();
                    final JSONObject data = FetchData.fetchAPIData(main.getApplicationContext(), city);
                    if (data != null) {
                        APIData api_data = new APIData(data, city);
                        ArrayList<APIData.WeatherData> new_data = new ArrayList<>();
                        new_data.add(api_data.getCurrentWeatherData());
                        main.runOnUiThread (() -> ((WeatherLiveFragment)(createFragment(0))).updateWeatherData(main.getApplicationContext(), new_data));
                    }
                } catch (Exception e) {
                    Log.e(getClass().getName(), "Error in live weather data update Timer : " + e.getMessage());
                }
            }
        };
    }
}
