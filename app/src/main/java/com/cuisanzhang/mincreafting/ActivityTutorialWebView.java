package com.cuisanzhang.mincreafting;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;


import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.jakewharton.disklrucache.DiskLruCache;
import com.luhuiguo.chinese.ChineseUtils;
//import com.luhuiguo.chinese.ChineseUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


public class ActivityTutorialWebView extends AppCompatActivity {

    public static String EXTRA_URI = "file_name";
    public static String EXTRA_TUTORIAL_NAME = "tutorial_name";

    private DiskLruCache mDiskLruCache = null;

    private WebView mWebview;
    WebSettings mWebSettings;
    private String HtmlUrl = "http://www.baidu.com/";
    private String tutorial_name = "错误页面!";

//    public static String EXTRA_IS_LANGUAGE_OF_SIMPLIFIED = "is_language_of_simplified";

//打开本包内asset目录下的index.html文件
//wView.loadUrl(" file:///android_asset/index.html ");
//
////打开本地sd卡内的index.html文件
//wView.loadUrl("content://com.android.htmlfileprovider/sdcard/index.html");
//
////打开指定URL的html文件
//wView.loadUrl(" http://m.oschina.net");

    private boolean isNetworkConnected = false;
    private boolean isVip;
    private boolean is_simplified_chinese  = true;
    private AdView mAdView;
    private String stringData = null;

    private boolean showAd = false;

    private static final int DISK_CACHE_INDEX = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        int theme = SettingUtils.ChangeTheme.getTheme(getApplicationContext());
        setTheme(theme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_webview_tutorial);


        Intent intent = getIntent();
        HtmlUrl = intent.getStringExtra(EXTRA_URI);
        tutorial_name = intent.getStringExtra(EXTRA_TUTORIAL_NAME);
//        boolean is_simplified = intent.getBooleanExtra(EXTRA_IS_LANGUAGE_OF_SIMPLIFIED, true);

        String language = LanguageUtil.getLocaleLanguage(ActivityTutorialWebView.this);
        if (language.equals(LanguageUtil.SIMPLIFIED_CHINESE)) {
            is_simplified_chinese = true;
        }else {
            is_simplified_chinese = false;
        }


        initActionBar();

//        //初始化cachewebview
//        String cachedir = getExternalCacheDir(ActivityTutorialWebView.this, "imageCache");
//        if(cachedir == null)
//            cachedir = getCacheDir(ActivityTutorialWebView.this, "imageCache");
//
//        CacheWebView.getCacheConfig().init(this,getExternalCacheDir().getAbsolutePath(),1024*1024*100,1024*1024*10)
//                .enableDebug(true);//1000M 磁盘缓存空间,100M 内存缓存空间


        isVip = SettingUtils.ChangeTheme.getVipState(ActivityTutorialWebView.this);

        isNetworkConnected = SettingUtils.isNetworkConnected(ActivityTutorialWebView.this);

        mAdView = (AdView) findViewById(R.id.adView);


        //2分之一的几率广告
        int random = new Random().nextInt(10);

        if (random < 5) {
            showAd = true;
        }

        if (!isVip && isNetworkConnected && showAd) {
////        google admob
            MobileAds.initialize(this, getString(R.string.admob_uni_id));
            mAdView.loadAd(new AdRequest.Builder().build());
//            //add admob in listView
//            LinearLayout tipEndViw = (LinearLayout) LayoutInflater.from(getApplicationContext()).inflate(R.layout.admob_native_layout, null);
//            NativeExpressAdView nativeExpressAdView = (NativeExpressAdView) tipEndViw.findViewById(R.id.nativeExpressAdView);
//
//
//            if (!isNetworkConnected) {
//                nativeExpressAdView.setVisibility(View.GONE);
//            } else {
//                nativeExpressAdView.loadAd(new AdRequest.Builder().build());
//            }
//            listView.addFooterView(tipEndViw);
        } else {
            if (mAdView != null) {
                mAdView.setVisibility(View.GONE);
            }

        }

        mWebview = (WebView) findViewById(R.id.webView);
//        mWebview.setBlockNetworkImage(true);
        mWebSettings = mWebview.getSettings();
        mWebSettings.setJavaScriptEnabled(true);
//        mWebSettings.setBlockNetworkImage(true);
//        mWebSettings.setDomStorageEnabled(true);

//        //缓存图片设置
//        if (SettingUtils.getSwitchCacheSetting(ActivityTutorialWebView.this)) {
//            mWebSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
//            //有时候网页需要自己保存一些关键数据,Android WebView 需要自己设置
//            mWebSettings.setDomStorageEnabled(true);
//            mWebSettings.setDatabaseEnabled(true);
//            mWebSettings.setAppCacheEnabled(true);
////            String appCachePath = getApplicationContext().getCacheDir().getAbsolutePath();
////            Log.e("appCachePath=","appCachePath" );
////            mWebSettings.setAppCachePath(appCachePath);
////            mWebSettings.setAppCacheMaxSize();
//
//        } else {
//            //设置不缓存的时候清除缓存
//            mWebview.clearCache(true);
//        }

//        mWebview.loadUrl(HtmlUrl);

