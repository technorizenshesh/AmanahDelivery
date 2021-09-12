package com.amanahdelivery.dialogs;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.amanahdelivery.Application.MyApplication;
import com.amanahdelivery.R;
import com.amanahdelivery.databinding.DialogNewRequestBinding;
import com.amanahdelivery.deliveryshops.activities.LoginAct;
import com.amanahdelivery.deliveryshops.activities.ShopOrderHomeAct;
import com.amanahdelivery.models.ModelLogin;
import com.amanahdelivery.utils.AppConstant;
import com.amanahdelivery.utils.MusicManager;
import com.amanahdelivery.utils.MyService;
import com.amanahdelivery.utils.ProjectUtil;
import com.amanahdelivery.utils.SharedPref;
import com.amanahdelivery.utils.retrofitutils.Api;
import com.amanahdelivery.utils.retrofitutils.ApiFactory;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RequestDialogFoodDev {

    public static String TAG = "NewRequestDialog";

    private static final RequestDialogFoodDev ourInstance = new RequestDialogFoodDev();

    public static RequestDialogFoodDev getInstance() {
        return ourInstance;
    }

    private RequestDialogFoodDev() {}

    private long timeCountInMilliSeconds = 1 * 60000;

    private enum TimerStatus {STARTED, STOPPED}

    private ProgressBar progressBarCircle;
    private TimerStatus timerStatus = TimerStatus.STOPPED;
    private CountDownTimer countDownTimer;
    private TextView textViewTime;
    SharedPref sharedPref;
    ModelLogin modelLogin;
    Dialog dialog;
    DialogNewRequestBinding binding;
    String driver_id = "", orderId = "";

    public void Request(Context context, String obj) {
        sharedPref = SharedPref.getInstance(context);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);
        JSONObject object;
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.dialog_new_request, null, false);
        dialog.setContentView(binding.getRoot());
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);

        try {
            Log.e("DialogChala====", "=======" + obj);
            object = new JSONObject(obj);
            if (object.get("status").equals("Cancel_by_user")) {
                Log.e("DialogChala====", "====dissssss===" + obj);
                stopCountDownTimer();
                dialog.dismiss();
            } else {
                driver_id = String.valueOf(object.get("driver_id"));
                orderId = String.valueOf(object.get("order_id"));
                binding.tvPickupLoc.setText(object.getString("dev_address"));
                binding.tvDestinationLoc.setText(object.getString("shop_address"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        binding.tvAccept.setOnClickListener(v -> {
            AcceptCancel(context, orderId, "Accept");
        });

        binding.tvRefuse.setOnClickListener(v -> {
            AcceptCancel(context, orderId, "Cancel");
        });

        startStop();

        dialog.show();

    }

    private void AcceptCancel(Context context, String orderId, String status) {
        ProjectUtil.showProgressDialog(context,false, context.getString(R.string.please_wait));

        HashMap<String, String> map = new HashMap<>();
        map.put("order_id", orderId);
        map.put("status", status);
        map.put("driver_id", modelLogin.getResult().getId());

        Log.e("sffdsfdsfsad","param = " + map.toString());

        Api api = ApiFactory.getClientWithoutHeader(context).create(Api.class);
        Call<ResponseBody> call = api.acceptCancelOrderCall(map);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    Log.e("responseString21113", "responseString = " + responseString);

                    if (jsonObject.getString("status").equals("1")) {
                        ProjectUtil.clearNortifications(context);
                        if (status.equals("Accept")) {
                            MusicManager.getInstance().initalizeMediaPlayer(context, Uri.parse
                                    (ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getPackageName() + "/" + R.raw.doogee_ringtone));
                            MusicManager.getInstance().stopPlaying();
                            ((ShopOrderHomeAct)context).callApiAgain();
                            dialog.dismiss();
                        } else {
                            MusicManager.getInstance().initalizeMediaPlayer(context, Uri.parse
                                    (ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getPackageName() + "/" + R.raw.doogee_ringtone));
                            MusicManager.getInstance().stopPlaying();
                            dialog.dismiss();
                        }
                    } else {
                        dialog.dismiss();
                        MusicManager.getInstance().initalizeMediaPlayer(context, Uri.parse
                                (ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getPackageName() + "/" + R.raw.doogee_ringtone));
                        MusicManager.getInstance().stopPlaying();
                       // MyApplication.showToast(context, context.getString(R.string.accept_by_another));
                    }

                } catch (Exception e) {
                    Toast.makeText(context, "Exception = " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Exception", "Exception = " + e.getMessage());
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
            }

        });

    }

    /**
     * method to reset count down timer
     */
    private void reset() {
        stopCountDownTimer();
        startCountDownTimer();
    }

    private void startStop() {
        if (timerStatus == TimerStatus.STOPPED) {

            // call to initialize the timer values
            setTimerValues();
            // call to initialize the progress bar values
            setProgressBarValues();
            timerStatus = TimerStatus.STARTED;
            // call to start the count down timer
            startCountDownTimer();

        } else {
            // changing the timer status to stopped
            timerStatus = TimerStatus.STOPPED;
            stopCountDownTimer();
            dialog.dismiss();

        }

    }

    /**
     * method to initialize the values for count down timer
     */
    private void setTimerValues() {
       /* int time = 0;
        if (!editTextMinute.getText().toString().isEmpty()) {
            // fetching value from edit text and type cast to integer
            time = Integer.parseInt(editTextMinute.getText().toString().trim());
        } else {
            // toast message to fill edit text
            Toast.makeText(getApplicationContext(), getString(R.string.message_minutes), Toast.LENGTH_LONG).show();
        }*/
        // assigning values after converting to milliseconds
        // timeCountInMilliSeconds = 1 * 60 * 1000;
        timeCountInMilliSeconds = 30000;
    }

    /**
     * method to start count down timer
     */
    private void startCountDownTimer() {

        countDownTimer = new CountDownTimer(timeCountInMilliSeconds, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                binding.textViewTime.setText(hmsTimeFormatter(millisUntilFinished));

                binding.progressBarCircle.setProgress((int) (millisUntilFinished / 1000));

            }

            @Override
            public void onFinish() {

                //  textViewTime.setText(hmsTimeFormatter(timeCountInMilliSeconds));
                // call to initialize the progress bar values
                //  setProgressBarValues();
                timerStatus = TimerStatus.STOPPED;
                dialog.dismiss();
            }

        }.start();
        countDownTimer.start();
    }

    /**
     * method to stop count down timer
     */
    private void stopCountDownTimer() {
        countDownTimer.cancel();
        dialog.dismiss();
    }

    /**
     * method to set circular progress bar values
     */
    private void setProgressBarValues() {
        binding.progressBarCircle.setMax((int) timeCountInMilliSeconds / 1000);
        binding.progressBarCircle.setProgress((int) timeCountInMilliSeconds / 1000);
    }


    /**
     * method to convert millisecond to time format
     *
     * @param milliSeconds
     * @return HH:mm:ss time formatted string
     */
    private String hmsTimeFormatter(long milliSeconds) {

        String hms = String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(milliSeconds),
                TimeUnit.MILLISECONDS.toMinutes(milliSeconds) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliSeconds)),
                TimeUnit.MILLISECONDS.toSeconds(milliSeconds) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliSeconds)));

        String ms[] = hms.split(":");

        return ms[1] + ":" + ms[2];
    }

