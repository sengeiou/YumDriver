package com.yum_driver.fcm;

import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.yum_driver.Activities.MainActivity;
import com.yum_driver.utils.SharedPreferenceManager;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FirebaseCloudMessagingService extends FirebaseMessagingService {

    private static final String TAG = "Yum Driver";
    boolean isLogin = false;

    SharedPreferenceManager prefManager;

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);

        prefManager = new SharedPreferenceManager(getApplicationContext());

        prefManager.connectDB();
        prefManager.setString("FCM_TOKEN", s);
        isLogin = prefManager.getBoolean("isLogin");
        prefManager.closeDB();

        System.out.println("FCM_TOKEN" + s);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // ...
        prefManager = new SharedPreferenceManager(getApplicationContext());

        prefManager.connectDB();
        isLogin = prefManager.getBoolean("isLogin");
        prefManager.closeDB();

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            try {
                String title = remoteMessage.getData().get("contentTitle");
                String message = remoteMessage.getData().get("message");

                MyNotificationManager mNotificationManager = new MyNotificationManager(getApplicationContext());


                if (isLogin) {

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtra("isFromNotification", true);
                    mNotificationManager.showSmallNotification(title, message, intent);
                }


            } catch (Exception e) {
            }
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {

            String title = remoteMessage.getNotification().getTitle();
            String message = remoteMessage.getNotification().getBody();

            MyNotificationManager mNotificationManager = new MyNotificationManager(getApplicationContext());

            if (isLogin) {

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("isFromNotification", true);
                mNotificationManager.showSmallNotification(title, message, intent);
            }

        }
    }

}
