package com.pcjz.lems.business.common.downloadapp.bean;

import java.io.Serializable;

/**
 * created by yezhengyu on 2017/5/24 11:22
 */
public class AppInfoBean implements Serializable{
    //当前id
    private String id;
    //名字
    private String appName;
    private String url;
    private int length;
    private int finished;
    //当前位置
    private int position;
    //当前子位置
    private int childPosition;
    //版本号
    private String versionCode;
    //是否已安装
    private boolean isInstall = false;

    public boolean isInstall() {
        return isInstall;
    }

    public void setIsInstall(boolean isInstall) {
        this.isInstall = isInstall;
    }

    //是否是打开
    private String isOpen;
    //区分
    private String basicUrl;
    //是否需要更新
    private boolean isUpdate = false;

    public boolean isUpdate() {
        return isUpdate;
    }

    public void setIsUpdate(boolean isUpdate) {
        this.isUpdate = isUpdate;
    }

    //应用图标
    private String appIcon;
    //应用描述
    private String remark;
    private float fileSize;
    private String appPackageName;
    //以免通知发送重复
    private String label;

    private boolean isSelected;

    public boolean isSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getAppPackageName() {
        return appPackageName;
    }

    public void setAppPackageName(String appPackageName) {
        this.appPackageName = appPackageName;
    }

    public String getIsOpen() {
        return isOpen;
    }

    public void setIsOpen(String isOpen) {
        this.isOpen = isOpen;
    }

    public String getBasicUrl() {
        return basicUrl;
    }

    public void setBasicUrl(String basicUrl) {
        this.basicUrl = basicUrl;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getFinished() {
        return finished;
    }

    public void setFinished(int finished) {
        this.finished = finished;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getChildPosition() {
        return childPosition;
    }

    public void setChildPosition(int childPosition) {
        this.childPosition = childPosition;
    }

    public String getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }
}
