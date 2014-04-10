package se.chalmers.agile.activities;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import se.chalmers.agile.R;

public class SettingsFragment extends PreferenceFragment {

    public static SettingsFragment createInstance() {
        SettingsFragment fragment = new SettingsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }

}