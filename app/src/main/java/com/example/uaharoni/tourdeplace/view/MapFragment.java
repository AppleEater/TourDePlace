package com.example.uaharoni.tourdeplace.view;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.uaharoni.tourdeplace.R;
import com.example.uaharoni.tourdeplace.model.Place;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    public static GoogleMap mMap;
    private final int LOCATION_REQUEST_CODE = 1;
    private final int PROVIDER_DISABLED_REQUEST_CODE = 2;

    public static final String BUNDLE_KEY = "PLACE";
    private static Marker currentLocationMarker;


    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("onCreateView-MapFrag", "Launching MapFragment");
        View layoutView = null;
        try{
            layoutView = inflater.inflate(R.layout.fragment_map, container, false);
            SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
            supportMapFragment.getMapAsync(this);
        } catch (Exception e){
            Log.e("onCreateView-MapFrag","Error inflating map inner fragment. " + e.getMessage());
        }

        return layoutView;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d("onMapReady", "Map successfully loaded");
        mMap = googleMap;
        Location mapLocation = null;

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setBuildingsEnabled(false);
        mMap.setIndoorEnabled(true);
        mMap.setTrafficEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);


        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.d("onMapReady", "Map has permissions for location");
            mMap.setMyLocationEnabled(true);
        }
        setCurrentLocation(MainActivity.currentLocation);

    }

    public void setCurrentLocation(@Nullable Location updatedLocation) {
        if(updatedLocation != null){
            Log.d("setCurrntLcatn-MapFrag", "Moving to location: " + updatedLocation.toString());
            LatLng geoCoordinates = new LatLng(updatedLocation.getLatitude(),updatedLocation.getLongitude());
            refreshMap(geoCoordinates);

            if (currentLocationMarker != null) {
                currentLocationMarker.remove();
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(geoCoordinates)
                        .title(getString(R.string.marker_current_location))
                        .alpha(0.7f)
                        .icon(BitmapDescriptorFactory
                                .defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)
                        );
                if(mMap != null){
                    Log.d("setCurLoc-MapFrag","Adding Marker");
                    currentLocationMarker = mMap.addMarker(markerOptions);
                }

            }

        }
    }
    public void refreshMap(@NonNull LatLng location) {
        CameraPosition cameraPosition = new CameraPosition.Builder().zoom(19).tilt(20).target(location).build();
        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
        if(mMap != null){
            Log.d("refreshMap","Moving the map to a new location: " + location.toString());
            mMap.moveCamera(cameraUpdate);
        }
    }

    public void addPlaceMarker(@NonNull Place place) {
        String name = place.getName();
        String address = place.getAddress().getName();
        double lat = place.getAddress().getAddLat();
        double lng = place.getAddress().getAddLong();
        String iconUrl = place.getPlaceIconUrl();
        //TODO: Bring URL in AsyncTask

        MarkerOptions markerOptions = new MarkerOptions()
                .position(new LatLng(lat, lng))
                .title(name)
                .alpha(0.9f)
                .snippet(address)
                .icon(BitmapDescriptorFactory.fromPath("https://developers.google.com/maps/documentation/javascript/examples/full/images/beachflag.png"));
        // the icon is expermiental
        if(mMap != null){
            mMap.addMarker(markerOptions);
        }
    }


}
