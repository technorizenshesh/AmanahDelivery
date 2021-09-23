package com.amanahdelivery.taxi.models;

import java.io.Serializable;
import java.util.ArrayList;

public class ModelCarsType implements Serializable {

    private ArrayList<Result> result;
    private String status;
    private String message;

    public void setResult(ArrayList<Result> result){
        this.result = result;
    }
    public ArrayList<Result> getResult(){
        return this.result;
    }
    public void setStatus(String status){
        this.status = status;
    }
    public String getStatus(){
        return this.status;
    }
    public void setMessage(String message){
        this.message = message;
    }
    public String getMessage(){
        return this.message;
    }

    public class Result implements Serializable
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
