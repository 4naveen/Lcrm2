package com.project.lorvent.lcrm.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import static android.content.Context.CONNECTIVITY_SERVICE;

/**
 * Created by Guest on 2/10/2017.
 */

public class NetworkStatus {

    public static boolean isConnected(Context context) {
        ConnectivityManager manager = (ConnectivityManager)context.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            return true;
        } else {
            return false;

        }
    }

}
