package com.example.lab3_restapi;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

public class UserPreferences {
    private final SharedPreferences prefs;

    public UserPreferences(Context context){
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public String getCity(){
        return prefs.getString("city", "Montreal, CA");
    }

    void setCity(String city){
        prefs.edit().putString("city", city).apply();
    }

    public String getUnits() {
        return prefs.getString("units", "metric");
    }

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

    public String getSpeedUnit() {
        final String unit_pref = prefs.getString("units", "metric");

        if (unit_pref.equals("imperial"))
            return "mph";
        else
            return "km/h";
    }

    public long getRefreshInterval() {
        return Long.parseLong(prefs.getString("data_refresh_interval", "1"));
    }
}
