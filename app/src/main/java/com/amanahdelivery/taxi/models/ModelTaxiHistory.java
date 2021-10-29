package com.amanahdelivery.taxi.models;

import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

import com.amanahdelivery.R;
import com.bumptech.glide.Glide;

import java.io.Serializable;
import java.util.ArrayList;

public class ModelTaxiHistory implements Serializable {

    private ArrayList<Result> result;
    private String message;
    private String status;

    public void setResult(ArrayList<Result> result){
        this.result = result;
    }
    public ArrayList<Result> getResult(){
        return this.result;
    }
    public void setMessage(String message){
        this.message = message;
    }
    public String getMessage(){
        return this.message;
    }
    public void setStatus(String status){
        this.status = status;
    }
    public String getStatus(){
        return this.status;
    }

    public class Result implements Serializable
    {
        private String id;

        private String user_id;

        private String driver_id;

        private String car_type_id;

        private String picuplocation;

        private String dropofflocation;

        private String picuplat;

        private String pickuplon;

        private String droplat;

        private String droplon;

        private String amount;

        private String status;

        private String shareride_type;

        private String booktype;

        private String req_datetime;

        private String timezone;

        private String picklatertime;

        private String picklaterdate;

        private String route_img;

        private String start_time;

        private String car_seats;

        private String end_time;

        private String apply_code;

        private String payment_type;

        private String service_type;

        private String booked_seats;

        private String passenger;

        private String distance;

        private Users_details users_details;

        private Drivers_details drivers_details;

        private Car_list car_list;

        public void setId(String id){
            this.id = id;
        }
        public String getId(){
            return this.id;
        }
        public void setUser_id(String user_id){
            this.user_id = user_id;
        }
        public String getUser_id(){
            return this.user_id;
        }
        public void setDriver_id(String driver_id){
            this.driver_id = driver_id;
        }
        public String getDriver_id(){
            return this.driver_id;
        }
        public void setCar_type_id(String car_type_id){
            this.car_type_id = car_type_id;
        }
        public String getCar_type_id(){
            return this.car_type_id;
        }
        public void setPicuplocation(String picuplocation){
            this.picuplocation = picuplocation;
        }
        public String getPicuplocation(){
            return this.picuplocation;
        }
        public void setDropofflocation(String dropofflocation){
            this.dropofflocation = dropofflocation;
        }
        public String getDropofflocation(){
            return this.dropofflocation;
        }
        public void setPicuplat(String picuplat){
            this.picuplat = picuplat;
        }
        public String getPicuplat(){
            return this.picuplat;
        }
        public void setPickuplon(String pickuplon){
            this.pickuplon = pickuplon;
        }
        public String getPickuplon(){
            return this.pickuplon;
        }
        public void setDroplat(String droplat){
            this.droplat = droplat;
        }
        public String getDroplat(){
            return this.droplat;
        }
        public void setDroplon(String droplon){
            this.droplon = droplon;
        }
        public String getDroplon(){
            return this.droplon;
        }
        public void setAmount(String amount){
            this.amount = amount;
        }
        public String getAmount(){
            return this.amount;
        }
        public void setStatus(String status){
            this.status = status;
        }
        public String getStatus(){
            return this.status;
        }
        public void setShareride_type(String shareride_type){
            this.shareride_type = shareride_type;
        }
        public String getShareride_type(){
            return this.shareride_type;
        }
        public void setBooktype(String booktype){
            this.booktype = booktype;
        }
        public String getBooktype(){
            return this.booktype;
        }
        public void setReq_datetime(String req_datetime){
            this.req_datetime = req_datetime;
        }
        public String getReq_datetime(){
            return this.req_datetime;
        }
        public void setTimezone(String timezone){
            this.timezone = timezone;
        }
        public String getTimezone(){
            return this.timezone;
        }
        public void setPicklatertime(String picklatertime){
            this.picklatertime = picklatertime;
        }
        public String getPicklatertime(){
            return this.picklatertime;
        }
        public void setPicklaterdate(String picklaterdate){
            this.picklaterdate = picklaterdate;
        }
        public String getPicklaterdate(){
            return this.picklaterdate;
        }
        public void setRoute_img(String route_img){
            this.route_img = route_img;
        }
        public String getRoute_img(){
            return this.route_img;
        }
        public void setStart_time(String start_time){
            this.start_time = start_time;
        }
        public String getStart_time(){
            return this.start_time;
        }
        public void setCar_seats(String car_seats){
            this.car_seats = car_seats;
        }
        public String getCar_seats(){
            return this.car_seats;
        }
        public void setEnd_time(String end_time){
            this.end_time = end_time;
        }
        public String getEnd_time(){
            return this.end_time;
        }
        public void setApply_code(String apply_code){
            this.apply_code = apply_code;
        }
        public String getApply_code(){
            return this.apply_code;
        }
        public void setPayment_type(String payment_type){
            this.payment_type = payment_type;
        }
        public String getPayment_type(){
            return this.payment_type;
        }
        public void setService_type(String service_type){
            this.service_type = service_type;
        }
        public String getService_type(){
            return this.service_type;
        }
        public void setBooked_seats(String booked_seats){
            this.booked_seats = booked_seats;
        }
        public String getBooked_seats(){
            return this.booked_seats;
        }
        public void setPassenger(String passenger){
            this.passenger = passenger;
        }
        public String getPassenger(){
            return this.passenger;
        }
        public void setDistance(String distance){
            this.distance = distance;
        }
        public String getDistance(){
            return this.distance;
        }
        public void setUsers_details(Users_details users_details){
            this.users_details = users_details;
        }
        public Users_details getUsers_details(){
            return this.users_details;
        }
        public void setDrivers_details(Drivers_details drivers_details){
            this.drivers_details = drivers_details;
        }
        public Drivers_details getDrivers_details(){
            return this.drivers_details;
        }
        public void setCar_list(Car_list car_list){
            this.car_list = car_list;
        }
        public Car_list getCar_list(){
            return this.car_list;
        }

