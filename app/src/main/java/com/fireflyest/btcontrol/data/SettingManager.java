package com.fireflyest.btcontrol.data;

import android.content.SharedPreferences;
import android.util.Log;

public class SettingManager implements SharedPreferences.OnSharedPreferenceChangeListener{

    //自动连接
    public static boolean AUTO_CONNECT;

    //蓝牙筛选
    public static boolean AUTO_DISCERN;

    //蓝牙地址
    public static String SELECT_ADDRESS;

    //关闭编码
    public static String CLOSE_CODE;

    private static SettingManager settingManager;

    private SharedPreferences sharedPreferences;

    public synchronized static SettingManager getInstance(){
        if(null == settingManager){
            settingManager = new SettingManager();
        }
        return settingManager;
    }

    private SettingManager(){
    }

    public void initSettingManager(SharedPreferences sharedPreferences){
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        this.sharedPreferences =sharedPreferences;
        AUTO_CONNECT = sharedPreferences.getBoolean("auto_connect", true);
        AUTO_DISCERN = sharedPreferences.getBoolean("auto_discern", false);
        SELECT_ADDRESS = sharedPreferences.getString("select_address", "none");
        CLOSE_CODE = sharedPreferences.getString("close_code", "300");
    }

    public void unRegister(){
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    public void setStringPreference(String key, String value){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public void setBooleanPreference(String key, boolean value){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public void setIntPreference(String key, int value){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        switch (key){
            case "auto_connect":
                AUTO_CONNECT = sharedPreferences.getBoolean(key, false);
                break;
            case "auto_discern":
                AUTO_DISCERN = sharedPreferences.getBoolean(key, false);
                break;
            case "select_address":
                SELECT_ADDRESS = sharedPreferences.getString(key, "none");
                break;
            case "close_code":
                CLOSE_CODE = sharedPreferences.getString(key, "300");
                break;
            default:
        }
    }
}
