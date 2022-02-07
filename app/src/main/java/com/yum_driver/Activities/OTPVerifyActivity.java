package com.yum_driver.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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
import com.yum_driver.Parsers.LoginParser;
import com.yum_driver.Parsers.OtpVerifyParser;
import com.yum_driver.R;
import com.yum_driver.utils.CommonMethod;
import com.yum_driver.utils.Constants;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class OTPVerifyActivity extends BaseActivity {
    private Button btn_continue;
    private ImageView imgBack;
    TextInputEditText edtOtp1, edtOtp2, edtOtp3, edtOtp4, edtOtp5;
    private boolean fromRegister,fromLogin;
    private String mobileNo, countryCode, otp, inputOtp;
    private TextView txtResendOtp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_o_t_p_verify);
        getIds();

        edtOtp1.requestFocus();

        edtOtp1.addTextChangedListener(new OTPVerifyActivity.GenericTextWatcher(edtOtp1));
        edtOtp2.addTextChangedListener(new OTPVerifyActivity.GenericTextWatcher(edtOtp2));
        edtOtp3.addTextChangedListener(new OTPVerifyActivity.GenericTextWatcher(edtOtp3));
        edtOtp4.addTextChangedListener(new OTPVerifyActivity.GenericTextWatcher(edtOtp4));
        edtOtp5.addTextChangedListener(new OTPVerifyActivity.GenericTextWatcher(edtOtp5));


        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        fromRegister = getIntent().getBooleanExtra("fromRegister", false);
        fromLogin = getIntent().getBooleanExtra("fromLogin", false);

        if (fromRegister) {
            mobileNo = getIntent().getStringExtra("mobileNo");
            countryCode = getIntent().getStringExtra("countryCode");
            otp = getIntent().getStringExtra("otp");

            System.out.println("OTPCODE :"+otp);
        }
        if (fromLogin) {
            mobileNo = getIntent().getStringExtra("mobileNo");
            countryCode = getIntent().getStringExtra("countryCode");
            otp = getIntent().getStringExtra("otpCode");
        }

        txtResendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (CommonMethod.isOnline(OTPVerifyActivity.this)) {
                    resendOtpApiCall();
                } else {
                    Toast.makeText(OTPVerifyActivity.this, getResources().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isValid()) {
                    System.out.println("inputOtp :"+inputOtp);
                    System.out.println("otp :"+otp);
                    if(inputOtp.equals(otp)) {
                        if (CommonMethod.isOnline(OTPVerifyActivity.this)) {
                            OTPVerifyApiCall();
                        } else {
                            Toast.makeText(OTPVerifyActivity.this, getResources().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        Toast.makeText(OTPVerifyActivity.this, getResources().getString(R.string.enter_valid_otp), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


    }

    private void resendOtpApiCall() {

        CommonMethod.showProgressDialog(OTPVerifyActivity.this);

        RequestQueue queue = Volley.newRequestQueue(OTPVerifyActivity.this);

        StringRequest request = new StringRequest(Request.Method.POST, Constants.BASE_URL + Constants.otpLogin, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                CommonMethod.hideProgressDialog(OTPVerifyActivity.this);
                System.out.println("response_loginByOtp: " + response);
                try {

                    Gson gson = new Gson();
                    LoginParser loginParser = gson.fromJson(response, LoginParser.class);

                    if (loginParser != null) {

                        if (loginParser.responsecode.equalsIgnoreCase("200")) {

                            edtOtp1.setText("");
                            edtOtp2.setText("");
                            edtOtp3.setText("");
                            edtOtp4.setText("");
                            edtOtp5.setText("");
                            edtOtp1.requestFocus();
                            Toast.makeText(OTPVerifyActivity.this,getResources().getString(R.string.otp_sent_success), Toast.LENGTH_SHORT).show();


                        } else {
                            Toast.makeText(OTPVerifyActivity.this, loginParser.responsemessage, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(OTPVerifyActivity.this, getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();
                    }


                } catch (Exception e) {
                    e.printStackTrace();
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
                String FCM_TOKEN = prefManager.getString("FCM_TOKEN");
                prefManager.closeDB();

                HashMap<String, String> params = new HashMap<>();

                params.put("token", Constants.Token);
                params.put("country_code", countryCode);
                params.put("mobilenumber", mobileNo);
                params.put("is_resend", "Yes");
               // params.put("fcm_token", FCM_TOKEN);

                System.out.println("param_loginByOtp :" + params);

                return params;
            }
        };
        queue.add(request).setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }

    private boolean isValid() {

        inputOtp = edtOtp1.getText().toString() + edtOtp2.getText().toString() + edtOtp3.getText().toString() + edtOtp4.getText().toString() + edtOtp5.getText().toString();


        if (inputOtp.equalsIgnoreCase("")) {
            Toast.makeText(this, getResources().getString(R.string.enter_otp), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (inputOtp.length() < 4) {
            Toast.makeText(this, getResources().getString(R.string.enter_valid_otp), Toast.LENGTH_SHORT).show();
            return false;
        }


        return true;
    }

    private void OTPVerifyApiCall() {

        CommonMethod.showProgressDialog(OTPVerifyActivity.this);

        RequestQueue queue = Volley.newRequestQueue(OTPVerifyActivity.this);

        StringRequest request = new StringRequest(Request.Method.POST, Constants.BASE_URL + Constants.confirmOtp, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                CommonMethod.hideProgressDialog(OTPVerifyActivity.this);
                System.out.println("response_OTP_verify: " + response);
                try {

                    Gson gson = new Gson();
                    OtpVerifyParser otpParser = gson.fromJson(response, OtpVerifyParser.class);

                    if (otpParser != null) {
                        if (otpParser.responsecode.equalsIgnoreCase("200")) {

                            Toast.makeText(OTPVerifyActivity.this, otpParser.responsemessage, Toast.LENGTH_SHORT).show();


                            if (fromRegister) {
                                prefManager.connectDB();
                                prefManager.setString("driver_id",otpParser.data.driver_id);
                                prefManager.setString("rst_profile_id",otpParser.data.rst_profile_id);
                                prefManager.setString("mobilenumber",otpParser.data.driver_mobile);
                                prefManager.setString("country_code",otpParser.data.driver_countrycode);
                                prefManager.setBoolean("isLogin",true);
                                prefManager.closeDB();

                                Intent i = new Intent(getApplicationContext(), AddVehicalActivity.class);
                                i.putExtra("fromVerifyOTP", true);
                                startActivity(i);
                                finish();
                            }

                            if (fromLogin) {
                                prefManager.connectDB();
                                prefManager.setString("driver_id",otpParser.data.driver_id);
                                prefManager.setString("rst_profile_id",otpParser.data.rst_profile_id);
                                prefManager.setString("mobilenumber",otpParser.data.driver_mobile);
                                prefManager.setString("country_code",otpParser.data.driver_countrycode);
                                prefManager.setBoolean("isLogin",true);
                                prefManager.closeDB();

                                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(i);
                                finishAffinity();
                            }


                        }
                        else {
                            edtOtp1.setText("");
                            edtOtp2.setText("");
                            edtOtp3.setText("");
                            edtOtp4.setText("");
                            edtOtp5.setText("");
                            edtOtp1.requestFocus();
                            Toast.makeText(OTPVerifyActivity.this, otpParser.responsemessage, Toast.LENGTH_SHORT).show();
                        }



                    } else {
                        Toast.makeText(OTPVerifyActivity.this, otpParser.responsemessage, Toast.LENGTH_SHORT).show();
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

                inputOtp = edtOtp1.getText().toString() + edtOtp2.getText().toString() + edtOtp3.getText().toString() + edtOtp4.getText().toString() + edtOtp5.getText().toString();

                HashMap<String, String> params = new HashMap<>();

                params.put("token", Constants.Token);
                params.put("otp_code",inputOtp);
                params.put("country_code", countryCode);
                params.put("mobilenumber", mobileNo);


                System.out.println("param_OTP :" + params);

                return params;
            }
        };
        queue.add(request).setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    private void getIds() {
        btn_continue = findViewById(R.id.btn_continue);
        imgBack = findViewById(R.id.imgBack);
        txtResendOtp = findViewById(R.id.txtResendOtp);

        edtOtp1 = findViewById(R.id.edt_otp1);
        edtOtp2 = findViewById(R.id.edt_otp2);
        edtOtp3 = findViewById(R.id.edt_otp3);
        edtOtp4 = findViewById(R.id.edt_otp4);
        edtOtp5 = findViewById(R.id.edt_otp5);



    }

    public class GenericTextWatcher implements TextWatcher {

        private View view;

        private GenericTextWatcher(View view) {
            this.view = view;
        }

        @Override
        public void afterTextChanged(Editable editable) {
            // TODO Auto-generated method stub
            String text = editable.toString();
            switch (view.getId()) {
                case R.id.edt_otp1:
                    if (text.length() == 1)
                        edtOtp2.requestFocus();
                    break;
                case R.id.edt_otp2:
                    if (text.length() == 1)
                        edtOtp3.requestFocus();
                    else if (text.length() == 0)
                        edtOtp1.requestFocus();
                    break;
                case R.id.edt_otp3:
                    if (text.length() == 1)
                        edtOtp4.requestFocus();
                    else if (text.length() == 0)
                        edtOtp2.requestFocus();
                    break;
                case R.id.edt_otp4:
                    if (text.length() == 1)
                        edtOtp5.requestFocus();
                    else if (text.length() == 0)
                        edtOtp3.requestFocus();
                    break;
                case R.id.edt_otp5:
                    if (text.length() == 0)
                        edtOtp4.requestFocus();
                    else{
                        InputMethodManager in = (InputMethodManager)getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        in.hideSoftInputFromWindow(edtOtp5.getWindowToken(), 0);
                        if (isValid()) {
                            if (CommonMethod.isOnline(OTPVerifyActivity.this)) {
                                OTPVerifyApiCall();
                            } else {
                                Toast.makeText(OTPVerifyActivity.this, "" + getResources().getString(R.string.no_internet_connection), Toast.LENGTH_LONG).show();
                            }
                        }
                    }

            }
        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            // TODO Auto-generated method stub
        }
    }
}