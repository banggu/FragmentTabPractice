package com.scnu.bangzhu.fragmenttabpractice.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import com.scnu.bangzhu.fragmenttabpractice.R;
import com.scnu.bangzhu.fragmenttabpractice.adapter.RecyclerViewShowCloseAdapter;
import com.scnu.bangzhu.fragmenttabpractice.model.Close;
import com.scnu.bangzhu.fragmenttabpractice.util.DeviceUtil;
import com.scnu.bangzhu.fragmenttabpractice.util.HttpCallbackListener;
import com.scnu.bangzhu.fragmenttabpractice.util.HttpUtil;
import com.scnu.bangzhu.fragmenttabpractice.util.JSONUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bangzhu on 2016/6/4.
 */
public class IndexActivity extends Activity implements SwipeRefreshLayout.OnRefreshListener{
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private RecyclerViewShowCloseAdapter adapter;
    private StaggeredGridLayoutManager mLayoutManager;
    private List<Close> closeList;
    private String deviceId;
    private String baseUrl = "http://120.25.127.46/penderie/p/getClose?deviceId=865983023786230&pageNumber=";
    private int pageNumber=1;
    private int lastVisibleView = 0;
    private int firstVisibleView = 0,lastVisiblePosition = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_close);
        initView();
        setContents();
        setListeners();
    }

    private void initView() {
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeLayout);
        recyclerView = (RecyclerView) findViewById(R.id.rvShowClose);
        closeList = new ArrayList<>();
        mLayoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
    }

    private void setContents() {
        // 第一次进入页面的时候显示加载进度条
        swipeRefreshLayout.setProgressViewOffset(false, 0, (int) TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources()
                        .getDisplayMetrics()));
        swipeRefreshLayout.setColorSchemeResources(R.color.swipe_scheme_color);
        swipeRefreshLayout.setSize(SwipeRefreshLayout.LARGE);
        swipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.swipe_bg_color);
        swipeRefreshLayout.setProgressViewEndTarget(true, 200);
        //设置layoutManager
//        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setLayoutManager(mLayoutManager);
        adapter = new RecyclerViewShowCloseAdapter(this, closeList);
        recyclerView.setAdapter(adapter);

        if(deviceId == null){
            deviceId = DeviceUtil.getDeviceId(IndexActivity.this);
        }
        String address = baseUrl + pageNumber;
        getClose(address);
    }

    private void setListeners() {
        swipeRefreshLayout.setOnRefreshListener(this);
        recyclerView.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                //滚动到第一行或最后一行
                if(newState == RecyclerView.SCROLL_STATE_IDLE && (2*(lastVisibleView+1)) == adapter.getItemCount()){
                    swipeRefreshLayout.setRefreshing(true);
                    //上滑滚动时
                    if(firstVisibleView > lastVisiblePosition){
                        String address = baseUrl + (pageNumber+1);
                        getClose(address);
                        swipeRefreshLayout.setRefreshing(false);
                    }
                    lastVisiblePosition = firstVisibleView;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int[] last = mLayoutManager.findLastVisibleItemPositions(new int[2]);
                lastVisibleView = last[0]/2;
                int[] first = mLayoutManager.findFirstVisibleItemPositions(new int[2]);
                firstVisibleView = first[0]/2;
            }
        });
    }

    private void getClose(String address) {
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                try {
                    JSONObject obj = new JSONObject(response);
                    JSONObject msg = obj.optJSONObject("msg");
                    pageNumber = msg.getInt("pageNumber");
                    JSONArray jsonArray = msg.optJSONArray("close");
                    if (pageNumber == 1) {
                        closeList.clear();
                    } else {
                    }
                    if (jsonArray != null) {
                        closeList.addAll(JSONUtil.getJsonList(jsonArray.toString(), Close.class));
                        adapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Exception e) {

            }
        });
    }

    @Override
    public void onRefresh() {
        String address = baseUrl + pageNumber;
        getClose(address);
        swipeRefreshLayout.setRefreshing(false);
    }
}
