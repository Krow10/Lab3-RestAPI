package com.example.lab3_restapi;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class APIData {
    private WeatherData current;
    private List<WeatherData> hourly;
    private List<WeatherData> daily;

    public final class WeatherData implements Serializable {
        public String city;
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

    public APIData(JSONObject root, final String city) {
        current = new WeatherData();
        hourly = new ArrayList<>();
        daily = new ArrayList<>();

        refreshData(root, city);
    }

    public void refreshData(JSONObject root, final String city) {
        try { // TODO : Add constant strings to xml file + parse json in other function (prevent c/c for forecast parsing, etc...)
            long timezone_offset = root.getLong("timezone_offset");
            current = parseWeatherData(root.getJSONObject("current"), city, timezone_offset);
            JSONArray hourly_weather = root.getJSONArray("hourly");
            for (int i = 0; i < hourly_weather.length(); i++)
                hourly.add(parseWeatherData(hourly_weather.getJSONObject(i), city, timezone_offset));

            JSONArray daily_weather = root.getJSONArray("daily");
            for (int i = 0; i < daily_weather.length(); i++)
                daily.add(parseWeatherData(daily_weather.getJSONObject(i), city, timezone_offset));

            Log.d(getClass().getName(), "Hourly : " + hourly + "\nDaily : " + daily);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public WeatherData getCurrentWeatherData() {
        return current;
    }

    public List<WeatherData> getHourlyWeatherData() {
        return hourly;
    }

    public List<WeatherData> getDailyWeatherData() {
        return daily;
    }

    private WeatherData parseWeatherData(JSONObject obj, final String city, long timezone_offset) {
        try {
            JSONObject current_weather = obj.getJSONArray("weather").getJSONObject(0);
            Function<Long, Long> convertTimestamp = (Function<Long, Long>) l -> Long.valueOf(l + timezone_offset*1000);

            WeatherData w = new WeatherData();
            w.city = city;
            w.timestamp = convertTimestamp.apply(obj.getLong("dt"));
            w.sunrise = convertTimestamp.apply(obj.has("sunrise") ? obj.getLong("sunrise") : 0);
            w.sunset = convertTimestamp.apply(obj.has("sunset") ? obj.getLong("sunset") : 0);
            w.temp = obj.get("temp") instanceof JSONObject ? ((JSONObject) obj.get("temp")).getDouble("day") : obj.getDouble("temp");
            w.feels_like = obj.get("feels_like") instanceof JSONObject ? ((JSONObject) obj.get("feels_like")).getDouble("day") : obj.getDouble("feels_like");
            w.pressure = obj.getDouble("pressure");
            w.humidity = obj.getDouble("humidity");
            w.wind_speed = obj.getDouble("wind_speed");
            w.wind_deg = obj.getDouble("wind_deg");
            w.description = current_weather.getString("description");
            w.icon_url = current_weather.getString("icon");

            return w;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new WeatherData();
    }
}
