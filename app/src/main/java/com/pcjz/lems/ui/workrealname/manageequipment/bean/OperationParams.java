package com.pcjz.lems.ui.workrealname.manageequipment.bean;

/**
 * created by yezhengyu on 2017/10/10 14:53
 */

public class OperationParams {
    private String projectId;
    private String empName;
    private String startTime;
    private String endTime;
    private String empWorkCode;
    private String deviceId;

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getEmpName() {
        return empName;
    }

    public void setEmpName(String empName) {
        this.empName = empName;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getEmpWorkCode() {
        return empWorkCode;
    }

    public void setEmpWorkCode(String empWorkCode) {
        this.empWorkCode = empWorkCode;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
}
