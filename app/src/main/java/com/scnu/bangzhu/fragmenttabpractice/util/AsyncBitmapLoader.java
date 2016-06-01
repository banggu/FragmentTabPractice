package com.scnu.bangzhu.fragmenttabpractice.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.LruCache;
import android.widget.ImageView;

import com.scnu.bangzhu.fragmenttabpractice.libcore.io.DiskLruCache;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

/**
 * Created by bangzhu on 2016/5/20.
 * 异步加载位图类
 */
public class AsyncBitmapLoader {
    private Context mContext;
    private LruCache<String, Bitmap> mCache;
    private DiskLruCache mDiskCache;
    private ImageView mImageView;
    private String mImageUrl;
    private File mCacheDir;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(mImageView.getTag().equals(mImageUrl)){
                mImageView.setImageBitmap((Bitmap) msg.obj);
            }
        }
    };

    public AsyncBitmapLoader(Context context){
        mContext = context;
        initDiskCache();
        //初始化内存缓存
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheSize = maxMemory/4;
        mCache = new LruCache<String, Bitmap>(cacheSize){
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount();
            }
        };
    }

    /**
     *  初始化SDcard缓存
     */
    private void initDiskCache(){
        try {
            //初始化缓存路径
            mCacheDir = getDiskCacheDir(mContext, "bitmap");
            if(!mCacheDir.exists()){
                mCacheDir.mkdirs();
            }
            mDiskCache = DiskLruCache.open(mCacheDir, getAppVersion(mContext), 1, 10*1024*1024);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 将图片加入内存缓存中
     * @param url 图片对应的url
     * @param bitmap 图片位图
     */
    public void addBitmapToCache(String url, Bitmap bitmap){
        if(getBitmapFromCache(url) == null){
            mCache.put(url, bitmap);
        }
    }

    /**
     * 从内存缓存中加载图片
     * @param url 请求加载图片的url
     * @return
     */
    public Bitmap getBitmapFromCache(String url){
        return mCache.get(url);
    }

    public void addBitmapToDiskCache(Bitmap bitmap){
        //将图片保存至本地缓存
        String bitmapName = hashKeyForDisk(mImageUrl);
        File bitmapFile = new File(mCacheDir.getPath() + File.separator + bitmapName);
        if(!bitmapFile.exists()){
            try {
                bitmapFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //打开文件输出流
        FileOutputStream fileOutputStream;
        try {
            fileOutputStream = new FileOutputStream(bitmapFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Bitmap getBitmapFromDiskCache(String imageUrl){
        Bitmap bitmap = null;
        //在本地缓存（即SD卡）中查找位图
        int i = 0;
        String bitmapDir = mCacheDir.getPath() + File.separator + hashKeyForDisk(imageUrl);
        Log.i("HZWing", bitmapDir);
        String diskCacheDir = mCacheDir.getPath();
        Log.i("HZWing", diskCacheDir);
        File cacheDir = new File(diskCacheDir);
        File[] cacheFiles = cacheDir.listFiles();
        if(cacheFiles == null){
            return null;
        }
        for(i=0;i<cacheFiles.length;i++){
            if(bitmapDir.equals(cacheFiles[i].getName())){
                break;
            }
        }
        if(i < cacheFiles.length){
            bitmap = BitmapFactory.decodeFile(bitmapDir);
        }
        return bitmap;
    }

    /**
     * 获取缓存路径
     * @param context
     * @param uniqueName 用于区分的子文件夹名，如 "bitmap"
     * @return
     */
    public File getDiskCacheDir(Context context, String uniqueName){
        String cachePath;
        if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                && !Environment.isExternalStorageRemovable()){
            cachePath = context.getExternalCacheDir().getPath();
        }else{
            cachePath = context.getCacheDir().getPath();
        }
        return new File(cachePath + File.separator + uniqueName);
    }

    /**
     *  对URL进行MD5加密
     * @param key
     * @return
     */
    public String hashKeyForDisk(String key) {
        String cacheKey;
        try {
            final MessageDigest mDigest = MessageDigest.getInstance("MD5");
            mDigest.update(key.getBytes());
            cacheKey = bytesToHexString(mDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            cacheKey = String.valueOf(key.hashCode());
        }
        return cacheKey;
    }

    private String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    /**
     *  获取应用程序版本号
     * @param context
     * @return
     */
    public int getAppVersion(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 1;
    }

    /**
     * 根据URL显示图片
     * @param imageUrl 加载位图的url
     * @param imageView 要显示位图的ImageView
     * @return
     */
    public void showBitmapFromUrl(final ImageView imageView, final String imageUrl){
        mImageView = imageView;
        mImageUrl = imageUrl;
        Bitmap bt = getBitmapFromDiskCache(imageUrl);
        if(bt == null){
            bt = getBitmapFromCache(imageUrl);
            if(bt == null){
                //当在内存缓存、本地缓存都没找到位图时，请求网络图片
                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        Bitmap bitmap = getBitmapFromUrl(imageUrl);
                        Message message = Message.obtain();
                        message.obj = bitmap;
                        mHandler.sendMessage(message);
                    }
                }.start();
            }else{
                imageView.setImageBitmap(bt);
            }
        }else{
            imageView.setImageBitmap(bt);
        }
    }

    /**
     * 加载网络图片
     * @param bitmapUrl 图片网址
     * @return bitmap 加载完的图片
     */
    public Bitmap getBitmapFromUrl(String bitmapUrl){
        Bitmap bitmap;
        InputStream is = null;
        try {
            //建立连接
            URL url = new URL(bitmapUrl);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            is= new BufferedInputStream(urlConnection.getInputStream());
            bitmap = BitmapFactory.decodeStream(is);
            //将图片存入内存缓存
            addBitmapToCache(mImageUrl, bitmap);
            //将图片缓存到SDcard
            addBitmapToDiskCache(bitmap);
            urlConnection.disconnect();
            return bitmap;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(is != null){
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
