package com.pcjz.lems.business.common.downloadapp.thread;

import com.pcjz.lems.business.common.downloadapp.ConfigAppPath;
import com.pcjz.lems.business.common.downloadapp.bean.AppInfoBean;
import com.pcjz.lems.business.common.downloadapp.callback.DownloadCallBack;
import com.pcjz.lems.business.common.utils.TLog;
import com.pcjz.lems.business.entity.offline.ThreadBean;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by kun on 2016/11/11.
 * 下载线程
 */
public class DownloadThread extends Thread {

    private final AppInfoBean appBean;
    private ThreadBean threadBean;
    private DownloadCallBack callback;
    private Boolean isPause = false;

    public DownloadThread(AppInfoBean appBean, ThreadBean threadBean, DownloadCallBack callback) {
        this.appBean = appBean;
        this.threadBean = threadBean;
        this.callback = callback;
    }

    public void setPause(Boolean pause) {
        isPause = pause;
    }

    @Override
    public void run() {
        HttpURLConnection connection = null;
        RandomAccessFile raf = null;
        InputStream inputStream = null;
        try {
            URL url = new URL(threadBean.getUrl());
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(10000);
            connection.setRequestMethod("GET");
            //设置下载起始位置
            int start = threadBean.getStart() + threadBean.getFinished();
            //传入当前进度给后台
            connection.setRequestProperty("Range", "bytes=" + start + "-" + threadBean.getEnd());
            TLog.log("第一次start" + start);
            //设置写入位置
            File file = new File(ConfigAppPath.downLoadPath, appBean.getAppName());
            raf = new RandomAccessFile(file, "rwd");
            raf.seek(start);
            //开始下载
            if (connection.getResponseCode() == HttpURLConnection.HTTP_PARTIAL) {
                //获得文件流
                inputStream = connection.getInputStream();
                byte[] bytes = new byte[512];
                int len = -1;
                int count = 0;
                int i = 0;
                while ((len = inputStream.read(bytes)) != -1) {
                    //i++;
                    //TLog.log("count相加" + (count += len));
                    //写入文件
                    raf.write(bytes, 0, len);
                    //将加载的进度回调出去
                    callback.progressCallBack(len);
                    //TLog.log("len" + "i=" + i + "-" +(count += len));
                    //保存进度
                    threadBean.setFinished(threadBean.getFinished() + len);
                    //TLog.log("及时进度" + (threadBean.getFinished() + len));
                    //在下载暂停的时候将下载进度保存到数据库
                    if (isPause) {
                        callback.pauseCallBack(threadBean);
                        return;
                    }
                }
                //下载完成
                callback.threadDownLoadFinished(threadBean);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
                raf.close();
                connection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
