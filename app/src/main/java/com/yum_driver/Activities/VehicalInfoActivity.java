package com.yum_driver.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.yum_driver.Parsers.AddVehicalParser;
import com.yum_driver.Pojo.VehicalListObject;
import com.yum_driver.R;
import com.yum_driver.utils.CommonMethod;
import com.yum_driver.utils.Constants;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VehicalInfoActivity extends BaseActivity {

    private ImageView imgBack;
    private Button btn_submit;
    private VehicalListObject vehicleObject;
    @BindView(R.id.et_model)
    TextInputEditText et_model;
    @BindView(R.id.et_vehicleNo)
    TextInputEditText et_vehicleNo;
    @BindView(R.id.et_vehicleImage)
    TextInputEditText et_vehicleImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehical_info);
        ButterKnife.bind(this);
        getIds();

        vehicleObject = (VehicalListObject) getIntent().getSerializableExtra("vehicleObject");

        et_model.setText(vehicleObject.vehicle_model);
        et_vehicleNo.setText(vehicleObject.vehicle_regno);
        et_vehicleImage.setText(vehicleObject.vehicle_color);


        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isValid()) {
                    if (CommonMethod.isOnline(VehicalInfoActivity.this)) {
                        RegisterVehicleApiCall();
                    } else {
                        Toast.makeText(VehicalInfoActivity.this, getResources().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                    }

                }
                //startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                // finish();
            }
        });
    }

    private void RegisterVehicleApiCall() {

        CommonMethod.showProgressDialog(VehicalInfoActivity.this);

        RequestQueue queue = Volley.newRequestQueue(VehicalInfoActivity.this);

        StringRequest request = new StringRequest(Request.Method.POST, Constants.BASE_URL + Constants.addVehical, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                CommonMethod.hideProgressDialog(VehicalInfoActivity.this);
                System.out.println("response_register_vehical: " + response);
                try {

                    Gson gson = new Gson();
                    AddVehicalParser registerParser = gson.fromJson(response, AddVehicalParser.class);

                    if (registerParser != null) {
                        if (registerParser.responsecode.equalsIgnoreCase("200")) {

                            Toast.makeText(VehicalInfoActivity.this, registerParser.responsemessage, Toast.LENGTH_SHORT).show();
                            finish();


                        } else {
                            Toast.makeText(VehicalInfoActivity.this, registerParser.responsemessage, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(VehicalInfoActivity.this, getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();
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

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                String currentDateandTime = sdf.format(new Date());

                HashMap<String, String> params = new HashMap<>();

                params.put("token", Constants.Token);
                params.put("driver_id", driver_id);
                params.put("vehicle_type", vehicleObject.vehicle_type);
                params.put("vehicle_no", et_vehicleNo.getText().toString().trim());
                params.put("chesis_no", vehicleObject.chesis_no);
                params.put("vehicle_color", et_vehicleImage.getText().toString().trim());
                params.put("vehicle_model", et_model.getText().toString().trim());
                params.put("vehicle_date_update", currentDateandTime);


                System.out.println("param_register :" + params);

                return params;
            }
        };
        queue.add(request).setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    private boolean isValid() {

        if (et_model.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(this, getResources().getString(R.string.vehical_model), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (et_vehicleNo.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(this, getResources().getString(R.string.vehical_number), Toast.LENGTH_SHORT).show();
            return false;
        }
//        if (et_vehicleNo.getText().toString().length() < 12) {
//            Toast.makeText(this, "Enter vehicle number", Toast.LENGTH_SHORT).show();
//            return false;
//        }

        if (et_vehicleImage.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(this, getResources().getString(R.string.vehical_color), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void getIds() {
        imgBack = findViewById(R.id.imgBack);
        btn_submit = findViewById(R.id.btn_submit);
    }
}