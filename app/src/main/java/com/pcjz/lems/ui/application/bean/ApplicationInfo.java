package com.pcjz.lems.ui.application.bean;

import java.util.ArrayList;

/**
 * created by yezhengyu on 2017/5/22 14:20
 */
public class ApplicationInfo {

    public int pageNo;
    public int pageSize;
    public int totalPage;
    public int totalRecord;
    public RecordParams params;
    public ArrayList<Application> results;

    public ArrayList<Application> getResults() {
        return results;
    }

    public void setResults(ArrayList<Application> results) {
        this.results = results;
    }
}
