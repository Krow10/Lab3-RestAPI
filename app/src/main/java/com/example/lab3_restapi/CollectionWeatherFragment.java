package com.example.lab3_restapi;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class CollectionWeatherFragment extends Fragment {
    WeatherCollectionAdapter weatherCollectionAdapter;
    ViewPager2 viewPager;

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

        TabLayout tabLayout = view.findViewById(R.id.forecast_tabs_layout);
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
