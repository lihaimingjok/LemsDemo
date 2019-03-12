package com.pcjz.lems.business.common.downloadapp.thread;

import android.os.Environment;

import com.pcjz.lems.business.common.downloadapp.ConfigAppPath;
import com.pcjz.lems.business.common.downloadapp.bean.AppInfoBean;
import com.pcjz.lems.business.common.downloadapp.event.EventMessage;
import com.pcjz.lems.business.config.ConfigPath;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by 坤 on 2016/11/10.
 * 初始化线程
 */
public class InitThread extends Thread {

    private AppInfoBean appBean;

    public InitThread(AppInfoBean appBean) {
        this.appBean = appBean;
    }

    @Override
    public void run() {
        HttpURLConnection connection = null;
        RandomAccessFile randomAccessFile = null;
        InputStream inputStream = null;
        try {
            URL url = new URL(appBean.getUrl());
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(10000);
            connection.setRequestMethod("GET");
            connection.connect();
            int fileLength = -1;
            if(connection.getResponseCode() == HttpURLConnection.HTTP_OK){
                fileLength = connection.getContentLength();
            }

            if(fileLength<=0) return;

            if (Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED)) {
                File dir0 = new File(ConfigPath.demsPath);
                if (!dir0.exists()) {
                    dir0.mkdir();
                }
                File dir = new File(ConfigAppPath.downLoadPath);
                if (!dir.exists()) {
                    dir.mkdir();
                }
                File file = new File(dir, appBean.getAppName());
                randomAccessFile = new RandomAccessFile(file, "rwd");
                randomAccessFile.setLength(fileLength);
            }
            //长度给fileInfo对象
            appBean.setLength(fileLength);
            //将对象传递给Service
            EventMessage eventMessage = new EventMessage(1, appBean);
            EventBus.getDefault().post(eventMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
