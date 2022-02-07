package com.yum_driver.Activities;

import androidx.annotation.Nullable;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.yum_driver.Parsers.RegisterParser;
import com.yum_driver.Pojo.Country;
import com.yum_driver.R;
import com.yum_driver.utils.CommonMethod;
import com.yum_driver.utils.Constants;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RegisterActivity extends BaseActivity {

    @BindView(R.id.rdNone)
    RadioButton rdNone;
    @BindView(R.id.rdCar)
    RadioButton rdCar;
    @BindView(R.id.rdByCycle)
    RadioButton rdByCycle;
    @BindView(R.id.rdb_parttime)
    RadioButton rdb_parttime;
    @BindView(R.id.rdBike)
    RadioButton rdBike;
    @BindView(R.id.rdb_regular)
    RadioButton rdb_regular;
    @BindView(R.id.edt_mobno)
    EditText edt_mobno;
    @BindView(R.id.edt_Password)
    EditText edt_Password;
    @BindView(R.id.edt_fullname)
    EditText edt_fullname;
    @BindView(R.id.edt_state)
    EditText edt_state;
    @BindView(R.id.btn_submit)
    Button btn_submit;
    @BindView(R.id.imgBack)
    ImageView imgBack;
    @BindView(R.id.txtCountryCode)
    TextView txtCountryCode;
    @BindView(R.id.txtAddress)
    TextView txtAddress;
    Dialog dialog;
    private String jobType = "", vehicalType = "";
    private String chosenCountry;
    private String chosenCountryCode;
    private String chosenCallingCode;
    private static int AUTOCOMPLETE_REQUEST_CODE = 1;
    private String City;
    private LatLng latlongg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);

        getIds();

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        txtAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!Places.isInitialized()) {
                    Places.initialize(getApplicationContext(), getResources().getString(R.string.google_maps_key));
                }
                PlacesClient placesClient = Places.createClient(RegisterActivity.this);

                // Set the fields to specify which types of place data to
                // return after the user has made a selection.
                List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME,Place.Field.ADDRESS, Place.Field.LAT_LNG);

                // Start the autocomplete intent.
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                        .build(RegisterActivity.this);
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
            }
        });

        rdb_parttime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jobType = "Part Time";
            }
        });
        rdb_regular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jobType = "Regular";
            }
        });
        rdBike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vehicalType = "Bike";
            }
        });
        rdByCycle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vehicalType = "ByCycle";
            }
        });
        rdCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vehicalType = "Car";
            }
        });
        rdNone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vehicalType = "None";
            }
        });
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CommonMethod.avoidClick();

                if (isValid()) {

                    if (rdb_parttime.isChecked()) {
                        jobType = "Part Time";
                    }
                    if (rdb_regular.isChecked()) {
                        jobType = "Regular";
                    }
                    if (rdBike.isChecked()) {
                        vehicalType = "Bike";
                    }
                    if (rdByCycle.isChecked()) {
                        vehicalType = "ByCycle";
                    }
                    if (rdCar.isChecked()) {
                        vehicalType = "Car";
                    }
                    if (rdNone.isChecked()) {
                        vehicalType = "None";
                    }

                    if (CommonMethod.isOnline(RegisterActivity.this)) {
                        RegisterApiCall();
                    } else {
                        Toast.makeText(RegisterActivity.this, getResources().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
    }


    private void RegisterApiCall() {

        CommonMethod.showProgressDialog(RegisterActivity.this);

        RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);

        StringRequest request = new StringRequest(Request.Method.POST, Constants.BASE_URL + Constants.signup, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                CommonMethod.hideProgressDialog(RegisterActivity.this);
                System.out.println("response_register: " + response);
                try {

                    Gson gson = new Gson();
                    RegisterParser registerParser = gson.fromJson(response, RegisterParser.class);

                    if (registerParser != null) {
                        if (registerParser.responsecode.equalsIgnoreCase("200")) {
                            Toast.makeText(RegisterActivity.this, registerParser.responsemessage, Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(getApplicationContext(), OTPVerifyActivity.class);
                            i.putExtra("fromRegister", true);
                            i.putExtra("mobileNo", edt_mobno.getText().toString().trim());
                            i.putExtra("countryCode", txtCountryCode.getText().toString());
                            i.putExtra("otp", registerParser.data.otp_code);
                            startActivity(i);
                            finish();

                          //  openDiaWindow();

                        } else {
                            Toast.makeText(RegisterActivity.this, registerParser.responsemessage, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(RegisterActivity.this, getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();
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
                params.put("fullname", edt_fullname.getText().toString().trim());
                params.put("countrycode", txtCountryCode.getText().toString());
                params.put("mobilenumber", edt_mobno.getText().toString().trim());
                params.put("password", edt_Password.getText().toString().trim());
                params.put("state", edt_state.getText().toString().trim());
                params.put("job_type", jobType);
                params.put("vehicle_type", vehicalType);
                params.put("address", txtAddress.getText().toString());
                params.put("dlat", String.valueOf(latlongg.latitude));
                params.put("dlng", String.valueOf(latlongg.longitude));
                params.put("fcm_token", FCM_TOKEN);

                System.out.println("param_register :" + params);

                return params;
            }
        };
        queue.add(request).setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }


    private void openDiaWindow() {

        dialog = new Dialog(RegisterActivity.this, R.style.AppTheme);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //BottomSheetDialog dialog = new BottomSheetDialog(LoginActivity.this);
        dialog.setContentView(R.layout.dia_thanks_for_applying);
        dialog.show();
       TextView done = dialog.findViewById(R.id.btn_done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(i);
                finishAffinity();
            }
        });
        Button btn_how_we_work = dialog.findViewById(R.id.btn_how_we_work);
        btn_how_we_work.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // startActivity(new Intent(RegisterActivity.this,WeAreFoodBird.class));
            }
        });
    }

    private boolean isValid() {

        if (edt_fullname.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(this, getResources().getString(R.string.enter_full_name), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (txtAddress.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(this, getResources().getString(R.string.enter_address), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (edt_mobno.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(this, getResources().getString(R.string.enter_phone_number), Toast.LENGTH_SHORT).show();
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
        if (!txtCountryCode.getText().toString().equalsIgnoreCase("+91")||!txtCountryCode.getText().toString().equalsIgnoreCase("+34")) {
            if ((edt_mobno.getText().toString().length() < 9)) {
                Toast.makeText(this, getResources().getString(R.string.please_enter_valid_phone), Toast.LENGTH_LONG).show();
                return false;
            }
        }
        if (edt_Password.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(this, getResources().getString(R.string.enter_password), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (edt_state.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(this, getResources().getString(R.string.enter_state), Toast.LENGTH_SHORT).show();
            return false;
        }

        if (jobType.equalsIgnoreCase("")) {
            Toast.makeText(this, getResources().getString(R.string.please_select_interest), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (vehicalType.equalsIgnoreCase("")) {
            Toast.makeText(this, getResources().getString(R.string.please_select_vehicle), Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void getIds() {

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
        else if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK)
            {
                Place place = Autocomplete.getPlaceFromIntent(data);
                Log.i("Place: ", "Place: " + place.getName());

                txtAddress.setText(place.getAddress());

                 latlongg = place.getLatLng();



                Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());

                try {
                    List<Address> addresses = gcd.getFromLocation(latlongg.latitude, latlongg.longitude, 1);
                    if (addresses.size() > 0) {
                        String address = addresses.get(0).getAddressLine(0);
                        City = addresses.get(0).getLocality();
                    }
                }
                catch (IOException e) {
                    e.printStackTrace();
                }

//                prefManager.connectDB();
//                prefManager.setString("Address", place.getAddress());
//                prefManager.setString("latitude", String.valueOf(latlongg.latitude));
//                prefManager.setString("longitude", String.valueOf(latlongg.longitude));
//                prefManager.setString("City", String.valueOf(City));
//                prefManager.closeDB();



            } else if (resultCode == AutocompleteActivity.RESULT_ERROR)
            {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i("Place: ", status.getStatusMessage());
            }
            else if (resultCode == RESULT_CANCELED)
            {
                // The user canceled the operation.
            }
            return;
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

}