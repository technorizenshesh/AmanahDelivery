package com.amanahdelivery.deliveryshops.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.amanahdelivery.Application.MyApplication;
import com.amanahdelivery.R;
import com.amanahdelivery.databinding.ActivityTrackShopBinding;
import com.amanahdelivery.databinding.DialogUniqueCodeBinding;
import com.amanahdelivery.models.ModelLogin;
import com.amanahdelivery.utils.AppConstant;
import com.amanahdelivery.utils.LatLngInterpolator;
import com.amanahdelivery.utils.MarkerAnimation;
import com.amanahdelivery.utils.MusicManager;
import com.amanahdelivery.utils.ProjectUtil;
import com.amanahdelivery.utils.SharedPref;
import com.amanahdelivery.utils.directionclasses.DrawPollyLine;
import com.amanahdelivery.utils.retrofitutils.Api;
import com.amanahdelivery.utils.retrofitutils.ApiFactory;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class TrackShopAct extends AppCompatActivity
        implements OnMapReadyCallback {

    Context mContext = TrackShopAct.this;
    ActivityTrackShopBinding binding;
    GoogleMap mMap;
    boolean isMapLoaded;
    private String status,orderId,orderIdScan,uniqueCode="";
    private Marker currentLocationMarker,storeOrCustomerLocationMarker;
    private LocationRequest mLocationRequest;
    private long UPDATE_INTERVAL = 3000; /* 5 secs */
    private long FASTEST_INTERVAL = 3000; /* 2 sec */
    private Location currentLocation;
    SharedPref sharedPref;
    ModelLogin modelLogin;
    // qr code scanner object
    private IntentIntegrator qrScan;
    private LatLng currentLatLon,storeLatLon,customerLatLon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_track_shop);
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);

        //intializing scan object
        qrScan = new IntentIntegrator(this);

        status = getIntent().getStringExtra("status");
        orderId = getIntent().getStringExtra("orderId");
        orderIdScan = getIntent().getStringExtra("orderIdScan");
        uniqueCode = getIntent().getStringExtra("code");

        Log.e("codecodecode","uniqueCode = " + uniqueCode);

        storeLatLon = new LatLng(getIntent().getDoubleExtra("storelat",0.0),
                      getIntent().getDoubleExtra("storelon",0.0));

        customerLatLon = new LatLng(getIntent().getDoubleExtra("custlat",0.0),
                      getIntent().getDoubleExtra("custlon",0.0));

        Log.e("fdasfasas","storeLatLon = " + storeLatLon);
        Log.e("fdasfasas","customerLatLon = " + customerLatLon);
        Log.e("fdasfasas","status = " + status);

        startLocationUpdates();

        itit();

    }

    private void itit() {

        if("Delivered".equals(status)) {
            binding.btnUpdateStatus.setText("This order is delivered!");
        } else if("Accept".equals(status)) {
            binding.btnUpdateStatus.setText(getString(R.string.update_when_you_pickorder));
        } else if("Pickup".equals(status)) {
            binding.btnCode.setVisibility(View.VISIBLE);
            binding.btnUpdateStatus.setText(getString(R.string.scan_qr_code_to_dev_order));
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(TrackShopAct.this);

        binding.btnUpdateStatus.setOnClickListener(v -> {
            if("Delivered".equals(status)) {
                Toast.makeText(mContext, getString(R.string.already_dev_order), Toast.LENGTH_SHORT).show();
            } else if("Accept".equals(status)) {
                AcceptCancel("Pickup");
            } else if("Pickup".equals(status)) {
                // initiating the qr code scan
                qrScan.initiateScan();
                // AcceptCancel("Delivered");
            }
        });

        binding.btnCode.setOnClickListener(v -> {
            uniqueCodeDialog();
        });

        binding.btnCustomerNav.setOnClickListener(v -> {
            Log.e("sdfasdfasf","btnCustomerNav");
            if(currentLatLon != null) {
                ProjectUtil.navigateToGooogleMap(mContext,AppConstant.MY_LOCATION,customerLatLon.latitude+","+customerLatLon.longitude);
                Log.e("sdfasdfasf","btnCustomerNav  currentLatLon = " + currentLatLon);
                // drawRoute("Cust",currentLatLon,customerLatLon);
            }
        });

        binding.btnStoreNav.setOnClickListener(v -> {
            if(currentLatLon != null) {
                ProjectUtil.navigateToGooogleMap(mContext,
                        AppConstant.MY_LOCATION,storeLatLon.latitude+","+storeLatLon.longitude);
               // drawRoute("store",currentLatLon,storeLatLon);
            }
        });

    }

    private void uniqueCodeDialog() {
        Dialog dialog = new Dialog(mContext, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.setCancelable(true);
        DialogUniqueCodeBinding dialogBindig = DataBindingUtil.inflate(LayoutInflater.from(mContext)
                ,R.layout.dialog_unique_code,null,false);
        dialog.setContentView(dialogBindig.getRoot());

        dialog.getWindow().setBackgroundDrawableResource(R.color.blacktemp);

        dialogBindig.btnSubmit.setOnClickListener(v -> {
            if(TextUtils.isEmpty(dialogBindig.etCode.getText().toString().trim())) {
                Toast.makeText(mContext, "Please enter 5 digit Code", Toast.LENGTH_SHORT).show();
            } else if(dialogBindig.etCode.getText().toString().trim().equals(uniqueCode.trim())) {
                AcceptCancel("Delivered");
            } else {
                dialogBindig.etCode.setError("Incorrect Code please confirm to admin");
                Toast.makeText(mContext, "Incorrect Code please confirm to admin", Toast.LENGTH_LONG).show();
            }
        });
        dialog.show();
    }

    // Getting the scan results
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            // if qrcode has nothing in it
            if (result.getContents() == null) {
                Toast.makeText(this, "Result Not Found", Toast.LENGTH_LONG).show();
            } else {
                // if qr contains data
                try {
                    // converting the data to json
                    Log.e("IntentResultQR","result.getContents() = " + result.getContents());
                    Log.e("IntentResultQR","orderIdScan = " + orderIdScan);
                    if(orderIdScan.equals(result.getContents().trim())) {
                        // Toast.makeText(mContext, "QR Scanned Successfully", Toast.LENGTH_SHORT).show();
                        AcceptCancel("Delivered");
                    } else {
                        Toast.makeText(mContext,"Please confirm that QR code is correct", Toast.LENGTH_LONG).show();
                    }
                    // setting values to textviews
                    // textViewName.setText(obj.getString("name"));
                    // textViewAddress.setText(obj.getString("address"));
                } catch (Exception e) {
                    e.printStackTrace();
                    // if control comes here
                    // that means the encoded format not matches
                    // in this case you can display whatever data is available on the qrcode
                    // to a toast
                    Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG).show();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void AcceptCancel(String statuss) {
        ProjectUtil.showProgressDialog(mContext,false, mContext.getString(R.string.please_wait));

        HashMap<String,String> map = new HashMap<>();
        map.put("order_id",orderId);
        map.put("status",statuss);
        map.put("driver_id", modelLogin.getResult().getId());

        Log.e("dfsadasdasd",map.toString());

        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.acceptCancelOrderCall(map);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    Log.e("responseTrack", "responseString = " + responseString);

                    if (jsonObject.getString("status").equals("1")) {
                        if("Delivered".equals(statuss)) {
                            status = statuss;
                            ShopOrderHomeAct.statusses = statuss;
                            binding.btnCode.setVisibility(View.GONE);
                            binding.btnUpdateStatus.setText("This order is delivered!");
                            startActivity(new Intent(mContext,ShopOrderHomeAct.class));
                            finish();
                        } else if("Accept".equals(statuss)) {
                            status = statuss;
                            ShopOrderHomeAct.statusses = statuss;
                            binding.btnUpdateStatus.setText(getString(R.string.update_when_you_pickorder));
                        } else if("Pickup".equals(statuss)) {
                            status = statuss;
                            ShopOrderHomeAct.statusses = statuss;
                            binding.btnCode.setVisibility(View.VISIBLE);
                            binding.btnUpdateStatus.setText(getString(R.string.scan_qr_code_to_dev_order));
                        }
                    } else {}
                } catch (Exception e) {
                    status = statuss;
                    binding.btnUpdateStatus.setText("This order is delivered!");
                    startActivity(new Intent(mContext,ShopOrderHomeAct.class));
                    finish();
                    // Toast.makeText(mContext, "Exception = " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Exception", "Exception = " + e.getMessage());
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
            }

        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        isMapLoaded = true;
        mMap = googleMap;
    }

    private void showDestinationMarker(@NonNull LatLng dcurrentLocation,String type) {
        Log.e("TAG", "showDestinationMarker: " + dcurrentLocation);
        if (dcurrentLocation != null) {
            if (storeOrCustomerLocationMarker == null) {
                String add = ProjectUtil.getCompleteAddressString(mContext,currentLatLon.latitude,currentLatLon.longitude);
                if("store".equals(type)) {
                    storeOrCustomerLocationMarker = mMap.addMarker(new MarkerOptions().position(dcurrentLocation).title(add)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.store_marker)));
                } else {
                    storeOrCustomerLocationMarker = mMap.addMarker(new MarkerOptions().position(dcurrentLocation).title(add)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.red_marker)));
                }
            }
        }
    }

    protected void startLocationUpdates() {

        // Create the location request to start receiving updates
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

        // Create LocationSettingsRequest object using location request
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        SettingsClient settingsClient = LocationServices.getSettingsClient(TrackShopAct.this);
        settingsClient.checkLocationSettings(locationSettingsRequest);

        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(TrackShopAct.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(TrackShopAct.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        getFusedLocationProviderClient(TrackShopAct.this).requestLocationUpdates(mLocationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if(locationResult != null) {
                    Log.e("hdasfkjhksdf", "StartLocationUpdate = " + locationResult.getLastLocation());
                    currentLocation = locationResult.getLastLocation();
                    currentLatLon = new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());
                    showMarkerCurrentLocation(new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude()));
                }
            }
        }, Looper.myLooper());

    }

    private void showMarkerCurrentLocation(@NonNull LatLng currentLocation) {
        if (currentLocation != null) {
            if (currentLocationMarker == null) {
                currentLocationMarker = mMap.addMarker(new MarkerOptions().position(currentLatLon).title("My Location")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.dev_marker)));
                animateCamera(currentLocation);
            } else {
                Log.e("sdfdsfdsfds","Hello Marker Anuimation");
                MarkerAnimation.animateMarkerToGB(currentLocationMarker, currentLocation, new LatLngInterpolator.Spherical());
            }

        }

    }

    private void drawRoute(String type,LatLng current,LatLng drop) {

        if(mMap != null) {
            mMap.clear();
            currentLocationMarker = null;
            storeOrCustomerLocationMarker = null;
        }

        showMarkerCurrentLocation(current);
        showDestinationMarker(drop,type);

        DrawPollyLine.get(this)
                .setOrigin(current)
                .setDestination(drop)
                .execute(new DrawPollyLine.onPolyLineResponse() {
                    @Override
                    public void Success(ArrayList<LatLng> latLngs) {
                        PolylineOptions options = new PolylineOptions();
                        options.addAll(latLngs);
                        options.color(Color.BLUE);
                        options.width(10);
                        options.startCap(new SquareCap());
                        options.endCap(new SquareCap());

                        Polyline line = mMap.addPolyline(options);

                    }
                });

        // zoomMapInitial(current,drop);


    }

    private void animateCamera(@NonNull LatLng location) {
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(getCameraPositionWithBearing(location)));
    }

    @NonNull
    private CameraPosition getCameraPositionWithBearing(LatLng latLng) {
        return new CameraPosition.Builder().target(latLng).zoom(15).build();
    }

    protected void zoomMapInitial(LatLng currenLoc,LatLng pickup) {
        try {
            int padding = 200;
            LatLngBounds.Builder bc = new LatLngBounds.Builder();
            bc.include(currenLoc);
            bc.include(pickup);
            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bc.build(), padding));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}