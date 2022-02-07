package com.yum_driver.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.yum_driver.R;

public class CompleteDeliveryActivity extends AppCompatActivity {

    private ImageView imgBack;
    private Button btn_submit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_delivery);

        getIds();

    }

    private void getIds() {

        imgBack = findViewById(R.id.imgBack);
        btn_submit = findViewById(R.id.btn_submit);

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}