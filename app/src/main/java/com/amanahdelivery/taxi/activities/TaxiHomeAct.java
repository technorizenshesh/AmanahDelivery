package com.amanahdelivery.taxi.activities;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import android.Manifest;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.Toast;
import com.amanahdelivery.Application.MyApplication;
import com.amanahdelivery.R;
import com.amanahdelivery.databinding.ActivityTaxiHomeBinding;
import com.amanahdelivery.databinding.CompAccountDialogBinding;
import com.amanahdelivery.databinding.SupportDialogBinding;
import com.amanahdelivery.deliveryshops.activities.ChnagePassAct;
import com.amanahdelivery.deliveryshops.activities.LoginAct;
import com.amanahdelivery.deliveryshops.activities.ShopOrderHomeAct;
import com.amanahdelivery.deliveryshops.adapters.AdapterSupports;
import com.amanahdelivery.models.ModelLogin;
import com.amanahdelivery.models.ModelSupport;
import com.amanahdelivery.taxi.dialogs.DialogMessage;
import com.amanahdelivery.taxi.dialogs.NewRequestDialogTaxi;
import com.amanahdelivery.taxi.models.ModelCurrentBooking;
import com.amanahdelivery.taxi.models.ModelCurrentBookingResult;
import com.amanahdelivery.taxi.taxihelper.GPSTracker;
import com.amanahdelivery.taxi.taxiinterfaces.RequestDialogCallBackInterface;
import com.amanahdelivery.taxi.taxiinterfaces.onRequestListener;
import com.amanahdelivery.utils.AppConstant;
import com.amanahdelivery.utils.LatLngInterpolator;
import com.amanahdelivery.utils.MarkerAnimation;
import com.amanahdelivery.utils.MusicManager;
import com.amanahdelivery.utils.MyService;
import com.amanahdelivery.utils.ProjectUtil;
import com.amanahdelivery.utils.SharedPref;
import com.amanahdelivery.utils.networks.NetworkReceiver;
import com.amanahdelivery.utils.retrofitutils.Api;
import com.amanahdelivery.utils.retrofitutils.ApiFactory;
import com.bumptech.glide.Glide;
import com.github.angads25.toggle.LabeledSwitch;
import com.github.angads25.toggle.interfaces.OnToggledListener;
import com.google.android.gms.location.FusedLocationProviderClient;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.json.JSONObject;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.TimeZone;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TaxiHomeAct extends AppCompatActivity implements OnMapReadyCallback,
        NetworkReceiver.ConnectivityReceiverListener,
        RequestDialogCallBackInterface,onRequestListener {

    private static final int PERMISSION_ID = 101;
    Context mContext = TaxiHomeAct.this;
    SharedPref sharedPref;
    ModelLogin modelLogin;
    NetworkReceiver receiver;
    ActivityTaxiHomeBinding binding;
    private FusedLocationProviderClient fusedLocationClient;
    private NewRequestDialogTaxi requestDialog;
    private String type;
    GoogleMap mMap;
    private Marker currentLocationMarker;
    private LocationRequest mLocationRequest;
    private long UPDATE_INTERVAL = 2000;  /* 5 secs */
    private long FASTEST_INTERVAL = 2000; /* 2 sec */
    private Location mLocation;
    private Location currentLocation;

    BroadcastReceiver JobStatusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getStringExtra("object") != null) {
                JSONObject object = null;
                try {
                    object = new JSONObject(intent.getStringExtra("object"));
                    if ("Pending".equals(object.getString("status")))
                        NewRequestDialogTaxi.getInstance().Request(TaxiHomeAct.this, intent.getStringExtra("object"));
                    else GetCurrentBooking();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (intent.getAction().equals("data_update_location")) {
                double lat = intent.getDoubleExtra("latitude", 0);
                double lng = intent.getDoubleExtra("longitude", 0);
                float bearing = intent.getFloatExtra("bearing", 0);
//                if (carMarker != null) {
//                    Log.e("locationResult", "" + bearing);
//                    carMarker.position(new LatLng(lat, lng));
//                    ProjectUtil.rotateMarker(carMarker, bearing);
//                }
            } else {
                GetCurrentBooking();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_taxi_home);
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);
        type = getIntent().getStringExtra("type");
        itit();
        if (getIntent().getStringExtra("object") != null) {
            MusicManager.getInstance().initalizeMediaPlayer(TaxiHomeAct.this, Uri.parse
                    (ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getPackageName() + "/" + R.raw.doogee_ringtone));
            MusicManager.getInstance().stopPlaying();
            NewRequestDialogTaxi.getInstance().Request(TaxiHomeAct.this, getIntent().getStringExtra("object"));
        }

    }

    private void itit() {

        receiver = new NetworkReceiver();
        MyApplication.getInstance().setConnectivityListener(TaxiHomeAct.this);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            mLocation = location;
                        }
                    }
                });

        binding.icMenu.setOnClickListener(v -> {
            binding.drawerLayout.openDrawer(GravityCompat.START);
        });

        binding.childNavDrawer.btnSupport.setOnClickListener(v -> {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            supportApi();
        });

        binding.childNavDrawer.btnCompAccount.setOnClickListener(v -> {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            compAccDialog();
        });

        binding.childNavDrawer.btnChangePass.setOnClickListener(v -> {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            startActivity(new Intent(mContext,ChnagePassAct.class));
        });

        binding.childNavDrawer.tvLogout.setOnClickListener(v -> {
            logoutAppDialog();
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        });

        if ("ONLINE".equals(modelLogin.getResult().getOnline_status())) {
            binding.switchOnOff.setOn(true);
        } else if ("OFFLINE".equals(modelLogin.getResult().getOnline_status())) {
            binding.switchOnOff.setOn(false);
        } else {
            binding.switchOnOff.setOn(false);
        }

        binding.switchOnOff.setOnToggledListener(new OnToggledListener() {
            @Override
            public void onSwitched(LabeledSwitch labeledSwitch, boolean isOn) {
                if (isOn) {
                    onlinOfflineApi("ONLINE");
                } else {
                    onlinOfflineApi("OFFLINE");
                }
            }
        });

    }

    private void compAccDialog() {
        Dialog dialog = new Dialog(mContext, WindowManager.LayoutParams.MATCH_PARENT);
        CompAccountDialogBinding dialogBinding = DataBindingUtil
                .inflate(LayoutInflater.from(mContext),R.layout.comp_account_dialog,
                        null,false);
        dialog.setContentView(dialogBinding.getRoot());

        dialogBinding.ivBack.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.show();

    }

    private void supportApi() {
        ProjectUtil.showProgressDialog(mContext,true,getString(R.string.please_wait));
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);

        Call<ResponseBody> call = api.getSupportApi();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {

                    String stringResponse = response.body().string();
                    JSONObject jsonObject = new JSONObject(stringResponse);

                    Log.e("supportApi","supportApi = " + stringResponse);

                    if(jsonObject.getString("status").equals("1")) {
                        ModelSupport modelSupport = new Gson().fromJson(stringResponse,ModelSupport.class);
                        supportDialog(modelSupport);
                    } else {
                        Toast.makeText(TaxiHomeAct.this, "Support is not available at this time", Toast.LENGTH_SHORT).show();
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

    private void supportDialog(ModelSupport modelSupport) {
        Dialog dialog = new Dialog(mContext, WindowManager.LayoutParams.MATCH_PARENT);
        SupportDialogBinding dialogBinding = DataBindingUtil
                .inflate(LayoutInflater.from(mContext),R.layout.support_dialog,
                        null,false);
        dialog.setContentView(dialogBinding.getRoot());

        AdapterSupports adapterSupports = new AdapterSupports(mContext,modelSupport.getResult());
        dialogBinding.rvSupport.setAdapter(adapterSupports);

        dialogBinding.ivBack.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.show();
    }

    private void logoutAppDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(getString(R.string.logout_text))
                .setCancelable(false)
                .setPositiveButton(mContext.getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sharedPref.clearAllPreferences();
                        Intent loginscreen = new Intent(mContext, LoginAct.class);
                        loginscreen.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        NotificationManager nManager = ((NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE));
                        nManager.cancelAll();
                        startActivity(loginscreen);
                        finishAffinity();
                    }
                }).setNegativeButton(mContext.getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }

    private void GetCurrentBooking() {
        HashMap<String, String> param = new HashMap<>();
        param.put("user_id", modelLogin.getResult().getId());
        param.put("type", AppConstant.TAXI_DRIVER);
        param.put("timezone", TimeZone.getDefault().getID());
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Log.e("BookingStatusResponse","param = " + param);
        Call<ResponseBody> call = api.getCurrentTaxiBooking(param);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String stringResponse = response.body().string();
                    JSONObject object=new JSONObject(stringResponse);
                    if (object.getString("status").equals("1")) {
                        Log.e("BookingStatusResponse","stringResponse123 = " + stringResponse);
                        // Type listType1 = new TypeToken<ModelCurrentBooking>(){}.getType();
                        Log.e("BookingStatusResponse","TypeToken = " + new Gson().fromJson(stringResponse, ModelCurrentBooking.class));
                        Type listType = new TypeToken<ModelCurrentBooking>(){}.getType();
                        ModelCurrentBooking data = new Gson().fromJson(stringResponse,listType);
                        Log.e("BookingStatus","data =  " + data.getStatus());
                        if (data.getStatus().equals(1)) {
                            ModelCurrentBookingResult result = data.getResult().get(0);
                            Log.e("BookingStatus","Result Status =  " + result.getStatus());
                            if (result.getStatus().equalsIgnoreCase("Accept")) {
                                Intent k = new Intent(mContext, TrackTaxiAct.class);
                                k.putExtra("data",data);
                                startActivity(k);
                            } else if (result.getStatus().equalsIgnoreCase("Arrived")) {
                                Intent j = new Intent(mContext, TrackTaxiAct.class);
                                j.putExtra("data",data);
                                startActivity(j);
                            } else if (result.getStatus().equalsIgnoreCase("Start")) {
                                Intent j = new Intent(mContext, TrackTaxiAct.class);
                                j.putExtra("data",data);
                                startActivity(j);
                            } else if (result.getStatus().equalsIgnoreCase("End")) {
//                                Intent j = new Intent(mContext, PaymentSummary.class);
//                                j.putExtra("data",data);
//                                startActivity(j);
                            }
                            sharedPref.setlanguage(AppConstant.LAST,result.getStatus());
                        }
                    }
                } catch (Exception e) {}
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    private void onlinOfflineApi(String status) {
        ProjectUtil.showProgressDialog(mContext, true, getString(R.string.please_wait));
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        HashMap<String, String> param = new HashMap<>();
        param.put("user_id", modelLogin.getResult().getId());
        param.put("status", status);
        Call<ResponseBody> call = api.updateOnOffApi(param);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                Log.e("xjgxkjdgvxsd", "response = " + response);
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    if (jsonObject.getString("status").equals("1")) {
                        if (status.equals("ONLINE")) {
                            modelLogin.getResult().setOnline_status("ONLINE");
                            sharedPref.setUserDetails(AppConstant.USER_DETAILS, modelLogin);
                            binding.switchOnOff.setOn(true);
                        } else {
                            modelLogin.getResult().setOnline_status("OFFLINE");
                            sharedPref.setUserDetails(AppConstant.USER_DETAILS, modelLogin);
                            binding.switchOnOff.setOn(false);
                        }
                    } else {
                    }

                } catch (Exception e) {
                    Toast.makeText(mContext, "Exception = " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Exception", "Exception = " + e.getMessage());
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
                Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("onFailure", "onFailure = " + t.getMessage());
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(JobStatusReceiver);
    }


    // Trigger new location updates at interval
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

        SettingsClient settingsClient = LocationServices.getSettingsClient(TaxiHomeAct.this);
        settingsClient.checkLocationSettings(locationSettingsRequest);

        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(TaxiHomeAct.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(TaxiHomeAct.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        getFusedLocationProviderClient(TaxiHomeAct.this)
                .requestLocationUpdates(mLocationRequest,new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if(locationResult != null) {
                    Log.e("hdasfkjhksdf", "StartLocationUpdate = " + locationResult.getLastLocation());
                    currentLocation = locationResult.getLastLocation();
                    showMarkerCurrentLocation(new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude()));
                }
            }
        }, Looper.myLooper());
    }

    private void showMarkerCurrentLocation(@NonNull LatLng currentLocation) {
        if (currentLocation != null) {
            if (currentLocationMarker == null) {
                if(mMap != null) {
                    currentLocationMarker = mMap.addMarker(new MarkerOptions().position(currentLocation).title("My Location")
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.car_top)));
                    animateCamera(currentLocation);
                }
            } else {
                Log.e("sdfdsfdsfds","Hello Marker Anuimation");
                MarkerAnimation.animateMarkerToGB(currentLocationMarker, currentLocation, new LatLngInterpolator.Spherical());
            }
        }
    }

    private void animateCamera(@NonNull LatLng location) {
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(getCameraPositionWithBearing(location)));
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        ProjectUtil.showSnack(this, findViewById(R.id.drawerLayout), isConnected);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    @NonNull
    private CameraPosition getCameraPositionWithBearing(LatLng latLng) {
        return new CameraPosition.Builder().target(latLng).zoom(16).build();
    }

    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        startLocationUpdates();

        binding.childNavDrawer.tvUsername.setText(modelLogin.getResult().getName());
        binding.childNavDrawer.tvEmail.setText(modelLogin.getResult().getEmail());

        binding.tvName.setText(modelLogin.getResult().getName());
        binding.tvVehicle.setText(modelLogin.getResult().getVehicle_number());

        Glide.with(mContext)
                .load(modelLogin.getResult().getImage())
                .into(binding.cvImg);

        MusicManager.getInstance().initalizeMediaPlayer(TaxiHomeAct.this, Uri.parse
                (ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getPackageName() + "/" + R.raw.doogee_ringtone));
        MusicManager.getInstance().stopPlaying();
        registerReceiver(JobStatusReceiver, new IntentFilter("Job_Status_Action_Taxi"));
        ContextCompat.startForegroundService(getApplicationContext(),new Intent(getApplicationContext(), MyService.class));
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                setCurrentLoc();
            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }

        registerReceiver(receiver,MyApplication.intentFilter);

        if (NetworkReceiver.isConnected()) {
            ProjectUtil.showSnack(this, findViewById(R.id.drawerLayout), true);
        } else {
            ProjectUtil.showSnack(this, findViewById(R.id.drawerLayout), false);
        }
        GetCurrentBooking();
        GetProfile();
    }

    private void GetProfile() {
        HashMap<String, String> param = new HashMap<>();
        param.put("user_id", modelLogin.getResult().getId());
        param.put("type", AppConstant.TAXI_DRIVER);
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.getCurrentTaxiBooking(param);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String stringResponse = response.body().string();
                    JSONObject object=new JSONObject(stringResponse);
                    if (object.getString("status").equals("1")) {
                        Type listType = new TypeToken<ModelCurrentBooking>(){}.getType();
                        ModelCurrentBooking data = new GsonBuilder().create().fromJson(stringResponse, listType);
                        if (data.getStatus().equals("1")) {
                            ModelCurrentBookingResult result = data.getResult().get(0);
                            Log.e("BookingStatus","Result Status =  " + result.getStatus());
                            if (result.getStatus().equalsIgnoreCase("Accept")) {
                                Intent k = new Intent(mContext, TrackTaxiAct.class);
                                k.putExtra("data",data);
                                startActivity(k);
                            } else if (result.getStatus().equalsIgnoreCase("Arrived")) {
                                Intent j = new Intent(mContext, TrackTaxiAct.class);
                                j.putExtra("data",data);
                                startActivity(j);
                            } else if (result.getStatus().equalsIgnoreCase("Start")) {
                                Intent j = new Intent(mContext, TrackTaxiAct.class);
                                j.putExtra("data",data);
                                startActivity(j);
                            } else if (result.getStatus().equalsIgnoreCase("End")) {
//                                Intent j = new Intent(mContext, PaymentSummary.class);
//                                j.putExtra("data",data);
//                                startActivity(j);
                            }
                            sharedPref.setlanguage(AppConstant.LAST,result.getStatus());
                        }
                    }
                } catch (Exception e) {}
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_ID
        );
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setCurrentLoc();
            }
        }
    }

    private void setCurrentLoc() {
        // gpsTracker = new GPSTracker(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
//        mMap.clear();
//        carMarker= new MarkerOptions().position(new LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude())).title("My Location")
//                .icon(BitmapDescriptorFactory.fromResource(R.drawable.car_top));
//        mMap.addMarker(carMarker);
//        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(getCameraPositionWithBearing(new LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude()))));
    }

    @Override
    public void onRequestAccept() {
        GetCurrentBooking();
    }

    @Override
    public void onRequestCancel() {
        DialogMessage.get(this).setMessage(getString(R.string.request_cancel_by_user)).show();
    }

    @Override
    public void bookingApiCalled() {
        GetCurrentBooking();
    }

}