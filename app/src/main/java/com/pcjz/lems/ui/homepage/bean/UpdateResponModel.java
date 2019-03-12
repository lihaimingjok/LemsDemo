package com.pcjz.lems.ui.homepage.bean;

/**
 * Created by ${YGP} on 2017/7/17.
 */

public class UpdateResponModel {
    public String code;
    public String appPackageName;
    public String versionCode;
    public String versionName;
    public String appAndroidPath;
    public String appType;
    public String remark;
    public String updateContent;

    public String getUpdateContent() {
        return updateContent;
    }

    public void setUpdateContent(String updateContent) {
        this.updateContent = updateContent;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getAppPackageName() {
        return appPackageName;
    }

    public void setAppPackageName(String appPackageName) {
        this.appPackageName = appPackageName;
    }

    public String getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getAppAndroidPath() {
        return appAndroidPath;
    }

    public void setAppAndroidPath(String appAndroidPath) {
        this.appAndroidPath = appAndroidPath;
    }

    public String getAppType() {
        return appType;
    }

    public void setAppType(String appType) {
        this.appType = appType;
    }


}
