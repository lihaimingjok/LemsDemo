package com.pcjz.lems.ui.workrealname.projectsituation.bean;

import java.util.List;

/**
 * created by yezhengyu on 2017/10/13 10:50
 */

public class DeviceStaticEntity {
    private String isSealed;
    private String updateUserId;
    private String typeName;
    private String tenantId;
    private String remark;
    private String updateTime;
    private String id;
    private List<DeviceStaticListEntity> list;

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

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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

    public List<DeviceStaticListEntity> getList() {
        return list;
    }

    public void setList(List<DeviceStaticListEntity> list) {
        this.list = list;
    }
}
