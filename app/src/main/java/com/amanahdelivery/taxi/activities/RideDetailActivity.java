package com.amanahdelivery.taxi.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.os.Bundle;

import com.amanahdelivery.R;
import com.amanahdelivery.databinding.ActivityRideDetailBinding;
import com.amanahdelivery.taxi.models.ModelTaxiHistory;

public class RideDetailActivity extends AppCompatActivity {

    Context mContext = RideDetailActivity.this;
    ActivityRideDetailBinding binding;
    ModelTaxiHistory.Result result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_ride_detail);
        result = (ModelTaxiHistory.Result) getIntent().getSerializableExtra("data");
        itit();
    }

    private void itit() {

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

        binding.setData(result);

    }


}