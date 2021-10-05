package com.example.lab3_restapi.fragments;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.RotateDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lab3_restapi.APIData;
import com.example.lab3_restapi.R;
import com.example.lab3_restapi.UserPreferences;

import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Function;

/**
 * This class represent the hourly forecast view used in the <i>Today</i> tab.
 */
public class WeatherHourlyFragment extends WeatherFragment {
    private ArrayList<APIData.WeatherData> current_hourly_forecast;
    private WeatherForecastAdapter hourly_adapter;

    /**
     * This class represents the <code>Adapter</code> holding the array of {@link com.example.lab3_restapi.APIData.WeatherData} for the <code>RecyclerView</code>.
     */
    private class WeatherForecastAdapter extends RecyclerView.Adapter<WeatherForecastAdapter.ForecastItemViewHolder> {
        private final ArrayList<APIData.WeatherData> forecast;

        /**
         * This class represents a single forecast item in the list.
         */
        public class ForecastItemViewHolder extends RecyclerView.ViewHolder {
            private final TextView time;
            private final ImageView weather_icon;
            private final TextView temp;
            private final ImageView wind_icon;
            private final TextView wind_dir;
            private final TextView wind_speed;

            public ForecastItemViewHolder(@NonNull View itemView) {
                super(itemView);

                time = itemView.findViewById(R.id.w_hourly_item_time);
                weather_icon = itemView.findViewById(R.id.w_hourly_item_weather_icon);
                temp = itemView.findViewById(R.id.w_hourly_item_temp);
                wind_icon = itemView.findViewById(R.id.w_hourly_item_wind_icon);
                wind_dir = itemView.findViewById(R.id.w_hourly_item_wind_dir);
                wind_speed = itemView.findViewById(R.id.w_hourly_item_wind_speed);
            }

            public TextView getTime() {
                return time;
            }

            public ImageView getWeatherIcon() {
                return weather_icon;
            }

            public TextView getTemp() {
                return temp;
            }

            public ImageView getWindIcon() {
                return wind_icon;
            }

            public TextView getWindDir() {
                return wind_dir;
            }

            public TextView getWindSpeed() {
                return wind_speed;
            }
        }

        /**
         * Default constructor.
         * @param data the list of hourly forecast
         */
        public WeatherForecastAdapter(ArrayList<APIData.WeatherData> data) {
            forecast = new ArrayList<>();
            updateForecast(data);
        }

        /**
         * Inherited method to inflate the layout for a single forecast item.
         * @param parent the parent view group
         * @param viewType unused
         * @return The new forecast item.
         */
        @NonNull
        @Override
        public ForecastItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.weather_hourly_item, parent, false);

