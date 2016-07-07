package com.example.uaharoni.tourdeplace.controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.CancellationSignal;
import android.provider.BaseColumns;
import android.util.Log;

import com.example.uaharoni.tourdeplace.model.Place;

import java.util.ArrayList;

public abstract class PlacesDB extends SQLiteOpenHelper implements BaseColumns {
    private static final int DB_VER = 1;
    private static final String DB_NAME = "places.db";
    public static final String TBL_NAME_SEARCH = "last_search";
    public static final String TBL_NAME_FAVORITES = "favorites";

    String TEXT_TYPE = " TEXT";
    String INTEGER_TYPE = " INTEGER";
    String REAL_TYPE = " REAL";
    String DATETIME_TYPE = " DATETIME";
    String COL_NULLABLE = null;
    String PRIMARY_KEY = " PRIMARY KEY AUTOINCREMENT";

    public static final String COL_ID = BaseColumns._ID;
    public static final String COL_NAME = "name";

    public static final String COL_ADD_LAT = "add_lat";
    public static final String COL_ADD_LONG = "add_long";
    public static final String COL_ADD_NAME = "add_name";
    public static final String COL_GPLACEID = "gplace_id";
    public static final String COL_GPLACEICON_URL = "gplace_icon_url";

    // Logcat tag
    public static final String LOG = "placesDbHelper";

    public PlacesDB(Context context) {
        super(context, DB_NAME, null, DB_VER);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
    }
    protected void deleteTBL(String tblName){
        Log.d("deleteTBL-PlaceDB","Deleting table " + tblName);
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            String sqlFormat = String.format("DROP TABLE IF EXISTS %s", tblName);
            db.execSQL(sqlFormat);
            db.close();
        } catch (Exception e) {
            Log.e("deleteTBL-PlaceDB", "Failed to delete table " + tblName + ". " + e.getMessage());
        }
    }
    protected Place getPlaceById(long rowid, String tblName) {
        Place place = null;
        String[] projection = {}; // null on purpose, to include all fields
        String selection = tblName + "." + COL_ID + " = ?";
        String[] selectionArgs = {String.valueOf(rowid)};
        String sortOrder = null;    // We don't resort the rows, as we keep the same order we got from google

        Log.d("getPlaceById-PlacesDB", "Opening table " + tblName + " to read row " + String.valueOf(rowid));
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor singleRow = db.query(tblName, projection, selection, selectionArgs, null, null, sortOrder);
            if (singleRow.getCount() > 0) {
                place = parseCursorRow(singleRow);
            } else {
                Log.e("getPlaceById-PlacesDB", "Row " + rowid + " not found.");
            }
            db.close();
        } catch (Exception e) {
            Log.d("getPlaceById-PlacesDB","Error fetching place. " + e.getMessage());
        }

        return place;
    }
    protected long insertPlace(Place place, String tblName){
        long rowid=0;
        Log.d("insertPlace-PlacesDB","Inserting Place " + place.getName() + " to table " + tblName);
        ContentValues updatedValues = extractPlace(place);
        try{
            SQLiteDatabase db = this.getWritableDatabase();
            rowid = db.insert(tblName, null, updatedValues);
            db.close();
        } catch (Exception e) {
            Log.e("insertPlace-PlacesDB","Error Inserting " + place.getName() + " to table " + tblName + ". " + e.getMessage());
        }
        return  rowid;
    }
    protected int deletePlace(Place place, String tblName){
        int linesReturned = 0;
        long rowid = place.getId();
        Log.d("deletePlace","Delete place " + place.getName() + " (" + rowid + ") from table " + tblName);
        String where = COL_ID;
        String[] whereArgs = {String.valueOf(rowid)};
        if(place != null) {
            try{
                SQLiteDatabase db = this.getWritableDatabase();
                linesReturned = db.delete(tblName,where,whereArgs);
                db.close();
            } catch (Exception e){
                Log.e("deletePlace-PlacesDB","Error deleting place. " + e.getMessage());
            }
        } else {
            return 0;
        }
        return linesReturned;
    }
    protected ArrayList<Place> getAllPlaces(String tblName, String columnSorting){
        ArrayList<Place> placesList = new ArrayList<>();
        Log.d("getPlacesArray-PlacesDB","Fetching data from " + tblName);
        String[] columnsList = getSelectedColums();
        String selection = null;
        String[] selectionArgs={};
        String groupBy = null,having = null;
        String orderBy = columnSorting;
        String limit = null;
        CancellationSignal cancellationSignal = null;
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor crsrResutls = db.queryWithFactory(null,true,tblName,columnsList,selection,selectionArgs,groupBy,having,orderBy,limit);
            // looping through all rows and adding to list
            if(crsrResutls.moveToFirst()){
                do {
                    Place place = parseCursorRow(crsrResutls);
                    placesList.add(place);
                }while (crsrResutls.moveToNext());
            }
            db.close();
        } catch (Exception e){
            Log.e("getPlacesArray-PlacesDB","Failed to bring table info. " + e.getMessage());
        }
        return placesList;
    }
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        Log.i("onUpgrade_PlacesDB","inside onUpgrade");
    }

    protected abstract Place parseCursorRow(Cursor cursor);
    protected abstract ContentValues extractPlace(Place place);
    protected abstract String[] getSelectedColums();

}
