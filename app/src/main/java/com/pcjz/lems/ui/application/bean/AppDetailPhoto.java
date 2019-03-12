package com.pcjz.lems.ui.application.bean;

/**
 * created by yezhengyu on 2017/5/23 15:07
 */
public class AppDetailPhoto {
    private String photoPath;
    private String id;
    private String uploadTime;
    private String upgradeId;

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(String uploadTime) {
        this.uploadTime = uploadTime;
    }

    public String getUpgradeId() {
        return upgradeId;
    }

    public void setUpgradeId(String upgradeId) {
        this.upgradeId = upgradeId;
    }
}
