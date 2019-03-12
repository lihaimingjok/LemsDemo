package com.pcjz.lems.business.constant;

import com.pcjz.lems.R;
import com.pcjz.lems.business.context.AppContext;
/**
 * Created by ${YGP} on 2017/3/28.
 */

public class ResultStatus {
    public static final String SUCCESS = "0";
    public static final String NO_REGIST = "1001";
    public static final String PW_ERROR = "1002";
    public static final String ALREADY_UPLOAD_ERROR = "5004";
    public static final String TOKEN = "token";
    public static final String USER_ID = "user_id";
    public static final String PHONE = "phone";
    public static final String REALNAME = "realname";
    public static final String POSTNAME = "postname";
    public static final String POSTID = "postid";
    public static final String PORTRAIT_IMG = "portrait_img";
    public static final String COMPANY = "company";
    public static final String COMPANY_CODE = "company_code";
    public static final String LOGIN_STATUS = "login_status";
    public static final String LOGIN_USERNAME = "login_username";
    public static final String LOGINED = "logined";
    public static final String UNLOGIN = "unlogin";
    public static final String TOKEN_INVALID = AppContext.mResource.getString(R.string.token_invalid);
    public static final String POST_CONSTRUCTION_TEAM = AppContext.mResource.getString(R.string.post_construction_team);
    public static final String POST_QULITY_INSPECTOR = AppContext.mResource.getString(R.string.post_qulity_inspector);
    public static final String POST_SUPERVISOR = AppContext.mResource.getString(R.string.post_supervisor);
    public static final String POST_MANAGER = AppContext.mResource.getString(R.string.post_manager);
    public static final String NETWORKSTATE1 = AppContext.mResource.getString(R.string.networkstate1);
    public static final String NETWORKSTATE2 = AppContext.mResource.getString(R.string.networkstate2);
    public static final String NETWORKSTATE3 = AppContext.mResource.getString(R.string.networkstate3);
    public static final String NETWORKSTATE4 = AppContext.mResource.getString(R.string.networkstate4);
    public static final String NETWORKSTATE5 = AppContext.mResource.getString(R.string.networkstate5);
    public static final String UNSEALED = "0";
    public static final String SEALED = "1";
    public static final String ISGENERAL = "1";
    public static final String NOGENERAL = "0";
    public static final String DEVICE_TYPE = "塔吊起重机";
}
