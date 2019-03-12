package com.pcjz.lems.business.model.personInfo;

import com.pcjz.lems.business.entity.person.PersonEntity;

/**
 * Created by Greak on 2017/9/13.
 */

public interface PersonInfoModel {
    void getPersonInfo(String userId, OnPersonInfoListener listener);
    void ModPersonInfo(PersonEntity personEntity, OnPersonInfoListener listener);
    void AddPersonInfo(PersonEntity personEntity, OnPersonInfoListener listener);
}
