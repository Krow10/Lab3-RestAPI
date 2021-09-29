package com.example.lab3_restapi.fragments;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
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

import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Calendar;

public class CollectionWeatherFragment extends Fragment {
    private APIData api_data;
    private WeatherCollectionAdapter weatherCollectionAdapter;
    private TabLayout tabLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.weather_viewpager, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        weatherCollectionAdapter = new WeatherCollectionAdapter(this);
        ViewPager2 viewPager = view.findViewById(R.id.forecast_viewpager);
        viewPager.setAdapter(weatherCollectionAdapter);

        if (tabLayout != null) {
            new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
                String tab_title;
                switch (position) {
                    case 0:
                        tab_title = getString(R.string.tab_live);
                        // Setup live tab red dot blink for 'REC' effect
                        BadgeDrawable rec_icon = tab.getOrCreateBadge();
                        rec_icon.setHorizontalOffset(dpToPixels(getResources(), -3.5f));
                        rec_icon.setVerticalOffset(dpToPixels(getResources(), .5f));

                        final ObjectAnimator colorAnim = ObjectAnimator.ofInt(rec_icon, "backgroundColor", rec_icon.getBackgroundColor(), Color.TRANSPARENT);
                        colorAnim.setDuration(getResources().getInteger(R.integer.live_icon_blinking_anim_speed));
                        colorAnim.setEvaluator(new ArgbEvaluator());
                        colorAnim.setInterpolator(new FastOutSlowInInterpolator());
                        colorAnim.setRepeatCount(ValueAnimator.INFINITE);
                        colorAnim.setRepeatMode(ValueAnimator.REVERSE);
                        colorAnim.start();
                        break;
                    case 1:
                        tab_title = getString(R.string.tab_today);
                        break;
                    default:
                        // Convert tab position to full date for the next days
                        Calendar pos_to_date = Calendar.getInstance();
                        pos_to_date.add(Calendar.DAY_OF_YEAR, position - 1);
                        tab_title = DateFormat.getDateInstance(DateFormat.MEDIUM).format(pos_to_date.getTime());
                }

                tab.setText(tab_title);
            }).attach();
        }
    }

    public void refreshApiData(Activity activity) {
        new Thread(() -> {
            final String city =  new CityPreferences(activity).getCity();
            final JSONObject data = FetchData.fetchAPIData(activity.getApplicationContext(), city);
            if (data != null) {
                api_data = new APIData(data, city);
                // Run on UI thread to enable updating the information on the fragments' layout.
                activity.runOnUiThread(() -> weatherCollectionAdapter.refreshFragmentsData(activity.getApplicationContext(), api_data));
            } else {
                Log.e(getTag(), "Unable to fetch API data.");
            }
        }).start();
    }

    public void setTabLayout(TabLayout tab) {
        tabLayout = tab;
    }

    public static int dpToPixels(Resources res, float dip) {
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, res.getDisplayMetrics()));
    }
}
