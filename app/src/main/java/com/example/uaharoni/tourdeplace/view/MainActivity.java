package com.example.uaharoni.tourdeplace.view;

import android.Manifest;
import android.app.ActionBar;
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
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import com.example.uaharoni.tourdeplace.R;
import com.example.uaharoni.tourdeplace.controller.ViewPagerAdapter;
import com.example.uaharoni.tourdeplace.helper.LocationHelper;

public class MainActivity extends AppCompatActivity implements LocationListener, SearchView.OnQueryTextListener, RecyclerView.OnItemTouchListener {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    ViewPager viewPager;
    private SearchView searchView;
    int searchFragId,mapFragId,favFragId;


    private SnackBarReceiver snackBarMessageReceiver;

    public static LocationManager locationManager;
    public static LocationHelper locationHelper;
    public static SharedPreferences sharedPreferences;
    private String searchTerm = null;
    private LocationProvider locProvLow,locProvHigh,locProvPassive;



    private final long MIN_TIME_ms = 10000L;
    private final float MIN_DISTANCE_m = 3f;
    public static Location currentLocation = null;
    private static final int LOCATION_REQUEST_CODE = 1;
    private static final int INTERNET_REQUEST_CODE = 2;


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
        Log.d("onResume-Main","onResume started.");
        LocalBroadcastManager.getInstance(this).registerReceiver(snackBarMessageReceiver,new IntentFilter(getString(R.string.power_receiver_custom_intent_action)));

