package com.pcjz.lems.ui.application;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.pcjz.lems.R;
import com.pcjz.lems.business.base.BaseActivity;
import com.pcjz.lems.business.base.basebean.BaseListData;
import com.pcjz.lems.business.common.downloadapp.bean.AppInfoBean;
import com.pcjz.lems.business.common.downloadapp.event.EventMessage;
import com.pcjz.lems.business.common.utils.CommUtil;
import com.pcjz.lems.business.common.utils.CommonUtil;
import com.pcjz.lems.business.common.utils.StringUtils;
import com.pcjz.lems.business.common.utils.TLog;
import com.pcjz.lems.business.config.AppConfig;
import com.pcjz.lems.business.constant.ResultStatus;
import com.pcjz.lems.business.constant.SysCode;
import com.pcjz.lems.business.context.AppContext;
import com.pcjz.lems.business.storage.downloaddb.AppInfoDaoImpl;
import com.pcjz.lems.business.webapi.MainApi;
import com.pcjz.lems.ui.application.adapter.MoreApplicationAdapter;
import com.pcjz.lems.ui.application.bean.MoreApplication;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * created by yezhengyu on 2017/5/22 16:36
 */
public class MoreAppliactionsActivity extends BaseActivity {

    private RelativeLayout mRlBack;
    private RelativeLayout mRlMyDownload;
    private RelativeLayout mRlNoData;
    private ArrayList<AppInfoBean> mApplications = new ArrayList<>();
    private MoreApplicationAdapter mAdapter;
    private ListView mLvList;
    private AppInfoDaoImpl mDao;

    private ImageView ivNoData;
    private TextView tvNoData;
    private CommonUtil mCommon;

