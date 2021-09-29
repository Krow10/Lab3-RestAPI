package com.example.lab3_restapi.fragments;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.RotateDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
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

import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Function;

public class WeatherHourlyFragment extends WeatherFragment {
    private ArrayList<APIData.WeatherData> current_hourly_forecast;
    private WeatherForecastAdapter hourly_adapter;

    private class WeatherForecastAdapter extends RecyclerView.Adapter<WeatherForecastAdapter.ViewHolder> {
        private final ArrayList<APIData.WeatherData> forecast;

        public class ViewHolder extends RecyclerView.ViewHolder {
            private final TextView time;
            private final ImageView weather_icon;
            private final TextView temp;
            private final ImageView wind_icon;
            private final TextView wind_dir;
            private final TextView wind_speed;

            public ViewHolder(@NonNull View itemView) {
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

        public WeatherForecastAdapter(ArrayList<APIData.WeatherData> data) {
            forecast = new ArrayList<>();
            updateForecast(data);
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.weather_hourly_item, parent, false);

            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            APIData.WeatherData hour = forecast.get(position);
            Function<String, String> formatTime = s -> s.substring(0, s.indexOf(':')) + (s.contains("p") ? " PM" : " AM");

            holder.getTime().setText(formatTime.apply(WeatherFragment.timestampToDate(hour.timestamp)));

            final int icon_id = getIconID(getContext(), hour.icon_url);
            holder.getWeatherIcon().setImageResource(icon_id != 0 ? icon_id : R.drawable.ic_baseline_cached_24);

            holder.getTemp().setText(formatDecimal(hour.temp) + "°C"); // TODO : Change to user preference

            RotateDrawable wind_dir_icon = (RotateDrawable) Objects.requireNonNull(ResourcesCompat.getDrawable(
                    getResources(), R.drawable.ic_wind_dir_animated, null)).mutate(); // Call 'mutate()' to create a copy
            Objects.requireNonNull(wind_dir_icon).setColorFilter(ResourcesCompat.getColor(
                    getResources(), R.color.ic_details_fill, null), PorterDuff.Mode.SRC_IN);
            wind_dir_icon.setToDegrees((float) hour.wind_deg);
            // For some reasons, using 'setLevel' on the icon doesn't work so we use an object animator with duration of zero to adjust wind indicator
            ObjectAnimator wind_dir_anim = ObjectAnimator.ofInt(wind_dir_icon, "level", 0, 10000);
            wind_dir_anim.setDuration(0);
            wind_dir_anim.start();
            holder.getWindIcon().setImageDrawable(wind_dir_icon);
            holder.getWindDir().setText(getDirectionFromAngle(hour.wind_deg));
            holder.getWindSpeed().setText(formatDecimal(hour.wind_speed * 3.6f) + " km/h"); // TODO : Change to user preference
        }

        @Override
        public int getItemCount() {
            return forecast.size();
        }

        public void updateForecast(ArrayList<APIData.WeatherData> f) {
            if (f != null && f.size() > 0) {
                for (int i = 0; i < f.size(); ++i) {
                    if (i < forecast.size()) {
                        forecast.set(i, f.get(i));
                        notifyItemChanged(i);
                    } else {
                        forecast.add(f.get(i));
                        notifyItemInserted(i);
                    }
                }

                Log.d(getTag(), "Updating forecast : " + forecast);
            }
        }
    }

    public WeatherHourlyFragment() {}

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

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("weather_data", current_hourly_forecast);
    }

    @Override
    public void updateWeatherData(Context ctx, ArrayList<APIData.WeatherData> new_forecast) {
        if (getView() != null) {
            current_hourly_forecast = new_forecast;
            hourly_adapter.updateForecast(new_forecast);
        } else {
            new Handler().postDelayed(() -> updateWeatherData(ctx, new_forecast), ctx.getResources().getInteger(R.integer.api_retry_delay_ms)* 10L); // TODO : Change this
        }
    }
}
