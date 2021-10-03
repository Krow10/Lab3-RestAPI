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

public abstract class WeatherFragment extends Fragment {
    protected Handler update_weather_data_handler = new Handler();
    public abstract void updateWeatherData(Context ctx, ArrayList<APIData.WeatherData> current);

    public static String timestampToDate(long timestamp) {
        DateFormat d_format = DateFormat.getTimeInstance(DateFormat.SHORT);
        d_format.setTimeZone(TimeZone.getTimeZone("GMT")); // Set timezone to GMT because timestamp already accounts for the city's hour offset
        return d_format.format(new Date(timestamp * 1000L)); // Multiply by 1000 to convert timestamp to milliseconds
    }

    public int getIconID(Context context, String iconRef) {
        if (iconRef.contains("03") || iconRef.contains("04"))
            iconRef = iconRef.substring(0, 2) + "dn";

        return context.getResources().getIdentifier("ic_" + iconRef, "drawable", context.getPackageName());
    }
    // From @Reverend Gonzo (https://stackoverflow.com/a/2131294)
    public String getDirectionFromAngle(final double angle) {
        String[] directions = {"N", "NE", "E", "SE", "S", "SW", "W", "NW"};
        return directions[(int)Math.round(((angle % 360) / 45)) % 8];
    }

    public String formatDecimal(final double d) {
        return new DecimalFormat("0.#").format(d);
    }

    public void loadIconText(TextView tv, int drawable_id, int color_id, String side) {
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
