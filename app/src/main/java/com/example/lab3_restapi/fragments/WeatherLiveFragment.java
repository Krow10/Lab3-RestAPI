package com.example.lab3_restapi.fragments;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.RotateDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import com.example.lab3_restapi.APIData;
import com.example.lab3_restapi.R;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class WeatherLiveFragment extends WeatherFragment {
    private APIData.WeatherData current;
    private float old_wind_deg;

    private TextView city;
    private TextView local_time;

    private ImageView weather_icon;
    private TextView weather_desc;

    private TextView current_temperature;
    private TextView feels_temperature;
    private TextView wind_dir;
    private TextView wind_speed;
    private TextView humidity;

    private TextView sunrise;
    private TextView sunset;

    private TextView updated;

    public WeatherLiveFragment() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        old_wind_deg = 0f;
        View rootView = inflater.inflate(R.layout.weather_live, container, false);
        rootView.setAlpha(0f);

        city = rootView.findViewById(R.id.w_live_city);
        local_time = rootView.findViewById(R.id.w_live_local_time);

        weather_icon = rootView.findViewById(R.id.w_live_weather_icon);
        weather_desc = rootView.findViewById(R.id.w_live_weather_desc);

        current_temperature = rootView.findViewById(R.id.w_live_current_temperature);
        feels_temperature = rootView.findViewById(R.id.w_live_feels_temperature);
        wind_dir = rootView.findViewById(R.id.w_live_wind_dir);
        wind_speed = rootView.findViewById(R.id.w_live_wind_speed);
        humidity = rootView.findViewById(R.id.w_live_humidity);

        sunrise = rootView.findViewById(R.id.w_live_sunrise);
        sunset = rootView.findViewById(R.id.w_live_sunset);

        updated = rootView.findViewById(R.id.w_live_updated);

        if (current != null)
            updateFields(rootView); // Sending rootView as parameter to prevent abrupt loading on app startup

        return rootView;
    }

    @Override
    public void updateWeatherData(Context ctx, ArrayList<APIData.WeatherData> current_) {
        View rootView = getView();
        if (rootView != null) {
            current = current_.get(0); // Only one weather forecast for live data
            Log.d(this.getTag(), "Updated live weather data !");
            updateFields(rootView);
        } else { // TODO : Add placeholder animations
            new Handler().postDelayed(() -> updateWeatherData(ctx, current_), ctx.getResources().getInteger(R.integer.api_retry_delay_ms));
        }
    }

    @SuppressLint("SetTextI18n")
    private void updateFields(View rootView) {
        city.setText(current.city);
        local_time.setText("Local time : " + timestampToDate(current.timestamp));

        final int icon_id = getIconID(getContext(), current.icon_url);
        weather_icon.setImageResource(icon_id != 0 ? icon_id : R.drawable.ic_baseline_cached_24);
        weather_desc.setText(current.description);

        final String temp = new DecimalFormat("0.#").format(current.temp_day);
        current_temperature.setText(temp);

        final String feels_temp = getResources().getString(R.string.feels_like) + " " + formatDecimal(current.feels_like);
        SpannableString formatted_feels_temp = new SpannableString(feels_temp);
        formatted_feels_temp.setSpan(new StyleSpan(Typeface.BOLD), 0, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        feels_temperature.setText(formatted_feels_temp);

        RotateDrawable wind_dir_icon = (RotateDrawable) Objects.requireNonNull(ResourcesCompat.getDrawable(
                getResources(), R.drawable.ic_wind_dir_animated, null)).mutate(); // Call 'mutate()' to create a copy
        Objects.requireNonNull(wind_dir_icon).setColorFilter(ResourcesCompat.getColor(
                getResources(), R.color.ic_details_fill, null), PorterDuff.Mode.SRC_IN);
        wind_dir_icon.setFromDegrees(old_wind_deg);
        wind_dir_icon.setToDegrees((float) current.wind_deg);
        old_wind_deg = (float) current.wind_deg;
        // Use ObjectAnimator to run the rotation animation by animating the 'level' property
        ObjectAnimator wind_dir_anim = ObjectAnimator.ofInt(wind_dir_icon, "level", 0, 10000);
        wind_dir_anim.setDuration(getResources().getInteger(R.integer.wind_dir_direction_anim_speed));
        wind_dir_anim.setInterpolator(new AnticipateOvershootInterpolator());
        wind_dir_anim.start();
        // Get cardinal direction from angle
        final String wind_direction = getDirectionFromAngle(current.wind_deg);
        wind_dir.setText(wind_direction);
        wind_dir.setCompoundDrawablesRelativeWithIntrinsicBounds(wind_dir_icon, null, null, null);
        wind_dir.setCompoundDrawablePadding(15);

        wind_speed.setText(formatDecimal(current.wind_speed * 3.6f) + " km/h"); // TODO : Change to user prefs
        loadIconText(wind_speed, R.drawable.ic_wind_speed, R.color.ic_details_fill, "start");
        humidity.setText(formatDecimal(current.humidity) + " %");
        loadIconText(humidity, R.drawable.ic_humidity, R.color.ic_details_fill, "start");

        sunrise.setText(timestampToDate(current.sunrise));
        loadIconText(sunrise, R.drawable.ic_sunrise, R.color.ic_sun_fill, "start");
        sunset.setText(timestampToDate(current.sunset));
        loadIconText(sunset, R.drawable.ic_sunset, R.color.ic_sun_fill, "start");

        final String device_local_time = DateFormat.getTimeInstance(DateFormat.SHORT).format(new Date().getTime());
        final String update_time = getResources().getString(R.string.last_updated) + " " + device_local_time;
        updated.setText(update_time);

        rootView.animate().alpha(1f).setDuration(getResources().getInteger(R.integer.tab_content_fade_in_anim_speed)).setInterpolator(new DecelerateInterpolator()).start();
    }
}