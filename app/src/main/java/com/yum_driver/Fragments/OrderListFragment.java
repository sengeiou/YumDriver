package com.yum_driver.Fragments;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.yum_driver.Activities.LoginActivity;
import com.yum_driver.Activities.MainActivity;
import com.yum_driver.Activities.MishapActivity;
import com.yum_driver.Adapters.OrderAdapter;
import com.yum_driver.Interfaces.RecyclerViewClickListener;
import com.yum_driver.Parsers.CommonParser;
import com.yum_driver.Parsers.OrderParser;
import com.yum_driver.Pojo.OrderObject;
import com.yum_driver.R;
import com.yum_driver.services.LocationService;
import com.yum_driver.utils.AutoStartService;
import com.yum_driver.utils.CommonMethod;
import com.yum_driver.utils.Constants;
import com.yum_driver.utils.GPSTracker;
import com.yum_driver.utils.SharedPreferenceManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;


public class OrderListFragment extends Fragment implements OnMapReadyCallback, LocationListener {

    private MainActivity context;
    private View v;
    private TextView txtSwitchStatus;
    private RecyclerView rvNotifications;
    private ImageView imgMishap, imageView7;
    private GoogleMap mMap;
    private RelativeLayout lytMain, lytOffline, lytNoOrders, rlOnOff;
    private LinearLayout lytGray;
    private static final int PERMISSION_REQUEST_CODE = 200;
    private Switch switchOnline;
    private GPSTracker gps;
    private double latitude;
    private double longitude;
    private List<Address> addresses;
    private String address, City;
    private SharedPreferenceManager prefManager;
    private LocationBroadcastReceiver locationBroadcastReceiver;
    private MarkerOptions marker;

    int countclick = 0;
    private OrderAdapter myOrdersAdapter;
    private ArrayList<OrderObject> order_list;
    private Dialog dialog;
    private ProgressDialog pDialog1, pDialog2, pDialog3;

    private long mLastClickTime = 0;
    private long WaitTime = 1500;
    private boolean isDeactive = false;
    boolean fromRegister = false;
    Intent mServiceIntent;
    private AutoStartService mAutoStartService;
    private AlertDialog alertDialog;
    private boolean isPermissiomGranted= false;


    public OrderListFragment(MainActivity context, boolean fromRegister) {
        this.context = context;
        this.fromRegister = fromRegister;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_order_list, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        prefManager = new SharedPreferenceManager(context);

        dialog = new Dialog(context);


        imgMishap = v.findViewById(R.id.img_mishap);
        imageView7 = v.findViewById(R.id.imageView7);

        imgMishap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mishap = new Intent(getActivity(), MishapActivity.class);
                startActivity(mishap);
            }
        });

        txtSwitchStatus = v.findViewById(R.id.txtSwitchStatus);
        rvNotifications = v.findViewById(R.id.rv_orders);
        lytMain = v.findViewById(R.id.lytMain);
        lytNoOrders = v.findViewById(R.id.lytNoOrders);
        lytOffline = v.findViewById(R.id.lytOffline);
        switchOnline = v.findViewById(R.id.switchOnline);
        lytGray = v.findViewById(R.id.lytGray);
        rlOnOff = v.findViewById(R.id.rlOnOff);

        rvNotifications.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        rvNotifications.setHasFixedSize(false);
        rvNotifications.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));

        if (!checkPermission()) {

            GetPermissions(context);
//            requestPermission();
        } else {
            isPermissiomGranted = true;
            enableLocation();
        }

