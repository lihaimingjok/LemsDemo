package com.pcjz.lems.ui.login;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.pcjz.lems.R;
import com.pcjz.lems.business.context.AppContext;
import com.pcjz.lems.business.base.BaseActivity;
import com.pcjz.lems.business.base.basebean.BaseData;
import com.pcjz.lems.business.base.basebean.BaseListData;
import com.pcjz.lems.business.common.utils.Base64Util;
import com.pcjz.lems.business.common.utils.MD5;
import com.pcjz.lems.business.common.utils.StringUtils;
import com.pcjz.lems.business.common.utils.TDevice;
import com.pcjz.lems.business.common.utils.TLog;
import com.pcjz.lems.business.config.AppConfig;
import com.pcjz.lems.business.constant.ResultStatus;
import com.pcjz.lems.business.storage.SharedPreferencesManager;
import com.pcjz.lems.business.webapi.ApiHttpClient;
import com.pcjz.lems.business.webapi.MainApi;
import com.pcjz.lems.ui.homepage.HomePageActivity;
import com.pcjz.lems.ui.login.bean.LoginInfo;
import com.pcjz.lems.ui.login.bean.OperatePrivilegesInfo;
import com.umeng.analytics.MobclickAgent;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends BaseActivity implements View.OnLayoutChangeListener {

    private static final String TAG = "tag-----alias";
    private AutoCompleteTextView etUsername;
    private EditText etPassword;
    private Button btLogin;
    private ScrollView mLoginFormView;
    private LinearLayout llLoginForm;
    private TextView tvTel;
    private ImageView ivClearUsername, ivClearPassword;
    private RelativeLayout rlLogin;
    private LinearLayout llErrorNotification;
    private StringEntity mStringEntity;
    private int difference;
    private boolean isMoved = false;
    int screenHeight, keyBordHeight;
    private View activityRootView;
    private LinearLayout llTop;
    private View mViewPh;
    private View mViewPw;
    private View mViewCom;
    private EditText etCode;
    private RelativeLayout rlBack;
    private LinearLayout llBottom;
    private View mChildOfContent;

    @Override
    public void setView() {
        setContentView(R.layout.activity_login);
    }


    @Override
    protected void setBack() {
        super.setBack();
        /*rlBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (GuideActivity.guideActivity != null && GuideActivity.guideActivity.isFinishing()) {
                    Intent intent = new Intent(LoginActivity.this, GuideActivity.class);
                    startActivity(intent);
                }
                finish();
            }
        });*/
    }

    @Override
    public void initView() {
        llTop = (LinearLayout) findViewById(R.id.ll_top);
        activityRootView = findViewById(R.id.rl_login);
        //获取屏幕高度
        screenHeight = this.getWindowManager().getDefaultDisplay().getHeight();
        //阀值设置为屏幕高度的1/3
        keyBordHeight = screenHeight / 3;
        rlBack = (RelativeLayout) findViewById(R.id.rl_back);
        etUsername = (AutoCompleteTextView) findViewById(R.id.autocompletetv_username);
        etPassword = (EditText) findViewById(R.id.et_password);
        etCode = (EditText) findViewById(R.id.et_company_code);
        btLogin = (Button) findViewById(R.id.bt_login);
        //添加返回，企业编号
        mViewPh = findViewById(R.id.view_phone);
        mViewPw = findViewById(R.id.view_password);
        mViewCom = findViewById(R.id.view_company);

        mLoginFormView = (ScrollView) findViewById(R.id.login_form);
        tvTel = (TextView) findViewById(R.id.tv_tel);
        ivClearUsername = (ImageView) findViewById(R.id.iv_clear_username);
        ivClearPassword = (ImageView) findViewById(R.id.iv_clear_password);
        rlLogin = (RelativeLayout) findViewById(R.id.rl_login);
        llLoginForm = (LinearLayout) findViewById(R.id.email_login_form);
        llErrorNotification = (LinearLayout) findViewById(R.id.ll_error_notification);
        llBottom = (LinearLayout) findViewById(R.id.ll_content_bottom);
        etPassword.setOnKeyListener(onKey);
        if (!StringUtils.isEmpty(SharedPreferencesManager.getString(ResultStatus.LOGIN_USERNAME))) {
            etUsername.setText(SharedPreferencesManager.getString(ResultStatus.LOGIN_USERNAME));
        }
        RelativeLayout rootView = (RelativeLayout) findViewById(R.id.rl_login);
        //contentlayout是最外层布局
        mChildOfContent = rootView.getChildAt(0);
        mChildOfContent.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    public void onGlobalLayout() {
                        possiblyResizeChildOfContent();
                    }
                });
    }

    @Override
    protected void setStatusBar() {
        //StatusBarUtil.setColor(this, ContextCompat.getColor(getApplicationContext(), R.color.white));
    }

    int usableHeightPrevious;

    private void possiblyResizeChildOfContent() {
        int usableHeightNow = computeUsableHeight();
        if (usableHeightNow != usableHeightPrevious) {
            int usableHeightSansKeyboard = mChildOfContent.getRootView().getHeight();
            int heightDifference = usableHeightSansKeyboard - usableHeightNow;
            if (heightDifference > (usableHeightSansKeyboard / 4)) {
                // 键盘弹出
                llBottom.setVisibility(View.GONE);
            } else {
                // 键盘收起
                llBottom.setVisibility(View.VISIBLE);
            }
            mChildOfContent.requestLayout();
            usableHeightPrevious = usableHeightNow;
        }
    }

    private int computeUsableHeight() {
        Rect r = new Rect();
        mChildOfContent.getWindowVisibleDisplayFrame(r);
        return (r.bottom - r.top);
    }

    @Override
    public void setListener() {
        if (btLogin == null) return;
        btLogin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin();
            }
        });

        tvTel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent phoneIntent = new Intent("android.intent.action.DIAL", Uri.parse("tel:" + tvTel.getText().toString()));
                startActivity(phoneIntent);
            }
        });
        etUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                initIvClearName(s.toString());
            }
        });

        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                initIvClearPass(s.toString());
            }
        });

        etUsername.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {//获得焦点
                    mViewPh.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.common_blue_color));
                    initIvClearName(etUsername.getText().toString());
                } else {//失去焦点
                    mViewPh.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.txt_gray_divider));
                }
            }
        });
        etPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {//获得焦点
                    mViewPw.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.common_blue_color));
                    initIvClearPass(etPassword.getText().toString());
                } else {//失去焦点
                    mViewPw.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.txt_gray_divider));
                }
            }
        });
        etCode.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {//获得焦点
                    mViewCom.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.common_blue_color));
                } else {//失去焦点
                    mViewCom.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.txt_gray_divider));
                }
            }
        });

        ivClearUsername.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                etUsername.setText("");
                ivClearUsername.setVisibility(View.GONE);
            }
        });
        ivClearPassword.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                etPassword.setText("");
                ivClearPassword.setVisibility(View.GONE);
            }
        });
        etUsername.requestFocus();
    }

    private void initIvClearName(String s) {
        if (!StringUtils.isEmpty(s.toString())) {
            ivClearUsername.setVisibility(View.VISIBLE);
            if (llErrorNotification.getVisibility() == View.VISIBLE) {
                llErrorNotification.setVisibility(View.INVISIBLE);
            }
        } else {
            ivClearUsername.setVisibility(View.GONE);
        }
    }

    private void initIvClearPass(String s) {
        if (!StringUtils.isEmpty(s)) {
            ivClearPassword.setVisibility(View.VISIBLE);
            if (llErrorNotification.getVisibility() == View.VISIBLE) {
                llErrorNotification.setVisibility(View.INVISIBLE);
            }
        } else {
            ivClearPassword.setVisibility(View.GONE);
        }
    }


    private void attemptLogin() {

        String username = etUsername.getText().toString();
        String password = etPassword.getText().toString();
        String code = etCode.getText().toString();
        boolean cancel = false;
        View focusView = null;

        //检测用户名是否为空
        if (TextUtils.isEmpty(username)) {
            AppContext.showToast(R.string.error_please_input_username);
            return;
        }
        String regex = "^((13[0-9])|(15[0-9])|(18[0-9])|(17[0-9])|(14[0-9]))\\d{8}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(username);
        if (!matcher.matches()) {
            AppContext.showToast(R.string.error_please_input_rightusername);
            return;
        }

        if (TextUtils.isEmpty(password)) {
            AppContext.showToast(R.string.error_please_input_password);
            return;
        }
        if (password.length() < 6 || password.length() > 16) {
            AppContext.showToast(R.string.error_please_input_rightpassword);
            return;
        }
        if (StringUtils.isEmpty(code)) {
            AppContext.showToast(R.string.please_input_code);
            return;
        }

        MD5 md5 = new MD5();
        String passmd5 = md5.getMD5(password);
        Log.i("yezheny", "MD5是：" + passmd5);
        String pwEncrypt = Base64Util.getBase64(passmd5);
        Log.i("yezheny", "最终密码是：" + pwEncrypt);

        initLoading("");
        /*JSONObject obj = new JSONObject();
        HttpEntity entity = null;
        try {
            JSONObject obj0 = new JSONObject();
            obj.put("password", pwEncrypt);
            obj.put("phone", username*//*"18820276678"*//*);
            obj.put("tenantId", code);
            obj0.put("params", obj);
            entity = new StringEntity(obj0.toString(), "UTF-8");
            login(entity);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return;
        }*/

        //retrofit实现
        JSONObject obj = new JSONObject();
        try {
            JSONObject obj0 = new JSONObject();
            obj.put("password", pwEncrypt);
            obj.put("phone", username/*"18820276678"*/);
            obj.put("tenantId", code);
            obj0.put("params", obj);
            RequestBody body = RequestBody.create(MediaType.parse("application/json"), obj0.toString());
            AppContext.getInstance().initRetrofit();
            Call<ResponseBody> data = AppContext.getInstance().getApiService().getMessage(body);
            data.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        String httpResult = response.body().string();
                        TLog.log("getStyleList : " + httpResult);
                        BaseData<LoginInfo> basedata = new Gson().fromJson(httpResult,
                                new TypeToken<BaseData<LoginInfo>>() {
                                }.getType());
                        if (StringUtils.equals(basedata.getCode(), ResultStatus.SUCCESS)) {
                            if (basedata.getData() != null) {
                                if (!StringUtils.isEmpty(basedata.getData().getId())) {
                                    SharedPreferencesManager.putString(ResultStatus.USER_ID, basedata.getData().getId());
                                }
                                if (!StringUtils.isEmpty(basedata.getData().getToken())) {
                                    SharedPreferencesManager.putString(ResultStatus.TOKEN, basedata.getData().getToken());
                                }
                                if (!StringUtils.isEmpty(basedata.getData().getPhone())) {
                                    SharedPreferencesManager.putString(ResultStatus.PHONE, basedata.getData().getPhone());
                                }
                                if (!StringUtils.isEmpty(basedata.getData().getRealName())) {
                                    SharedPreferencesManager.putString(ResultStatus.REALNAME, basedata.getData().getRealName());
                                }
                                if (!StringUtils.isEmpty(basedata.getData().getPostName())) {
                                    SharedPreferencesManager.putString(ResultStatus.POSTNAME, basedata.getData().getPostName());
                                }
                                if (!StringUtils.isEmpty(basedata.getData().getPostId())) {
                                    SharedPreferencesManager.putString(ResultStatus.POSTID, basedata.getData().getPostId());
                                }
                                if (!StringUtils.isEmpty(basedata.getData().getImg())) {
                                    SharedPreferencesManager.putString(ResultStatus.PORTRAIT_IMG, basedata.getData().getImg());
                                }
                                if (!StringUtils.isEmpty(basedata.getData().getCompanyName())) {
                                    SharedPreferencesManager.putString(ResultStatus.COMPANY, basedata.getData().getCompanyName());
                                }
                                if (!StringUtils.isEmpty(basedata.getData().getTenantId())) {
                                    //保存集团编号
                                    SharedPreferencesManager.putString(ResultStatus.COMPANY_CODE, basedata.getData().getTenantId());
                                }
                                SharedPreferencesManager.putString(ResultStatus.LOGIN_STATUS, ResultStatus.LOGINED);
                                SharedPreferencesManager.putString(ResultStatus.LOGIN_USERNAME, etUsername.getText().toString());
                                AppContext.getInstance().initRetrofit();
                                AppContext.getInstance().reinitWebApi();

                                //登录之后设置推送别名为当前用户id
                                String alias = basedata.getData().getId();
                                setAlias(alias);
                                MobclickAgent.onProfileSignIn(alias);

                                //登录成功后获取用户的操作权限
                                getUserOperatPrivileges();
                                return;
                            }
                        } else if (StringUtils.equals(basedata.getCode(), ResultStatus.NO_REGIST)) {
                            AppContext.showToast(R.string.login_no_regist);
                            hideLoading();
                            return;
                        } else if (StringUtils.equals(basedata.getCode(), ResultStatus.PW_ERROR)) {
                            //AppContext.showToast(R.string.login_pw_error);
                            llErrorNotification.setVisibility(View.VISIBLE);
                            hideLoading();
                            return;
                        } else {
                            AppContext.showToast(basedata.getMsg());
                            //by zhengyuye
                            hideLoading();
                            return;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    AppContext.showToast(R.string.login_failed);
                    hideLoading();
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    AppContext.showToast(R.string.login_failed);
                    hideLoading();
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
    }

    private void login(HttpEntity entity) {
        ApiHttpClient.post(LoginActivity.this, AppConfig.LOGIN_URL, entity, AppConfig.CONTENT_TYPE, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                try {
                    String httpResult = new String(bytes);
                    TLog.log("getStyleList : " + httpResult);
                    BaseData<LoginInfo> basedata = new Gson().fromJson(httpResult,
                            new TypeToken<BaseData<LoginInfo>>() {
                            }.getType());
                    if (StringUtils.equals(basedata.getCode(), ResultStatus.SUCCESS)) {
                        if (basedata.getData() != null) {
                            if (!StringUtils.isEmpty(basedata.getData().getId())) {
                                SharedPreferencesManager.putString(ResultStatus.USER_ID, basedata.getData().getId());
                            }
                            if (!StringUtils.isEmpty(basedata.getData().getToken())) {
                                SharedPreferencesManager.putString(ResultStatus.TOKEN, basedata.getData().getToken());
                            }
                            if (!StringUtils.isEmpty(basedata.getData().getPhone())) {
                                SharedPreferencesManager.putString(ResultStatus.PHONE, basedata.getData().getPhone());
                            }
                            if (!StringUtils.isEmpty(basedata.getData().getRealName())) {
                                SharedPreferencesManager.putString(ResultStatus.REALNAME, basedata.getData().getRealName());
                            }
                            if (!StringUtils.isEmpty(basedata.getData().getPostName())) {
                                SharedPreferencesManager.putString(ResultStatus.POSTNAME, basedata.getData().getPostName());
                            }
                            if (!StringUtils.isEmpty(basedata.getData().getPostId())) {
                                SharedPreferencesManager.putString(ResultStatus.POSTID, basedata.getData().getPostId());
                            }
                            if (!StringUtils.isEmpty(basedata.getData().getImg())) {
                                SharedPreferencesManager.putString(ResultStatus.PORTRAIT_IMG, basedata.getData().getImg());
                            }
                            if (!StringUtils.isEmpty(basedata.getData().getCompanyName())) {
                                SharedPreferencesManager.putString(ResultStatus.COMPANY, basedata.getData().getCompanyName());
                            }
                            if (!StringUtils.isEmpty(basedata.getData().getTenantId())) {
                                //保存集团编号
                                SharedPreferencesManager.putString(ResultStatus.COMPANY_CODE, basedata.getData().getTenantId());
                            }
                            SharedPreferencesManager.putString(ResultStatus.LOGIN_STATUS, ResultStatus.LOGINED);
                            SharedPreferencesManager.putString(ResultStatus.LOGIN_USERNAME, etUsername.getText().toString());
                            AppContext.getInstance().reinitWebApi();

                            //登录之后设置推送别名为当前用户id
                            String alias = basedata.getData().getId();
                            setAlias(alias);
                            MobclickAgent.onProfileSignIn(alias);

                            //登录成功后获取用户的操作权限
                            getUserOperatPrivileges();
                            return;
                        }
                    } else if (StringUtils.equals(basedata.getCode(), ResultStatus.NO_REGIST)) {
                        AppContext.showToast(R.string.login_no_regist);
                        hideLoading();
                        return;
                    } else if (StringUtils.equals(basedata.getCode(), ResultStatus.PW_ERROR)) {
                        //AppContext.showToast(R.string.login_pw_error);
                        llErrorNotification.setVisibility(View.VISIBLE);
                        hideLoading();
                        return;
                    } else {
                        AppContext.showToast(basedata.getMsg());
                        //by zhengyuye
                        hideLoading();
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                AppContext.showToast(R.string.login_failed);
                hideLoading();
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                AppContext.showToast(R.string.login_failed);
                hideLoading();
            }
        });
    }

    // 这是来自 JPush Example 的设置别名的 Activity 里的代码。一般 App 的设置的调用入口，在任何方便的地方调用都可以。
    private void setAlias(String alias) {
        if (TextUtils.isEmpty(alias)) {
            AppContext.showToast(R.string.get_alias_empty);
            return;
        }
        // 调用 Handler 来异步设置别名
        mHandler.sendMessage(mHandler.obtainMessage(MSG_SET_ALIAS, alias));
    }

    private final TagAliasCallback mAliasCallback = new TagAliasCallback() {
        @Override
        public void gotResult(int code, String alias, Set<String> tags) {
            String logs;
            switch (code) {
                case 0:
                    logs = "Set tag and alias success";
                    Log.i(TAG, logs);
                    // 建议这里往 SharePreference 里写一个成功设置的状态。成功设置一次后，以后不必再次设置了。
                    break;
                case 6002:
                    logs = "Failed to set alias and tags due to timeout. Try again after 60s.";
                    Log.i(TAG, logs);
                    // 延迟 60 秒来调用 Handler 设置别名
                    mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_SET_ALIAS, alias), 1000 * 60);
                    break;
                default:
                    logs = "Failed with errorCode = " + code;
                    Log.e(TAG, logs);
            }
            //AppContext.showToast(logs);
        }
    };
    private static final int MSG_SET_ALIAS = 1001;
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_SET_ALIAS:
                    Log.d(TAG, "Set alias in handler.");
                    // 调用 JPush 接口来设置别名。
                    JPushInterface.setAliasAndTags(getApplicationContext(),
                            (String) msg.obj,
                            null,
                            mAliasCallback);
                    break;
                default:
                    Log.i(TAG, "Unhandled msg - " + msg.what);
            }
        }
    };

    private void getUserOperatPrivileges() {

        Call<BaseListData<OperatePrivilegesInfo>> userOperatPrivileges = AppContext.getInstance().getApiService().getUserOperatPrivileges();
        userOperatPrivileges.enqueue(new Callback<BaseListData<OperatePrivilegesInfo>>() {
            @Override
            public void onResponse(Call<BaseListData<OperatePrivilegesInfo>> call, Response<BaseListData<OperatePrivilegesInfo>> response) {
                try {
                    hideLoading();
                    /*String httpResult = new String(bytes);
                    TLog.log("getUserOperatPrivileges : " + httpResult);
                    Type type = new TypeToken<BaseListData<OperatePrivilegesInfo>>() {
                    }.getType();
                    BaseListData<OperatePrivilegesInfo> datas = new Gson().fromJson(httpResult, type);*/
                    BaseListData<OperatePrivilegesInfo> datas = response.body();
                    if (StringUtils.equals(datas.getCode(), ResultStatus.SUCCESS)) {
                        ArrayList<OperatePrivilegesInfo> mDatas = datas.getData();
                        /*if (mDatas != null) {
                            AppContext.getACache().put("operatPrivileges", httpResult);
                        }*/
                        closeKeyboard();
                        if (GuideActivity.guideActivity != null && !GuideActivity.guideActivity.isFinishing()) {
                            GuideActivity.guideActivity.finish();
                        }
                        finish();
                        Intent intent = new Intent(LoginActivity.this, HomePageActivity.class);
                        startActivity(intent);
                        return;
                    }
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                }
                AppContext.showToast(R.string.get_user_operat_privileges_failed);
            }

            @Override
            public void onFailure(Call<BaseListData<OperatePrivilegesInfo>> call, Throwable t) {
                hideLoading();
                AppContext.showToast(R.string.get_user_operat_privileges_failed);
            }
        });

        /*try {
            mStringEntity = new StringEntity("{}", "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        MainApi.requestCommon(this, AppConfig.USER_OPERATPRIVILEGES_URL, mStringEntity, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                try {
                    hideLoading();
                    String httpResult = new String(bytes);
                    TLog.log("getUserOperatPrivileges : " + httpResult);
                    Type type = new TypeToken<BaseListData<OperatePrivilegesInfo>>() {
                    }.getType();
                    BaseListData<OperatePrivilegesInfo> datas = new Gson().fromJson(httpResult, type);
                    if (StringUtils.equals(datas.getCode(), ResultStatus.SUCCESS)) {
                        ArrayList<OperatePrivilegesInfo> mDatas = datas.getData();
                        if (mDatas != null) {
                            AppContext.getACache().put("operatPrivileges", httpResult);
                        }
                        closeKeyboard();
                        if (GuideActivity.guideActivity != null && !GuideActivity.guideActivity.isFinishing()) {
                            GuideActivity.guideActivity.finish();
                        }
                        finish();
                        Intent intent = new Intent(LoginActivity.this, HomePageActivity.class);
                        startActivity(intent);
                        return;
                    }
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                }
                AppContext.showToast(R.string.get_user_operat_privileges_failed);
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                hideLoading();
                AppContext.showToast(R.string.get_user_operat_privileges_failed);
            }
        });*/
    }

    @Override
    protected boolean hasLoading() {
        return true;
    }

    @Override
    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
        //现在认为只要控件将Activity向上推的高度超过了1/3屏幕高，就认为软键盘弹起
        if (oldBottom != 0 && bottom != 0 && (oldBottom - bottom > keyBordHeight)) {
            llTop.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    240
            ));
            llTop.setGravity(Gravity.CENTER);

        } else if (oldBottom != 0 && bottom != 0 && (bottom - oldBottom > keyBordHeight)) {
            llTop.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    500
            ));
            llTop.setGravity(Gravity.CENTER);
        }
    }

    /**
     * @param root         最外层布局，需要调整的布局
     * @param scrollToView 被键盘遮挡的scrollToView，滚动root,使scrollToView在root可视区域的底部
     */
    private void controlKeyboardLayout(final View root, final View scrollToView) {
        root.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver
                .OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                TLog.log("BEGIN");
                Rect rect = new Rect();
                //获取root在窗体的可视区域
                root.getWindowVisibleDisplayFrame(rect);
                //获取root在窗体的不可视区域高度(被其他View遮挡的区域高度)
                int rootInvisibleHeight = root.getRootView().getHeight() - rect.bottom;
                //若不可视区域高度大于100，则键盘显示
                if (rootInvisibleHeight > 100) {
                    TLog.log("SHENGD");
                    int[] location = new int[2];
                    //获取scrollToView在窗体的坐标
                    scrollToView.getLocationInWindow(location);
                    //计算root滚动高度，使scrollToView在可见区域
                    TLog.log(location[1] + "");
                    int srollHeight = (location[1] + scrollToView.getHeight() + (int) (10 * TDevice.getDensity(LoginActivity.this))) - rect.bottom;
                    if (srollHeight != 0) {
                        root.scrollTo(0, srollHeight);
                    }

                } else {
                    //键盘隐藏
                    TLog.log("THRESS");
                    root.scrollTo(0, 0);
                }
            }
        });
    }

    View.OnKeyListener onKey = new View.OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_NAVIGATE_NEXT) {
                InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm.isActive()) {
                    attemptLogin();
                }
                return true;
            }
            return false;
        }

    };

    private void closeKeyboard() {
        View view = getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //添加layout大小发生改变监听器
        activityRootView.addOnLayoutChangeListener(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            finish();
            if (GuideActivity.guideActivity != null && GuideActivity.guideActivity.isFinishing()) {
                Intent intent = new Intent(LoginActivity.this, GuideActivity.class);
                startActivity(intent);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}

