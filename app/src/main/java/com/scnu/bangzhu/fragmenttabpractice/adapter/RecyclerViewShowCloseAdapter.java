package com.scnu.bangzhu.fragmenttabpractice.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.scnu.bangzhu.fragmenttabpractice.R;
import com.scnu.bangzhu.fragmenttabpractice.activity.IndexActivity;
import com.scnu.bangzhu.fragmenttabpractice.activity.MainActivity;
import com.scnu.bangzhu.fragmenttabpractice.model.Close;
import com.scnu.bangzhu.fragmenttabpractice.util.AsyncBitmapLoader1;

import java.util.List;

/**
 * Created by bangzhu on 2016/6/4.
 */
public class RecyclerViewShowCloseAdapter extends RecyclerView.Adapter{
    private Context mContex;
    private List<Close> mCloseList;
    private AsyncBitmapLoader1 asyncBitmapLoader1;
    private int screenW = 0, imgW=0;
    private  int imgDistance = 0;
    public RecyclerViewShowCloseAdapter(Context context, List<Close> closeList){
        mContex = context;
        mCloseList = closeList;
        asyncBitmapLoader1 = new AsyncBitmapLoader1(mContex);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.show_close_onlyimage_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        Close close = mCloseList.get(i);
        ViewHolder vh = (ViewHolder) viewHolder;
        ImageView imgClose =  vh.getImageView();
        if (screenW == 0 || imgW == 0) {
            screenW = ((IndexActivity) mContex).getWindowManager().getDefaultDisplay().getWidth();
            imgDistance = (int) mContex.getResources().getDimension(R.dimen.index_close_distance);
            imgW = screenW / 2 - imgDistance - imgDistance / 2;
        }
        int imgH = imgW * close.h / close.w;

        LinearLayout.LayoutParams imgPparams = (LinearLayout.LayoutParams) imgClose.getLayoutParams();
        imgPparams.width = imgW;
        imgPparams.height = imgH;
        imgPparams.bottomMargin = (int) (imgDistance / 1.5);
        imgPparams.topMargin = (int) (imgDistance / 1.5);
        if (i % 2 == 0) {
            imgPparams.leftMargin = imgDistance;
            imgPparams.rightMargin = imgDistance / 2;
        } else {
            imgPparams.leftMargin = imgDistance / 2;
            imgPparams.rightMargin = imgDistance;
        }
        imgClose.setLayoutParams(imgPparams);
        imgClose.setTag(close.peImg);
        asyncBitmapLoader1.showBitmapFromUrl(imgClose, close.peImg);
    }

    @Override
    public int getItemCount() {
        return mCloseList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView imageView;
        public ViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.ivClosePic);
        }
        public ImageView getImageView(){
            return imageView;
        }
    }
}
