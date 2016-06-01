package com.scnu.bangzhu.fragmenttabpractice.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.scnu.bangzhu.fragmenttabpractice.R;

/**
 * Created by bangzhu on 2016/5/16.
 */
public class NotificationFragment extends Fragment {
    View rootView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(null != rootView){
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if(null != parent){
                parent.removeView(rootView);
            }
        }else{
            rootView = inflater.inflate(R.layout.fragment_notification, container, false);
        }
        return rootView;
    }

}
