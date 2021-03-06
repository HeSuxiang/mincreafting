package com.cuisanzhang.mincreafting;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * 网络工具类
 * Created by hesuxiang on 17/9/20.
 */

public class SettingUtils {

    private static final String PREF_SETTING = "pref_setting";
    private static final String THEME = "theme";
    private static final String COLOR = "color";
    private static final String USER_NAME = "user_name";
    private static final String VIP_STATE = "vip_state";
    private static final String IsFirstTimeOpenTutorial = "isFirstTimeOpenCacheTutorial";
    private static final String IsFirstTimeOpenCreating = "isFirstTimeOpenCreating";
    private static final String IsOpenImageCache = "isOpenImageCache";
    private static final String IsMobileConnectCache = "isMobileConnectCache";

    static String  VIP_URL = "http://owpvbuvtf.bkt.clouddn.com/vip.txt";

    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            // 获取手机所有连接管理对象(包括对wi-fi,net等连接的管理)
            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            // 获取NetworkInfo对象
            NetworkInfo networkInfo = manager.getActiveNetworkInfo();
            //判断NetworkInfo对象是否为空
            if (networkInfo != null)
                return networkInfo.isAvailable();
        }
        return false;
    }


    //是否连接WIFI
    public static boolean isWifiConnected(Context context)
    {
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        if (activeNetwork != null) { // connected to the internet
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                return true ;
            }
        }
        return false ;
    }

    public  static  boolean isFirstTimeOpenTutorial(Context context){
        SharedPreferences sp = context.getSharedPreferences(PREF_SETTING, Context.MODE_PRIVATE);
        boolean isFirstTimeOpenTutorial = sp.getBoolean(IsFirstTimeOpenTutorial, true);
        if (isFirstTimeOpenTutorial == true) {
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean(IsFirstTimeOpenTutorial, false);
            editor.apply();
        }

        return isFirstTimeOpenTutorial;
    }

    public  static  boolean isFirstTimeOpenCreating(Context context){
        SharedPreferences sp = context.getSharedPreferences(PREF_SETTING, Context.MODE_PRIVATE);
        boolean isFirstTimeOpenCreating = sp.getBoolean(IsFirstTimeOpenCreating, true);
        if (isFirstTimeOpenCreating == true) {
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean(IsFirstTimeOpenCreating, false);
            editor.apply();
        }

        return isFirstTimeOpenCreating;
    }

    //缓存图片设置
    public static void setSwitchCacheSetting(Context context, boolean isOpenImageCache) {
        SharedPreferences sp = context.getSharedPreferences(PREF_SETTING, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(IsOpenImageCache, isOpenImageCache);
        editor.apply();
    }

    public static boolean getSwitchCacheSetting(Context context) {
        SharedPreferences sp = context.getSharedPreferences(PREF_SETTING, Context.MODE_PRIVATE);
        return sp.getBoolean(IsOpenImageCache, true);
    }


    //移动网络缓存图片设置
    public static void setMobileConnectCacheSetting(Context context, boolean isMobileConnectCache) {
        SharedPreferences sp = context.getSharedPreferences(PREF_SETTING, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(IsMobileConnectCache, isMobileConnectCache);
        editor.apply();
    }

    public static boolean getMobileConnectCacheSetting(Context context) {
        SharedPreferences sp = context.getSharedPreferences(PREF_SETTING, Context.MODE_PRIVATE);
        return sp.getBoolean(IsMobileConnectCache, false);
    }


//        作者：AlicFeng
//        链接：http://www.jianshu.com/p/10ed9ae02775
//        來源：简书
//        著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。


    public static String filterString(Context context, String str) {
        String language = LanguageUtil.getLocaleLanguage(context);

        String[] des = {","
                , "+"
//                ,"("
//                ,")"
//                ,"(基础"
//                ,"还原)"
                , "或"
                , "和"
                , "任何"
                , "对应的"
                , "损坏的"

//                ,"红色"
//                ,"白色"
        };

        String[] des_zw = {","
                , "+"
//                ,"("
//                ,")"
//                ,"(基礎"
//                ,"還原)"
                , "或"
                , "和"
                , "任何"
                , "對應的"
                , "損壞的"

//                ,"紅色"
//                ,"白色"
        };


        if (language.equals(LanguageUtil.TRADITIONAL_CHINESE)) {
            for (String item : des_zw) {
                str = str.replace(item, " ");
            }
        }else {
            for (String item : des) {
                str = str.replace(item, " ");
            }
        }

//        Toast.makeText(ActivityListViewShowBlocks.this, str,Toast.LENGTH_LONG).show();
        str = str.trim();
        return str;
    }

    /**
     * Created by hesuxiang on 17/3/19.
     */

    public static class ChangeTheme {

        public static final int THEME_DEEPGRAY = R.style.Base_Theme_Design_deep_gray;
        public static final int THEME_GREEN = R.style.Base_Theme_Design_green;
        public static final int THEME_DRAKGREEN = R.style.Base_Theme_Design_darkgreen;
        public static final int THEME_BLUE = R.style.Base_Theme_Design_blue;
        public static final int THEME_SKY_BLUE = R.style.Base_Theme_Design_sky_blue;
        public static final int THEME_DEEP_BLUE = R.style.Base_Theme_Design_deep_blue;
        public static final int THEME_BROWN = R.style.Base_Theme_Design_brown;
        public static final int THEME_SADDLEBROWN = R.style.Base_Theme_Design_saddlebrown;
        public static final int THEME_HOTPINK = R.style.Base_Theme_Design_hotpink;
        public static final int THEME_PINK = R.style.Base_Theme_Design_pink;

        public static final int THEME_DEEP_DRAK= R.style.Base_Theme_Design_deep_drak;
        public static final int THEME_GRAY = R.style.Base_Theme_Design_gray;
        public static final int THEME_LIGHTGRAY = R.style.Base_Theme_Design_light_gray;
        public static final int THEME_ORANGE_RED = R.style.Base_Theme_Design_orange_red;
        public static final int THEME_ORANGE = R.style.Base_Theme_Design_orange;
        public static final int THEME_GOLD = R.style.Base_Theme_Design_gold;
        public static final int THEME_YELLOW = R.style.Base_Theme_Design_yellow;
        public static final int THEME_BLUE_PURPLE = R.style.Base_Theme_Design_blue_purple;
        public static final int THEME_PURPLE = R.style.Base_Theme_Design_purple;
        public static final int THEME_RED = R.style.Base_Theme_Design_red;





        public static void setTheme(Context context, int theme) {
            SharedPreferences sp = context.getSharedPreferences(PREF_SETTING, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putInt(THEME, theme);
            editor.apply();
        }

        public static int getTheme(Context context) {
            SharedPreferences sp = context.getSharedPreferences(PREF_SETTING, Context.MODE_PRIVATE);
            return sp.getInt(THEME, THEME_DEEP_DRAK);
        }

        public static void setTitleColor(Context context, int color) {
            SharedPreferences sp = context.getSharedPreferences(PREF_SETTING, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putInt(COLOR, color);
            editor.apply();
        }

        public static int getTitleColor(Context context) {
            SharedPreferences sp = context.getSharedPreferences(PREF_SETTING, Context.MODE_PRIVATE);
            return sp.getInt(COLOR, R.color.colorAccent_deep_drak);
        }

        public  static  void setUserName(Context context, String userName){
            SharedPreferences sp = context.getSharedPreferences(PREF_SETTING, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString(USER_NAME, userName);
            editor.apply();
        }

        public static String getUserName(Context context) {
            SharedPreferences sp = context.getSharedPreferences(PREF_SETTING, Context.MODE_PRIVATE);
            return sp.getString(USER_NAME, context.getString(R.string.enter_name));
        }


        public  static  void setVipState(Context context, boolean VipState){
            SharedPreferences sp = context.getSharedPreferences(PREF_SETTING, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean(VIP_STATE, VipState);
            editor.apply();
        }

        public static boolean getVipState(Context context) {
            SharedPreferences sp = context.getSharedPreferences(PREF_SETTING, Context.MODE_PRIVATE);
            return sp.getBoolean(VIP_STATE, false);
        }


    }







}
