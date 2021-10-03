package com.example.lab3_restapi.fragments;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import com.example.lab3_restapi.R;

public class UserSettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.user_settings, rootKey);
    }
}
