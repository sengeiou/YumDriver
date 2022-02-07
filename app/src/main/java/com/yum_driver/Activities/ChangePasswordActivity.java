package com.yum_driver.Activities;

import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.yum_driver.Parsers.CommonParser;
import com.yum_driver.R;
import com.yum_driver.utils.CommonMethod;
import com.yum_driver.utils.Constants;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class ChangePasswordActivity extends BaseActivity {

    private ImageView imgBack;
    private Button btn_submit;
    private Dialog dialog;
    private TextInputEditText txt_oldPass,txt_newPass,txt_cnfoldPass;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
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

               CommonMethod.avoidClick();

                if (isValid()){
                    if (CommonMethod.isOnline(ChangePasswordActivity.this)){
                        ChangePasswordApiCall();
                    }else {
                        Toast.makeText(ChangePasswordActivity.this, ""+getResources().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        txt_cnfoldPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (charSequence.length() >= 6){
                    btn_submit.setBackgroundResource(R.drawable.border_orange);
                }else {
                    btn_submit.setBackground(ContextCompat.getDrawable(ChangePasswordActivity.this, R.drawable.border_grey));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void ChangePasswordApiCall() {

        CommonMethod.showProgressDialog(ChangePasswordActivity.this);

        RequestQueue queue = Volley.newRequestQueue(ChangePasswordActivity.this);

        StringRequest request = new StringRequest(Request.Method.POST, Constants.BASE_URL + Constants.changePassword, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                CommonMethod.hideProgressDialog(ChangePasswordActivity.this);

                System.out.println("response_change_password" + response);

                Gson gson = new Gson();
                CommonParser commonParser = gson.fromJson(response, CommonParser.class);
                if (commonParser != null){
                    if (commonParser.responsecode.equals("200")){
                        openDiaWindow();
                    }else {
                        Toast.makeText(ChangePasswordActivity.this, ""+commonParser.responsemessage, Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(ChangePasswordActivity.this, ""+getResources().getString(R.string.feedback_not_added), Toast.LENGTH_SHORT).show();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("error :"+error.toString());

                CommonMethod.hideProgressDialog(ChangePasswordActivity.this);

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
                params.put("driver_id",driver_id);
                params.put("password",txt_newPass.getText().toString());
                params.put("oldpassword",txt_oldPass.getText().toString());
                params.put("lng", language);

                System.out.println("params_changePass :"+params);

                return params;
            }
        };
        queue.add(request).setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    private void getIds() {

        imgBack = findViewById(R.id.imgBack);
        btn_submit = findViewById(R.id.btn_submit);
        txt_oldPass = findViewById(R.id.txt_oldPass);
        txt_newPass = findViewById(R.id.txt_newPass);
        txt_cnfoldPass = findViewById(R.id.txt_cnfoldPass);
        dialog= new Dialog(this);

    }


    private boolean isValid() {

        if (txt_oldPass.getText().toString().equals("")){
            Toast.makeText(this, ""+getResources().getString(R.string.please_enter_old_password), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (txt_newPass.getText().toString().equals("")){
            Toast.makeText(this, ""+getResources().getString(R.string.please_enter_new_password), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!CommonMethod.isPasswordValidMethod(txt_newPass.getText().toString())){
            Toast.makeText(this, ""+getResources().getString(R.string.please_enter_strong_password), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (txt_newPass.getText().toString().length() < 8){
            Toast.makeText(this, ""+getResources().getString(R.string.password_length), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (txt_cnfoldPass.getText().toString().equals("")){
            Toast.makeText(this, ""+getResources().getString(R.string.password_confirm), Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!txt_newPass.getText().toString().equals(txt_cnfoldPass.getText().toString())){
            Toast.makeText(this, ""+getResources().getString(R.string.password_not_match), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void openDiaWindow() {

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //BottomSheetDialog dialog = new BottomSheetDialog(LoginActivity.this);
        dialog.setContentView(R.layout.dialog_change_password);

        TextView txtDone = dialog.findViewById(R.id.txtDone);
        txtDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                prefManager.connectDB();
                prefManager.setString("driver_id", "");
                prefManager.setString("rst_profile_id", "");
                prefManager.setString("mobilenumber", "");
                prefManager.setString("country_code", "");
                prefManager.setBoolean("isLogin", false);
                prefManager.closeDB();
                Intent i = new Intent(ChangePasswordActivity.this, LoginActivity.class);
                startActivity(i);
                finishAffinity();
            }
        });
        dialog.show();

    }
}