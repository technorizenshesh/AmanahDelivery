package com.amanahdelivery.taxi.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.amanahdelivery.R;
import com.amanahdelivery.databinding.ActivityPaymentTaxiBinding;
import com.amanahdelivery.models.ModelLogin;
import com.amanahdelivery.taxi.dialogs.DialogMessage;
import com.amanahdelivery.taxi.models.ModelCurrentBooking;
import com.amanahdelivery.taxi.models.ModelCurrentBookingResult;
import com.amanahdelivery.taxi.models.ModelTaxiPayment;
import com.amanahdelivery.utils.AppConstant;
import com.amanahdelivery.utils.ProjectUtil;
import com.amanahdelivery.utils.SharedPref;
import com.amanahdelivery.utils.retrofitutils.Api;
import com.amanahdelivery.utils.retrofitutils.ApiFactory;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.TimeZone;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentTaxiAct extends AppCompatActivity {

    Context mContext = PaymentTaxiAct.this;
    ActivityPaymentTaxiBinding binding;
    private ModelCurrentBooking data;
    ModelCurrentBookingResult bookingDetail;
    private ModelTaxiPayment result;
    String type = "", resquestId = "";
    SharedPref sharedPref;
    ModelLogin modelLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_payment_taxi);
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);

        resquestId = getIntent().getStringExtra("id");
        type = getIntent().getStringExtra("type");

        try {
            if (getIntent() != null) {

                if ("home".equals(type)) {
                    data = (ModelCurrentBooking) getIntent().getSerializableExtra("data");
                    bookingDetail = data.getResult().get(0);
                    Log.e("resultresult", "Result bookingDetail = " + new Gson().toJson(bookingDetail));
                } else {
                    result = (ModelTaxiPayment) getIntent().getSerializableExtra("data");
                    Log.e("resultresult", "Result = Payment" + new Gson().toJson(result));
                }

            }
        } catch (Exception e) {
        }

        itit();

    }

    private void itit() {

        binding.btnCollectPay.setOnClickListener(v -> {
            paymentConiformDialog();
        });

        if ("home".equals(type)) {
            Log.e("bookingDetail", "pick = " + bookingDetail.getPicuplocation());
            Log.e("bookingDetail", "drop = " + bookingDetail.getDropofflocation());
            Log.e("bookingDetail", "pay = " + bookingDetail.getSharerideType());
            Log.e("bookingDetail", "amount = " + bookingDetail.getAmount());
            Log.e("bookingDetail", "amount = " + bookingDetail.getDistance());
            Log.e("bookingDetail", "waiting = " + bookingDetail.getWaiting_time());
            Log.e("bookingDetail", "distance = " + bookingDetail.getAmount());
            binding.tvPickUpLoc.setText(bookingDetail.getPicuplocation());
            binding.tvDropUpLoc.setText(bookingDetail.getDropofflocation());
            binding.tvPayType.setText(bookingDetail.getSharerideType());
            binding.tvTotalPay.setText("Eirr " + bookingDetail.getAmount());
            binding.tvDistance.setText(bookingDetail.getDistance() + " Km");
            binding.tvWaiting.setText(bookingDetail.getWaiting_time() + " Minute");
        } else {
            Log.e("bookingDetail", "tvWaiting = " + result.getResult().getWaiting_time());
            binding.tvPickUpLoc.setText(result.getResult().getPicuplocation());
            binding.tvDropUpLoc.setText(result.getResult().getDropofflocation());
            binding.tvPayType.setText(result.getResult().getShareride_type());
            binding.tvDistance.setText(result.getResult().getDistance() + " Km");
            binding.tvWaiting.setText(result.getResult().getWaiting_time() + " Minute");
            binding.tvTotalPay.setText("Eirr " + result.getResult().getAmount());
        }

    }

    private void paymentConiformDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage("Are you sure you get the payment from customer ?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                DriverChangeStatus("Finish");
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create().show();
    }

    private void DriverChangeStatus(String status) {

        HashMap<String, String> params = new HashMap<>();
        params.put("request_id", resquestId);
        params.put("status", status);
        params.put("cancel_reason", "");
        params.put("timezone", TimeZone.getDefault().getID());
        params.put("driver_id", modelLogin.getResult().getId());

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
                        finish();
                        startActivity(new Intent(mContext, TaxiHomeAct.class));
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

}