package com.example.uaharoni.tourdeplace.view;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class SimpleTapbsAdapter extends FragmentPagerAdapter {

    private final ArrayList<Fragment> fragments = new ArrayList<>();
    private final ArrayList<String>tabTitles = new ArrayList<>();


    public SimpleTapbsAdapter(FragmentManager fm) {
        super(fm);
    }
   public void addFragment(Fragment fragment,String tabTitle){
       fragments.add(fragment);
       tabTitles.add(tabTitle);
   }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
    @Override
    public CharSequence getPageTitle(int position) {
        if(position<fragments.size()){
            return tabTitles.get(position);
        } else {
            return super.getPageTitle(position);
        }
    }
}
