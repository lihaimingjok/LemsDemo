package com.pcjz.lems.business.model.personInfo;

import com.pcjz.lems.business.entity.person.PersonEntity;

/**
 * Created by Greak on 2017/9/13.
 * request result
 */

public interface OnPersonInfoListener {
    void success(PersonEntity personEntity);

    void error();
}
