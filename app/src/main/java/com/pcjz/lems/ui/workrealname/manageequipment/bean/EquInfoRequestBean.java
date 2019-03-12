package com.pcjz.lems.ui.workrealname.manageequipment.bean;

/**
 * created by yezhengyu on 2017/10/11 14:41
 */

public class EquInfoRequestBean {
    private String id;
    private String deviceCode;
    private String deviceName;
    private String deviceSerial;
    private String servicesPhone;
    private String remark;
    private String deviceTypeId;
    private String deviceStatus;
    private String useStatus;
    private String projectId;
    private String[] newbindPersons;
    private String[] unbindPersons;

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

    public String getDeviceSerial() {
        return deviceSerial;
    }

    public void setDeviceSerial(String deviceSerial) {
        this.deviceSerial = deviceSerial;
    }

    public String getServicesPhone() {
        return servicesPhone;
    }

    public void setServicesPhone(String servicesPhone) {
        this.servicesPhone = servicesPhone;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getDeviceTypeId() {
        return deviceTypeId;
    }

    public void setDeviceTypeId(String deviceTypeId) {
        this.deviceTypeId = deviceTypeId;
    }

    public String getDeviceStatus() {
        return deviceStatus;
    }

    public void setDeviceStatus(String deviceStatus) {
        this.deviceStatus = deviceStatus;
    }

    public String getUseStatus() {
        return useStatus;
    }

    public void setUseStatus(String useStatus) {
        this.useStatus = useStatus;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String[] getNewbindPersons() {
        return newbindPersons;
    }

    public void setNewbindPersons(String[] newbindPersons) {
        this.newbindPersons = newbindPersons;
    }

    public String[] getUnbindPersons() {
        return unbindPersons;
    }

    public void setUnbindPersons(String[] unbindPersons) {
        this.unbindPersons = unbindPersons;
    }
}
