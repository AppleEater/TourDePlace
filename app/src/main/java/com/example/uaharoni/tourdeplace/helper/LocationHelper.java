package com.example.uaharoni.tourdeplace.helper;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.LocationManager;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.example.uaharoni.tourdeplace.R;

public class LocationHelper {

    private LocationManager locationManager;
    private Context context;

    public LocationHelper(Context context, LocationManager locationManager) {
        this.locationManager = locationManager;
        this.context = context;
    }
    public void showLocationSettingsAlert() {
        Log.d("showLocationSttngsAlrt","Display alert to user");
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle(context.getString(R.string.alert_dialog_title))
                .setCancelable(true)
                .setMessage(context.getString(R.string.alert_dialog_text))
                .setPositiveButton(context.getString(R.string.alert_dialog_positive), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        context.startActivity(myIntent);
                    }
                })
                .setNegativeButton(context.getString(R.string.alert_dialog_negative), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Log.d("locationSettingsAlert","User cancelled NoLocations alert");
                    }
                });
        dialog.create();
    }
    public String getLocProvider() {
        Criteria locationCriteria = new Criteria();
        locationCriteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);
        locationCriteria.setCostAllowed(true);
        locationCriteria.setSpeedRequired(false);
        locationCriteria.setBearingRequired(false);
        locationCriteria.setAltitudeRequired(false);
        locationCriteria.setPowerRequirement(Criteria.POWER_HIGH);

        String locationProvider = locationManager.getBestProvider(locationCriteria, true);
        if(locationProvider == null) {
            locationProvider =   LocationManager.PASSIVE_PROVIDER;
        }
        Log.d("getLocProvider", "Got locationProvider " + locationProvider);

        return locationProvider;
    }
    /** this criteria will settle for less accuracy, high power, and cost **/
    public static Criteria createCoarseCriteria() {

        Criteria c = new Criteria();
        c.setAccuracy(Criteria.ACCURACY_COARSE);
        c.setHorizontalAccuracy(Criteria.ACCURACY_LOW);
        c.setAltitudeRequired(false);
        c.setBearingRequired(false);
        c.setSpeedRequired(false);
        c.setCostAllowed(true);
        c.setPowerRequirement(Criteria.POWER_HIGH);
        return c;
    }
    /** this criteria needs high accuracy, high power, and cost */
    public static Criteria createFineCriteria() {

        Criteria c = new Criteria();
        c.setAccuracy(Criteria.ACCURACY_FINE);
        c.setHorizontalAccuracy(Criteria.ACCURACY_FINE);
        c.setSpeedRequired(false);
        c.setAltitudeRequired(false);
        c.setBearingRequired(false);
        c.setSpeedRequired(false);
        c.setCostAllowed(true);
        c.setPowerRequirement(Criteria.POWER_HIGH);
        return c;
    }
    public long getRadiusinM(@NonNull String length){
        long lengthMeters = 0;
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String distanceUnit = sp.getString(context.getString(R.string.settings_distance_units_key),context.getString(R.string.unit_system_km));
        if(distanceUnit.equals(context.getString(R.string.unit_system_km))){
            lengthMeters = (long)(Double.parseDouble(length)*1000); //distance in Meters
        } else {
            // Assuming distance is in Miles
            lengthMeters = (long)(Double.parseDouble(length)*1609.344);
        }
        Log.d("getRaiusinM","Converted " + length + " " + distanceUnit + " to " + lengthMeters + " meters");
        return lengthMeters;
    }

}
