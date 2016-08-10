package com.example.uaharoni.tourdeplace.controller;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import java.util.ArrayList;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    private ArrayList<Fragment> fragments = new ArrayList<>();
    private ArrayList<String> tabTitles = new ArrayList<>();

    public ViewPagerAdapter(FragmentManager fm) {

        super(fm);
        Log.d("ViewPagerAdapter","Constructor...");
    }

    public int addFragment(Fragment fragment, String tabTitle){
        Bundle args = new Bundle();
//        args.putInt(fragment.ARG_OBJECT, fragments.size());
        fragment.setArguments(args);
        this.fragments.add(fragment);
        this.tabTitles.add(tabTitle);
        return (fragments.size()-1);
    }


    @Override
    public Fragment getItem(int position) {
        if(position != -1) {
            return fragments.get(position);
        } else {
            return null;
        }
    }
    @Override
    public CharSequence getPageTitle(int position) {
        if(position<fragments.size()){
            return tabTitles.get(position);
        } else {
            return super.getPageTitle(position);
        }
    }
    @Override
    public int getCount() {
        return fragments.size();
    }
}
