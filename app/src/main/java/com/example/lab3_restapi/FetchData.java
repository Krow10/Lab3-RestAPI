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

public class FetchData {
    public static JSONObject fetchAPIData(Context context, String city) {
        // Setup OpenWeather API request for live and forecast data
        RequestQueue queue = Volley.newRequestQueue(context);
        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        LatLng city_coordinates = getCityLocation(context, city);
        String url = "https://api.openweathermap.org/data/2.5/onecall?"
                + "lat=" + city_coordinates.latitude
                + "&lon=" + city_coordinates.longitude
                + "&units=metric" // TODO : Add units as user preference
                + "&appid=" + context.getResources().getString(R.string.api_key);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, future, error -> System.err.println("Error fetching data  : " + error.getMessage()));

        Log.d(FetchData.class.getName(), "API request sent !");
        queue.add(jsonObjectRequest);

        try {
            return future.get(); // Blocking call
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        } catch (ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

    // From @Steve Benett (https://stackoverflow.com/a/20166820)
    private static LatLng getCityLocation(Context context, final String city) {
        if (Geocoder.isPresent()) {
            try {
                Geocoder gc = new Geocoder(context);
                List<Address> addresses= gc.getFromLocationName(city, 5); // get the found Address Objects

                List<LatLng> ll = new ArrayList<LatLng>(addresses.size()); // A list to save the coordinates if they are available
                for(Address a : addresses){
                    if(a.hasLatitude() && a.hasLongitude()){
                        ll.add(new LatLng(a.getLatitude(), a.getLongitude()));
                    }
                }

                return ll.get(0);
            } catch (IOException e) { // TODO : Show notification for user
                System.err.println("Unable to find this location (" + city + ") : " + e.getMessage());
            }
        }

        return new LatLng(45.5017, 73.5673); // Default to Montreal weather
    }
}
