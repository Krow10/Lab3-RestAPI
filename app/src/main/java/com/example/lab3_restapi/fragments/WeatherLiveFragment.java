package com.example.lab3_restapi.fragments;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.RotateDrawable;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
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
import com.example.lab3_restapi.UserPreferences;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

/**
 * This class represent the live forecast view used in the <i>Live</i> tab.
 */
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

    /**
     * Default empty constructor.
     */
    public WeatherLiveFragment() {}

    /**
     * Inherited listener used to inflate the layout content.
     * @param inflater the layout inflater
     * @param container the container for the new layout
     * @param savedInstanceState unused
     * @return The view created from the layout definition.
     */
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

    /**
     * Inherited method to update the view's fields. It is called recursively until the view is ready.
     * @param ctx used to get the delay before retrying the update
     * @param new_forecast the new forecast data
     */
    @Override
    public void updateWeatherData(Context ctx, ArrayList<APIData.WeatherData> new_forecast) {
        View rootView = getView();
        if (rootView != null) {
            current = new_forecast.get(0); // Only one weather forecast for live data
            updateFields(rootView);
        } else {
            update_weather_data_handler.removeCallbacksAndMessages(null);
            update_weather_data_handler.postDelayed(() -> updateWeatherData(ctx, new_forecast), ctx.getResources().getInteger(R.integer.api_retry_delay_ms));
        }
    }

    /**
     * Update all the displayed values with the current forecast values.
     */
    @SuppressLint("SetTextI18n")
    private void updateFields(View rootView) {
        city.setText(current.city);
        local_time.setText("Local time : " + timestampToTime(current.timestamp));

        final int icon_id = getIconID(getContext(), current.icon_url);
        weather_icon.setImageResource(icon_id != 0 ? icon_id : R.drawable.ic_baseline_cached_24);
        weather_desc.setText(current.description);

        final String temp = new DecimalFormat("0.#").format(current.temp_day);
        current_temperature.setText(temp);

        final String feels_temp = new UserPreferences(getContext()).getTempUnit() + "\n"
                + getResources().getString(R.string.feels_like) + " "
                + formatDecimal(current.feels_like);
        SpannableString formatted_feels_temp = new SpannableString(feels_temp);
        formatted_feels_temp.setSpan(new StyleSpan(Typeface.BOLD), 0, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        feels_temperature.setText(formatted_feels_temp);

        RotateDrawable wind_dir_icon = (RotateDrawable) Objects.requireNonNull(ResourcesCompat.getDrawable(
                getResources(), R.drawable.ic_wind_dir_animated, null)).mutate(); // Call 'mutate()' to create a copy
        Objects.requireNonNull(wind_dir_icon).setColorFilter(ResourcesCompat.getColor(
                getResources(), R.color.ic_details_fill, null), PorterDuff.Mode.SRC_IN); // Fill with details icon color
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

        wind_speed.setText(formatDecimal(current.wind_speed * (new UserPreferences(getContext()).getSpeedUnit().equals("mph") ? 1 : 3.6f)) + " "
                + new UserPreferences(getContext()).getSpeedUnit());
        loadTextViewDrawable(wind_speed, R.drawable.ic_wind_speed, R.color.ic_details_fill, "start");
        humidity.setText(formatDecimal(current.humidity) + " %");
        loadTextViewDrawable(humidity, R.drawable.ic_humidity, R.color.ic_details_fill, "start");

        sunrise.setText(timestampToTime(current.sunrise));
        loadTextViewDrawable(sunrise, R.drawable.ic_sunrise, R.color.ic_sun_fill, "start");
        sunset.setText(timestampToTime(current.sunset));
        loadTextViewDrawable(sunset, R.drawable.ic_sunset, R.color.ic_sun_fill, "start");

        final String device_local_time = DateFormat.getTimeInstance(DateFormat.SHORT).format(new Date().getTime());
        final String update_time = getResources().getString(R.string.last_updated) + " " + device_local_time;
        updated.setText(update_time);

        rootView.animate().alpha(1f).setDuration(getResources().getInteger(R.integer.tab_content_fade_in_anim_speed)).setInterpolator(new DecelerateInterpolator()).start();
    }
}