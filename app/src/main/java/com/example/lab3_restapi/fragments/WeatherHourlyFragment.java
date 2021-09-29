package com.example.lab3_restapi.fragments;

import android.content.Context;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lab3_restapi.APIData;
import com.example.lab3_restapi.R;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

public class WeatherHourlyFragment extends WeatherFragment {
    private ArrayList<APIData.WeatherData> current_hourly_forecast;
    private WeatherForecastAdapter hourly_adapter;

    private class WeatherForecastAdapter extends RecyclerView.Adapter<WeatherForecastAdapter.ViewHolder> {
        private final ArrayList<APIData.WeatherData> forecast;

        public class ViewHolder extends RecyclerView.ViewHolder {
            private final TextView forecast_time;
            private final ImageView forecast_icon;
            private final TextView forecast_temp;
            private final TextView forecast_details;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                forecast_time = itemView.findViewById(R.id.w_hourly_time);
                forecast_icon = itemView.findViewById(R.id.w_hourly_icon);
                forecast_temp = itemView.findViewById(R.id.w_hourly_temp);
                forecast_details = itemView.findViewById(R.id.w_hourly_details);
            }

            public TextView getForecastTime() {
                return forecast_time;
            }

            public ImageView getForecastIcon() {
                return forecast_icon;
            }

            public TextView getForecastTemp() {
                return forecast_temp;
            }

            public TextView getForecastDetails() {
                return forecast_details;
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
            // TODO : Add constant strings to xml file
            APIData.WeatherData hour = forecast.get(position);

            final String time = WeatherFragment.timestampToDate(hour.timestamp);
            final String temp = hour.temp + " °C (feels like " + hour.feels_like + " °C)";
            final String weather_icon_url = "http://openweathermap.org/img/wn/" + hour.icon_url + "@2x.png";
            final String details = hour.description
                    + " / Humidity : " + hour.humidity
                    + " / Pressure : " + hour.pressure;

            holder.getForecastTime().setText(time);
            Picasso.get()
                    .load(weather_icon_url)
                    .placeholder(Objects.requireNonNull(new CircularProgressIndicator(requireContext()).getIndeterminateDrawable()))
                    .into(holder.getForecastIcon());
            holder.getForecastTemp().setText(temp);
            holder.getForecastDetails().setText(details);
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