    @Override
    public void setView() {
        setContentView(R.layout.activity_more_applications);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        mDao = new AppInfoDaoImpl(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void initView() {
        //无数据时显示
        mRlNoData = (RelativeLayout) findViewById(R.id.rl_no_data);
        tvNoData = (TextView) findViewById(R.id.tv_no_data);
        ivNoData = (ImageView) findViewById(R.id.iv_no_data);
        ivNoData.setImageResource(R.drawable.pop_window_choose_no_content);
        tvNoData.setText(R.string.no_applications_data);
        TextView tvTitle = (TextView) findViewById(R.id.tv_title);
        tvTitle.setText(R.string.more_applications);
        //返回
        mRlBack = (RelativeLayout) findViewById(R.id.rl_back);
        //我的下载
        mRlMyDownload = (RelativeLayout) findViewById(R.id.rl_my_download);
        TextView tvMyDownload = (TextView) findViewById(R.id.tv_titlebar_right);
        tvMyDownload.setText(R.string.my_downloads);
        mCommon = CommonUtil.getInstance();
        mLvList = (ListView) findViewById(R.id.lv_more_applications);
        mAdapter = new MoreApplicationAdapter(this, "more");
        mAdapter.setData(mApplications);
        mLvList.setAdapter(mAdapter);
        mLvList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String appId = mApplications.get(position).getId();
                if (appId != null) {
                    Intent intent = new Intent(MoreAppliactionsActivity.this, ApplicationDeatilActivity.class);
                    intent.putExtra("id", appId);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public void setListener() {
        mRlBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mRlMyDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MoreAppliactionsActivity.this, MyDownloadsActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void initWebData() {
        JSONObject obj = new JSONObject();
        HttpEntity entity = null;
        try {
            JSONObject obj0 = new JSONObject();
            obj.put("appPackageName", "com.pcjz.lems");
            obj0.put("params", obj);
            String s = obj0.toString();
            entity = new StringEntity(s, "UTF-8");
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return;
        }
        requestApplications(entity);
    }

    private void requestApplications(HttpEntity entity) {
        MainApi.requestCommon(this, AppConfig.GET_APPLICATIONS_URL, entity, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                try {
                    String httpResult = new String(bytes);
                    TLog.log("getMoreApplications : " + httpResult);
                    Type type = new TypeToken<BaseListData<MoreApplication>>() {
                    }.getType();
                    BaseListData<MoreApplication> datas = new Gson().fromJson(httpResult, type);
                    if (StringUtils.equals(datas.getCode(), ResultStatus.SUCCESS)) {
                        ArrayList<MoreApplication> results = datas.getData();
                        if (results != null && results.size() != 0) {
                            //格式化数据源
                            refreshDatas(results);
                        } else {
                            mLvList.setVisibility(View.GONE);
                            mRlNoData.setVisibility(View.VISIBLE);
                        }
                        return;
                    } else {
                        AppContext.showToast(datas.getMsg());
                        return;
                    }
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                }
                AppContext.showToast(R.string.get_application_list_failed);
                mLvList.setVisibility(View.GONE);
                mRlNoData.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                mLvList.setVisibility(View.GONE);
                mRlNoData.setVisibility(View.VISIBLE);
                ivNoData.setImageResource(R.drawable.no_internet_icon);
                tvNoData.setText(AppContext.mResource.getString(R.string.please_check_network));
                if (throwable == null) {
                    return;
                }
                AppContext.showToast(R.string.get_application_list_failed);
            }
        });
    }

    private void refreshDatas(ArrayList<MoreApplication> results) {
        mApplications.clear();
        AppInfoBean bean = null;
        //过滤信息给AppInfoBean
        for (int j = 0; j < results.size(); j++) {
            bean = new AppInfoBean();
            bean.setId(results.get(j).getId());
            bean.setUrl(SysCode.APP_DOWNLOAD_URL + results.get(j).getAppAndroidPath());
            bean.setAppName(results.get(j).getName());
            bean.setAppIcon(AppConfig.IMAGE_FONT_URL + results.get(j).getAppIcon());
            bean.setFileSize(results.get(j).getFileSize());
            bean.setVersionCode(results.get(j).getVersionCode());
            bean.setRemark(results.get(j).getRemark());
            bean.setAppPackageName(results.get(j).getAppPackageName());
            //用于获取所有数据库中的appBean
            bean.setBasicUrl(SysCode.APP_DOWNLOAD_URL);
            bean.setLabel(SysCode.APPINFO_MOREAPPLIACTIONS);
            mApplications.add(bean);
        }
        List<AppInfoBean> appInfoBeans = mDao.getApps(SysCode.APP_DOWNLOAD_URL);
        if (appInfoBeans != null && appInfoBeans.size() != 0) {
            for (int j = 0; j < mApplications.size(); j++) {
                for (int k = 0; k < appInfoBeans.size(); k++) {
                    if (mApplications.get(j).getId().equals(appInfoBeans.get(k).getId())) {
                        mApplications.get(j).setFinished(appInfoBeans.get(k).getFinished());
                        mApplications.get(j).setLength(appInfoBeans.get(k).getLength());
                        String versionCode = mApplications.get(j).getVersionCode();
                        if (versionCode != null) {
                            if (!versionCode.equals(appInfoBeans.get(k).getVersionCode())) {
                                mApplications.get(j).setIsUpdate(true);
                            }
                        }
                        if (appInfoBeans.get(k).getAppPackageName() != null) {
                            boolean isInstall = CommUtil.getInstance().isInstalled(this, appInfoBeans.get(k).getAppPackageName());
                            mApplications.get(j).setIsInstall(isInstall);
                        }
                    }
                }
            }
        } else {
            //如果用户清理的缓存或卸载了App，还是要判断应用是否安装
            for (int i = 0; i < mApplications.size(); i++) {
                if (mApplications.get(i).getAppPackageName() != null) {
                    boolean isInstall = CommUtil.getInstance().isInstalled(this, mApplications.get(i).getAppPackageName());
                    mApplications.get(i).setIsInstall(isInstall);
                }
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventMessage(EventMessage eventMessage) {
        switch (eventMessage.getType()) {
            case 2://下载完成
                AppInfoBean appBean1 = (AppInfoBean) eventMessage.getObject();
                if (SysCode.APPINFO_MOREAPPLIACTIONS.equals(appBean1.getLabel())) {
                    Toast.makeText(this, appBean1.getAppName() + "已下载完成", Toast.LENGTH_SHORT).show();
                    updateFinished(appBean1);
                }
                break;
            case 3://下载进度刷新
                AppInfoBean appBean2 = (AppInfoBean) eventMessage.getObject();
                if (SysCode.APPINFO_MOREAPPLIACTIONS.equals(appBean2.getLabel())) {
                    updateProgress(appBean2);
                }
                break;
            case 4://点击暂停
                AppInfoBean appBean3 = (AppInfoBean) eventMessage.getObject();
                if (SysCode.APPINFO_MOREAPPLIACTIONS.equals(appBean3.getLabel())) {
                    updatePause(appBean3);
                }
                break;
        }
    }

    private void updateFinished(final AppInfoBean appBean1) {
        int position = appBean1.getPosition();
        View item = mCommon.getViewByPosition(position, mLvList);
        TextView tvClick = (TextView) item.findViewById(R.id.tv_download_click);
        TextView tvPause = (TextView) item.findViewById(R.id.tv_pause_click);
        ProgressBar progressBarDownload = (ProgressBar) item.findViewById(R.id.progressBar);
        //安装
        TextView tvInstallApk = (TextView) item.findViewById(R.id.tv_install_apk);
        //打开
        TextView tvOpenApp = (TextView) item.findViewById(R.id.tv_open_app);
        for (AppInfoBean data : mApplications) {
            if (data.getId().equals(appBean1.getId())) {
                //如果下载或更新完了就安装
                tvClick.setVisibility(View.GONE);
                tvPause.setVisibility(View.GONE);
                tvOpenApp.setVisibility(View.GONE);
                progressBarDownload.setProgress(0);
                tvInstallApk.setVisibility(View.VISIBLE);
                tvInstallApk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //安装apk
                        CommUtil.getInstance().installApk(MoreAppliactionsActivity.this, appBean1.getAppName());
                    }
                });
                break;
            }
        }
    }

    private void updatePause(AppInfoBean appBean3) {
        int position = appBean3.getPosition();
        View item = mCommon.getViewByPosition(position, mLvList);
        TextView tvClick = (TextView) item.findViewById(R.id.tv_download_click);
        TextView tvPause = (TextView) item.findViewById(R.id.tv_pause_click);
        ProgressBar progressBarDownload = (ProgressBar) item.findViewById(R.id.progressBar);
        for (AppInfoBean data : mApplications) {
            if (data.getId().equals(appBean3.getId())) {
                int progress = (int) (appBean3.getFinished() * 1.0f / appBean3.getLength() * 100);
                tvPause.setVisibility(View.GONE);
                tvClick.setVisibility(View.VISIBLE);
                tvClick.setText("继续");
                tvClick.setTextColor(getResources().getColor(R.color.off_line_pause));
                progressBarDownload.setProgress(progress);
                break;
            }
        }
    }

    public void updateProgress(AppInfoBean appBean2) {
        int position = appBean2.getPosition();
        View item = mCommon.getViewByPosition(position, mLvList);
        TextView tvClick = (TextView) item.findViewById(R.id.tv_download_click);
        TextView tvPause = (TextView) item.findViewById(R.id.tv_pause_click);
        ProgressBar progressBarDownload = (ProgressBar) item.findViewById(R.id.progressBar);
        for (AppInfoBean data : mApplications) {
            if (data.getId().equals(appBean2.getId())) {
                int progress = (int) (appBean2.getFinished() * 1.0f / appBean2.getLength() * 100);
                if (progress > 0 && progress < 100) {
                    tvClick.setVisibility(View.GONE);
                    tvPause.setVisibility(View.VISIBLE);
                }
                progressBarDownload.setProgress(progress);
                TLog.log("finished::" + appBean2.getFinished());
                TLog.log("progress" + progress);
                return;
            }
        }
    }
}
