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
import android.widget.EditText;
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
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class FeedbackActivity extends BaseActivity {
    private ImageView imgBack;
    private EditText edtComment;
    private TextView textView8;
    private String driver_id;
    private Button button;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        getIds();
        prefManager.connectDB();
        driver_id = prefManager.getString("driver_id");
        prefManager.closeDB();

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        edtComment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() <= 200){
                    textView8.setText(getResources().getString(R.string.maximum_words)+" "+s.length()+"/"+getResources().getString(R.string.two_hundred));
                }
                if (s.length() > 3){
                    button.setBackgroundResource(R.drawable.border_orange);
                }else {
                    button.setBackground(ContextCompat.getDrawable(FeedbackActivity.this, R.drawable.border_grey));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValid()){
                    if (CommonMethod.isOnline(FeedbackActivity.this)){
                        SendFeedBackApiCall();
                    }else {
                        Toast.makeText(FeedbackActivity.this, ""+getResources().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });


    }

    private void SendFeedBackApiCall() {

        CommonMethod.showProgressDialog(FeedbackActivity.this);

        RequestQueue queue = Volley.newRequestQueue(FeedbackActivity.this);

        StringRequest request = new StringRequest(Request.Method.POST, Constants.BASE_URL_ADD_FEEDBACK + Constants.addfeedback, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                CommonMethod.hideProgressDialog(FeedbackActivity.this);

                System.out.println("response_feedback" + response);

                Gson gson = new Gson();
                CommonParser  commonParser = gson.fromJson(response, CommonParser.class);
                if (commonParser != null){
                    if (commonParser.responsecode.equals("200")){
                        openDiaWindow();
                    }else {
                        Toast.makeText(FeedbackActivity.this, ""+commonParser.responsemessage, Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(FeedbackActivity.this, ""+getResources().getString(R.string.feedback_not_added), Toast.LENGTH_SHORT).show();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("error :"+error.toString());

                CommonMethod.hideProgressDialog(FeedbackActivity.this);

            }
        }) {
            @Override
            protected Map<String, String> getParams() {


                prefManager.connectDB();
                String language = prefManager.getString("language");
                prefManager.closeDB();

                HashMap<String, String> params = new HashMap<>();
                params.put("token", Constants.Token);
                params.put("userid",driver_id);
                params.put("type","driver");
                params.put("comment",edtComment.getText().toString());
                params.put("lng", language);

                return params;
            }
        };
        queue.add(request).setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    private boolean isValid() {
        if (edtComment.getText().toString().equals("")){
            Toast.makeText(this, ""+getResources().getString(R.string.please_enter_comment), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void openDiaWindow() {
        dialog= new Dialog(this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //BottomSheetDialog dialog = new BottomSheetDialog(LoginActivity.this);
        dialog.setContentView(R.layout.dia_feedback);

        TextView txtDone = dialog.findViewById(R.id.txtDone);
        txtDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                finish();
            }
        });
        dialog.show();

    }

    private void getIds() {
        imgBack = findViewById(R.id.imgBack);
        edtComment = findViewById(R.id.edtComment);
        textView8 = findViewById(R.id.textView8);
        button = findViewById(R.id.btn_feedback);


    }
}