package com.example.lab3_restapi;

import android.app.Activity;
import android.content.SharedPreferences;

public class CityPreferences {
    private SharedPreferences prefs;

    public CityPreferences(Activity activity){
        prefs = activity.getPreferences(Activity.MODE_PRIVATE);
    }

    public String getCity(){
        return prefs.getString("city", "Montreal, CA");
    }

    void setCity(String city){
        prefs.edit().putString("city", city).commit();
    }
}
