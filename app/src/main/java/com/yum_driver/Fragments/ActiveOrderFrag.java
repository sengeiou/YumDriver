package com.yum_driver.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import com.yum_driver.Activities.CurrentOrderDetails;
import com.yum_driver.Activities.MainActivity;
import com.yum_driver.Adapters.OrderActiveAdapter;
import com.yum_driver.Interfaces.RecyclerViewClickListenerNew;
import com.yum_driver.Parsers.CommonParser;
import com.yum_driver.Parsers.OrderParser;
import com.yum_driver.R;
import com.yum_driver.utils.CommonMethod;
import com.yum_driver.utils.Constants;
import com.yum_driver.utils.SharedPreferenceManager;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;


public class ActiveOrderFrag extends Fragment {

    Context context;
    private View v;
    private LinearLayout lytTop,lytNoData;
    private TextView txtFind;
    private RecyclerView rvActiveOrder;

    private SharedPreferenceManager preferenceManager;
    private ProgressDialog pDialog1;
    private SwipeRefreshLayout pullToRefresh;


    public ActiveOrderFrag(Context context) {
        this.context=context;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_active_order, container, false);
        preferenceManager = new SharedPreferenceManager(getActivity());

        getIds();

        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(CommonMethod.isOnline(context))
                {
                    GetActiveOrders();
                }
                else
                {
                    Toast.makeText(context, ""+getResources().getString(R.string.no_internet_connection), Toast.LENGTH_LONG).show();
                }
                pullToRefresh.setRefreshing(false);
            }
        });

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
        if(CommonMethod.isOnline(context))
        {
            GetActiveOrders();
        }
        else
        {
            Toast.makeText(context, ""+getResources().getString(R.string.no_internet_connection), Toast.LENGTH_LONG).show();
        }

    }
    public void showProgressDialog() {
        pDialog1 = ProgressDialog.show(context, null, null, true);
        pDialog1.setContentView(R.layout.progress_dialog);
        pDialog1.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    public  void hideProgressDialog() {
        if (pDialog1 != null) {
            pDialog1.dismiss();
        }
    }


    private void GetActiveOrders() {

        hideProgressDialog();
        CommonMethod.hideProgressDialog2(context);
        showProgressDialog();

        RequestQueue queue = Volley.newRequestQueue(context);

        StringRequest request = new StringRequest(Request.Method.POST, Constants.BASE_URL + Constants.activeorderlist, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                hideProgressDialog();

                System.out.println("response_Active_order_list" + response);

                try {
                    Gson gson = new Gson();
                    OrderParser orderOrder = gson.fromJson(response, OrderParser.class);

                    if(orderOrder != null) {
                        if (orderOrder.responsecode.equalsIgnoreCase("200")) {
                            try
                            {
                                lytNoData.setVisibility(View.GONE);
                                lytTop.setVisibility(View.VISIBLE);
                                OrderActiveAdapter myOrdersAdapter = new OrderActiveAdapter(context, orderOrder.order_list, new RecyclerViewClickListenerNew() {
                                    @Override
                                    public void onRecyclerViewListClicked(int position, String status) {
                                        if (CommonMethod.isOnline(context)){
                                            ChangeOrderStatusApiCall(orderOrder.order_list.get(position).order_id,status,orderOrder.order_list.get(position).rst_id);
                                        }else {
                                            Toast.makeText(context, ""+getResources().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                                rvActiveOrder.setAdapter(myOrdersAdapter);
                            }
                            catch (Exception e)
                            {
                                e.printStackTrace();
                            }
                        }
                        else
                        {
                            Toast.makeText(context, orderOrder.responsemessage, Toast.LENGTH_SHORT).show();
                            lytNoData.setVisibility(View.VISIBLE);
                            lytTop.setVisibility(View.GONE);
                        }
                    }
                    else
                    {
                        Toast.makeText(context, ""+getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();
                        lytNoData.setVisibility(View.VISIBLE);
                        lytTop.setVisibility(View.GONE);
                    }



                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("err:" + e.toString());
                    hideProgressDialog();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("error :"+error.toString());

              hideProgressDialog();

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
                params.put("status", "accept");
                params.put("lng", language);

                System.out.println("param_orderList :" + params);

                return params;
            }
        };
        queue.add(request).setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


    }

    private void ChangeOrderStatusApiCall(String order_id, String status, String rst_id) {

        CommonMethod.showProgressDialog(context);

        RequestQueue queue = Volley.newRequestQueue(context);

        StringRequest request = new StringRequest(Request.Method.POST, Constants.BASE_URL + Constants.orderdriverstatus, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                CommonMethod.hideProgressDialog(context);

                System.out.println("response_status_change" + response);

                try {
                    Gson gson = new Gson();
                    CommonParser  changeOrderStatusParser = gson.fromJson(response, CommonParser.class);
                    if (changeOrderStatusParser != null){
                        if (changeOrderStatusParser.responsecode.equals("200"))
                        {
                            Toast.makeText(context, ""+changeOrderStatusParser.responsemessage, Toast.LENGTH_SHORT).show();

                            if(status.equalsIgnoreCase("delivered")){
                                Intent i = new Intent(context, CurrentOrderDetails.class);
                                i.putExtra("order_id",order_id);
                                i.putExtra("rst_id",rst_id);
                                startActivity(i);
                            }



                            if(CommonMethod.isOnline(context))
                            {
                                GetActiveOrders();
                            }
                            else
                            {
                                Toast.makeText(context, ""+getResources().getString(R.string.no_internet_connection), Toast.LENGTH_LONG).show();
                            }

                        }else {
                            Toast.makeText(context, ""+changeOrderStatusParser.responsemessage, Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Toast.makeText(context, ""+getResources().getString(R.string.orders_not_found), Toast.LENGTH_SHORT).show();
                    }



                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("err:" + e.toString());
                    CommonMethod.hideProgressDialog(context);
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                CommonMethod.hideProgressDialog(context);

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
                params.put("rstid", rst_id);
                params.put("driver_id", driver_id);
                params.put("order_id", order_id);
                params.put("assigned_status", status);
                params.put("lng", language);

                System.out.println("param_ChangeStatus_orderList :" + params);

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

        rvActiveOrder=v.findViewById(R.id.rvActiveOrder);

        rvActiveOrder.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false));
        rvActiveOrder.setHasFixedSize(false);


    }

}