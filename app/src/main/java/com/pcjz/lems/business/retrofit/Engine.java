package com.pcjz.lems.business.retrofit;

import com.pcjz.lems.business.base.basebean.BaseData;
import com.pcjz.lems.business.base.basebean.BaseListData;
import com.pcjz.lems.business.entity.SelectEntity;
import com.pcjz.lems.ui.login.bean.LoginInfo;
import com.pcjz.lems.ui.login.bean.OperatePrivilegesInfo;
import com.pcjz.lems.ui.workrealname.manageequipment.bean.DeviceBean;
import com.pcjz.lems.ui.workrealname.manageequipment.bean.EquTypeBean;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

/**
 * created by yezhengyu on 2018/11/22 14:30
 */

public interface Engine {
    @GET("book/search")
    Call<Book> getSearchBook(@Query("q") String name,
                             @Query("tag") String tag,
                             @Query("start") int start,
                             @Query("count") int count);

    @Headers({"Content-Type: application/json","Accept: application/json"})//需要添加头
    @POST("client/login")
    Call<ResponseBody> getMessage(@Body RequestBody info);   // 请求体味RequestBody 类型

    @POST("client/userAuth/getUserOperatPrivileges")
    Call<BaseListData<OperatePrivilegesInfo>> getUserOperatPrivileges();

    //上传图片
    @Multipart
    @POST("client/img/upload")
    Observable<BaseData<String>> upload(@Part MultipartBody.Part file);

    @POST("client/user/mod")
    Observable<BaseData<LoginInfo>> requestChangePortrait(@Body RequestBody info);

    @POST("client/deviceLargeDeviceInfo/getPage")
    Observable<BaseData<DeviceBean>> requestList(@Body RequestBody info);

}
