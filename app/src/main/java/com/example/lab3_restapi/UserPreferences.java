package com.example.lab3_restapi;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

/**
 * This class holds a reference to the <a href="https://developer.android.com/reference/android/content/SharedPreferences">SharedPreferences</a>
 * that is used to store user settings for the app.
 * <p>It is intended to be instantiated every time a value needs to be retrieved from the user's settings.</p>
 */
public class UserPreferences {
    private final SharedPreferences prefs;

    /**
     * Default constructor.
     * @param context required to access the default shared preferences for the app
     */
    public UserPreferences(Context context){
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    /**
     * Retrieve the user's city for which to fetch weather forecasts.
     * @return The preferred user's city.
     */
    public String getCity(){
        return prefs.getString("city", "Montreal, CA");
    }

    /**
     * Modify the user's city for which to retrieve weather forecasts.
     * @param city the new preferred user's city
     */
    void setCity(String city){
        prefs.edit().putString("city", city).apply();
    }

    /**
     * Retrieve the unit system to use for displaying weather data.
     * <p>This can be one of three values :</p><ul><li>Standard</li><li>Metric</li><li>Imperial</li></ul>
     * @return The current unit system set by the user.
     */
    public String getUnits() {
        return prefs.getString("units", "metric");
    }

    /**
     * Get the temperature unit associated with the current unit system set by the user (K, °C or °F).
     * @return The associated unit.
     */
    public String getTempUnit() {
        String unit = "°C";
        final String unit_pref = prefs.getString("units", "metric");

        switch (unit_pref) {
            case "standard":
                unit = "K";
                break;

            case "imperial":
                unit = "°F";
                break;

            default:
                break;
        }

        return unit;
    }

    /**
     * Get the velocity unit associated with the current unit system set by the user (km/h or mph).
     * @return The associated unit.
     */
    public String getSpeedUnit() {
        final String unit_pref = prefs.getString("units", "metric");

        if (unit_pref.equals("imperial"))
            return "mph";
        else
            return "km/h";
    }

    /**
     * Retrieve the duration between two calls to the API for new data.
     * @return The duration in minutes (1, 5, 10, 30 or 60 minutes).
     */
    public long getRefreshInterval() {
        return Long.parseLong(prefs.getString("data_refresh_interval", "1"));
    }

    /**
     * Retrieve the limit for the number of hourly forecast displayed in the <i>Today</i> tab.
     * @return The limit (between 1 and 24).
     */
    public int getMaxHourlyForecast() {
        return prefs.getInt("max_hourly_forecast", 18);
    }
}
