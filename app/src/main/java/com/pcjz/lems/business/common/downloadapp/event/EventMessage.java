package com.pcjz.lems.business.common.downloadapp.event;

/**
 * Created by kun on 2016/11/10.
 */
public class EventMessage {

    /**
     * 1 获取下载文件的长度
     * 2 下载完成
     * 3 下载进度刷新
     */
    private int type;

    private Object object;

    public EventMessage(int type, Object object) {
        this.type = type;
        this.object = object;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }
}