        InputStream is;
        StringBuilder builder = new StringBuilder();
        try {
            is = getAssets().open(HtmlUrl);
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader reader = new BufferedReader(isr);

            String line = null;

            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }

            reader.close();
            isr.close();
            is.close();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        stringData = builder.toString();

        //webview.loadUrl(htmlurl);
        mWebview.getSettings().setDefaultTextEncodingName("UTF-8");//设置默认为utf-8

//		 webview.loadData( ChineseUtils.toTraditional(string),  "text/html; charset=UTF-8", null);

//		  if(is_simplified){
//			  webview.loadDataWithBaseURL("file:///android_asset/",string,  "text/html; charset=UTF-8", null,null);
//		  }else{
//			  webview.loadDataWithBaseURL("file:///android_asset/", ChineseUtils.toTraditional(string),  "text/html; charset=UTF-8", null,null);
//		  }
        if(is_simplified_chinese){
            mWebview.loadDataWithBaseURL("file:///android_asset/html/",stringData,  "text/html", "UTF-8",null);
        }else{
            mWebview.loadDataWithBaseURL("file:///android_asset/html/",
                    ChineseUtils.toTraditional(stringData),
//                    string,
                    "text/html", "UTF-8",null);
        }

//        ChineseUtils.getInstance(ActivityTutorialWebView.this,true);


        mWebview.setWebViewClient(new WebViewClientCache(ActivityTutorialWebView.this));


