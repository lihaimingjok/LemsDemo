package com.pcjz.lems.business.entity;

/**
 * Created by Greak on 2017/9/19.
 */

public class SelectDialogEntity {
    public String mSelectName;
    public String mSelectId;
    private boolean mIsSelect = false;

    public String getmSelectName() {
        return mSelectName;
    }

    public void setmSelectName(String mSelectName) {
        this.mSelectName = mSelectName;
    }

    public String getmSelectId() {
        return mSelectId;
    }

    public void setmSelectId(String mSelectId) {
        this.mSelectId = mSelectId;
    }

    public boolean ismIsSelect() {
        return mIsSelect;
    }

    public void setmIsSelect(boolean mIsSelect) {
        this.mIsSelect = mIsSelect;
    }
}
