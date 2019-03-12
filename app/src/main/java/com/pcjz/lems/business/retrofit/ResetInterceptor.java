package com.pcjz.lems.business.retrofit;

import com.pcjz.lems.business.constant.ResultStatus;
import com.pcjz.lems.business.storage.SharedPreferencesManager;
import com.pcjz.lems.ui.myinfo.MyInfoActivity;

import java.io.IOException;
import java.util.Locale;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * created by yezhengyu on 2018/11/26 15:50
 */

public class ResetInterceptor implements Interceptor {

    private final static String HOST = "172.16.12.150";

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request;
        if (MyInfoActivity.IMG_STATUS) {
            request = chain.request()
                    .newBuilder()
                    .addHeader("Accept-Language", Locale.getDefault().toString())
                    .addHeader("Host", HOST)
                    .addHeader("Connection", "Keep-Alive")
                    .addHeader("Accept-Charset", "UTF-8")
                    .addHeader("application1", SharedPreferencesManager.getString(ResultStatus.TOKEN))
                    .addHeader("application2", SharedPreferencesManager.getString(ResultStatus.COMPANY_CODE))
                    .addHeader("application3", "android-v1.0")
                    .addHeader("application4", SharedPreferencesManager.getString(ResultStatus.PHONE))
                    .addHeader("application5", SharedPreferencesManager.getString(ResultStatus.USER_ID))
                    .build();
        } else {
            request = chain.request()
                    .newBuilder()
                    .addHeader("Accept-Language", Locale.getDefault().toString())
                    .addHeader("Host", HOST)
                    .addHeader("Connection", "Keep-Alive")
                    .addHeader("Accept-Charset", "UTF-8")
                    .addHeader("application1", SharedPreferencesManager.getString(ResultStatus.TOKEN))
                    .addHeader("application2", SharedPreferencesManager.getString(ResultStatus.COMPANY_CODE))
                    .addHeader("application3", "android-v1.0")
                    .addHeader("application4", SharedPreferencesManager.getString(ResultStatus.PHONE))
                    .addHeader("application5", SharedPreferencesManager.getString(ResultStatus.USER_ID))
                    .addHeader("Content-Type", "application/json")
                    .build();
        }
        return chain.proceed(request);
    }

}
