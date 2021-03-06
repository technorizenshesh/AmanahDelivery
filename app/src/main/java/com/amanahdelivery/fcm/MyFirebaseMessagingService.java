package com.amanahdelivery.fcm;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.amanahdelivery.R;
import com.amanahdelivery.deliveryshops.activities.ShopOrderHomeAct;
import com.amanahdelivery.models.ModelLogin;
import com.amanahdelivery.taxi.activities.TaxiHomeAct;
import com.amanahdelivery.utils.AppConstant;
import com.amanahdelivery.utils.MusicManager;
import com.amanahdelivery.utils.SharedPref;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.Random;;

public class MyFirebaseMessagingService
        extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    private NotificationChannel mChannel;
    private NotificationManager notificationManager;
    private MediaPlayer mPlayer;
    Intent intent;
    SharedPref sharedPref;
    ModelLogin modelLogin;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.e(TAG, "c:" + remoteMessage.getData());

//        try {
//            Log.e(TAG, "fsfsdfd:" + remoteMessage.getNotification());
//            Log.e(TAG, "fsfsdfd:" + remoteMessage.getNotification().getBody());
//        } catch (Exception e) {}

        if (remoteMessage.getData().size() > 0) {

            Map<String,String> data = remoteMessage.getData();

            try {

                String title = "", key = "", status = "", noti_type = "", driverId = "", bookindStatus = "";

                JSONObject object = new JSONObject(data.get("message"));

                try {
                    bookindStatus = object.getString("booking_status");
                    Log.e("dsfasdfasf", "object.getString() booking_status = " + object.getString("status"));
                } catch (Exception e) {
                }

                try {
                    noti_type = object.getString("noti_type");
                } catch (Exception e) {
                }

                try {
                    key = object.getString("key");
                    status = object.getString("status");
                    Log.e("dsfasdfasf", "object.getString() status = " + object.getString("status"));
                    driverId = object.getString("driver_id");
                } catch (Exception e) {
                }

                if (AppConstant.DEV_FOOD.equals(noti_type)) {
                    title = "New Order";
                    if ("Pending".equals(status)) {
                        key = object.getString("key");
                        Intent intent1 = new Intent("Job_Status_Action");
                        Log.e("SendData=====", object.toString());
                        intent1.putExtra("object", object.toString());
                        sendBroadcast(intent1);
                    }
                } else if (AppConstant.TAXI_DRIVER.equals(noti_type)) {
                    title = "New Booking Request";
                    if ("Pending".equals(status)) {
                        // title = object.getString("title");
                        title = "Taxi Booking";
                        key = object.getString("key");
                        Intent intent1 = new Intent("Job_Status_Action_Taxi");
                        Log.e("SendData=====", object.toString());
                        intent1.putExtra("object", object.toString());
                        sendBroadcast(intent1);
                    } else if ("Cancel_by_user".equals(bookindStatus)) {
                        Intent intent1 = new Intent("job_status");
                        Log.e("SendData=====", object.toString());
                        intent1.putExtra("status", "Cancel");
                        sendBroadcast(intent1);
                    }

                }

                sharedPref = SharedPref.getInstance(this);

                Log.e("dsfasdfasf", "isLoginLogin = " + sharedPref.getBooleanValue(AppConstant.IS_REGISTER));
                Log.e("dsfasdfasf", "booking_status = " + bookindStatus);

                if (sharedPref.getBooleanValue(AppConstant.IS_REGISTER)) {
                    if (AppConstant.TAXI_DRIVER.equals(noti_type)) {
                        Log.e("dsfasdfasf", "booking_status = " + key);
                        Log.e("dsfasdfasf", "noti_type = " + noti_type);
                        MusicManager.getInstance().initalizeMediaPlayer(this, Uri.parse
                                (ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getPackageName() + "/" + R.raw.doogee_ringtone));
                        MusicManager.getInstance().startPlaying();

                        if(status == null || status.equals("")) {
                            displayCustomNotificationForTaxi(bookindStatus, title, key, object.toString());
                        } else {
                            displayCustomNotificationForTaxi(status, title, key, object.toString());
                        }

//                        if (bookindStatus == null) {
//                            Log.e("bookindStatusbookin", "status inside null = " + status);
//                            displayCustomNotificationForTaxi(status, title, key, object.toString());
//                        } else if (bookindStatus.equals("")) {
//                            Log.e("bookindStatusbookin", "status inside empty = " + status);
//                            displayCustomNotificationForTaxi(status, title, key, object.toString());
//                        } else {
//                            Log.e("bookindStatusbookin", "bookindStatus inside empty = " + status);
//                            displayCustomNotificationForTaxi(bookindStatus, title, key, object.toString());
//                        }

                    } else if (AppConstant.DEV_FOOD.equals(noti_type)) {
                        MusicManager.getInstance().initalizeMediaPlayer(this, Uri.parse
                                (ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getPackageName() + "/" + R.raw.doogee_ringtone));
                        MusicManager.getInstance().startPlaying();
                        displayCustomNotificationForOrders(status, title, "You have a new order", object.toString());
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

    private void displayCustomNotificationForTaxi(String status, String title, String msg, String data) {

      Log.e("ggddfdfdfd", "Displaying Notify Cancel");
      Log.e("ggddfdfdfd", "status = " + status);
      Log.e("ggddfdfdfd", "Data Status 123= " + data);
      Log.e("ggddfdfdfd", "Cancel_by_user = " + !"Cancel_by_user".equals(status));

        intent = new Intent(this, TaxiHomeAct.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        if (!"Cancel_by_user".equals(status)) {
            intent.putExtra("type", "dialog");
            intent.putExtra("data", data);
            intent.putExtra("object", data);
        }

//      intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//      intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(intent);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent
                (0, PendingIntent.FLAG_UPDATE_CURRENT);
        /*PendingIntent.getActivity(this, 123, intent,PendingIntent.FLAG_UPDATE_CURRENT);*/
        String channelId = "123";
        // Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this,channelId)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                        .setSmallIcon(R.drawable.ic_logo)
                        //.setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.ic_logo))
                        .setContentTitle(getString(R.string.app_name))
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setContentText(msg)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setAutoCancel(true)
                        /*.setSound(defaultSoundUri)*/
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);
        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Channel human readable title
            NotificationChannel channel = new NotificationChannel(channelId, "Cloud Messaging Service",
                    NotificationManager.IMPORTANCE_HIGH);
            channel.enableLights(true);
            channel.setLightColor(Color.GREEN);
            channel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            channel.enableVibration(true);

            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(getNotificationId(), notificationBuilder.build());
    }

    private void displayCustomNotificationForOrders(String status, String title, String msg, String data) {

        intent = new Intent(this, ShopOrderHomeAct.class);
        intent.putExtra("type", "dialog");
        intent.putExtra("data", data);
        intent.putExtra("object", data);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //      intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//      intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(intent);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent
                (0, PendingIntent.FLAG_ONE_SHOT);/*PendingIntent.getActivity(this, 123, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);*/
        String channelId = "123";
        // Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                        .setSmallIcon(R.drawable.ic_logo)
                        //.setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.ic_logo))
                        .setContentTitle(getString(R.string.app_name))
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setContentText(msg)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setAutoCancel(true)
                        /*.setSound(defaultSoundUri)*/
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);
        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Channel human readable title
            NotificationChannel channel = new NotificationChannel(channelId, "Cloud Messaging Service",
                    NotificationManager.IMPORTANCE_HIGH);
            channel.enableLights(true);
            channel.setLightColor(Color.GREEN);
            channel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            channel.enableVibration(true);

            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(getNotificationId(), notificationBuilder.build());

    }

    private static int getNotificationId() {
        Random rnd = new Random();
        return 100 + rnd.nextInt(9000);
    }

}
