package com.pcjz.lems.ui.workrealname.manageequipment.bean;

import java.util.List;

/**
 * created by yezhengyu on 2017/10/10 14:51
 */

public class OperationBean {
    public int pageNo;
    public int pageSize;
    public OperationParams params;

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public OperationParams getParams() {
        return params;
    }

    public void setParams(OperationParams params) {
        this.params = params;
    }
}
