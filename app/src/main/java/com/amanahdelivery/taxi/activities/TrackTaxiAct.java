package com.amanahdelivery.taxi.activities;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.os.StrictMode;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.amanahdelivery.R;
import com.amanahdelivery.databinding.ActivityTrackTaxiBinding;
import com.amanahdelivery.databinding.RideCancellationDialogBinding;
import com.amanahdelivery.models.ModelLogin;
import com.amanahdelivery.taxi.dialogs.DialogMessage;
import com.amanahdelivery.taxi.models.ModelCurrentBooking;
import com.amanahdelivery.taxi.models.ModelCurrentBookingResult;
import com.amanahdelivery.taxi.models.ModelTaxiPayment;
import com.amanahdelivery.utils.AppConstant;
import com.amanahdelivery.utils.LatLngInterpolator;
import com.amanahdelivery.utils.MarkerAnimation;
import com.amanahdelivery.utils.MusicManager;
import com.amanahdelivery.utils.ProjectUtil;
import com.amanahdelivery.utils.SharedPref;
import com.amanahdelivery.utils.directionclasses.BaseClass;
import com.amanahdelivery.utils.retrofitutils.Api;
import com.amanahdelivery.utils.retrofitutils.ApiFactory;
import com.bumptech.glide.Glide;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.TimeZone;

