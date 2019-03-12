package com.pcjz.lems.ui.application.adapter;

import android.content.Context;
import android.content.Intent;

import com.pcjz.lems.R;
import com.pcjz.lems.business.common.downloadapp.bean.AppInfoBean;
import com.pcjz.lems.business.common.downloadapp.services.DownloadService;
import com.pcjz.lems.business.common.utils.NetStateUtil;
import com.pcjz.lems.business.common.utils.StringUtils;
import com.pcjz.lems.business.config.AppConfig;
import com.pcjz.lems.business.constant.ResultStatus;
import com.pcjz.lems.business.constant.SysCode;
import com.pcjz.lems.business.context.AppContext;
import com.pcjz.lems.business.storage.SharedPreferencesManager;

import java.util.Calendar;

/**
 * created by yezhengyu on 2017/7/17 16:27
 * 抽取下载方法
 */

public class DownloadCommon {
    private static DownloadCommon instance = null;

    public synchronized static DownloadCommon getInstance() {
        if (instance == null) {
            instance = new DownloadCommon();
        }
        return instance;
    }

    public static final int MIN_CLICK_DELAY_TIME = 1500;
    private long lastClickTime = 0;

    public void downloadClick(Context context, AppInfoBean appBean, int position) {
        long currentTime = Calendar.getInstance().getTimeInMillis();
        if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
            lastClickTime = currentTime;
            if (judgeInternet(context)) return;
            appBean.setPosition(position);
            Intent startIntent = new Intent(context, DownloadService.class);
            startIntent.setAction(DownloadService.ACTION_START);
            startIntent.putExtra("appBean", appBean);
            context.startService(startIntent);
        }
    }


    public void pauseClick(Context context, AppInfoBean appBean) {
        if (judgeInternet(context)) return;
        Intent pauseIntent = new Intent(context, DownloadService.class);
        pauseIntent.setAction(DownloadService.ACTION_PAUSE);
        pauseIntent.putExtra("appBean", appBean);
        context.startService(pauseIntent);
    }

    private boolean judgeInternet(Context context) {
        if (!NetStateUtil.isNetworkConnected(context)) {
            AppContext.showToast(AppConfig.STRING.NET_NOT_CONNECT);
            return true;
        }
        //网络状态
        String netWorkState = SharedPreferencesManager.getString(ResultStatus.NETWORKSTATE2);
        if (StringUtils.equals(netWorkState, SysCode.NETWORKSTATE_OFF)) {
            AppContext.showToast(R.string.current_be_in_offline_state);
            return true;
        }
        return false;
    }


}
