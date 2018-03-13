package com.cuisanzhang.mincreafting;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import com.jakewharton.disklrucache.DiskLruCache;

import java.io.File;
import java.io.IOException;

/**
 * Created by hesuxiang on 18/3/13.
 */

public class MyDiskLruCache {

    //缓存最大1G
    public static final int MAX_SIZE_CACHE = 1000 * 1024 * 1024; //1000m

    private static MyDiskLruCache myDiskLruCache ;
    private DiskLruCache mDiskLruCache = null;
    private Context context;


    public static MyDiskLruCache newInstance(Context context) {
        if (myDiskLruCache == null){
            myDiskLruCache = new MyDiskLruCache(context);
            myDiskLruCache.initDiskLruCache();
        }

        return myDiskLruCache;
    }

    private MyDiskLruCache(Context context){
        this.context = context;
    }

    public  void initDiskLruCache() {
        if (mDiskLruCache == null || mDiskLruCache.isClosed()) {
            try {
                File cacheDir = getDiskCacheDir(context, "imageCache");
                if (!cacheDir.exists()) {
                    cacheDir.mkdirs();
                }
                //diskCacheDir 缓存文件夹，具体指sdcard/Android/data/package_name/cache
                //1 应用版本号，一般写为1
                //1 单个节点所对应的数据的个数，一般写1
                //DISK_CACHE_SIZE 缓存大小
                mDiskLruCache = DiskLruCache.open(cacheDir, 1, 1, MAX_SIZE_CACHE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



    //优先外部缓存
    private   File getDiskCacheDir(Context context, String uniqueName) {
        String cachePath;
//        Log.e("cachePath", cachePath);
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
//            Log.e("cachePath", cachePath);
            cachePath = context.getExternalCacheDir().getAbsolutePath();
        }else {
            cachePath = context.getCacheDir().getAbsolutePath();
        }


        File cacheDir = new File(cachePath + File.separator + uniqueName);
        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        }
        Log.e("getCacheDir", cacheDir.getAbsolutePath());
        return cacheDir;
    }


    public boolean close(){

            try {
                if (mDiskLruCache != null) {
                    mDiskLruCache.close();
                }
                context = null;
                mDiskLruCache = null;

                return true;

            } catch (IOException e) {
                e.printStackTrace();

            }

            return false;

    }



    public void deleteAllCache(){

        try {
            mDiskLruCache.delete();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public long getCacheDirSize(){

        return  mDiskLruCache.size();

    }

    public DiskLruCache getDiskLruCache(){
        return mDiskLruCache;
    }
}
