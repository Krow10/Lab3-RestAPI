package com.example.lab3_restapi;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lab3_restapi.fragments.CollectionWeatherFragment;
import com.google.android.material.appbar.MaterialToolbar;

public class MainActivity extends AppCompatActivity {
    private CollectionWeatherFragment frag_manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar((MaterialToolbar)(findViewById(R.id.appbar)));

        if (savedInstanceState == null) {
            frag_manager = new CollectionWeatherFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.main_container, frag_manager).commit();
            frag_manager.setTabLayout(findViewById(R.id.forecast_tabs_layout));
            frag_manager.refreshApiData(this);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.app_bar_menu, menu);

        // Setup refresh icon animation
        MenuItem refresh_item = menu.findItem(R.id.action_refresh_data);
        ImageView iv = (ImageView) refresh_item.getActionView();
        Activity main = this;
        iv.setOnClickListener((View v) -> {
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

        return super.onCreateOptionsMenu(menu);
    }
}