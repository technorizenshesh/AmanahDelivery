package com.amanahdelivery.deliveryshops.activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.amanahdelivery.R;
import com.amanahdelivery.models.ModelLogin;
import com.amanahdelivery.taxi.activities.TaxiHomeAct;
import com.amanahdelivery.utils.AppConstant;
import com.amanahdelivery.utils.MyService;
import com.amanahdelivery.utils.ProjectUtil;
import com.amanahdelivery.utils.SharedPref;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class SplashAct extends AppCompatActivity {

    private static final int REQUEST_LOCATION_PERMISSION = 1234;
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

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onResume() {
        Log.e("versionosos", "version code = " + android.os.Build.VERSION.SDK_INT);
        requestLocationPermission();
        super.onResume();
    }

    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                /*ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED &&*/
                ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_MEDIA_LOCATION) == PackageManager.PERMISSION_GRANTED &&
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

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void requestPermissions() {
        requestLocationPermission();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @AfterPermissionGranted(REQUEST_LOCATION_PERMISSION)
    public void requestLocationPermission() {

        String[] perms = {Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                /* Manifest.permission.ACCESS_BACKGROUND_LOCATION */};

        if (EasyPermissions.hasPermissions(this, perms)) {
            processNextActivity();
            Toast.makeText(this, "All Permission granted", Toast.LENGTH_SHORT).show();
        } else {
            EasyPermissions.requestPermissions(this, "Please allow location permission All the time", REQUEST_LOCATION_PERMISSION, perms);
        }

    }

    //    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == PERMISSION_ID) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                processNextActivity();
//            }
//        }
//    }

    private void processNextActivity() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (sharedPref.getBooleanValue(AppConstant.IS_REGISTER)) {
                    modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);
                    if ("en".equals(sharedPref.getLanguage("lan"))) {
                        ProjectUtil.updateResources(mContext, "en");
                    } else if ("so".equals(sharedPref.getLanguage("lan"))) {
                        ProjectUtil.updateResources(mContext, "so");
                    } else {
                        sharedPref.setlanguage("lan", "so");
                        ProjectUtil.updateResources(mContext, "so");
                    }
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
                        if ("en".equals(sharedPref.getLanguage("lan"))) {
                            ProjectUtil.updateResources(mContext, "en");
                        } else if ("so".equals(sharedPref.getLanguage("lan"))) {
                            ProjectUtil.updateResources(mContext, "so");
                        } else {
                            sharedPref.setlanguage("lan", "so");
                            ProjectUtil.updateResources(mContext, "so");
                        }
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
                    if ("en".equals(sharedPref.getLanguage("lan"))) {
                        ProjectUtil.updateResources(mContext, "en");
                    } else if ("so".equals(sharedPref.getLanguage("lan"))) {
                        ProjectUtil.updateResources(mContext, "so");
                    } else {
                        sharedPref.setlanguage("lan", "so");
                        ProjectUtil.updateResources(mContext, "so");
                    }
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
        builder.setMessage("Click on Ok than -> \n\n Choose Location Permission ->" +
                "\n Allow all the time \n\n Than come back to Application");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                isDialogEnable = true;
                Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }).create().show();
    }

}
