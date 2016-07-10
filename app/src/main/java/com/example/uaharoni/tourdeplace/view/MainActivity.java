package com.example.uaharoni.tourdeplace.view;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;

import com.example.uaharoni.tourdeplace.R;
import com.example.uaharoni.tourdeplace.helper.LocationHelper;
import com.google.android.gms.maps.model.LatLng;

public class MainActivity extends AppCompatActivity implements LocationListener{

    private Toolbar toolbar;

    private SharedPreferences sharedPreferences;
    private BroadcastReceiver snackBarMessageReceiver;
    public static LocationManager locationManager;
    public static LocationHelper locationHelper;
    public LocationProvider locProvLow,locProvHigh,locProvPassive;


    public static final String KEY_UNIT_SYSTEM = "UNIT_SYSTEM";
    public static final String KEY_SEARCH_RADIUS = "SEARCH_RADIUS";
    public static final String KEY_PREF_LAT = "KEY_LAT";
    public static final String KEY_PREF_LONG = "KEY_LNG";

    private LatLng lastLocation = null;
    private final long MIN_TIME_ms = 10000l;
    private final float MIN_DISTANCE_m = 3f;
    private Location lastKnownLocation = null;


    public static final int LOCATION_REQUEST_CODE = 1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationHelper = new LocationHelper(this,locationManager);
        int orientation = getResources().getConfiguration().orientation;
        int screenSize = getResources().getConfiguration().screenWidthDp;
        Log.d("omCreate","Running on screen resolution: " + screenSize + ", orientation: " + orientation);

        initReceivers();
        initToolBar();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // This method always return true on API<23
            Log.d("onCreate-Main","Permissions are granted");
            initLocation();
            getLocationByPriority(locProvPassive);
        } else {
            Log.d("onCreate","No permissions, running on API>=23. Running getLocationPermissions");
            getLocationPermissions();
        }


        Log.d("onCreate-Main","Finished onCreate");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("onResume-Main","onResume started.");
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(snackBarMessageReceiver, new IntentFilter("EVENT_SNACKBAR"));

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.d("onResume","Get lastKnownLocation from PassiveProvider");
            lastKnownLocation = locationManager.getLastKnownLocation(locProvLow.getName());
        }

            if(lastKnownLocation == null){
            Log.d("onResume-Main", "No lastLocation available. using saved info");
            double prefLat = Double.parseDouble(sharedPreferences.getString(KEY_PREF_LAT, "32.0640349"));
            double prefLong = Double.parseDouble(sharedPreferences.getString(KEY_PREF_LONG, "34.7844135"));
            lastLocation = new LatLng(prefLat, prefLong);
        } else {
            lastLocation = new LatLng(lastKnownLocation.getLatitude(),lastKnownLocation.getLongitude());
        }
        Log.d("onResume-Main","Obtained last/Default location: " + lastLocation.toString());
        //TODO: MapFragment.setCurrentLocation(lastLocation);
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(snackBarMessageReceiver);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.removeUpdates(this);
        }
    }

    private void initReceivers(){
        snackBarMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent != null) {
                    String message = intent.getStringExtra("MESSAGE");
                    Log.d("onReceive","Got message " + message);
                    Snackbar.make(findViewById(R.id.main_container),message,Snackbar.LENGTH_LONG).show();
                }
            }
        };  // closing the receiver
    }
    public void initLocation(){
        // Get passive provider
        locProvPassive =
                locationManager.getProvider(LocationManager.PASSIVE_PROVIDER);
        // get low accuracy provider
        locProvLow=
                locationManager.getProvider(locationManager.getBestProvider(LocationHelper.createCoarseCriteria(),true));
        // get high accuracy provider
        locProvHigh=
                locationManager.getProvider(locationManager.getBestProvider(LocationHelper.createFineCriteria(),true));
    }
    public void getLocationByPriority(@NonNull LocationProvider locationProvider){
        // Checking for permissions
            if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
               Log.d("getLocByPrio","Permissions exist for provider " + locationProvider.getName());
               // lastKnownLocation = locationManager.getLastKnownLocation(locationProvider.getName());
                locationManager.requestLocationUpdates(locationProvider.getName(), MIN_TIME_ms, MIN_DISTANCE_m, this);
            }
            else {
                // If we're here - we're on API>=23 and didn't get permissions before
                Log.d("getLocByPrio","No permission. Running getLocationPermissions." );
                getLocationPermissions();
            }
        Log.d("getLoc","Finished getLocationByPriority for provider " + locationProvider.getName());

    }

    private void getLocationPermissions(){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)){
                Log.d("getLocPermisns","Explain to the user why we need to get location");
                Snackbar.make(findViewById(R.id.main_container),getString(R.string.snackbar_message_location_permissions_needed),Snackbar.LENGTH_INDEFINITE)
                        .setAction(getString(R.string.snackbar_action_ok), new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Log.d("onClick-GetLocPerms","Requesting permissions after Snackbar message");
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},LOCATION_REQUEST_CODE);
                                }
                            }
                        })
                        .show();
            } else {
                Log.d("getLocPermisns","Requesting permissions after checking for rationale");
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},LOCATION_REQUEST_CODE);
            }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull final String[] permissions, @NonNull int[] grantResults) {
        Log.d("RequestPerms", "Got grantResult " + grantResults[0] + " for permission " + permissions[0]);
        switch (requestCode) {
            case LOCATION_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("RequestPerms", "Permission granted. Running initLocation with getLocationByPriority(Passive)");
                    initLocation();
                    getLocationByPriority(locProvPassive);
                } else {
                    Log.d("RequestPerms", "Location Permissions denied");
                }
                break;
            default:
                Log.d("RequestPerms", "No requestCode. Falling back");
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }
    }
    private void initToolBar(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onLocationChanged(Location location) {
       //TODO: MapFragment.setCurrentLocation(lastLocation);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
    }

    @Override
    public void onProviderEnabled(String s) {
    }

    @Override
    public void onProviderDisabled(String s) {
    }
}
