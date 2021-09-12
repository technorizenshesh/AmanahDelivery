package com.amanahdelivery.utils.directionclasses;

import android.content.Context;
import android.util.Log;

import com.amanahdelivery.R;
import com.google.android.gms.maps.model.LatLng;

public class BaseClass {

    public static String BaseUrl="https://equipmeapp.co.nz/hammonia_taxi/webservice/";

    public static BaseClass get() {
        return new BaseClass();
    }

    public String Login(){
        return BaseUrl.concat("login");
    }

    public String SignUp(){
        return BaseUrl.concat("signup_provider");
    }

    public String getProfile(){
        return BaseUrl.concat("get_profile");
    }

    public String getPolyLineUrl(Context context,LatLng origin, LatLng dest) {
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        String sensor = "sensor=false";
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&key=" + context.getResources().getString(R.string.googlekey_other);
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
        Log.e("PathURL","====> "+url);
        return url;
    }

    public String updateOnlineStatus(){
        return BaseUrl.concat("update_online_status");
    }

    public String getCurrentBooking() {
        return BaseUrl.concat("get_current_booking");
    }

    public String AcceptCancelRequest(){
        return BaseUrl.concat("driver_accept_and_Cancel_request");
    }
    public String getPayment(){
        return BaseUrl.concat("get_payment");
    }
    public String getUserHistory(){
        return BaseUrl.concat("get_user_history");
    }
    public String add_payment(){
        return BaseUrl.concat("add_payment");
    }

    public String upload_document(){
        return BaseUrl.concat("upload_document");
    }

    public String vehicle_list(){
        return BaseUrl.concat("vehicle_list");
    }

    public String getNearUser() {
        return BaseUrl.concat("get_heat_view");
    }

    public String get_user_feedback(){
        return BaseUrl.concat("get_user_feedback");
    }

}