        public class Users_details implements Serializable
        {
            private String id;

            private String type;

            private String user_name;

            private String mobile;

            private String email;

            private String password;

            private String image;

            private String lat;

            private String lon;

            private String social_id;

            private String ios_register_id;

            private String date_time;

            private String address;

            private String register_id;

            private String status;

            private String country_code;

            private String otp;

            private String stripe_account_id;

            private String driver_lisence_img;

            private String responsible_letter_img;

            private String identification_img;

            private String name;

            private String online_status;

            private String car_type_id;

            private String vehicle_number;

            public void setId(String id){
                this.id = id;
            }
            public String getId(){
                return this.id;
            }
            public void setType(String type){
                this.type = type;
            }
            public String getType(){
                return this.type;
            }
            public void setUser_name(String user_name){
                this.user_name = user_name;
            }
            public String getUser_name(){
                return this.user_name;
            }
            public void setMobile(String mobile){
                this.mobile = mobile;
            }
            public String getMobile(){
                return this.mobile;
            }
            public void setEmail(String email){
                this.email = email;
            }
            public String getEmail(){
                return this.email;
            }
            public void setPassword(String password){
                this.password = password;
            }
            public String getPassword(){
                return this.password;
            }
            public void setImage(String image){
                this.image = image;
            }
            public String getImage(){
                return this.image;
            }
            public void setLat(String lat){
                this.lat = lat;
            }
            public String getLat(){
                return this.lat;
            }
            public void setLon(String lon){
                this.lon = lon;
            }
            public String getLon(){
                return this.lon;
            }
            public void setSocial_id(String social_id){
                this.social_id = social_id;
            }
            public String getSocial_id(){
                return this.social_id;
            }
            public void setIos_register_id(String ios_register_id){
                this.ios_register_id = ios_register_id;
            }
            public String getIos_register_id(){
                return this.ios_register_id;
            }
            public void setDate_time(String date_time){
                this.date_time = date_time;
            }
            public String getDate_time(){
                return this.date_time;
            }
            public void setAddress(String address){
                this.address = address;
            }
            public String getAddress(){
                return this.address;
            }
            public void setRegister_id(String register_id){
                this.register_id = register_id;
            }
            public String getRegister_id(){
                return this.register_id;
            }
            public void setStatus(String status){
                this.status = status;
            }
            public String getStatus(){
                return this.status;
            }
            public void setCountry_code(String country_code){
                this.country_code = country_code;
            }
            public String getCountry_code(){
                return this.country_code;
            }
            public void setOtp(String otp){
                this.otp = otp;
            }
            public String getOtp(){
                return this.otp;
            }
            public void setStripe_account_id(String stripe_account_id){
                this.stripe_account_id = stripe_account_id;
            }
            public String getStripe_account_id(){
                return this.stripe_account_id;
            }
            public void setDriver_lisence_img(String driver_lisence_img){
                this.driver_lisence_img = driver_lisence_img;
            }
            public String getDriver_lisence_img(){
                return this.driver_lisence_img;
            }
            public void setResponsible_letter_img(String responsible_letter_img){
                this.responsible_letter_img = responsible_letter_img;
            }
            public String getResponsible_letter_img(){
                return this.responsible_letter_img;
            }
            public void setIdentification_img(String identification_img){
                this.identification_img = identification_img;
            }
            public String getIdentification_img(){
                return this.identification_img;
            }
            public void setName(String name){
                this.name = name;
            }
            public String getName(){
                return this.name;
            }
            public void setOnline_status(String online_status){
                this.online_status = online_status;
            }
            public String getOnline_status(){
                return this.online_status;
            }
            public void setCar_type_id(String car_type_id){
                this.car_type_id = car_type_id;
            }
            public String getCar_type_id(){
                return this.car_type_id;
            }
            public void setVehicle_number(String vehicle_number){
                this.vehicle_number = vehicle_number;
            }
            public String getVehicle_number(){
                return this.vehicle_number;
            }
        }

