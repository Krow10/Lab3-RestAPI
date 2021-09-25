package com.example.lab3_restapi;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;

public class WeatherCollectionAdapter extends FragmentStateAdapter {
    private ArrayList<Fragment> weather_fragments;

    public WeatherCollectionAdapter(Fragment fragment) {
        super(fragment);

        weather_fragments = new ArrayList<>();
        weather_fragments.add(new WeatherLiveFragment());
        weather_fragments.add(new WeatherLiveFragment());
        weather_fragments.add(new WeatherLiveFragment());
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment = weather_fragments.get(position);
        return fragment;
    }

    @Override
    public int getItemCount() {
        return weather_fragments.size();
    }

    public void addFragment(Fragment fragment) { // TODO : Use this method to add week forecast tabs
        weather_fragments.add(fragment);
    }
}
