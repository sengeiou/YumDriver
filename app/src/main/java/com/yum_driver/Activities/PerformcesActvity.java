package com.yum_driver.Activities;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.yum_driver.Adapters.ReviewAdapter;
import com.yum_driver.Pojo.DriverPerformanceParser;
import com.yum_driver.Pojo.DriverReview;
import com.yum_driver.R;
import com.yum_driver.utils.CommonMethod;
import com.yum_driver.utils.Constants;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PerformcesActvity extends BaseActivity {

    private static final int SPAN_INCLUSIVE_INCLUSIVE = 0;
    private PieChart pieChart;
    private ProgressDialog pDialog;
    ProgressBar progressfive, progressfour, progressthree, progresstwo, progressone;
    String performanceResponse;
    String performOption = "today";
    Spinner spin;
    private String driver_id;
    float sum;
    RecyclerView mRecycler;
    RecyclerView.LayoutManager mLayoutManager;
    ArrayList<DriverReview> reviewList;
    ReviewAdapter reviewAdapter;
    Float ratingsRecd, ordersCan, ordersDel;
    DriverPerformanceParser driverPerformanceParser;
    TextView tv_totalDeliveries, tv_totalRatingsReceived, tv_totalOrderCancel, tv_totalOrderDelivered, tv_totalRatings,tv_avgRatings, txtRatingCount;
    private TextView txtAllRatings;
    private ImageView imgBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_performces);

        getIds();

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        String[] performTime = getResources().getStringArray(R.array.arrayPerformance);

        spin = findViewById(R.id.perform_spin);
        ArrayAdapter aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, performTime);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(aa);
        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //your code here
                switch (spin.getSelectedItemPosition())
                {
                    case 0:
                        performOption="today";
                        break;
                    case 1:
                        performOption="thisweek";
                        break;
                    case 2:
                        performOption="thismonth";
                        break;
                    case 3:
                        performOption="alltime";
                        break;
                }

                if (CommonMethod.isOnline(PerformcesActvity.this)) {
                    DriverPerformanceApiCall();
                } else {
                    Toast.makeText(PerformcesActvity.this, ""+getResources().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                }



                //  setStaticData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void getIds() {

        imgBack = findViewById(R.id.imgBack);
        txtAllRatings = findViewById(R.id.txtAllRatings);
        mRecycler = findViewById(R.id.rvReview);
        mRecycler.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this,RecyclerView.HORIZONTAL,false);

        //tv_totalDeliveries = findViewById(R.id.tv_totalDeliveries);
        tv_totalRatingsReceived = findViewById(R.id.tv_totalRaatingsReceived);
        tv_totalOrderCancel = findViewById(R.id.tv_totalOrderCancel);
        tv_totalOrderDelivered = findViewById(R.id.tv_totalOrderDelivered);
        //tv_totalRatings = findViewById(R.id.tv_totalR)
        txtRatingCount = findViewById(R.id.txtRatingCount);
        pieChart = findViewById(R.id.progress_bar);
        progressfive = findViewById(R.id.progressFive);
        progressfour = findViewById(R.id.progressFour);
        progressthree = findViewById(R.id.progressThree);
        progresstwo = findViewById(R.id.progressTwo);
        progressone = findViewById(R.id.progressOne);




    }

    private void DriverPerformanceApiCall() {
        CommonMethod.showProgressDialog(PerformcesActvity.this);

        RequestQueue queue = Volley.newRequestQueue(PerformcesActvity.this);

        StringRequest request = new StringRequest(Request.Method.POST, Constants.BASE_URL + Constants.driverdashboard, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                CommonMethod.hideProgressDialog(PerformcesActvity.this);

                System.out.println("response_order_list" + response);

                try {
                    Gson gson = new Gson();
                    try
                    {
                        driverPerformanceParser = gson.fromJson(response, DriverPerformanceParser.class);
                        System.out.println("perform_response: "+response);

                    } catch (JsonSyntaxException e) {
                        e.printStackTrace();
                    }
                    System.out.println("perform_checknull"+driverPerformanceParser.driverdatas.totalOrderDelivered);
                    //tv_totalDeliveries.setText(String.valueOf(driverPerformanceParser.driverdatas.totalDeliveries));
                    tv_totalRatingsReceived.setText((""+driverPerformanceParser.driverdatas.TotalRatingRecieved).toString());
                    tv_totalOrderCancel.setText(""+driverPerformanceParser.driverdatas.totalOrderCancel);
                    tv_totalOrderDelivered.setText(""+driverPerformanceParser.driverdatas.totalOrderDelivered);
                    txtRatingCount.setText(""+driverPerformanceParser.driverdatas.avgRatings);
                    txtAllRatings.setText(""+driverPerformanceParser.driverdatas.totalRatings+" "+getResources().getString(R.string.ratings));


                    sum = driverPerformanceParser.driverdatas.TotalRatingRecieved+driverPerformanceParser.driverdatas.totalOrderDelivered+driverPerformanceParser.driverdatas.totalOrderCancel;
                    ratingsRecd = ((100*driverPerformanceParser.driverdatas.TotalRatingRecieved)/sum);
                    ordersCan = ((100*driverPerformanceParser.driverdatas.totalOrderCancel)/sum);
                    ordersDel = ((100*driverPerformanceParser.driverdatas.totalOrderDelivered)/sum);
                    //sum = ratingsRecd+ordersDel+ordersCan;
                    System.out.println("percent: "+ratingsRecd);

                    progressfive.setProgress((int) driverPerformanceParser.driverdatas.totalratings_5);
                    progressfour.setProgress((int) driverPerformanceParser.driverdatas.totalratings_4);
                    progressthree.setProgress((int) driverPerformanceParser.driverdatas.totalratings_3);
                    progresstwo.setProgress((int) driverPerformanceParser.driverdatas.totalratings_2);
                    progressone.setProgress((int) driverPerformanceParser.driverdatas.totalratings_1);

                    setupPieChart();
                    loadPieChartData();

                    mRecycler.setLayoutManager(mLayoutManager);
                    reviewAdapter = new ReviewAdapter(PerformcesActvity.this,driverPerformanceParser.driverdatas.reviewsList);
                    mRecycler.setAdapter(reviewAdapter);




                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("err:" + e.toString());
                    CommonMethod.hideProgressDialog(PerformcesActvity.this);
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("error :"+error.toString());

                CommonMethod.hideProgressDialog(PerformcesActvity.this);

            }
        }) {
            @Override
            protected Map<String, String> getParams() {


                prefManager.connectDB();
                String language = prefManager.getString("language");
                driver_id =prefManager.getString("driver_id");
                prefManager.closeDB();


                HashMap<String, String> dparams = new HashMap<>();
                dparams.put("token", Constants.Token);
                dparams.put("driver_id",driver_id);
                dparams.put("strweekdays",performOption );
                dparams.put("lng", language);
                System.out.println("Params: "+dparams);

                return dparams;
            }
        };
        queue.add(request).setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    private void setupPieChart() {
        pieChart.setDescription(null);
        pieChart.setDrawHoleEnabled(true);
        //pieChart.setUsePercentValues(true);
        //pieChart.setEntryLabelTextSize(12);
        pieChart.setDrawEntryLabels(false);
        pieChart.setMinimumWidth(5);
        //pieChart.setEntryLabelColor(Color.BLACK);
        String strText = "<h1>"+driverPerformanceParser.driverdatas.totalDeliveries+"</h1><br><p>"+getResources().getString(R.string.total_deliveries)+"</p>";
        pieChart.setCenterText(Html.fromHtml(strText));
        pieChart.setCenterTextSize(11);

        //pieChart.getDescription().setEnabled(false);
/*
        Legend l = pieChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setEnabled(true);

 */
    }

    private void loadPieChartData() {
        System.out.println("percent1: "+ratingsRecd);
        System.out.println("percent2: "+ordersDel);
        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry((float) ratingsRecd));
        entries.add(new PieEntry((float) ordersCan));
        entries.add(new PieEntry((float) ordersDel));

        //entries.add
        //entries.add(new PieEntry(0.25f, "Electricity and Gas"));
        //entries.add(new PieEntry(0.3f, "Housing"));

        int [] color={ Color.rgb(90,233,190), Color.rgb(248,130,93), Color.rgb(240,233,71)};


//        ArrayList<Integer> colors = new ArrayList<>();
//        for (int color: ColorTemplate.MATERIAL_COLORS) {
//            colors.add(color);
//        }

//        for (int color: ColorTemplate.VORDIPLOM_COLORS) {
//            colors.add(color);
//        }


        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(color);
        PieData data = new PieData(dataSet);
        //data.setDrawValues(true);
        //data.setValueFormatter(new PercentFormatter(pieChart));
        //data.setValueTextSize(12f);
        //data.setValueTextColor(Color.BLACK);
        pieChart.setData(data);
        pieChart.invalidate();
        pieChart.animateY(1400, Easing.EaseInOutQuad);
    }






}