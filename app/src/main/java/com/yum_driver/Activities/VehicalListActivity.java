package com.yum_driver.Activities;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.yum_driver.Adapters.VehicalListAdapter;
import com.yum_driver.Interfaces.VehicleObjectClick;
import com.yum_driver.Parsers.CommonParser;
import com.yum_driver.Parsers.VehicalListParser;
import com.yum_driver.Pojo.VehicalListObject;
import com.yum_driver.R;
import com.yum_driver.utils.CommonMethod;
import com.yum_driver.utils.Constants;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class VehicalListActivity extends BaseActivity {

    private ImageView imgBack;
    private Button btn_submit;
    private TextView txtNoVehical;
    private RecyclerView rvVehicalList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehical_list);

        getIds();
        rvVehicalList.setLongClickable(true);


        if (CommonMethod.isOnline(VehicalListActivity.this)) {
            VehicleListApiCall();
        } else {
            Toast.makeText(VehicalListActivity.this, getResources().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
        }

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), AddVehicalActivity.class);
                i.putExtra("fromVehicalList", true);
                startActivity(i);
                finish();
            }
        });
    }

    private void VehicleListApiCall() {
        CommonMethod.showProgressDialog(VehicalListActivity.this);

        RequestQueue queue = Volley.newRequestQueue(VehicalListActivity.this);

        StringRequest request = new StringRequest(Request.Method.POST, Constants.BASE_URL + Constants.vehicalList, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                CommonMethod.hideProgressDialog(VehicalListActivity.this);
                System.out.println("response_list_vehical: " + response);
                try {

                    Gson gson = new Gson();
                    VehicalListParser registerParser = gson.fromJson(response, VehicalListParser.class);

                    if (registerParser != null) {
                        if (registerParser.responsecode.equalsIgnoreCase("200")) {

                            if (registerParser.data.isEmpty()) {
                                txtNoVehical.setVisibility(View.VISIBLE);
                                rvVehicalList.setVisibility(View.GONE);
                            } else {
                                txtNoVehical.setVisibility(View.GONE);
                                rvVehicalList.setVisibility(View.VISIBLE);
                                VehicalListAdapter adapter = new VehicalListAdapter(VehicalListActivity.this, registerParser.data, new VehicleObjectClick() {
                                    @Override
                                    public void onRecyclerViewListClicked(int position, VehicalListObject object, String status) {
                                        if (status.equalsIgnoreCase("select")) {
                                            selectDefaultVehicalApi(object);
                                        } else {

                                            AlertDialog.Builder builder = new AlertDialog.Builder(VehicalListActivity.this);
                                            builder.setMessage("" + getResources().getString(R.string.sure_delete_vehicle))
                                                    .setCancelable(false)
                                                    .setPositiveButton("" + getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int id) {
                                                            DeleteVehicaleApi(object);
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
                                        System.out.println("status: " + status);

                                    }
                                });
                                rvVehicalList.setAdapter(adapter);
                            }

                        } else
                       {
                           txtNoVehical.setVisibility(View.VISIBLE);
                           rvVehicalList.setVisibility(View.GONE);
                           // Toast.makeText(VehicalListActivity.this, registerParser.responsemessage, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(VehicalListActivity.this, getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("err:" + e.toString());
                    CommonMethod.hideProgressDialog(getApplicationContext());
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CommonMethod.hideProgressDialog(getApplicationContext());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {

                prefManager.connectDB();
                String language = prefManager.getString("language");
                String driver_id = prefManager.getString("driver_id");
                prefManager.closeDB();


                HashMap<String, String> params = new HashMap<>();

                params.put("token", Constants.Token);
                params.put("driver_id", driver_id);

                System.out.println("param_vehicalelist :" + params);

                return params;
            }
        };
        queue.add(request).setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    private void selectDefaultVehicalApi(VehicalListObject object) {

        CommonMethod.showProgressDialog(VehicalListActivity.this);

        RequestQueue queue = Volley.newRequestQueue(VehicalListActivity.this);

        StringRequest request = new StringRequest(Request.Method.POST, Constants.BASE_URL + Constants.setDefalutVehicle, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                CommonMethod.hideProgressDialog(VehicalListActivity.this);
                System.out.println("response_list_vehical_set: " + response);
                try {

                    Gson gson = new Gson();
                    CommonParser registerParser = gson.fromJson(response, CommonParser.class);

                    if (registerParser != null) {
                        if (registerParser.responsecode.equalsIgnoreCase("200")) {
                            VehicleListApiCall();
                        } else {
                            Toast.makeText(VehicalListActivity.this, registerParser.responsemessage, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(VehicalListActivity.this, getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("err:" + e.toString());
                    CommonMethod.hideProgressDialog(getApplicationContext());
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CommonMethod.hideProgressDialog(getApplicationContext());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {

                prefManager.connectDB();
                String language = prefManager.getString("language");
                String driver_id = prefManager.getString("driver_id");
                prefManager.closeDB();


                HashMap<String, String> params = new HashMap<>();

                params.put("token", Constants.Token);
                params.put("driver_id", driver_id);
                params.put("vehicle_no", object.vehicle_regno);

                System.out.println("param_vehicaledefault :" + params);

                return params;
            }
        };
        queue.add(request).setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }

    private void DeleteVehicaleApi(VehicalListObject object) {

        CommonMethod.showProgressDialog(VehicalListActivity.this);

        RequestQueue queue = Volley.newRequestQueue(VehicalListActivity.this);

        StringRequest request = new StringRequest(Request.Method.POST, Constants.BASE_URL + Constants.setDeleteVehicle, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                CommonMethod.hideProgressDialog(VehicalListActivity.this);
                System.out.println("response_list_vehical_delete: " + response);
                try {

                    Gson gson = new Gson();
                    CommonParser registerParser = gson.fromJson(response, CommonParser.class);

                    if (registerParser != null) {
                        if (registerParser.responsecode.equalsIgnoreCase("200")) {

                                VehicleListApiCall();

                        } else {
                            Toast.makeText(VehicalListActivity.this, registerParser.responsemessage, Toast.LENGTH_SHORT).show();
                        }
                    }else if(registerParser.responsecode.equalsIgnoreCase("202")){
                        VehicleListApiCall();
                    }
                    else {
                        Toast.makeText(VehicalListActivity.this, getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("err:" + e.toString());
                    CommonMethod.hideProgressDialog(getApplicationContext());
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CommonMethod.hideProgressDialog(getApplicationContext());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {

                prefManager.connectDB();
                String language = prefManager.getString("language");
                String driver_id = prefManager.getString("driver_id");
                prefManager.closeDB();


                HashMap<String, String> params = new HashMap<>();

                params.put("token", Constants.Token);
                params.put("driver_id", driver_id);
                params.put("vehicle_no", object.vehicle_regno);

                System.out.println("param_vehicaledefault_del :" + params);

                return params;
            }
        };
        queue.add(request).setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }



    private void getIds() {

        rvVehicalList = findViewById(R.id.rvVehicalList);
        imgBack = findViewById(R.id.imgBack);
        btn_submit = findViewById(R.id.btn_submit);
        txtNoVehical = findViewById(R.id.txtNoVehical);

        rvVehicalList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvVehicalList.setHasFixedSize(false);


    }


}