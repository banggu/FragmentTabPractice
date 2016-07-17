package com.scnu.bangzhu.fragmenttabpractice.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by bangzhu on 2016/7/16.
 */
public class NetWorkUtil {
    public static boolean checkNetWorkConnection(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netWorkInfo = connectivityManager.getActiveNetworkInfo();
        return netWorkInfo != null && netWorkInfo.isConnected();
    }
}
