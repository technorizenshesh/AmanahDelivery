package com.amanahdelivery.deliveryshops.activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amanahdelivery.R;
import com.amanahdelivery.databinding.ActivityPinLocationBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;

import java.util.List;
import java.util.Locale;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class PinLocationActivity extends AppCompatActivity implements LocationListener {

    private static final int AUTOCOMPLETE_REQUEST_CODE = 101;
    private ActivityPinLocationBinding binding;
    private LocationManager locationManager;
    private Location location;
    private GoogleMap mMap;
    private Integer THRESHOLD = 2;
    double lat = 0, lng = 0;
    private Animation myAnim;
    ImageView ivBack;
    TextView tvDone;
    private MenuItem mItem;
    Context mContext = PinLocationActivity.this;
    String type = "", id = "";
    private int count;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_pin_location);

        try {
            type = getIntent().getStringExtra("type");
        } catch (Exception e) {
        }

        ivBack = findViewById(R.id.ivBack);
        tvDone = findViewById(R.id.tvDone);

        initLocation();
        bindMap();

        init();

        ivBack.setOnClickListener(v -> {
            finish();
        });

        tvDone.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(binding.tvAddress.getText().toString().trim())) {
                if (lat != 0.0) {
                    Intent intent = new Intent();
                    intent.putExtra("add", binding.tvAddress.getText().toString().trim());
                    intent.putExtra("lat", lat);
                    intent.putExtra("lon", lng);
                    setResult(222, intent);
                    finish();
                }
            } else {
                Toast.makeText(mContext, getString(R.string.please_select_address), Toast.LENGTH_SHORT).show();
            }

        });

    }

    private void init() {
        binding.tvAddress.requestFocus();
        binding.tvAddress.setOnClickListener(v -> {
//            List<Place.Field> fields = Arrays.asList (
//                    Place.Field.ID,
//                    Place.Field.NAME,
//                    Place.Field.LAT_LNG,
//                    Place.Field.ADDRESS,Place.Field.ADDRESS_COMPONENTS);
//            Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
//                    .build(this);
//            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initLocation() {
        myAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (checkSelfPermission(ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            String[] arr = {ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION};
            requestPermissions(arr, 100);
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        onLocationChanged(location);
        binding.tvAddress.setText(getCompleteAddressString(PinLocationActivity.this, lat, lng));
    }

    private void bindMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.frg);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap map) {
                mMap = map;
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                if (location != null) {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(),
                            location.getLongitude()), 18.0f));
                }
                mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
                    @Override
                    public void onCameraIdle() {
                        lat = mMap.getCameraPosition().target.latitude;
                        lng = mMap.getCameraPosition().target.longitude;
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 18.0f));
                        binding.tvAddress.setText(getCompleteAddressString(PinLocationActivity.this, lat, lng));
                        binding.imgMarker.startAnimation(myAnim);
                    }
                });
            }
        });
    }

    public static String getCompleteAddressString(Context context, double LATITUDE, double LONGITUDE) {
        String strAdd = "getting address...";
        if (context != null) {
            Geocoder geocoder = new Geocoder(context.getApplicationContext(), Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
                if (addresses != null) {
                    Address returnedAddress = addresses.get(0);
                    StringBuilder strReturnedAddress = new StringBuilder("");
                    for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                        strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                    }
                    strAdd = strReturnedAddress.toString();
                    Log.w("My Current address", strReturnedAddress.toString());
                } else {
                    strAdd = "No Address Found";
                    Log.w("My Current address", "No Address returned!");
                }
            } catch (Exception e) {
                e.printStackTrace();
                strAdd = "Cant get Address";
                Log.w("My Current address", "Canont get Address!");
            }
        }
        return strAdd;
    }

    @Override
    public void onLocationChanged(Location loc) {
        location = loc;
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                lat = place.getLatLng().latitude;
                lng = place.getLatLng().longitude;

                Log.e("adasdas", "place.getAddress() = " + place.getAddress());
                Log.e("adasdas", "place.getAddressComponents() = " + place.getAddressComponents());

                binding.tvAddress.setText(place.getAddress()/*getCompleteAddressString(mContext,lat,lng)*/);
                try {
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    if (lat != 0) {
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 18.0f));
                    }
                } catch (Exception e) {
                }
            }

        }

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        mItem = item;
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
    }


}