package com.yum_driver.Fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yum_driver.Adapters.TabAdapter;
import com.yum_driver.R;
import com.google.android.material.tabs.TabLayout;


public class DeliveryFragment extends Fragment {
    Context context;
    TabAdapter adapter;
    ViewPager viewPager;
    TabLayout tabLayout;


    public DeliveryFragment(Context context) {
        this.context= context;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_delivery, container, false);

        tabLayout=v.findViewById(R.id.tabLayout);
        viewPager=v.findViewById(R.id.viewPager);
        tabLayout.addTab(tabLayout.newTab().setText(""+getResources().getString(R.string.active)));
        tabLayout.addTab(tabLayout.newTab().setText(""+getResources().getString(R.string.complete)));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        adapter = new TabAdapter(context,getActivity().getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.setSaveFromParentEnabled(false);


        return v;
    }
    @Override
    public void onResume() {
        super.onResume();

    }
}