import ng.max.slideview.SlideView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrackTaxiAct extends AppCompatActivity implements OnMapReadyCallback {

    Context mContext = TrackTaxiAct.this;
    private ModelCurrentBooking data;
    ActivityTrackTaxiBinding binding;
    SharedPref sharedPref;
    ModelLogin modelLogin;
    private Marker currentLocationMarker;
    private ModelLogin.Result UserDetails;
    private ModelCurrentBookingResult result;
    private String requestId, userMobile, userId, userName;
    private LocationRequest mLocationRequest;
    private long UPDATE_INTERVAL = 2000;  /* 5 secs */
    private long FASTEST_INTERVAL = 2000; /* 2 sec */
    Location currentLocation;
    Vibrator vibrator;
    GoogleMap mMap;
    private LatLng pickLocation, droplocation;
    SupportMapFragment mapFragment;

    BroadcastReceiver statusBroadCast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("job_status")) {
                if (intent.getStringExtra("status").equals("Cancel")) {
                    MusicManager.getInstance().initalizeMediaPlayer(TrackTaxiAct.this, Uri.parse
                            (ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getPackageName() + "/" + R.raw.doogee_ringtone));
                    MusicManager.getInstance().stopPlaying();
                    Toast.makeText(context, "Ride cancelled by user", Toast.LENGTH_LONG).show();
                    finishAffinity();
                    startActivity(new Intent(TrackTaxiAct.this, TaxiHomeAct.class));
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_track_taxi);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(TrackTaxiAct.this);

        // startLocationUpdates();

        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            if (getIntent() != null) {
                data = (ModelCurrentBooking) getIntent().getSerializableExtra("data");
                result = data.getResult().get(0);
                requestId = result.getId();
                userMobile = data.getResult().get(0).getUserDetails().get(0).getMobile();
                userId = data.getResult().get(0).getUserDetails().get(0).getId();
                userName = data.getResult().get(0).getUserDetails().get(0).getUser_name();
                UserDetails = result.getUserDetails().get(0);
                binding.setBooking(UserDetails);
                if (UserDetails.getProfile_image() != null) {
                    Glide.with(mContext)
                            .load(UserDetails.getProfile_image())
                            .placeholder(R.drawable.user_ic)
                            .error(R.drawable.user_ic)
                            .into(binding.userImage);
                }
            }
        } catch (Exception e) {
        }

        itit();

    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(statusBroadCast, new IntentFilter("job_status"));
        startLocationUpdates();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(statusBroadCast);
    }

    private void itit() {

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        pickLocation = new LatLng(Double.parseDouble(result.getPicuplat()),
                Double.parseDouble(result.getPickuplon()));

        try {
            droplocation = new LatLng(Double.parseDouble(result.getDroplat()), Double.parseDouble(result.getDroplon()));
        } catch (Exception e) {
        }

        if (result.getStatus().equalsIgnoreCase("Arrived")) {
            binding.btnArrived.setVisibility(View.GONE);
            binding.btnBegin.setVisibility(View.VISIBLE);
        } else if (result.getStatus().equalsIgnoreCase("Start")) {
            binding.btnArrived.setVisibility(View.GONE);
            binding.btnBegin.setVisibility(View.GONE);
            binding.btnEnd.setVisibility(View.VISIBLE);
        } else if (result.getStatus().equalsIgnoreCase("End")) {
//            startActivity(new Intent(mContext, PaymentTaxiAct.class)
//                    .putExtra("data", data));
//            finish();
        } else if (result.getStatus().equalsIgnoreCase("Cancel")) {
            finish();
        }

        binding.ivCancel.setOnClickListener(v -> {
            rideCancelDialog();
//            DialogMessage.get(this)
//                    .setMessage(getResources().getString(R.string.ride_cancel_text))
//                    .Callback(() -> {
//
//                    }).show();
        });

        binding.btnArrived.setOnClickListener(v -> {
            DialogMessage.get(this)
                    .setMessage(getResources().getString(R.string.are_you_sure_you_have_arrived_at_pickup_location_of_passenger))
                    .Callback(() -> {
                        DriverChangeStatus("Arrived", "");
                    }).show();
        });

        binding.slideViewBegin.setOnSlideCompleteListener(new SlideView.OnSlideCompleteListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onSlideComplete(SlideView slideView) {
                vibrator.vibrate(100);
                DialogMessage.get(mContext)
                        .setMessage(getResources().getString(R.string.tostarttrip))
                        .Callback(() -> {
                            DriverChangeStatus("Start", "");
                        }).show();
            }
        });

        binding.slideViewEnd.setOnSlideCompleteListener(new SlideView.OnSlideCompleteListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onSlideComplete(SlideView slideView) {
                vibrator.vibrate(100);
                DialogMessage.get(mContext)
                        .setMessage(getResources().getString(R.string.toendtrip))
                        .Callback(() -> {
                            DriverChangeStatus("End", "");
                        }).show();
            }
        });

        binding.icCall.setOnClickListener(v -> {
            ProjectUtil.callCustomer(mContext, userMobile);
        });

        binding.btnPickupNavigate.setOnClickListener(v -> {
            // if (currentLocation != null)
            moveToGoogle("Your location", pickLocation.latitude + "," + pickLocation.longitude);
        });

        binding.btnDestiNavigate.setOnClickListener(v -> {
            try {
                moveToGoogle("Your location", droplocation.latitude + "," + droplocation.longitude);
            } catch (Exception e) {
                moveToGoogle("Your location", ",");
            }
        });

    }

    private void rideCancelDialog() {

        Dialog dialog = new Dialog(mContext, WindowManager.LayoutParams.MATCH_PARENT);
        RideCancellationDialogBinding dialogBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext)
                , R.layout.ride_cancellation_dialog, null, false);
        dialog.setContentView(dialogBinding.getRoot());

        dialogBinding.btnSubmit.setOnClickListener(v -> {
            if (TextUtils.isEmpty(dialogBinding.etReason.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.please_enter_reason), Toast.LENGTH_SHORT).show();
            } else {
                dialog.dismiss();
                DriverChangeStatus("Cancel", dialogBinding.etReason.getText().toString().trim());
            }
        });

        dialog.show();

    }

    // Trigger new location updates at interval
    @SuppressLint("MissingPermission")
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

        SettingsClient settingsClient = LocationServices.getSettingsClient(TrackTaxiAct.this);
        settingsClient.checkLocationSettings(locationSettingsRequest);

        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(TrackTaxiAct.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(TrackTaxiAct.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        getFusedLocationProviderClient(TrackTaxiAct.this).requestLocationUpdates(mLocationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null) {
                    Log.e("hdasfkjhksdf", "StartLocationUpdate = " + locationResult.getLastLocation());
                    currentLocation = locationResult.getLastLocation();
                    // currentlocation = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                    showMarkerCurrentLocation(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()));
                }
            }
        }, Looper.myLooper());

    }

    private void showMarkerCurrentLocation(@NonNull LatLng currentLocation) {
        if (currentLocation != null) {
            if (currentLocationMarker == null) {
                if (mMap != null) {
                    currentLocationMarker = mMap.addMarker(new MarkerOptions().position(currentLocation).title("My Location")
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.car_top)));
                    animateCamera(currentLocation);
                }
            } else {
                Log.e("sdfdsfdsfds", "Hello Marker Anuimation");
                // animateCamera(currentLocation);
                MarkerAnimation.animateMarkerToGB(currentLocationMarker, currentLocation, new LatLngInterpolator.Spherical());
            }
        }
    }

    private void animateCamera(@NonNull LatLng location) {
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(getCameraPositionWithBearing(location)));
    }

    @NonNull
    private CameraPosition getCameraPositionWithBearing(LatLng latLng) {
        return new CameraPosition.Builder().target(latLng).zoom(16).build();
    }

    private void DriverChangeStatus(String status, String reason) {

        HashMap<String, String> params = new HashMap<>();
        params.put("request_id", result.getId());
        params.put("cancel_reason", reason);
        params.put("status", status);
        params.put("timezone", TimeZone.getDefault().getID());
        params.put("driver_id", modelLogin.getResult().getId());

        if (status.equals("End")) {
            if (currentLocation != null) {
                String add = ProjectUtil.getCompleteAddressString(mContext, currentLocation.getLatitude(), currentLocation.getLongitude());
                params.put("droplat", "" + currentLocation.getLatitude());
                params.put("droplon", "" + currentLocation.getLongitude());
                params.put("dropofflocation", add);
            } else {
                params.put("droplat", "");
                params.put("droplon", "");
                params.put("dropofflocation", "");
            }
        }

        Log.e("zsdfasdfasdfas", "params = " + params);

        ProjectUtil.showProgressDialog(mContext, false, getString(R.string.please_wait));
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.acceptCancelOrderCallTaxi(params);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String stringResp = response.body().string();
                    Log.e("zsdfasdfasdfas", "stringResp = " + stringResp);
                    // Log.e("zsdfasdfasdfas","modelTaxiPayment = " + new Gson().toJson(modelTaxiPayment));
                    JSONObject object = new JSONObject(stringResp);
                    if (object.getString("status").equals("1")) {
                        Log.e("zsdfasdfasdfas", "status = " + status);
                        ModelTaxiPayment modelTaxiPayment = new Gson().fromJson(stringResp, ModelTaxiPayment.class);
                        Log.e("zsdfasdfasdfas", "modelTaxiPayment = " + new Gson().toJson(modelTaxiPayment));
                        sharedPref.setlanguage(AppConstant.LAST, status);
                        if (status.equalsIgnoreCase("Arrived")) {
                            Log.e("zsdfasdfasdfas", "Arrived = " + status);
                            binding.btnArrived.setVisibility(View.GONE);
                            binding.btnBegin.setVisibility(View.VISIBLE);
                        } else if (status.equalsIgnoreCase("Start")) {
                            Log.e("zsdfasdfasdfas", "Start = " + status);
                            binding.btnArrived.setVisibility(View.GONE);
                            binding.btnBegin.setVisibility(View.GONE);
                            binding.btnEnd.setVisibility(View.VISIBLE);
                        } else if (status.equalsIgnoreCase("End")) {
                            Log.e("zsdfasdfasdfas", "Start = " + status);
                            Toast.makeText(mContext, "Trip Finish", Toast.LENGTH_SHORT).show();
//                            startActivity(new Intent(mContext, PaymentTaxiAct.class)
//                                    .putExtra("data", modelTaxiPayment)
//                                    .putExtra("id", modelTaxiPayment.getResult().getId()));
                            GetCurrentBooking();
                        } else if (status.equalsIgnoreCase("Cancel")) {
                            Log.e("zsdfasdfasdfas", "Start = " + status);
                            finish();
                        }
                    } else {
                        DialogMessage.get(mContext).setMessage("Something went wrong! try again").show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
            }
        });

    }

    private void GetCurrentBooking() {
        HashMap<String, String> param = new HashMap<>();
        param.put("user_id", modelLogin.getResult().getId());
        param.put("type", AppConstant.TAXI_DRIVER);
        param.put("timezone", TimeZone.getDefault().getID());
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Log.e("BookingStatusResponse", "param = " + param);
        Call<ResponseBody> call = api.getCurrentTaxiBooking(param);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String stringResponse = response.body().string();
                    JSONObject object = new JSONObject(stringResponse);
                    if (object.getString("status").equals("1")) {
                        Log.e("BookingStatusResponse", "stringResponse123 = " + stringResponse);
                        // Type listType1 = new TypeToken<ModelCurrentBooking>(){}.getType();
                        Log.e("BookingStatusResponse", "TypeToken = " + new Gson().fromJson(stringResponse, ModelCurrentBooking.class));
                        Type listType = new TypeToken<ModelCurrentBooking>() {
                        }.getType();
                        ModelCurrentBooking data = new Gson().fromJson(stringResponse, listType);
                        Log.e("BookingStatus", "data =  " + data.getStatus());
                        if (data.getStatus().equals(1)) {
                            ModelCurrentBookingResult result = data.getResult().get(0);
                            Log.e("BookingStatus", "Result Status =  " + result.getStatus());
                            if (result.getStatus().equalsIgnoreCase("Accept")) {
                                Intent k = new Intent(mContext, TrackTaxiAct.class);
                                k.putExtra("data", data);
                                startActivity(k);
                            } else if (result.getStatus().equalsIgnoreCase("Arrived")) {
                                Intent j = new Intent(mContext, TrackTaxiAct.class);
                                j.putExtra("data", data);
                                startActivity(j);
                            } else if (result.getStatus().equalsIgnoreCase("Start")) {
                                Intent j = new Intent(mContext, TrackTaxiAct.class);
                                j.putExtra("data", data);
                                startActivity(j);
                            } else if (result.getStatus().equalsIgnoreCase("End")) {
                                Intent j = new Intent(mContext, PaymentTaxiAct.class);
                                j.putExtra("data", data);
                                j.putExtra("type", "home");
                                j.putExtra("id", result.getId());
                                startActivity(j);
                                finish();
                            }
                            sharedPref.setlanguage(AppConstant.LAST, result.getStatus());
                        }
                    }
                } catch (Exception e) {
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    private void moveToGoogle(String sAddres, String dAddress) {
        try {
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                    Uri.parse("http://maps.google.com/maps?saddr=" + sAddres + "&daddr=" + dAddress));
            intent.setPackage("com.google.android.apps.maps");
            startActivity(intent);
        } catch (Exception e) {
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                    Uri.parse("http://maps.google.com/maps?saddr=" + sAddres + "&daddr=" + dAddress));
            startActivity(intent);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setMapToolbarEnabled(false);
    }


}