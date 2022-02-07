package com.yum_driver.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.yum_driver.R;
import com.yum_driver.utils.CommonMethod;

public class NoInternetActivity extends BaseActivity {

    private ImageView imgNoInternet;
    private Button btn_Retry;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_internet);

        imgNoInternet = findViewById(R.id.imgNoInternet);
        btn_Retry = findViewById(R.id.btn_Retry);


        Glide.with(NoInternetActivity.this)
                .load(R.raw.nointernet)
                .into(imgNoInternet);

        // check internet status
        checkUpdates();

    }

    private void checkUpdates() {


            handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (CommonMethod.isOnline(NoInternetActivity.this)) {
                        Intent intent = new Intent();
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                    checkUpdates();
                }
            }, 500);

    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }
}