package com.yum_driver.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class NetworkStateChangeObserver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String status = NetworkUtil.getConnectivityStatusString(context);
        if(status.isEmpty()) {
            status="Internet is unavailable";
        }

        if (status.equalsIgnoreCase("Internet is unavailable")) {
            Toast.makeText(context, status, Toast.LENGTH_LONG).show();
        }
    }
}
