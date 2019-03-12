package com.pcjz.lems.business.entity.person;

import com.pcjz.lems.business.entity.SelectDialogEntity;

import java.util.List;

/**
 * Created by Greak on 2017/9/26.
 */

public class PersonTypeEntity {

    public List<SelectDialogEntity> mSelectPerssonTypeList;

    public List<SelectDialogEntity> getmSelectPerssonTypeList() {
        return mSelectPerssonTypeList;
    }

    public void setmSelectPerssonTypeList(List<SelectDialogEntity> mSelectPerssonTypeList) {
        this.mSelectPerssonTypeList = mSelectPerssonTypeList;
    }
}
