package com.example.uaharoni.tourdeplace.controller;

import android.content.Context;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.uaharoni.tourdeplace.R;
import com.example.uaharoni.tourdeplace.model.Place;
import com.example.uaharoni.tourdeplace.view.MainActivity;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Locale;

// Create the basic adapter extending from RecyclerView.Adapter
public class PlacesAdapter extends RecyclerView.Adapter<PlacesAdapter.CommonViewHolder> {
    private ArrayList<Place> places;
    private Context context;
    protected OnItemClickListener itemClickListener;
    protected OnItemLongClickListener itemLongClickListener;


    public void SetOnItemClickListener(OnItemClickListener listener){
        this.itemClickListener = listener;
    }
    public void SetOnItemLongClickListener(OnItemLongClickListener listener){
        this.itemLongClickListener = listener;
    }


    public PlacesAdapter(Context context, ArrayList<Place> places) {
        Log.d("PlacesAdapter","Init Constructor...");
        this.places = places;
        this.context = context;
    }


    @Override
    public CommonViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        Log.d("onCreateViewHolder","Got view from parent " + parent.getId());
        View itemView = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (parent.getId()){
            case R.id.rv_search:
                itemView = inflater.inflate(R.layout.rv_search_item,null,false);
                break;
            case R.id.rv_fav:
                itemView = inflater.inflate(R.layout.rv_fav_item,null,false);
        }
        if(itemView != null){
            return (new CommonViewHolder(itemView));
        } else {
            Log.d("onCreateViewHolder", "Parent view not found.");
            return null;
        }
    }

    @Override
    public void onBindViewHolder(CommonViewHolder holder, int position) {
        Place currentplace = places.get(position);
        Log.d("onBindViewHolder","Got place " + currentplace.getName() + " [" + currentplace.getAddress().getAddLat() + "," + currentplace.getAddress().getAddLong() + "]  at position " + position);
        holder.bind(currentplace);
    }


    @Override
    public int getItemCount() {
        return places.size();
    }


