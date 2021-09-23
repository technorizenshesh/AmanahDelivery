package com.amanahdelivery.deliveryshops.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.amanahdelivery.R;
import com.amanahdelivery.models.ModelLogin;
import com.amanahdelivery.taxi.activities.TaxiHomeAct;
import com.amanahdelivery.utils.AppConstant;
import com.amanahdelivery.utils.MyService;
import com.amanahdelivery.utils.SharedPref;

public class SplashAct extends AppCompatActivity {

    Context mContext = SplashAct.this;
    SharedPref sharedPref;
    ModelLogin modelLogin;
    int PERMISSION_ID = 44;
    boolean isDialogEnable = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        sharedPref = SharedPref.getInstance(mContext);
    }

    @Override
    protected void onResume() {

        Log.e("versionosos","version code = " + android.os.Build.VERSION.SDK_INT);

        if (android.os.Build.VERSION.SDK_INT >= 29) {
            int backgroundLocationPermissionApproved = ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION);
            if (backgroundLocationPermissionApproved != 0) {
                if(isDialogEnable) {
                    isDialogEnable = false;
                    showLocationDialog();
                }
            } else {
                if (checkPermissions()) {
                    if (isLocationEnabled()) {
                        processNextActivity();
                    } else {
                        Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    }
                } else {
                    requestPermissions();
                }
            }
        } else {
            if (checkPermissions()) {
                if (isLocationEnabled()) {
                    processNextActivity();
                } else {
                    Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            } else {
                requestPermissions();
            }
        }
        super.onResume();
    }

    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE},
                PERMISSION_ID
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                processNextActivity();
            }
        }
    }

    private void processNextActivity() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (sharedPref.getBooleanValue(AppConstant.IS_REGISTER)) {
                    modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);
                    ContextCompat.startForegroundService(SplashAct.this, new Intent(SplashAct.this, MyService.class));
                    if (AppConstant.DEV_FOOD.equalsIgnoreCase(modelLogin.getResult().getType())) {
                        Log.e("adfasdfss", "getDriver_lisence_img = " + modelLogin.getResult().getDriver_lisence_img());
                        if (modelLogin.getResult().getDriver_lisence_img() == null ||
                                "".equals(modelLogin.getResult().getDriver_lisence_img())) {
                            startActivity(new Intent(mContext, DriverDocumentAct.class));
                            finish();
                        } else {
                            startActivity(new Intent(mContext, ShopOrderHomeAct.class));
                            finish();
                        }
                    } else if (AppConstant.TAXI_DRIVER.equalsIgnoreCase(modelLogin.getResult().getType())) {
                        if (modelLogin.getResult().getDriver_lisence_img() == null ||
                                "".equals(modelLogin.getResult().getDriver_lisence_img())) {
                            startActivity(new Intent(mContext, DriverDocumentAct.class));
                            finish();
                        } else {
                            startActivity(new Intent(mContext, TaxiHomeAct.class));
                            finish();
                        }
                    }
                } else {
                    Intent i = new Intent(SplashAct.this, LoginAct.class);
                    startActivity(i);
                    finish();
                }
            }
        }, 3000);
    }

    private void showLocationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setCancelable(false);
        builder.setTitle("Need Important Permission");
        builder.setMessage("Click on Ok than -> \n\n Choose AmanahDelivery ->" +
                "\n Permissions ->\n Location ->\n Allow all the time \n\n Than come back to Application");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                isDialogEnable = true;
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        }).create().show();
    }


}