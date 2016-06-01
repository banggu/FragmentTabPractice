package com.scnu.bangzhu.fragmenttabpractice.util;

/**
 * Created by bangzhu on 2016/5/16.
 */
public class UrlUtil {
    public static final String BASE_URL = "http://120.25.127.46/penderie/p/";
    /**
     *  获取完整的URL地址
     */
    public static String getUrlString(String getMethod, String deviceId){
        String url = UrlUtil.BASE_URL+getMethod+"?"+"deviceId="+deviceId;
        return url;
    }
}
