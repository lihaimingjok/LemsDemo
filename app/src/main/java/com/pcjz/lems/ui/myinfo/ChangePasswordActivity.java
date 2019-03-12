package com.pcjz.lems.ui.myinfo;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.pcjz.lems.R;
import com.pcjz.lems.business.context.AppContext;
import com.pcjz.lems.business.base.BaseActivity;
import com.pcjz.lems.business.base.basebean.Base;
import com.pcjz.lems.business.common.utils.AliasUtil;
import com.pcjz.lems.business.common.utils.Base64Util;
import com.pcjz.lems.business.common.utils.MD5;
import com.pcjz.lems.business.common.utils.StringUtils;
import com.pcjz.lems.business.common.utils.TLog;
import com.pcjz.lems.business.config.AppConfig;
import com.pcjz.lems.business.constant.ResultStatus;
import com.pcjz.lems.business.constant.SysCode;
import com.pcjz.lems.business.context.AppManager;
import com.pcjz.lems.business.storage.SharedPreferencesManager;
import com.pcjz.lems.business.webapi.MainApi;
import com.pcjz.lems.ui.login.LoginActivity;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

/**
 * created by yezhengyu on 2017/4/12.
 */

public class ChangePasswordActivity extends BaseActivity {

    private EditText mOldPw;
    private EditText mNewPw;
    private EditText mConfirmPw;
    private Button mBtnCommit;
    private String oldPswStr;
    private String newPswStr;
    private String confirmPswStr;

    @Override
    public void setView() {
        setContentView(R.layout.activity_change_password);
    }

    @Override
    protected void initView() {
        super.initView();
        setTitle(AppContext.mResource.getString(R.string.changepassword));
        mOldPw = (EditText) findViewById(R.id.et_old_pw);
        mNewPw = (EditText) findViewById(R.id.et_new_pw);
        mConfirmPw = (EditText) findViewById(R.id.et_confirm_pw);
        mBtnCommit = (Button) findViewById(R.id.btn_commit);
    }

    @Override
    public void setListener() {
        mBtnCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verifyInfo()) {
                    submitToWebPsw();
                }
            }
        });
    }

    private void submitToWebPsw() {
        JSONObject obj = new JSONObject();
        MD5 md5 = new MD5();
        String oldPswmd5 = md5.getMD5(oldPswStr);
        String oldPswBase64 = Base64Util.getBase64(oldPswmd5);
        String newPswmd5 = md5.getMD5(newPswStr);
        String newPswBase64 = Base64Util.getBase64(newPswmd5);
        HttpEntity entity = null;
        try {
            JSONObject obj0 = new JSONObject();
            obj.put("password", oldPswBase64);
            obj.put("npassword", newPswBase64);
            obj0.put("params", obj);
            entity = new StringEntity(obj0.toString(), "UTF-8");
            requestChangPassword(entity);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return;
        }
    }

    private void requestChangPassword(HttpEntity entity) {
        MainApi.requestCommon(ChangePasswordActivity.this, AppConfig.USER_MOD_URL, entity, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                try {
                    String httpResult = new String(bytes);
                    TLog.log("getChangPassword : " + httpResult);
                    Base base = new Gson().fromJson(httpResult, Base.class);
                    if (StringUtils.equals(base.getCode(), SysCode.SUCCESS)) {
                        AppContext.showToast(R.string.pw_correct_success);
                        AliasUtil.getInstance().setAlias("", ChangePasswordActivity.this);
                        SharedPreferencesManager.putString(ResultStatus.LOGIN_STATUS, ResultStatus.UNLOGIN);
                        AppManager.getAppManager().finishAllActivity();
                        Intent intent = new Intent(ChangePasswordActivity.this, LoginActivity.class);
                        startActivity(intent);
                        return;
                    } else {
                        AppContext.showToast(base.getMsg());
                        return;
                    }
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                }
                AppContext.showToast(R.string.pw_corret_failed);
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                AppContext.showToast(R.string.pw_corret_failed);
            }
        });
    }

    private boolean verifyInfo() {
        oldPswStr = mOldPw.getText().toString();
        newPswStr = mNewPw.getText().toString();
        confirmPswStr = mConfirmPw.getText().toString();
        //判断密码是否为空
        if (StringUtils.isEmpty(oldPswStr)) {
            AppContext.showToast(R.string.please_enter_old_pw);
            return false;
        }
        if (oldPswStr.length() < 6) {
            AppContext.showToast(R.string.please_enter_old_pw_limit);
            return false;
        }
        if (StringUtils.isEmpty(newPswStr)) {
            AppContext.showToast(R.string.please_enter_new_pw);
            return false;
        }
        if (newPswStr.length() < 6) {
            AppContext.showToast(R.string.please_enter_new_pw_limit);
            return false;
        }
        if (StringUtils.isEmpty(confirmPswStr)) {
            AppContext.showToast(R.string.please_enter_again_new_pw);
            return false;
        }
        if (!TextUtils.equals(newPswStr, confirmPswStr)) {
            AppContext.showToast(R.string.two_times_different);
            return false;
        }
        return true;
    }
}
