package com.pcjz.lems.business.entity;

/**
 * created by yezhengyu on 2017/4/8 16:00
 */
public class ProjectPeroidInfo {

    public String isSealed;
    public String updateUserId;
    public String tenantId;
    public String periodName;
    public String projectManagerId;
    public String updateTime;
    public String id;
    public String comId;
    public String idTree;
    public String nameTree;
    public String projectId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ProjectPeroidInfo(String periodName, String projectId, String id) {
        this.periodName = periodName;
        this.projectId = projectId;
        this.id = id;
    }

    public ProjectPeroidInfo() {

    }

    public String getPeriodName() {
        return periodName;
    }

    public void setPeriodName(String periodName) {
        this.periodName = periodName;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
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
}
