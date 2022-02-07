package com.yum_driver.Activities;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
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

import java.util.HashMap;
import java.util.Map;

public class MyRevenueActivity extends BaseActivity {

    private ImageView imgBack;
    private RecyclerView rvReneuve;
    private String type = "today";
    private TextView txtNodata,txtTotalRevenue,txtTotalTrip,txtTripBelow,txtAmountBelow;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_revenue);
        getIds();

        type = getIntent().getStringExtra("type");

        if(type.equalsIgnoreCase("thismonth"))
        {
            txtAmountBelow.setText(getResources().getString(R.string.total_revenue_this_month));
            txtTripBelow.setText(getResources().getString(R.string.trips_this_months));
        }

        if (CommonMethod.isOnline(MyRevenueActivity.this)) {
            GetRevenueDetailsApiCall();
        } else {
            Toast.makeText(MyRevenueActivity.this, getResources().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
        }

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void getIds() {

        rvReneuve = findViewById(R.id.rvReneuve);
        imgBack = findViewById(R.id.imgBack);
        txtNodata = findViewById(R.id.txtNodata);
        txtTotalRevenue = findViewById(R.id.txtTotalRevenue);
        txtTotalTrip = findViewById(R.id.txtTotalTrip);
        txtTripBelow = findViewById(R.id.txtTripBelow);
        txtAmountBelow = findViewById(R.id.txtAmountBelow);

        rvReneuve.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvReneuve.setHasFixedSize(false);

      //  setData();
    }

    private void GetRevenueDetailsApiCall() {
        CommonMethod.showProgressDialog(MyRevenueActivity.this);

        RequestQueue queue = Volley.newRequestQueue(MyRevenueActivity.this);

        StringRequest request = new StringRequest(Request.Method.POST, Constants.BASE_URL + Constants.revenueDetails, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                CommonMethod.hideProgressDialog(MyRevenueActivity.this);

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

                            txtTotalRevenue.setText(getResources().getString(R.string.dollar)+revenueParser.totalearning);
                            txtTotalTrip.setText(revenueParser.tripcount);



                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("err:" + e.toString());
                    CommonMethod.hideProgressDialog(MyRevenueActivity.this);
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CommonMethod.hideProgressDialog(MyRevenueActivity.this);
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

}