package com.yum_driver.Fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.yum_driver.Activities.BaseActivity;
import com.yum_driver.Activities.NoInternetActivity;
import com.yum_driver.Adapters.NotificationAdapter;
import com.yum_driver.Parsers.NotificationParser;
import com.yum_driver.R;
import com.yum_driver.utils.CommonMethod;
import com.yum_driver.utils.Constants;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;


public class FragNotifications extends Fragment {

    private BaseActivity userInfo;
    private ProgressDialog pDialog;
    RecyclerView rvNotifications;
    private String nResponse;
    LinearLayout lyt_no_data;
    private View v;

    public FragNotifications(BaseActivity userInfo) {
        this.userInfo = userInfo;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_notifications, container, false);

        getIds();


        if(CommonMethod.isOnline(userInfo)){
            NotificationsApiCall();
        }
        else
        {
            Intent i = new Intent(userInfo, NoInternetActivity.class);
            startActivityForResult(i,404);
            Toast.makeText(userInfo, ""+getResources().getString(R.string.no_internet_connection), Toast.LENGTH_LONG).show();
        }


        return v;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==404 && resultCode == RESULT_OK)
        {
            NotificationsApiCall();
        }
    }

    private void NotificationsApiCall() {

        CommonMethod.showProgressDialog(userInfo);

        RequestQueue queue = Volley.newRequestQueue(userInfo);

        StringRequest request = new StringRequest(Request.Method.POST, Constants.BASE_URL_ADD_FEEDBACK + Constants.userNotificationsList, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                CommonMethod.hideProgressDialog(userInfo);

                System.out.println("response_notification" + response);

                try {
                    Gson gson = new Gson();
                    NotificationParser notificationParser = gson.fromJson(response, NotificationParser.class);
                    if (notificationParser != null)
                    {
                        if(notificationParser.responsecode.equals("200"))
                        {
                            if(notificationParser.data.isEmpty())
                            {
                                lyt_no_data.setVisibility(View.VISIBLE);
                                rvNotifications.setVisibility(View.GONE);
                            }
                            else
                            {
                                lyt_no_data.setVisibility(View.GONE);
                                rvNotifications.setVisibility(View.VISIBLE);
                                NotificationAdapter notificationListAdapter = new NotificationAdapter(userInfo, notificationParser.data);
                                rvNotifications.setAdapter(notificationListAdapter);
                            }

                        }
                        else
                        {
                            Toast.makeText(userInfo, notificationParser.responsemessage, Toast.LENGTH_SHORT).show();
                            lyt_no_data.setVisibility(View.VISIBLE);
                            rvNotifications.setVisibility(View.GONE);
                        }
                    }else {
                        Toast.makeText(userInfo, ""+getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();
                        lyt_no_data.setVisibility(View.VISIBLE);
                        rvNotifications.setVisibility(View.GONE);
                    }



                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("err:" + e.toString());
                    CommonMethod.hideProgressDialog(userInfo);
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("error :"+error.toString());
                CommonMethod.hideProgressDialog(userInfo);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {

                userInfo.prefManager.connectDB();
                String driver_id = userInfo.prefManager.getString("driver_id");
                String language = userInfo.prefManager.getString("language");
                userInfo.prefManager.closeDB();


                HashMap<String, String> notiparams = new HashMap<>();
                notiparams.put("token", Constants.Token);
                notiparams.put("user_id",driver_id);
                notiparams.put("type","driver");
                notiparams.put("lng", language);

                System.out.println("param_notif :" + notiparams);

                return notiparams;
            }
        };
        queue.add(request).setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }

    private void getIds() {

        rvNotifications = v.findViewById(R.id.rvNotifications);
        lyt_no_data = v.findViewById(R.id.lyt_no_data);

        rvNotifications.setLayoutManager(new LinearLayoutManager(userInfo, LinearLayoutManager.VERTICAL, false));
        rvNotifications.setHasFixedSize(false);
        rvNotifications.addItemDecoration(new DividerItemDecoration(userInfo, DividerItemDecoration.VERTICAL));


    }




}