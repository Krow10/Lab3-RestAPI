package com.example.lab3_restapi;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * This class acts as the intermediary between the <a href="https://openweathermap.org/api">OpenWeather API</a> and the app for handling requests.
 * <p>It uses the Geocoder library as well as the Volley library to extract more precise location from the user input and make requests to the API.</p>
 * <p>It is intended to be used as a <i>no-instance</i> helper class (hence static methods) to actualize the weather data when needed.</p>
 */
public final class FetchData {
    /**
     * Blocking method to request new forecast data from the API. Must be called from a thread to prevent stalling the flow of execution.
     * @param context required for the Geocoder and Volley libraries
     * @param city the city for which to retrieve weather data
     * @return The JSON response from the API.
     */
    public static JSONObject fetchAPIData(Context context, String city) {
        LatLng city_coordinates = getCityLocation(context, city);
        if (city_coordinates == null) {
            Log.e("fetchAPIData", "Could not get city '" + city + "' coordinates.");
            return null;
        }

        // Setup OpenWeather API request for live and forecast data
        RequestQueue queue = Volley.newRequestQueue(context);
        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        String url = "https://api.openweathermap.org/data/2.5/onecall?"
                + "lat=" + city_coordinates.latitude
                + "&lon=" + city_coordinates.longitude
                + "&units=" + new UserPreferences(context).getUnits()
                + "&appid=" + context.getResources().getString(R.string.api_key);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, future, error -> System.err.println("Error fetching data  : " + error.getMessage()));
        queue.add(jsonObjectRequest);

        try {
            return future.get(); // Blocking call since this method is launched from a alternate thread
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Lookup latitude and longitude coordinates for the location provided. Adapted from Steve Benett's <a href="https://stackoverflow.com/a/20166820">answer</a>.
     * @param context required for the Geocoder library
     * @param city the location to lookup
     * @return The geographic position of the closest match to the location provided.
     */
    public static LatLng getCityLocation(Context context, final String city) {
        List<Address> addresses = getCitySuggestions(context, city);

        if (addresses != null) {
            List<LatLng> ll = new ArrayList<>(addresses.size()); // A list to save the coordinates if they are available
            for (Address a : addresses) {
                if (a.hasLatitude() && a.hasLongitude()) {
                    ll.add(new LatLng(a.getLatitude(), a.getLongitude()));
                }
            }

            return ll.isEmpty() ? null : ll.get(0);
        } else {
            return null;
        }
    }

    /**
     * Provide up to five matches from the Geocoder library for the location provided (useful if user misspells the name of the location for example).
     * @param context required for the Geocoder library
     * @param city the location to lookup
     * @return A list of up to 5 addresses matching the provided location.
     */
    public static List<Address> getCitySuggestions(Context context, final String city) {
        if (Geocoder.isPresent()) {
            try {
                Geocoder gc = new Geocoder(context);
                return gc.getFromLocationName(city, 5);
            } catch (IOException | IndexOutOfBoundsException e) {
                Log.e("FetchData", "Unable to find this location (" + city + ") : " + e.getMessage());
            }
        }

        return null;
    }
}
