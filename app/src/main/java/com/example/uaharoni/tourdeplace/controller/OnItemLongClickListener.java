package com.example.uaharoni.tourdeplace.controller;

import com.example.uaharoni.tourdeplace.model.Place;

public interface OnItemLongClickListener {
    public void onAddToFavorites (Place place);
    public void onSharePlace (Place place);
    public void onRemoveFromFavorites (Place place);

}
