package com.amanahdelivery.deliveryshops.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.Toast;

import com.amanahdelivery.R;
import com.amanahdelivery.databinding.ActivityShopOrderHomeBinding;
import com.amanahdelivery.databinding.CompAccountDialogBinding;
import com.amanahdelivery.databinding.SupportDialogBinding;
import com.amanahdelivery.deliveryshops.adapters.AdapterStoreOrders;
import com.amanahdelivery.deliveryshops.adapters.AdapterSupports;
import com.amanahdelivery.dialogs.RequestDialogFoodDev;
import com.amanahdelivery.models.ModelLogin;
import com.amanahdelivery.models.ModelStoreOrders;
import com.amanahdelivery.models.ModelSupport;
import com.amanahdelivery.utils.AppConstant;
import com.amanahdelivery.utils.MusicManager;
import com.amanahdelivery.utils.MyService;
import com.amanahdelivery.utils.ProjectUtil;
import com.amanahdelivery.utils.SharedPref;
import com.amanahdelivery.utils.retrofitutils.Api;
import com.amanahdelivery.utils.retrofitutils.ApiFactory;
import com.github.angads25.toggle.LabeledSwitch;
import com.github.angads25.toggle.interfaces.OnToggledListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShopOrderHomeAct extends AppCompatActivity {

    Context mContext = ShopOrderHomeAct.this;
    ActivityShopOrderHomeBinding binding;
    SharedPref sharedPref;
    ModelLogin modelLogin;
    int position = 0;
    public static String statusses = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_shop_order_home);
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);

        Log.e("dsadsadsd","object1 = " + getIntent().getStringExtra("object"));
        Log.e("dsadsadsd","getOnline_status = " + modelLogin.getResult().getOnline_status());

        if(getIntent().getStringExtra("object") != null) {
            RequestDialogFoodDev.getInstance().Request(ShopOrderHomeAct.this,getIntent().getStringExtra("object"));
            MusicManager.getInstance().initalizeMediaPlayer(ShopOrderHomeAct.this, Uri.parse
                    (ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getPackageName() + "/" + R.raw.doogee_ringtone));
            MusicManager.getInstance().stopPlaying();
        }

        itti();

    }

    private void onlinOfflineApi(String status) {
        ProjectUtil.showProgressDialog(mContext,true,getString(R.string.please_wait));
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        HashMap<String,String> param = new HashMap<>();
        param.put("user_id",modelLogin.getResult().getId());
        param.put("status",status);
        Call<ResponseBody> call = api.updateOnOffApi(param);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                Log.e("xjgxkjdgvxsd","response = " + response);
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    if(jsonObject.getString("status").equals("1")) {
                        if(status.equals("ONLINE")) {
                            modelLogin.getResult().setOnline_status("ONLINE");
                            sharedPref.setUserDetails(AppConstant.USER_DETAILS,modelLogin);
                            binding.switch4.setOn(true);
                        } else {
                            modelLogin.getResult().setOnline_status("OFFLINE");
                            sharedPref.setUserDetails(AppConstant.USER_DETAILS,modelLogin);
                            binding.switch4.setOn(false);
                        }
                    } else {

                    }

                } catch (Exception e) {
                    Toast.makeText(mContext, "Exception = " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Exception","Exception = " + e.getMessage());
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
                Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("onFailure","onFailure = " + t.getMessage());
            }
        });
    }

    BroadcastReceiver JobStatusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getStringExtra("object") != null) {
                JSONObject object = null;
                try {
                    object = new JSONObject(intent.getStringExtra("object"));
                    RequestDialogFoodDev.getInstance().Request(ShopOrderHomeAct.this,intent.getStringExtra("object"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
//        int backgroundLocationPermissionApproved = ActivityCompat.checkSelfPermission(this,
//                        Manifest.permission.ACCESS_BACKGROUND_LOCATION);
     //   Log.e("dsadsadsd","backgroundLocationPermissionApproved = " + backgroundLocationPermissionApproved);
//        if(backgroundLocationPermissionApproved != 0) {
//            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
//        }

        Log.e("fdsasdasdad","Status from Track screen = " + statusses);
        if(statusses.equals("")) {
            getOrderWithoutDialog("Accept");
            binding.tabLayout.getTabAt(0).select();
        } else if(statusses.equals("Pickup")) {
            getOrderWithoutDialog("Pickup");
            binding.tabLayout.getTabAt(1).select();
        } else if(statusses.equals("Delivered")) {
            binding.tabLayout.getTabAt(2).select();
            getOrderWithoutDialog("Delivered");
        }
        MusicManager.getInstance().initalizeMediaPlayer(ShopOrderHomeAct.this, Uri.parse
                (ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getPackageName() + "/" + R.raw.doogee_ringtone));
        MusicManager.getInstance().stopPlaying();
        registerReceiver(JobStatusReceiver, new IntentFilter("Job_Status_Action"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(JobStatusReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        statusses = "";
    }

    private void itti() {

//      TabLayout.Tab pendingTab = binding.tabLayout.newTab();
//      pendingTab.setText("Pending");
//      binding.tabLayout.addTab(pendingTab);

        if("ONLINE".equals(modelLogin.getResult().getOnline_status())) {
            binding.switch4.setOn(true);
        } else if("OFFLINE".equals(modelLogin.getResult().getOnline_status())) {
            binding.switch4.setOn(false);
        } else {
            binding.switch4.setOn(false);
        }

        binding.switch4.setOnToggledListener(new OnToggledListener() {
            @Override
            public void onSwitched(LabeledSwitch labeledSwitch, boolean isOn) {
                if(isOn) {
                    onlinOfflineApi("ONLINE");
                } else {
                    onlinOfflineApi("OFFLINE");
                }
            }
        });

        getMyOrders("Accept");

        binding.swipLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(position == 0) {
                    getMyOrders("Accept");
                } else if(position == 1) {
                    getMyOrders("Pickup");
                } else if(position == 2) {
                    getMyOrders("Delivered");
                }
            }
        });

        TabLayout.Tab acceptTab = binding.tabLayout.newTab();
        acceptTab.setText("Accept");
        binding.tabLayout.addTab(acceptTab);

        TabLayout.Tab pickTab = binding.tabLayout.newTab();
        pickTab.setText("PickUp");
        binding.tabLayout.addTab(pickTab);

//        TabLayout.Tab onthewayTab = binding.tabLayout.newTab();
//        onthewayTab.setText("On The Way");
//        binding.tabLayout.addTab(onthewayTab);

        TabLayout.Tab deliveredTab = binding.tabLayout.newTab();
        deliveredTab.setText("Delivered");
        binding.tabLayout.addTab(deliveredTab);

        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab.getPosition() == 0) {
                    position = 0;
                   getMyOrders("Accept");
                } else if(tab.getPosition() == 1) {
                    position = 1;
                    getMyOrders("Pickup");
                } else if(tab.getPosition() == 2) {
                    position = 2;
                    getMyOrders("Delivered");
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        binding.childNavDrawer.tvUsername.setText(modelLogin.getResult().getUser_name());
        binding.childNavDrawer.tvEmail.setText(modelLogin.getResult().getEmail());

        binding.ivMenu.setOnClickListener(v -> {
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

        binding.childNavDrawer.btnOrders.setOnClickListener(v -> {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        });

        binding.childNavDrawer.btnChangePass.setOnClickListener(v -> {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            startActivity(new Intent(mContext,ChnagePassAct.class));
        });

        binding.childNavDrawer.tvLogout.setOnClickListener(v -> {
            logoutAppDialog();
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        });

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
                        Toast.makeText(ShopOrderHomeAct.this, "Support is not available at this time", Toast.LENGTH_SHORT).show();
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

    private void getOrderWithoutDialog(String status){
        HashMap<String,String> paramHash = new HashMap<>();
        paramHash.put("driver_id",modelLogin.getResult().getId());
        paramHash.put("status",status);

        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.getDevOrdersApiCall(paramHash);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                binding.swipLayout.setRefreshing(false);
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    Log.e("responseString","responseString = " + responseString);

                    if(jsonObject.getString("status").equals("1")) {
                        ModelStoreOrders modelStoreOrders = new Gson().fromJson(responseString,ModelStoreOrders.class);
                        AdapterStoreOrders adapterStoreOrders = new AdapterStoreOrders(mContext,modelStoreOrders.getResult());
                        binding.rvOrders.setAdapter(adapterStoreOrders);
                    } else {
                        AdapterStoreOrders adapterStoreOrders = new AdapterStoreOrders(mContext,null);
                        binding.rvOrders.setAdapter(adapterStoreOrders);
                        // Toast.makeText(ShopOrderHomeAct.this, getString(R.string.no_data_found), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    // Toast.makeText(mContext, "Exception = " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Exception","Exception = " + e.getMessage());
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
                binding.swipLayout.setRefreshing(false);
            }

        });

    }

    private void getMyOrders(String status) {
        ProjectUtil.showProgressDialog(mContext,true,getString(R.string.please_wait));

        HashMap<String,String> paramHash = new HashMap<>();
        paramHash.put("driver_id",modelLogin.getResult().getId());
        paramHash.put("status",status);

        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.getDevOrdersApiCall(paramHash);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                binding.swipLayout.setRefreshing(false);
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    Log.e("responseString","responseString = " + responseString);

                    if(jsonObject.getString("status").equals("1")) {
                        ModelStoreOrders modelStoreOrders = new Gson().fromJson(responseString,ModelStoreOrders.class);
                        AdapterStoreOrders adapterStoreOrders = new AdapterStoreOrders(mContext,modelStoreOrders.getResult());
                        binding.rvOrders.setAdapter(adapterStoreOrders);
                    } else {
                        AdapterStoreOrders adapterStoreOrders = new AdapterStoreOrders(mContext,null);
                        binding.rvOrders.setAdapter(adapterStoreOrders);
                       // Toast.makeText(ShopOrderHomeAct.this, getString(R.string.no_data_found), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                   // Toast.makeText(mContext, "Exception = " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Exception","Exception = " + e.getMessage());
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
                binding.swipLayout.setRefreshing(false);
            }

        });

    }

    public void callApiAgain() {
        getMyOrders("Accept");
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

    private void logoutAppDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(getString(R.string.logout_text))
                .setCancelable(false)
                .setPositiveButton(mContext.getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sharedPref.clearAllPreferences();
                        Intent loginscreen = new Intent(mContext,LoginAct.class);
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


}