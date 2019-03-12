package com.pcjz.lems.business.entity.offline;

import java.util.List;

/**
 * created by yezhengyu on 2017/5/3 11:52
 */
public class OfflineProjectInfo {
    private String isSealed;
    private String updateUserId;
    private String projectType;
    private String updateTime;
    private String constructionId;
    private int periodCount;
    private String tenantId;
    private String id;
    private String projectName;
    private List<OfflinePeriodInfo> list;

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

    public String getProjectType() {
        return projectType;
    }

    public void setProjectType(String projectType) {
        this.projectType = projectType;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getConstructionId() {
        return constructionId;
    }

    public void setConstructionId(String constructionId) {
        this.constructionId = constructionId;
    }

    public int getPeriodCount() {
        return periodCount;
    }

    public void setPeriodCount(int periodCount) {
        this.periodCount = periodCount;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public List<OfflinePeriodInfo> getList() {
        return list;
    }

    public void setList(List<OfflinePeriodInfo> list) {
        this.list = list;
    }
}
