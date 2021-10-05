package com.example.lab3_restapi.fragments;

import android.content.Context;
import android.os.Bundle;
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
import com.example.lab3_restapi.UserPreferences;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;

/**
 * This class represent the daily forecast view used in the <i>day</i> tabs.
 */
public class WeatherDailyFragment extends WeatherFragment {
    private APIData.WeatherData current;

    private TextView city;

    private ImageView weather_icon;
    private TextView weather_desc;

    private LineChartView temp_chart;

    private TextView sunrise;
    private TextView sunset;

    /**
     * Default empty constructor.
     */
    public WeatherDailyFragment() {}

    /**
     * Inherited listener used to inflate the layout content. Also setup the temperature line chart appearance.
     * @param inflater the layout inflater
     * @param container the container for the new layout
     * @param savedInstanceState unused
     * @return The view created from the layout definition.
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.weather_daily, container, false);
        rootView.setAlpha(0f);

        city = rootView.findViewById(R.id.w_daily_city);

        weather_icon = rootView.findViewById(R.id.w_daily_weather_icon);
        weather_desc = rootView.findViewById(R.id.w_daily_weather_desc);

        temp_chart = rootView.findViewById(R.id.w_daily_temp_chart);

        Line line = new Line(new ArrayList<>(3)) // 3 points for morning, day, evening
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
        temp_chart.setLineChartData(data);

        sunrise = rootView.findViewById(R.id.w_daily_sunrise);
        sunset = rootView.findViewById(R.id.w_daily_sunset);

        if (current != null)
            updateFields();

        rootView.animate().alpha(1f).setDuration(getResources().getInteger(R.integer.tab_content_fade_in_anim_speed)).setInterpolator(new DecelerateInterpolator()).start();
        return rootView;
    }

    /**
     * Inherited method to update the view's fields. It is called recursively until the view is ready.
     * @param ctx used to get the delay before retrying the update
     * @param new_forecast the new forecast data
     */
    @Override
    public void updateWeatherData(Context ctx, ArrayList<APIData.WeatherData> new_forecast) {
        if (getView() != null) {
            current = new_forecast.get(0);
            updateFields();
        } else {
            update_weather_data_handler.removeCallbacksAndMessages(null);
            update_weather_data_handler.postDelayed(() -> updateWeatherData(ctx, new_forecast), ctx.getResources().getInteger(R.integer.api_retry_delay_ms));
        }
    }

    /**
     * Update all the displayed values with the current forecast values.
     */
    private void updateFields() {
        city.setText(current.city);

        final int icon_id = getIconID(getContext(), current.icon_url);
        weather_icon.setImageResource(icon_id != 0 ? icon_id : R.drawable.ic_baseline_cached_24);
        weather_desc.setText(current.description);

        // Create new temperature values with respective labels
        final String temp_unit = new UserPreferences(getContext()).getTempUnit();
        List<PointValue> temp_values = new ArrayList<>();
        temp_values.add(new PointValue(0, (float) current.temp_morning).setLabel(formatDecimal(current.temp_morning) + temp_unit));
        temp_values.add(new PointValue(1, (float) current.temp_day).setLabel(formatDecimal(current.temp_day) + temp_unit));
        temp_values.add(new PointValue(2, (float) current.temp_evening).setLabel(formatDecimal(current.temp_evening) + temp_unit));

        // Update graph data
        LineChartData updated_data = new LineChartData(temp_chart.getLineChartData());
        updated_data.getLines().get(0).setValues(temp_values);
        temp_chart.setLineChartData(updated_data);

        // Scale the y axis to prevent line chart to start from the lowest y value
        // Adapted from @lecho (https://github.com/lecho/hellocharts-android/issues/252#issuecomment-196530789)
        final Viewport max_viewport = new Viewport(temp_chart.getMaximumViewport());
        max_viewport.top += getResources().getInteger(R.integer.temp_chart_y_axis_values_offset);
        max_viewport.bottom -= getResources().getInteger(R.integer.temp_chart_y_axis_values_offset);

        temp_chart.setZoomEnabled(false);
        temp_chart.setMaximumViewport(max_viewport);
        temp_chart.setCurrentViewport(max_viewport);

        sunrise.setText(timestampToTime(current.sunrise));
        loadTextViewDrawable(sunrise, R.drawable.ic_sunrise, R.color.ic_sun_fill, "start");
        sunset.setText(timestampToTime(current.sunset));
        loadTextViewDrawable(sunset, R.drawable.ic_sunset, R.color.ic_sun_fill, "start");
    }
}
