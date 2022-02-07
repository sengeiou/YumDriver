package com.yum_driver.Activities;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.yum_driver.Adapters.LanguageListAdapter;
import com.yum_driver.R;

public class LanuguageListActivity extends BaseActivity {


    private RecyclerView lytList;
    private LinearLayoutManager linearLayoutManager;
    private ImageView imgBack;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lanuguage_list);

        lytList = findViewById(R.id.lytList);
        imgBack = findViewById(R.id.imgBack);

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(Activity.RESULT_OK);
                finish();
            }
        });

        linearLayoutManager = new LinearLayoutManager(LanuguageListActivity.this);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(lytList.getContext(), linearLayoutManager.getOrientation());
        lytList.addItemDecoration(dividerItemDecoration);
        lytList.setLayoutManager(linearLayoutManager);

        prefManager.connectDB();
        String language = prefManager.getString("language");
        prefManager.closeDB();

        System.out.println("lang :"+language);

        LanguageListAdapter adapter = new LanguageListAdapter(LanuguageListActivity.this, language);
        lytList.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        setResult(Activity.RESULT_OK);
        finish();
    }
}