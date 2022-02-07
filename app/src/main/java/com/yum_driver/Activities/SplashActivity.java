package com.yum_driver.Activities;

import androidx.annotation.RequiresApi;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import com.yum_driver.R;
import com.yum_driver.services.LocationService;
import com.yum_driver.utils.Constants;

public class SplashActivity extends BaseActivity {

    private boolean islogin = false;
    private static int SPLASH_SCREEN=4000;
    private Thread t;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        prefManager.connectDB();
        islogin = prefManager.getBoolean("isLogin");
        prefManager.closeDB();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (islogin)
                {
                    //Dashboard
                    Intent i = new Intent(SplashActivity.this,MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                    finish();

                }else {
                    Intent i = new Intent(SplashActivity.this,LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                    finish();
                }

            }
        },SPLASH_SCREEN);
        /* stopLocationService();*/

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startLocationService();
            }
        }, 1000);


    }

    private Boolean isLocationServiceRunning() {
        ActivityManager activityManager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);

        if (activityManager != null) {
            for (ActivityManager.RunningServiceInfo runningServiceInfo : activityManager.getRunningServices(Integer.MAX_VALUE)) {
                if (LocationService.class.getName().equalsIgnoreCase(runningServiceInfo.service.getClassName())) {
                    if (runningServiceInfo.foreground) {
                        return true;
                    }
                }
            }
            return false;
        }
        return false;
    }

    private void startLocationService() {
        if (!isLocationServiceRunning()) {
            try {
                t = new Thread(){
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    public void run(){
                        Intent intent = new Intent(getApplicationContext(), LocationService.class);
                        intent.setAction(Constants.ACTION_START_LOCATION_SERVICE);
                        try {
                            startService(intent);
                        } catch (Exception ignored) {
                            try {
                                startForegroundService(intent);
                            } catch (Exception ex) { }
                        }
                    }
                };
                t.start();

                /*
                Intent intent = new Intent(getApplicationContext(), LocationService.class);
                intent.setAction(Globals.ACTION_START_LOCATION_SERVICE);

                try {
                    startService(intent);
                } catch (Exception ignored) {
                    try {
                        startForegroundService(intent);
                    } catch (Exception ex) { }
                }
                */
            } catch (Exception ignored) { }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void stopLocationService() {
        if (isLocationServiceRunning()) {
            try {
                Intent intent = new Intent(getApplicationContext(), LocationService.class);
                intent.setAction(Constants.ACTION_STOP_LOCATION_SERVICE);

                try {
                    startService(intent);
                } catch (Exception ignored) {
                    try {
                        startForegroundService(intent);
                    } catch (Exception ex) { }
                }
            } catch (Exception ignored) { }
        }
    }
}