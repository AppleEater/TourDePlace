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

// Create the basic adapter extending from RecyclerView.Adapter
public class PlacesAdapter  extends RecyclerView.Adapter<PlacesAdapter.ViewHolder> {

    public interface OnItemClickListener
    {
        public void onItemClick(View viewClicked);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // Your holder should contain a member variable for any view that will be set as you render a row
        private TextView txtPlaceName, txtPlaceAddress, txtPlaceDistance;
        private RatingBar rbPlaceRating;
        private ImageView iVPlaceImage;
        private Context context;
        private  OnItemClickListener listener;

        public ViewHolder(View itemView) {
            super(itemView);
            txtPlaceName = (TextView) itemView.findViewById(R.id.txtPlaceName);
            txtPlaceAddress = (TextView) itemView.findViewById(R.id.txtPlaceAddress);
            iVPlaceImage = (ImageView) itemView.findViewById(R.id.iv_placeIcon);
            txtPlaceDistance = (TextView) itemView.findViewById(R.id.txtPlaceDistance);
            rbPlaceRating = (RatingBar) itemView.findViewById(R.id.rb_placeRating);
            context = itemView.getContext();
            //itemView.setClickable(true);
            itemView.setOnClickListener(this);

        }
        public void bind(@NonNull final Place remotePlace, final OnItemClickListener listener){
            this.listener = listener;
            Log.d("bind","Showing remote place " + remotePlace.getName() + "[" + remotePlace.getAddress().getAddLat() +"," +remotePlace.getAddress().getAddLong() + "]");
            txtPlaceName.setText(remotePlace.getName());
            txtPlaceAddress.setText(remotePlace.getAddress().getName());
            txtPlaceDistance.setText(getDistanceString(remotePlace));

            if(rbPlaceRating != null){
                rbPlaceRating.setRating(remotePlace.getPlaceRating());
            }
            if (iVPlaceImage != null) {
                Picasso.with(context)
                        .load(remotePlace.getPlaceIconUrl())
                        .placeholder(android.R.drawable.ic_menu_upload_you_tube)
                        .into(iVPlaceImage);
            }
        }
        private String getDistanceString(Place remotePlace){
            String distanceLocalized=null;
            String distanceString;
            Location homeLocation = MainActivity.currentLocation;
            Log.d("getDistanceString", "Home Location: " + homeLocation.toString());
            Location remoteLocation = new Location(remotePlace.getName());
            remoteLocation.setLatitude(remotePlace.getAddress().getAddLat());
            remoteLocation.setLongitude(remotePlace.getAddress().getAddLong());
            Log.d("getDistanceString", "Remote Location: " + remoteLocation.toString());
            float distanceMeters = homeLocation.distanceTo(remoteLocation);
            Log.d("getDistanceString","Calculated distance: " + distanceMeters );
            //TODO: Implement Google Maps Direction API (https://developers.google.com/maps/documentation/directions/)
            String distanceUnit = MainActivity.sharedPreferences.getString(context.getString(R.string.settings_distance_units_key),context.getString(R.string.unit_system_km));
            if(distanceUnit.equals(context.getString(R.string.unit_system_km))){
                distanceLocalized = Float.toString(Math.round(distanceMeters /1000));
                distanceString = context.getString(R.string.unit_system_km_value);
            } else {
                distanceLocalized = Float.toString(Math.round(distanceMeters / 1609.344f));
                distanceString = context.getString(R.string.unit_system_mi_value);
            }
            distanceLocalized = distanceLocalized.concat(" " + distanceString);
            Log.d("getDistanceString","Converted distance: " + distanceLocalized );

            return distanceLocalized;
        }

        @Override
        public void onClick(View view) {
            Log.d("OnClick-ViewHolder","Got Click on view " + view.toString());
            listener.onItemClick(view);
        }
    }

    private ArrayList<Place> places;
    private String distanceUnit;
    private final OnItemClickListener listener;
    private static final int SEARCH_ITEM = 0; // Declaring Variable to Understand which View is being worked on
    private static final int FAV_ITEM = 1;



    public PlacesAdapter(Context context, ArrayList<Place> places,OnItemClickListener listener) {
        this.places = places;
        this.listener = listener;
        distanceUnit = MainActivity.sharedPreferences.getString(context.getString(R.string.settings_distance_units_key),context.getString(R.string.unit_system_km));
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = null;
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        // We don't care about the position. only the parentView
        switch (parent.getId()){
            case R.id.rv_search:
                Log.d("onCreateViewHolder", "Found search layout");
                itemView = inflater.inflate(R.layout.rv_search_item,parent,false);
                break;
            case R.id.rv_fav:
                Log.d("onCreateViewHolder", "Found fav layout");
                itemView = inflater.inflate(R.layout.rv_search_item,parent,false);
                break;
            default:
                Log.d("onCreateViewHolder", "No parent view identified");
                break;
        }

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Log.d("onBindViewHolder","Got holder id: " + holder.getItemId());
        // Get the data model based on position
        Place place = places.get(position);
        Log.d("onBindViewHolder","Got place " + place.getName() + " [" + place.getAddress().getAddLat() + "," + place.getAddress().getAddLong() + "]  at position " + position);

        // Set item views based on your views and data model
        holder.bind(place,listener);
    }

    @Override
    public int getItemCount() {
        return places.size();
    }

}
