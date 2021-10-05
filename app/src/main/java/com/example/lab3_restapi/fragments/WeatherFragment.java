package com.example.lab3_restapi.fragments;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.example.lab3_restapi.APIData;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;
import java.util.TimeZone;

/**
 * This class is the base class for the other forecast fragments and contains utility methods for those classes.
 *
 * @see WeatherLiveFragment
 * @see WeatherHourlyFragment
 * @see WeatherDailyFragment
 */
public abstract class WeatherFragment extends Fragment {
    /**
     * Used for setting up a callback to retry updating weather data if the view for a fragment isn't available.
     *
     * @see #updateWeatherData(Context, ArrayList)
     */
    protected final Handler update_weather_data_handler = new Handler();

    /**
     * Method implemented by all fragments to update their display values with the latest forecast data.
     * @param ctx the context used to setup the retry callback
     * @param current the new forecast data
     */
    public abstract void updateWeatherData(Context ctx, ArrayList<APIData.WeatherData> current);

    /**
     * Convert a UNIX epoch timestamp to a time formatted string.
     * @param timestamp the timestamp to convert
     * @return The formatted time (XX:XX A.M/P.M).
     */
    public static String timestampToTime(long timestamp) {
        DateFormat d_format = DateFormat.getTimeInstance(DateFormat.SHORT);
        d_format.setTimeZone(TimeZone.getTimeZone("GMT")); // Set timezone to GMT because timestamp already accounts for the city's hour offset
        return d_format.format(new Date(timestamp * 1000L)); // Multiply by 1000 to convert timestamp to milliseconds
    }

    /**
     * Convert an angle in degree to a cardinal direction. From Reverend Gonzo's <a href="https://stackoverflow.com/a/2131294">answer</a>.
     * @param angle the angle in degree
     * @return The abbreviated cardinal direction (N, NE, etc.).
     */
    public String getDirectionFromAngle(final double angle) {
        String[] directions = {"N", "NE", "E", "SE", "S", "SW", "W", "NW"};
        return directions[(int)Math.round(((angle % 360) / 45)) % 8];
    }

    /**
     * Format a decimal number to one decimal using the {@link DecimalFormat} class.
     * @param d the number to format
     * @return The formatted number with one decimal maximum (or none if that decimal is zero).
     */
    public String formatDecimal(final double d) {
        return new DecimalFormat("0.#").format(d);
    }

    /**
     * Retrieve a weather icon ID from the <i>drawable</i> folder using the icon's name. The naming convention for the icons is the same used by the
     * <a href="https://openweathermap.org/weather-conditions#How-to-get-icon-URL">OpenWeather API</a>.
     * @param context used to get access to the resources
     * @param iconRef the name of the desired icon
     * @return The ID of the icon resource.
     */
    public int getIconID(Context context, String iconRef) {
        if (iconRef.contains("03") || iconRef.contains("04")) // Handle cases where day and night icon are the same
            iconRef = iconRef.substring(0, 2) + "dn";

        return context.getResources().getIdentifier("ic_" + iconRef, "drawable", context.getPackageName());
    }

    /**
     * Set a drawable to the specified side of the <code>TextView</code> and fill it with the specified color.
     * @param tv the targeted <code>TextView</code>
     * @param drawable_id the drawable ID to add to the <code>TextView</code>
     * @param color_id the color ID for filling the drawable
     * @param side the side where the drawable should be displayed (one of <i>start</i>, <i>top</i>, <i>end</i> or <i>bottom</i>)
     */
    public void loadTextViewDrawable(TextView tv, int drawable_id, int color_id, String side) {
        Drawable d = ResourcesCompat.getDrawable(getResources(), drawable_id, null);
        Objects.requireNonNull(d).setColorFilter(ResourcesCompat.getColor(getResources(), color_id, null), PorterDuff.Mode.SRC_IN);
        switch (side) {
            case "start":
                tv.setCompoundDrawablesRelativeWithIntrinsicBounds(d, null, null, null);
                break;
            case "top":
                tv.setCompoundDrawablesRelativeWithIntrinsicBounds(null, d, null, null);
                break;
            case "end":
                tv.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, d, null);
                break;
            case "bottom":
                tv.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, d);
                break;
            default:
                tv.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null);
                break;
        }
        tv.setCompoundDrawablePadding(15);
    }
}
