package com.pcjz.lems.business.entity.offline;

import java.io.Serializable;

/**
 * created by yezhengyu on 2017/5/3 11:55
 */
public class OfflinePeriodInfo implements Serializable{
    private String isSealed;
    private String updateUserId;
    private String tenantId;
    private String projectManagerId;
    private String updateTime;
    private String comId;
    private String idTree;
    private String nameTree;
    private String projectId;

    //当前id
    private String id;
    //名字
    private String periodName;
    private String url;
    private int length;
    private int finished;
    //当前位置
    private int position;
    //当前子位置
    private int childPosition;

    private String phone;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getChildPosition() {
        return childPosition;
    }

    public void setChildPosition(int childPosition) {
        this.childPosition = childPosition;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
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

    public String getIsSealed() {
        return isSealed;
    }

    public void setIsSealed(String isSealed) {
        this.isSealed = isSealed;
    }

    public String getUpdateUserId() {
        return updateUserId;
    }

    public void setUpdateUserId(String updateUserId) {
        this.updateUserId = updateUserId;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getPeriodName() {
        return periodName;
    }

    public void setPeriodName(String periodName) {
        this.periodName = periodName;
    }

    public String getProjectManagerId() {
        return projectManagerId;
    }

    public void setProjectManagerId(String projectManagerId) {
        this.projectManagerId = projectManagerId;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getComId() {
        return comId;
    }

    public void setComId(String comId) {
        this.comId = comId;
    }

    public String getIdTree() {
        return idTree;
    }

    public void setIdTree(String idTree) {
        this.idTree = idTree;
    }

    public String getNameTree() {
        return nameTree;
    }

    public void setNameTree(String nameTree) {
        this.nameTree = nameTree;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }
}
