package com.pcjz.lems.ui.workrealname.projectsituation.bean;

/**
 * Created by greak on 2017/9/23.
 */

public class WarnMsgEntity {
    public String msgId;
    public String warnDevice;
    public String warnTitle;
    public String warnContent;
    public String warnDate;
    public String warnPart;
    public int isRead;

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public int getIsRead() {
        return isRead;
    }

    public void setIsRead(int isRead) {
        this.isRead = isRead;
    }

    public String getWarnPart() {
        return warnPart;
    }

    public void setWarnPart(String warnPart) {
        this.warnPart = warnPart;
    }

    public String getWarnDevice() {
        return warnDevice;
    }

    public void setWarnDevice(String warnDevice) {
        this.warnDevice = warnDevice;
    }

    public String getWarnTitle() {
        return warnTitle;
    }

    public void setWarnTitle(String warnTitle) {
        this.warnTitle = warnTitle;
    }

    public String getWarnContent() {
        return warnContent;
    }

    public void setWarnContent(String warnContent) {
        this.warnContent = warnContent;
    }

    public String getWarnDate() {
        return warnDate;
    }

    public void setWarnDate(String warnDate) {
        this.warnDate = warnDate;
    }
}
