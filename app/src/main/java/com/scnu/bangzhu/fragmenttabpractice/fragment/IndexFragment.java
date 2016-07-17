package com.scnu.bangzhu.fragmenttabpractice.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.scnu.bangzhu.fragmenttabpractice.R;
import com.scnu.bangzhu.fragmenttabpractice.adapter.ShowCloseAdapter;
import com.scnu.bangzhu.fragmenttabpractice.model.Close;
import com.scnu.bangzhu.fragmenttabpractice.util.DeviceUtil;
import com.scnu.bangzhu.fragmenttabpractice.util.HttpCallbackListener;
import com.scnu.bangzhu.fragmenttabpractice.util.HttpUtil;
import com.scnu.bangzhu.fragmenttabpractice.util.JSONUtil;
import com.scnu.bangzhu.fragmenttabpractice.util.LoadCloseTask;
import com.scnu.bangzhu.fragmenttabpractice.util.NetWorkUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bangzhu on 2016/5/16.
 */
public class IndexFragment extends Fragment implements View.OnClickListener,SwipeRefreshLayout.OnRefreshListener{
    private View rootView;
    private ListView lvShowClose;
    private TextView tvNull;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String deviceId = null;
    private ShowCloseAdapter showCloseAdapter = null;
    private List<Close> closeList = new ArrayList<Close>();
    private int count, pageNumber=1;
    private int lastVisiblePosition = 0;
    private String baseUrl = "http://120.25.127.46/penderie/p/getClose?deviceId=865983023786230&pageNumber=";
    private boolean isConnection;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(null != rootView){
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if(null != parent){
                parent.removeView(rootView);
            }
        }else{
            rootView = inflater.inflate(R.layout.fragment_show_close, container, false);
            findView();
            setContents();
            setListeners();
        }
        return rootView;
    }

    public void findView(){
        lvShowClose = (ListView) rootView.findViewById(R.id.lvShowClose);
        tvNull = (TextView) rootView.findViewById(R.id.tvNull);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeLayout);
    }

    public void setContents(){
        swipeRefreshLayout.setColorSchemeResources(R.color.swipe_scheme_color);
        swipeRefreshLayout.setSize(SwipeRefreshLayout.LARGE);
        swipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.swipe_bg_color);
        swipeRefreshLayout.setProgressViewOffset(false, 0, 24);
        swipeRefreshLayout.setProgressViewEndTarget(true, 200);
        lvShowClose.setEmptyView(tvNull);
        showCloseAdapter = new ShowCloseAdapter(getActivity(), closeList);
        lvShowClose.setAdapter(showCloseAdapter);
        isConnection = NetWorkUtil.checkNetWorkConnection(getActivity());

        if(deviceId == null){
            deviceId = DeviceUtil.getDeviceId(getActivity().getBaseContext());
        }
        String address = baseUrl + pageNumber;
        if(isConnection){
            getClose(address);
        }else{
            Toast.makeText(getActivity(), "无网络连接", Toast.LENGTH_LONG).show();
        }
    }

    private void getClose(String address) {
        new LoadCloseTask(address, showCloseAdapter, closeList, new LoadCloseTask.OnFinishListener() {
            @Override
            public void onFinishTask(int pageNum) {
                pageNumber =  pageNum;
            }
        }).execute();
//        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
//            @Override
//            public void onFinish(String response) {
//                try {
//                    JSONObject obj = new JSONObject(response);
//                    JSONObject msg = obj.optJSONObject("msg");
//                    pageNumber = msg.getInt("pageNumber");
//                    JSONArray jsonArray = msg.optJSONArray("close");
//                    count = msg.optInt("count");
//                    if (pageNumber == 1) {
//                        closeList.clear();
//                    } else {
//                    }
//                    if (jsonArray != null) {
//                        closeList.addAll(JSONUtil.getJsonList(jsonArray.toString(), Close.class));
//                        showCloseAdapter.notifyDataSetChanged();
//                    }
//                    tvNull.setVisibility(View.GONE);
//                    if (pageNumber == 1 && closeList.size() <= 0) {
//                        tvNull.setVisibility(View.VISIBLE);
//                        tvNull.setText("无网络连接。。。");
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onError(Exception e) {
//                tvNull.setText("网络请求出错");
//            }
//        });
    }

    public void setListeners(){
        tvNull.setOnClickListener(this);
        lvShowClose.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                //当ListView滑到底部时
                if (firstVisibleItem + visibleItemCount == totalItemCount && totalItemCount > 0) {
                    //上滑ListView加载下一页数据
                    if (firstVisibleItem > lastVisiblePosition) {
                        String address = baseUrl + (pageNumber + 1);
                        swipeRefreshLayout.setRefreshing(true);
                        getClose(address);
                        swipeRefreshLayout.setRefreshing(false);
                    }
                    lastVisiblePosition = firstVisibleItem;
                }
            }
        });
        swipeRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.tvNull :
                getClose("http://120.25.127.46/penderie/p/getClose?deviceId=865983023786230");
                break;
        }
    }

    @Override
    public void onRefresh() {
        String address = baseUrl + pageNumber;
        if(isConnection){
            getClose(address);
            swipeRefreshLayout.setRefreshing(false);
        }else{
            Toast.makeText(getActivity(), "无网络连接", Toast.LENGTH_LONG).show();
            swipeRefreshLayout.setRefreshing(false);
        }
    }
}
