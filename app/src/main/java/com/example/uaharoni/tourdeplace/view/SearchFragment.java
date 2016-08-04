package com.example.uaharoni.tourdeplace.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.uaharoni.tourdeplace.R;
import com.example.uaharoni.tourdeplace.controller.DividerItemDecoration;
import com.example.uaharoni.tourdeplace.controller.OnItemClickListener;
import com.example.uaharoni.tourdeplace.controller.OnItemLongClickListener;
import com.example.uaharoni.tourdeplace.controller.PlacesAdapter;
import com.example.uaharoni.tourdeplace.controller.SearchTBL;
import com.example.uaharoni.tourdeplace.model.Place;

public class SearchFragment extends Fragment implements OnItemClickListener, OnItemLongClickListener {

    private SearchTBL dbHelper;
    private SearchReceiver searchServiceReceiver;
    private PlacesAdapter recyclerAdapter;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    //OnPlaceSelected mCallback;
    OnItemClickListener itemClickListener;
    OnItemLongClickListener itemLongClickListener;



    private String TAG = "SearchFrag";

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // This makes sure that the container activity has implemented the callback interface. If not, it throws an exception
        try {
            //mCallback = (OnPlaceSelected) getActivity();
            itemClickListener = (OnItemClickListener) getActivity();
            itemLongClickListener = (OnItemLongClickListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement missing interface");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = new SearchTBL(getContext());
        searchServiceReceiver = new SearchReceiver();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("SearchFrag", "Registering search service broadcast with action " + getString(R.string.search_service_custom_intent_action));
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(searchServiceReceiver, new IntentFilter(getString(R.string.search_service_custom_intent_action)));
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("SearchFrag", "Removing receivers");
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(searchServiceReceiver);
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "Inflating View");
        View fragmentLayout = inflater.inflate(R.layout.fragment_search, container, false);

        recyclerView = (RecyclerView) fragmentLayout.findViewById(R.id.rv_search);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        RecyclerView.ItemDecoration itemDecoration =
                new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL);
        recyclerView.addItemDecoration(itemDecoration);
        refreshAdapter();

        progressBar = (ProgressBar) fragmentLayout.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        return fragmentLayout;
    }
    protected void refreshAdapter() {
        Log.d(TAG, "Refreshing RecyclerView adapter...");
        try {
            recyclerAdapter = new PlacesAdapter(getContext(), dbHelper.getAllPlaces());
        } catch (Exception e) {
            Log.d(TAG,"Failed to create adapter. " + e.getMessage());
        }
        recyclerAdapter.SetOnItemClickListener(this);
        recyclerAdapter.SetOnItemLongClickListener(this);
        recyclerView.setAdapter(recyclerAdapter);
    }
    @Override
    public void onItemClick(@NonNull Place place) {
        Log.d("onItemClick-"+TAG,"Got Place " + place.getName() + ". Asking the parent activity to add marker at MapFragment");
        // Send the event to the host activity
        itemClickListener.onItemClick(place);
    }
    @Override
    public void onAddToFavorites(@NonNull Place place) {
        Log.d("onAddToFavorites-"+TAG,"Got Place " + place.getName() + ". Asking the parent activity to add to Favorites");
        itemLongClickListener.onAddToFavorites(place);
    }

    @Override
    public void onSharePlace(@NonNull Place place) {
        Log.d("onSharePlace-"+TAG,"Got Place " + place.getName() + ". Asking the parent activity to share");
        itemLongClickListener.onSharePlace(place);
    }

    @Override
    public void onRemoveFromFavorites(Place place) {
        // Not relevant
    }

}