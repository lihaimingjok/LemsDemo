package com.pcjz.lems.business.common.downloadapp.callback;


import com.pcjz.lems.business.entity.offline.ThreadBean;

/**
 * Created by kun on 2016/11/11.
 * 下载进度回调
 */
public interface DownloadCallBack {
    /**
     * 暂停回调
     * @param threadBean
     */
    void pauseCallBack(ThreadBean threadBean);
    /**
     * 下载进度
     * @param length
     */
    void progressCallBack(int length);

    /**
     * 线程下载完毕
     * @param threadBean
     */
    void threadDownLoadFinished(ThreadBean threadBean);
}
