package com.example.uaharoni.tourdeplace.controller;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.uaharoni.tourdeplace.R;
import com.example.uaharoni.tourdeplace.model.Place;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Locale;

public class SearchGplace extends IntentService {

    private static final String TAG = "SearchGplace";

    //sample: https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=-33.8670522,151.1957362&radius=500&type=restaurant&name=cruise&key=YOUR_API_KEY

    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_FOO = "com.example.uaharoni.tourdeplace.view.action.FOO";
    private static final String ACTION_BAZ = "com.example.uaharoni.tourdeplace.view.action.BAZ";

    // TODO: Rename parameters
    private static final String EXTRA_PARAM1 = "com.example.uaharoni.tourdeplace.view.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "com.example.uaharoni.tourdeplace.view.extra.PARAM2";

    public SearchGplace() {
        super("SearchGplace");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionFoo(Context context, String param1, String param2) {
        Intent intent = new Intent(context, SearchGplace.class);
        intent.setAction(ACTION_FOO);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionBaz(Context context, String param1, String param2) {
        Intent intent = new Intent(context, SearchGplace.class);
        intent.setAction(ACTION_BAZ);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "Service Started.");
        if (intent != null) {
            final String searchTerm = intent.getStringExtra(getString(R.string.search_service_intent_query_extra));
            double[] locArray = intent.getDoubleArrayExtra(getString(R.string.search_service_intent_location_extra));
            Location loc = new Location(getString(R.string.search_service_location_name));
            loc.setLatitude(locArray[0]);
            loc.setLongitude(locArray[1]);
            Log.d(TAG,"Search term: " + searchTerm + ", Location: " + loc.toString());
            updateServiceStatus(getString(R.string.search_service_status_RUNNING));
            //TODO: Display progressBar in status 0
            try{
                Log.d(TAG,"Running the getPlacesList with keyword: " + searchTerm);
                getPlacesList(searchTerm);
            } catch (Exception e){
                Log.d(TAG,"Error in getPlaces. " + e.getMessage());
                updateServiceStatus(getString(R.string.search_service_status_ERROR));
            }

            /*
            final String action = intent.getAction();
            if (ACTION_FOO.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionFoo(param1, param2);
            } else if (ACTION_BAZ.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionBaz(param1, param2);
            }
            */
        }
        Log.d(TAG,"Service Finished!"); // No need to call stopSelf()
    }
    private void updateServiceStatus(String status){
        Log.d(TAG,"Creating Intent for Status " + status);
        Intent localIntent = new Intent(getString(R.string.search_service_custom_intent_action));
        localIntent.putExtra(getString(R.string.search_service_custom_intent_status),status);
        Log.d(TAG,"Broadcasting the intent with status " + status);
        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
    }
    protected void getPlacesList(String query){
        ArrayList<Place> placeArrayList = new ArrayList<>();
        //TODO: populate ArrayList with GooglePlaces list

        SearchResultsTBL searchDbHelper = new SearchResultsTBL(this);
        for (int i = 0; i < placeArrayList.size() ; i++) {
            searchDbHelper.insertPlace(placeArrayList.get(i));
        }
        updateServiceStatus(getString(R.string.search_service_status_FINISHED));
        //TODO: Hide progressBar in activity when service finished
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFoo(String param1, String param2) {
        // TODO: Handle action Foo
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(String param1, String param2) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private Uri buildUrl(LatLng location, int searchRadius, String term) {
        final String JSON_RESPNOSE = "status";
        final String JSON_RESPONSE_TYPE_TRUE = "OK";
        final String JSON_SEARCH_ARRAY = "results";
        final String JSON_PLACE_NAME = "name";
        final String JSON_PLACE_ADDRESS_NAME = "vicinity";

        Uri.Builder urlBuilder = new Uri.Builder().scheme("https")
                .authority("maps.googleapis.com")
                .appendPath("maps")
                .appendPath("api")
                .appendPath("place")
                .appendPath("nearbysearch")
                .appendPath("json")
                .appendQueryParameter("language", Locale.getDefault().getLanguage())
                .appendQueryParameter("radius", String.valueOf(searchRadius))
                .appendQueryParameter("location", String.valueOf(location.latitude) + "," + String.valueOf(location.longitude))
                .appendQueryParameter("keyword", (term == null) ? "" : term)
                .appendQueryParameter("key", getString(R.string.google_maps_key))
//                .appendQueryParameter("pagetoken", (pagetoken == null) ? "" : pagetoken)
                ;

        Log.i("buildUrl", "Loading URI:" + urlBuilder.build().toString());
        return urlBuilder.build();
    }
}
