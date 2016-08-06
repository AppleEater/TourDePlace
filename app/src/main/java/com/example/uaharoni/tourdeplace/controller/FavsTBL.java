package com.example.uaharoni.tourdeplace.controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.util.Log;
import com.example.uaharoni.tourdeplace.helper.PlacesDB;
import com.example.uaharoni.tourdeplace.model.Address;
import com.example.uaharoni.tourdeplace.model.Place;
import java.util.ArrayList;

public class FavsTBL extends PlacesDB {
    public FavsTBL(Context context) {
        super(context);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        super.onCreate(sqLiteDatabase);
        Log.i("onCreate_FavsTBL", "Local empty onCreate");
    }
    public long insertPlace(@NonNull Place place){
        return (super.insertPlace(place,TBL_NAME_FAVORITES));
    }
    public int deletePlace(@NonNull Place place){
        return (super.deletePlace(place,TBL_NAME_FAVORITES));
    }
    public void deleteTBL(){
        super.deleteTBL(TBL_NAME_FAVORITES);
    }
    public Place getPlaceById(long placeID){
        return (super.getPlaceById(placeID, TBL_NAME_FAVORITES));
    }
    public ArrayList<Place> getAllPlaces(){
        return (super.getAllPlaces(TBL_NAME_FAVORITES,COL_NAME));
    }
    @Override
    protected Place parseCursorRow(Cursor cursor) {
        int id_index = -1, id_name = -1, id_addName = -1, id_addLat = -1, id_addLong = -1, id_gplaceID = -1, id_gPlaceIcon = -1;
        Place returnPlace = null;
        Log.d("parseCursor-FavsTBL", "Parsing started");

        id_index = cursor.getColumnIndex(COL_ID);
        id_name = cursor.getColumnIndex(COL_NAME);
        id_addName = cursor.getColumnIndex(COL_ADD_NAME);
        id_addLat = cursor.getColumnIndexOrThrow(COL_ADD_LAT);
        id_addLong = cursor.getColumnIndexOrThrow(COL_ADD_LONG);
        id_gplaceID = cursor.getColumnIndex(COL_GPLACEID);
        id_gPlaceIcon = cursor.getColumnIndex(COL_GPLACEICON_URL);


        long placeId = cursor.getLong(id_index);
        String placeName = (id_name != -1) ? cursor.getString(id_name) : "N/A";
        String placeAddress = (id_addName != -1) ? cursor.getString(id_addName) : null;
        double placeAddress_lat = (id_addLat != -1) ? cursor.getDouble(id_addLat) : 0;
        double placeAddress_long = (id_addLong != -1) ? cursor.getDouble(id_addLong) : 0;
        String gPlaceId = (id_gplaceID != -1) ? cursor.getString(id_gplaceID) : null;
        String gPlaceIconUrl = (id_gPlaceIcon != -1) ? cursor.getString(id_gPlaceIcon) : null;

        try {
            returnPlace = new Place(
                    placeName
                    , new Address(placeAddress, placeAddress_lat, placeAddress_long)
                    , gPlaceId
                    , gPlaceIconUrl
            );
            returnPlace.setId(placeId);
            Log.d("parseCursorRow", "Created place " + returnPlace.getName() + ",Address: " + returnPlace.getAddress().getAddLat() + "," + returnPlace.getAddress().getAddLong());
        } catch (Exception e) {
            Log.d("parseCursorRow", "Error creating Place. " + e.getMessage());
        }
        return returnPlace;
    }

        @Override
    protected ContentValues extractPlace(@NonNull Place place) {
        ContentValues values = new ContentValues();
        values.put(COL_NAME, place.getName());
        values.put(COL_ADD_NAME, place.getAddress().getName());
        values.put(COL_ADD_LAT,place.getAddress().getAddLat());
        values.put(COL_ADD_LONG,place.getAddress().getAddLong());
        values.put(COL_GPLACEID,place.getgPlaceId());
        values.put(COL_GPLACEICON_URL,place.getPlaceIconUrl());

        return values;
    }
    protected String[] getSelectedColums(){
        return(new String[] {COL_ID,COL_NAME,COL_ADD_NAME,COL_ADD_LAT,COL_ADD_LONG,COL_GPLACEICON_URL});
    }
}
