package com.scnu.bangzhu.fragmenttabpractice.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.scnu.bangzhu.fragmenttabpractice.R;
import com.scnu.bangzhu.fragmenttabpractice.activity.MainActivity;
import com.scnu.bangzhu.fragmenttabpractice.model.Close;
import com.scnu.bangzhu.fragmenttabpractice.util.AsyncBitmapLoader;
import com.scnu.bangzhu.fragmenttabpractice.util.AsyncBitmapLoader1;
import com.scnu.bangzhu.fragmenttabpractice.util.LoadImageCallBackListener;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by bangzhu on 2016/5/16.
 */
public class ShowCloseAdapter extends BaseAdapter {
    Context context;
    int screenW;
    int imgW;
    private List<Close> list;
    private LayoutInflater mInflater;
//    private ImageLoader imageLoader;
    private AsyncBitmapLoader1 asyncBitmapLoader;
	
    public ShowCloseAdapter(Context context, List<Close> list){
        this.context = context;
        this.list = list;
        mInflater = LayoutInflater.from(context);
        asyncBitmapLoader = new AsyncBitmapLoader1(context);
    }
    public void setData(List<Close> data){
        this.list = data;
        notifyDataSetChanged();
    }
    public List<Close> getData(){
        return list;
    }
    @Override
    public int getCount() {
        return list.size();
    }


    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(convertView == null){
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.show_close_item, null);
            holder.ivCloseImg = (ImageView) convertView.findViewById(R.id.ivCloseImg);
            holder.tvCloseName = (TextView) convertView.findViewById(R.id.tvCloseName);
            holder.tvClosePrice = (TextView) convertView.findViewById(R.id.tvClosePrice);
            convertView.setTag(holder);
        } else{
            holder = (ViewHolder) convertView.getTag();
        }
        Close close = list.get(position);
        if(screenW == 0){
            screenW = ((MainActivity)context).getWindowManager().getDefaultDisplay().getWidth();
            imgW = screenW/6;
        }
        LinearLayout.LayoutParams imgParams = (LinearLayout.LayoutParams) holder.ivCloseImg.getLayoutParams();
        imgParams.width = imgW;
        imgParams.height = imgW;
        imgParams.topMargin = 10;
        holder.ivCloseImg.setLayoutParams(imgParams);
//        Picasso.with(context).load(close.peImg).resize(imgW, imgW).centerCrop().into(holder.ivCloseImg);
//        Picasso.with(context).load(close.peImg).into(holder.ivCloseImg);
//        imageLoader = ImageLoader.getInstance();
//        imageLoader.displayImage(close.peImg, holder.ivCloseImg);
        holder.ivCloseImg.setTag(close.peImg);
        asyncBitmapLoader.showBitmapFromUrl(holder.ivCloseImg, close.peImg); ;
        holder.tvCloseName.setText(close.name);
        holder.tvClosePrice.setText(close.price+"");
        return convertView;
    }

    public void refreshCloseList(List<Close> closeList){

    }

    public final class ViewHolder{
        public ImageView ivCloseImg;
        public TextView tvCloseName;
        public TextView tvClosePrice;
    }
}
