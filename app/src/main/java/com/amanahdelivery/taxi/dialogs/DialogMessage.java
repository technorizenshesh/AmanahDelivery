package com.amanahdelivery.taxi.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;

import com.amanahdelivery.R;
import com.amanahdelivery.databinding.DialogMessageBinding;

public class DialogMessage extends Dialog {

    private DialogMessageBinding binding;
    private String message = "";
    private onMessageDialogListener listener;

    public static DialogMessage get(Context context) {
        return new DialogMessage(context);
    }

    public DialogMessage(Context context) {
        super(context);
    }

    public interface onMessageDialogListener {
        void onClickSuccess();
    }

    public DialogMessage Callback(onMessageDialogListener listener) {
        this.listener = listener;
        return this;
    }

    public DialogMessage setMessage(String message) {
        this.message = message;
        return this;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.dialog_message, null, false);
        setContentView(binding.getRoot());
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        setCanceledOnTouchOutside(true);
        binding.tvMessage.setText(message);
        binding.tvCancel.setOnClickListener(v->dismiss());
        binding.tvOk.setOnClickListener(v -> {
            if (listener!=null) {
                listener.onClickSuccess();
            }
            dismiss();
        });
    }
}
