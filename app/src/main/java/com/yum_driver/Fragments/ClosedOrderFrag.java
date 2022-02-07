package com.yum_driver.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.yum_driver.Activities.MainActivity;
import com.yum_driver.Adapters.OrderCompleteAdapter;
import com.yum_driver.Parsers.OrderParser;
import com.yum_driver.R;
import com.yum_driver.utils.CommonMethod;
import com.yum_driver.utils.Constants;
import com.yum_driver.utils.SharedPreferenceManager;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class ClosedOrderFrag extends Fragment {

    Context context;
    private View v;
    private LinearLayout lytTop,lytNoData;
    private TextView txtFind;
    private RecyclerView rvACloseOrder;
    private SharedPreferenceManager preferenceManager;
    private SwipeRefreshLayout pullToRefresh;

    public ClosedOrderFrag(Context context) {
        // Required empty public constructor
        this.context= context;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_closed_order, container, false);
        preferenceManager = new SharedPreferenceManager(getActivity());
        getIds();

        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(CommonMethod.isOnline(context))
                {
                    GetOrderCompleteOrder();
                }
                else
                {
                    Toast.makeText(context, ""+getResources().getString(R.string.no_internet_connection), Toast.LENGTH_LONG).show();
                }

                pullToRefresh.setRefreshing(false);
            }
        });

        if(CommonMethod.isOnline(context))
        {
            GetOrderCompleteOrder();
        }
        else
        {
            Toast.makeText(context, ""+getResources().getString(R.string.no_internet_connection), Toast.LENGTH_LONG).show();
        }


        txtFind.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent i = new Intent(context, MainActivity.class);
                startActivity(i);
            }
        });

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    private void GetOrderCompleteOrder() {

        CommonMethod.showProgressDialog2(context);

        RequestQueue queue = Volley.newRequestQueue(context);

        StringRequest request = new StringRequest(Request.Method.POST, Constants.BASE_URL + Constants.completeorderlist, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                CommonMethod.hideProgressDialog2(context);

                System.out.println("response_Close_order_list" + response);

                try {
                    Gson gson = new Gson();
                    OrderParser orderOrder = gson.fromJson(response, OrderParser.class);

                    if(orderOrder != null) {
                        if (orderOrder.responsecode.equalsIgnoreCase("200")) {
                            try
                            {
                                if(orderOrder.order_list!=null) {
                                    lytTop.setVisibility(View.VISIBLE);
                                    lytNoData.setVisibility(View.GONE);
                                    OrderCompleteAdapter myOrdersAdapter = new OrderCompleteAdapter(context, orderOrder.order_list);
                                    rvACloseOrder.setAdapter(myOrdersAdapter);
                                }else{
                                    lytTop.setVisibility(View.GONE);
                                    lytNoData.setVisibility(View.VISIBLE);
                                }

                            }
                            catch (Exception e)
                            {
                                e.printStackTrace();
                                lytTop.setVisibility(View.GONE);
                                lytNoData.setVisibility(View.VISIBLE);

                            }
                        }
                        else
                        {
                            //Toast.makeText(context, orderOrder.responsemessage, Toast.LENGTH_SHORT).show();
                            lytTop.setVisibility(View.GONE);
                            lytNoData.setVisibility(View.VISIBLE);
                        }
                    }
                    else
                    {
                        Toast.makeText(context, ""+getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();
                        lytTop.setVisibility(View.GONE);
                        lytNoData.setVisibility(View.VISIBLE);
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("err:" + e.toString());
                    CommonMethod.hideProgressDialog2(context);
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("error :"+error.toString());
                lytTop.setVisibility(View.GONE);
                lytNoData.setVisibility(View.VISIBLE);
                CommonMethod.hideProgressDialog2(context);

            }
        }) {
            @Override
            protected Map<String, String> getParams() {


                preferenceManager.connectDB();
                String userId = preferenceManager.getString("driver_id");
                String driver_lat = preferenceManager.getString("currentLat");
                String driver_lng = preferenceManager.getString("currentLng");
                String language = preferenceManager.getString("language");
                preferenceManager.closeDB();


                HashMap<String, String> params = new HashMap<>();

                params.put("token", Constants.Token);
                params.put("driver_id", userId);
                params.put("driver_lat", driver_lat);
                params.put("driver_lng", driver_lng);
                params.put("order_status", "delivered");
                params.put("lng", language);
                System.out.println("param_CloseorderList :" + params);

                return params;
            }
        };
        queue.add(request).setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));



    }

    private void getIds() {

        pullToRefresh=v.findViewById(R.id.pullToRefresh);
        lytNoData=v.findViewById(R.id.lytNoData);
        lytTop=v.findViewById(R.id.lytTop);
        txtFind=v.findViewById(R.id.txtFind);

        rvACloseOrder=v.findViewById(R.id.rvCloseOrder);

        rvACloseOrder.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false));
        rvACloseOrder.setHasFixedSize(false);

    }

}