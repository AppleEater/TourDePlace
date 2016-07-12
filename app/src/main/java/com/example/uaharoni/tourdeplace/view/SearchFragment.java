package com.example.uaharoni.tourdeplace.view;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.uaharoni.tourdeplace.R;
import com.example.uaharoni.tourdeplace.controller.SearchResultsTBL;
import com.example.uaharoni.tourdeplace.model.Place;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SearchFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class SearchFragment extends Fragment {

    private SearchResultsTBL searchDbHelper;

    private OnFragmentInteractionListener mListener;
    private ShareActionProvider shareProvider;

    public SearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("onCreateView-SearchFrag","parsing bundle");
        searchDbHelper = new SearchResultsTBL(getContext());
        // Inflate the layout for this fragment
        View searchView = inflater.inflate(R.layout.fragment_search, container, false);

    //    RecyclerView recyclerView = (RecyclerView) searchView.findViewById(R.id.rv_search);
     //   PlaceListAdapter searchAdapter = new PlaceListAdapter(searchDbHelper.getAllPlaces(),R.id.rv_search,currentLocation);

//        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//        recyclerView.setAdapter(searchAdapter);

        return searchView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.share_place,menu);
        shareProvider = (ShareActionProvider)menu.findItem(R.id.action_share);
        return;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_share:
                doSharePlace((Place) item);
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
        intent.putExtra(getString(R.string.settings_last_location_latitude),place.getAddress().getAddLat());
        intent.putExtra(getString(R.string.settings_last_location_longitude),place.getAddress().getAddLong());
        intent.putExtra("PLACE_NAME",place.getName());
        shareProvider.setShareIntent(intent);
}
}