package com.food4thought.test.adapter;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;


import com.food4thought.test.ui.fragments.restuarantdetails.NoteFragment;
import com.food4thought.test.ui.fragments.restuarantdetails.RestaurantDetailsFragment;
import com.food4thought.test.ui.fragments.restuarantdetails.ReviewFragment;

/**
 * Created by samwdp on 13/04/2016.
 */
public class PageAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public PageAdapter(FragmentManager fm,int numTabs) {
        super(fm);
        this.mNumOfTabs = numTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                RestaurantDetailsFragment restaurantDetailsFragment = new RestaurantDetailsFragment();
                return restaurantDetailsFragment;
            case 1:
                ReviewFragment reviewFragment = new ReviewFragment();
                return reviewFragment;
            case 2:
                NoteFragment noteFragment = new NoteFragment();
                return noteFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}