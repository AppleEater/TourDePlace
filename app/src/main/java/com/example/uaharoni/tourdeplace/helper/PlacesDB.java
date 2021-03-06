package com.example.uaharoni.tourdeplace.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.uaharoni.tourdeplace.model.Place;

import java.util.ArrayList;

public abstract class PlacesDB extends SQLiteOpenHelper implements BaseColumns {
    protected static final int DB_VER = 1;
    protected static final String DB_NAME = "places.sqlite";
    public static final String TBL_NAME_SEARCH = "search";
    public static final String TBL_NAME_FAVORITES = "favorites";

    public final String TEXT_TYPE = " TEXT";
    public final String INTEGER_TYPE = " INTEGER";
    public final String REAL_TYPE = " REAL";
    public final String DATETIME_TYPE = " DATETIME";
    public final String COL_NULLABLE = null;
    public final String PRIMARY_KEY = " PRIMARY KEY AUTOINCREMENT";

    public static final String COL_ID = BaseColumns._ID;
    public static final String COL_NAME = "name";

    public static final String COL_ADD_LAT = "add_lat";
    public static final String COL_ADD_LONG = "add_long";
    public static final String COL_ADD_NAME = "add_name";
    public static final String COL_GPLACEID = "gplace_id";
    public static final String COL_GPLACEICON_URL = "gplace_icon_url";
    public static final String COL_RATING = "rating";

    private final String sqlCreateTableSearch =  "CREATE TABLE " +
            TBL_NAME_SEARCH + " ("
            + COL_ID + INTEGER_TYPE + PRIMARY_KEY
            +  "," + COL_NAME + TEXT_TYPE
            +  "," +  COL_ADD_NAME + TEXT_TYPE
            + "," + COL_ADD_LAT + REAL_TYPE
            + "," + COL_ADD_LONG + REAL_TYPE
            + "," + COL_GPLACEID + TEXT_TYPE
            + "," + COL_GPLACEICON_URL + TEXT_TYPE
            + "," + COL_RATING + REAL_TYPE
            + ")";

    private final String sqlCreateTableFav =  "CREATE TABLE  " +
            TBL_NAME_FAVORITES + " ("
            + COL_ID + INTEGER_TYPE + PRIMARY_KEY
            +  "," + COL_NAME + TEXT_TYPE
            +  "," +  COL_ADD_NAME + TEXT_TYPE
            + "," + COL_ADD_LAT + REAL_TYPE
            + "," + COL_ADD_LONG + REAL_TYPE
            + "," + COL_GPLACEID + TEXT_TYPE
            + "," + COL_GPLACEICON_URL + TEXT_TYPE
            + ")";


    public static final String LOG = "placesDbHelper";

    public PlacesDB(Context context) {
        super(context, DB_NAME, null, DB_VER);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.d("onCreate-PlacesDB","Super constructor is running. Creating tables...");
        Log.d("onCreate", "Running command " + sqlCreateTableSearch);
        sqLiteDatabase.execSQL(sqlCreateTableSearch);
        Log.d("onCreate", "Running command " + sqlCreateTableFav);
        sqLiteDatabase.execSQL(sqlCreateTableFav);
    }
    protected void deleteTBL(String tblName){
        Log.d("deleteTBL-PlaceDB","Deleting table " + tblName);
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.delete(tblName,null,null);
            db.close();
        } catch (Exception e) {
            Log.e("deleteTBL-PlaceDB", "Failed to delete table " + tblName + ". " + e.getMessage());
        }
    }
    protected Place getPlaceById( long rowid, @NonNull String tblName) {
        Place place = null;
        String[] projection = {}; // null on purpose, to include all fields
        String selection = tblName + "." + COL_ID + " = ?";
        String[] selectionArgs = {String.valueOf(rowid)};
        String sortOrder = null;    // We don't resort the rows, as we keep the same order we got from google

        Log.d("getPlaceById-PlacesDB", "Opening table " + tblName + " to read row " + String.valueOf(rowid));
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor singleRow = db.query(tblName, projection, selection, selectionArgs, null, null,  sortOrder);
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
    public long insertPlace(@NonNull Place place, @NonNull String tblName){
        long rowid=0;
        Log.d("insertPlace-PlacesDB","table: " + tblName + " -  Inserting Place " + place.getName() + "[" + place.getAddress().getAddLat() +"," + place.getAddress().getAddLong() + "]");
        ContentValues updatedValues = extractPlace(place);
        try{
            SQLiteDatabase db = this.getWritableDatabase();
            rowid = db.insertWithOnConflict(tblName,COL_NULLABLE,updatedValues,SQLiteDatabase.CONFLICT_IGNORE);
            db.close();
        } catch (Exception e) {
            Log.e("insertPlace-PlacesDB","Error Inserting " + place.getName() + " to table " + tblName + ". " + e.getMessage());
        }
        Log.d("insertPlace-PlacesDB","Added place to row: " + rowid);
        return  rowid;
    }
    public int deletePlace(@NonNull Place place, @NonNull String tblName){
        int linesReturned = 0;
        long rowid = place.getId();
        Log.d("deletePlace-PlacesDB","Delete place " + place.getName() + " (" + rowid + ") from table " + tblName);
        String where = COL_ID + " =  ?";
        String[] whereArgs = {String.valueOf(rowid)};
        if(place != null) {
            try{
                SQLiteDatabase db = this.getWritableDatabase();
                linesReturned = db.delete(tblName,where,whereArgs);
                db.close();
            } catch (Exception e){
                Log.e("deletePlace-PlacesDB","Error deleting place. " + e.getMessage());
            }
            Log.d("deletePlace-PlacesDB","deleted rows: " + linesReturned);
        } else {
            return 0;
        }
        return linesReturned;
    }
    public ArrayList<Place> getAllPlaces(@NonNull String tblName, @Nullable String columnSorting){
        SQLiteDatabase db = null;
        ArrayList<Place> placesList = new ArrayList<>();
        Cursor crsrResults;
        Log.d("getPlacesArray-PlacesDB","Fetching data from " + tblName);
        String[] columnsList = getSelectedColums();
        String[] selectionArgs={};

    Log.d("getPlacesArray-PlacesDB","Open Database");
        db = getReadableDatabase();
        if (db != null){
            Log.d("getPlacesArray-PlacesDB","Query table " + tblName);
            crsrResults = db.query(tblName,columnsList,null,selectionArgs,null,null,columnSorting,null);
            if(crsrResults.moveToFirst()){
                // looping through all rows and adding to list
                do {
                    Place place = parseCursorRow(crsrResults);
                    placesList.add(place);
                }while (crsrResults.moveToNext());
            }
            db.close();
            Log.d("getAllPlaces","Results found: " + crsrResults.getCount());
        } else {
            Log.d("getAllPlaces","Failed to open DB");
        }
        return placesList;
    }
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        Log.i("onUpgrade_PlacesDB","inside onUpgrade");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TBL_NAME_SEARCH);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TBL_NAME_FAVORITES);
        onCreate(sqLiteDatabase);
    }

    protected abstract Place parseCursorRow(Cursor cursor);
    protected abstract ContentValues extractPlace(Place place);
    protected abstract String[] getSelectedColums();

}
