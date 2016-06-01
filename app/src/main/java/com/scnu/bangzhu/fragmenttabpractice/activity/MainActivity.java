package com.scnu.bangzhu.fragmenttabpractice.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.scnu.bangzhu.fragmenttabpractice.R;
import com.scnu.bangzhu.fragmenttabpractice.adapter.MyFragmentPagerAdapter;
import com.scnu.bangzhu.fragmenttabpractice.adapter.MyFragmentStatePagerAdapter;
import com.scnu.bangzhu.fragmenttabpractice.fragment.IndexFragment;
import com.scnu.bangzhu.fragmenttabpractice.fragment.NotificationFragment;
import com.scnu.bangzhu.fragmenttabpractice.fragment.UserInfoFragment;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends FragmentActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {

    private IndexFragment indexFragment = null;
    private NotificationFragment notificationFragment = null;
    private UserInfoFragment userInfoFragment = null;
    private LinearLayout llClose, llNotification, llMy;
    private TextView tvTitleName;
    private ImageView ivClose, ivNotification, ivMy;
    private FragmentManager fManager;
    private ViewPager viewPager;
    private MyFragmentPagerAdapter myFragmentPagerAdapter;
    private MyFragmentStatePagerAdapter myFragmentStatePagerAdapter;
    private List<Fragment> fragmentList;

    //页面状态
    public static final int PAGE_ONE = 0;
    public static final int PAGE_TWO = 1;
    public static final int PAGE_THREE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        fManager = getSupportFragmentManager();
        bindView();
        setContents();
        setListeners();
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        FragmentTransaction fTransaction = fManager.beginTransaction();
//        if(indexFragment == null){
//            indexFragment = new IndexFragment();
//            fTransaction.add(R.id.flContent, indexFragment);
//        }
//        fTransaction.commit();
//    }

    //初始化控件
    public void bindView(){
        llClose = (LinearLayout) findViewById(R.id.llClose);
        llNotification = (LinearLayout) findViewById(R.id.llNotification);
        llMy = (LinearLayout) findViewById(R.id.llMy);
        tvTitleName = (TextView) findViewById(R.id.tvTitleName);
        ivClose = (ImageView) findViewById(R.id.ivClose);
        ivNotification = (ImageView) findViewById(R.id.ivNotification);
        ivMy = (ImageView) findViewById(R.id.ivMy);
        viewPager = (ViewPager) findViewById(R.id.vpContent);

        fragmentList = new ArrayList<Fragment>();
    }

    private void setContents() {
        indexFragment = new IndexFragment();
        notificationFragment = new NotificationFragment();
        userInfoFragment = new UserInfoFragment();
        fragmentList.add(indexFragment);
        fragmentList.add(notificationFragment);
        fragmentList.add(userInfoFragment);

//        myFragmentPagerAdapter = new MyFragmentPagerAdapter(fManager, fragmentList);
        myFragmentStatePagerAdapter = new MyFragmentStatePagerAdapter(fManager, fragmentList);
        viewPager.setAdapter(myFragmentStatePagerAdapter);
//        viewPager.setOffscreenPageLimit(3);
    }

    public void setListeners(){
        llClose.setOnClickListener(this);
        llNotification.setOnClickListener(this);
        llMy.setOnClickListener(this);
        viewPager.addOnPageChangeListener(this);
    }
    @Override
    public void onClick(View v) {
//        FragmentTransaction fTransaction = fManager.beginTransaction();
//        hideAllFragment(fTransaction);
        resetImage();
        switch(v.getId()){
            case R.id.llClose:
//                if(indexFragment == null){
//                    indexFragment = new IndexFragment();
//                    fTransaction.add(R.id.flContent, indexFragment);
//                }else{
//                    fTransaction.show(indexFragment);
//                }
                viewPager.setCurrentItem(PAGE_ONE);
                ivClose.setImageResource(R.drawable.u14);
                tvTitleName.setText(getResources().getString(R.string.tvClose));
                break;
            case R.id.llNotification:
//                if(notificationFragment == null){
//                    notificationFragment = new NotificationFragment();
//                    fTransaction.add(R.id.flContent, notificationFragment);
//                }else{
//                    fTransaction.show(notificationFragment);
//                }
                viewPager.setCurrentItem(PAGE_TWO);
                ivNotification.setImageResource(R.drawable.notice_press);
                tvTitleName.setText(getResources().getString(R.string.tvNotification));
                break;
            case R.id.llMy:
//                if(userInfoFragment == null){
//                    userInfoFragment = new UserInfoFragment();
//                    fTransaction.add(R.id.flContent, userInfoFragment);
//                }else{
//                    fTransaction.show(userInfoFragment);
//                }
                viewPager.setCurrentItem(PAGE_THREE);
                ivMy.setImageResource(R.drawable.u18_select);
                tvTitleName.setText(getResources().getString(R.string.tvMy));
                break;
        }
//        fTransaction.commit();
    }
    //
    private void resetImage(){
        ivClose.setImageResource(R.drawable.u16_uppress);
        ivNotification.setImageResource(R.drawable.u16);
        ivMy.setImageResource(R.drawable.u18);
    }
    //隐藏所有的碎片
//    private void hideAllFragment(FragmentTransaction fragmentTransaction){
//        if(indexFragment != null) fragmentTransaction.hide(indexFragment);
//        if(notificationFragment != null) fragmentTransaction.hide(notificationFragment);
//        if(userInfoFragment != null) fragmentTransaction.hide(userInfoFragment);
//    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        resetImage();
        if(position == 0){
            ivClose.setImageResource(R.drawable.u14);
            tvTitleName.setText(getResources().getString(R.string.tvClose));
        }else if(position ==1){
            ivNotification.setImageResource(R.drawable.notice_press);
            tvTitleName.setText(getResources().getString(R.string.tvNotification));
        }else if(position ==2){
            ivMy.setImageResource(R.drawable.u18_select);
            tvTitleName.setText(getResources().getString(R.string.tvMy));
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }
}
