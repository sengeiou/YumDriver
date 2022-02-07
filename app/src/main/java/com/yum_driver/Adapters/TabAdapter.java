package com.yum_driver.Adapters;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.yum_driver.Fragments.ActiveOrderFrag;
import com.yum_driver.Fragments.ClosedOrderFrag;


public class TabAdapter extends FragmentStatePagerAdapter {

    private Context myContext;
    int totalTabs;

    public TabAdapter(Context context, FragmentManager fm, int totalTabs) {
        super(fm);
        myContext = context;
        this.totalTabs = totalTabs;
        System.out.println(totalTabs);
    }

    // this is for fragment tabs  
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                ActiveOrderFrag activeOrderFrag = new ActiveOrderFrag(myContext);
                return activeOrderFrag;
            case 1:
                ClosedOrderFrag closedOrderFrag = new ClosedOrderFrag(myContext);
                return closedOrderFrag;
            default:
                return null;
        }
    }
    // this counts total number of tabs  
    @Override
    public int getCount() {
        return totalTabs;
    }
}  
