package com.amanahdelivery.models;

import java.io.Serializable;

public class ModelLogin implements Serializable {

    private String message;
    private Result result;
    private String status;

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public Result getResult() {
        return this.result;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return this.status;
    }

    public class Result implements Serializable {

        private String address;

        private String badge;

        private String name;

        private String image;

        private String date_time;

        private String vehicle_number;

        private String email;

        private String shop_status;

        private String id;

        private String identity;

        private String lat;

        private String lon;

        private String mobile;

        private String nxn_point;

        private String online_status;

        private String otp;

        private String password;

        private String phone_code;

        private String driver_lisence_img;

        private String responsible_letter_img;

        private String identification_img;

        private String register_id;

        private String screen_count;

        private String status;

        private String type;

        private String user_name;

        private String wallet;

        public String getVehicle_number() {
            return vehicle_number;
        }

        public void setVehicle_number(String vehicle_number) {
            this.vehicle_number = vehicle_number;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDriver_lisence_img() {
            return driver_lisence_img;
        }

        public void setDriver_lisence_img(String driver_lisence_img) {
            this.driver_lisence_img = driver_lisence_img;
        }

        public String getResponsible_letter_img() {
            return responsible_letter_img;
        }

        public void setResponsible_letter_img(String responsible_letter_img) {
            this.responsible_letter_img = responsible_letter_img;
        }

        public String getIdentification_img() {
            return identification_img;
        }

        public void setIdentification_img(String identification_img) {
            this.identification_img = identification_img;
        }

        public String getShop_status() {
            return shop_status;
        }
        public void setShop_status(String shop_status) {
            this.shop_status = shop_status;
        }
        public void setAddress(String address){
            this.address = address;
        }
        public String getAddress(){
            return this.address;
        }
        public void setBadge(String badge){
            this.badge = badge;
        }
        public String getBadge(){
            return this.badge;
        }
        public void setDate_time(String date_time){
            this.date_time = date_time;
        }
        public String getDate_time(){
            return this.date_time;
        }
        public void setEmail(String email){
            this.email = email;
        }
        public String getEmail(){
            return this.email;
        }
        public void setId(String id){
            this.id = id;
        }
        public String getId(){
            return this.id;
        }
        public void setIdentity(String identity){
            this.identity = identity;
        }
        public String getIdentity(){
            return this.identity;
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
        public void setMobile(String mobile){
            this.mobile = mobile;
        }
        public String getMobile(){
            return this.mobile;
        }
        public void setNxn_point(String nxn_point){
            this.nxn_point = nxn_point;
        }
        public String getNxn_point(){
            return this.nxn_point;
        }
        public void setOnline_status(String online_status){
            this.online_status = online_status;
        }
        public String getOnline_status(){
            return this.online_status;
        }
        public void setOtp(String otp){
            this.otp = otp;
        }
        public String getOtp(){
            return this.otp;
        }
        public void setPassword(String password){
            this.password = password;
        }
        public String getPassword(){
            return this.password;
        }
        public void setPhone_code(String phone_code){
            this.phone_code = phone_code;
        }
        public String getPhone_code(){
            return this.phone_code;
        }
        public void setRegister_id(String register_id){
            this.register_id = register_id;
        }
        public String getRegister_id(){
            return this.register_id;
        }
        public void setScreen_count(String screen_count){
            this.screen_count = screen_count;
        }
        public String getScreen_count(){
            return this.screen_count;
        }
        public void setStatus(String status){
            this.status = status;
        }
        public String getStatus(){
            return this.status;
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
        public void setWallet(String wallet){
            this.wallet = wallet;
        }
        public String getWallet(){
            return this.wallet;
        }

    }

}
