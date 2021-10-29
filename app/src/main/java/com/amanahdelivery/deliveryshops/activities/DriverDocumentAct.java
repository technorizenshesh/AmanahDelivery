package com.amanahdelivery.deliveryshops.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.amanahdelivery.BuildConfig;
import com.amanahdelivery.R;
import com.amanahdelivery.databinding.ActivityDriverDocumentBinding;
import com.amanahdelivery.models.ModelLogin;
import com.amanahdelivery.taxi.activities.TaxiHomeAct;
import com.amanahdelivery.utils.AppConstant;
import com.amanahdelivery.utils.Compress;
import com.amanahdelivery.utils.ProjectUtil;
import com.amanahdelivery.utils.SharedPref;
import com.amanahdelivery.utils.keshavsirpck.DataManager;
import com.amanahdelivery.utils.retrofitutils.Api;
import com.amanahdelivery.utils.retrofitutils.ApiFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.File;
import java.util.Calendar;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DriverDocumentAct extends AppCompatActivity {

    private static final int PERMISSION_ID = 101;
    Context mContext = DriverDocumentAct.this;
    private final int GALLERY = 0, CAMERA = 1;
    ActivityDriverDocumentBinding binding;
    int imageCapturedCode;
    File lisenceFile, responsibleFile, identityFile, profileFile;
    SharedPref sharedPref;
    ModelLogin modelLogin;
    private Uri uriSavedImage;
    String str_image_path = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_driver_document);
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);
        itit();
    }

    public void getStringPath(String strPath) {
        str_image_path = strPath;
    }

    private void itit() {

        binding.ivDriverLisenceImg.setOnClickListener(v -> {
            Log.e("ImageCapture", "ivDriverLisenceImg");
            if (ProjectUtil.checkPermissions(mContext)) {
                imageCapturedCode = 0;
                Log.e("ImageCapture", "imageCapturedCode = " + imageCapturedCode);
                showPictureDialog();
            } else {
                Log.e("ImageCapture", "requestPermissions");
                ProjectUtil.requestPermissions(mContext);
            }
        });

        binding.ivletterImg.setOnClickListener(v -> {
            if (ProjectUtil.checkPermissions(mContext)) {
                imageCapturedCode = 1;
                Log.e("ImageCapture", "imageCapturedCode = " + imageCapturedCode);
                showPictureDialog();
            } else {
                ProjectUtil.requestPermissions(mContext);
            }
        });

        binding.ivIdentificationImg.setOnClickListener(v -> {
            if (ProjectUtil.checkPermissions(mContext)) {
                imageCapturedCode = 2;
                Log.e("ImageCapture", "imageCapturedCode = " + imageCapturedCode);
                showPictureDialog();
            } else {
                ProjectUtil.requestPermissions(mContext);
            }
        });

        binding.ivProfile.setOnClickListener(v -> {
            if (ProjectUtil.checkPermissions(mContext)) {
                imageCapturedCode = 3;
                Log.e("ImageCapture", "imageCapturedCode = " + imageCapturedCode);
                showPictureDialog();
            } else {
                ProjectUtil.requestPermissions(mContext);
            }
        });

        binding.btSubmit.setOnClickListener(v -> {
            if (lisenceFile == null) {
                Toast.makeText(mContext, getString(R.string.driving_licesne_text), Toast.LENGTH_SHORT).show();
            } else if (responsibleFile == null) {
                Toast.makeText(mContext, getString(R.string.upload_res_person_copy), Toast.LENGTH_LONG).show();
            } else if (identityFile == null) {
                Toast.makeText(mContext, getString(R.string.upload_identification), Toast.LENGTH_LONG).show();
            } else if (profileFile == null) {
                Toast.makeText(mContext, getString(R.string.upload_profile_picture), Toast.LENGTH_LONG).show();
            } else {
                addDocumentApiCall();
            }
        });

    }

    public void showPictureDialog() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(mContext);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {"Select photo from gallery", "Capture photo from camera"};
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                ProjectUtil.openGallery(mContext, GALLERY);
                                break;
                            case 1:
                                str_image_path = ProjectUtil.openCamera(mContext, CAMERA);
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    private void addDocumentApiCall() {

        ProjectUtil.showProgressDialog(mContext, false, getString(R.string.please_wait));

        MultipartBody.Part lisenceFilePart;
        MultipartBody.Part responsibleFilePart;
        MultipartBody.Part identityFilePart;
        MultipartBody.Part profileFilePart;

        RequestBody user_id = RequestBody.create(MediaType.parse("text/plain"), modelLogin.getResult().getId());
        lisenceFilePart = MultipartBody.Part.createFormData("driver_lisence_img", lisenceFile.getName(), RequestBody.create(MediaType.parse("car_document/*"), lisenceFile));
        responsibleFilePart = MultipartBody.Part.createFormData("responsible_letter_img", responsibleFile.getName(), RequestBody.create(MediaType.parse("car_document/*"), responsibleFile));
        identityFilePart = MultipartBody.Part.createFormData("identification_img", identityFile.getName(), RequestBody.create(MediaType.parse("car_document/*"), identityFile));
        profileFilePart = MultipartBody.Part.createFormData("image", profileFile.getName(), RequestBody.create(MediaType.parse("car_document/*"), profileFile));

        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.addDriverDocumentApiCall(user_id, lisenceFilePart, responsibleFilePart, identityFilePart, profileFilePart);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    Log.e("documentsdriver", "responseString = " + responseString);

                    if (jsonObject.getString("status").equals("1")) {
                        modelLogin = new Gson().fromJson(responseString, ModelLogin.class);
                        sharedPref.setUserDetails(AppConstant.USER_DETAILS, modelLogin);
                        if (AppConstant.DEV_FOOD.equals(modelLogin.getResult().getType())) {
                            startActivity(new Intent(mContext, ShopOrderHomeAct.class));
                            finish();
                        } else if (AppConstant.TAXI_DRIVER.equals(modelLogin.getResult().getType())) {
                            startActivity(new Intent(mContext, TaxiHomeAct.class));
                            finish();
                        }
                    }

                } catch (Exception e) {
                    Toast.makeText(mContext, "Exception = " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Exception", "Exception = " + e.getMessage());
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
                Log.e("Exception", "Throwable = " + t.getMessage());
            }

        });

    }

    private void setImageFromCameraGallery(File file) {
        if (imageCapturedCode == 0) {
            lisenceFile = file;
            Compress.get(mContext).setQuality(90).execute(new Compress.onSuccessListener() {
                @Override
                public void response(boolean status, String message, File file) {
                    lisenceFile = file;
                    binding.ivDriverLisenceImg.setImageURI(Uri.parse(file.getPath()));
                }
            }).CompressedImage(file.getPath());
        } else if (imageCapturedCode == 1) {
            responsibleFile = file;
            Compress.get(mContext).setQuality(90).execute(new Compress.onSuccessListener() {
                @Override
                public void response(boolean status, String message, File file) {
                    responsibleFile = file;
                    binding.ivletterImg.setImageURI(Uri.parse(file.getPath()));
                }
            }).CompressedImage(file.getPath());
        } else if (imageCapturedCode == 2) {
            identityFile = file;
            Compress.get(mContext).setQuality(90).execute(new Compress.onSuccessListener() {
                @Override
                public void response(boolean status, String message, File file) {
                    identityFile = file;
                    binding.ivIdentificationImg.setImageURI(Uri.parse(file.getPath()));
                }
            }).CompressedImage(file.getPath());
        } else if (imageCapturedCode == 3) {
            profileFile = file;
            Compress.get(mContext).setQuality(90).execute(new Compress.onSuccessListener() {
                @Override
                public void response(boolean status, String message, File file) {
                    profileFile = file;
                    binding.ivProfile.setImageURI(Uri.parse(file.getPath()));
                }
            }).CompressedImage(file.getPath());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY) {
            if (resultCode == RESULT_OK) {
                String path = ProjectUtil.getRealPathFromURI(mContext, data.getData());
                setImageFromCameraGallery(new File(path));
            }
        } else if (requestCode == CAMERA) {
            if (resultCode == RESULT_OK) {
                setImageFromCameraGallery(new File(str_image_path));
            }
        }

    }


}

