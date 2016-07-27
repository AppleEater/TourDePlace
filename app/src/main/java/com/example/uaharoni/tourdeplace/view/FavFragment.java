package com.example.uaharoni.tourdeplace.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.uaharoni.tourdeplace.DividerItemDecoration;
import com.example.uaharoni.tourdeplace.R;
import com.example.uaharoni.tourdeplace.controller.FavsTBL;
import com.example.uaharoni.tourdeplace.controller.PlacesAdapter;
import com.example.uaharoni.tourdeplace.model.Place;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FavFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class FavFragment extends Fragment {

    private FavsTBL FavDbHelper;
    private PlacesAdapter recyclerAdapter;
    private RecyclerView recyclerView;
    private ShareActionProvider shareView;


    private String TAG = "FavFrag";


    public FavFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FavDbHelper = new FavsTBL(getContext());
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "Inflating View");
        View favFragLayout = inflater.inflate(R.layout.fragment_fav, container, false);

        recyclerView = (RecyclerView) favFragLayout.findViewById(R.id.rv_fav);
        refreshAdapter();
        //recyclerAdapter = new PlacesAdapter(getContext(), FavDbHelper.getAllPlaces());
        //recyclerView.setAdapter(recyclerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        RecyclerView.ItemDecoration itemDecoration =
                new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL);
        recyclerView.addItemDecoration(itemDecoration);


        // Inflate the layout for this fragment
        return favFragLayout;
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.share_place, menu);

        MenuItem shareItem = menu.findItem(R.id.action_share);
        shareView = (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d("onOptionsItemSelecd", "Selected item " + item.toString());
        switch (item.getItemId()) {
            case R.id.action_share:
                //TODO: Get the selected place
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
    public void doSharePlace(Place place) {
        // populate the share intent with data
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(getString(R.string.settings_last_location_latitude), place.getAddress().getAddLat());
        intent.putExtra(getString(R.string.settings_last_location_longitude), place.getAddress().getAddLong());
        intent.putExtra("PLACE_NAME", place.getName());
        shareView.setShareIntent(intent);
    }
    public void refreshAdapter() {
        Log.d("refreshAdapter-FavsFrag", "Refreshing RecyclerView adapter...");
        recyclerAdapter = new PlacesAdapter(getContext(), FavDbHelper.getAllPlaces());
        recyclerView.setAdapter(recyclerAdapter);
    }

}
