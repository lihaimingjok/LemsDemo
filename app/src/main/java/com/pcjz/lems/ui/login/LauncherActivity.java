package com.pcjz.lems.ui.login;

import android.content.Intent;
import android.os.SystemClock;
import android.support.v4.content.ContextCompat;

import com.pcjz.lems.R;
import com.pcjz.lems.business.common.utils.StatusBarUtil;
import com.pcjz.lems.business.context.AppContext;
import com.pcjz.lems.business.base.BaseActivity;
import com.pcjz.lems.business.common.utils.PrefUtils;
import com.pcjz.lems.business.common.utils.StringUtils;
import com.pcjz.lems.business.constant.ResultStatus;
import com.pcjz.lems.business.storage.SharedPreferencesManager;
import com.pcjz.lems.ui.homepage.HomePageActivity;

/**
 * Created by ${YGP} on 2017/4/19.
 */

public class LauncherActivity extends BaseActivity {

    @Override
    public void setView() {
        setContentView(R.layout.activity_launcher);
    }

    @Override
    protected void setStatusBar() {
        StatusBarUtil.setColor(this, ContextCompat.getColor(getApplicationContext(), R.color.white));
    }

    @Override
    public void setListener() {
    }

    @Override
    protected void initView() {
        super.initView();
        //FileUtils.copyFile(ConfigPath.downLoadPath+"安托山花园二期",ConfigPath.downLoadPath+"安托山花园二期（1）");

        new Thread(new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(1000);
                /*// 判断之前有没有展示过功能引导页
                boolean guideShowed = PrefUtils.getBoolean(LauncherActivity.this,
                        PrefUtils.GUIDE_SHOWED, false);

                String loginStatus = SharedPreferencesManager.getString(ResultStatus.LOGIN_STATUS);
                // 如果没有展示过功能引导页
                if (!guideShowed) {
                    if (loginStatus != null && StringUtils.equals(loginStatus,ResultStatus.LOGINED)) {
                        AppContext.getInstance().reinitWebApi();
                        startActivity(new Intent(LauncherActivity.this, HomePageActivity.class));
                        finish();
                        return;
                    } else {
                        // 跳转到功能引导页
                        startActivity(new Intent(LauncherActivity.this, GuideActivity.class));
                        finish();
                    }
                } else {
                    if (loginStatus != null && StringUtils.equals(loginStatus, ResultStatus.LOGINED)) {
                        AppContext.getInstance().reinitWebApi();
                        startActivity(new Intent(LauncherActivity.this, HomePageActivity.class));
                        finish();
                        return;
                    } else {
                        startActivity(new Intent(LauncherActivity.this, LoginActivity.class));
                        finish();
                    }
                }*/

                //去掉浏览页之后
                String loginStatus = SharedPreferencesManager.getString(ResultStatus.LOGIN_STATUS);
                if (loginStatus != null && StringUtils.equals(loginStatus, ResultStatus.LOGINED)) {
                    AppContext.getInstance().reinitWebApi();
                    startActivity(new Intent(LauncherActivity.this, HomePageActivity.class));
                    finish();
                } else {
                    startActivity(new Intent(LauncherActivity.this, LoginActivity.class));
                    finish();
                }
            }
        }).start();
    }
}
