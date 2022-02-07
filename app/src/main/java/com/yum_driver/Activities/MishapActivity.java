package com.yum_driver.Activities;

import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.yum_driver.Parsers.AddMishhapParser;
import com.yum_driver.R;
import com.yum_driver.utils.CommonMethod;
import com.yum_driver.utils.Constants;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class MishapActivity extends BaseActivity {

    private ImageView imgBack;
    private Button btn_submit;
    private Dialog dialog;
    private String order_id,driver_id;
    private TextInputEditText edtProblem;
    private TextView txtMinimumWords;
    Button button;
    private AddMishhapParser addMishhapParser;
    private RadioButton rdbTrafficJam,rdbAccidentFood,rdbWrongRoute,rdbOther;
    private String reason="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mishap);

        getIds();

        order_id = getIntent().getStringExtra("order_id");
        prefManager.connectDB();
        driver_id =prefManager.getString("driver_id");
        prefManager.closeDB();

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        edtProblem.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() <= 200){
                    txtMinimumWords.setText(getResources().getString(R.string.maximum_words)+" "+s.length()+"/"+getResources().getString(R.string.two_hundred));
                }
                if (s.length() > 3){
                    button.setBackgroundResource(R.drawable.border_orange);
                }else {
                    button.setBackground(ContextCompat.getDrawable(MishapActivity.this, R.drawable.border_grey));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        rdbTrafficJam.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    reason =rdbTrafficJam.getText().toString();
                }
            }
        });
        rdbAccidentFood.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    reason =rdbAccidentFood.getText().toString();
                }
            }
        });
        rdbWrongRoute.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    reason =rdbWrongRoute.getText().toString();
                }
            }
        });
        rdbOther.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    reason =rdbOther.getText().toString();
                }
            }
        });
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CommonMethod.avoidClick();

                if (isValid()){
                    if (CommonMethod.isOnline(MishapActivity.this)){
                        AddMishapApiCall();
                    }else {
                        Toast.makeText(MishapActivity.this, ""+getResources().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void AddMishapApiCall() {

        CommonMethod.showProgressDialog(MishapActivity.this);

        RequestQueue queue = Volley.newRequestQueue(MishapActivity.this);

        StringRequest request = new StringRequest(Request.Method.POST, Constants.BASE_URL + Constants.addMishap, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                CommonMethod.hideProgressDialog(MishapActivity.this);

                System.out.println("response_mishap" + response);

                try {
                    Gson gson = new Gson();
                    addMishhapParser = gson.fromJson(response,AddMishhapParser.class);
                    if (addMishhapParser != null){
                        if (addMishhapParser.responsecode.equals("200")){
                            openDiaWindow();
                        }else {
                            Toast.makeText(MishapActivity.this, ""+addMishhapParser.responsemessage, Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Toast.makeText(MishapActivity.this, ""+getResources().getString(R.string.mishap_not_added), Toast.LENGTH_SHORT).show();
                    }



                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("err:" + e.toString());
                    CommonMethod.hideProgressDialog(MishapActivity.this);
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                CommonMethod.hideProgressDialog(MishapActivity.this);

            }
        }) {
            @Override
            protected Map<String, String> getParams() {


                prefManager.connectDB();
                String driver_id = prefManager.getString("driver_id");
                String language = prefManager.getString("language");
                prefManager.closeDB();

                HashMap<String, String> dparams = new HashMap<>();
                dparams.put("token", Constants.Token);
                dparams.put("driver_id",driver_id);
                dparams.put("order_id",order_id);
                dparams.put("reason",reason);
                dparams.put("comments",edtProblem.getText().toString());
                dparams.put("lng", language);
                System.out.println("Params_misp: "+dparams);

                return dparams;
            }
        };
        queue.add(request).setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }

    private boolean isValid() {
        if (!(rdbTrafficJam.isChecked() || rdbAccidentFood.isChecked() || rdbWrongRoute.isChecked() || rdbOther.isChecked())){
            Toast.makeText(this, ""+getResources().getString(R.string.please_select_reason), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (edtProblem.getText().toString().equals("")){
            Toast.makeText(this, ""+getResources().getString(R.string.please_enter_problem), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void getIds() {
        imgBack = findViewById(R.id.imgBack);
        btn_submit = findViewById(R.id.btn_submit);
        dialog = new Dialog(this);

        txtMinimumWords =findViewById(R.id.txtMinimumWords);
        edtProblem =findViewById(R.id.edtProblem);
        button =findViewById(R.id.btn_submit);
        rdbTrafficJam  = findViewById(R.id.rdbTrafficJam);
        rdbAccidentFood  = findViewById(R.id.rdbAccidentFood);
        rdbWrongRoute  = findViewById(R.id.rdbWrongRoute);
        rdbOther  = findViewById(R.id.rdbOther);


    }

    private void openDiaWindow() {


        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //BottomSheetDialog dialog = new BottomSheetDialog(LoginActivity.this);

        dialog.setContentView(R.layout.dia_note_has_been_sent);
        TextView txtOkay = dialog.findViewById(R.id.txtOkay);
        txtOkay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                finish();
            }
        });
        dialog.show();

    }
}