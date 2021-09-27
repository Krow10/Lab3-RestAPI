package com.example.lab3_restapi.fragments;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import androidx.viewpager2.widget.ViewPager2;

import com.example.lab3_restapi.APIData;
import com.example.lab3_restapi.CityPreferences;
import com.example.lab3_restapi.FetchData;
import com.example.lab3_restapi.R;
import com.google.android.material.badge.BadgeDrawable;
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
                        BadgeDrawable rec_icon = tab.getOrCreateBadge();
                        rec_icon.setHorizontalOffset(dpToPixels(-3.5f));
                        rec_icon.setVerticalOffset(dpToPixels(.5f));

                        final ObjectAnimator colorAnim = ObjectAnimator.ofInt(rec_icon, "backgroundColor", rec_icon.getBackgroundColor(), Color.TRANSPARENT);
                            colorAnim.setDuration(getResources().getInteger(R.integer.live_icon_blinking_anim_speed));
                            colorAnim.setEvaluator(new ArgbEvaluator());
                            colorAnim.setInterpolator(new FastOutSlowInInterpolator());
                            colorAnim.setRepeatCount(ValueAnimator.INFINITE);
                            colorAnim.setRepeatMode(ValueAnimator.REVERSE);
                        colorAnim.start();
                        break;
                    case 1:
                        tab_title = "Today";
                        break;
                    case 2:
                        tab_title = "Tomorrow";
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

    private int dpToPixels(float dip) {
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, getResources().getDisplayMetrics()));
    }
}
