package com.yum_driver.Activities;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentTransaction;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.MenuItem;

import com.yum_driver.Fragments.DeliveryFragment;
import com.yum_driver.Fragments.FragNotifications;
import com.yum_driver.Fragments.FragProfile;
import com.yum_driver.Fragments.OrderListFragment;
import com.yum_driver.utils.BottomNavigationViewHelper;
import com.yum_driver.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends BaseActivity {

    BottomNavigationView navigation;

    private long mLastClickTime = 0;
    private long WaitTime =1500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        BottomNavigationViewHelper.disableShiftMode(navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        boolean fromRegister = getIntent().getBooleanExtra("fromRegister",false);

        OrderListFragment fragment1 = new OrderListFragment(MainActivity.this,fromRegister);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer,fragment1);
        transaction.addToBackStack(null);
        transaction.commit();
    }
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.dashboard:

                    if (SystemClock.elapsedRealtime() - mLastClickTime < WaitTime){
                        return true;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();

                    OrderListFragment fragment1 = new OrderListFragment(MainActivity.this,false);
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragmentContainer,fragment1);
                    transaction.addToBackStack(null);
                    transaction.commit();

                    return true;
                case R.id.history:

//                    if (SystemClock.elapsedRealtime() - mLastClickTime < WaitTime){
//                        return false;
//                    }
//                    mLastClickTime = SystemClock.elapsedRealtime();


                    DeliveryFragment fragment = new DeliveryFragment(MainActivity.this);
                    FragmentTransaction transaction4 = getSupportFragmentManager().beginTransaction();
                    transaction4.replace(R.id.fragmentContainer,fragment);
                    transaction4.addToBackStack(null);
                    transaction4.commit();
                    return true;
                case R.id.notification:

//                    if (SystemClock.elapsedRealtime() - mLastClickTime < WaitTime){
//                        return false;
//                    }
//                    mLastClickTime = SystemClock.elapsedRealtime();

                    FragNotifications fragment2 = new FragNotifications(MainActivity.this);
                    FragmentTransaction transaction2 = getSupportFragmentManager().beginTransaction();
                    transaction2.replace(R.id.fragmentContainer,fragment2);
                    transaction2.addToBackStack(null);
                    transaction2.commit();
                    return true;

                case R.id.profile:

                    if (SystemClock.elapsedRealtime() - mLastClickTime < WaitTime){
                        return true;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();

                    FragProfile fragment3 = new FragProfile(MainActivity.this);
                    FragmentTransaction transaction3 = getSupportFragmentManager().beginTransaction();
                    transaction3.replace(R.id.fragmentContainer,fragment3);
                    transaction3.addToBackStack(null);
                    transaction3.commit();
                    return true;

            }
            return false;
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {

        if(navigation.getSelectedItemId()==R.id.dashboard)
        {
            exitDialog();
        }
        else
        {
            navigation.setSelectedItemId(R.id.dashboard);
            OrderListFragment fragment1 = new OrderListFragment(MainActivity.this,false);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragmentContainer,fragment1);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }
    private void exitDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.exit));
        builder.setMessage(getResources().getString(R.string.are_you_sure));
        builder.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                finishAffinity();
            }
        });
        builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {

            }
        });
        builder.show();
    }
}