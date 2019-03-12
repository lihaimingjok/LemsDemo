package com.pcjz.lems.business.model.personInfo;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.pcjz.lems.business.entity.person.PersonEntity;
import com.pcjz.lems.business.volley.VolleyRequest;

/**
 * Created by Greak on 2017/9/13.
 */

public class PersonInfoModelImpl implements PersonInfoModel{

    @Override
    public void getPersonInfo(String userId, final OnPersonInfoListener listener) {
        VolleyRequest.newInstance().newGsonRequest("http://www.weather.com.cn/data/sk/101280601.html",
                PersonEntity.class, new Response.Listener<PersonEntity>() {
                    @Override
                    public void onResponse(PersonEntity personEntity) {
                        if(personEntity != null){
                            listener.success(personEntity);
                        }else{
                            listener.error();
                        }
                    }
        }, new Response.ErrorListener(){

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        listener.error();
                    }
        });
    }

    @Override
    public void ModPersonInfo(PersonEntity personEntity, OnPersonInfoListener listener) {

    }

    @Override
    public void AddPersonInfo(PersonEntity personEntity, OnPersonInfoListener listener) {

    }
}
