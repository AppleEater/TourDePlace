package com.example.uaharoni.tourdeplace.controller;

import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.uaharoni.tourdeplace.R;
import com.example.uaharoni.tourdeplace.model.Place;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;

public class SearchGplace extends IntentService {

    private static final String TAG = "SearchGplace";

    public SearchGplace() {
        super("SearchGplace");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "Service Started.");
        if (intent != null) {
            //TODO: Check if the values order is relevant
            final String searchTerm = intent.getStringExtra(getString(R.string.search_service_intent_query_extra));
            final String searchRadiusM = intent.getStringExtra(getString(R.string.search_service_intent_search_radius_m_extra));
            String locationName = intent.getStringExtra(getString(R.string.search_service_intent_location_name_extra));
            double[] locArray = intent.getDoubleArrayExtra(getString(R.string.search_service_intent_location_extra));
            Location targetLocation = new Location(locationName);
            targetLocation.setLatitude(locArray[0]);
            targetLocation.setLongitude(locArray[1]);
            Log.d(TAG,"Search term received: " + searchTerm + ", Location: " + targetLocation.toString() + ", radius: " + searchRadiusM);
            updateServiceStatus(getString(R.string.search_service_status_RUNNING));
            //TODO: Display progressBar in status 0
            try{
                Log.d(TAG,"Running the getPlacesList with keyword: " + searchTerm);
                getPlacesList(searchTerm,searchRadiusM,targetLocation);
            } catch (Exception e){
                Log.d(TAG,"Error in getPlaces. " + e.getMessage());
                updateServiceStatus(getString(R.string.search_service_status_ERROR));
            }
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
    protected void getPlacesList(String query,String radius,Location currentLocation){
        ArrayList<Place> placeArrayList = new ArrayList<>();
        //TODO: populate ArrayList with GooglePlaces list
        String gplaceUrlString = buildUrl(currentLocation,radius,query).toString();
        try {
            URL gplaceUrl = new URL(gplaceUrlString);
            HttpURLConnection urlConnection = (HttpURLConnection) gplaceUrl.openConnection();


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        SearchResultsTBL searchDbHelper = new SearchResultsTBL(this);
        for (int i = 0; i < placeArrayList.size() ; i++) {
            searchDbHelper.insertPlace(placeArrayList.get(i));
        }
        updateServiceStatus(getString(R.string.search_service_status_FINISHED));
    }

    private Uri buildUrl(Location location, String searchRadius, String term) {
        //sample: https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=-33.8670522,151.1957362&radius=500&type=restaurant&name=cruise&key=YOUR_API_KEY

        final String JSON_RESPNOSE = "status";
        final String JSON_RESPONSE_TYPE_TRUE = "OK";
        final String JSON_SEARCH_ARRAY = "results";
        final String JSON_PLACE_NAME = "name";
        final String JSON_PLACE_ADDRESS_NAME = "vicinity";


        String tempLocation = Double.toString(location.getLatitude()) + "," + Double.toString(location.getLongitude());
        Log.d("buildUrl", "Location string: " + tempLocation);

        Uri.Builder urlBuilder = new Uri.Builder().scheme("https")
                .authority("maps.googleapis.com")
                .appendPath("maps")
                .appendPath("api")
                .appendPath("place")
                .appendPath("nearbysearch")
                .appendPath("json")
                .appendQueryParameter("location", tempLocation)
                .appendQueryParameter("radius", searchRadius)
                .appendQueryParameter("language", Locale.getDefault().getLanguage())
                .appendQueryParameter("keyword", (term == null) ? "" : term)
                .appendQueryParameter("key", getString(R.string.google_maps_key))
//                .appendQueryParameter("pagetoken", (pagetoken == null) ? "" : pagetoken)
                ;

        Log.i("buildUrl", "Loading URI:" + urlBuilder.build().toString());
        return urlBuilder.build();
    }
}
