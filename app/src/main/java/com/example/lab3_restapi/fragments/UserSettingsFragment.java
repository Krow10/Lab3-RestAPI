package com.example.lab3_restapi.fragments;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import com.example.lab3_restapi.R;

/**
 * This class is used to hold the view for the {@link com.example.lab3_restapi.UserSettingsActivity}.
 */
public class UserSettingsFragment extends PreferenceFragmentCompat {
    /**
     * Inherited listener used to inflate the layout content.
     * @param savedInstanceState unused
     * @param rootKey used to root the view to the <a href="https://developer.android.com/reference/androidx/preference/PreferenceScreen">PreferenceScreen</a>
     */
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.user_settings, rootKey);
    }
}
