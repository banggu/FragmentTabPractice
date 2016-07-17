package com.scnu.bangzhu.fragmenttabpractice.util;

import android.os.AsyncTask;

import com.scnu.bangzhu.fragmenttabpractice.adapter.ShowCloseAdapter;
import com.scnu.bangzhu.fragmenttabpractice.model.Close;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bangzhu on 2016/7/16.
 */
public class LoadCloseTask extends AsyncTask<Void,Void, List<Close>>{
    private ShowCloseAdapter mAdapter;
    private OnFinishListener mListener;
    private String mUrl;
    private int mPageNum;
    private List<Close> mCloseList;

    public LoadCloseTask(String url, ShowCloseAdapter adapter, List<Close> closeList){
        super();
        mUrl = url;
        mAdapter = adapter;
        mCloseList = closeList;
    }

    public LoadCloseTask(String url, ShowCloseAdapter adapter, List<Close> closeList, OnFinishListener listener){
        super();
        mUrl = url;
        mAdapter = adapter;
        mCloseList = closeList;
        mListener = listener;

    }

    @Override
    protected List<Close> doInBackground(Void... params) {
        try {

            String response = HttpUtil.get(mUrl);
            JSONObject obj = new JSONObject(response);
            JSONObject msg = obj.optJSONObject("msg");
            mPageNum = msg.getInt("pageNumber");
            JSONArray jsonArray = msg.optJSONArray("close");
            if (jsonArray != null) {
                if(mPageNum == 1){
                    mCloseList.clear();
                }
                mCloseList.addAll(JSONUtil.getJsonList(jsonArray.toString(), Close.class));
            }
            return mCloseList;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<Close> closes) {
        mAdapter.notifyDataSetChanged();
        if(mListener != null){
            mListener.onFinishTask(mPageNum);
        }
    }

    public interface OnFinishListener{
        public void onFinishTask(int pageNum);
    }
}
