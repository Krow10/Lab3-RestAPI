package com.example.lab3_restapi.fragments;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
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
import android.widget.LinearLayout;
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
    private LinearLayout details_layout;
    private TextView current_temperature;
    private TextView feels_temperature;
    private TextView wind_dir;
    private TextView wind_speed;
    private TextView humidity;
    private LinearLayout sun_layout;
    private TextView sunrise;
    private TextView sunset;
    private TextView updated;

    public WeatherLiveFragment() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.weather_live, container, false);
        rootView.setAlpha(0f);
        old_wind_deg = 0;

        details_layout = rootView.findViewById(R.id.w_live_details_layout);
        sun_layout = rootView.findViewById(R.id.w_live_sun_layout);
        city = (TextView) rootView.findViewById(R.id.w_live_city);
        local_time = (TextView) rootView.findViewById(R.id.w_live_local_time);
        weather_icon = (ImageView) rootView.findViewById(R.id.w_live_weather_icon);
        weather_desc = (TextView) rootView.findViewById(R.id.w_live_weather_desc);
        current_temperature = (TextView) rootView.findViewById(R.id.w_live_current_temperature);
        feels_temperature = (TextView) rootView.findViewById(R.id.w_live_feels_temperature);
        wind_dir = (TextView) rootView.findViewById(R.id.w_live_wind_dir);
        wind_speed = (TextView) rootView.findViewById(R.id.w_live_wind_speed);
        humidity = (TextView) rootView.findViewById(R.id.w_live_humidity);
        sunrise = (TextView) rootView.findViewById(R.id.w_live_sunrise);
        sunset = (TextView) rootView.findViewById(R.id.w_live_sunset);
        updated = (TextView) rootView.findViewById(R.id.w_live_updated);

        if (current != null)
            updateFields();

        rootView.animate().alpha(1f).setDuration(getResources().getInteger(R.integer.tab_content_fade_in_anim_speed)).setInterpolator(new DecelerateInterpolator()).start();
        return rootView;
    }

    @Override
    public void updateWeatherData(Context ctx, ArrayList<APIData.WeatherData> current_) { // TODO : Call this every minute for live data refresh
        if (getView() != null) {
            current = current_.get(0);
            Log.d(this.getTag(), "Updated live weather data !");
            updateFields();
        } else { // TODO : Add placeholder animations
            new Handler().postDelayed(() -> updateWeatherData(ctx, current_), ctx.getResources().getInteger(R.integer.api_retry_delay_ms));
        }
    }

    @SuppressLint("SetTextI18n")
    private void updateFields() {
        // TODO : Check for empty weather data

        final int icon_id = getIconID(getContext(), current.icon_url);
        final String temp = new DecimalFormat("0.#").format(current.temp); // TODO : Make °C / °F user preferences
        final String feels_temp = getResources().getString(R.string.feels_like) + formatDecimal(current.feels_like);
        SpannableString formatted_feels_temp = new SpannableString(feels_temp);
            formatted_feels_temp.setSpan(new StyleSpan(Typeface.BOLD), 0, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        final String wind_direction = getDirectionFromAngle(current.wind_deg);

        RotateDrawable wind_dir_icon = (RotateDrawable) ResourcesCompat.getDrawable(getResources(), R.drawable.ic_wind_dir_animated, null);
        Objects.requireNonNull(wind_dir_icon).setColorFilter(ResourcesCompat.getColor(getResources(), R.color.ic_details_fill, null), PorterDuff.Mode.SRC_IN);
        wind_dir_icon.setFromDegrees(old_wind_deg);
        wind_dir_icon.setToDegrees((float) current.wind_deg);
        old_wind_deg = (float) current.wind_deg;
        ObjectAnimator anim = ObjectAnimator.ofInt(wind_dir_icon, "level", 0, 10000);
        anim.setDuration(getResources().getInteger(R.integer.wind_dir_direction_anim_speed));
        anim.setInterpolator(new AnticipateOvershootInterpolator());
        anim.start();

        final String device_local_time = DateFormat.getTimeInstance(DateFormat.SHORT).format(new Date().getTime());
        final String update_time = getResources().getString(R.string.last_updated) + device_local_time;

        city.setText(current.city);
        local_time.setText("Local time : " + timestampToDate(current.timestamp));
        weather_icon.setImageResource(icon_id != 0 ? icon_id : R.drawable.ic_baseline_cached_24);
        weather_desc.setText(current.description);
        current_temperature.setText(temp);
        feels_temperature.setText(formatted_feels_temp);

        wind_dir.setText(wind_direction);
        wind_dir.setCompoundDrawablesRelativeWithIntrinsicBounds(wind_dir_icon, null, null, null);
        wind_dir.setCompoundDrawablePadding(15);

        wind_speed.setText(formatDecimal(current.wind_speed * 3.6f) + " km/h");
        loadIconText(wind_speed, R.drawable.ic_wind_speed, R.color.ic_details_fill);

        details_layout.setVisibility(View.VISIBLE);

        humidity.setText(formatDecimal(current.humidity) + " %");
        loadIconText(humidity, R.drawable.ic_humidity, R.color.ic_details_fill);

        sunrise.setText(timestampToDate(current.sunrise));
        loadIconText(sunrise, R.drawable.ic_sunrise, R.color.ic_sun_fill);

        sunset.setText(timestampToDate(current.sunset));
        loadIconText(sunset, R.drawable.ic_sunset, R.color.ic_sun_fill);

        sun_layout.setVisibility(View.VISIBLE);

        updated.setText(update_time);
    }

    // From @Reverend Gonzo (https://stackoverflow.com/a/2131294)
    private String getDirectionFromAngle(final double angle) {
        String[] directions = {"N", "NE", "E", "SE", "S", "SW", "W", "NW"};
        return directions[(int)Math.round(((angle % 360) / 45)) % 8];
    }

    private String formatDecimal(final double d) {
        return new DecimalFormat("0.#").format(d);
    }

    private int getIconID(Context context, String iconRef) {
        if (iconRef.contains("03") || iconRef.contains("04"))
            iconRef = iconRef.substring(0, 2) + "dn";
        return context.getResources().getIdentifier("ic_" + iconRef, "drawable", context.getPackageName());
    }

    private void loadIconText(TextView tv, int id, int color_id) {
        Drawable d = ResourcesCompat.getDrawable(getResources(), id, null);
        Objects.requireNonNull(d).setColorFilter(ResourcesCompat.getColor(getResources(), color_id, null), PorterDuff.Mode.SRC_IN);
        tv.setCompoundDrawablesRelativeWithIntrinsicBounds(d, null, null, null);
        tv.setCompoundDrawablePadding(15);
    }
}