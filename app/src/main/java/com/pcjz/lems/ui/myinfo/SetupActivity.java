package com.pcjz.lems.ui.myinfo;

import android.app.Dialog;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.pcjz.lems.R;
import com.pcjz.lems.business.context.AppContext;
import com.pcjz.lems.business.base.BaseActivity;
import com.pcjz.lems.business.base.basebean.Base;
import com.pcjz.lems.business.common.utils.DataCleanManager2;
import com.pcjz.lems.business.common.utils.FileUtils;
import com.pcjz.lems.business.common.utils.StringUtils;
import com.pcjz.lems.business.common.utils.TLog;
import com.pcjz.lems.business.common.view.dialog.MyDialog;
import com.pcjz.lems.business.common.view.dialog.NoMsgDialog;
import com.pcjz.lems.business.common.view.dialog.OnMyNegativeListener;
import com.pcjz.lems.business.common.view.dialog.OnMyPositiveListener;
import com.pcjz.lems.business.config.AppConfig;
import com.pcjz.lems.business.config.ConfigAppPath;
import com.pcjz.lems.business.config.ConfigPath;
import com.pcjz.lems.business.constant.ResultStatus;
import com.pcjz.lems.business.constant.SysCode;
import com.pcjz.lems.business.context.AppManager;
import com.pcjz.lems.business.storage.ACache;
import com.pcjz.lems.business.storage.SharedPreferencesManager;
import com.pcjz.lems.business.webapi.MainApi;
import com.pcjz.lems.ui.login.LoginActivity;
import com.umeng.analytics.MobclickAgent;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;

import java.io.UnsupportedEncodingException;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

/**
 * created by yezhengyu on 2017/4/11.
 */

public class SetupActivity extends BaseActivity {

    private static final String TAG = "tag-----alias";
    private String OPEN = "open";
    private String CLOSE = "close";
    private Button btnExit;
    private LinearLayout llClearAche;
    private LinearLayout llChangePassword;
    private ImageView ivIsPull;
    private String mUserId;
    private TextView tvCacheSize;

    @Override
    public void setView() {
        setContentView(R.layout.activity_setup);
    }

    @Override
    protected void initView() {
        setTitle(AppContext.mResource.getString(R.string.setup));
        //缓存的大小
        tvCacheSize = (TextView) findViewById(R.id.tv_cache_size);
        initCacheSize();
        //退出登录
        btnExit = (Button)findViewById(R.id.btn_exit);
        //清楚缓存
        llClearAche = (LinearLayout)findViewById(R.id.ll_clear_ache);
        //更改密码
        llChangePassword = (LinearLayout)findViewById(R.id.ll_changepassword);
        //是否推送
        ivIsPull = (ImageView) findViewById(R.id.iv_is_pull_message);
        mUserId = SharedPreferencesManager.getString(ResultStatus.USER_ID);
        String status = SharedPreferencesManager.getString(SysCode.MESSAGE_STATUS);
        if (status == null) {
            ivIsPull.setTag(OPEN);
            ivIsPull.setImageResource(R.drawable.my_open_btn);
        } else if ("1".equals(status)) {
            ivIsPull.setTag(OPEN);
            ivIsPull.setImageResource(R.drawable.my_open_btn);
        } else {
            ivIsPull.setTag(CLOSE);
            ivIsPull.setImageResource(R.drawable.my_close_btn);
        }
    }

    private void initCacheSize() {
        try {
            String totalCacheSize = DataCleanManager2.getTotalCacheSize(this);
            tvCacheSize.setText(totalCacheSize);
        } catch (Exception e) {
            e.printStackTrace();
            tvCacheSize.setText("");
        }
    }

