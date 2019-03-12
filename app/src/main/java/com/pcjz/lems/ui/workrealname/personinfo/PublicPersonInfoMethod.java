package com.pcjz.lems.ui.workrealname.personinfo;

import android.content.Context;
import android.graphics.Bitmap;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.pcjz.lems.business.base.basebean.BaseData;
import com.pcjz.lems.business.common.utils.CommonUtil;
import com.pcjz.lems.business.common.utils.StringUtils;
import com.pcjz.lems.business.common.utils.TLog;
import com.pcjz.lems.business.config.AppConfig;
import com.pcjz.lems.business.config.ConfigPath;
import com.pcjz.lems.business.constant.SysCode;
import com.pcjz.lems.business.context.AppContext;
import com.pcjz.lems.business.entity.SelectDialogEntity;
import com.pcjz.lems.business.webapi.MainApi;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Greak on 2017/9/26.
 */

public class PublicPersonInfoMethod {
    public static List<SelectDialogEntity> mList;
    public static List<SelectDialogEntity> mmList;
    public static String imgTempPath = "";
    public static String realPicTempPath = "";
    public static String IDFrontTempPath;
    public static String IDOppositeTempPath;
    public static String specialPicTempPath;
    public static String safePicTempPath;


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

    public static List<SelectDialogEntity> getSpecialPersonType(Context context){
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


    public static String getServerImgTempPath(String photoPath, Bitmap bitmap){
        String tempPath;
        String imagePath = photoPath;
        Bitmap mbitmap = bitmap;
        File[] files = new File[1];
        try {
            files[0] = CommonUtil.saveFile(mbitmap, ConfigPath.downloadImagePath, CommonUtil.getPicNameFromPath(imagePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        AppContext.getInstance().initWebApiUploadImg();
        MainApi.uploadFile(files[0], AppConfig.IMG_UPLOAD_URL, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                try {
                    String httpResult = new String(bytes);
                    TLog.log("getUploadImages : " + httpResult);
                    BaseData<String> basedata = new Gson().fromJson(httpResult,
                            new TypeToken<BaseData<String>>() {
                            }.getType());
                    if (StringUtils.equals(basedata.getCode(), SysCode.SUCCESS)) {
                        imgTempPath = "";
                        imgTempPath = basedata.getData();
                        AppContext.getInstance().reinitWebApi();


                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    AppContext.getInstance().reinitWebApi();
                }

            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                AppContext.getInstance().reinitWebApi();
            }

            @Override
            public void onProgress(int bytesWritten, int totalSize) {
                super.onProgress(bytesWritten, totalSize);
                AppContext.getInstance().reinitWebApi();
            }
        });

        return imgTempPath;
    }






}
