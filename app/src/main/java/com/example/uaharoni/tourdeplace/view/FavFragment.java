package com.example.uaharoni.tourdeplace.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.uaharoni.tourdeplace.R;
import com.example.uaharoni.tourdeplace.controller.DividerItemDecoration;
import com.example.uaharoni.tourdeplace.controller.FavsTBL;
import com.example.uaharoni.tourdeplace.controller.OnItemClickListener;
import com.example.uaharoni.tourdeplace.controller.OnItemLongClickListener;
import com.example.uaharoni.tourdeplace.controller.PlacesAdapter;
import com.example.uaharoni.tourdeplace.model.Place;

public class FavFragment extends Fragment implements OnItemClickListener,OnItemLongClickListener {

    private FavsTBL dbHelper;
    private PlacesAdapter recyclerAdapter;
    private RecyclerView recyclerView;
    private ShareActionProvider shareView;
    OnItemClickListener itemClickListener;
    OnItemLongClickListener itemLongClickListener;


    private String TAG = "FavFrag";


    public FavFragment() {
        // Required empty public constructor
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // This makes sure that the container activity has implemented the callback interface. If not, it throws an exception
        try {
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
        dbHelper = new FavsTBL(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "Inflating View");
        View favFragLayout = inflater.inflate(R.layout.fragment_fav, container, false);

        recyclerView = (RecyclerView) favFragLayout.findViewById(R.id.rv_fav);
        refreshAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        RecyclerView.ItemDecoration itemDecoration =
                new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL);
        recyclerView.addItemDecoration(itemDecoration);


        // Inflate the layout for this fragment
        return favFragLayout;
    }

    @Override
    public void onItemClick(@NonNull Place place) {
        Log.d("onItemClick-"+TAG,"Got Place " + place.getName() + ". Asking the parent activity to add marker at MapFragment");
        itemClickListener.onItemClick(place);
    }

    public void doSharePlace(@NonNull Place place) {
        // populate the share intent with data
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(getString(R.string.settings_last_location_latitude), place.getAddress().getAddLat());
        intent.putExtra(getString(R.string.settings_last_location_longitude), place.getAddress().getAddLong());
        intent.putExtra("PLACE_NAME", place.getName());
        shareView.setShareIntent(intent);
    }
    public void refreshAdapter() {
        Log.d("refreshAdapter-FavsFrag", "Refreshing RecyclerView adapter...");
        try {
            recyclerAdapter = new PlacesAdapter(getContext(), dbHelper.getAllPlaces());
        } catch (Exception e) {
            Log.d(TAG,"Failed to create adapter. " + e.getMessage());
        }
        recyclerAdapter.SetOnItemClickListener(this);
        recyclerAdapter.SetOnItemLongClickListener(this);
        recyclerView.setAdapter(recyclerAdapter);
    }
    protected void addPlace(@NonNull Place place){
        Log.d("addPlace-FavFrag","Adding place " + place.getName() + "(gplaceId: " + place.getgPlaceId() + ") to FavDB");
        dbHelper.insertPlace(place);
        Log.d("addPlace-FavFrag","Refreshing adapter");
        refreshAdapter();
    }

    @Override
    public void onRemoveFromFavorites(Place place) {
        Log.d("deletePlace-FavFrag","Deleting place " + place.getName() + "(gplaceId: " + place.getgPlaceId() + ") from FavDB");
        dbHelper.deletePlace(place);
        Log.d("deletePlace-FavFrag","Refreshing adapter");
        refreshAdapter();
    }
    @Override
    public void onAddToFavorites(Place place) {
        // Not relevant
    }

    @Override
    public void onSharePlace(Place place) {
        Log.d("onSharePlace-"+TAG,"Got Place " + place.getName() + ". Asking the parent activity to share");
        itemLongClickListener.onSharePlace(place);
    }

}
