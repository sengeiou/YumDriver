package com.yum_driver.utils;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.yum_driver.R;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class AutoStartService extends Service {

    private static final String TAG = "AutoService";
    public int counter = 0;
    private Timer timer;
    private TimerTask timerTask;



    private double distanceTravelled = 0F;
    private SharedPreferenceManager preferenceManager;
    private String mResponse3;
    private GPSTracker gps;
    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);

            if (locationResult != null && locationResult.getLastLocation() != null) {
                try {
                    LatLng latLng = new LatLng(locationResult.getLastLocation().getLatitude(), locationResult.getLastLocation().getLongitude());


                    preferenceManager.connectDB();
                    String lastLat = preferenceManager.getString("currentLat");
                    String lastLng = preferenceManager.getString("currentLng");
                    preferenceManager.setString("lastLat", lastLat);
                    preferenceManager.setString("lastLng", lastLng);
                    preferenceManager.setString("currentLat", String.valueOf(latLng.latitude));
                    preferenceManager.setString("currentLng", String.valueOf(latLng.longitude));
                    preferenceManager.closeDB();

                    LatLng lastLatLng = new LatLng(Double.parseDouble(lastLat),Double.parseDouble(lastLng));
                    float bearing = locationResult.getLastLocation().getBearing();
                    if (lastLatLng != null && latLng != null) {
                        distanceTravelled += SphericalUtil.computeDistanceBetween(lastLatLng, latLng);
                    }


                    broadcastNewLocationInfo(latLng,bearing);

                } catch (Exception e) {}
            }
        }
    };


    public AutoStartService() {
        Log.i(TAG, "AutoStartService: Here we go.....");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        preferenceManager = new SharedPreferenceManager(AutoStartService.this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not implemented");
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        startTimer(intent);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy: Service is destroyed :( ");
//        Intent broadcastIntent = new Intent(this, MainActivity.class);
//        sendBroadcast(broadcastIntent);
        stoptimertask();
    }

    public void startTimer(Intent intent) {

        timer = new Timer();

        //initialize the TimerTask's job
        initialiseTimerTask(intent);

        //schedule the timer, to wake up every 1 second
        timer.schedule(timerTask, 1000, 1000); //
    }

    public void initialiseTimerTask(Intent intent) {
        timerTask = new TimerTask() {
            @Override
            public void run() {

               /* locationCallback = new LocationCallback() {
                    @Override
                    public void onLocationResult(@NonNull @NotNull LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        if (locationResult != null && locationResult.getLastLocation() != null) {
                            try {
                                LatLng latLng = new LatLng(locationResult.getLastLocation().getLatitude(), locationResult.getLastLocation().getLongitude());


                                preferenceManager.connectDB();
                                String lastLat = preferenceManager.getString("currentLat");
                                String lastLng = preferenceManager.getString("currentLng");
                                preferenceManager.setString("lastLat", lastLat);
                                preferenceManager.setString("lastLng", lastLng);
                                preferenceManager.setString("currentLat", String.valueOf(latLng.latitude));
                                preferenceManager.setString("currentLng", String.valueOf(latLng.longitude));
                                preferenceManager.closeDB();

                                LatLng lastLatLng = new LatLng(Double.parseDouble(lastLat),Double.parseDouble(lastLng));
                                float bearing = locationResult.getLastLocation().getBearing();
                                if (lastLatLng != null && latLng != null) {
                                    distanceTravelled += SphericalUtil.computeDistanceBetween(lastLatLng, latLng);
                                }

                                try {
                                    UpdateLatLongApiCall(String.valueOf(latLng.latitude),String.valueOf(latLng.longitude),String.valueOf(bearing));

                                }
                                catch (Exception e)
                                {
                                    e.printStackTrace();
                                }
//                                broadcastNewLocationInfo(latLng,bearing);

                            } catch (Exception e) {}
                        }

                    }
                };*/
//                Intent intent = new Intent(getApplicationContext(), LocationService.class);
//                intent.setAction(Constants.ACTION_START_LOCATION_SERVICE);
                Log.e("locserv", "1");
                if (intent != null) {
                    String action = intent.getAction();
                    System.out.println("action"+action);
                    Log.e("locserv", "2");
                    if (action != null) {
                        if (action.equalsIgnoreCase(Constants.ACTION_START_LOCATION_SERVICE)) {
                            Log.e("locserv", "3");
                            startLocationService();
                        } else if (action.equalsIgnoreCase(Constants.ACTION_STOP_LOCATION_SERVICE)) {
                            stopLocationService();
                        }
                    }
                }
                Log.i(TAG, "Timer is running " + counter++);
            }
        };
    }
    private Notification manageNotification(String statusText) {
        Intent resultIntent = new Intent();

        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext(), Constants.LOCATION_SERVICE_NOTIFICATION_CHANNEL_ID);
        notificationBuilder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        notificationBuilder.setSmallIcon(R.mipmap.ic_launcher_round);
        notificationBuilder.setContentTitle(getResources().getString(R.string.location_service));
        notificationBuilder.setContentText(statusText);
        notificationBuilder.setContentIntent(pendingIntent);
        notificationBuilder.setTicker(getResources().getString(R.string.please_do_not_kill));
        notificationBuilder.setAutoCancel(false);
        notificationBuilder.setOngoing(true);
        notificationBuilder.setOnlyAlertOnce(true);
        notificationBuilder.setDefaults(NotificationCompat.DEFAULT_LIGHTS);
        notificationBuilder.setPriority(NotificationCompat.PRIORITY_LOW);

        return notificationBuilder.build();
    }

    private void startLocationService() {
        try {
            if (!isLocationEnabled(getApplicationContext())) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    //Toast.makeText(getApplicationContext(), getResources().getString(R.string.please_enable_location), Toast.LENGTH_LONG).show();

                });

            }

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PERMISSION_GRANTED) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startLocationService();
                    }
                }, 1000);

                return;
            }

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
                if (notificationManager != null && notificationManager.getNotificationChannel(Constants.LOCATION_SERVICE_NOTIFICATION_CHANNEL_ID) == null) {
                    NotificationChannel notificationChannel = new NotificationChannel(Constants.LOCATION_SERVICE_NOTIFICATION_CHANNEL_ID, "Deseos Driver Location Service Channel", NotificationManager.IMPORTANCE_DEFAULT);
                    notificationChannel.setDescription("");

                    notificationManager.createNotificationChannel(notificationChannel);
                }
            }

            LocationRequest locationRequest = new LocationRequest();
            locationRequest.setInterval(2000);
            locationRequest.setFastestInterval(1000);
            locationRequest.setSmallestDisplacement(Constants.LOCATION_MIN_DISTANCE_FILTER);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());

            Notification notification = manageNotification("Yum Driver app using location");
            startForeground(Constants.LOCATION_SERVICE_ID, notification);


            System.out.println("Service is up and running!");
        } catch (Exception e) {

            e.printStackTrace();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startLocationService();
                }
            }, 1000);
        }
    }

    private void stopLocationService() {
        LocationServices.getFusedLocationProviderClient(this).removeLocationUpdates(locationCallback);

        stopForeground(true);

        stopSelf();
    }

    private void broadcastNewLocationInfo(LatLng latLng, float bearing) {
        if (latLng == null) {
            return;
        }
        Intent intent = new Intent();
        intent.setAction(Constants.LOCATION_MESSAGE_ID);
        intent.putExtra("newLatLng", latLng);
        sendBroadcast(intent);

        System.out.println("newLatLng"+latLng);
        try {
            UpdateLatLongApiCall(String.valueOf(latLng.latitude),String.valueOf(latLng.longitude),String.valueOf(bearing));

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void UpdateLatLongApiCall(String ulat, String ulng, String bearing) {

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

        StringRequest request = new StringRequest(Request.Method.POST, Constants.BASE_URL + Constants.updatedriverlocation, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                System.out.println("response_updateLatLog: " + response);
                try {

                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("err:" + e.toString());
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {
            @Override
            protected Map<String, String> getParams() {

                preferenceManager.connectDB();
                String driver_id = preferenceManager.getString("driver_id");
                String language = preferenceManager.getString("language");
                preferenceManager.closeDB();

                HashMap<String, String> params = new HashMap<>();

                params.put("token", Constants.Token);
                params.put("user_id", driver_id);
                params.put("ulat", ulat);
                params.put("ulng", ulng);
                params.put("bearing", bearing);
                params.put("lng", language);
                System.out.println("params_loc :"+params);
                return params;
            }
        };
        queue.add(request).setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    public boolean isLocationEnabled(Context context) {
        try {
            int locationMode = 0;
            String locationProviders;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                try {
                    locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

                } catch (Settings.SettingNotFoundException e) {
                    e.printStackTrace();
                    return false;
                }

                return locationMode != Settings.Secure.LOCATION_MODE_OFF;

            } else {
                locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
                return !TextUtils.isEmpty(locationProviders);
            }
        } catch (Exception e) {
            return true;
        }
    }


    public void stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
}
