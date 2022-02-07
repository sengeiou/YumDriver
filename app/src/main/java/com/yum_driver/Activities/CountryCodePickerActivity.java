package com.yum_driver.Activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.EditText;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yum_driver.Adapters.RecyclerViewAdapter_Countries;
import com.yum_driver.Interfaces.RecyclerViewClickListener;
import com.yum_driver.R;

public class CountryCodePickerActivity extends AppCompatActivity implements RecyclerViewClickListener {
    private RecyclerView mRecyclerView;
    private EditText txtVSearch;
    private RecyclerViewAdapter_Countries adapter;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_country_code_picker);

        setupRecyclerView();


        txtVSearch = findViewById(R.id.txtFSearch);
        txtVSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.e("key", "intercepted");

                adapter.performSearch(txtVSearch.getText().toString());
            }
        });
    }

    public void dismiss(View view) {
        this.finish();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setupRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView = findViewById(R.id.recyclerView_callingCodes);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setOutlineProvider(ViewOutlineProvider.BOUNDS);
        mRecyclerView.setClipChildren(false);
        mRecyclerView.setClipToPadding(false);
        mRecyclerView.setClipToOutline(false);

        adapter = new RecyclerViewAdapter_Countries(this, this);
        mRecyclerView.setAdapter(adapter);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onRecyclerViewListClicked(int position, Boolean toggleOn,String status) {
        String result = adapter.get(position);

        Log.e("res", result);

        Intent intent = new Intent();
        intent.putExtra("res", result);

        setResult(RESULT_OK, intent);

        finishAfterTransition();
    }

}