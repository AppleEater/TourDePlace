package com.example.uaharoni.tourdeplace.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.uaharoni.tourdeplace.R;
import com.example.uaharoni.tourdeplace.controller.PlacesAdapter;
import com.example.uaharoni.tourdeplace.controller.SearchResultsTBL;
import com.example.uaharoni.tourdeplace.model.Place;


public class SearchFragment extends Fragment {

    private SearchResultsTBL searchDbHelper;
    private SearchReceiver searchServiceReceiver;
    // private PlaceListAdapter searchAdapter;
    private PlacesAdapter searchAdapter;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private ShareActionProvider shareView;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        searchDbHelper = new SearchResultsTBL(getContext());
        searchServiceReceiver = new SearchReceiver();
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("SearchFrag", "Inflating View");
        // Inflate the layout for this fragment
        View searchFragLayout = inflater.inflate(R.layout.fragment_search, container, false);

        searchAdapter = new PlacesAdapter(getContext(), searchDbHelper.getAllPlaces());

        recyclerView = (RecyclerView) searchFragLayout.findViewById(R.id.rv_search);
        recyclerView.setAdapter(searchAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);

        progressBar = (ProgressBar) searchFragLayout.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        return searchFragLayout;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("SearchFrag", "Registering search service broadcast with action " + getString(R.string.search_service_custom_intent_action));
        //LocalBroadcastManager.getInstance(getContext().getApplicationContext()).registerReceiver(searchServiceReceiver, new IntentFilter(getString(R.string.search_service_custom_intent_action)));
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(searchServiceReceiver, new IntentFilter(getString(R.string.search_service_custom_intent_action)));

    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("SearchFrag", "Removing receivers");
        //LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(searchServiceReceiver);
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(searchServiceReceiver);

    }

    public void updateProgressBar(int statusCde) {
        switch (statusCde) {
            case 0:
                progressBar.setVisibility(View.VISIBLE);
                break;
            case 1:
                progressBar.setVisibility(View.GONE);
                break;
            case 2:
                progressBar.setVisibility(View.GONE);
        }
    }

    public void refreshAdapter() {
        Log.d("refreshAdapter", "Refreshing RecyclerView adapter...");
        searchAdapter.notifyDataSetChanged();
        recyclerView.invalidate();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.share_place, menu);

        MenuItem shareItem = menu.findItem(R.id.action_share);
        shareView = (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d("onOptionsItemSelecd", "Selected item " + item.toString());
        switch (item.getItemId()) {
            case R.id.action_share:
                //TODO: Get the selected place
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    public void doSharePlace(Place place) {
        // populate the share intent with data
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(getString(R.string.settings_last_location_latitude), place.getAddress().getAddLat());
        intent.putExtra(getString(R.string.settings_last_location_longitude), place.getAddress().getAddLong());
        intent.putExtra("PLACE_NAME", place.getName());
        shareView.setShareIntent(intent);
    }

    public class SearchReceiver extends BroadcastReceiver {
        private String TAG = "SearchReceiver";

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive");
            if (intent != null) {
                String intentAction = intent.getAction();
                Log.d(TAG, "Received action " + intentAction);
                if (intentAction.equals(context.getString(R.string.search_service_custom_intent_action))) {
                    String serviceStatus = intent.getStringExtra(context.getString(R.string.search_service_custom_intent_status));
                    Log.d(TAG, "Got Search service status: " + serviceStatus);
                    Intent intentSnack = new Intent(context.getString(R.string.power_receiver_custom_intent_action));
                    String message = "Unknown";
                    switch (Integer.parseInt(serviceStatus)) {
                        case 0:
                            message = context.getString(R.string.search_service_status_RUNNING_text);
                            break;
                        case 1:
                            message = context.getString(R.string.search_service_status_FINISHED_text);
                            break;
                        case 2:
                            message = context.getString(R.string.search_service_status_ERROR_text);
                            break;
                        default:
                            message = context.getString(R.string.search_service_status_UNKNOWN_text);
                            break;
                    }
                    intentSnack.putExtra(context.getString(R.string.snackbar_message_custom_intent_extra_text), message);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intentSnack);
                    updateProgressBar(Integer.parseInt(serviceStatus));
                    if (Integer.parseInt(serviceStatus) == 1) {
                        Log.d("onReceive", "Time to refresh the adapter");
                        refreshAdapter();
                    }
                }
            }
        }
    }


}