//    public void AcceptCancel(Context context,String orderId,String status) {
//        HashMap<String, String> map = new HashMap<>();
//        map.put("driver_id", SessionManager.get(context).getUserID());
//        map.put("orderId", orderId);
//        map.put("status", status);
//        ApiCallBuilder.build(context)
//                .setUrl(BaseClass.get().AcceptCancelRequest())
//                .setParam(map)
//                .isShowProgressBar(true)
//                .execute(new ApiCallBuilder.onResponse() {
//                    @Override
//                    public void Success(String response) {
//                        try {
//                            JSONObject jsonObject = new JSONObject(response);
//                            if(jsonObject.getString("status").equals("1")) {
//                                if(status.equals("Accept")) {
////                                    context.startActivity(new Intent(context, TrackAct.class)
////                                            .putExtra("orderId",orderId));
//                                    MusicManager.getInstance().initalizeMediaPlayer(context, Uri.parse
//                                            (ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getPackageName() + "/" + R.raw.doogee_ringtone));
//                                    MusicManager.getInstance().stopPlaying();
//                                    dialog.dismiss();
//                                } else {
//                                    MusicManager.getInstance().initalizeMediaPlayer(context, Uri.parse
//                                            (ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getPackageName() + "/" + R.raw.doogee_ringtone));
//                                    MusicManager.getInstance().stopPlaying();
//                                    dialog.dismiss();
//                                }
//                            } else {
//                                dialog.dismiss();
//                                MyApplication.showToast(context,context.getString(R.string.accept_by_another));
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//
//                    @Override
//                    public void Failed(String error) {
//
//                    }
//                });
//
//    }

}