//        imageView7.setVisibility(View.GONE);
//        lytOffline.setVisibility(View.VISIBLE);

        imageView7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkPermission()) {
                    //  requestPermission();
                    GetPermissions(context);
                } else {
                    enableLocation();
                }
            }
        });

        if (CommonMethod.isOnline(context)) {

            if (checkPermission() && isGPSEnable()) {
                GetStatusDetailsApiCall();
            } else {
                enableLocation();
            }
        } else {
            Toast.makeText(context, "" + getResources().getString(R.string.no_internet_connection), Toast.LENGTH_LONG).show();
        }

        switchOnline.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                // mis-clicking prevention, using threshold of 1000 ms
                if (SystemClock.elapsedRealtime() - mLastClickTime < WaitTime) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                if (CommonMethod.isOnline(context)) {
                    ChangeDriverStatus();
                } else {
                    Toast.makeText(context, "" + getResources().getString(R.string.no_internet_connection), Toast.LENGTH_LONG).show();
                }


            }
        });

        startLocationService();


        return v;
    }

    private void GetPermissions(Context context) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dia_permission, null);
        dialogBuilder.setView(dialogView);

        Button btn_no = (Button) dialogView.findViewById(R.id.btn_no);
        Button btn_yes = (Button) dialogView.findViewById(R.id.btn_yes);
        ImageView imgClose = (ImageView) dialogView.findViewById(R.id.imgClose);

        alertDialog = dialogBuilder.create();


        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        btn_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        btn_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                requestPermission();
            }
        });
        alertDialog.show();
    }

    private boolean isGPSEnable() {

        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }
        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        if (!gps_enabled && !network_enabled) {

            return false;
            // notify user

//            new AlertDialog.Builder(context)
//                    .setMessage(R.string.gps_enable)
//                    .setPositiveButton(R.string.gps_setting, new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
//                            context.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
//                        }
//                    })
//                    .setNegativeButton(R.string.cancel,null)
//                    .show();
        } else {
            return true;
        }
    }

    private void GetStatusDetailsApiCall() {

        showProgressDialog();

        RequestQueue queue = Volley.newRequestQueue(context);

        StringRequest request = new StringRequest(Request.Method.POST, Constants.BASE_URL + Constants.getonlinestatus, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                hideProgressDialog();

                System.out.println("response_driver_get_status" + response);
                rlOnOff.setVisibility(View.VISIBLE);

                try {
                    Gson gson = new Gson();
                    CommonParser commonParser = gson.fromJson(response, CommonParser.class);

                    if (commonParser != null) {
                        if (commonParser.responsecode.equalsIgnoreCase("200")) {
                            try {

                                if (!commonParser.driver_status.equalsIgnoreCase("active")) {
                                    //logout by admin
                                    logoutDialog();
                                    isDeactive = true;
                                }
                                if (commonParser.isonline.equalsIgnoreCase("y")) {
                                    switchOnline.setChecked(true);
                                    lytGray.setVisibility(View.GONE);
                                    lytNoOrders.setVisibility(View.VISIBLE);
                                    imageView7.setVisibility(View.VISIBLE);
                                } else {
                                    lytGray.setVisibility(View.VISIBLE);
                                    switchOnline.setChecked(false);
                                    lytMain.setVisibility(View.GONE);
                                    lytNoOrders.setVisibility(View.GONE);
                                    lytOffline.setVisibility(View.VISIBLE);
                                    imageView7.setVisibility(View.GONE);

                                    // stop location service
//                                    mAutoStartService = new AutoStartService();
//                                    mServiceIntent = new Intent(context, AutoStartService.class);
//                                    mServiceIntent.setAction(Constants.ACTION_START_LOCATION_SERVICE);
//                                    if (!isMyServiceRunning(AutoStartService.class)) {
//                                        context.stopService(mServiceIntent);
//                                        stopLocationBroadcastReceiver();
//                                    }

                                }


                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(context, commonParser.responsemessage, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(context, "" + getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();
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
                hideProgressDialog();
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
                params.put("lng", language);

                System.out.println("param_changeStatus:" + params);

                return params;
            }
        };
        queue.add(request).setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


    }

    private void logoutDialog() {

        String sms = "";

        if (fromRegister) {
            sms = "" + getResources().getString(R.string.need_admin_approval);
        } else {
            sms = "" + getResources().getString(R.string.logout_by_admin);

        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setMessage("" + sms)
                .setCancelable(false)
                .setPositiveButton("" + getResources().getString(R.string.okay), new DialogInterface.OnClickListener() {
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
                });

        AlertDialog alert = builder.create();
        alert.show();


    }

    // Driver Status Change
    private void ChangeDriverStatus() {
        showProgressDialog2();

        RequestQueue queue = Volley.newRequestQueue(context);

        StringRequest request = new StringRequest(Request.Method.POST, Constants.BASE_URL + Constants.setonlinestatus, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                hideProgressDialog2();

                System.out.println("response_driver_status_change" + response);

                try {
                    Gson gson = new Gson();
                    CommonParser commonParser1 = gson.fromJson(response, CommonParser.class);
                    if (commonParser1 != null) {
                        if (commonParser1.responsecode.equalsIgnoreCase("200")) {
                            try {
                                if (commonParser1.isonline.equalsIgnoreCase("y")) {
                                    lytGray.setVisibility(View.GONE);
                                    txtSwitchStatus.setText(getResources().getString(R.string.online));
                                    if (CommonMethod.isOnline(context)) {
                                        GetOrderListApiCall();
                                    } else {
                                        Toast.makeText(context, "" + getResources().getString(R.string.no_internet_connection), Toast.LENGTH_LONG).show();
                                    }

                                } else {
                                    lytGray.setVisibility(View.VISIBLE);
                                    lytMain.setVisibility(View.GONE);
                                    lytNoOrders.setVisibility(View.GONE);
                                    lytOffline.setVisibility(View.VISIBLE);
                                    imageView7.setVisibility(View.GONE);
                                    imgMishap.setVisibility(View.GONE);
                                    txtSwitchStatus.setText(getResources().getString(R.string.offline));
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(context, commonParser1.responsemessage, Toast.LENGTH_SHORT).show();
                        }
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("err:" + e.toString());
                    hideProgressDialog2();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgressDialog2();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {

                prefManager.connectDB();
                String driver_id = prefManager.getString("driver_id");
                String language = prefManager.getString("language");
                prefManager.closeDB();

                String status;
                if (switchOnline.isChecked()) {
                    status = "y";
                } else {
                    status = "n";
                }

                HashMap<String, String> params = new HashMap<>();
                params.put("token", Constants.Token);
                params.put("driver_id", driver_id);
                params.put("online_status", status);
                params.put("lng", language);

                System.out.println("param_checkDriverStatus :" + params);

                return params;
            }
        };
        queue.add(request).setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }

    private void GetOrderListApiCall() {

        showProgressDialog3();

        RequestQueue queue = Volley.newRequestQueue(context);

        StringRequest request = new StringRequest(Request.Method.POST, Constants.BASE_URL + Constants.orderlist, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                hideProgressDialog3();

                System.out.println("response_order_list" + response);

                try {
                    Gson gson = new Gson();
                    OrderParser orderOrder = gson.fromJson(response, OrderParser.class);
                    if (orderOrder != null) {
                        if (orderOrder.responsecode.equalsIgnoreCase("200")) {
                            try {
                                if (!orderOrder.order_list.isEmpty()) {

                                    lytMain.setVisibility(View.GONE);
                                    lytNoOrders.setVisibility(View.GONE);
                                    lytOffline.setVisibility(View.GONE);


                                    if (orderOrder.order_list.size() > 0) {
                                        ShowBottomOrderList(orderOrder);
                                    } else {
                                        dialog.dismiss();
                                    }


                                } else {
                                    imgMishap.setVisibility(View.GONE);
                                    imageView7.setVisibility(View.VISIBLE);
                                    lytMain.setVisibility(View.GONE);
                                    lytNoOrders.setVisibility(View.VISIBLE);
                                    lytOffline.setVisibility(View.GONE);
                                    dialog.dismiss();
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            // Toast.makeText(context, orderOrder.responsemessage, Toast.LENGTH_SHORT).show();
                            imgMishap.setVisibility(View.GONE);
                            imageView7.setVisibility(View.VISIBLE);
                            dialog.dismiss();
                            lytMain.setVisibility(View.GONE);
                            lytNoOrders.setVisibility(View.VISIBLE);
                            lytOffline.setVisibility(View.GONE);
                        }
                    } else {
                        Toast.makeText(context, "" + getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();
                        imgMishap.setVisibility(View.GONE);
                        imageView7.setVisibility(View.VISIBLE);
                        dialog.dismiss();
                        lytMain.setVisibility(View.GONE);
                        lytNoOrders.setVisibility(View.VISIBLE);
                        lytOffline.setVisibility(View.GONE);
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("err:" + e.toString());
                    hideProgressDialog3();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                hideProgressDialog3();
                imgMishap.setVisibility(View.GONE);
                imageView7.setVisibility(View.VISIBLE);
                dialog.dismiss();
                lytMain.setVisibility(View.GONE);
                lytNoOrders.setVisibility(View.VISIBLE);
                lytOffline.setVisibility(View.GONE);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {


                prefManager.connectDB();
                String driver_id = prefManager.getString("driver_id");
                String driver_lat = prefManager.getString("currentLat");
                String driver_lng = prefManager.getString("currentLng");
                String language = prefManager.getString("language");
                prefManager.closeDB();

                System.out.println("CurrLat :" + driver_lat + " ,long : " + driver_lng);

                HashMap<String, String> params = new HashMap<>();

                params.put("token", Constants.Token);
                params.put("driver_id", driver_id);
                params.put("driver_lat", driver_lat);
                params.put("driver_lng", driver_lng);
                params.put("order_status", "pending");
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

    private void AcceptOrderDetails(String rst_id, String order_id, String status) {

        showProgressDialog();

        RequestQueue queue = Volley.newRequestQueue(context);

        StringRequest request = new StringRequest(Request.Method.POST, Constants.BASE_URL + Constants.orderstatusupdate, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                hideProgressDialog();

                System.out.println("response_order_list" + response);

                try {
                    Gson gson = new Gson();
                    CommonParser commonParser = gson.fromJson(response, CommonParser.class);
                    if (commonParser != null) {
                        if (commonParser.responsecode.equalsIgnoreCase("200")) {

                            if (status.equalsIgnoreCase("reject")) {
                                MediaPlayer mMediaPlayer = MediaPlayer.create(context, R.raw.order_rejected);
                                mMediaPlayer.setVolume(100, 100);
                                if (mMediaPlayer.isPlaying()) {
                                    mMediaPlayer.stop();
                                }
                                mMediaPlayer.start();
                                mMediaPlayer.setLooping(false);
                            }
                            Toast.makeText(context, "" + commonParser.responsemessage, Toast.LENGTH_SHORT).show();
                            GetOrderListApiCall();
                        } else {
                            Toast.makeText(context, commonParser.responsemessage, Toast.LENGTH_SHORT).show();

                        }
                    } else {
                        Toast.makeText(context, "" + getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();

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

                hideProgressDialog();
                imgMishap.setVisibility(View.GONE);
                imageView7.setVisibility(View.VISIBLE);

                lytMain.setVisibility(View.GONE);
                lytNoOrders.setVisibility(View.VISIBLE);
                lytOffline.setVisibility(View.GONE);
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
                params.put("rstid", rst_id);
                params.put("order_id", order_id);
                params.put("assigned_status", status);

                System.out.println("param_Accept_orderList :" + params);

                return params;
            }
        };
        queue.add(request).setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }


    private void ShowBottomOrderList(OrderParser orderOrder) {
        dialog.dismiss();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        BottomSheetDialog dialog = new BottomSheetDialog(context);
        dialog.setContentView(R.layout.layout_active_orders);


        LinearLayout rlNoItem = dialog.findViewById(R.id.rlNoItem);

        RecyclerView rv_orders = dialog.findViewById(R.id.rv_orders);
        rv_orders.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        rv_orders.setHasFixedSize(true);
        rv_orders.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));


        if (orderOrder.order_list.size() > 0) {

            rv_orders.setVisibility(View.VISIBLE);
            rlNoItem.setVisibility(View.GONE);

            myOrdersAdapter = new OrderAdapter(context, orderOrder.order_list, new RecyclerViewClickListener() {
                @Override
                public void onRecyclerViewListClicked(int position, Boolean toggleOnm, String status) {
                    if (CommonMethod.isOnline(context)) {
                        AcceptOrderDetails(orderOrder.order_list.get(position).rst_id, orderOrder.order_list.get(position).order_id, status);
                    } else {
                        Toast.makeText(context, "" + getResources().getString(R.string.no_internet_connection), Toast.LENGTH_LONG).show();
                    }
                    dialog.dismiss();
                }
            });
            rv_orders.setAdapter(myOrdersAdapter);
        } else {

            rv_orders.setVisibility(View.GONE);

            imgMishap.setVisibility(View.GONE);
            imageView7.setVisibility(View.VISIBLE);
            lytMain.setVisibility(View.GONE);
            lytNoOrders.setVisibility(View.VISIBLE);
            lytOffline.setVisibility(View.GONE);

        }


        dialog.show();

        if (isDeactive) {
            dialog.dismiss();
        }
    }


    public void showProgressDialog() {
        pDialog1 = ProgressDialog.show(context, null, null, true);
        pDialog1.setContentView(R.layout.progress_dialog);
        pDialog1.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    public void hideProgressDialog() {
        if (pDialog1 != null) {
            pDialog1.dismiss();
        }
    }

    public void showProgressDialog2() {
        pDialog2 = ProgressDialog.show(context, null, null, true);
        pDialog2.setContentView(R.layout.progress_dialog);
        pDialog2.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    public void hideProgressDialog2() {
        if (pDialog2 != null) {
            pDialog2.dismiss();
        }
    }

    public void showProgressDialog3() {
        pDialog3 = ProgressDialog.show(context, null, null, true);
        pDialog3.setContentView(R.layout.progress_dialog);
        pDialog3.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    public void hideProgressDialog3() {
        if (pDialog3 != null) {
            pDialog3.dismiss();
        }
    }


    @Override
    public void onLocationChanged(@NonNull Location location) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
/*
        if (!checkPermission()) {
            GetPermissions(context);
          //  requestPermission();
        } else {
            enableLocation();
        }*/
    }

    private void enableLocation() {
        gps = new GPSTracker(context);

        if (gps.canGetLocation()) {

            if (gps.getLocation() == null) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        enableLocation();
                    }
                }, 2000);
                return;
            }

            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
            try {
                Geocoder gcd = new Geocoder(context, Locale.getDefault());

                try {
                    addresses = gcd.getFromLocation(latitude, longitude, 1);
                    if (addresses.size() > 0) {
                        address = addresses.get(0).getAddressLine(0);
                        City = addresses.get(0).getLocality();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("latitude: " + latitude + "," + "longitude: " + longitude);
                if (Double.parseDouble(String.valueOf(latitude)) == 0.00 || Double.parseDouble(String.valueOf(longitude)) == 0.00) {
                    Toast.makeText(context, getResources().getString(R.string.location_not_availabl), Toast.LENGTH_SHORT).show();
                } else {
                    prefManager.connectDB();
                    prefManager.setString("Address", address);
                    prefManager.setString("latitude", String.valueOf(latitude));
                    prefManager.setString("longitude", String.valueOf(longitude));
                    prefManager.setString("City", String.valueOf(City));
                    prefManager.closeDB();

                    LatLng sydney = new LatLng(latitude, longitude);
                    mMap.clear();
                    marker = new MarkerOptions().position(sydney).title("You are here!");
                    marker.icon(bitmapDescriptorFromVector(context, (R.drawable.ic_markers)));
                    mMap.addMarker(marker);
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(15.0f));

                }
            } catch (Exception e) {
                e.getMessage();
                System.out.println("exception :" + e);
            }
        } else {

            if(isPermissiomGranted) {
                gps.showSettingsAlert();
            }

        }
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(getActivity(), new
                String[]{ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_CODE);
    }

    public boolean checkPermission() {
        int result3 = ContextCompat.checkSelfPermission(getActivity(),
                ACCESS_FINE_LOCATION);
        int result4 = ContextCompat.checkSelfPermission(getActivity(),
                ACCESS_COARSE_LOCATION);

        return result3 == PackageManager.PERMISSION_GRANTED
                && result4 == PackageManager.PERMISSION_GRANTED;

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        Log.e("value", "Code: " + requestCode);
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("value", "Permission Granted, Location ON .");
                    //enableLocation();
                        alertDialog.dismiss();

                   /* if( checkPermission() && isGPSEnable() ) {
                        GetStatusDetailsApiCall();
                    }else {
                        enableLocation();
                    }*/
                } else {
                    Log.e("value", "Permission Denied, You cannot use local drive .");
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        alertDialog.dismiss();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!checkPermission()) {
           // GetPermissions(context);
            // requestPermission();
        } else {
            mAutoStartService = new AutoStartService();
            mServiceIntent = new Intent(context, AutoStartService.class);
            mServiceIntent.setAction(Constants.ACTION_START_LOCATION_SERVICE);
            if (!isMyServiceRunning(AutoStartService.class)) {
                context.startService(mServiceIntent);
            }
        }
        startLocationBroadcastReceiverIfAppropriate();
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i("isMyServiceRunning?", true + "");
                return true;
            }
        }
        Log.i("isMyServiceRunning?", false + "");
        return false;
    }

    private class LocationBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.getAction().equals(Constants.LOCATION_MESSAGE_ID)) {
                LatLng currentLatLng = intent.getParcelableExtra("newLatLng");

                locationWasUpdated(currentLatLng);
            }
        }
    }

    private Boolean isLocationServiceRunning() {
        ActivityManager activityManager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);

        if (activityManager != null) {
            for (ActivityManager.RunningServiceInfo runningServiceInfo : activityManager.getRunningServices(Integer.MAX_VALUE)) {
                if (LocationService.class.getName().equalsIgnoreCase(runningServiceInfo.service.getClassName())) {
                    if (runningServiceInfo.foreground) {
                        return true;
                    }
                }
            }
            return false;
        }
        return false;
    }

    private void startLocationService() {
        if (!isLocationServiceRunning()) {
            Intent intent = new Intent(getActivity(), LocationService.class);
            intent.setAction(Constants.ACTION_START_LOCATION_SERVICE);
            context.startService(intent);
        }
    }

    private void startLocationBroadcastReceiverIfAppropriate() {
        if (mMap != null && locationBroadcastReceiver == null) {
            try {
                locationBroadcastReceiver = new LocationBroadcastReceiver();
                getActivity().registerReceiver(locationBroadcastReceiver, new IntentFilter(Constants.LOCATION_MESSAGE_ID));

                Log.e("MPACT", "started");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startLocationBroadcastReceiverIfAppropriate();
                }
            }, 100);
        }
    }

    private void stopLocationBroadcastReceiver() {
        try {
            getActivity().unregisterReceiver(locationBroadcastReceiver);
            locationBroadcastReceiver = null;

            Log.e("MPACT", "stopped");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        stopLocationBroadcastReceiver();
    }

    public void locationWasUpdated(LatLng newLocation) {
        if (newLocation == null) {
            return;
        }
        try {
            LatLng eastLocation = getLatLngAway(newLocation, 90, 1000);
            LatLng westLocation = getLatLngAway(newLocation, -90, 1000);

            LatLngBounds bounds = new LatLngBounds.Builder().include(newLocation).include(eastLocation).include(westLocation).build();
            //mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 20));

        } catch (Exception e) {
        }
    }

    private LatLng getLatLngAway(LatLng fromLatLng, double bearing, float distance) {
        try {
            distance = distance / 6371;
            bearing = toRad(bearing);

            double lat1 = toRad(fromLatLng.latitude);
            double lon1 = toRad(fromLatLng.longitude);

            double lat2 = Math.asin(Math.sin(lat1) * Math.cos(distance) +
                    Math.cos(lat1) * Math.sin(distance) * Math.cos(bearing));

            double lon2 = lon1 + Math.atan2(Math.sin(bearing) * Math.sin(distance) *
                            Math.cos(lat1),
                    Math.cos(distance) - Math.sin(lat1) *
                            Math.sin(lat2));

            if (java.lang.Double.isNaN(lat2) || java.lang.Double.isNaN(lon2)) return null;

            return new LatLng(toDeg(lat2), toDeg(lon2));
        } catch (Exception e) {
            return fromLatLng;
        }
    }

    private double toRad(double number) {
        return number * Math.PI / 180;
    }

    private double toDeg(double number) {
        return number * 180 / Math.PI;
    }
}