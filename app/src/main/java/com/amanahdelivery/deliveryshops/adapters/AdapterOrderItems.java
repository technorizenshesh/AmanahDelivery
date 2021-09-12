package com.amanahdelivery.deliveryshops.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.amanahdelivery.R;
import com.amanahdelivery.databinding.AdapterItemDetailsBinding;
import com.amanahdelivery.models.ModelStoreOrders;
import com.amanahdelivery.utils.AppConstant;
import com.amanahdelivery.utils.ProjectUtil;
import com.bumptech.glide.Glide;
import java.util.ArrayList;

public class AdapterOrderItems extends RecyclerView.Adapter<AdapterOrderItems.StoreHolder> {

    Context mContext;
    ArrayList<ModelStoreOrders.Result.Cart_list> cartList;

    public AdapterOrderItems(Context mContext, ArrayList<ModelStoreOrders.Result.Cart_list> cartList) {
        this.mContext = mContext;
        this.cartList = cartList;
    }

    @NonNull
    @Override
    public AdapterOrderItems.StoreHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AdapterItemDetailsBinding binding = DataBindingUtil
                .inflate(LayoutInflater.from(mContext),R.layout.adapter_item_details,parent,false);
        return new StoreHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterOrderItems.StoreHolder holder, int position) {
        ModelStoreOrders.Result.Cart_list data = cartList.get(position);

        holder.binding.tvName.setText(data.getItem_name());
        holder.binding.tvPrice.setText(AppConstant.CURRENCY +" "+ data.getItem_price() +" x "+ data.getQuantity());

        Glide.with(mContext).load(data.getItem_image()).into(holder.binding.ivImage);

        holder.binding.ivImage.setOnClickListener(v -> {
            ProjectUtil.imageShowFullscreenDialog(mContext,data.getItem_image());
        });

    }

    @Override
    public int getItemCount() {
        return cartList == null?0:cartList.size();
    }

    public class StoreHolder extends RecyclerView.ViewHolder{

        AdapterItemDetailsBinding binding;

        public StoreHolder(AdapterItemDetailsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

    }


}
