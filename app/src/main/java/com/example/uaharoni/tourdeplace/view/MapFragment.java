package com.example.uaharoni.tourdeplace.view;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
    private SharedPreferences sharedPreferences;

    public static final String BUNDLE_KEY = "PLACE";
    private static Marker currentLocationMarker;
    private Place receivedPlace = null;


    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            receivedPlace = getArguments().getParcelable(BUNDLE_KEY);
        }
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("onCreateView-MapFrag", "Launching MapFragment");
        View layoutView = null;
        try{
            layoutView = inflater.inflate(R.layout.fragment_map, container, false);
            SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
            supportMapFragment.getMapAsync(this);
            //TODO: Test for MapView implementation instead of MapFragment
        } catch (Exception e){
            Log.e("onCreateView","Error inflating map inner fragment. " + e.getMessage());
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
        mMap = googleMap;

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setBuildingsEnabled(false);
        mMap.setIndoorEnabled(true);
        mMap.setTrafficEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);

        if(ContextCompat.checkSelfPermission(getContext(),Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }
        if(receivedPlace != null) {
            addPlaceMarker(receivedPlace);
        }



        /*
        if(ContextCompat.checkSelfPermission(getContext(),Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.d("onMapReady-FragMap","Have location permission. Getting location");
            getLocation();
        }
        if(ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),Manifest.permission.ACCESS_FINE_LOCATION)){
            //Explain to the user why we need to read the contacts
            Snackbar.make(getView(), getString(R.string.snackbar_location_permissions_needed), Snackbar.LENGTH_INDEFINITE)
                    .setAction(getString(R.string.snackbar_action_ok), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
                        }
                    })
                    .show();
        } else {
            ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
        }
        */

    }





    public void setCurrentLocation(@Nullable LatLng updatedLocation) {
        Log.d("setCurrntLcatn-MapFrag", "Got LatLng location");
        if (currentLocationMarker != null) {
            currentLocationMarker.remove();
        }
        refreshMap(updatedLocation);
        MarkerOptions markerOptions = new MarkerOptions()
                .position(updatedLocation)
                .title(getString(R.string.marker_current_location))
                .alpha(0.7f)
                .icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)
                );
        Log.d("setCurLoc-MapFrag","Adding Marker");
        currentLocationMarker = mMap.addMarker(markerOptions);
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
        mMap.addMarker(markerOptions);
    }

    public static void refreshMap(@NonNull LatLng location) {
        CameraPosition cameraPosition = new CameraPosition.Builder().zoom(19).tilt(20).target(location).build();
        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
        mMap.moveCamera(cameraUpdate);
    }
}
