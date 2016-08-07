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
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.uaharoni.tourdeplace.R;
import com.example.uaharoni.tourdeplace.controller.OnItemClickListener;
import com.example.uaharoni.tourdeplace.controller.OnItemLongClickListener;
import com.example.uaharoni.tourdeplace.controller.ViewPagerAdapter;
import com.example.uaharoni.tourdeplace.helper.LocationHelper;
import com.example.uaharoni.tourdeplace.model.Place;

public class MainActivity extends AppCompatActivity
            implements LocationListener, SearchView.OnQueryTextListener,OnItemClickListener,OnItemLongClickListener {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    ViewPager viewPager;
    private SearchView searchView;
    int searchFragId=-1,mapFragId=-1,favFragId=-1;


    private SnackBarReceiver snackBarMessageReceiver;

    public static LocationManager locationManager;
    public static LocationHelper locationHelper;
    public static SharedPreferences sharedPreferences;
    private String searchTerm = null;
    private LocationProvider locProvLow,locProvHigh,locProvPassive;


    private final long MIN_TIME_ms = 5000L;
    private final float MIN_DISTANCE_m = 10f;
    public static Location currentLocation = null;
    private static final int LOCATION_REQUEST_CODE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationHelper = new LocationHelper(this,locationManager);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        initReceivers();
        initToolBar();
        initTabs();

        Log.d("onCreate-Main","Finished onCreate");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("onResume-Main","Registering snackBar receiver.");
        LocalBroadcastManager.getInstance(this).registerReceiver(snackBarMessageReceiver,new IntentFilter(getString(R.string.power_receiver_custom_intent_action)));

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            Log.d("onResume-Main","Permissions approved. Checking for providers...");
            if (getProviders()) {
                Log.d("onResume-Main", "Providers exist. Getting currentLocation...");
                if (locProvPassive != null) {
                    Log.d("onResume-Main", "Requesting location from Passive Providers");
                    locationManager.requestLocationUpdates(locProvPassive.getName(), MIN_TIME_ms, MIN_DISTANCE_m, this);
                }
                if (locProvLow != null) {
                    Log.d("onResume-Main", "Requesting location from Low Providers");
                    locationManager.requestLocationUpdates(locProvLow.getName(), MIN_TIME_ms, MIN_DISTANCE_m, this);
                }
            }
            viewPager.setCurrentItem(searchFragId);
        } else {
            viewPager.setCurrentItem(mapFragId);
        }
        if(currentLocation == null){
            Log.d("onResume-Main","No real location from the providers. We'll fake it from the preferences...");
            String latPref = sharedPreferences.getString(getString(R.string.settings_last_location_latitude), "31.7767189");
            String longtPref = sharedPreferences.getString(getString(R.string.settings_last_location_longitude), "35.2323145");
            currentLocation = new Location(getString(R.string.search_service_location_name));
            currentLocation.setLatitude(Double.parseDouble(latPref));
            currentLocation.setLongitude(Double.parseDouble(longtPref));
            Log.d("onResume-Main", "CurrentLocation from preferences: " + currentLocation.toString());
        }

        Log.d("onResume-Main", "Finished onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("onPause-Main","Removing receivers");
        LocalBroadcastManager.getInstance(this).unregisterReceiver(snackBarMessageReceiver);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.d("onPause-Main","Removing Location updates");
            locationManager.removeUpdates(this);
        }
        if(currentLocation != null){
            Log.d("onPause-Main","Saving last location to Prefs.");
            sharedPreferences.edit()
                    .putString(getString(R.string.settings_last_location_latitude),String.valueOf(currentLocation.getLatitude()))
                    .putString(getString(R.string.settings_last_location_longitude),String.valueOf(currentLocation.getLongitude()))
                    .apply();
        }
    }

    private void initReceivers(){
        snackBarMessageReceiver = new SnackBarReceiver();
    }

    public void displayPlaceOnMap(Place place) {
        Log.d("onDisplayPlaceOnMap","Got Place " + place.getName());
        Fragment mapFrag = ((ViewPagerAdapter) viewPager.getAdapter()).getItem(mapFragId);
        if (mapFrag != null) {
            Log.d("onDisplayPlaceOnMap", "Display marker in the map fragment");
            ((MapFragment) mapFrag).addPlaceMarker(place);
            viewPager.setCurrentItem(mapFragId);
        }
    }

    @Override
    public void onAddToFavorites(Place place) {
        FavFragment favFrag = (FavFragment) ((ViewPagerAdapter)viewPager.getAdapter()).getItem(favFragId);
        if(favFrag != null){
            Log.d("onAddToFavorites","Adding place " + place.getName() + " in Fav Fragment");
            favFrag.addPlace(place);
            Snackbar.make(findViewById(R.id.main_content),getString(R.string.snackbar_message_added_to_favorites),Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSharePlace(Place place) {
        Log.d("onSharePlace-Main","Sharing place " + place.getName());

        Uri gmmIntentUri = Uri.parse("geo:"+place.getAddress().getAddLat()+","+ place.getAddress().getAddLong()+"?q="+Uri.encode(place.getAddress().getName()));
        Intent intent = new Intent(Intent.ACTION_VIEW,gmmIntentUri);
        intent.setPackage("com.google.android.apps.maps");
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(Intent.createChooser(intent, "Share via"));
        }
    }

    @Override
    public void onRemoveFromFavorites(Place place) {
        //Not used
    }

    @Override
    public void onItemClick(Place place) {
        displayPlaceOnMap(place);
    }


    private class SnackBarReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent !=null){
                Log.d("onReceive","Got intent " + intent.toString());
                if(intent.getAction() .equals(getString(R.string.power_receiver_custom_intent_action))){
                    String message = intent.getStringExtra(getString(R.string.snackbar_message_custom_intent_extra_text));
                    if(message != null){
                        Log.d("onReceive","Got message " + message);
                        Snackbar.make(findViewById(R.id.main_content),message,Snackbar.LENGTH_LONG).show();
                    }
                }
            }
        }
    }   //End of class SnackBarReceiver

    private void initToolBar(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }
    private void initTabs() {

        viewPager = (ViewPager) findViewById(R.id.fragment_container);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        Log.d("initTabs","Adding Fragments");
        favFragId  = viewPagerAdapter.addFragment(new FavFragment(),getString(R.string.tab_favorites));
        mapFragId = viewPagerAdapter.addFragment(new MapFragment(),getString(R.string.tab_map));
        searchFragId  = viewPagerAdapter.addFragment(new SearchFragment(),getString(R.string.tab_search));


        Log.d("initTabs","Connecting the tabs to the Adapter");
        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(viewPagerAdapter);

        Log.d("initTabs","Creating tabLayout");
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        tabLayout.setSelectedTabIndicatorHeight(10);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setupWithViewPager(viewPager);

        Log.d("initTabs","Adding icons to the tab");
        try{
            tabLayout.getTabAt(mapFragId).setIcon(android.R.drawable.ic_dialog_map);
            tabLayout.getTabAt(searchFragId).setIcon(android.R.drawable.ic_menu_directions);
            tabLayout.getTabAt(favFragId).setIcon(android.R.drawable.ic_menu_myplaces);
        } catch (Exception e){
            Log.e("initTabs","Error adding icons. " + e.getMessage());
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d("onCreateMenu","Loading menu");
        getMenuInflater().inflate(R.menu.options_menu, menu);

        MenuItem searchBusiness = menu.findItem(R.id .action_search_business);
        searchView = (SearchView) MenuItemCompat.getActionView(searchBusiness);
        searchView.setIconifiedByDefault(false);
        searchView.setInputType(InputType.TYPE_CLASS_TEXT);
        searchView.setQueryHint(getString(R.string.search_hint));
        searchView.setOnQueryTextListener(this);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d("onItemSelected","Selected Menu Option " + item.getTitle());
        switch (item.getItemId()){
            case R.id.action_settings:
                getFragmentManager().beginTransaction()
                        .replace(R.id.settingsContainer, new SettingsFragment())
                        .addToBackStack(getString(R.string.action_settings))
                        .commit();
                return true;
            case R.id.action_search_any:
                setLocationRequest();
                viewPager.setCurrentItem(searchFragId);
                return true;
            case R.id.action_feedback:
                return true;
            default:
                Log.d("onItemSelected","Can't find action. going to superclass");
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("onLocChange","Got Location " + location.toString());
        currentLocation = location;

        Fragment mapFrag = ((ViewPagerAdapter) viewPager.getAdapter()).getItem(mapFragId);
        if (mapFrag != null) {
            Log.d("onLocChanged","Updating the location in the map fragment");
            ((MapFragment)mapFrag).setCurrentLocation(currentLocation);
        }
        SearchFragment searchFrag = (SearchFragment)((ViewPagerAdapter)viewPager.getAdapter()).getItem(searchFragId);
        if(searchFrag != null){
            Log.d("onLocChanged","Updating the location in the search fragment");
            searchFrag.refreshAdapter();
        }
        FavFragment favFrag = (FavFragment)((ViewPagerAdapter)viewPager.getAdapter()).getItem(favFragId);
        if(favFrag != null){
            Log.d("onLocChanged","Updating the location in the favorites fragment");
            favFrag.refreshAdapter();
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
        Log.d("onStatusChanged","Provider " + s + " changed status to " + bundle.toString());
    }

    @Override
    public void onProviderEnabled(String s) {
    }

    @Override
    public void onProviderDisabled(String s) {
    }
    @Override
    public boolean onQueryTextSubmit(String query) {
        searchTerm = query.trim();
        searchView.clearFocus();
        Log.d("onQuerySubmit","Query string: " + query);
        setLocationRequest();
        viewPager.setCurrentItem(searchFragId);

        return true;
    }
    private void searchPlaces(){
        long searchRadiusM = 0;
        if(currentLocation != null){
            Log.d("searchPlaces","We have a real location from the providers");
        } else {
            Log.w("searchPlaces","No location from the providers");
            return;
        }
        String searchRadius = sharedPreferences.getString(getString(R.string.settings_searchRadius_key),getString(R.string.settings_searchRadius_value_500));
        searchRadiusM = locationHelper.getRadiusinM(searchRadius);
        Intent serviceSearch = new Intent(this,com.example.uaharoni.tourdeplace.controller.SearchGplace.class);
        serviceSearch.putExtra(getString(R.string.search_service_intent_query_extra),searchTerm);
        serviceSearch.putExtra(getString(R.string.search_service_intent_search_radius_m_extra), Long.toString(searchRadiusM));
        serviceSearch.putExtra(getString(R.string.search_service_intent_location_name_extra),currentLocation.getProvider());
        serviceSearch.putExtra(getString(R.string.search_service_intent_location_extra),new double[] {currentLocation.getLatitude(),currentLocation.getLongitude()});
        Log.d("searchGooglePlaces","Sending location: " + currentLocation.toString());
        Log.d("searchGooglePlaces","Starting the search service");
        startService(serviceSearch);
    }

    private void setLocationRequest() {
        Location lastKnownLocation = null;
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                ||
                (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED)
                )
        {
            Log.d("setLocationRequest","No Permissions for location or internet. Requesting...");
            getPermissions();
        } else {
            Log.d("setLocationRequest", "We have all the permissions needed. Requesting providers...");
            if (!getProviders()) {
                Log.e("setLocationRequest", "No location providers are enabled...");
                Snackbar.make(findViewById(R.id.main_content),"Location is disabled",Snackbar.LENGTH_LONG);
                return;
            }
            Log.d("setLocationRequest", "Providers exist. Getting currentLocation");
            if (locProvLow != null) {
                Log.d("setLocationRequest", "Requesting location from lowProvider");
                locationManager.requestLocationUpdates(locProvLow.getName(), MIN_TIME_ms, MIN_DISTANCE_m, this);
                lastKnownLocation = locationManager.getLastKnownLocation(locProvPassive.getName());
                if (lastKnownLocation != null) {
                    Log.d("setLocationRequest", "Got lastKnowLocation from lowProvider. Running searchPlaces");
                    currentLocation = lastKnownLocation;
                    searchPlaces();
                } else {
                    Log.d("setLocationRequest", "No last known location from lowProvider");
                }
            } else if (locProvPassive != null) {
                Log.d("setLocationRequest", "Requesting location from passiveProvider");
                locationManager.requestLocationUpdates(locProvLow.getName(), MIN_TIME_ms, MIN_DISTANCE_m, this);
                lastKnownLocation = locationManager.getLastKnownLocation(locProvPassive.getName());
                if (lastKnownLocation != null) {
                    Log.d("setLocationRequest", "Got last known location from locProvPassive. Running searchPlaces");
                    currentLocation = lastKnownLocation;
                    searchPlaces();
                } else {
                    Log.d("setLocationRequest", "No last known location from locProvPassive");
                    Snackbar.make(findViewById(R.id.main_content),"Location unknown....",Snackbar.LENGTH_LONG);
                }
            }
        }
    }
    private boolean getProviders() {
        boolean providerSuccess = true;
        Log.d("getProviders", "Getting provider Passive");
        locProvPassive =
                locationManager.getProvider(LocationManager.PASSIVE_PROVIDER);
        Log.d("getProviders", "Getting provider Low");
        locProvLow =
                locationManager.getProvider(locationManager.getBestProvider(LocationHelper.createCoarseCriteria(), true));
        Log.d("getProviders", "Checking if providers exist...");
        if (locProvHigh == null && locProvLow == null && locProvPassive == null) {
            Log.d("getLocation", "No providers exist. Alerting");
            locationHelper.showLocationSettingsAlert();
            providerSuccess = false;
        }
        return providerSuccess;
    }
    private void getPermissions(){
        String []permissions =new String[] {Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.INTERNET};
        if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)
                ||
                ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.INTERNET)
                ){
            Log.d("getPermissions","Explain to the user why we need to get these permissions");
            Snackbar.make(findViewById(R.id.main_content),getString(R.string.snackbar_message_location_permissions_needed),Snackbar.LENGTH_LONG)
                    .show();
        }
        ActivityCompat.requestPermissions(this,permissions,LOCATION_REQUEST_CODE);
        }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        boolean permissionDenied = false;
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case LOCATION_REQUEST_CODE:
                if(grantResults.length==0){
                    Log.d("onRqustPermssonsReslt","Location permissions request was cancelled");
                    permissionDenied = true;
                } else {
                    int i = 0;
                    while (i < grantResults.length && !permissionDenied) {
                        permissionDenied = (grantResults[i] != PackageManager.PERMISSION_GRANTED);
                        i++;
                    }
                    if (permissionDenied) {
                        Log.d("onRqustPermssonsReslt", "Permission " + permissions[i - 1] + " was denied!");
                    }
                }
                if(permissionDenied){
                        Log.e("onRqustPermssonsReslt","No permissions granted. Can't continue.");
                    Snackbar.make(findViewById(R.id.main_content),R.string.snackbar_message_location_permissions_needed,Snackbar.LENGTH_LONG).show();
                } else {
                    Log.d("onRqustPermssonsReslt","All permissions are good. Continue");
                    searchPlaces();
                }
                break;
        }
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

}
