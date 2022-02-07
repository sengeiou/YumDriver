package com.yum_driver.Activities;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.yum_driver.Parsers.CommonParser;
import com.yum_driver.Parsers.ProfileParser;
import com.yum_driver.Parsers.UserProfileParser;
import com.yum_driver.R;
import com.yum_driver.utils.CommonMethod;
import com.yum_driver.utils.Constants;
import com.yum_driver.utils.MultipartUtility;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.theartofdev.edmodo.cropper.CropImage;
import com.yalantis.ucrop.UCrop;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class ProfileSetting extends BaseActivity {

    private ImageView imgBack;
    private TextInputEditText et_Password;
    private CircularImageView imgProfile;

    @BindView(R.id.llChangePassword)
    LinearLayout llChangePassword;
    @BindView(R.id.edName)
    EditText edName;
    @BindView(R.id.edtMobile)
    TextView edtMobile;
    @BindView(R.id.llAddress)
    LinearLayout llAddress;

    @BindView(R.id.edEmail)
    EditText edEmail;
    @BindView(R.id.edDOB)
    TextView edDOB;
    @BindView(R.id.txtAddress)
    TextView txtAddress;
    @BindView(R.id.btn_submit)
    Button btn_submit;

    private long mLastClickTime = 0;
    private long WaitTime =1500;

    private int MY_IMAGE=1;
    private int IMAGE_CODE_11=11;
    private int CROP_IMAGE=12;
    private String strImage = "";
    private Uri imageUrl;
    private static final int PERMISSION_REQUEST_CODE = 200;
    private File outputFile;
    private String finalPath="";
    private final int GALLERY_ACTION_PICK_REQUEST_CODE = 2410;
    private String imagePath = "";
    private String response;
    private UserProfileParser userProfileParser;
    private DatePickerDialog datePickerDialog;
    private static int AUTOCOMPLETE_REQUEST_CODE = 101;
    private String City;
    private LatLng latlongg;
    private ProfileParser profileParser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setting);
        ButterKnife.bind(this);

        getIds();

        if (!checkPermission()) {
            requestPermission();
        }

        if(CommonMethod.isOnline(ProfileSetting.this))
        {
            GetProfileDetailsApiCall();
        }
        else
        {
            Toast.makeText(ProfileSetting.this, getResources().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
        }

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        llChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileSetting.this,ChangePasswordActivity.class));
                finish();
            }
        });

        txtAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Places.isInitialized()) {
                    Places.initialize(getApplicationContext(), getResources().getString(R.string.google_maps_key));
                }
                PlacesClient placesClient = Places.createClient(ProfileSetting.this);

                // Set the fields to specify which types of place data to
                // return after the user has made a selection.
                List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME,Place.Field.ADDRESS, Place.Field.LAT_LNG);

                // Start the autocomplete intent.
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                        .build(ProfileSetting.this);
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
            }
        });


        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // mis-clicking prevention, using threshold of 1000 ms
                if (SystemClock.elapsedRealtime() - mLastClickTime < WaitTime){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();


                if(isValid())
                {
                    if(CommonMethod.isOnline(ProfileSetting.this))
                    {
                        UpdateProfileApiCall();
                    }
                    else
                    {
                        Toast.makeText(ProfileSetting.this, getResources().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });

        edDOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current month
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                // date picker dialog
                datePickerDialog = new DatePickerDialog(ProfileSetting.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {


                                String SelectedDate = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;

                                System.out.println("selected Date :" + SelectedDate);

                                String todayDate = (year + "-"
                                        + (monthOfYear + 1) + "-" + dayOfMonth);

                                try {
                                    //current date format
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

                                    Date objDate = dateFormat.parse(SelectedDate);

                                    //Expected date format
                                    SimpleDateFormat dateFormat2 = new SimpleDateFormat("dd-MMM-yyyy");

                                    String finalDate = dateFormat2.format(objDate);

                                    edDOB.setText(finalDate);
                                    System.out.println("finalDate :" + finalDate);


                                } catch (Exception e) {
                                    e.printStackTrace();
                                }


                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
                datePickerDialog.show();

            }
        });
        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q)
                {
                    chooseProfilePicture();
                }else {
                    Intent intent = CropImage.activity(null).setAspectRatio(4, 4).getIntent(ProfileSetting.this);
                    File f = new File(Environment.getExternalStorageDirectory(), ".jpg");
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                    startActivityForResult(intent, MY_IMAGE);
                }
            }
        });
    }

    private void UpdateProfileApiCall() {

        CommonMethod.showProgressDialog(ProfileSetting.this);

        RequestQueue queue = Volley.newRequestQueue(ProfileSetting.this);

        StringRequest request = new StringRequest(Request.Method.POST, Constants.BASE_URL + Constants.updateDriverProfile, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                CommonMethod.hideProgressDialog(ProfileSetting.this);

                System.out.println("response_Update_profile_details"+response);

                try {
                    Gson gson = new Gson();
                    System.out.println("resp : "+response);
                    CommonParser profileParser =  gson.fromJson(response, CommonParser.class);
                    if(profileParser!= null)
                    {
                        if(profileParser.responsecode.equalsIgnoreCase("200")){
                            GetProfileDetailsApiCall();
                            Toast.makeText(ProfileSetting.this, profileParser.responsemessage, Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(ProfileSetting.this, profileParser.responsemessage, Toast.LENGTH_SHORT).show();

                        }
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
                params.put("driver_id",driver_id );
                params.put("driver_name",edName.getText().toString());
                params.put("lng", language);
                params.put("address", txtAddress.getText().toString());
                params.put("email", edEmail.getText().toString());
                params.put("dob", edDOB.getText().toString());

                if(latlongg==null){
                    params.put("dlat", String.valueOf(profileParser.userdata.driver_lat));
                    params.put("dlng", String.valueOf(profileParser.userdata.driver_lng));
                }else{
                    params.put("dlat", String.valueOf(latlongg.latitude));
                    params.put("dlng", String.valueOf(latlongg.longitude));
                }



                System.out.println("param_update_profile:" + params);

                return params;
            }
        };
        queue.add(request).setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }

    private boolean isValid() {

        if(edName.getText().toString().equalsIgnoreCase(""))
        {
            Toast.makeText(this, getResources().getString(R.string.enter_full_name), Toast.LENGTH_SHORT).show();
            return false;
        }
        if(edEmail.getText().toString().equalsIgnoreCase(""))
        {
            Toast.makeText(this, getResources().getString(R.string.enter_email), Toast.LENGTH_SHORT).show();
            return false;
        }
        if(txtAddress.getText().toString().equalsIgnoreCase(""))
        {
            Toast.makeText(this,  getResources().getString(R.string.enter_address), Toast.LENGTH_SHORT).show();
            return false;
        }
        if(!CommonMethod.isEmailValid(edEmail.getText().toString().trim()))
        {
            Toast.makeText(this,getResources().getString(R.string.enter_valid_email) , Toast.LENGTH_SHORT).show();
            return false;
        }
        if(edDOB.getText().toString().equalsIgnoreCase(""))
        {
            Toast.makeText(this, getResources().getString(R.string.select_dob), Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void chooseProfilePicture() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileSetting.this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_dialog_profile_picture, null);
        builder.setCancelable(true);
        builder.setView(dialogView);
        LinearLayout imageViewADPPCamera = dialogView.findViewById(R.id.imageViewADPPCamera);
        LinearLayout imageViewADPPGallery = dialogView.findViewById(R.id.imageViewADPPGallery);

        final AlertDialog alertDialogProfilePicture = builder.create();
        alertDialogProfilePicture.show();

        imageViewADPPCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,IMAGE_CODE_11);
                alertDialogProfilePicture.dismiss();
            }
        });

        imageViewADPPGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePictureFromGallery();
                alertDialogProfilePicture.dismiss();
            }
        });
    }
    private void takePictureFromGallery() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto, GALLERY_ACTION_PICK_REQUEST_CODE);
    }

    private void getIds() {
        imgBack = findViewById(R.id.imgBack);
        //et_Password = findViewById(R.id.et_Password);
        imgProfile = findViewById(R.id.imgProfile);
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(ProfileSetting.this, new
                String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE,CAMERA}, PERMISSION_REQUEST_CODE);
               // String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE, ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION,CAMERA}, PERMISSION_REQUEST_CODE);
    }

    public boolean checkPermission() {
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(),
                WRITE_EXTERNAL_STORAGE);
        int result2 = ContextCompat.checkSelfPermission(getApplicationContext(),
                READ_EXTERNAL_STORAGE);
      /*  int result3 = ContextCompat.checkSelfPermission(getApplicationContext(),
                ACCESS_FINE_LOCATION);
        int result4 = ContextCompat.checkSelfPermission(getApplicationContext(),
                ACCESS_COARSE_LOCATION);*/
        int result5 = ContextCompat.checkSelfPermission(getApplicationContext(),
                CAMERA);

        return result1 == PackageManager.PERMISSION_GRANTED
                && result2 == PackageManager.PERMISSION_GRANTED
             /*   && result3 == PackageManager.PERMISSION_GRANTED
                && result4 == PackageManager.PERMISSION_GRANTED*/
                && result5 == PackageManager.PERMISSION_GRANTED;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("value", "Permission Granted, Now you can use local drive .");
                } else {
                    Log.e("value", "Permission Denied, You cannot use local drive .");
                }
                break;
        }
    }

    private void GetProfileDetailsApiCall() {

        CommonMethod.showProgressDialog(ProfileSetting.this);

        RequestQueue queue = Volley.newRequestQueue(ProfileSetting.this);

        StringRequest request = new StringRequest(Request.Method.POST, Constants.BASE_URL + Constants.myprofile, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                CommonMethod.hideProgressDialog(ProfileSetting.this);

                System.out.println("response_profile_details"+response);

                try {
                    Gson gson = new Gson();
                    System.out.println("resp : "+response);
                     profileParser =  gson.fromJson(response, ProfileParser.class);

                    if(profileParser != null)
                    {
                        if(profileParser.responsecode.equalsIgnoreCase("200")){

                            edName.setText(profileParser.userdata.driver_name);
                            edtMobile.setText(profileParser.userdata.driver_mobile);
                            txtAddress.setText(profileParser.userdata.address);
                            edEmail.setText(profileParser.userdata.email);
                            edDOB.setText(profileParser.userdata.dob);
                            Glide.with(ProfileSetting.this).load(Uri.parse(profileParser.userdata.driver_photo)).placeholder(R.drawable.ic_user_profile).error(R.drawable.ic_user_profile).into(imgProfile);
                        }
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
                String driver_id = prefManager.getString("driver_id");
                String lang = prefManager.getString("language");
                prefManager.closeDB();

                HashMap<String, String> params = new HashMap<>();

                params.put("token", Constants.Token);
                params.put("driver_id",driver_id);
                params.put("lng", lang);


                System.out.println("param :" + params);

                return params;
            }
        };
        queue.add(request).setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }




    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MY_IMAGE)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                try {

                    Calendar c = Calendar.getInstance();
                    String path1 = Environment.getExternalStorageDirectory().toString() + File.separator + "Deseos Driver";
                    try {
                        File MerchantDirectory = new File(path1);
                        MerchantDirectory.mkdirs();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    File file;
                    strImage = String.valueOf(result.getUri());
                    file = new File(new URI(strImage));

                    outputFile = new File(path1, "User_Photo_" + c.getTimeInMillis() + ".jpg");



                    FileOutputStream out = new FileOutputStream(outputFile);
                    InputStream io = new FileInputStream(file);
                    Bitmap bmp = BitmapFactory.decodeStream(io);
                    bmp.compress(Bitmap.CompressFormat.JPEG, 40, out);
                    out.flush();
                    out.close();

                    finalPath = String.valueOf(outputFile);

                    if (CommonMethod.isOnline(ProfileSetting.this)){
                        ChangePhotoAsynch changePhotoAsynch  = new ChangePhotoAsynch();
                        changePhotoAsynch.execute();
                    }else {
                        Toast.makeText(this, ""+getResources().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                    }


                    strImage = outputFile.getName();
                    imageUrl = result.getUri();

                } catch (Exception e) {
                    e.printStackTrace();
                }

                imageUrl = result.getUri();
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUrl);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        else if (requestCode == IMAGE_CODE_11)
        {
            if (resultCode == RESULT_OK) {
                try {

                    Calendar c = Calendar.getInstance();
                    //String path1 = Environment.getExternalStorageDirectory().toString() + File.separator + "Deseos Customer";
                    String path1 = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString() + File.separator + "Deseos Driver";


                    try {
                        File MerchantDirectory = new File(path1);
                        MerchantDirectory.mkdirs();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    outputFile = new File(path1, "User_Photo_" + c.getTimeInMillis() + ".png");

                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");


                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG,100, bos);
                    byte[] bitmapdata = bos.toByteArray();


                    FileOutputStream out = new FileOutputStream(outputFile);
                    out.write(bitmapdata);
                    out.flush();
                    out.close();

                    Intent intent = CropImage.activity(Uri.fromFile(outputFile)).setAspectRatio(2, 3).getIntent(ProfileSetting.this);
                    File f = new File(Environment.getExternalStorageDirectory(), ".png");
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                    startActivityForResult(intent, CROP_IMAGE);

                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        }
        else if (requestCode == CROP_IMAGE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                try {

                    Calendar c = Calendar.getInstance();
                    // String path1 = Environment.getExternalStorageDirectory().toString() + File.separator + "Deseos Customer";
                    String path1 = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString() + File.separator + "Deseos Driver";


                    try {
                        File MerchantDirectory = new File(path1);
                        MerchantDirectory.mkdirs();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    File file;
                    strImage = String.valueOf(result.getUri());
                    file = new File(new URI(strImage));

                    outputFile = new File(path1, "User_Photo_" + c.getTimeInMillis() + ".png");


                    FileOutputStream out = new FileOutputStream(outputFile);
                    InputStream io = new FileInputStream(file);
                    Bitmap bmp = BitmapFactory.decodeStream(io);
                    bmp.compress(Bitmap.CompressFormat.PNG, 100, out);
                    out.flush();
                    out.close();

                    finalPath = String.valueOf(outputFile);


                    if (CommonMethod.isOnline(ProfileSetting.this)){
                        ChangePhotoAsynch changePhotoAsynch  = new ChangePhotoAsynch();
                        changePhotoAsynch.execute();
                    }else {
                        Toast.makeText(this, "NO internet connection.", Toast.LENGTH_SHORT).show();
                    }
                    strImage = outputFile.getName();
                    imageUrl = result.getUri();

                } catch (Exception e) {
                    e.printStackTrace();
                }

                imageUrl = result.getUri();
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUrl);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }else if(requestCode == GALLERY_ACTION_PICK_REQUEST_CODE){
            if (resultCode == RESULT_OK) {
                Uri GalleryUri = data.getData();
                try {
                    Calendar c = Calendar.getInstance();
                    String path1 = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString() + File.separator + "Deseos Driver";

                    try {
                        File MerchantDirectory = new File(path1);
                        MerchantDirectory.mkdirs();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    outputFile = new File(path1, "User_Photo_" + c.getTimeInMillis() + ".jpg");

                    finalPath = String.valueOf(outputFile);
                    strImage = outputFile.getName();
                    openCropActivity(GalleryUri, Uri.fromFile(outputFile));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }else if (requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK) {
            Uri uri = UCrop.getOutput(data);
            if (CommonMethod.isOnline(ProfileSetting.this)){
                ChangePhotoAsynch changePhotoAsynch  = new ChangePhotoAsynch();
                changePhotoAsynch.execute();
            }else {
                Toast.makeText(this, getResources().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
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
    @SuppressLint("Range")
    private void openCropActivity(Uri sourceUri, Uri destinationUri) {
        UCrop.of(sourceUri, destinationUri)
                .withAspectRatio(5f, 5f)
                .start(ProfileSetting.this);
    }

    private class ChangePhotoAsynch extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... strings) {
            MultipartUtility multipart = null;
            String charset = "UTF-8";

            prefManager.connectDB();
            String driver_id = prefManager.getString("driver_id");
            prefManager.closeDB();

            try {
                multipart = new MultipartUtility(Constants.BASE_URL+Constants.updateProfilePhoto, charset);

                multipart.addFormField("driver_id", driver_id);
                multipart.addFormField("token", Constants.Token);
                if(finalPath != null ) {
                    if(!finalPath.equalsIgnoreCase("")) {
                        multipart.addFilePart("driver_photo", new File(finalPath));
                    }
                }
                response = multipart.finish(); // response from server.

                System.out.println("response_photo :"+response);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            CommonMethod.showProgressDialog(ProfileSetting.this);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            CommonMethod.hideProgressDialog(ProfileSetting.this);
            System.out.println(response);

            Gson gson=new Gson();
            CommonParser commonparser = gson.fromJson(response, CommonParser.class);
            if(commonparser.responsecode.equals("200"))
            {
                Toast.makeText(ProfileSetting.this, getResources().getString(R.string.profile_changed_successfully), Toast.LENGTH_LONG).show();
                if(CommonMethod.isOnline(ProfileSetting.this))
                {
                    GetProfileDetailsApiCall();
                }
                else
                {
                    Toast.makeText(ProfileSetting.this, getResources().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                }

            }
            else
            {
                Toast.makeText(ProfileSetting.this, commonparser.responsemessage, Toast.LENGTH_LONG).show();
            }

        }
    }
}