    @Override
    public void setListener() {
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showExitDialog();
            }
        });
        llClearAche.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showClearAcheDialog();
            }
        });
        llChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SetupActivity.this, ChangePasswordActivity.class));
            }
        });

        ivIsPull.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ivIsPull.getTag().equals(OPEN)) {
                    showPushDialog();
                } else {
                    //打开推送，设置别名为当前用户id
                    setAlias(mUserId);
                    SharedPreferencesManager.putString(SysCode.MESSAGE_STATUS, "1");
                    ivIsPull.setImageResource(R.drawable.my_open_btn);
                    ivIsPull.setTag(OPEN);
                }
            }
        });
    }

    private void showPushDialog() {
        final Dialog mDialog = new Dialog(this, R.style.select_dialog);
        mDialog.setContentView(R.layout.alert_dialog_delete_photo);
        TextView tvInfo = (TextView) mDialog.findViewById(R.id.tv_info);
        tvInfo.setText(R.string.close_push_cannot_receive_message);
        TextView tvConfirm = (TextView) mDialog.findViewById(R.id.confirm_inspect);
        tvConfirm.setText(R.string.close_push_confirm_close);
        TextView tvConcel = (TextView) mDialog.findViewById(R.id.cancel_inspect);
        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //关闭推送，设置别名为空
                setAlias("");
                SharedPreferencesManager.putString(SysCode.MESSAGE_STATUS, "0");
                ivIsPull.setImageResource(R.drawable.my_close_btn);
                ivIsPull.setTag(CLOSE);
                mDialog.dismiss();
            }
        });
        tvConcel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }

    private void showExitDialog() {
        NoMsgDialog noMsgDialog = new NoMsgDialog(SetupActivity.this,"确定退出？","","退出","取消",
                new OnMyPositiveListener(){
                    @Override
                    public void onClick(){
                        String networkstate = SharedPreferencesManager.getString(ResultStatus.NETWORKSTATE1);
                        if (networkstate != null && StringUtils.equals(networkstate, SysCode.NETWORKSTATE_OFF)) {
                            //先将WiFi置于在线状态
                            SharedPreferencesManager.putString(ResultStatus.NETWORKSTATE1, SysCode.NETWORKSTATE_ON);
                        }
                        loginout();
                    }
                },new OnMyNegativeListener(){
            @Override
            public void onClick() {
                super.onClick();
            }
        });
        noMsgDialog.show();
    }

    private void showClearAcheDialog() {
        final MyDialog clearAcheDialog = new MyDialog(SetupActivity.this,"提示","即将清理所有数据， 确认是否清理？","确定","取消",
                new OnMyPositiveListener(){
                    @Override
                    public void onClick() {
                        //String data = DataCleanManager2.cleanCache(SetupActivity.this);
                        //AppContext.getACache().clear();
                        DataCleanManager2.clearAllCache(SetupActivity.this);
                        //重置Acache
                        ACache.get(SetupActivity.this);
                        //new ThreadDaoImpl(SetupActivity.this).clearTable("period_info");
                        FileUtils.DeleteFolder(ConfigPath.downLoadPath);
                        FileUtils.DeleteFolder(ConfigAppPath.downLoadPath);
                        FileUtils.DeleteFolder(ConfigPath.downloadImagePath);
                        FileUtils.DeleteFolder(AppConfig.PICTURE_PATH);
                        initCacheSize();
                        AppContext.showToast("已清除缓存");
                        //updateDialog.dismiss();

                    }
                },new OnMyNegativeListener(){
            @Override
            public void onClick() {
                super.onClick();
            }
        });
        clearAcheDialog.setTitleColor(R.color.dialog_msg_title_color);
        clearAcheDialog.show();
    }

    private void loginout() {
        HttpEntity entity = null;
        try {
            entity = new StringEntity("{}", "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        MainApi.requestCommon(SetupActivity.this, AppConfig.LOGIN_OUT_URL, entity, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                try {
                    String httpResult = new String(bytes);
                    TLog.log("loginout : " + httpResult);
                    Base base = new Gson().fromJson(httpResult, Base.class);
                    if (StringUtils.equals(base.getCode(), ResultStatus.SUCCESS)) {
                        //清除别名为空
                        setAlias("");
                        MobclickAgent.onProfileSignOff();
                        SharedPreferencesManager.putString(ResultStatus.LOGIN_STATUS, ResultStatus.UNLOGIN);
                        AppManager.getAppManager().finishAllActivity();
                        startActivity(new Intent(SetupActivity.this, LoginActivity.class));
                        return;
                    } else {
                        AppManager.getAppManager().finishAllActivity();
                        startActivity(new Intent(SetupActivity.this, LoginActivity.class));
                        AppContext.showToast(base.getMsg());
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                AppManager.getAppManager().finishAllActivity();
                startActivity(new Intent(SetupActivity.this, LoginActivity.class));
                AppContext.showToast(R.string.unlogin_failed);
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                AppManager.getAppManager().finishAllActivity();
                startActivity(new Intent(SetupActivity.this, LoginActivity.class));
                AppContext.showToast(R.string.unlogin_failed);
            }
        });
    }

    //打开和关闭推送消息
    // 这是来自 JPush Example 的设置别名的 Activity 里的代码。一般 App 的设置的调用入口，在任何方便的地方调用都可以。
    private void setAlias(String alias) {
        /*if (TextUtils.isEmpty(alias)) {
            AppContext.showToast(R.string.get_alias_empty);
            return;
        }*/
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
}
