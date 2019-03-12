package com.pcjz.lems.ui.workrealname.personinfo;

import android.content.Context;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.pcjz.lems.business.common.utils.StringUtils;
import com.pcjz.lems.business.config.AppConfig;
import com.pcjz.lems.business.constant.SysCode;
import com.pcjz.lems.business.entity.SelectDialogEntity;
import com.pcjz.lems.business.webapi.MainApi;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Greak on 2017/9/26.
 */

public class GetPublicPersonType {
    public static List<SelectDialogEntity> mList;
    public static List<SelectDialogEntity> mmList;


    public static List<SelectDialogEntity> getCommonPersonType(Context context){
        HttpEntity entity = null;
        MainApi.requestCommon(context, AppConfig.GET_COMMON_PERSON_TYPE, entity, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String httpResult = new String(bytes);
                try {
                    JSONObject obj = new JSONObject(httpResult);
                    if(StringUtils.equals(obj.getString("code"), SysCode.SUCCESS)){
                        JSONArray tempList = obj.getJSONArray("data");
                        mList = new ArrayList<SelectDialogEntity>();
                        for(int j = 0; j < tempList.length(); j ++){
                            JSONObject itemObj = tempList.getJSONObject(j);
                            SelectDialogEntity mEntity = new SelectDialogEntity();
                            mEntity.setmSelectId(itemObj.getString("id"));
                            mEntity.setmSelectName(itemObj.getString("name"));
                            mEntity.setmIsSelect(false);
                            mList.add(mEntity);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

            }
        });

        return mList;
    }

    public static List<SelectDialogEntity>      getSpecialPersonType(Context context){
        HttpEntity entity = null;
        MainApi.requestCommon(context, AppConfig.GET_SPECIAL_PERSON_TYPE, entity, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String httpResult = new String(bytes);
                try {
                    JSONObject obj = new JSONObject(httpResult);
                    if(StringUtils.equals(obj.getString("code"), SysCode.SUCCESS)){
                        JSONArray tempList = obj.getJSONArray("data");
                        mmList = new ArrayList<SelectDialogEntity>();
                        for(int j = 0; j < tempList.length(); j ++){
                            JSONObject itemObj = tempList.getJSONObject(j);
                            SelectDialogEntity mEntity = new SelectDialogEntity();
                            mEntity.setmSelectId(itemObj.getString("id"));
                            mEntity.setmSelectName(itemObj.getString("name"));
                            mEntity.setmIsSelect(false);
                            mmList.add(mEntity);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

            }
        });

        return mmList;
    }




}