        public class Drivers_details implements Serializable
        {
            private String id;

            private String type;

            private String user_name;

            private String mobile;

            private String email;

            private String password;

            private String image;

            private String lat;

            private String lon;

            private String social_id;

            private String ios_register_id;

            private String date_time;

            private String address;

            private String register_id;

            private String status;

            private String country_code;

            private String otp;

            private String stripe_account_id;

            private String driver_lisence_img;

            private String responsible_letter_img;

            private String identification_img;

            private String name;

            private String online_status;

            private String car_type_id;

            private String vehicle_number;

            public void setId(String id){
                this.id = id;
            }
            public String getId(){
                return this.id;
            }
            public void setType(String type){
                this.type = type;
            }
            public String getType(){
                return this.type;
            }
            public void setUser_name(String user_name){
                this.user_name = user_name;
            }
            public String getUser_name(){
                return this.user_name;
            }
            public void setMobile(String mobile){
                this.mobile = mobile;
            }
            public String getMobile(){
                return this.mobile;
            }
            public void setEmail(String email){
                this.email = email;
            }
            public String getEmail(){
                return this.email;
            }
            public void setPassword(String password){
                this.password = password;
            }
            public String getPassword(){
                return this.password;
            }
            public void setImage(String image){
                this.image = image;
            }
            public String getImage(){
                return this.image;
            }
            public void setLat(String lat){
                this.lat = lat;
            }
            public String getLat(){
                return this.lat;
            }
            public void setLon(String lon){
                this.lon = lon;
            }
            public String getLon(){
                return this.lon;
            }
            public void setSocial_id(String social_id){
                this.social_id = social_id;
            }
            public String getSocial_id(){
                return this.social_id;
            }
            public void setIos_register_id(String ios_register_id){
                this.ios_register_id = ios_register_id;
            }
            public String getIos_register_id(){
                return this.ios_register_id;
            }
            public void setDate_time(String date_time){
                this.date_time = date_time;
            }
            public String getDate_time(){
                return this.date_time;
            }
            public void setAddress(String address){
                this.address = address;
            }
            public String getAddress(){
                return this.address;
            }
            public void setRegister_id(String register_id){
                this.register_id = register_id;
            }
            public String getRegister_id(){
                return this.register_id;
            }
            public void setStatus(String status){
                this.status = status;
            }
            public String getStatus(){
                return this.status;
            }
            public void setCountry_code(String country_code){
                this.country_code = country_code;
            }
            public String getCountry_code(){
                return this.country_code;
            }
            public void setOtp(String otp){
                this.otp = otp;
            }
            public String getOtp(){
                return this.otp;
            }
            public void setStripe_account_id(String stripe_account_id){
                this.stripe_account_id = stripe_account_id;
            }
            public String getStripe_account_id(){
                return this.stripe_account_id;
            }
            public void setDriver_lisence_img(String driver_lisence_img){
                this.driver_lisence_img = driver_lisence_img;
            }
            public String getDriver_lisence_img(){
                return this.driver_lisence_img;
            }
            public void setResponsible_letter_img(String responsible_letter_img){
                this.responsible_letter_img = responsible_letter_img;
            }
            public String getResponsible_letter_img(){
                return this.responsible_letter_img;
            }
            public void setIdentification_img(String identification_img){
                this.identification_img = identification_img;
            }
            public String getIdentification_img(){
                return this.identification_img;
            }
            public void setName(String name){
                this.name = name;
            }
            public String getName(){
                return this.name;
            }
            public void setOnline_status(String online_status){
                this.online_status = online_status;
            }
            public String getOnline_status(){
                return this.online_status;
            }
            public void setCar_type_id(String car_type_id){
                this.car_type_id = car_type_id;
            }
            public String getCar_type_id(){
                return this.car_type_id;
            }
            public void setVehicle_number(String vehicle_number){
                this.vehicle_number = vehicle_number;
            }
            public String getVehicle_number(){
                return this.vehicle_number;
            }
        }

        public class Car_list implements Serializable
        {
            private String id;

            private String car_name;

            private String car_image;

            private String charge;

            private String status;

            public void setId(String id){
                this.id = id;
            }
            public String getId(){
                return this.id;
            }
            public void setCar_name(String car_name){
                this.car_name = car_name;
            }
            public String getCar_name(){
                return this.car_name;
            }
            public void setCar_image(String car_image){
                this.car_image = car_image;
            }
            public String getCar_image(){
                return this.car_image;
            }
            public void setCharge(String charge){
                this.charge = charge;
            }
            public String getCharge(){
                return this.charge;
            }
            public void setStatus(String status){
                this.status = status;
            }
            public String getStatus(){
                return this.status;
            }
        }

    }

    @BindingAdapter({"imageUrl"})
    public static void loadImage(ImageView view, String url) {
        Glide.with(view.getContext())
                .load(url)
                .placeholder(R.drawable.user_ic)
                .into(view);
    }


}
