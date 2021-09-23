package com.amanahdelivery.taxi.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import com.amanahdelivery.R;

public class TrackTaxiAct extends AppCompatActivity {

    Context mContext = TrackTaxiAct.this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_taxi);
    }


}