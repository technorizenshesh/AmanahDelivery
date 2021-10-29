package com.amanahdelivery.taxi.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class ModelCurrentBooking implements Serializable {

    @SerializedName("estimated_time")
    @Expose
    private String estimatedTime;
    @SerializedName("estimated_dis")
    @Expose
    private Double estimatedDis;
    @SerializedName("waiting_time")
    @Expose
    private String waiting_time;
    @SerializedName("fare")
    @Expose
    private Integer fare;
    @SerializedName("time_taken")
    @Expose
    private String timeTaken;
    @SerializedName("wait_time")
    @Expose
    private String waitTime;
    @SerializedName("base_fare")
    @Expose
    private String baseFare;
    @SerializedName("trip_fare")
    @Expose
    private Double tripFare;
    @SerializedName("tax_amt")
    @Expose
    private Integer taxAmt;
    @SerializedName("service_tax")
    @Expose
    private String serviceTax;
    @SerializedName("service_amt")
    @Expose
    private Integer serviceAmt;
    @SerializedName("sub_total")
    @Expose
    private Integer subTotal;
    @SerializedName("paid_amount")
    @Expose
    private Integer paidAmount;
    @SerializedName("discount_percent")
    @Expose
    private Integer discountPercent;
    @SerializedName("discount")
    @Expose
    private Integer discount;
    @SerializedName("distance")
    @Expose
    private String distance;
    @SerializedName("route_img")
    @Expose
    private String routeImg;
    @SerializedName("fav_status")
    @Expose
    private String favStatus;
    @SerializedName("result")
    @Expose
    private List<ModelCurrentBookingResult> result = null;
    @SerializedName("map")
    @Expose
    private String map;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("diff_second")
    @Expose
    private String diffSecond;
    @SerializedName("booking_dropoff")
    @Expose
    private List<Object> bookingDropoff = null;
    @SerializedName("status")
    @Expose
    private Integer status;

    public String getWaiting_time() {
        return waiting_time;
    }

    public void setWaiting_time(String waiting_time) {
        this.waiting_time = waiting_time;
    }

    public String getEstimatedTime() {
        return estimatedTime;
    }

    public void setEstimatedTime(String estimatedTime) {
        this.estimatedTime = estimatedTime;
    }

    public Double getEstimatedDis() {
        return estimatedDis;
    }

    public void setEstimatedDis(Double estimatedDis) {
        this.estimatedDis = estimatedDis;
    }

    public Integer getFare() {
        return fare;
    }

    public void setFare(Integer fare) {
        this.fare = fare;
    }

    public String getTimeTaken() {
        return timeTaken;
    }

    public void setTimeTaken(String timeTaken) {
        this.timeTaken = timeTaken;
    }

    public Object getWaitTime() {
        return waitTime;
    }

    public void setWaitTime(String waitTime) {
        this.waitTime = waitTime;
    }

    public String getBaseFare() {
        return baseFare;
    }

    public void setBaseFare(String baseFare) {
        this.baseFare = baseFare;
    }

    public Double getTripFare() {
        return tripFare;
    }

    public void setTripFare(Double tripFare) {
        this.tripFare = tripFare;
    }

    public Integer getTaxAmt() {
        return taxAmt;
    }

    public void setTaxAmt(Integer taxAmt) {
        this.taxAmt = taxAmt;
    }

    public String getServiceTax() {
        return serviceTax;
    }

    public void setServiceTax(String serviceTax) {
        this.serviceTax = serviceTax;
    }

    public Integer getServiceAmt() {
        return serviceAmt;
    }

    public void setServiceAmt(Integer serviceAmt) {
        this.serviceAmt = serviceAmt;
    }

    public Integer getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(Integer subTotal) {
        this.subTotal = subTotal;
    }

    public Integer getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(Integer paidAmount) {
        this.paidAmount = paidAmount;
    }

    public Integer getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(Integer discountPercent) {
        this.discountPercent = discountPercent;
    }

    public Integer getDiscount() {
        return discount;
    }

    public void setDiscount(Integer discount) {
        this.discount = discount;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getRouteImg() {
        return routeImg;
    }

    public void setRouteImg(String routeImg) {
        this.routeImg = routeImg;
    }

    public String getFavStatus() {
        return favStatus;
    }

    public void setFavStatus(String favStatus) {
        this.favStatus = favStatus;
    }

    public List<ModelCurrentBookingResult> getResult() {
        return result;
    }

    public void setResult(List<ModelCurrentBookingResult> result) {
        this.result = result;
    }

    public String getMap() {
        return map;
    }

    public void setMap(String map) {
        this.map = map;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDiffSecond() {
        return diffSecond;
    }

    public void setDiffSecond(String diffSecond) {
        this.diffSecond = diffSecond;
    }

    public List<Object> getBookingDropoff() {
        return bookingDropoff;
    }

    public void setBookingDropoff(List<Object> bookingDropoff) {
        this.bookingDropoff = bookingDropoff;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

}
