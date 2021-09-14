package com.amanahdelivery.deliveryshops.adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.amanahdelivery.R;
import com.amanahdelivery.databinding.AdapterFoodDevOrdersBinding;
import com.amanahdelivery.databinding.FoodOrderDetailDialogBinding;
import com.amanahdelivery.deliveryshops.activities.TrackShopAct;
import com.amanahdelivery.models.ModelLogin;
import com.amanahdelivery.models.ModelStoreOrders;
import com.amanahdelivery.utils.AppConstant;
import com.amanahdelivery.utils.ProjectUtil;
import com.amanahdelivery.utils.SharedPref;
import com.amanahdelivery.utils.retrofitutils.Api;
import com.amanahdelivery.utils.retrofitutils.ApiFactory;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdapterStoreOrders extends RecyclerView.Adapter<AdapterStoreOrders.StoreHolder> {

    Context mContext;
    ArrayList<ModelStoreOrders.Result> storeList;
    SharedPref sharedPref;
    ModelLogin modelLogin;

    public AdapterStoreOrders(Context mContext, ArrayList<ModelStoreOrders.Result> storeList) {
        this.mContext = mContext;
        this.storeList = storeList;
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);
    }

    @NonNull
    @Override
    public AdapterStoreOrders.StoreHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AdapterFoodDevOrdersBinding binding = DataBindingUtil
                .inflate(LayoutInflater.from(mContext), R.layout.adapter_food_dev_orders, parent, false);
        return new StoreHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterStoreOrders.StoreHolder holder, int position) {

        ModelStoreOrders.Result data = storeList.get(position);

        Log.e("sfdsfxdsfdsf", "data.getStatus() = " + data.getStatus());

        holder.binding.setOrder(data);

        holder.binding.btItemsDetail.setOnClickListener(v -> {
            openOrderDetailDialog(data);
        });

        holder.binding.btNavigate.setOnClickListener(v -> {
            mContext.startActivity(new Intent(mContext, TrackShopAct.class)
                    .putExtra("storelat", Double.parseDouble(data.getShop_lat()))
                    .putExtra("storelon", Double.parseDouble(data.getShop_lon()))
                    .putExtra("custlat", Double.parseDouble(data.getLat()))
                    .putExtra("custlon", Double.parseDouble(data.getLon()))
                    .putExtra("status", data.getStatus())
                    .putExtra("orderId", data.getId())
                    .putExtra("orderIdScan", data.getOrder_id())
                    .putExtra("code", data.getQr_code())
            );
        });

    }

    private void openOrderDetailDialog(ModelStoreOrders.Result data) {
        Dialog dialog = new Dialog(mContext, WindowManager.LayoutParams.MATCH_PARENT);
        FoodOrderDetailDialogBinding dialogBinding = DataBindingUtil
                .inflate(LayoutInflater.from(mContext), R.layout.food_order_detail_dialog, null, false);
        dialog.setContentView(dialogBinding.getRoot());

        dialogBinding.itemsTotal.setText(AppConstant.CURRENCY + " " + data.getAmount());
        dialogBinding.tvDevAddress.setText(data.getAddress());
        dialogBinding.tvStoreAddress.setText(data.getShop_address());
        dialogBinding.tvOrderId.setText(data.getOrder_id());
        dialogBinding.payType.setText(data.getPayment_type());

        if(data.getName() == null || data.getName().equals("")){
            dialogBinding.cusName.setText(data.getUser_name());
        } else {
            dialogBinding.cusName.setText(data.getName());
        }

        dialogBinding.ivCall.setOnClickListener(v -> {
            ProjectUtil.callCustomer(mContext,data.getUser_mobile());
        });

        AdapterOrderItems adapterOrderItems = new AdapterOrderItems(mContext, data.getCart_list());
        dialogBinding.rvItems.setAdapter(adapterOrderItems);

        dialogBinding.ivBack.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.show();
    }

    private void acceptOrderApi(String id, int position) {

        ProjectUtil.showProgressDialog(mContext, false, mContext.getString(R.string.please_wait));
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);

        HashMap<String, String> paramsHash = new HashMap<>();
        paramsHash.put("status", "Accept");
        paramsHash.put("order_id", id);
        paramsHash.put("driver_id", modelLogin.getResult().getId());

        Call<ResponseBody> call = api.updateOrderStatusApiCall(paramsHash);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    if (jsonObject.getString("status").equals("1")) {

                        Log.e("hfdsfsdfdsf", "responseString = " + responseString);
                        storeList.get(position).setStatus("Accept");
                        notifyDataSetChanged();

                    } else {
                        // Toast.makeText(mContext, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    // Toast.makeText(mContext, "Exception = " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Exception", "Exception = " + e.getMessage());
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
            }

        });


    }

    @Override
    public int getItemCount() {
        return storeList == null ? 0 : storeList.size();
    }

    public class StoreHolder extends RecyclerView.ViewHolder {

        AdapterFoodDevOrdersBinding binding;

        public StoreHolder(AdapterFoodDevOrdersBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

    }


}
