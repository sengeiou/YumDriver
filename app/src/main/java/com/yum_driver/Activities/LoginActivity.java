package com.yum_driver.Activities;

import androidx.annotation.Nullable;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.yum_driver.Parsers.LoginParser;
import com.yum_driver.Pojo.Country;
import com.yum_driver.R;
import com.yum_driver.utils.CommonMethod;
import com.yum_driver.utils.Constants;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.Gson;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends BaseActivity {

    private LinearLayout llRegister;
    private RelativeLayout rlOtp;
    private Button btn_login;

    @BindView(R.id.edt_mobno)
    EditText edt_mobno;
    @BindView(R.id.edtPassword)
    EditText edtPassword;
    @BindView(R.id.txtCountryCode)
    TextView txtCountryCode;
    private String chosenCountry;
    private String chosenCountryCode;
    private String chosenCallingCode;

    private long mLastClickTime = 0;
    private long WaitTime =1500;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        getIds();

        FirebaseApp.initializeApp(this);

       /* FirebaseMessaging.getInstance().getToken().addOnSuccessListener(LoginActivity.this, new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                String token = s;
                System.out.println("FCM_TOKEN: "+s);
                prefManager.connectDB();
                prefManager.setString("FCM_TOKEN",token);
                prefManager.closeDB();

            }
        });*/

       FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(LoginActivity.this, new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String token = instanceIdResult.getToken();

                prefManager.connectDB();
                prefManager.setString("FCM_TOKEN", token);
                prefManager.closeDB();

                System.out.println("FCM_TOKEN" + token);
            }
        });

        llRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
                //  finish();
            }
        });
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (SystemClock.elapsedRealtime() - mLastClickTime < WaitTime){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                if (isValid()) {

                    if (CommonMethod.isOnline(LoginActivity.this)) {
                        LoginByPassApiCall();

                    } else {
                        Toast.makeText(LoginActivity.this, getResources().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                    }

                }

            }
        });
        rlOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isValidMob()) {

                    if (CommonMethod.isOnline(LoginActivity.this)) {
                        LoginByOtpApiCall();
                    } else {
                        Toast.makeText(LoginActivity.this, getResources().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });

        edt_mobno.addTextChangedListener(new GenericTextWatcher(edt_mobno));
    }

    private void getIds() {

        llRegister = findViewById(R.id.llRegister);
        rlOtp = findViewById(R.id.rlOtp);
        btn_login = findViewById(R.id.btn_login);


    }

    private void LoginByOtpApiCall() {

        CommonMethod.showProgressDialog(LoginActivity.this);

        RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);

        StringRequest request = new StringRequest(Request.Method.POST, Constants.BASE_URL + Constants.otpLogin, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                CommonMethod.hideProgressDialog(LoginActivity.this);
                System.out.println("response_loginByOtp: " + response);
                try {

                    Gson gson = new Gson();
                    LoginParser loginParser = gson.fromJson(response, LoginParser.class);

                    if (loginParser != null) {

                        if (loginParser.responsecode.equalsIgnoreCase("200")) {

                           // Toast.makeText(LoginActivity.this, loginParser.responsemessage, Toast.LENGTH_SHORT).show();

                            Intent i = new Intent(getApplicationContext(), OTPVerifyActivity.class);
                            i.putExtra("fromLogin", true);
                            i.putExtra("mobileNo", edt_mobno.getText().toString().trim());
                            i.putExtra("countryCode", loginParser.data.driver_countrycode);
                            i.putExtra("otpCode", loginParser.data.otp_code);
                            startActivity(i);


                        } else {
                            Toast.makeText(LoginActivity.this, loginParser.responsemessage, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();
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
                String FCM_TOKEN = prefManager.getString("FCM_TOKEN");
                prefManager.closeDB();

                HashMap<String, String> params = new HashMap<>();

                params.put("token", Constants.Token);
                params.put("country_code", txtCountryCode.getText().toString());
                params.put("mobilenumber", edt_mobno.getText().toString().trim());
                params.put("fcm_token", FCM_TOKEN);
                params.put("is_resend", "No");

                System.out.println("param_loginByOtp :" + params);

                return params;
            }
        };
        queue.add(request).setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }

    private boolean isValidMob() {
        if (edt_mobno.getText().toString().equals("")) {
            Toast.makeText(this, getResources().getString(R.string.enter_registered_phone_number), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (txtCountryCode.getText().toString().equalsIgnoreCase("+255")) {
            if ((edt_mobno.getText().toString().length() < 8)) {
                Toast.makeText(this, getResources().getString(R.string.please_enter_valid_phone), Toast.LENGTH_LONG).show();
                return false;
            }
        }
        if (txtCountryCode.getText().toString().equalsIgnoreCase("+91")) {
            if ((edt_mobno.getText().toString().length() != 10)) {
                Toast.makeText(this, getResources().getString(R.string.please_enter_valid_phone), Toast.LENGTH_LONG).show();
                return false;
            }
        }
        return true;
    }

    private void LoginByPassApiCall() {

        CommonMethod.showProgressDialog(LoginActivity.this);

        RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);

        StringRequest request = new StringRequest(Request.Method.POST, Constants.BASE_URL + Constants.loginbypwd, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                CommonMethod.hideProgressDialog(LoginActivity.this);
                System.out.println("response_loginByPass: " + response);
                try {

                    Gson gson = new Gson();
                    LoginParser loginParser = gson.fromJson(response, LoginParser.class);

                    if (loginParser != null) {

                        if (loginParser.responsecode.equalsIgnoreCase("200")) {

                           // Toast.makeText(LoginActivity.this, loginParser.responsemessage, Toast.LENGTH_SHORT).show();

                            prefManager.connectDB();
                            prefManager.setString("driver_id", loginParser.data.driver_id);
                            prefManager.setString("rst_profile_id", loginParser.data.rst_profile_id);
                            prefManager.setString("mobilenumber", loginParser.data.driver_mobile);
                            prefManager.setString("country_code", loginParser.data.driver_countrycode);
                            prefManager.setBoolean("isLogin", true);
                            prefManager.closeDB();

                            Intent i = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(i);
                            finishAffinity();

                        } else {
                            Toast.makeText(LoginActivity.this, loginParser.responsemessage, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();
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
                String FCM_TOKEN = prefManager.getString("FCM_TOKEN");
                prefManager.closeDB();

                HashMap<String, String> params = new HashMap<>();

                params.put("token", Constants.Token);
                params.put("country_code", txtCountryCode.getText().toString());
                params.put("mobilenumber", edt_mobno.getText().toString().trim());
                params.put("password", edtPassword.getText().toString().trim());
                params.put("fcm_token", FCM_TOKEN);
                System.out.println("param_loginByPass :" + params);

                return params;
            }
        };
        queue.add(request).setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    private boolean isValid() {


        if (edt_mobno.getText().toString().equals("")) {
            Toast.makeText(this, getResources().getString(R.string.enter_registered_phone_number), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (txtCountryCode.getText().toString().equalsIgnoreCase("+255")) {
            if ((edt_mobno.getText().toString().length() <8 )) {
                Toast.makeText(this, getResources().getString(R.string.please_enter_valid_phone), Toast.LENGTH_LONG).show();
                return false;
            }
        }
        if (txtCountryCode.getText().toString().equalsIgnoreCase("+91")) {
            if ((edt_mobno.getText().toString().length() != 10)) {
                Toast.makeText(this, getResources().getString(R.string.please_enter_valid_phone), Toast.LENGTH_LONG).show();
                return false;
            }
        }


//        if (edt_mobno.getText().toString().equalsIgnoreCase("")) {
//            Toast.makeText(this, "Enter register phone number", Toast.LENGTH_SHORT).show();
//            return false;
//        }
//        if (edt_mobno.getText().toString().length()<10) {
//            Toast.makeText(this, "Enter valid phone number", Toast.LENGTH_SHORT).show();
//            return false;
//        }

        if (edtPassword.getText().toString().equals("")) {
            Toast.makeText(this, getResources().getString(R.string.enter_password), Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }


    public void showCodePicker(View view) {
        Intent intent = new Intent(this, CountryCodePickerActivity.class);
        startActivityForResult(intent, 949);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 949 && resultCode == RESULT_OK) {
            try {
                setupUI(data.getStringExtra("res"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void setupUI(String chosenResult) {
        if (chosenResult == null) {
            Locale currentLocale = getResources().getConfiguration().locale;
            Log.e(currentLocale.getCountry(), currentLocale.getISO3Country());

            chosenCountry = "United States"; //currentLocale.getDisplayCountry();
            chosenCountryCode = "US"; //currentLocale.getCountry();

            int index = -1;

            for (String countryName : Country.country) {
                Log.e("comparing >>>>>> ", countryName + " :: " + currentLocale.getCountry() + " :: " + currentLocale.getDisplayCountry() + " :: " + currentLocale.getDisplayName());
                if (countryName.equalsIgnoreCase(chosenCountry)) {
                    index = Arrays.asList(Country.country).indexOf(countryName);
                    break;
                }
            }

            if (index != -1) {
                chosenCallingCode = Country.code[index];

                txtCountryCode.setText(String.format("+%s", chosenCallingCode));
            } else {
                Log.e("CCC", "Not found");
            }
        } else {
            chosenCountry = chosenResult.split(" <><><> ")[0];
            chosenCountryCode = chosenResult.split(" <><><> ")[1];
            chosenCallingCode = chosenResult.split(" <><><> ")[2];


            txtCountryCode.setText(String.format("+%s", chosenCallingCode));
        }
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
                case R.id.edt_mobno:

                    if (txtCountryCode.getText().toString().equalsIgnoreCase("+34")) {
                        if (text.length() == 9) {
                            InputMethodManager in = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                            in.hideSoftInputFromWindow(edt_mobno.getWindowToken(), 0);
                        }
                    } else if (txtCountryCode.getText().toString().equalsIgnoreCase("+91")) {
                        if (text.length() == 10) {
                            InputMethodManager in = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                            in.hideSoftInputFromWindow(edt_mobno.getWindowToken(), 0);
                        }
                    } else {
                        if (text.length() == 10) {
                            InputMethodManager in = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                            in.hideSoftInputFromWindow(edt_mobno.getWindowToken(), 0);
                        }
                    }

                    break;

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