package com.example.uaharoni.tourdeplace.controller;

import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.uaharoni.tourdeplace.R;
import com.example.uaharoni.tourdeplace.model.Place;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
    protected void getPlacesList(@Nullable String query,@NonNull String radius,@NonNull Location currentLocation){
        Log.d("getPlacesList","Starting...");
        ArrayList<Place> placeArrayList = new ArrayList<>();
        //TODO: populate ArrayList with GooglePlaces list
        try {
            URL gplaceUrl = new URL(buildURL(currentLocation,radius,query));
            HttpURLConnection connection = getConnection(gplaceUrl);
            JSONObject jsResponse = getJSON(connection);

        } catch (MalformedURLException e) {
            Log.d("getPlacesList", " Error in URL. " + e.getMessage());
        } catch (IOException ei) {
            Log.d("getPlacesList","Error obtaining JSON object. " + ei.getMessage());
        }

        SearchResultsTBL searchDbHelper = new SearchResultsTBL(this);
        for (int i = 0; i < placeArrayList.size() ; i++) {
            searchDbHelper.insertPlace(placeArrayList.get(i));
        }
        updateServiceStatus(getString(R.string.search_service_status_FINISHED));
    }
    private String buildURL(@NonNull Location location,@NonNull String searchRadius,@Nullable String searchTerm){
        String url = null;
        String tempLocation = Double.toString(location.getLatitude()) + "," + Double.toString(location.getLongitude());
        if(searchTerm != null){
             url = String.format("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=%s&radius=%s&key=%s&keyword=%s&language=%s"
                    ,tempLocation
                    ,searchRadius
                     ,getString(R.string.google_maps_key)
                     ,searchTerm
                     , Locale.getDefault().getLanguage()
            );

        } else {
            url = String.format("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=%s&radius=%s&key=%s&language=%s"
                    ,tempLocation
                    ,searchRadius
                    ,getString(R.string.google_maps_key)
                    ,Locale.getDefault().getLanguage()
            );
        }

        Log.d("buildURL","URL Constructed: " + url);
        return url;
    }
    private HttpURLConnection getConnection(URL url){
        Log.i("getConnection","Connecting to URL: " + url.toString());
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
            if(connection.getResponseCode()  != HttpURLConnection.HTTP_OK){
                Log.e("getConnection","Bad HTTP message");
            }
        } catch (IOException e) {
            Log.e("getConnection","Can't connect." + e.getMessage());
        }
        return connection;
    }
    private JSONObject getJSON(HttpURLConnection connection){
        JSONObject movieJson = null,jsonResponse = null;
        String inputLine="", resultResponse="";

        Log.i("getJSON","Reading the response from the URL");
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            while ((inputLine = reader.readLine()) != null) {
                resultResponse += inputLine;
            }
            jsonResponse = new JSONObject(resultResponse);
            if(jsonResponse.getString("Response").equals("True")){
                Log.i("getJSON","Found movie object " + jsonResponse.getString("Title"));
                movieJson = jsonResponse;
            } else {
                Log.i("getJSON","JSON object returned no movie." + jsonResponse.getString("Error"));
            }
        } catch (IOException e) {
            Log.w("getJSON","Input Stream not opened. " + e.getMessage());
        } catch (JSONException je) {
            Log.e("getJSON","No JSON object obtained. " + je.getMessage());
        }
        return movieJson;
    }


}
