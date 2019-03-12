package com.pcjz.lems.ui.application.bean;

/**
 * created by yezhengyu on 2017/5/22 11:57
 */
public class Application {
    private String id;
    private String name;
    //应用图标
    private String appIcon;
    //应用描述
    private String remark;
    private float fileSize;
    private String publishTime;
    private String appIosPath;
    private String versionId;
    private String appPackageName;
    private String versionName;
    private String appAndroidPath;
    private String versionCode;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAppIcon() {
        return appIcon;
    }

    public void setAppIcon(String appIcon) {
        this.appIcon = appIcon;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public float getFileSize() {
        return fileSize;
    }

    public void setFileSize(float fileSize) {
        this.fileSize = fileSize;
    }

    public String getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(String publishTime) {
        this.publishTime = publishTime;
    }

    public String getAppIosPath() {
        return appIosPath;
    }

    public void setAppIosPath(String appIosPath) {
        this.appIosPath = appIosPath;
    }

    public String getVersionId() {
        return versionId;
    }

    public void setVersionId(String versionId) {
        this.versionId = versionId;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getAppPackageName() {
        return appPackageName;
    }

    public void setAppPackageName(String appPackageName) {
        this.appPackageName = appPackageName;
    }

    public String getAppAndroidPath() {
        return appAndroidPath;
    }

    public void setAppAndroidPath(String appAndroidPath) {
        this.appAndroidPath = appAndroidPath;
    }

    public String getVersionCode() {
        return versionCode;
    }
    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }
}
