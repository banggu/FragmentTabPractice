package com.scnu.bangzhu.fragmenttabpractice.util;

/**
 * Created by bangzhu on 2016/5/16.
 */
public interface HttpCallbackListener {
    void onFinish(String response);
    void onError(Exception e);
}