        if(currentLocation == null) {
            Log.d("onResume-Main", "No current location. Using from Preferences");
            String latPref = sharedPreferences.getString(getString(R.string.settings_last_location_latitude), "31.7767189");
            String longtPref = sharedPreferences.getString(getString(R.string.settings_last_location_longitude), "35.2323145");
            currentLocation = new Location(getString(R.string.search_service_location_name));
            currentLocation.setLatitude(Double.parseDouble(latPref));
            currentLocation.setLongitude(Double.parseDouble(longtPref));
            Log.d("onResume-Main", "CurrentLocation: " + currentLocation.toString());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("onPause","onPause started.");
        Log.d("onPause","Removing receivers");
        LocalBroadcastManager.getInstance(this).unregisterReceiver(snackBarMessageReceiver);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.removeUpdates(this);
        }
        if(currentLocation != null){
            Log.d("onPause","Saving last location to Prefs: ");
            sharedPreferences.edit()
                    .putString(getString(R.string.settings_last_location_latitude),String.valueOf(currentLocation.getLatitude()))
                    .putString(getString(R.string.settings_last_location_longitude),String.valueOf(currentLocation.getLongitude()))
                    .apply();
        }
    }

    private void initReceivers(){
        snackBarMessageReceiver = new SnackBarReceiver();
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
    }
    private void initToolBar(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }
    private void initTabs() {

        viewPager = (ViewPager) findViewById(R.id.fragment_container);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());;

        Log.d("initTabs","Adding Fragments");
        searchFragId  = viewPagerAdapter.addFragment(new SearchFragment(),getString(R.string.tab_search));
        mapFragId = viewPagerAdapter.addFragment(new MapFragment(),getString(R.string.tab_map));
        favFragId  = viewPagerAdapter.addFragment(new dummyFragment(),getString(R.string.tab_favorites));

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

        MenuItem searchAny = menu.findItem(R.id .action_search_any);

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
                searchGooglePlaces();
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
        }
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
    @Override
    public boolean onQueryTextSubmit(String query) {
        searchTerm = query.trim();
        searchView.clearFocus();
        Log.d("onQuerySubmit","Query string: " + query);
        viewPager.setCurrentItem(0);
        searchGooglePlaces();


        return true;
    }
    private void searchGooglePlaces(){
        long searchRadiusM = 0;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            getLocationPermissions();
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            Log.d("searchGooglePlaces","No permission to access the network. Requesting permissions");
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.INTERNET},INTERNET_REQUEST_CODE);
        } else {
            Log.d("searchGooglePlaces","Internet permissions exist. Getting locationUpdates");
            Location currentLoc =  getLocationUpdates();
            Log.d("searchGooglePlaces","Returned to searchGooglePlace. Getting info from Preferences");
            String searchRadius = sharedPreferences.getString(getString(R.string.settings_searchRadius_key),getString(R.string.settings_searchRadius_value_500));
            searchRadiusM = locationHelper.getRadiusinM(searchRadius);
            Intent serviceSearch = new Intent(this,com.example.uaharoni.tourdeplace.controller.SearchGplace.class);
            serviceSearch.putExtra(getString(R.string.search_service_intent_query_extra),searchTerm);
            serviceSearch.putExtra(getString(R.string.search_service_intent_search_radius_m_extra), Long.toString(searchRadiusM));
            serviceSearch.putExtra(getString(R.string.search_service_intent_location_name_extra),currentLocation.getProvider());
            serviceSearch.putExtra(getString(R.string.search_service_intent_location_extra),new double[] {currentLoc.getLatitude(),currentLoc.getLongitude()});
            Log.d("searchGooglePlaces","Sending location: " + currentLoc.toString());
            Log.d("searchGooglePlaces","We have permissions. Starting the search service");
            startService(serviceSearch);
        }
    }
    private Location getLocationUpdates() {
        Location tempLocation = null;
        Location lastKnownLocation = null;
        Log.d("getLocation", "Obtaining providers");
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.d("getLocation", "Getting provider Passive");
            locProvPassive =
                    locationManager.getProvider(LocationManager.PASSIVE_PROVIDER);
            Log.d("getLocation", "Getting provider Low");
            locProvLow =
                    locationManager.getProvider(locationManager.getBestProvider(LocationHelper.createCoarseCriteria(), true));
            Log.d("getLocation", "Getting provider High");
            locProvHigh =
                    locationManager.getProvider(locationManager.getBestProvider(LocationHelper.createFineCriteria(), true));
            Log.d("getLocation", "Checking if providers exist...");
            if (locProvHigh == null && locProvLow == null && locProvPassive == null) {
                Log.d("getLocation", "No providers exist. Alerting");
                locationHelper.showLocationSettingsAlert();
            } else {
                Log.d("getLocation", "High access providers exist. Getting currentLocation");
                locationManager.requestLocationUpdates(locProvHigh.getName(), MIN_TIME_ms, MIN_DISTANCE_m, this);
                lastKnownLocation = locationManager.getLastKnownLocation(locProvPassive.getName());
            }
        }
        if(lastKnownLocation != null){
            tempLocation = lastKnownLocation;
        } else {
            Log.d("getLocation","Using currentLocation from Preferences.");
            tempLocation = currentLocation;
        }
        Log.d("getLocationUpdates","Returned location: " + tempLocation.toString());
        return tempLocation;
    }

    private void getLocationPermissions(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)){
            Log.d("getLocPermsns","Explain to the user why we need to get location");
            Snackbar.make(findViewById(R.id.main_content),getString(R.string.snackbar_message_location_permissions_needed),Snackbar.LENGTH_INDEFINITE)
                    .setAction(getString(R.string.snackbar_action_ok), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Log.d("getLocPermsns","Requesting permissions after Snackbar message");
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},LOCATION_REQUEST_CODE);
                            }
                        }
                    })
                    .show();
        } else {
            Log.d("getLocPermsns","Requesting permissions after checking for rationale");
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},LOCATION_REQUEST_CODE);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull final String[] permissions, @NonNull int[] grantResults) {
        Log.d("RequestPerms", "Got grantResult " + grantResults[0] + " for permission " + permissions[0]);
        switch (requestCode) {
            case LOCATION_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("RequestPerms", "Permission granted. Running");
                    searchGooglePlaces();
                } else {
                    Log.d("RequestPerms", "Location Permissions denied on API>=23");
                    Snackbar.make(findViewById(R.id.main_content),getString(R.string.snackbar_message_no_location_permissions),Snackbar.LENGTH_LONG).show();
                }
                break;
            case INTERNET_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("RequestPerms","Internet permission granted.");
                    searchGooglePlaces();
                } else {
                    Log.d("RequestPerms","Internet permissions denied");
                }
                break;
            default:
                Log.d("RequestPerms", "No requestCode. Falling back");
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }
    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d("onTouchEvent","Got event " + event.toString());
        if(event.getAction() == MotionEvent.ACTION_DOWN) {
            toggleActionBar();
        }
        return true;
    }

    private void toggleActionBar() {
        ActionBar actionBar = getActionBar();

        if(actionBar != null) {
            if(actionBar.isShowing()) {
                Log.d("toggleActionBar","Hiding the actionBar");
                actionBar.hide();
            }
            else {
                Log.d("toggleActionBar","Showing the actionBar");
                actionBar.show();
            }
        }
    }
}
