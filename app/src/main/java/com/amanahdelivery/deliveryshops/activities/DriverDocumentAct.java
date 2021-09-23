package com.amanahdelivery.deliveryshops.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.amanahdelivery.R;
import com.amanahdelivery.databinding.ActivityDriverDocumentBinding;
import com.amanahdelivery.models.ModelLogin;
import com.amanahdelivery.taxi.activities.TaxiHomeAct;
import com.amanahdelivery.utils.AppConstant;
import com.amanahdelivery.utils.Compress;
import com.amanahdelivery.utils.ProjectUtil;
import com.amanahdelivery.utils.SharedPref;
import com.amanahdelivery.utils.retrofitutils.Api;
import com.amanahdelivery.utils.retrofitutils.ApiFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.File;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_driver_document);
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);
        itit();
    }

    private void itit() {

        binding.ivDriverLisenceImg.setOnClickListener(v -> {
            Log.e("ImageCapture", "ivDriverLisenceImg");
            if (checkPermissions()) {
                imageCapturedCode = 0;
                Log.e("ImageCapture", "imageCapturedCode = " + imageCapturedCode);
                showPictureDialog();
            } else {
                Log.e("ImageCapture", "requestPermissions");
                requestPermissions();
            }
        });

        binding.ivletterImg.setOnClickListener(v -> {
            if (checkPermissions()) {
                imageCapturedCode = 1;
                Log.e("ImageCapture", "imageCapturedCode = " + imageCapturedCode);
                showPictureDialog();
            } else {
                requestPermissions();
            }
        });

        binding.ivIdentificationImg.setOnClickListener(v -> {
            if (checkPermissions()) {
                imageCapturedCode = 2;
                Log.e("ImageCapture", "imageCapturedCode = " + imageCapturedCode);
                showPictureDialog();
            } else {
                requestPermissions();
            }
        });

        binding.ivProfile.setOnClickListener(v -> {
            if (checkPermissions()) {
                imageCapturedCode = 3;
                Log.e("ImageCapture", "imageCapturedCode = " + imageCapturedCode);
                showPictureDialog();
            } else {
                requestPermissions();
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
        Call<ResponseBody> call = api.addDriverDocumentApiCall(user_id,lisenceFilePart,responsibleFilePart,identityFilePart, profileFilePart);
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
                        if(AppConstant.DEV_FOOD.equals(modelLogin.getResult().getType())){
                            startActivity(new Intent(mContext, ShopOrderHomeAct.class));
                            finish();
                        } else if(AppConstant.TAXI_DRIVER.equals(modelLogin.getResult().getType())){
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
            Compress.get(mContext).setQuality(70).execute(new Compress.onSuccessListener() {
                @Override
                public void response(boolean status, String message, File file) {
                    lisenceFile = file;
                    binding.ivDriverLisenceImg.setImageURI(Uri.parse(file.getPath()));
                }
            }).CompressedImage(file.getPath());
        } else if (imageCapturedCode == 1) {
            responsibleFile = file;
            Compress.get(mContext).setQuality(70).execute(new Compress.onSuccessListener() {
                @Override
                public void response(boolean status, String message, File file) {
                    responsibleFile = file;
                    binding.ivletterImg.setImageURI(Uri.parse(file.getPath()));
                }
            }).CompressedImage(file.getPath());
        } else if (imageCapturedCode == 2) {
            identityFile = file;
            Compress.get(mContext).setQuality(70).execute(new Compress.onSuccessListener() {
                @Override
                public void response(boolean status, String message, File file) {
                    identityFile = file;
                    binding.ivIdentificationImg.setImageURI(Uri.parse(file.getPath()));
                }
            }).CompressedImage(file.getPath());
        } else if (imageCapturedCode == 3) {
            profileFile = file;
            Compress.get(mContext).setQuality(70).execute(new Compress.onSuccessListener() {
                @Override
                public void response(boolean status, String message, File file) {
                    profileFile = file;
                    binding.ivProfile.setImageURI(Uri.parse(file.getPath()));
                }
            }).CompressedImage(file.getPath());
        }
    }

    private void showPictureDialog() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(mContext);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {"Select photo from gallery", "Capture photo from camera"};
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult(galleryIntent, GALLERY);
                                break;
                            case 1:
                                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                startActivityForResult(intent, CAMERA);
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) ==
                        PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                        PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                }, PERMISSION_ID);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    Uri contentURI = data.getData();
                    try {
                        String path = ProjectUtil.getRealPathFromURI(mContext, contentURI);
                        setImageFromCameraGallery(new File(path));
                    } catch (Exception e) {
                        Log.e("hjagksads", "image = " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
        } else if (requestCode == CAMERA) {
            if (resultCode == RESULT_OK) {
                Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                String path = ProjectUtil.getRealPathFromURI(mContext, ProjectUtil.getImageUri(mContext, thumbnail));
                setImageFromCameraGallery(new File(path));
            }
        }
    }

}

