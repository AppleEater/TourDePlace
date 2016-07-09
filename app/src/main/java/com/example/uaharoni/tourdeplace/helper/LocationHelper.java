package com.example.uaharoni.tourdeplace.helper;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.LocationManager;
import android.provider.Settings;
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
    public boolean isLocationEnabled() {

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }
    public void showLocationSettingsAlert() {
        //TODO: switch toDialogFragment
        final AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle(context.getString(R.string.alert_dialog_title))
                .setCancelable(true)
                .setMessage(context.getString(R.string.alert_dialog_text))
                .setPositiveButton(context.getString(R.string.alert_dialog_positive), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        context.startActivity(myIntent);
                        //TODO: check why the dialog is not dismissed when clicking positiveButton

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

}
