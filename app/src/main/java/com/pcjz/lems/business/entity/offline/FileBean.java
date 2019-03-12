package com.pcjz.lems.business.entity.offline;

import java.io.Serializable;

/**
 * Created by kun on 2016/11/10.
 */
public class FileBean implements Serializable {

    //每一个下载对象的信息
    private int id;
    private String fileName;
    private String url;
    private int length;
    private int finished;
    private String projectPeriodId;

    public String getProjectPeriodId() {
        return projectPeriodId;
    }

    public void setProjectPeriodId(String projectPeriodId) {
        this.projectPeriodId = projectPeriodId;
    }

    public FileBean() {
    }

    public FileBean(int id, String fileName, String url, int length, String projectPeriodId) {
        this.id = id;
        this.fileName = fileName;
        this.url = url;
        this.length = length;
        this.projectPeriodId = projectPeriodId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
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

    @Override
    public String toString() {
        int progress = (int) (finished*1.0f/length *100);
        return "FileBean{" +
                "id=" + id +
                ", fileName='" + fileName + '\'' +
                ", length=" + length +
                ", finished=" + finished +
                "，progress="+progress+
                '}';
    }
}
