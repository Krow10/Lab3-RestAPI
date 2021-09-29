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

import androidx.annotation.Nullable;

import com.example.lab3_restapi.APIData;
import com.example.lab3_restapi.R;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

public class WeatherDailyFragment extends WeatherFragment {
    private APIData.WeatherData current;
    private ImageView weatherIcon;

    public WeatherDailyFragment() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.weather_daily, container, false);
        rootView.setAlpha(0f);
        weatherIcon = (ImageView) rootView.findViewById(R.id.w_daily_icon);

        if (current != null)
            updateFields();

        rootView.animate().alpha(1f).setDuration(getResources().getInteger(R.integer.tab_content_fade_in_anim_speed)).setInterpolator(new DecelerateInterpolator()).start();
        return rootView;
    }

    @Override
    public void updateWeatherData(Context ctx, ArrayList<APIData.WeatherData> current_) {
        if (getView() != null) {
            current = current_.get(0);
            Log.d(this.getTag(), "Updated hourly weather data !");
            updateFields();
        } else { // TODO : Add placeholder animations
            new Handler().postDelayed(() -> updateWeatherData(ctx, current_), ctx.getResources().getInteger(R.integer.api_retry_delay_ms));
        }
    }

    private void updateFields() {
        final String weather_icon_url = "http://openweathermap.org/img/wn/" + current.icon_url + "@2x.png";

        Picasso.get()
                .load(weather_icon_url)
                .placeholder(Objects.requireNonNull(new CircularProgressIndicator(requireContext()).getIndeterminateDrawable()))
                .into(weatherIcon);
    }
}
