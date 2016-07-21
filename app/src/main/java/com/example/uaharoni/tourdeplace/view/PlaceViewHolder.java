package com.example.uaharoni.tourdeplace.view;

import android.content.Context;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.uaharoni.tourdeplace.R;
import com.example.uaharoni.tourdeplace.model.Place;
import com.squareup.picasso.Picasso;

public class PlaceViewHolder extends RecyclerView.ViewHolder {

    private TextView txtPlaceName, txtPlaceAddress, txtPlaceDistance;
    private RatingBar rbPlaceRating;
    private ImageView iVPlaceImage;
    private Context context;
    private Place place;
    private Location curPlace;

    public PlaceViewHolder(@NonNull View itemView) {
        super(itemView);

        txtPlaceName = (TextView) itemView.findViewById(R.id.txtPlaceName);
        txtPlaceAddress = (TextView) itemView.findViewById(R.id.txtPlaceAddress);
        iVPlaceImage = (ImageView) itemView.findViewById(R.id.iv_placeIcon);
        txtPlaceDistance = (TextView) itemView.findViewById(R.id.txtPlaceDistance);
        rbPlaceRating = (RatingBar)itemView.findViewById(R.id.rb_placeRating);

        context = itemView.getContext().getApplicationContext();
    }

    public void bind(@NonNull Place remotePlace,String distance){
        txtPlaceName.setText(remotePlace.getName());
        txtPlaceAddress.setText(remotePlace.getAddress().getName());
        txtPlaceDistance.setText(getDistanceString(remotePlace,distance));

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
private String getDistanceString(Place remotePlace,String distanceUnit){
    String distanceLocalized=null;
    String distanceString;
    Location homeLocation = MainActivity.currentLocation;
    Location remoteLocation = new Location(remotePlace.getName());
    remoteLocation.setLatitude(remotePlace.getAddress().getAddLat());
    remoteLocation.setLongitude(remotePlace.getAddress().getAddLong());
    float distanceMeters = homeLocation.distanceTo(remoteLocation);
    //TODO: Implement Google Maps Direction API (https://developers.google.com/maps/documentation/directions/)
    if(distanceUnit.equals(context.getString(R.string.unit_system_km))){
        distanceLocalized = Float.toString(distanceMeters / 1000);
        distanceString = context.getString(R.string.unit_system_km_value);
    } else {
        distanceLocalized = Float.toString(distanceMeters / 1609.344f);
        distanceString = context.getString(R.string.unit_system_mi_value);
    }
    distanceLocalized = distanceLocalized.concat(" " + distanceString);
    Log.d("getDistanceString","Calculated distance: " + distanceLocalized );

    return distanceLocalized;
    }
}