            return new ForecastItemViewHolder(view);
        }

        /**
         * Inherited method to fill the data for forecast item when it's attached to the <code>RecyclerView</code>.
         * @param holder the forecast item to fill
         * @param position the forecast item's position in the list
         */
        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull ForecastItemViewHolder holder, int position) {
            APIData.WeatherData hour = forecast.get(position);
            Function<String, String> formatTime = s -> s.substring(0, s.indexOf(':')) + (s.contains("p") ? " PM" : " AM");

            holder.getTime().setText(formatTime.apply(WeatherFragment.timestampToTime(hour.timestamp)));

            final int icon_id = getIconID(getContext(), hour.icon_url);
            holder.getWeatherIcon().setImageResource(icon_id != 0 ? icon_id : R.drawable.ic_baseline_cached_24);

            holder.getTemp().setText(formatDecimal(hour.temp_day) + new UserPreferences(getContext()).getTempUnit());

            RotateDrawable wind_dir_icon = (RotateDrawable) Objects.requireNonNull(ResourcesCompat.getDrawable(
                    getResources(), R.drawable.ic_wind_dir_animated, null)).mutate(); // Call 'mutate()' to create a copy
            Objects.requireNonNull(wind_dir_icon).setColorFilter(ResourcesCompat.getColor(
                    getResources(), R.color.ic_details_fill, null), PorterDuff.Mode.SRC_IN); // Fill with details icon color
            wind_dir_icon.setToDegrees((float) hour.wind_deg);
            // For some reasons, using 'setLevel' on the icon doesn't work so we use an object animator with duration of zero to adjust wind indicator
            ObjectAnimator wind_dir_anim = ObjectAnimator.ofInt(wind_dir_icon, "level", 0, 10000);
            wind_dir_anim.setDuration(0);
            wind_dir_anim.start();

            holder.getWindIcon().setImageDrawable(wind_dir_icon);
            holder.getWindDir().setText(getDirectionFromAngle(hour.wind_deg));
            holder.getWindSpeed().setText(formatDecimal(hour.wind_speed * (new UserPreferences(getContext()).getSpeedUnit().equals("mph") ? 1 : 3.6f)) + " "
                    + new UserPreferences(getContext()).getSpeedUnit());
        }

        /**
         * Inherited method to get the number of forecast items currently held.
         * @return The number of forecast items.
         */
        @Override
        public int getItemCount() {
            return forecast.size();
        }

        /**
         * Update the current forecast data and modify the list of items (add or remove) if the amount of forecasts changed (i.e. from the hourly forecast limit setting).
         * @param new_forecast the new forecast data
         *
         * @see UserPreferences#getMaxHourlyForecast()
         */
        public void updateForecast(ArrayList<APIData.WeatherData> new_forecast) {
            if (new_forecast != null && new_forecast.size() > 0) {
                for (int i = 0; i < new_forecast.size(); ++i) {
                    if (i < forecast.size()) { // Update items if possible
                        forecast.set(i, new_forecast.get(i));
                        notifyItemChanged(i);
                    } else { // Add new items if forecast hour limit changed
                        forecast.add(new_forecast.get(i));
                        notifyItemInserted(i);
                    }
                }

                if (new_forecast.size() < forecast.size()) { // Remove items if forecast hour limit changed
                    final int items_to_remove = forecast.size() - new_forecast.size(); // Needed or else the loop will recalculate forecast size at every step
                    for (int i = 0; i < items_to_remove; ++i) {
                        forecast.remove(forecast.size() - 1);
                        notifyItemRemoved(forecast.size()); // No need for -1 since forecast got resized the line before
                    }
                }
            }
        }
    }

    /**
     * Default empty constructor.
     */
    public WeatherHourlyFragment() {}

    /**
     * Inherited listener used to inflate the layout content. Restore the {@link WeatherForecastAdapter} data from the saved instance if available.
     * @param inflater the layout inflater
     * @param container the container for the new layout
     * @param savedInstanceState the {@link WeatherForecastAdapter} data if available
     * @return The view created from the layout definition.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        RecyclerView hourly_view;
        View rootView = inflater.inflate(R.layout.weather_hourly, container, false);
        rootView.setAlpha(0f);

        // Makes the loading of the hourly forecast tab faster by restoring previously saved data
        if (savedInstanceState != null) {
            //noinspection unchecked
            hourly_adapter = new WeatherForecastAdapter((ArrayList<APIData.WeatherData>) savedInstanceState.get("weather_data"));
        } else {
            hourly_adapter = new WeatherForecastAdapter(new ArrayList<>());
            if (current_hourly_forecast != null)
                updateWeatherData(getContext(), current_hourly_forecast);
        }

        hourly_view = rootView.findViewById(R.id.w_hourly_recyclerview);
        hourly_view.setLayoutManager(new LinearLayoutManager(getContext()));
        hourly_view.setAdapter(hourly_adapter);

        rootView.animate().alpha(1f).setDuration(getResources().getInteger(R.integer.tab_content_fade_in_anim_speed)).setInterpolator(new DecelerateInterpolator()).start();
        return rootView;
    }

    /**
     * Saves the {@link WeatherForecastAdapter} data to a <code>Bundle</code> to make the fragment load faster when it's recreated.
     * @param outState the bundle to save the data to
     */
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("weather_data", current_hourly_forecast);
    }

    /**
     * Inherited method to update the view's fields. It is called recursively until the view is ready.
     * @param ctx used to get the delay before retrying the update
     * @param new_forecast the new forecast data
     */
    @Override
    public void updateWeatherData(Context ctx, ArrayList<APIData.WeatherData> new_forecast) {
        if (getView() != null) {
            current_hourly_forecast = new_forecast;
            hourly_adapter.updateForecast(new_forecast);
        } else {
            update_weather_data_handler.removeCallbacksAndMessages(null);
            update_weather_data_handler.postDelayed(() -> updateWeatherData(ctx, new_forecast), ctx.getResources().getInteger(R.integer.api_retry_delay_ms));
        }
    }
}
