package com.example.uaharoni.tourdeplace.controller;

import android.content.Context;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import com.example.uaharoni.tourdeplace.R;
import com.example.uaharoni.tourdeplace.model.Place;
import com.example.uaharoni.tourdeplace.view.MainActivity;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.Locale;

// Create the basic adapter extending from RecyclerView.Adapter
public class PlacesAdapter  extends RecyclerView.Adapter<PlacesAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // Your holder should contain a member variable for any view that will be set as you render a row
        private TextView txtPlaceName, txtPlaceAddress, txtPlaceDistance;
        private RatingBar rbPlaceRating;
        private ImageView iVPlaceImage;
        private Context context;

        private String TAG = "ViewHolder";

        public ViewHolder(View itemView) {
            super(itemView);
            Log.d(TAG, "Initializing ViewHolder");

            txtPlaceName = (TextView) itemView.findViewById(R.id.txtPlaceName);
            txtPlaceAddress = (TextView) itemView.findViewById(R.id.txtPlaceAddress);
            iVPlaceImage = (ImageView) itemView.findViewById(R.id.iv_placeIcon);
            txtPlaceDistance = (TextView) itemView.findViewById(R.id.txtPlaceDistance);
            rbPlaceRating = (RatingBar) itemView.findViewById(R.id.rb_placeRating);
            context = itemView.getContext();
            itemView.setClickable(true);
            itemView.setOnClickListener(this);

        }

        public void bind(@NonNull final Place remotePlace) {
            Log.d("bind", "Showing remote place " + remotePlace.getName() + "[" + remotePlace.getAddress().getAddLat() + "," + remotePlace.getAddress().getAddLong() + "]");
            txtPlaceName.setText(remotePlace.getName());
            txtPlaceAddress.setText(remotePlace.getAddress().getName());
            txtPlaceDistance.setText(getDistanceString(remotePlace));

            if (rbPlaceRating != null) {
                rbPlaceRating.setRating(remotePlace.getPlaceRating());
            }
            if (iVPlaceImage != null) {
                Picasso.with(context)
                        .load(remotePlace.getPlaceIconUrl())
                        .placeholder(android.R.drawable.ic_menu_upload_you_tube)
                        .skipMemoryCache()
                        .into(iVPlaceImage);
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
        public void onClick(View view) {
            Log.d(TAG, "Clicked view at position " + getLayoutPosition());
        }
    }

    private ArrayList<Place> places;
    private String distanceUnit;
    private static final int SEARCH_ITEM = 0; // Declaring Variable to Understand which View is being worked on
    private static final int FAV_ITEM = 1;



    public PlacesAdapter(Context context, ArrayList<Place> places) {
        Log.d("PlacesAdapter","Init Constructor...");
        this.places = places;
        distanceUnit = MainActivity.sharedPreferences.getString(context.getString(R.string.settings_distance_units_key),context.getString(R.string.unit_system_km));
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = null;
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        Log.d("onCreateViewHolder", "Got Parent " + parent.toString());

        // We don't care about the position. only the parentView
        switch (parent.getId()){
            case R.id.rv_search:
                itemView = inflater.inflate(R.layout.rv_search_item,parent,false);
                break;
            case R.id.rv_fav:
                itemView = inflater.inflate(R.layout.rv_fav_item,parent,false);
                break;
            default:
                Log.d("onCreateViewHolder", "No parent view identified");
                break;
        }

        // Return a new holder instance
        return (new ViewHolder(itemView));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // Get the data model based on position
        Place place = places.get(position);
        Log.d("onBindViewHolder","Got place " + place.getName() + " [" + place.getAddress().getAddLat() + "," + place.getAddress().getAddLong() + "]  at position " + position);

        // Set item views based on your views and data model
        holder.bind(place);
    }

    @Override
    public int getItemCount() {
        return places.size();
    }

}
