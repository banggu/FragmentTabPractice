package com.scnu.bangzhu.fragmenttabpractice.util;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

/**
 * Created by bangzhu on 2016/5/16.
 */
public class DeviceUtil {
    /**
     *  获取设备id
     */
    public static String getDeviceId(Context context){
        TelephonyManager tm = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
        String deviceId = tm.getDeviceId();
        //
        if(deviceId == null || TextUtils.isEmpty(deviceId.trim())){
            deviceId = Settings.System.getString(context.getContentResolver(), "DEVICE_ID");
        }
        if(deviceId == null || TextUtils.isEmpty(deviceId.trim())){
            deviceId = "android_" + System.currentTimeMillis();

        }
        return deviceId;
    }

    /**
     *  获取手机型号
     */
    public static String getPhoneType(){
        return Build.MODEL;
    }

    /**
     *  获取SDK版本
     */
    public static int getSdkVersion(){
        return Build.VERSION.SDK_INT;
    }

    /**
     *  获取系统版本
     */
    public static String getSystemVersion(){
        return Build.VERSION.RELEASE;
    }
}
