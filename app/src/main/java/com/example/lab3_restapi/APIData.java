package com.example.lab3_restapi;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class APIData {
    private WeatherData current;
    private List<WeatherData> hourly;
    private List<WeatherData> daily;

    public final class WeatherData {
        public long timestamp;
        public long sunrise;
        public long sunset;
        public double temp;
        public double feels_like;
        public double pressure;
        public double humidity;
        public double wind_speed;
        public double wind_deg;
        public String description;
        public String icon_url;
    }

    public APIData(JSONObject root) {
        current = new WeatherData();
        hourly = new ArrayList<>();
        daily = new ArrayList<>();

        refreshData(root);
    }

    public void refreshData(JSONObject root) {
        try { // TODO : Add constant strings to xml file + parse json in other function (prevent c/c for forecast parsing, etc...)
            JSONObject current_obj = root.getJSONObject("current");
            JSONObject current_weather = current_obj.getJSONArray("weather").getJSONObject(0);
            Function<Long, Long> convertTimestamp = (Function<Long, Long>) l -> {
                try { return Long.valueOf((l + root.getLong("timezone_offset"))*1000); } catch (JSONException e) { e.printStackTrace(); }
                return 0l;
            };

            current = new WeatherData();
            current.timestamp = convertTimestamp.apply(current_obj.getLong("dt"));
            current.sunrise = convertTimestamp.apply(current_obj.getLong("sunrise"));
            current.sunset = convertTimestamp.apply(current_obj.getLong("sunset"));
            current.temp = current_obj.getDouble("temp");
            current.feels_like = current_obj.getDouble("feels_like");
            current.pressure = current_obj.getDouble("pressure");
            current.humidity = current_obj.getDouble("humidity");
            current.wind_speed = current_obj.getDouble("wind_speed");
            current.wind_deg = current_obj.getDouble("wind_deg");
            current.description = current_weather.getString("description");
            current.icon_url = current_weather.getString("icon");

            // TODO : Refresh for hourly and daily
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public WeatherData getCurrentWeatherData() {
        return current;
    }

    public WeatherData getHourlyWeatherData(int hour) {
        return hourly.get(hour);
    }

    public WeatherData getDailyWeatherData(int day) {
        return daily.get(day);
    }
}
