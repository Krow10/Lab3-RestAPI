package com.example.lab3_restapi;

import android.app.Activity;
import android.content.SharedPreferences;

public class UserPreferences {
    private final SharedPreferences prefs;

    public UserPreferences(Activity activity){
        prefs = activity.getPreferences(Activity.MODE_PRIVATE);
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

    void setUnits(String unit) {
        prefs.edit().putString("units", unit).apply();
    }
}
