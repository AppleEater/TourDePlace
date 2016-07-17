package com.example.uaharoni.tourdeplace.view;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
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
import com.example.uaharoni.tourdeplace.controller.PlaceListAdapter;
import com.example.uaharoni.tourdeplace.controller.SearchReceiver;
import com.example.uaharoni.tourdeplace.controller.SearchResultsTBL;
import com.example.uaharoni.tourdeplace.model.Place;


public class SearchFragment extends Fragment {

    private SearchResultsTBL searchDbHelper;
    private SearchReceiver searchServiceReceiver;
    private static PlaceListAdapter searchAdapter;
    static ProgressBar progressBar;

    private ShareActionProvider shareProvider;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        searchDbHelper = new SearchResultsTBL(getContext());
        searchServiceReceiver = new SearchReceiver();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("onCreateViewSearchFrag","Inflating View");
        // Inflate the layout for this fragment
        View searchFragLayout = inflater.inflate(R.layout.fragment_search, container, false);


        RecyclerView recyclerView = (RecyclerView) searchFragLayout.findViewById(R.id.rv_search);
        searchAdapter = new PlaceListAdapter(searchDbHelper.getAllPlaces(),R.id.rv_search);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(searchAdapter);

        progressBar = (ProgressBar) container.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        return searchFragLayout;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("onResumeSearchFrag","Registering search service broadcast with action " + getString(R.string.search_service_custom_intent_action));
        LocalBroadcastManager.getInstance(getContext().getApplicationContext()).registerReceiver(searchServiceReceiver, new IntentFilter(getString(R.string.search_service_custom_intent_action)));
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("onPauseSearchFrag","Removing receivers");
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(searchServiceReceiver);
    }
    public static void updateProgressBar(int statusCde) {
        switch (statusCde){
            case 0:
                progressBar.setVisibility(View.VISIBLE);
                break;
            case 1:
                progressBar.setVisibility(View.GONE);
                break;
            case 2:
                progressBar.setVisibility(View.GONE);
        }
        searchAdapter.notifyDataSetChanged();
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
        inflater.inflate(R.menu.share_place,menu);
        shareProvider = (ShareActionProvider)menu.findItem(R.id.action_share);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_share:
                doSharePlace((Place) item);
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
        intent.putExtra(getString(R.string.settings_last_location_latitude),place.getAddress().getAddLat());
        intent.putExtra(getString(R.string.settings_last_location_longitude),place.getAddress().getAddLong());
        intent.putExtra("PLACE_NAME",place.getName());
        shareProvider.setShareIntent(intent);
}
}