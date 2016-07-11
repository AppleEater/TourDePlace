package com.example.uaharoni.tourdeplace.controller;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    private ArrayList<Fragment> fragments = new ArrayList<>();
    private ArrayList<String> tabTitles = new ArrayList<>();

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
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
        return fragments.get(position);
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
