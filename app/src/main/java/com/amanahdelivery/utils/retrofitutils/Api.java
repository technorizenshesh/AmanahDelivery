package com.amanahdelivery.utils.retrofitutils;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface Api {

    @FormUrlEncoded
    @POST("change_password")
    Call<ResponseBody> changePass(@FieldMap Map<String, String> params);

    @POST("get_support")
    Call<ResponseBody> getSupportApi();

    @FormUrlEncoded
    @POST("get_my_order")
    Call<ResponseBody> myOrderApiCall(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("update_lat_lon")
    Call<Map<String, String>> updateLocation(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("forgot_password")
    Call<ResponseBody> forgotPass(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("update_status")
    Call<ResponseBody> updateOnOffApi(@FieldMap Map<String, String> params);

    @POST("get_category")
    Call<ResponseBody> getShopCategory();

    @POST("get_car_list")
    Call<ResponseBody> getCarList();

    @FormUrlEncoded
    @POST("devfood_login")
    Call<ResponseBody> loginApiCall(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("get_dev_order")
    Call<ResponseBody> getDevOrdersApiCall(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("driver_accept_and_Cancel_request")
    Call<ResponseBody> acceptCancelOrderCall(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("get_current_booking")
    Call<ResponseBody> getCurrentTaxiBooking(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("accept_and_Cancel_request")
    Call<ResponseBody> acceptCancelOrderCallTaxi(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("place_order")
    Call<ResponseBody> placeDevOrderApiCall(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("get_shop_items")
    Call<ResponseBody> getAllShopItems(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("delete_shop_items")
    Call<ResponseBody> deleteShopItems(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("delete_cart_data")
    Call<ResponseBody> deleteCartItems(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("signup")
    Call<ResponseBody> signUpApiCall(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("get_allshop_list_bycategory")
    Call<ResponseBody> getAllShopApiCall(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("delete_cart")
    Call<ResponseBody> deleteStoreItemApiCall(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("get_cart")
    Call<ResponseBody> getStoreCartApiCall(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("add_to_cart")
    Call<ResponseBody> addToCartApiCall(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("add_to_cart")
    Call<ResponseBody> updateOrderStatusApiCall(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("get_count_cart")
    Call<ResponseBody> getCartCountApiCall(@FieldMap Map<String, String> params);

    @Multipart
    @POST("add_driver_documents")
    Call<ResponseBody> addDriverDocumentApiCall(@Part("user_id") RequestBody user_id,
                                                @Part MultipartBody.Part file1,
                                                @Part MultipartBody.Part file2,
                                                @Part MultipartBody.Part file3,
                                                @Part MultipartBody.Part file4);

    @Multipart
    @POST("add_shop_items")
    Call<ResponseBody> addShopsItems(@Part("user_id") RequestBody shop_id,
                                     @Part("item_name") RequestBody item_name,
                                     @Part("item_price") RequestBody item_price,
                                     @Part("item_description") RequestBody item_description,
                                     @Part MultipartBody.Part file1);

}