// Start of ViewHolder class
    public class CommonViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener, PopupMenu.OnMenuItemClickListener, View.OnClickListener {
        protected TextView txtPlaceName, txtPlaceAddress, txtPlaceDistance;
        protected ImageView iVPlaceImage;
        protected RatingBar rbPlaceRating;
        protected Context context;

        public CommonViewHolder(View itemView) {
            super(itemView);
            String TAG = "CommonViewHolder";
            context = itemView.getContext();

            Log.d(TAG, "Initializing " + TAG + " with parent " + itemView.getId());
            txtPlaceName = (TextView) itemView.findViewById(R.id.txtPlaceName);
            txtPlaceAddress = (TextView) itemView.findViewById(R.id.txtPlaceAddress);
            iVPlaceImage = (ImageView) itemView.findViewById(R.id.iv_placeIcon);
            txtPlaceDistance = (TextView) itemView.findViewById(R.id.txtPlaceDistance);
            rbPlaceRating = (RatingBar) itemView.findViewById(R.id.rb_placeRating);

            itemView.setOnLongClickListener(this);
            itemView.setOnClickListener(this);

        }
        public void bind(Place remotePlace){
            Log.d("bind", "Showing remote place " + remotePlace.getName() + "[" + remotePlace.getAddress().getAddLat() + "," + remotePlace.getAddress().getAddLong() + "]");
            txtPlaceName.setText(remotePlace.getName());
            txtPlaceAddress.setText(remotePlace.getAddress().getName());
            txtPlaceDistance.setText(getDistanceString(remotePlace));
            if (iVPlaceImage != null) {
                Picasso.with(context)
                        .load(remotePlace.getPlaceIconUrl())
                        .placeholder(android.R.drawable.ic_menu_view)
                        .memoryPolicy(MemoryPolicy.NO_STORE)
                        .into(iVPlaceImage);
            }
            if (rbPlaceRating != null) {
                rbPlaceRating.setRating(remotePlace.getPlaceRating());
            }
        }
    private String getDistanceString(Place remotePlace) {
        float distanceLocalized;
        String distanceString, returnString;
        Location homeLocation = MainActivity.currentLocation;
        Log.d("getDistanceString", "Home Location: " + homeLocation.toString());
        Location remoteLocation = new Location(remotePlace.getName());
        remoteLocation.setLatitude(remotePlace.getAddress().getAddLat());
        remoteLocation.setLongitude(remotePlace.getAddress().getAddLong());
        Log.d("getDistanceString", "Remote Location: " + remoteLocation.toString());
        float distanceMeters = homeLocation.distanceTo(remoteLocation);
        Log.d("getDistanceString", "Calculated distance: " + distanceMeters);
        //TODO: Implement Google Maps Direction API (https://developers.google.com/maps/documentation/directions/)
        String distanceUnit = MainActivity.sharedPreferences.getString(context.getString(R.string.settings_distance_units_key), context.getString(R.string.unit_system_km));
        if (distanceUnit.equals(context.getString(R.string.unit_system_km))) {
            distanceLocalized = distanceMeters / 1000;
            distanceString = context.getString(R.string.unit_system_km_value);
        } else {
            distanceLocalized = distanceMeters / 1609.344f;
            distanceString = context.getString(R.string.unit_system_mi_value);
        }

        returnString = String.format(Locale.getDefault(),"%.2f %s",distanceLocalized, distanceString);

        //distanceLocalized = distanceLocalized.concat(" " + distanceString);
        Log.d("getDistanceString", "Converted distance: " + returnString);

        return returnString;
    }

    @Override
    public boolean onLongClick(View view) {
        Log.d("onLongClick","LongClick on view " + view.getId());

        PopupMenu popup = new PopupMenu(context,view);
        switch (view.getId()){
            case R.id.rv_search:
                Log.d("onLongClick","Inflating PopupMenu for SearchFrag");
                popup.inflate(R.menu.search_frag_popup);
                break;
            case R.id.rv_fav:
                Log.d("onLongClick","Inflating PopupMenu for FavFrag");
                popup.inflate(R.menu.fav_frag_popup);
                break;
                default:
                    Log.d("onLongClick","No view was found");
                    break;
        }
        popup.setOnMenuItemClickListener(this);
        popup.show();

        return true;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        Place selectedPlace = places.get(getLayoutPosition());
        Log.d("onMenuItemClick","Clicked item " + item.getTitle() + " for place " + selectedPlace.getName());
        if(itemLongClickListener != null){
            switch (item.getItemId()) {
                case R.id.search_frag_add_to_favorites:
                    Log.d("onMenuItemClick","Adding place " + selectedPlace.getName() + " to Favorites");
                    itemLongClickListener.onAddToFavorites(selectedPlace);
                    break;
                case R.id.item_share:
                    Log.d("onMenuItemClick","Sharing place " + selectedPlace.getName() + " to other places");
                    itemLongClickListener.onSharePlace(selectedPlace);
                    break;
                case R.id.fav_frag_remove_from_favorites:
                    Log.d("onMenuItemClick","Remove place " + selectedPlace.getName() + " from Favorites");
                    itemLongClickListener.onRemoveFromFavorites(selectedPlace);
                    break;
            }
        } else {
            Log.d("onMenuItemClick", "No itemLongClickListener was found.");
        }

        return true;
    }

    @Override
    public void onClick(@NonNull View view) {
        int position = getLayoutPosition();
        Log.d("onClick-ViewHolder","Clicked item number " + position);
        Place selectedPlace = places.get(position);
        Log.d("onClick-ViewHolder","Got place " + selectedPlace.getName() + ". Calling parent fragment...");
        if(itemClickListener != null) {
            itemClickListener.onItemClick(selectedPlace);
        }
    }
}   // End of ViewHolder class
}   // End of PlacesAdapter
