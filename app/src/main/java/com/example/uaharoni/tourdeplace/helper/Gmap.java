package com.example.uaharoni.tourdeplace.helper;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.example.uaharoni.tourdeplace.R;
import com.example.uaharoni.tourdeplace.model.Place;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;

public class Gmap {
    public static String AppLang = Locale.getDefault().getLanguage();
    public final  static String URL_PROTOCOL = "https";
    public final  static String AUTHORITY = "maps.googleapis.com";
    public final static String JSON_RESPNOSE = "status";
    public final static String JSON_RESPONSE_TYPE_TRUE = "OK";
    public final static String JSON_SEARCH_ARRAY = "results";
    public final static String JSON_PLACE_NAME = "name";
    public final static String JSON_PLACE_ADDRESS_NAME = "vicinity";

    private Uri buildUrl(Context context, LatLng location, int searchRadius, String term, String pagetoken) {
        Uri.Builder urlBuilder = new Uri.Builder().scheme(Gmap.URL_PROTOCOL)
                .authority(Gmap.AUTHORITY)
                .appendPath("maps")
                .appendPath("api")
                .appendPath("place")
                .appendPath("nearbysearch")
                .appendPath("json")
                .appendQueryParameter("language", Locale.getDefault().getLanguage())
                .appendQueryParameter("pagetoken", (pagetoken == null) ? "" : pagetoken)
                .appendQueryParameter("radius", String.valueOf(searchRadius))
                .appendQueryParameter("location", String.valueOf(location.latitude) + "," + String.valueOf(location.longitude))
                .appendQueryParameter("keyword", (term == null) ? "" : term)
                .appendQueryParameter("key", context.getString(R.string.google_maps_key));

        Log.i("buildUrl", "Loading URI:" + urlBuilder.build().toString());
        return urlBuilder.build();
    }
    private ArrayList<Place> getNearbyPlacesList(Context context, LatLng location, int searchRadius, String keyword) {
        ArrayList<Place> arrPlacesList = new ArrayList<>();
        URL searchPlacesUrl = null;
        try {
            searchPlacesUrl = new URL(buildUrl(context, location, searchRadius, keyword, null).toString());
            HttpURLConnection connection = (HttpURLConnection) searchPlacesUrl.openConnection();
            JSONObject jsResponse = getJSON(connection);


        } catch (MalformedURLException e) {
            Log.d("getNearbyPlacesList", "Invalid URL." + e.getMessage());
        } catch (IOException e) {
            Log.d("getNearbyPlacesList", "Connection Error. " + e.getMessage());
        }

        return arrPlacesList;
    }
    private JSONObject getJSON(HttpURLConnection connection) {
        JSONObject jsonResponse = null;
        if (connection == null) {
            Log.e("getJSON:", "No connection was found ");
            return null;
        }
        String inputLine;
        String resultResponse = "";
        BufferedReader reader;
        try {
            Log.d("getJSON", "Reading the response from the URL");
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            while ((inputLine = reader.readLine()) != null) {
                resultResponse += inputLine;
            }
        } catch (IOException e) {
            Log.e("getJSON", "Error reading from BufferReader. " + e.getMessage());
        }
        return jsonResponse;
    }
}
