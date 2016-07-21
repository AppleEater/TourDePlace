package com.example.uaharoni.tourdeplace.controller;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.uaharoni.tourdeplace.R;
import com.example.uaharoni.tourdeplace.model.Place;
import com.example.uaharoni.tourdeplace.view.MainActivity;
import com.example.uaharoni.tourdeplace.view.PlaceViewHolder;

import java.util.ArrayList;

public class PlaceListAdapter extends RecyclerView.Adapter<PlaceViewHolder> {

    private ArrayList<Place> places;
    private int viewId;
    private Context context;
    String distanceUnit;


    public PlaceListAdapter(ArrayList<Place> placeList, int viewId) {
        this.places = placeList;
        this.viewId = viewId;
    }

    @Override
    public int getItemViewType(int position) {
        switch (viewId){
            case R.id.rv_search:
                return R.id.rv_search;
            //TODO: Add rv_fav support
            default:
                return super.getItemViewType(position);
        }
    }

    @Override
    public PlaceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d("onCreateViewHolder","got parent " + parent.toString() + ", viewType: " + viewType);
        View itemView=null;
        context = parent.getContext();

        //View view = geLayoutInflater().inflate(R.layout.view_item, viewGroup, false);

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType){
            case R.id.rv_search:
                itemView = inflater.inflate(R.layout.rv_search_item,parent,false);
                break;
        }

        distanceUnit = MainActivity.sharedPreferences.getString(context.getString(R.string.settings_distance_units_key),context.getString(R.string.unit_system_km));

        return new PlaceViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PlaceViewHolder holder, int position) {
        Place place = places.get(position);
        Log.d("onBindView","Got place in position " + position);
        //holder.bindPlaceView(context,place,unitSystem,currentLocation);
        holder.bind(place,distanceUnit);
    }

    @Override
    public int getItemCount() {
        return places.size();
    }

}
