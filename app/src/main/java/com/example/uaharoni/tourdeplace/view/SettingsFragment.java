package com.example.uaharoni.tourdeplace.view;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.uaharoni.tourdeplace.R;

public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {

    private static final String TAG = SettingsFragment.class.getSimpleName();

    SharedPreferences sharedPreferences;


    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        //TODO: Test Preference Headers
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        // Add listeners for every object
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        if (view != null) {
            //view.setBackgroundColor(getResources().getColor(android.R.color.white));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                view.setBackgroundColor(getResources().getColor(android.R.color.white,getActivity().getTheme()));
            } else {
                view.setBackgroundColor(getResources().getColor(android.R.color.white));
            }

        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        //sharedPreferences.registerOnSharedPreferenceChangeListener(this);

    }

    @Override
    public void onPause() {
        super.onPause();
        //sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }

/*
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String keyPref) {
        Preference preference = findPreference(keyPref);

        if (preference instanceof ListPreference) {
            ListPreference listPreference = (ListPreference) preference;
            int prefIndex = listPreference.findIndexOfValue(sharedPreferences.getString(keyPref, ""));
            if (prefIndex >= 0) {
                preference.setSummary(listPreference.getEntries()[prefIndex]);
            }
        } else {
            preference.setSummary(sharedPreferences.getString(keyPref, ""));

        }
    }
*/
    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        /*
          if(preference.getKey().equals("edit_text")){
            editTextPreference.setSummary("Your name is: " + newValue);
        }
        else if(preference.getKey().equals("list1")){
            preference.setSummary("You picked " + newValue);
        }
         */
        return true;
    }
}
