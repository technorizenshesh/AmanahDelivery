package com.amanahdelivery.taxi.models;

import java.io.Serializable;

public class ModelTaxiPayment implements Serializable {

    private Result result;
    private String message;
    private int status;

    public void setResult(Result result){
        this.result = result;
    }
    public Result getResult(){
        return this.result;
    }
    public void setMessage(String message){
        this.message = message;
    }
    public String getMessage(){
        return this.message;
    }
    public void setStatus(int status){
        this.status = status;
    }
    public int getStatus(){
        return this.status;
    }

    public class Result implements Serializable
    {
        private String id;

        private String user_id;

        private String driver_id;

        private String picuplocation;

        private String service_type;

        private String dropofflocation;

        private String picuplat;

        private String pickuplon;

        private String droplat;

        private String droplon;

        private String shareride_type;

        private String booktype;

        private String car_type_id;

        private String car_seats;

        private String passenger;

        private String booked_seats;

        private String req_datetime;

        private String timezone;

        private String picklatertime;

        private String picklaterdate;

        private String route_img;

        private String start_time;

        private String end_time;

        private String wt_start_time;

        private String wt_end_time;

        private String accept_time;

        private String waiting_status;

        private String distance;

        private String waiting_time;

        private String waiting_cnt;

        private String reason_id;

        private String card_id;

        private String apply_code;

        private String payment_type;

        private String favorite_ride;

        private String status;

        private String user_rating_status;

        private String tip_amount;

        private String amount;

        public String getDistance() {
            return distance;
        }

        public void setDistance(String distance) {
            this.distance = distance;
        }

        public String getWaiting_time() {
            return waiting_time;
        }

        public void setWaiting_time(String waiting_time) {
            this.waiting_time = waiting_time;
        }

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
        public void setPicuplocation(String picuplocation){
            this.picuplocation = picuplocation;
        }
        public String getPicuplocation(){
            return this.picuplocation;
        }
        public void setService_type(String service_type){
            this.service_type = service_type;
        }
        public String getService_type(){
            return this.service_type;
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
        public void setCar_type_id(String car_type_id){
            this.car_type_id = car_type_id;
        }
        public String getCar_type_id(){
            return this.car_type_id;
        }
        public void setCar_seats(String car_seats){
            this.car_seats = car_seats;
        }
        public String getCar_seats(){
            return this.car_seats;
        }
        public void setPassenger(String passenger){
            this.passenger = passenger;
        }
        public String getPassenger(){
            return this.passenger;
        }
        public void setBooked_seats(String booked_seats){
            this.booked_seats = booked_seats;
        }
        public String getBooked_seats(){
            return this.booked_seats;
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
        public void setEnd_time(String end_time){
            this.end_time = end_time;
        }
        public String getEnd_time(){
            return this.end_time;
        }
        public void setWt_start_time(String wt_start_time){
            this.wt_start_time = wt_start_time;
        }
        public String getWt_start_time(){
            return this.wt_start_time;
        }
        public void setWt_end_time(String wt_end_time){
            this.wt_end_time = wt_end_time;
        }
        public String getWt_end_time(){
            return this.wt_end_time;
        }
        public void setAccept_time(String accept_time){
            this.accept_time = accept_time;
        }
        public String getAccept_time(){
            return this.accept_time;
        }
        public void setWaiting_status(String waiting_status){
            this.waiting_status = waiting_status;
        }
        public String getWaiting_status(){
            return this.waiting_status;
        }
        public void setWaiting_cnt(String waiting_cnt){
            this.waiting_cnt = waiting_cnt;
        }
        public String getWaiting_cnt(){
            return this.waiting_cnt;
        }
        public void setReason_id(String reason_id){
            this.reason_id = reason_id;
        }
        public String getReason_id(){
            return this.reason_id;
        }
        public void setCard_id(String card_id){
            this.card_id = card_id;
        }
        public String getCard_id(){
            return this.card_id;
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
        public void setFavorite_ride(String favorite_ride){
            this.favorite_ride = favorite_ride;
        }
        public String getFavorite_ride(){
            return this.favorite_ride;
        }
        public void setStatus(String status){
            this.status = status;
        }
        public String getStatus(){
            return this.status;
        }
        public void setUser_rating_status(String user_rating_status){
            this.user_rating_status = user_rating_status;
        }
        public String getUser_rating_status(){
            return this.user_rating_status;
        }
        public void setTip_amount(String tip_amount){
            this.tip_amount = tip_amount;
        }
        public String getTip_amount(){
            return this.tip_amount;
        }
        public void setAmount(String amount){
            this.amount = amount;
        }
        public String getAmount(){
            return this.amount;
        }
    }

}
