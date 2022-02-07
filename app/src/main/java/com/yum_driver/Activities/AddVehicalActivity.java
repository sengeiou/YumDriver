package com.yum_driver.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.yum_driver.Parsers.AddVehicalParser;
import com.yum_driver.R;
import com.yum_driver.utils.CommonMethod;
import com.yum_driver.utils.Constants;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddVehicalActivity extends BaseActivity {

    private Button btn_submit;
    private ImageView imgBack;
    @BindView(R.id.edVehicalType)
    EditText edVehicalType;
    @BindView(R.id.edVehicalModel)
    EditText edVehicalModel;
    @BindView(R.id.edtVehicalNo)
    EditText edtVehicalNo;
    @BindView(R.id.edtCheseNo)
    EditText edtCheseNo;
    @BindView(R.id.edVehicalColor)
    EditText edVehicalColor;

    private long mLastClickTime = 0;
    private long WaitTime =1500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_vehical);
        ButterKnife.bind(this);

        getIds();


        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (SystemClock.elapsedRealtime() - mLastClickTime < WaitTime){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                if (isValid()) {
                    if (CommonMethod.isOnline(AddVehicalActivity.this)) {
                        RegisterVehicleApiCall();
                    } else {
                        Toast.makeText(AddVehicalActivity.this, getResources().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                    }

                }

            }
        });


    }

    private void RegisterVehicleApiCall() {

        CommonMethod.showProgressDialog(AddVehicalActivity.this);

        RequestQueue queue = Volley.newRequestQueue(AddVehicalActivity.this);

        StringRequest request = new StringRequest(Request.Method.POST, Constants.BASE_URL + Constants.addVehical, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                CommonMethod.hideProgressDialog(AddVehicalActivity.this);
                System.out.println("response_register_vehical: " + response);
                try {

                    Gson gson = new Gson();
                    AddVehicalParser registerParser = gson.fromJson(response, AddVehicalParser.class);

                    if (registerParser != null) {
                        if (registerParser.responsecode.equalsIgnoreCase("200")) {

                            Toast.makeText(AddVehicalActivity.this, registerParser.responsemessage, Toast.LENGTH_SHORT).show();
                            if (getIntent().getBooleanExtra("fromVerifyOTP", false)) {

                                Intent i = new Intent(AddVehicalActivity.this, MainActivity.class);
                                i.putExtra("fromRegister",true);
                                startActivity(i);
                                finish();
                            }
                            if (getIntent().getBooleanExtra("fromVehicalList", false)) {
                                finish();
                            }


                        } else {
                            Toast.makeText(AddVehicalActivity.this, registerParser.responsemessage, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(AddVehicalActivity.this, getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();
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
                params.put("vehicle_type", edVehicalType.getText().toString());
                params.put("vehicle_no", edtVehicalNo.getText().toString().trim());
                params.put("chesis_no", edtCheseNo.getText().toString().trim());
                params.put("vehicle_color", edVehicalColor.getText().toString().trim());
                params.put("vehicle_model", edVehicalModel.getText().toString().trim());
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
        if (edVehicalType.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(this, getResources().getString(R.string.vehical_type), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (edVehicalModel.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(this, getResources().getString(R.string.vehical_model), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (edtVehicalNo.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(this, getResources().getString(R.string.vehical_number),Toast.LENGTH_SHORT).show();
            return false;
        }
//        if (edtVehicalNo.getText().toString().length()<12) {
//            Toast.makeText(this, "Enter vehicle number", Toast.LENGTH_SHORT).show();
//            return false;
//        }
        if (edtCheseNo.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(this, getResources().getString(R.string.vehical_chasis_number), Toast.LENGTH_SHORT).show();
            return false;
        }
//        if (edtCheseNo.getText().toString().length() != 17) {
//            Toast.makeText(this, getResources().getString(R.string.vehical_chasis_valid_number), Toast.LENGTH_SHORT).show();
//            return false;
//        }
        if (edVehicalColor.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(this, getResources().getString(R.string.vehical_color), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void getIds() {

        btn_submit = findViewById(R.id.btn_submit);
        imgBack = findViewById(R.id.imgBack);


    }
}