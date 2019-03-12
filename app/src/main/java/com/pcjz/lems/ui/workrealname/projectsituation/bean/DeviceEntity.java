package com.pcjz.lems.ui.workrealname.projectsituation.bean;

import android.support.annotation.NonNull;

import java.util.List;

/**
 * Created by Greak on 2017/9/21.
 */

public class DeviceEntity implements Comparable<DeviceEntity>{
    public String devNo;
    public String devName;
    public String devState;
    public String totalTimes;
    public String todayUseTimes;
    public String devCurState;
    public String relateMachineNo;
    public String devType;
    public int sequeNum;
    public List<DeviceDriverEntity> mdevDrvList;
    public List<RelateMachineEntity> mRelateMachineList;

    public int getSequeNum() {
        return sequeNum;
    }

    public void setSequeNum(int sequeNum) {
        this.sequeNum = sequeNum;
    }

    public List<RelateMachineEntity> getmRelateMachineList() {
        return mRelateMachineList;
    }

    public String getDevType() {
        return devType;
    }

    public void setDevType(String devType) {
        this.devType = devType;
    }

    public void setmRelateMachineList(List<RelateMachineEntity> mRelateMachineList) {
        this.mRelateMachineList = mRelateMachineList;
    }

    public String getTotalTimes() {
        return totalTimes;
    }

    public void setTotalTimes(String totalTimes) {
        this.totalTimes = totalTimes;
    }

    public String getTodayUseTimes() {
        return todayUseTimes;
    }

    public void setTodayUseTimes(String todayUseTimes) {
        this.todayUseTimes = todayUseTimes;
    }

    public String getDevCurState() {
        return devCurState;
    }

    public void setDevCurState(String devCurState) {
        this.devCurState = devCurState;
    }

    public String getDevNo() {
        return devNo;
    }

    public void setDevNo(String devNo) {
        this.devNo = devNo;
    }

    public String getDevName() {
        return devName;
    }

    public void setDevName(String devName) {
        this.devName = devName;
    }

    public String getDevState() {
        return devState;
    }

    public void setDevState(String devState) {
        this.devState = devState;
    }

    public String getRelateMachineNo() {
        return relateMachineNo;
    }

    public void setRelateMachineNo(String relateMachineNo) {
        this.relateMachineNo = relateMachineNo;
    }

    public List<DeviceDriverEntity> getMdevDrvList() {
        return mdevDrvList;
    }

    public void setMdevDrvList(List<DeviceDriverEntity> mdevDrvList) {
        this.mdevDrvList = mdevDrvList;
    }


    @Override
    public int compareTo(@NonNull DeviceEntity deviceEntity) {
        return (this.sequeNum == deviceEntity.getSequeNum() ? 0 : (this.sequeNum > deviceEntity.getSequeNum() ? 1 : -1));
    }
}
