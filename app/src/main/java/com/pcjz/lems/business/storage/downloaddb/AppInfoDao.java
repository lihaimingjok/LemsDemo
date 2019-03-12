package com.pcjz.lems.business.storage.downloaddb;

import com.pcjz.lems.business.common.downloadapp.bean.AppInfoBean;

import java.util.List;

/**
 * created by yezhengyu on 2017/5/24 15:22
 */
public interface AppInfoDao {

    void insertApp(AppInfoBean appBean);

    void updateApp(String basicUrl, String id, int finished);

    void deleteApp(String id);
    //根据URL来查询item集合
    List<AppInfoBean> getApps(String basicUrl);
    //判断是否有下载过文件
    boolean isExists(String basicUrl);
    //更新版本号
    void updateVersionApp(String basicUrl, String id, String versionCode);
}
