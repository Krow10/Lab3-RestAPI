package com.example.lab3_restapi;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.lab3_restapi.fragments.WeatherHourlyFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.function.Function;

/**
 * This class encapsulate all the data coming from the <a href="https://openweathermap.org/api/one-call-api">OpenWeather JSON API</a>
 * and provide access to each of the three different forecast types used in the app : live, hourly (24h) and 7-day forecast.
 * It also exposes a data class, {@link WeatherData}, for holding those forecast's information.
 */
public class APIData {
    private WeatherData current;
    private final ArrayList<WeatherData> hourly;
    private final ArrayList<WeatherData> daily;

    /**
     * This class stores the forecast information like temperature, wind, etc. <p>It implements Parcelable in order to be stored in a
     * Bundle for restoring the state of the RecylerView's adapter when creating the {@link com.example.lab3_restapi.fragments.WeatherDailyFragment} fragment.</p>
     *
     * @see WeatherHourlyFragment#onSaveInstanceState
     */
    public static final class WeatherData implements Parcelable {
        /**
         * Time at which the forecast was made.
         */
        public long timestamp;
        /**
         * Timestamp for sun rise time.
         */
        public long sunrise;
        /**
         * Timestamp for sun set time.
         */
        public long sunset;
        public double temp_morning;
        public double temp_day;
        public double temp_evening;
        /**
         * Temperature accounting for human perception (wind, etc.).
         */
        public double feels_like;
        public double pressure;
        public double humidity;
        public double wind_speed;
        /**
         * Wind direction in degrees.
         */
        public double wind_deg;
        public String city;
        /**
         * Weather description (rainy, sunny, etc.)
         */
        public String description;
        /**
         * Weather image to be retrieved from drawable folder.
         */
        public String icon_url;

        /**
         * Default constructor (empty).
         */
        public WeatherData() {}

        /**
         * Parcelable inherited constructor.
         * @param in used to initialize the Parcelable instance
         */
        protected WeatherData(Parcel in) {
            timestamp = in.readLong();
            sunrise = in.readLong();
            sunset = in.readLong();
            temp_morning = in.readDouble();
            temp_day = in.readDouble();
            temp_evening = in.readDouble();
            feels_like = in.readDouble();
            pressure = in.readDouble();
            humidity = in.readDouble();
            wind_speed = in.readDouble();
            wind_deg = in.readDouble();
            city = in.readString();
            description = in.readString();
            icon_url = in.readString();
        }

        /**
         * From the <a href="https://developer.android.com/reference/android/os/Parcelable">android reference </a> :
         * <i>Interface that must be implemented and provided as a public CREATOR field that generates instances of your Parcelable class from a Parcel.</i>
         */
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

        /**
         * Parcelable implementation (unused).
         * @return 0
         */
        @Override
        public int describeContents() {
            return 0;
        }

        /**
         * Use to store the weather data into a Parcel object.
         * @param dest the Parcel object
         * @param flags additional flags for Parcel format
         */
        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeLong(timestamp);
            dest.writeLong(sunrise);
            dest.writeLong(sunset);
            dest.writeDouble(temp_morning);
            dest.writeDouble(temp_day);
            dest.writeDouble(temp_evening);
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

    /**
     * Default constructor to initialize the JSON parsing of weather data.
     * @param root the JSON result of the API request
     * @param city used to keep track of the current city set by the user
     */
    public APIData(JSONObject root, final String city) {
        current = new WeatherData();
        hourly = new ArrayList<>();
        daily = new ArrayList<>();

        try {
            long timezone_offset = root.getLong("timezone_offset");
            current = parseWeatherData(root.getJSONObject("current"), city, timezone_offset);

            JSONArray hourly_weather = root.getJSONArray("hourly");
            for (int i = 0; i < hourly_weather.length(); i++)
                hourly.add(parseWeatherData(hourly_weather.getJSONObject(i), city, timezone_offset));

            JSONArray daily_weather = root.getJSONArray("daily");
            for (int i = 0; i < daily_weather.length(); i++)
                daily.add(parseWeatherData(daily_weather.getJSONObject(i), city, timezone_offset));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Data for the <i>Live</i> tab.
     * @return The live weather data.
     */
    public WeatherData getCurrentWeatherData() {
        return current;
    }

    /**
     * Data for the <i>Today</i> tab with one forecast per hour.
     * @param max_hourly_forecast between 1 and 24 for the maximum hour lookahead from now
     * @return The hourly forecast data with a maximum forecast of 24 hours.
     */
    public ArrayList<WeatherData> getHourlyWeatherData(final int max_hourly_forecast) {
        return new ArrayList<>(hourly.subList(0, max_hourly_forecast));
    }

    /**
     * Data for the individual <i>day</i> tabs.
     * @param day between 0 and 6 for the forecasted day
     * @return The weather forecast for this particular day.
     */
    public WeatherData getDailyWeatherData(int day) {
        return daily.get(day);
    }

    /**
     * Method to extract all the information from the <i>one-call</i> OpenWeather API data
     * (more info available <a href="https://openweathermap.org/api/one-call-api">here</a>).
     * @param obj represent a <i>weather</i> forecast JSON object (temperature, wind, etc.)
     * @param city the user's current city to store in the {@link WeatherData} object
     * @param timezone_offset used to correct the timestamps provided in the forecast (sunrise, sunset, etc.)
     * @return The parsed forecast.
     */
    private WeatherData parseWeatherData(JSONObject obj, final String city, long timezone_offset) {
        try {
            JSONObject current_weather = obj.getJSONArray("weather").getJSONObject(0);
            Function<Long, Long> changeTimezone = l -> l + timezone_offset;

            WeatherData w = new WeatherData();
            w.city = city;
            w.timestamp = changeTimezone.apply(obj.getLong("dt"));
            w.sunrise = changeTimezone.apply(obj.has("sunrise") ? obj.getLong("sunrise") : 0);
            w.sunset = changeTimezone.apply(obj.has("sunset") ? obj.getLong("sunset") : 0);
            if (obj.get("temp") instanceof JSONObject) {
                w.temp_morning = ((JSONObject) obj.get("temp")).getDouble("morn");
                w.temp_day = ((JSONObject) obj.get("temp")).getDouble("day");
                w.temp_evening = ((JSONObject) obj.get("temp")).getDouble("eve");
            } else {
                w.temp_morning = 0;
                w.temp_day = obj.getDouble("temp");
                w.temp_evening = 0;
            }
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
