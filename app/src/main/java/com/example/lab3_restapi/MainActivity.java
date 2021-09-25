package com.example.lab3_restapi;

import android.app.Activity;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    public static APIData api_data;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        refreshApiData();

        if (savedInstanceState == null)
            getSupportFragmentManager().beginTransaction().add(R.id.main_container, new CollectionWeatherFragment()).commit();
    }

    public void refreshApiData() {
        Activity main_activity = this;

        new Thread() {
            @Override
            public void run() {
                api_data = new APIData(FetchData.fetchAPIData(MainActivity.this, new CityPreferences(main_activity).getCity()));
            }
        }.start();
    }
}