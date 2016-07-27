package com.example.uaharoni.tourdeplace.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
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
import com.example.uaharoni.tourdeplace.DividerItemDecoration;
import com.example.uaharoni.tourdeplace.R;
import com.example.uaharoni.tourdeplace.controller.PlacesAdapter;
import com.example.uaharoni.tourdeplace.controller.SearchResultsTBL;

public class SearchFragment extends Fragment {

    private SearchResultsTBL dbHelper;
    private SearchReceiver searchServiceReceiver;
    private PlacesAdapter recyclerAdapter;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;

    private String TAG = "SearchFrag";

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = new SearchResultsTBL(getContext());

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
        recyclerAdapter = new PlacesAdapter(getContext(), dbHelper.getAllPlaces());
        recyclerView.setAdapter(recyclerAdapter);
    }
}