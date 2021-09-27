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

public class WeatherHourlyFragment extends WeatherFragment {
    private RecyclerView hourly_view;
    private WeatherForecastAdapter hourly_adapter;
    private List<APIData.WeatherData> current;

    private class WeatherForecastAdapter extends RecyclerView.Adapter<WeatherForecastAdapter.ViewHolder> {
        private List<APIData.WeatherData> forecast;

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

        public WeatherForecastAdapter(List<APIData.WeatherData> data) {
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
//                for (int k = 0; k < getItemCount(); ++k)
//                    notifyItemInserted(k);
            }
        }
    }

    public WeatherHourlyFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.weather_hourly, container, false);
        hourly_adapter = new WeatherForecastAdapter(new ArrayList<>());
        hourly_view = rootView.findViewById(R.id.w_hourly_recyclerview);
        hourly_view.setLayoutManager(new LinearLayoutManager(getContext()));
        hourly_view.setAdapter(hourly_adapter);

        if (current != null)
            updateWeatherData(getContext(), current);

        return rootView;
    }

    @Override
    public void updateWeatherData(Context ctx, List<APIData.WeatherData> new_forecast) {
        if (getView() != null) {
            current = new_forecast;
            hourly_adapter.updateForecast(new_forecast);
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
