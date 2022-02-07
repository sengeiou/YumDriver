package com.yum_driver.Activities;

import androidx.core.content.ContextCompat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.yum_driver.Parsers.CurrentTripParser;
import com.yum_driver.Pojo.CurrentTripData;
import com.yum_driver.R;
import com.yum_driver.utils.CommonMethod;
import com.yum_driver.utils.Constants;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CurrentOrderDetails extends BaseActivity {

    private ImageView imgBack;
    private GoogleMap mMap;
    private Marker destinationMarker, sourceMarker, driverMarker;
    private Polyline arc, route;
    private Float routeDistance, routeDuration;
    private LatLng sourceLatLng, destinationLatLng, driverLatLng;
    TextView txtTime, txtKm, txtTravel, txtAmt, txtOrderNo, txtFareAmt, txtAdminFees, txtTotalPayout, txtRestName, txtrestAddr, txtCustomername, txtCustomerAddress;
    private String order_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_order_details);

        order_id = getIntent().getStringExtra("order_id");


        getIds();


        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if (CommonMethod.isOnline(CurrentOrderDetails.this)) {
            GetCurrentTripDetails();
        } else {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
        }
    }

    private void GetCurrentTripDetails() {

        CommonMethod.showProgressDialog(CurrentOrderDetails.this);

        RequestQueue queue = Volley.newRequestQueue(CurrentOrderDetails.this);

        StringRequest request = new StringRequest(Request.Method.POST, Constants.BASE_URL + Constants.currentTrip, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                CommonMethod.hideProgressDialog(CurrentOrderDetails.this);
                System.out.println("response_currentTrip: " + response);
                try {

                    Gson gson = new Gson();
                    CurrentTripParser currentTripParser = gson.fromJson(response, CurrentTripParser.class);

                    if (currentTripParser != null) {
                        if (currentTripParser.responsecode.equalsIgnoreCase("200")) {

                            setupMap(currentTripParser.tripdatas.get(0));

                            txtTime.setText("" + currentTripParser.tripdatas.get(0).duration+" "+getResources().getString(R.string.min));

                            txtKm.setText("" + currentTripParser.tripdatas.get(0).distance+" "+getResources().getString(R.string.km));

                            txtOrderNo.setText(getResources().getString(R.string.order_id) +" "+ currentTripParser.tripdatas.get(0).order_no);

                            txtAmt.setText(getResources().getString(R.string.dollar) + currentTripParser.tripdatas.get(0).order_place_amt+"");
                            txtFareAmt.setText(getResources().getString(R.string.dollar) + currentTripParser.tripdatas.get(0).fare_amt+"");
                            txtAdminFees.setText(getResources().getString(R.string.dollar) +currentTripParser.tripdatas.get(0).admin_fees+"");
                            txtTotalPayout.setText(getResources().getString(R.string.dollar) + currentTripParser.tripdatas.get(0).total_payout+"");
                            txtRestName.setText(currentTripParser.tripdatas.get(0).rst_name);
                            txtrestAddr.setText(currentTripParser.tripdatas.get(0).rst_address);
                            txtCustomername.setText(currentTripParser.tripdatas.get(0).user);
                            txtCustomerAddress.setText(currentTripParser.tripdatas.get(0).useraddress);
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
                params.put("driver_id", driver_id);
                params.put("order_id", order_id);
                params.put("lng", language);

                System.out.println("param_currentTrip :" + params);

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
        txtTime = findViewById(R.id.txtTime);
        txtKm = findViewById(R.id.txtKm);
        txtAmt = findViewById(R.id.txtAmt);
        txtOrderNo = findViewById(R.id.txtOrderNo);
        txtFareAmt = findViewById(R.id.txtFareAmt);
        txtAdminFees = findViewById(R.id.txtAdminFees);
        txtTotalPayout = findViewById(R.id.txtTotalPayout);
        txtRestName = findViewById(R.id.txtRestName);
        txtrestAddr = findViewById(R.id.txtrestAddr);
        txtCustomername = findViewById(R.id.txtCustomername);
        txtCustomerAddress = findViewById(R.id.txtCustomerAddress);
        txtTravel = findViewById(R.id.txtTravel);


    }


    private void setupMap(CurrentTripData tripdatas) {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                mMap.getUiSettings().setZoomControlsEnabled(true);
                mMap.getUiSettings().setCompassEnabled(false);
                mMap.getUiSettings().setMapToolbarEnabled(false);

                mMap = googleMap;

                String StoreLat = tripdatas.rst_lat, StoreLong =tripdatas.rst_lng;
                String CustomerAddressLat = tripdatas.user_lat, CustomerAddressLong = tripdatas.user_lng;


                sourceLatLng = new LatLng(Double.parseDouble(String.valueOf(StoreLat)), Double.parseDouble(String.valueOf(StoreLong)));
                destinationLatLng = new LatLng(Double.parseDouble(String.valueOf(CustomerAddressLat)), Double.parseDouble(String.valueOf(CustomerAddressLong)));

                setupSourceMarker(sourceLatLng);
                setupDestinationMarker(destinationLatLng);

                LatLngBounds.Builder boundsBuilder = LatLngBounds.builder();

                boundsBuilder = boundsBuilder.include(sourceMarker.getPosition());
                boundsBuilder = boundsBuilder.include(destinationMarker.getPosition());

                moveMapCamera(boundsBuilder.build(), dpToPx(60));

                drawArc(sourceMarker.getPosition(), destinationMarker.getPosition());

                moveMapCamera(sourceLatLng, 12);

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        drawRoute(sourceMarker.getPosition(), destinationMarker.getPosition());
                    }
                }, 3000);


            }
        });
    }

    private int dpToPx(int dp) {
        float density = this.getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    private void setupSourceMarker(final LatLng latLng) {
        if (sourceMarker != null) {
            sourceMarker.remove();
            sourceMarker = null;
        }

        System.out.println("SorcesLatlong :" + latLng);

        MarkerOptions options = new MarkerOptions();
        options.flat(true);
        options.anchor(0.5f, 0.5f);
        options.position(latLng);
        options.icon(bitmapDescriptorFromVector(CurrentOrderDetails.this, R.drawable.ic_user_1));

        sourceMarker = mMap.addMarker(options);
    }

    private void setupDestinationMarker(final LatLng latLng) {
        if (destinationMarker != null) {
            destinationMarker.remove();
            destinationMarker = null;
        }

        System.out.println("destLatlong :" + latLng);

        MarkerOptions options = new MarkerOptions();
        options.flat(true);
        options.anchor(0.5f, 0.5f);
        options.position(latLng);
        options.icon(bitmapDescriptorFromVector(CurrentOrderDetails.this, R.drawable.ic_location_1));

        destinationMarker = mMap.addMarker(options);
    }

    private void drawArc(LatLng start, LatLng end) {
        if (arc != null) {
            arc.remove();
            arc = null;
        }

        if (route != null) {
            route.remove();
            route = null;
        }

        List<LatLng> alLatLng = new ArrayList<>();

        double cLat = ((start.latitude + end.latitude) / 2);
        double cLon = ((start.longitude + end.longitude) / 2);

        //add skew and arcHeight to move the midPoint
        if (Math.abs(start.longitude - end.longitude) < 0.0001) {
            cLon -= 0.019257;
        } else {
            cLat += 0.019257;
        }

        double tDelta = 1.0 / 5000;
        for (double t = 0; t <= 1.0; t += tDelta) {
            double oneMinusT = (1.0 - t);
            double t2 = Math.pow(t, 2);
            double lon = oneMinusT * oneMinusT * start.longitude
                    + 2 * oneMinusT * t * cLon
                    + t2 * end.longitude;
            double lat = oneMinusT * oneMinusT * start.latitude
                    + 2 * oneMinusT * t * cLat
                    + t2 * end.latitude;
            alLatLng.add(new LatLng(lat, lon));
        }

        PolylineOptions arcOptions = new PolylineOptions().addAll(alLatLng).width(10).color(getResources().getColor(R.color.blue_new));
        arc = mMap.addPolyline(arcOptions);
    }

    private void drawRoute(LatLng start, LatLng end) {
        try {

            final StringRequest jsonObjectRequest = new StringRequest(Request.Method.GET, "https://maps.googleapis.com/maps/api/directions/json?origin=" + start.latitude + "," + start.longitude + "&destination=" + end.latitude + "," + end.longitude + "&key=AIzaSyAt7o6V5RlTQLWlKjfgwjOgaOhOBSNop0w&sensor=true", new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    //Log.e("Route success", response);

                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        JSONArray routesArray = jsonObject.getJSONArray("routes");

                        if (routesArray.length() > 0) {
                            JSONObject overviewPolylines = routesArray.getJSONObject(0).getJSONObject("overview_polyline");
                            String encodedPolyline = overviewPolylines.getString("points");

                        /*
                        self.routeDuration = Float(Int("\(json["routes"][0]["legs"][0]["duration"]["value"])") ?? 100000)
                                    self.routeDistance = Float(Int("\(json["routes"][0]["legs"][0]["distance"]["value"])") ?? 100000)
                         */

                            routeDuration = Float.valueOf(routesArray.getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("duration").getInt("value"));
                            routeDistance = Float.valueOf(routesArray.getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("distance").getInt("value"));

                            List<LatLng> latLngs = PolyUtil.decode(encodedPolyline);

                            if (arc != null) {
                                arc.remove();
                                arc = null;
                            }

                            if (route != null) {
                                route.remove();
                                route = null;
                            }

                            PolylineOptions arcOptions = new PolylineOptions().addAll(latLngs).width(10).color(getResources().getColor(R.color.blue_new));
                            route = mMap.addPolyline(arcOptions);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("Route err", error.getMessage());
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();

                    return params;
                }
            };

            Volley.newRequestQueue(getApplicationContext()).add(jsonObjectRequest);
        } catch (Exception ignored) {
        }
    }

    private void moveMapCamera(LatLng latLng, float zoom) {
        try {
            mMap.stopAnimation();
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        } catch (Exception e) {
        }
    }

    private void moveMapCamera(LatLngBounds bounds, int padding) {
        try {
            mMap.stopAnimation();
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}