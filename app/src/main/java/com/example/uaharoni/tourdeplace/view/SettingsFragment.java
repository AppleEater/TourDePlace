package com.example.uaharoni.tourdeplace.view;

import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.uaharoni.tourdeplace.R;

public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {

    private static final String TAG = SettingsFragment.class.getSimpleName();
    private ListPreference distanceUnit, searchRadius;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        //TODO: Test Preference Headers

        distanceUnit = (ListPreference)getPreferenceManager().findPreference(getString(R.string.settings_distance_units_key));
        searchRadius = (ListPreference)getPreferenceManager().findPreference(getString(R.string.settings_searchRadius_key));


        /*
        getPreferenceManager().findPreference(getString(R.string.settings_distance_units_key)).setOnPreferenceChangeListener(this);
        getPreferenceManager().findPreference(getString(R.string.settings_searchRadius_key)).setOnPreferenceChangeListener(this);
        */
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
        distanceUnit.setOnPreferenceChangeListener(this);
        searchRadius.setOnPreferenceChangeListener(this);

        if(distanceUnit.getValue().equals(getString(R.string.unit_system_mi))){
            searchRadius.setEntries(getResources().getStringArray(R.array.settings_searchRadius_mi_entries));
            searchRadius.setEntryValues(getResources().getStringArray(R.array.settings_searchRadius_values));
        }
        if(distanceUnit.getValue().equals(getString(R.string.unit_system_km))){
            searchRadius.setEntries(getResources().getStringArray(R.array.settings_searchRadius_km_entries));
            searchRadius.setEntryValues(getResources().getStringArray(R.array.settings_searchRadius_values));
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
            ((ListPreference) preference).setEntryValues(getResources().getStringArray(R.array.settings_searchRadius_values));
        }

        if(preference.getKey().equals(getString(R.string.settings_distance_units_key))){
            Log.d("onPrefsChange","New distanceUnit entry name: " + ((ListPreference)preference).getEntries()[((ListPreference)preference).findIndexOfValue(newValue.toString())]);
            String[] newEntries=null;
            ListPreference searchRadius = (ListPreference)getPreferenceManager().findPreference(getText(R.string.settings_searchRadius_key));
            String searchRadiusCurVal = searchRadius.getValue();
            Log.d("onPrefsChange","Current radius value is " + searchRadiusCurVal);
            int searchRadiusCurEntry = searchRadius.findIndexOfValue(searchRadiusCurVal);
            Log.d("onPrefsChange","Current radius entry is " + searchRadiusCurEntry);


            if(newValue.toString().equals(getString(R.string.unit_system_mi))){
                Log.d("onPrefsChange","Pref has been changed to " + getString(R.string.unit_system_mi));
                Log.d("onPrefsChange","Modifying the entries list for the MI distance Unit");
                newEntries = getResources().getStringArray(R.array.settings_searchRadius_mi_entries);

            } else if(newValue.toString().equals(getString(R.string.unit_system_km))){
                Log.d("onPrefsChange","Pref has been changed to " + getString(R.string.unit_system_km));
                Log.d("onPrefsChange","Modifying the entries list for the KM distance Unit");
                newEntries = getResources().getStringArray(R.array.settings_searchRadius_km_entries);
            }

            Log.d("onPrefsChange","Modifying the entries list for the searchRadius");
            searchRadius.setEntries(newEntries);
            Log.d("onPrefsChange","Modifying the values list for the searchRadius");
            searchRadius.setEntryValues(getResources().getStringArray(R.array.settings_searchRadius_values));
            String searchRadiusNewEntry = (String) searchRadius.getEntries()[searchRadiusCurEntry];
            Log.d("onPrefsChange","Updating searchRadius summary to " + searchRadiusNewEntry);
            searchRadius.setSummary(searchRadiusNewEntry);

            }
            return true;
        }
    }
