package com.example.uaharoni.tourdeplace.view;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;
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

        getPreferenceManager().findPreference(getString(R.string.settings_distance_units_key)).setOnPreferenceChangeListener(this);
        getPreferenceManager().findPreference(getString(R.string.settings_searchRadius_key)).setOnPreferenceChangeListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        if (view != null) {
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
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        Log.d("onPrefsChange","Pref: " + preference.getKey() + " got value " + newValue.toString());

        if(preference.getKey().equals(getString(R.string.settings_searchRadius_key))){
            String newEntryValue = (String) ((ListPreference)preference).getEntries()[((ListPreference)preference).findIndexOfValue(newValue.toString())];
            Log.d("onPrefsChange","Entry name: " + newEntryValue);
            preference.setSummary(newEntryValue);
        }

        if(preference.getKey().equals(getString(R.string.settings_distance_units_key))){
            Log.d("onPrefsChange","New entry name: " + ((ListPreference)preference).getEntries()[((ListPreference)preference).findIndexOfValue(newValue.toString())]);
            String[] newEntries=null;
            ListPreference searchRadius = (ListPreference)getPreferenceManager().findPreference(getText(R.string.settings_searchRadius_key));
            String searchRadiusCurVal = searchRadius.getValue();
            int searchRadiusCurEntry = searchRadius.findIndexOfValue(searchRadiusCurVal);

            if(newValue.toString().equals(getString(R.string.unit_system_mi))){
                newEntries = getResources().getStringArray(R.array.settings_searchRadius_mi_entries);

            } else if(newValue.toString().equals(getString(R.string.unit_system_km))){
                Log.d("onPrefsChange","Modifying the entries list for the searchRadius");
                newEntries = getResources().getStringArray(R.array.settings_searchRadius_km_entries);
            }

            Log.d("onPrefsChange","Modifying the entries list for the searchRadius");
            searchRadius.setEntries(newEntries);
            String searchRadiusNewEntry = (String) searchRadius.getEntries()[searchRadiusCurEntry];
            searchRadius.setSummary(searchRadiusNewEntry);

            }
            return true;
        }
    }
