package com.example.uaharoni.tourdeplace.view;

import android.content.Context;
import android.location.Location;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.uaharoni.tourdeplace.R;
import com.example.uaharoni.tourdeplace.controller.OnViewHolderClickListener;
import com.example.uaharoni.tourdeplace.model.Place;

public class PlaceViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

    private Place place;
    private TextView txtPlaceName, txtPlaceAddress, txtPlaceDistance;
    private String placeName, placeAddress;
    private double placeAddLat, placeAddLong;
    private ImageView iVPlaceImage;

    private OnViewHolderClickListener mCallback;

    public PlaceViewHolder(View itemView) {
        super(itemView);
        Log.d("PlaceViewHolder", "Got item " + getItemId() + " on view " + getItemViewType());

        txtPlaceName = (TextView) itemView.findViewById(0);
        txtPlaceAddress = (TextView) itemView.findViewById(0);
        iVPlaceImage = (ImageView) itemView.findViewById(0);
        txtPlaceDistance = (TextView) itemView.findViewById(0);

        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);
    }

    public void bindPlaceView(Context context, Place place, int unitSystem, Location currentLocation) {
        this.place = place;
        placeName = place.getName();
        placeAddress = place.getAddress().getName();
        placeAddLat = place.getAddress().getAddLat();
        placeAddLong = place.getAddress().getAddLong();

        txtPlaceName.setText(placeName);
        txtPlaceAddress.setText(placeAddress);

        float distanceValue = getLocalizedDistance(placeAddLat, placeAddLong, unitSystem,currentLocation);
        String textDistance = String.valueOf(distanceValue);
        switch (unitSystem){
            case MainActivity.UNIT_SYSTEM_METRIC:
                textDistance.concat(context.getString(R.string.unit_system_km));
                break;
            case MainActivity.UNIT_SYSTEM_US:
                textDistance.concat(context.getString(R.string.unit_system_mi));
                break;
        }
        txtPlaceDistance.setText("" + textDistance);
        ;
        if (iVPlaceImage != null) {
            iVPlaceImage.setImageBitmap(null);  // TODO: fix dummy code with picasso
        }
    }
    private float getLocalizedDistance(double destLat, double destLong, int unitSystem, Location homeLocation) {
        float distanceLocalized;
        Location remotePlace = new Location(txtPlaceName.getText().toString());
        remotePlace.setLatitude(destLat);
        remotePlace.setLongitude(destLong);

        /*
        Location localPlace = new Location("Current");
        localPlace.setLatitude(homeLocation.latitude);
        localPlace.setLongitude(homeLocation.longitude);
        */
        float distanceMeters = homeLocation.distanceTo(remotePlace);
        //TODO: Implement Google Maps Direction API (https://developers.google.com/maps/documentation/directions/)
        switch (unitSystem) {
            case MainActivity.UNIT_SYSTEM_US:
                distanceLocalized = distanceMeters / 1609.344f; // distance in miles
                break;
            case MainActivity.UNIT_SYSTEM_METRIC:
                distanceLocalized = distanceMeters / 1000f;   // distance in km
                break;
            default:
                distanceLocalized = distanceMeters / 1000f;
                break;
        }
        return distanceLocalized;
    }

    @Override
    public void onClick(View view) {
        Log.d("onClick", "Clicked on item " + view.getId());
        // Send the event to the host activity
        mCallback.onPlaceItemSelected(this.place);
    }

    @Override
    public boolean onLongClick(View view) {
        Log.d("onLongClick", "Will share " + view.getRootView().toString());
        return true;
    }

}