package com.amanahdelivery.deliveryshops.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.amanahdelivery.R;
import com.amanahdelivery.databinding.AdapterSupportsBinding;
import com.amanahdelivery.models.ModelSupport;
import com.amanahdelivery.utils.ProjectUtil;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class AdapterSupports extends RecyclerView.Adapter<AdapterSupports.SupportView> {

    Context mContext;
    ArrayList<ModelSupport.Result> supportList;

    public AdapterSupports(Context mContext, ArrayList<ModelSupport.Result> supportList) {
        this.mContext = mContext;
        this.supportList = supportList;
    }

    @NonNull
    @NotNull
    @Override
    public SupportView onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        AdapterSupportsBinding binding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.adapter_supports,parent,false);
        return new SupportView(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull SupportView holder, int position) {
        ModelSupport.Result data = supportList.get(position);
        holder.binding.setData(data);

        holder.binding.ivSend.setOnClickListener(v -> {
            if(ProjectUtil.isValidEmail(data.getText().trim())) {
                ProjectUtil.sendEmail(mContext,data.getText());
            } else {
                ProjectUtil.call(mContext,data.getText());
            }
        });

    }

    @Override
    public int getItemCount() {
        return supportList == null?0:supportList.size();
    }

    public class SupportView extends RecyclerView.ViewHolder {

        AdapterSupportsBinding binding;

        public SupportView(AdapterSupportsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

    }


}
