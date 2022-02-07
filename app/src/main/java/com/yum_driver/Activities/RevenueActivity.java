package com.yum_driver.Activities;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.yum_driver.Adapters.RevenueListAdapter;
import com.yum_driver.Parsers.RevenueParser;
import com.yum_driver.R;
import com.yum_driver.utils.CommonMethod;
import com.yum_driver.utils.Constants;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class RevenueActivity extends BaseActivity {

    private ImageView imgBack;
    private RecyclerView rvReneuve;
    private RelativeLayout rlDaily,rlWeekly,rlMonthly;
    private CardView cdWeekly,cdMonthly,cdDaily;
    private TextView txtDate,txtTotalTrip,txtTotalEaring,txtNodata,txtDaily,txtMonthly,txtWeekly;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_revenue);

        getIds();


        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        cdDaily.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                rlDaily.setBackground(getResources().getDrawable(R.drawable.card_bg_orange_filled));
                txtDaily.setTextColor(getResources().getColor(R.color.white));

                rlWeekly.setBackground(getResources().getDrawable(R.drawable.card_bg_not_select));
                txtWeekly.setTextColor(getResources().getColor(R.color.black));

                rlMonthly.setBackground(getResources().getDrawable(R.drawable.card_bg_not_select));
                txtMonthly.setTextColor(getResources().getColor(R.color.black));

                if (CommonMethod.isOnline(RevenueActivity.this)) {
                    GetRevenueDetailsApiCall();
                } else {
                    Toast.makeText(RevenueActivity.this, getResources().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                }

            }
        });
        cdWeekly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                rlDaily.setBackground(getResources().getDrawable(R.drawable.card_bg_not_select));
                txtDaily.setTextColor(getResources().getColor(R.color.black));

                rlWeekly.setBackground(getResources().getDrawable(R.drawable.card_bg_orange_filled));
                txtWeekly.setTextColor(getResources().getColor(R.color.white));

                rlMonthly.setBackground(getResources().getDrawable(R.drawable.card_bg_not_select));
                txtMonthly.setTextColor(getResources().getColor(R.color.black));

               Intent intent = new Intent(RevenueActivity.this,MyRevenueActivity.class);
               intent.putExtra("type","thisweek");
               startActivity(intent);
            }
        });
        cdMonthly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                rlDaily.setBackground(getResources().getDrawable(R.drawable.card_bg_not_select));
                txtDaily.setTextColor(getResources().getColor(R.color.black));

                rlWeekly.setBackground(getResources().getDrawable(R.drawable.card_bg_not_select));
                txtWeekly.setTextColor(getResources().getColor(R.color.black));

                rlMonthly.setBackground(getResources().getDrawable(R.drawable.card_bg_orange_filled));
                txtMonthly.setTextColor(getResources().getColor(R.color.white));

                Intent i = new Intent(RevenueActivity.this,MyRevenueActivity.class);
                i.putExtra("type","thismonth");
                startActivity(i);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        rlDaily.setBackground(getResources().getDrawable(R.drawable.card_bg_orange_filled));
        txtDaily.setTextColor(getResources().getColor(R.color.white));

        rlWeekly.setBackground(getResources().getDrawable(R.drawable.card_bg_not_select));
        txtWeekly.setTextColor(getResources().getColor(R.color.black));

        rlMonthly.setBackground(getResources().getDrawable(R.drawable.card_bg_not_select));
        txtMonthly.setTextColor(getResources().getColor(R.color.black));

        if (CommonMethod.isOnline(RevenueActivity.this)) {
            GetRevenueDetailsApiCall();
        } else {
            Toast.makeText(RevenueActivity.this, getResources().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
        }

    }

    private void GetRevenueDetailsApiCall() {
        CommonMethod.showProgressDialog(RevenueActivity.this);

        RequestQueue queue = Volley.newRequestQueue(RevenueActivity.this);

        StringRequest request = new StringRequest(Request.Method.POST, Constants.BASE_URL + Constants.revenueDetails, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                CommonMethod.hideProgressDialog(RevenueActivity.this);

                System.out.println("response_revenue_details" + response);

                try {
                    Gson gson = new Gson();
                    RevenueParser revenueParser = gson.fromJson(response, RevenueParser.class);

                    if (revenueParser != null) {
                        if (revenueParser.responsecode.equalsIgnoreCase("200")) {

                            if(revenueParser.commissionList.isEmpty()){
                                txtNodata.setVisibility(View.GONE);
                                rvReneuve.setVisibility(View.GONE);
                            }else{
                                txtNodata.setVisibility(View.GONE);
                                rvReneuve.setVisibility(View.VISIBLE);
                                RevenueListAdapter adapter = new RevenueListAdapter(getApplicationContext(),revenueParser.commissionList);
                                rvReneuve.setAdapter(adapter);

                            }

                            String currentDate = new SimpleDateFormat("dd MMM,yyyy", Locale.getDefault()).format(new Date());
                            txtTotalEaring.setText(getResources().getString(R.string.dollar)+revenueParser.totalearning);
                            txtTotalTrip.setText(revenueParser.tripcount);
                            txtDate.setText(currentDate+"  >");

                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("err:" + e.toString());
                    CommonMethod.hideProgressDialog(RevenueActivity.this);
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CommonMethod.hideProgressDialog(RevenueActivity.this);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {

                prefManager.connectDB();
                String driver_id = prefManager.getString("driver_id");
                String language = prefManager.getString("language");

                prefManager.closeDB();

                HashMap<String, String> params = new HashMap<>();

                params.put("token", Constants.Token);
                params.put("driver_id", driver_id);
                params.put("strdays", "today");
                params.put("lng", language);

                System.out.println("param_revenue_details :" + params);

                return params;
            }
        };
        queue.add(request).setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    private void getIds() {

        rvReneuve = findViewById(R.id.rvReneuve);
        imgBack = findViewById(R.id.imgBack);
        cdWeekly = findViewById(R.id.cdWeekly);
        txtDate = findViewById(R.id.txtDate);
        cdMonthly = findViewById(R.id.cdMonthly);
        txtTotalTrip = findViewById(R.id.txtTotalTrip);
        txtTotalEaring = findViewById(R.id.txtTotalEaring);
        txtNodata = findViewById(R.id.txtNodata);
        txtDaily = findViewById(R.id.txtDaily);
        rlDaily = findViewById(R.id.rlDaily);
        rlWeekly = findViewById(R.id.rlWeekly);
        txtWeekly = findViewById(R.id.txtWeekly);
        rlMonthly = findViewById(R.id.rlMonthly);
        txtMonthly = findViewById(R.id.txtMonthly);
        cdDaily = findViewById(R.id.cdDaily);

        rvReneuve.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvReneuve.setHasFixedSize(false);

    }


}