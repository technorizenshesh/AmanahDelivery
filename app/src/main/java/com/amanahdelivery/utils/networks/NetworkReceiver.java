package com.amanahdelivery.utils.networks;

import static com.amanahdelivery.utils.networks.NetworkUtil.NOT_CONNECT;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


public class NetworkReceiver extends BroadcastReceiver {

    public static ConnectivityReceiverListener connectivityReceiverListener;
    public static Boolean connect = true;

    public NetworkReceiver() {
        super();
    }

    public static boolean isConnected() {
        return connect;
    }

    @Override
    public void onReceive(Context context, Intent arg1) {

        String status = NetworkUtil.getConnectivityStatusString(context);
        if (status.equals(NOT_CONNECT)) {
            Log.e("Receiver ", "not connection");// your code when internet lost
            connect = false;
        } else
            connect = true;

        if (connectivityReceiverListener != null) {
            connectivityReceiverListener.onNetworkConnectionChanged(connect);
        }
    }


    public interface ConnectivityReceiverListener {
        void onNetworkConnectionChanged(boolean isConnected);
    }
}
