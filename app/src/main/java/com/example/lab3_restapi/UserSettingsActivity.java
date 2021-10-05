package com.example.lab3_restapi;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.lab3_restapi.fragments.UserSettingsFragment;

/**
 * This class is used to hold the view for the settings to be displayed.
 * <p>It is designated as a child activity of the {@link MainActivity} and returns back to it when destroyed.</p>
 */
public class UserSettingsActivity extends AppCompatActivity {
    /**
     * Inherited listener used to initialize the view and navigation arrow action.
     * @param savedInstanceState unused
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_settings_layout);

        getSupportFragmentManager().beginTransaction().replace(R.id.user_settings_container, new UserSettingsFragment()).commit();

        findViewById(R.id.user_settings_toolbar).setOnClickListener(v -> finish());
    }
}
