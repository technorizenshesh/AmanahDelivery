package com.amanahdelivery.taxi.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.amanahdelivery.R;
import com.amanahdelivery.databinding.AdapterTaxiHistoryBinding;
import com.amanahdelivery.taxi.activities.RideDetailActivity;
import com.amanahdelivery.taxi.models.ModelTaxiHistory;
import com.amanahdelivery.utils.ProjectUtil;

import java.util.ArrayList;

public class AdapterRideHistory extends RecyclerView.Adapter<AdapterRideHistory.RideHolder> {

    Context mContext;
    ArrayList<ModelTaxiHistory.Result> historyList;

    public AdapterRideHistory(Context mContext, ArrayList<ModelTaxiHistory.Result> historyList) {
        this.mContext = mContext;
        this.historyList = historyList;
    }

    @NonNull
    @Override
    public RideHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AdapterTaxiHistoryBinding binding = DataBindingUtil.inflate(LayoutInflater.from(mContext),
                R.layout.adapter_taxi_history, parent, false);
        return new RideHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RideHolder holder, int position) {

        holder.binding.setData(historyList.get(position));

        if (historyList.get(position).getStatus().equals("Cancel_by_user") || historyList.get(position).getStatus().equals("Cancel"))
            holder.binding.tvStatus.setTextColor(ContextCompat.getColor(mContext, R.color.red_spalsh));

        holder.binding.tvStatus.setText(historyList.get(position)
                .getStatus().replace("_", " ").toUpperCase());

        holder.binding.ivDetails.setOnClickListener(v -> {
            ProjectUtil.blinkAnimation(holder.binding.ivDetails);
            mContext.startActivity(new Intent(mContext, RideDetailActivity.class)
                    .putExtra("data", historyList.get(position))
            );
        });

    }

    @Override
    public int getItemCount() {
        return historyList == null ? 0 : historyList.size();
    }

    public class RideHolder extends RecyclerView.ViewHolder {

        AdapterTaxiHistoryBinding binding;

        public RideHolder(AdapterTaxiHistoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

    }

}
