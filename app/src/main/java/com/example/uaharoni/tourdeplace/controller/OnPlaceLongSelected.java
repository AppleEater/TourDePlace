package com.example.uaharoni.tourdeplace.controller;

import com.example.uaharoni.tourdeplace.model.Place;

public interface OnPlaceLongSelected {
    public void onAddToFavorites (Place place);
    public void onSharePlace (Place place);
}
