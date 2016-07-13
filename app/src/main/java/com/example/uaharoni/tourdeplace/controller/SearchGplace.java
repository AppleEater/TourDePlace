package com.example.uaharoni.tourdeplace.controller;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.example.uaharoni.tourdeplace.R;
import com.google.android.gms.maps.model.LatLng;

import java.util.Locale;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class SearchGplace extends IntentService {

    public static String AppLang = Locale.getDefault().getLanguage();
    public final  static String URL_PROTOCOL = "https";
    public final  static String AUTHORITY = "maps.googleapis.com";
    public final static String JSON_RESPNOSE = "status";
    public final static String JSON_RESPONSE_TYPE_TRUE = "OK";
    public final static String JSON_SEARCH_ARRAY = "results";
    public final static String JSON_PLACE_NAME = "name";
    public final static String JSON_PLACE_ADDRESS_NAME = "vicinity";

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
        if (intent != null) {
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
        }
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
        Uri.Builder urlBuilder = new Uri.Builder().scheme(URL_PROTOCOL)
                .authority(AUTHORITY)
                .appendPath("maps")
                .appendPath("api")
                .appendPath("place")
                .appendPath("nearbysearch")
                .appendPath("json")
                .appendQueryParameter("language", Locale.getDefault().getLanguage())
                .appendQueryParameter("radius", String.valueOf(searchRadius))
                .appendQueryParameter("location", String.valueOf(location.latitude) + "," + String.valueOf(location.longitude))
                .appendQueryParameter("keyword", (term == null) ? "" : term)
                .appendQueryParameter("key",getString(R.string.google_maps_key))
//                .appendQueryParameter("pagetoken", (pagetoken == null) ? "" : pagetoken)
                ;

        Log.i("buildUrl", "Loading URI:" + urlBuilder.build().toString());
        return urlBuilder.build();
    }
}
