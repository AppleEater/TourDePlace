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
                + ")";
        Log.d("onCreate", "Running command " + sqlCreateTable);
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
        Log.d("parseCursorRow", "Parsing cursor " + cursor.toString());

        int id_index = cursor.getColumnIndex(COL_ID);
        int id_name = cursor.getColumnIndex(COL_NAME);
        int id_addName = cursor.getColumnIndex(COL_ADD_NAME);
        int id_addLat = cursor.getColumnIndex(COL_ADD_LAT);
        int id_addLong = cursor.getColumnIndex(COL_ADD_LONG);
        int id_gplaceID = cursor.getColumnIndex(COL_GPLACEID);
        int id_gPlaceIcon = cursor.getColumnIndex(COL_GPLACEICON_URL);

        cursor.moveToFirst();

        long placeId = cursor.getLong(id_index);
        String placeName = cursor.getString(id_name);
        String placeAddress = cursor.getString((id_addName!=-1)?id_addName:null);
        Long placeAddress_lat = cursor.getLong((id_addLat!=-1)?id_addLat:0);
        Long placeAddress_long = cursor.getLong((id_addLong!=-1)?id_addLong:0);
        String gPlaceId = cursor.getString((id_gplaceID!=-1)?id_gplaceID:null);
        String gPlaceIconUrl = cursor.getString((id_gPlaceIcon!=-1)?id_gPlaceIcon:null);

        return (new Place(
                placeName
                , new Address(placeAddress, placeAddress_lat, placeAddress_long)
                , placeId
                , gPlaceId
                , gPlaceIconUrl
        )
        );
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

        return values;
    }
    protected String[] getSelectedColums(){
        return(new String[] {COL_ID,COL_NAME,COL_ADD_NAME,COL_GPLACEICON_URL});
    }
}
