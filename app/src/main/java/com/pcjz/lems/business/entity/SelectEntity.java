package com.pcjz.lems.business.entity;

import java.io.Serializable;

/**
 * created by yezhengyu on 2017/7/3 11:24
 */

public class SelectEntity implements Serializable{
    private String id;
    private String name;
    private boolean isSelect = false;

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
