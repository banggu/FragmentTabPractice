package com.scnu.bangzhu.fragmenttabpractice.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.LruCache;
import android.widget.ImageView;

import com.scnu.bangzhu.fragmenttabpractice.libcore.io.DiskLruCache;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by bangzhu on 2016/5/27.
 */
public class AsyncBitmapLoader1 {
    private Context mContext;
    private LruCache<String, Bitmap> mCache;
    private DiskLruCache mDiskLruCache;
    private String mImageUrl;
    private ImageView mImageView;

    public AsyncBitmapLoader1(Context context){
        this.mContext = context;
        initLruCache();
        initDiskLruCache();
    }

    private void initLruCache(){
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
    private void initDiskLruCache(){
        File cacheDir = getDiskCacheDir(mContext, "bitmap");
        if(!cacheDir.exists()){
            cacheDir.mkdirs();
        }
        try {
            mDiskLruCache = DiskLruCache.open(cacheDir, getAppVersion(mContext), 1, 10*1024*1024);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void showBitmapFromUrl(final ImageView imageView, final String imageUrl){
        mImageView = imageView;
        mImageUrl = imageUrl;
        Bitmap bitmap = getBitmapFromDiskCache(imageUrl);
        if(bitmap == null){
            bitmap = getBitmapFromCache(imageUrl);
            if(bitmap == null){
                MyAsyncTask myAsyncTask = new MyAsyncTask(mImageView, mImageUrl);
                myAsyncTask.execute(imageUrl);
            }else{
                mImageView.setImageBitmap(bitmap);
            }
        }else{
            mImageView.setImageBitmap(bitmap);
        }

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
     * 从内存缓存中加载图片
     * @param imageUrl 请求加载图片的url
     * @return
     */
    public Bitmap getBitmapFromCache(String imageUrl){
        return mCache.get(imageUrl);
    }

    /**
     * 将图片加入内存缓存中
     * @param imageUrl 图片对应的url
     * @param bitmap 图片位图
     */
    public void addBitmapToCache(String imageUrl, Bitmap bitmap){
        if(getBitmapFromCache(imageUrl) == null){
            mCache.put(imageUrl, bitmap);
        }
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
     * 从本地缓存中读取图片
     * @param imageUrl
     * @return
     */
    public Bitmap getBitmapFromDiskCache(String imageUrl){
        Bitmap bitmap = null;
        try {
            String key = hashKeyForDisk(imageUrl);
            DiskLruCache.Snapshot snapshot = mDiskLruCache.get(key);
            if(snapshot != null){
                InputStream is = snapshot.getInputStream(0);
                bitmap = BitmapFactory.decodeStream(is);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * 将图片写入本地缓存
     * @param bitmap
     */
    public void addBitmapToDiskCache(Bitmap bitmap){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String key = hashKeyForDisk(mImageUrl);
                    DiskLruCache.Editor editor = mDiskLruCache.edit(key);
                    if(editor != null){
                        OutputStream outputStream = editor.newOutputStream(0);
                        if(downloadUrlToStream(mImageUrl, outputStream)){
                            editor.commit();
                        }else{
                            editor.abort();
                        }
                    }
                    mDiskLruCache.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 下载网络图片
     * @param imageUrl
     * @param os
     * @return
     */
    public boolean downloadUrlToStream(String imageUrl, OutputStream os){
        HttpURLConnection urlConnection = null;
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try {
            final URL url = new URL(imageUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            bis = new BufferedInputStream(urlConnection.getInputStream(), 8*1024);
            bos = new BufferedOutputStream(os, 8*1024);
            int b;
            while((b = bis.read()) != -1){
                bos.write(b);
            }
            return true;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(urlConnection != null){
                urlConnection.disconnect();
            }

            try {
                if(bis != null){
                    bos.close();
                }
                if(bos != null){
                    bis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public Bitmap getBitmapFromUrl(final String imageUrl){
        Bitmap bitmap = null;
        InputStream is = null;
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            is = new BufferedInputStream(urlConnection.getInputStream());
            bitmap = BitmapFactory.decodeStream(is);
            //加入内存缓存
            addBitmapToCache(mImageUrl, bitmap);
            //加入本地缓存
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

    private class MyAsyncTask extends AsyncTask<String, Void, Bitmap> {
        private ImageView imageView;
        private String imageUrl;
        public MyAsyncTask(ImageView imageView, String url){
            this.imageView = imageView;
            this.imageUrl = url;
        }
        @Override
        protected Bitmap doInBackground(String... params) {
            return getBitmapFromUrl(params[0]);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if(imageView.getTag().equals(imageUrl)){
                mImageView.setImageBitmap(bitmap);
            }
        }
    }
}
