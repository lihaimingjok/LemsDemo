package com.pcjz.lems.business.storage.downloaddb;

import com.pcjz.lems.business.entity.offline.OfflinePeriodInfo;
import com.pcjz.lems.business.entity.offline.ThreadBean;

import java.util.List;

/**
 * Created by kun on 2016/11/10.
 */
public interface ThreadDao {
    /**
     * 插入下载线程信息
     * @param threadBean
     */
    void insertThread(ThreadBean threadBean);

    /**
     * 更新下载线程信息
     * @param url
     * @param thread_id
     * @param finished
     */
    void updateThread(String url, int thread_id, int finished, String phone);

    /**
     * 删除下载线程
     * @param url
     */
    void deleteThread(String url, String phone);

    /**
     * 获取下载线程
     * @param url
     * @return
     */
    List<ThreadBean> getThreads(String url, String phone);

    /**
     * 判断下载线程是否存在
     * @param url
     * @param thread_id
     * @return
     */
    boolean isExists(String url, int thread_id);

    void insertPeriod(OfflinePeriodInfo offlinePeriod);

    void updatePeriod(String url, String id, int finished, String phone);

    void deletePeriod(String id, String phone);
    //根据URL来查询item集合
    List<OfflinePeriodInfo> getPeriods(String url, String phone);
}
