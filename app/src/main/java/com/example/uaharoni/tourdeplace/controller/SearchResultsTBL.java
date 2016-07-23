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

public class SearchResultsTBL extends PlacesDB {

    public SearchResultsTBL(Context context) {
        super(context);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.i("onCreate_SearchResults", "inside onCreate");
        String sqlCreateTable = "CREATE TABLE " +
                TBL_NAME_SEARCH + "("
                + COL_ID + INTEGER_TYPE + PRIMARY_KEY
                + "," + COL_NAME + TEXT_TYPE
                + "," + COL_ADD_NAME + TEXT_TYPE
                + "," + COL_ADD_LAT + REAL_TYPE
                + "," + COL_ADD_LONG + REAL_TYPE
                + "," + COL_GPLACEID + TEXT_TYPE
                + "," + COL_GPLACEICON_URL + TEXT_TYPE
                + "," + COL_RATING + REAL_TYPE
                + ")";
        try {
            Log.d("onCreate", "Running command " + sqlCreateTable);
            sqLiteDatabase.execSQL(sqlCreateTable);
        } catch (Exception e) {
            Log.d("onCreate_SearchTBL", "Error creating table. " + e.getMessage());
        }
        super.onCreate(sqLiteDatabase);
    }
    public long insertPlace(@NonNull Place place){
        return (super.insertPlace(place,TBL_NAME_SEARCH));
    }
    public int deletePlace(@NonNull Place place){
        return (super.deletePlace(place,TBL_NAME_SEARCH));
    }
    public void deleteTBL(){
        super.deleteTBL(TBL_NAME_SEARCH);
    }
    public Place getPlaceById(long placeID){
        return (super.getPlaceById(placeID, TBL_NAME_SEARCH));
    }
    public ArrayList<Place> getAllPlaces(){
        return (super.getAllPlaces(TBL_NAME_SEARCH,null));
    }
    @Override
    protected Place parseCursorRow(Cursor cursor) {
        int id_index=-1,id_name=-1,id_addName=-1,id_addLat=-1,id_addLong=-1,id_gplaceID=-1,id_gPlaceIcon=-1,id_rating=-1;
        Place returnPlace = null;
        Log.d("parseCursor-SearchTBL","Parsing started");

            id_index = cursor.getColumnIndex(COL_ID);
            id_name = cursor.getColumnIndex(COL_NAME);
            id_addName = cursor.getColumnIndex(COL_ADD_NAME);
            id_addLat = cursor.getColumnIndexOrThrow(COL_ADD_LAT);
            id_addLong = cursor.getColumnIndexOrThrow(COL_ADD_LONG);
            id_gplaceID = cursor.getColumnIndex(COL_GPLACEID);
            id_gPlaceIcon = cursor.getColumnIndex(COL_GPLACEICON_URL);
            id_rating = cursor.getColumnIndex(COL_RATING);


        long placeId = cursor.getLong(id_index);
        String placeName = (id_name != -1) ? cursor.getString(id_name) : "N/A";
        String placeAddress = (id_addName != -1) ? cursor.getString(id_addName) : null;
        double placeAddress_lat = (id_addLat != -1) ? cursor.getDouble(id_addLat)  :  0;
        double placeAddress_long = (id_addLong != -1) ? cursor.getDouble(id_addLong)  :  0;
        String gPlaceId = (id_gplaceID != -1) ? cursor.getString(id_gplaceID) : null;
        String gPlaceIconUrl = (id_gPlaceIcon != -1) ? cursor.getString(id_gPlaceIcon) : null;
        float placeRating = (id_rating != -1) ? cursor.getFloat(id_rating)  :  0f;;
        Log.d("parseCursorRow", "obtained info on place " + placeName + "[" + placeAddress + ":" + placeAddress_lat +","+placeAddress_long + "]");

        try {
            returnPlace = new Place(
                    placeName
                    , new Address(placeAddress, placeAddress_lat, placeAddress_long)
                    , gPlaceId
                    , gPlaceIconUrl
            );
            returnPlace.setId(placeId);
            returnPlace.setPlaceRating(placeRating);
            Log.d("parseCursorRow","Created place " + returnPlace.getName() +",Address: " + returnPlace.getAddress().getAddLat() + "," + returnPlace.getAddress().getAddLong() );

        } catch (Exception e) {
            Log.d("parseCursorRow", "Error creating Place. " + e.getMessage());
        }

        return returnPlace;
    }

    @Override
    protected ContentValues extractPlace(Place place) {
        ContentValues values = new ContentValues();
        values.put(COL_NAME, place.getName());
        values.put(COL_ADD_NAME, place.getAddress().getName());
        values.put(COL_ADD_LAT,place.getAddress().getAddLat());
        values.put(COL_ADD_LONG,place.getAddress().getAddLong());
        values.put(COL_GPLACEID,place.getgPlaceId());
        values.put(COL_GPLACEICON_URL,place.getPlaceIconUrl());
        values.put(COL_RATING,place.getPlaceRating());

        return values;
    }
    protected String[] getSelectedColums(){
        return(new String[] {COL_ID,COL_NAME,COL_ADD_NAME,COL_ADD_LAT,COL_ADD_LONG,COL_GPLACEID,COL_GPLACEICON_URL,COL_RATING});
    }
}
