package com.example.lab3_restapi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import com.example.lab3_restapi.fragments.WeatherViewPagerFragment;

import java.util.List;
import java.util.Objects;

/**
 * This class represents the app's main activity launched at startup.
 */
public class MainActivity extends AppCompatActivity {
    private WeatherViewPagerFragment frag_manager;
    private Toast error_toast;

    /**
     * Inherited listener used to initialize the view, app bar and tabs as well as fetching data from the API for the first time.
     * @param savedInstanceState unused
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN); // Prevent keyboard from pushing up the view
        setContentView(R.layout.activity_main);
        setSupportActionBar(findViewById(R.id.appbar));

        frag_manager = new WeatherViewPagerFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.main_container, frag_manager).commit();
        frag_manager.setTabLayout(findViewById(R.id.forecast_tabs_layout));
        frag_manager.refreshApiData(this);
    }

    /**
     * Inherited listener used to apply new settings when coming back from the {@link UserSettingsActivity}.
     */
    @Override
    protected void onResume() {
        super.onResume();
        frag_manager.refreshApiData(this);
        frag_manager.restartUpdateTimer();
    }

    /**
     * Inherited listener used to setup all the menu actions (refresh, edit location and overflow menu).
     * @param menu the app bar menu
     * @return <code>True</code> for the menu to be displayed.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.app_bar_menu, menu);

        // Setup refresh icon animation
        MenuItem refresh_item = menu.findItem(R.id.action_refresh_data);
        ImageView refresh_iv = (ImageView) refresh_item.getActionView();
        Activity main = this;
        refresh_iv.setOnClickListener((View v) -> {
            frag_manager.refreshApiData(main);

            Animation refresh_anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.refresh_contacts_rotate);
            refresh_anim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}

                @Override
                public void onAnimationEnd(Animation animation) { v.setEnabled(true); }

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });

            v.startAnimation(refresh_anim);
            v.setEnabled(false); // Prevent refresh spamming
        });

        // Setup the change location icon actions
        MenuItem location_item = menu.findItem(R.id.action_change_location);
        location_item.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                item.setVisible(false); // Make this item not appear in overflow menu when SearchView is expanded since we're already editing the city
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                /*
                    Problem : If overflow menu is clicked while SearchView is expanded, some menu items disappear after returning to collapsed state.

                    Solution : Invalidate the options menu to recalculate available space for items. Though items are still appearing and disappearing when they
                               shouldn't when clicking the overflow menu while SearchView is expanded.
                 */
                supportInvalidateOptionsMenu();
                SearchView sv = item.getActionView().findViewById(R.id.location_searchview);
                if (sv != null)
                    sv.clearFocus(); // Hides keyboard when SearchView is collapsed
                return true;
            }
        });
        location_item.setOnMenuItemClickListener(item -> {
            SearchView location_searchview = item.getActionView().findViewById(R.id.location_searchview);
            location_searchview.setQuery("", false); // Reset text in search field for quicker typing
            location_searchview.requestFocusFromTouch();
            showKeyboard();

            location_searchview.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    List<Address> cities_suggestions = FetchData.getCitySuggestions(getApplicationContext(), query);
                    if (!Objects.requireNonNull(cities_suggestions).isEmpty()) {
                        Address new_city = cities_suggestions.get(0);
                        new UserPreferences(getApplicationContext()).setCity((new_city.getLocality() == null ? new_city.getFeatureName() : new_city.getLocality())
                                + ", " + new_city.getCountryCode()); // Format user input by using proper location spelling
                        frag_manager.refreshApiData(main);
                        location_item.collapseActionView();

                        return true;
                    } else {
                        if (error_toast != null)
                            error_toast.cancel();

                        error_toast = Toast.makeText(getApplicationContext(), getString(R.string.location_not_found_error_msg), Toast.LENGTH_LONG);
                        error_toast.show();

                        return false;
                    }
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    return true;
                }
            });

            return true;
        });

        // Setup settings action
        menu.findItem(R.id.action_show_settings).setOnMenuItemClickListener(item -> {
            Intent intent = new Intent(main, UserSettingsActivity.class);
            startActivity(intent);
            return true;
        });

        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Utility method to force keyboard to appear (used for faster interaction with city location editing).
     */
    private void showKeyboard() {
        ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }
}