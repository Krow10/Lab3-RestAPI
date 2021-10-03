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

import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import com.example.lab3_restapi.APIData;
import com.example.lab3_restapi.R;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;

public class WeatherDailyFragment extends WeatherFragment {
    private APIData.WeatherData current;

    private TextView city;

    private ImageView weather_icon;
    private TextView weather_desc;

    private LineChartView temp_chart;

    private TextView sunrise;
    private TextView sunset;

    public WeatherDailyFragment() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.weather_daily, container, false);
        rootView.setAlpha(0f);

        city = rootView.findViewById(R.id.w_daily_city);

        weather_icon = rootView.findViewById(R.id.w_daily_weather_icon);
        weather_desc = rootView.findViewById(R.id.w_daily_weather_desc);

        temp_chart = rootView.findViewById(R.id.w_daily_temp_chart);

        Line line = new Line(new ArrayList<>(3))
            .setColor(ResourcesCompat.getColor(getResources(), R.color.temp_graph_color, null))
            .setCubic(true)
            .setHasLabels(true)
            .setFilled(true)
            .setAreaTransparency(100);

        List<Line> lines = new ArrayList<>();
        lines.add(line);

        LineChartData data = new LineChartData()
            .setLines(lines)
            .setBaseValue(getResources().getInteger(R.integer.temp_chart_y_axis_values_offset)); // Make area below line fill up to the x-axis
        temp_chart.setLineChartData(data); // Link chart data to the chart view (to do before the viewport setup !)

        sunrise = rootView.findViewById(R.id.w_daily_sunrise);
        sunset = rootView.findViewById(R.id.w_daily_sunset);

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
        city.setText(current.city);

        final int icon_id = getIconID(getContext(), current.icon_url);
        weather_icon.setImageResource(icon_id != 0 ? icon_id : R.drawable.ic_baseline_cached_24);
        weather_desc.setText(current.description);

        List<PointValue> temp_values = new ArrayList<>();
        temp_values.add(new PointValue(0, (float) current.temp_morning).setLabel(formatDecimal(current.temp_morning) + "°C"));
        temp_values.add(new PointValue(1, (float) current.temp_day).setLabel(formatDecimal(current.temp_day) + "°C"));
        temp_values.add(new PointValue(2, (float) current.temp_evening).setLabel(formatDecimal(current.temp_evening) + "°C"));

        temp_chart.getLineChartData().getLines().get(0).setValues(temp_values);
        temp_chart.setLineChartData(temp_chart.getLineChartData()); // Update graph data

        // Scale the y axis to prevent line chart to start from the lowest y value
        // From @lecho (https://github.com/lecho/hellocharts-android/issues/252#issuecomment-196530789)
        final Viewport max_viewport = new Viewport(temp_chart.getMaximumViewport());
        max_viewport.top += getResources().getInteger(R.integer.temp_chart_y_axis_values_offset);
        max_viewport.bottom -= getResources().getInteger(R.integer.temp_chart_y_axis_values_offset);

        temp_chart.setViewportCalculationEnabled(false);
        temp_chart.setZoomEnabled(false);
        temp_chart.setMaximumViewport(max_viewport);
        temp_chart.setCurrentViewport(max_viewport);

        sunrise.setText(timestampToDate(current.sunrise));
        loadIconText(sunrise, R.drawable.ic_sunrise, R.color.ic_sun_fill, "start");
        sunset.setText(timestampToDate(current.sunset));
        loadIconText(sunset, R.drawable.ic_sunset, R.color.ic_sun_fill, "start");
    }
}
