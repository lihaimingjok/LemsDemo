package com.pcjz.lems.ui.workrealname.manageequipment.bean;

import com.pcjz.lems.business.entity.PersonInfoEntity;

import java.util.List;

/**
 * created by yezhengyu on 2017/9/27 18:14
 */

public class EquInfoBean {

    private String deviceSerial;
    private String isSealed;
    private String deviceTypeId;
    private String servicesPhone;
    private String updateUserId;
    private String tenantId;
    private String remark;
    private String updateTime;
    private String id;
    private String deviceCode;
    private String deviceName;
    private String useStatus;

    private String deviceTypeName;
    private List<PersonInfoEntity> bindPersons;
    private String bindPerson;
    //是否绑定了考勤机
    private int bindAttendanceDeviceSize;

    public String getDeviceTypeName() {
        return deviceTypeName;
    }

    public void setDeviceTypeName(String deviceTypeName) {
        this.deviceTypeName = deviceTypeName;
    }

    public List<PersonInfoEntity> getBindPersons() {
        return bindPersons;
    }

    public void setBindPersons(List<PersonInfoEntity> bindPersons) {
        this.bindPersons = bindPersons;
    }

    public String getDeviceSerial() {
        return deviceSerial;
    }

    public void setDeviceSerial(String deviceSerial) {
        this.deviceSerial = deviceSerial;
    }

    public String getIsSealed() {
        return isSealed;
    }

    public void setIsSealed(String isSealed) {
        this.isSealed = isSealed;
    }

    public String getDeviceTypeId() {
        return deviceTypeId;
    }

    public void setDeviceTypeId(String deviceTypeId) {
        this.deviceTypeId = deviceTypeId;
    }

    public String getServicesPhone() {
        return servicesPhone;
    }

    public void setServicesPhone(String servicesPhone) {
        this.servicesPhone = servicesPhone;
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

    public String getDeviceCode() {
        return deviceCode;
    }

    public void setDeviceCode(String deviceCode) {
        this.deviceCode = deviceCode;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getUseStatus() {
        return useStatus;
    }

    public void setUseStatus(String useStatus) {
        this.useStatus = useStatus;
    }

    public String getBindPerson() {
        return bindPerson;
    }

    public void setBindPerson(String bindPerson) {
        this.bindPerson = bindPerson;
    }

    public int getBindAttendanceDeviceSize() {
        return bindAttendanceDeviceSize;
    }

    public void setBindAttendanceDeviceSize(int bindAttendanceDeviceSize) {
        this.bindAttendanceDeviceSize = bindAttendanceDeviceSize;
    }
}
