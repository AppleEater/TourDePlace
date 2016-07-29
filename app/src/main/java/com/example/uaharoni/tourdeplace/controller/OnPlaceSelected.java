package com.example.uaharoni.tourdeplace.controller;

import com.example.uaharoni.tourdeplace.model.Place;

// Container Activity must implement this interface
public interface OnPlaceSelected {
    public void onDisplayPlaceOnMap(Place place);
}
