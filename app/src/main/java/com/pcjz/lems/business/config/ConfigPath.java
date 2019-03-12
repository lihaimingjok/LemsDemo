package com.pcjz.lems.business.config;

import android.os.Environment;

import com.pcjz.lems.business.constant.ResultStatus;
import com.pcjz.lems.business.storage.SharedPreferencesManager;

/**
 * Created by kun on 2016/11/10.
 * 配置类
 */
public class ConfigPath {
    /**
     * 文件地址
     */
    public final static String demsPath = Environment.getExternalStorageDirectory()
            + "/LEMS/";
    /**
     * 文件下载地址
     */
    //String useName = SharedPreferencesManager.getString(ResultStatus.LOGIN_USERNAME);
    public static String downLoadPathRoot = Environment.getExternalStorageDirectory()
            + "/LEMS/" + "downloads";

    public static String downLoadPath = Environment.getExternalStorageDirectory()
            + "/LEMS/" + "downloads" + "/" + SharedPreferencesManager.getString(ResultStatus.LOGIN_USERNAME) + "/";
    /**
     * 图片下载地址
     */
    public final static String downloadImagePath = Environment.getExternalStorageDirectory()
            + "/LEMS/" + "downloadImagePath/";

}
