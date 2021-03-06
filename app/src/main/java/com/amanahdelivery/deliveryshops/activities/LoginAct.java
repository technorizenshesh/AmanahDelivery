package com.amanahdelivery.deliveryshops.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.amanahdelivery.R;
import com.amanahdelivery.databinding.ActivityLoginBinding;
import com.amanahdelivery.models.ModelLogin;
import com.amanahdelivery.taxi.activities.TaxiHomeAct;
import com.amanahdelivery.utils.AppConstant;
import com.amanahdelivery.utils.InternetConnection;
import com.amanahdelivery.utils.MyService;
import com.amanahdelivery.utils.ProjectUtil;
import com.amanahdelivery.utils.SharedPref;
import com.amanahdelivery.utils.retrofitutils.Api;
import com.amanahdelivery.utils.retrofitutils.ApiFactory;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginAct extends AppCompatActivity {

    ActivityLoginBinding binding;
    Context mContext = LoginAct.this;
    SharedPref sharedPref;
    ModelLogin modelLogin;
    private String registerId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        sharedPref = SharedPref.getInstance(mContext);

        allOnclickListeners();

        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(token -> {
            if (!TextUtils.isEmpty(token)) {
                registerId = token;
                Log.e("tokentoken", "retrieve token successful : " + token);
            } else {
                Log.e("tokentoken", "token should not be null...");
            }
        }).addOnFailureListener(e -> {
        }).addOnCanceledListener(() -> {
        }).addOnCompleteListener(task -> Log.e("tokentoken", "This is the token : " + task.getResult()));

        // getDynamicLinksFromFirebase();

    }

    private void getDynamicLinksFromFirebase() {

        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(@NonNull PendingDynamicLinkData pendingDynamicLinkData) {
                        Log.e("asdasdasds", "PendingDynamicLinkData = we have a dynamic link");
                        Uri deepLink = null;

                        if (pendingDynamicLinkData != null) {
                            deepLink = pendingDynamicLinkData.getLink();
                        }

                        if (deepLink != null) {
                            Log.e("asdasdasds", "deepLink Param = Email -->");
                            String email = deepLink.getQueryParameter("email");
                            String password = deepLink.getQueryParameter("password");
                            Log.e("asdasdasds", "email --> " + email);
                            Log.e("asdasdasds", "password --> " + password);
                            binding.etEmail.setText(email);
                            binding.etPass.setText(password);
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(mContext, "Exception = " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void allOnclickListeners() {

        binding.changeLang.setOnClickListener(v -> {
            changeLangDialog();
        });

        binding.rlLinkSignup.setOnClickListener(v -> {
            Intent i = new Intent(LoginAct.this, SignupAct.class);
            startActivity(i);
        });

        binding.rlForgotPass.setOnClickListener(v -> {
            Intent i = new Intent(LoginAct.this, ForgotPasswordAct.class);
            startActivity(i);
        });

        binding.btnSignin.setOnClickListener(v -> {
            if (TextUtils.isEmpty(binding.etEmail.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.please_enter_email_add), Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(binding.etPass.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.please_enter_pass), Toast.LENGTH_SHORT).show();
            } else {
                if (InternetConnection.checkConnection(mContext)) {
                    loginApiCall();
                } else {
                    Toast.makeText(mContext, getString(R.string.please_check_internet), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void changeLangDialog() {
        Dialog dialog = new Dialog(mContext, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.setContentView(R.layout.change_language_dialog);
        dialog.setCancelable(true);

        Button btContinue = dialog.findViewById(R.id.btContinue);
        RadioButton radioEng = dialog.findViewById(R.id.radioEng);
        RadioButton radioSpanish = dialog.findViewById(R.id.radioSpanish);

        if ("so".equals(sharedPref.getLanguage("lan"))) {
            radioSpanish.setChecked(true);
        } else {
            radioEng.setChecked(true);
        }

        dialog.getWindow().setBackgroundDrawableResource(R.color.translucent_black);

        btContinue.setOnClickListener(v -> {
            if (radioEng.isChecked()) {
                ProjectUtil.updateResources(mContext, "en");
                sharedPref.setlanguage("lan", "en");
                finish();
                startActivity(new Intent(mContext, LoginAct.class));
                dialog.dismiss();
            } else if (radioSpanish.isChecked()) {
                ProjectUtil.updateResources(mContext, "so");
                sharedPref.setlanguage("lan", "so");
                finish();
                startActivity(new Intent(mContext, LoginAct.class));
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    private void loginApiCall() {
        ProjectUtil.showProgressDialog(mContext, false, getString(R.string.please_wait));

        HashMap<String, String> paramHash = new HashMap<>();
        paramHash.put("email", binding.etEmail.getText().toString().trim());
        paramHash.put("password", binding.etPass.getText().toString().trim());
        paramHash.put("lat", "");
        paramHash.put("lon", "");
        paramHash.put("type", "DEV_FOOD");
        paramHash.put("register_id", registerId);

        Log.e("asdfasdfasf", "paramHash = " + paramHash);

        Api api = ApiFactory
                .getClientWithoutHeader(mContext)
                .create(Api.class);
        Call<ResponseBody> call = api.loginApiCall(paramHash);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    Log.e("responseString", "responseString = " + responseString);

                    if (jsonObject.getString("status").equals("1")) {

                        modelLogin = new Gson().fromJson(responseString, ModelLogin.class);

                        sharedPref.setBooleanValue(AppConstant.IS_REGISTER, true);
                        sharedPref.setUserDetails(AppConstant.USER_DETAILS, modelLogin);

                        ContextCompat.startForegroundService(getApplicationContext()
                                , new Intent(getApplicationContext(), MyService.class));

                        if (modelLogin.getResult().getType().equals(AppConstant.USER)) {
                            Toast.makeText(LoginAct.this, getString(R.string.invalid_credentials), Toast.LENGTH_SHORT).show();
                        } else if (AppConstant.DEV_FOOD.equals(modelLogin.getResult().getType())) {
                            if ("".equals(modelLogin.getResult().getDriver_lisence_img())) {
                                startActivity(new Intent(mContext, DriverDocumentAct.class));
                                finish();
                            } else {
                                startActivity(new Intent(mContext, ShopOrderHomeAct.class));
                                finish();
                            }
                        } else if (AppConstant.TAXI_DRIVER.equals(modelLogin.getResult().getType())) {
                            startActivity(new Intent(mContext, TaxiHomeAct.class));
                            finish();
                        }
                    } else {
                        Toast.makeText(LoginAct.this, getString(R.string.invalid_credentials), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(mContext, "Exception = " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Exception", "Exception = " + e.getMessage());
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
            }

        });

    }

}