package com.yum_driver.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.yum_driver.Activities.FeedbackActivity;
import com.yum_driver.Activities.LanuguageListActivity;
import com.yum_driver.Activities.LoginActivity;
import com.yum_driver.Activities.MainActivity;
import com.yum_driver.Activities.PerformcesActvity;
import com.yum_driver.Activities.ProfileSetting;
import com.yum_driver.Activities.RevenueActivity;
import com.yum_driver.Activities.VehicalListActivity;
import com.yum_driver.Activities.WebViewActivity;
import com.yum_driver.Parsers.ProfileParser;
import com.yum_driver.R;
import com.yum_driver.utils.CommonMethod;
import com.yum_driver.utils.Constants;
import com.google.gson.Gson;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.HashMap;
import java.util.Map;


public class FragProfile extends Fragment {
    private MainActivity context;
    RelativeLayout rlProfileSetting,rlLanguageSetting, rlFeedBack, rlPerformace, rlLogout, rlVehicalInfo, rlRevenue, rlRating, rlAboutUS,rlSupport;
    private String driver_id;
    private ProgressDialog pDialog;
    String mResponse;
    TextView txtName, txtAddress, txtUserName;
    private View v;
    private CircularImageView imgProfile;


    public FragProfile() {
    }

    public FragProfile(MainActivity context) {
        this.context = context;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_profile, container, false);

        getIds();

        rlProfileSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, ProfileSetting.class));
            }
        });
        rlFeedBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, FeedbackActivity.class));
            }
        });
        rlPerformace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, PerformcesActvity.class));
            }
        });
        rlLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutDialog();
            }
        });
        rlVehicalInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, VehicalListActivity.class));
            }
        });
        rlRevenue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, RevenueActivity.class));
            }
        });
        rlSupport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, WebViewActivity.class);
                intent.putExtra("url", Constants.support);
                intent.putExtra("toolbar", getResources().getString(R.string.support));
                startActivity(intent);
            }
        });
        rlAboutUS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, WebViewActivity.class);
                intent.putExtra("url", Constants.aboutus);
                intent.putExtra("toolbar", getResources().getString(R.string.about));
                startActivity(intent);
            }
        });

        rlRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String appPackageName = context.getPackageName(); // getPackageName() from Context or Activity object
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
            }
        });

        rlLanguageSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(context, LanuguageListActivity.class), 100);

            }
        });


        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (CommonMethod.isOnline(context)) {
            GetProfileDetailsApiCall();
        } else {
            Toast.makeText(context, getResources().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
        }
    }

    private void getIds() {

        rlLanguageSetting = v.findViewById(R.id.rlLanguageSetting);
        rlProfileSetting = v.findViewById(R.id.rlProfileSetting);
        rlFeedBack = v.findViewById(R.id.rlFeedBack);
        rlPerformace = v.findViewById(R.id.rlPerformace);
        rlLogout = v.findViewById(R.id.rlLogout);
        rlVehicalInfo = v.findViewById(R.id.rlVehicalInfo);
        rlRevenue = v.findViewById(R.id.rlRevenue);
        imgProfile = v.findViewById(R.id.imgProfile);
        txtUserName = v.findViewById(R.id.txtUserName);
        rlRating = v.findViewById(R.id.rlRating);
        rlAboutUS = v.findViewById(R.id.rlAboutUS);
        txtAddress = v.findViewById(R.id.txtAddress);
        rlSupport = v.findViewById(R.id.rlSupport);


    }

    private void GetProfileDetailsApiCall() {

        CommonMethod.showProgressDialog(context);

        RequestQueue queue = Volley.newRequestQueue(context);

        StringRequest request = new StringRequest(Request.Method.POST, Constants.BASE_URL + Constants.myprofile, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                CommonMethod.hideProgressDialog(context);

                System.out.println("response_profile_details" + response);

                try {
                    Gson gson = new Gson();
                    System.out.println("resp : " + response);
                    ProfileParser profileParser = gson.fromJson(response, ProfileParser.class);

                    if (profileParser != null) {
                        if (profileParser.responsecode.equalsIgnoreCase("200")) {
                            txtUserName.setText(profileParser.userdata.driver_name);
                            if (profileParser.userdata.address!=null) {
                                txtAddress.setText(profileParser.userdata.address);
                            }else {
                                txtAddress.setVisibility(View.GONE);
                            }

                            Glide.with(context).load(Uri.parse(profileParser.userdata.driver_photo)).placeholder(R.drawable.ic_user_profile).error(R.drawable.ic_user_profile).into(imgProfile);
                        }
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

                context.prefManager.connectDB();
                String driver_id = context.prefManager.getString("driver_id");
                String lang = context.prefManager.getString("language");
                context.prefManager.closeDB();

                HashMap<String, String> params = new HashMap<>();

                params.put("token", Constants.Token);
                params.put("driver_id", driver_id);
                params.put("lng", lang);


                System.out.println("param :" + params);

                return params;
            }
        };
        queue.add(request).setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }


    private void logoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("" + getResources().getString(R.string.sure_logout))
                .setCancelable(false)
                .setPositiveButton("" + getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        context.prefManager.connectDB();
                        context.prefManager.setString("driver_id", "");
                        context.prefManager.setString("rst_profile_id", "");
                        context.prefManager.setString("mobilenumber", "");
                        context.prefManager.setString("country_code", "");
                        context.prefManager.setBoolean("isLogin", false);
                        context.prefManager.closeDB();

                        Intent i = new Intent(context, LoginActivity.class);
                        startActivity(i);
                        getActivity().finishAffinity();

                    }
                })
                .setNegativeButton("" + getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 100) {
                context.finish();
                context.overridePendingTransition(0, 0);
                startActivity(new Intent(context,MainActivity.class));
                context.overridePendingTransition(0, 0);
            }
        }
    }

}