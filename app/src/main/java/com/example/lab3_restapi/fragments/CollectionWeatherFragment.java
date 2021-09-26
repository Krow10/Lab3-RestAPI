package com.example.lab3_restapi.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.lab3_restapi.APIData;
import com.example.lab3_restapi.CityPreferences;
import com.example.lab3_restapi.FetchData;
import com.example.lab3_restapi.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class CollectionWeatherFragment extends Fragment {
    WeatherCollectionAdapter weatherCollectionAdapter;
    TabLayout tabLayout;
    ViewPager2 viewPager;
    private APIData api_data;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.weather_viewpager, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        weatherCollectionAdapter = new WeatherCollectionAdapter(this);
        viewPager = view.findViewById(R.id.forecast_viewpager);
        viewPager.setAdapter(weatherCollectionAdapter);

        if (tabLayout != null) {
            new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
                String tab_title;
                switch (position) {
                    case 0:
                        tab_title = "Live";
                        break;
                    case 1:
                        tab_title = "Today";
                        break;
                    default:
                        tab_title = "Day " + (position - 1); // TODO : Change to a date format
                }

                tab.setText(tab_title);
            }).attach();
        }
    }

    public void refreshApiData(Activity activity) {
        new Thread() {
            @Override
            public void run() {
                final String city =  new CityPreferences(activity).getCity();
                api_data = new APIData(FetchData.fetchAPIData(activity.getApplicationContext(), city), city);
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        weatherCollectionAdapter.refreshFragmentsData(activity.getApplicationContext(), api_data); // TODO : Change to allow passing forecast data
                    }
                });
            }
        }.start();
    }

    public void setTabLayout(TabLayout tab) {
        tabLayout = tab;
    }
}
