package com.pcjz.lems.business.config;

import android.os.Environment;

/**
 * Created by kun on 2016/11/10.
 * 配置类
 */
public class ConfigAppPath {
    /**
     * 文件下载地址
     */
    public final static String downLoadPath = Environment.getExternalStorageDirectory()
            + "/LEMS/appInfo/";
}
