package com.example.lab3_restapi.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.List;

public class WeatherForecastFragment extends WeatherFragment {
    private RecyclerView forecast_view;
    private WeatherForecastAdapter forecast_adapter;

    private class WeatherForecastAdapter extends RecyclerView.Adapter<WeatherForecastAdapter.ViewHolder> {
        private List<APIData.WeatherData> forecast;

        public class ViewHolder extends RecyclerView.ViewHolder {
            private final ImageView forecast_icon;
            private final TextView forecast_temp;
            private final TextView forecast_details;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                forecast_icon = itemView.findViewById(R.id.forecast_icon);
                forecast_temp = itemView.findViewById(R.id.forecast_temp);
                forecast_details = itemView.findViewById(R.id.forecast_details);
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

        public WeatherForecastAdapter(List<APIData.WeatherData> data) {
            forecast = new ArrayList<>();
            updateForecast(data);
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.weather_forecast_item, parent, false);

            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            APIData.WeatherData day = forecast.get(position);

            final String temp = day.temp + " °C (feels like " + day.feels_like + " °C)";
            final String weather_icon_url = "http://openweathermap.org/img/wn/" + day.icon_url + "@2x.png";
            final String details = day.description
                    + "\nHumidity : " + day.humidity
                    + "\nPressure : " + day.pressure;

            Picasso.get().load(weather_icon_url).placeholder(new CircularProgressIndicator(getContext()).getIndeterminateDrawable()).into(holder.getForecastIcon());
            holder.getForecastTemp().setText(temp);
            holder.getForecastDetails().setText(details);
        }

        @Override
        public int getItemCount() {
            return forecast.size();
        }

        public void updateForecast(List<APIData.WeatherData> f) {
            if (f != null && f.size() > 0) {
                forecast = f;
                Log.d(getTag(), "Updating forecast : " + forecast); // TODO : Update all items
                notifyDataSetChanged();
            }
        }
    }

    public WeatherForecastFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.weather_forecast, container, false);
        forecast_adapter = new WeatherForecastAdapter(new ArrayList<>());
        forecast_view = rootView.findViewById(R.id.forecast_recyclerview);
        forecast_view.setLayoutManager(new LinearLayoutManager(getContext()));
        forecast_view.setAdapter(forecast_adapter);

        return rootView;
    }

    @Override
    public void updateWeatherData(Context ctx, List<APIData.WeatherData> new_forecast) {
        if (getView() != null) {
            forecast_adapter.updateForecast(new_forecast);
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    updateWeatherData(ctx, new_forecast);
                }
            }, ctx.getResources().getInteger(R.integer.api_retry_delay_ms)*10); // TODO : Change this
        }
    }
}
