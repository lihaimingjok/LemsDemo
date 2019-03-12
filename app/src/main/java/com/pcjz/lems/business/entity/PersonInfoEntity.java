package com.pcjz.lems.business.entity;

import com.pcjz.lems.business.widget.contractlist.CN;

import java.io.Serializable;

/**
 * created by yezhengyu on 2017/9/19 15:49
 */

public class PersonInfoEntity implements CN,Serializable{
    private String empName;
    private String jobName;
    private String facephoto;
    private String backGroundColor;
    private String personId;
    private String projectPersonId;
    private String deleteStatus;
    private String sortLetters;  //显示数据拼音的首字母

    public String getEmpName() {
        return empName;
    }

    public void setEmpName(String empName) {
        this.empName = empName;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getFacephoto() {
        return facephoto;
    }

    public void setFacephoto(String facephoto) {
        this.facephoto = facephoto;
    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public String getProjectPersonId() {
        return projectPersonId;
    }

    public void setProjectPersonId(String projectPersonId) {
        this.projectPersonId = projectPersonId;
    }

    public String getSortLetters() {
        return sortLetters;
    }

    public void setSortLetters(String sortLetters) {
        this.sortLetters = sortLetters;
    }

    public String getBackGroundColor() {
        return backGroundColor;
    }

    public void setBackGroundColor(String backGroundColor) {
        this.backGroundColor = backGroundColor;
    }

    public String getDeleteStatus() {
        return deleteStatus;
    }

    public void setDeleteStatus(String deleteStatus) {
        this.deleteStatus = deleteStatus;
    }

    @Override
    public String chineseText() {
        return empName;
    }

    @Override
    public String chineseContent() {
        return jobName;
    }
}
