package com.example.lab3_restapi;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.function.Function;

public class APIData {
    private WeatherData current;
    private final ArrayList<WeatherData> hourly;
    private final ArrayList<WeatherData> daily;

    public static final class WeatherData implements Parcelable {
        public long timestamp;
        public long sunrise;
        public long sunset;
        public double temp;
        public double feels_like;
        public double pressure;
        public double humidity;
        public double wind_speed;
        public double wind_deg;
        public String city;
        public String description;
        public String icon_url;

        public WeatherData() {}

        protected WeatherData(Parcel in) {
            timestamp = in.readLong();
            sunrise = in.readLong();
            sunset = in.readLong();
            temp = in.readDouble();
            feels_like = in.readDouble();
            pressure = in.readDouble();
            humidity = in.readDouble();
            wind_speed = in.readDouble();
            wind_deg = in.readDouble();
            city = in.readString();
            description = in.readString();
            icon_url = in.readString();
        }

        public final Creator<WeatherData> CREATOR = new Creator<WeatherData>() {
            @Override
            public WeatherData createFromParcel(Parcel in) {
                return new WeatherData(in);
            }

            @Override
            public WeatherData[] newArray(int size) {
                return new WeatherData[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeLong(timestamp);
            dest.writeLong(sunrise);
            dest.writeLong(sunset);
            dest.writeDouble(temp);
            dest.writeDouble(feels_like);
            dest.writeDouble(pressure);
            dest.writeDouble(humidity);
            dest.writeDouble(wind_speed);
            dest.writeDouble(wind_deg);
            dest.writeString(city);
            dest.writeString(description);
            dest.writeString(icon_url);
        }
    }

    public APIData(JSONObject root, final String city) {
        current = new WeatherData();
        hourly = new ArrayList<>();
        daily = new ArrayList<>();

        refreshData(root, city);
    }

    public void refreshData(JSONObject root, final String city) {
        try {
            long timezone_offset = root.getLong("timezone_offset");
            current = parseWeatherData(root.getJSONObject("current"), city, timezone_offset);

            JSONArray hourly_weather = root.getJSONArray("hourly");
            for (int i = 0; i < hourly_weather.length(); i++)
                hourly.add(parseWeatherData(hourly_weather.getJSONObject(i), city, timezone_offset));

            JSONArray daily_weather = root.getJSONArray("daily");
            for (int i = 0; i < daily_weather.length(); i++)
                daily.add(parseWeatherData(daily_weather.getJSONObject(i), city, timezone_offset));

            Log.d(getClass().getName(), "Refresh : " + root.toString(4));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public WeatherData getCurrentWeatherData() {
        return current;
    }

    public ArrayList<WeatherData> getHourlyWeatherData() {
        return new ArrayList<>(hourly.subList(0, 18));
    }

    public WeatherData getDailyWeatherData(int day) {
        return daily.get(day);
    }

    private WeatherData parseWeatherData(JSONObject obj, final String city, long timezone_offset) { // TODO : Parse differently for live, hourly and daily
        try {
            JSONObject current_weather = obj.getJSONArray("weather").getJSONObject(0);
            Function<Long, Long> changeTimezone = (Function<Long, Long>) l -> l + timezone_offset;

            WeatherData w = new WeatherData();
            w.city = city;
            w.timestamp = changeTimezone.apply(obj.getLong("dt"));
            w.sunrise = changeTimezone.apply(obj.has("sunrise") ? obj.getLong("sunrise") : 0);
            w.sunset = changeTimezone.apply(obj.has("sunset") ? obj.getLong("sunset") : 0);
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