        mDiskLruCache = MyDiskLruCache.newInstance(ActivityTutorialWebView.this).getDiskLruCache();

//
    }


    private class WebViewClientCache extends WebViewClient {


        private Context mContext;


        WebViewClientCache(Context context) {
            mContext = context;
        }


        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
//            view.loadUrl(HtmlUrl);
            return super.shouldOverrideUrlLoading(view, request);
        }


        //sdk21_api
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
            Log.e("getCache", "shouldInterceptRequest(request)");
            String url = request.getUrl().toString();

            if (isImageUrl(url)) {
                Log.e("getCache", "isImageUrl " + url);

                WebResourceResponse response = getCache(url);

                if (response != null) {
                    return response;
                }
            }

            return super.shouldInterceptRequest(view, request);
        }

        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
            Log.e("getCache", "shouldInterceptRequest(url)");

            if (isImageUrl(url)) {
                Log.e("getCache", "isImageUrl " + url);

                WebResourceResponse response = getCache(url);

                if (response != null) {
                    return response;
                }
            }


            return super.shouldInterceptRequest(view, url);
        }


        private WebResourceResponse getCache(String imageUrl) {
//            Log.e("getCache", "getCache() " + imageUrl);
            String key = hashKeyFromUrl(imageUrl);

            try {
//                Log.e("getCache", "next line is ---- snapshot = mDiskLruCache.get(key) ");
                DiskLruCache.Snapshot snapshot = mDiskLruCache.get(key);

                //有缓存
                if (snapshot != null) {
//                    Log.e("getCache", "snapshot != null)");
                    InputStream in = snapshot.getInputStream(0);
//                    Log.e("getCache", "return cache");
//                    return cache;
                    return new WebResourceResponse(getMinetype(imageUrl), "UTF-8", in);
                }

                //无缓存
                else {
//                    Log.e("getCache", "snapshot == null");

                    boolean isNetworkConnected = SettingUtils.isNetworkConnected(ActivityTutorialWebView.this);
                    boolean isSetCacheImage = SettingUtils.getSwitchCacheSetting(ActivityTutorialWebView.this);

                    //没网络,或者设置不缓存就不下载
                    if (!isNetworkConnected || !isSetCacheImage) {
//                        Log.e("getCache", "NetworkConnected close or don't Cache image !");
                        return null;

                    }

                    boolean isWifiConnected = SettingUtils.isWifiConnected(ActivityTutorialWebView.this);
                    boolean isMobileConnectCache = SettingUtils.getMobileConnectCacheSetting(ActivityTutorialWebView.this);

                    //有WIFI或者设置移动网络缓存, 下载
                    if (isWifiConnected || isMobileConnectCache) {
//                        Log.e("getCache", "start down cache");
                        //后台开线程下载保存
                        MyCacheImageRunnable runnable =  new MyCacheImageRunnable(imageUrl);
                        new Thread(runnable).start();
                    }

                    //防止白屏,浏览器去加载
                    return null;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;

        }

    }


    private class MyCacheImageRunnable implements Runnable {

        private String ImageUrl;

        MyCacheImageRunnable(String url) {
            ImageUrl = url;
        }

        @Override
        public void run() {

            try {
                String key = hashKeyFromUrl(ImageUrl);
                DiskLruCache.Editor editor = mDiskLruCache.edit(key);
//                Log.e("getCache", "MyCacheImageRunnable.run " + ImageUrl);
                if (editor != null) {
                    OutputStream out = editor.newOutputStream(DISK_CACHE_INDEX);

                    if (downLoadUrlToStream(ImageUrl, out)) {
                        editor.commit();//提交
                    } else {
                        editor.abort();//重复操作
                    }
                    mDiskLruCache.flush();//刷新                    }

                }

            } catch (IOException e) {
//                Log.e("getCache", "MyRunnable Cache image error in DiskLruCache");
                e.printStackTrace();
            }

        }
    }


    private boolean downLoadUrlToStream(String ImageUrl, OutputStream out) {

//        Log.e("getCache", "downLoadUrlToStream! " + ImageUrl);
        HttpURLConnection urlConnection = null;
        BufferedOutputStream bos = null;
        BufferedInputStream bis = null;
        try {
            final URL url = new URL(ImageUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            bis = new BufferedInputStream(urlConnection.getInputStream());
            bos = new BufferedOutputStream(out);
            int b;
            while ((b = bis.read()) != -1) {
                bos.write(b);
            }

            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }

            closeStream(bis);
            closeStream(bos);

        }
        return false;
    }

    //hash code
    public String hashKeyFromUrl(String url) {
        String cacheKey;

        try {
            final MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(url.getBytes());
            cacheKey = bytesToString(messageDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            cacheKey = String.valueOf(url.hashCode());
            e.printStackTrace();
        }
        return cacheKey ;

    }

    private String bytesToString(byte[] bytes) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0XFF & bytes[i]); //得到十六进制字符串
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }


    private boolean isImageUrl(String url) {

        //本地图片
        if(url.startsWith("file")){
            return  false;
        }
        //本地图片
        if(url.startsWith("local")){
            return  false;
        }


        //网络图片
        if (url.endsWith(".jpg")
                || url.endsWith(".JPG")
                || url.endsWith(".jpeg")
                || url.endsWith(".bmp")
                || url.endsWith(".gif")
                || url.endsWith(".png")) {
            return true;
        }

//        不是图片
        return false;
    }


    private String getMinetype(String url) {
//        MIME 类型
//        image/bmp	    bmp
//        image/gif	    gif
//        image/jpeg	jpg
//        image/jpeg	jpeg
//        image/png      png

        if (url.endsWith(".jpg")) {
            return "image/jpeg";
        }
        if (url.endsWith(".JPG")) {
            return "image/jpeg";
        }
        if (url.endsWith(".jpeg")) {
            return "image/jpeg";
        }
        if (url.endsWith(".bmp")) {
            return "image/bmp";
        }
        if (url.endsWith(".gif")) {
            return "image/gif";
        }
        if (url.endsWith(".png")) {
            return "image/png";
        }


        return "image/png";
    }

    /**
     * 关闭流
     */
    private void closeStream(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        try {
//            mDiskLruCache.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    public void initActionBar() {
        TextView title = findViewById(R.id.title);
        if (is_simplified_chinese){
            title.setText(tutorial_name);
        }else {
            title.setText(ChineseUtils.toTraditional(tutorial_name));
        }
//        if(!is_simplified_chinese){
//            title.setText(ChineseUtils.toTraditional("我的世界合成表大全"));
//        }
        ImageView imageViewMenu = (ImageView) findViewById(R.id.imageViewToolbar_menu);
        imageViewMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
//        TextView titleBar = (TextView) findViewById(R.id.titleBar);


//        setSupportActionBar(toolbar);
//        ImageView imageViewRefresh_menu = (ImageView) findViewById(R.id.imageViewRefresh_menu);
//        imageViewRefresh_menu.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(is_simplified_chinese){
//                    mWebview.loadDataWithBaseURL("file:///android_asset/html/",stringData,  "text/html", "UTF-8",null);
//                }else{
//                    mWebview.loadDataWithBaseURL("file:///android_asset/html/",
//                            ChineseUtils.toTraditional(stringData),
////                    string,
//                            "text/html", "UTF-8",null);
//                }
//
//            }
//        });
    }
}
