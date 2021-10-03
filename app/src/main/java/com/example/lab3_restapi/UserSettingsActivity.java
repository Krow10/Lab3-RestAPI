package com.example.lab3_restapi;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.lab3_restapi.fragments.UserSettingsFragment;

public class UserSettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_settings_layout);

        getSupportFragmentManager().beginTransaction().replace(R.id.user_settings_container, new UserSettingsFragment()).commit();

        findViewById(R.id.user_settings_toolbar).setOnClickListener(v -> finish());
    }
}
