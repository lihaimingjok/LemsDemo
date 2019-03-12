package com.pcjz.lems.ui.application;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.pcjz.lems.R;
import com.pcjz.lems.business.base.BaseFragment;
import com.pcjz.lems.business.base.basebean.BaseData;
import com.pcjz.lems.business.base.basebean.BaseListData;
import com.pcjz.lems.business.common.downloadapp.bean.AppInfoBean;
import com.pcjz.lems.business.common.downloadapp.event.EventMessage;
import com.pcjz.lems.business.common.utils.CommUtil;
import com.pcjz.lems.business.common.utils.StringUtils;
import com.pcjz.lems.business.common.utils.TLog;
import com.pcjz.lems.business.common.view.MyGridView;
import com.pcjz.lems.business.config.AppConfig;
import com.pcjz.lems.business.constant.ResultStatus;
import com.pcjz.lems.business.constant.SysCode;
import com.pcjz.lems.business.context.AppContext;
import com.pcjz.lems.business.storage.downloaddb.AppInfoDaoImpl;
import com.pcjz.lems.business.webapi.MainApi;
import com.pcjz.lems.ui.application.adapter.ApplicationAdapter;
import com.pcjz.lems.ui.application.bean.Application;
import com.pcjz.lems.ui.application.bean.ApplicationInfo;
import com.pcjz.lems.ui.application.bean.MoreApplication;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * created by yezhengyu on 2017/5/20 10:26
 */
public class ApplicationFragment extends BaseFragment implements View.OnClickListener {

    private FragmentActivity activity;
    private RelativeLayout mRlNoData;
    private ArrayList<AppInfoBean> mApplications = new ArrayList<>();
    private ApplicationAdapter mAdapter;
    private int currentPage = 1;
    private int pageSize = 9;
    private LinearLayout mPageList;
    private MyGridView mGridView;
    private int totalRecord;
    private AppInfoDaoImpl mDao;
    private List<AppInfoBean> mAppInfoBeans;
    private TextView mTvPage;
    private ArrayList<MoreApplication> mResults;
    private LinearLayout llMyDownload;
    private View view;
    //存放更新后没有安装的App
    private List<AppInfoBean> updatedInstalls = new ArrayList<>();
    private TextView tvNoData;
    private ImageView ivNoData;
    private TextView tvLoad;
    private String mType;

    @Override
    protected void initView(View view) {
        try {
            activity = getActivity();
            EventBus.getDefault().register(this);
            mDao = new AppInfoDaoImpl(activity);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.view = view;
        mGridView = (MyGridView) view.findViewById(R.id.gv_applications);
        mPageList = (LinearLayout) view.findViewById(R.id.ll_page_list);
        //无数据时显示
        mRlNoData = (RelativeLayout) view.findViewById(R.id.rl_no_data);
        tvNoData = (TextView) view.findViewById(R.id.tv_no_data);
        ivNoData = (ImageView) view.findViewById(R.id.iv_no_data);
        tvLoad = (TextView) view.findViewById(R.id.tv_again_loading);
        tvLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRlNoData.setVisibility(View.GONE);
                mPageList.setVisibility(View.VISIBLE);
                mType = "click";
                getAppInfoList(currentPage);
            }
        });
        initNoDataView();
        //换一批
        TextView tvChangeBatch = (TextView) view.findViewById(R.id.tv_change_batch);
        //更多
        TextView tvMore = (TextView) view.findViewById(R.id.tv_more_applications);
        RelativeLayout rlMyApp = (RelativeLayout) view.findViewById(R.id.rl_my_appinfo);
        RelativeLayout rlMyDownload = (RelativeLayout) view.findViewById(R.id.rl_my_download);
        llMyDownload = (LinearLayout) view.findViewById(R.id.ll_my_download);
        mTvPage = (TextView) view.findViewById(R.id.tv_page);
        tvChangeBatch.setOnClickListener(this);
        tvMore.setOnClickListener(this);
        rlMyApp.setOnClickListener(this);
        rlMyDownload.setOnClickListener(this);
        mAdapter = new ApplicationAdapter(activity);
        mAdapter.setData(mApplications);
        mGridView.setAdapter(mAdapter);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String appId = mApplications.get(position).getId();
                if (appId != null) {
                    Intent intent = new Intent(activity, ApplicationDeatilActivity.class);
                    intent.putExtra("id", appId);
                    startActivity(intent);
                }
            }
        });

        //getAppInfoList(currentPage);
        //获取所有应用信息
        getAllAppList();
    }

    private void initNoDataView() {
        tvNoData.setText(R.string.no_applications_data);
        ivNoData.setImageResource(R.drawable.pop_window_choose_no_content);
    }

    private void initMyDownloads(ArrayList<MoreApplication> allAppList) {
        mAppInfoBeans = mDao.getApps(SysCode.APP_DOWNLOAD_URL);
        ArrayList<AppInfoBean> myDownloadNo = new ArrayList<>();
        if (mAppInfoBeans != null && mAppInfoBeans.size() != 0) {
            for (int i = 0; i < allAppList.size(); i++) {
                for (int j = 0; j < mAppInfoBeans.size(); j++) {
                    if (allAppList.get(i).getId().equals(mAppInfoBeans.get(j).getId())) {
                        int progress = (int) (mAppInfoBeans.get(j).getFinished() * 1.0f / mAppInfoBeans.get(j).getLength() * 100);
                        if (progress == 100) {
                            String versionCode = allAppList.get(i).getVersionCode();
                            //过滤筛选去掉更新和已安装的应用
                            if (versionCode.equals(mAppInfoBeans.get(j).getVersionCode())) {
                                boolean installed = CommUtil.getInstance().isInstalled(activity, mAppInfoBeans.get(j).getAppPackageName());
                                if (!installed) {
                                    myDownloadNo.add(mAppInfoBeans.get(j));
                                }
                            }
                        } else {
                            myDownloadNo.add(mAppInfoBeans.get(j));
                        }
                    }
                }
            }
        }
        if (myDownloadNo.size() == 0) {
            llMyDownload.setVisibility(View.GONE);
            return;
        }
        llMyDownload.setVisibility(View.VISIBLE);
        mTvPage.setText(myDownloadNo.size() + "");
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_application;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mType = "resume";
        getAppInfoList(currentPage);
    }

    @Override
    protected boolean hasLoading() {
        return true;
    }

    private void getAllAppList() {
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
        MainApi.requestCommon(activity, AppConfig.GET_APPLICATIONS_URL, entity, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                try {
                    String httpResult = new String(bytes);
                    TLog.log("getMoreApplications : " + httpResult);
                    Type type = new TypeToken<BaseListData<MoreApplication>>() {
                    }.getType();
                    BaseListData<MoreApplication> datas = new Gson().fromJson(httpResult, type);
                    if (StringUtils.equals(datas.getCode(), ResultStatus.SUCCESS)) {
                        mResults = datas.getData();
                        if (mResults != null && mResults.size() != 0) {
                            initMyDownloads(mResults);
                            AppContext.getACache().put("all_appinfo_list", mResults);
                        }
                        return;
                    } else {
                       // AppContext.showToast(datas.getMsg());
                        return;
                    }
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
            }
        });
    }

    public void getAppInfoList(final int currentPage) {
        if (currentPage == 1) {
            initNoDataView();
        }
        if (StringUtils.equals(mType, "click")) {
            initLoading("加载中...", view);
        }
        HttpEntity entity = null;
        try {
            JSONObject obj = new JSONObject();
            JSONObject obj2 = new JSONObject();
            obj.put("pageNo", currentPage);
            obj.put("pageSize", pageSize);
            obj2.put("appPackageName", "com.pcjz.lems");
            obj.put("params", obj2);
            entity = new StringEntity(obj.toString(), "UTF-8");
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return;
        }
        MainApi.requestCommon(activity, AppConfig.GET_APPINFOLIST_URL, entity, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                try {
                    hideLoading();
                    String httpResult = new String(bytes);
                    TLog.log("getApplications : " + httpResult);
                    Type type = new TypeToken<BaseData<ApplicationInfo>>() {
                    }.getType();
                    BaseData<ApplicationInfo> datas = new Gson().fromJson(httpResult, type);
                    if (StringUtils.equals(datas.getCode(), ResultStatus.SUCCESS)) {
                        totalRecord = datas.getData().totalRecord;
                        ArrayList<Application> results = datas.getData().getResults();
                        if (results != null && results.size() != 0) {
                            //格式化数据源
                            refreshDatas(results);
                        } else {
                            mPageList.setVisibility(View.GONE);
                            mRlNoData.setVisibility(View.VISIBLE);
                        }
                        return;
                    } else {
                        hideLoading();
                        return;
                    }
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                }
                mPageList.setVisibility(View.GONE);
                mRlNoData.setVisibility(View.VISIBLE);
                hideLoading();
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                ivNoData.setImageResource(R.drawable.no_internet_icon);
                tvNoData.setText(AppContext.mResource.getString(R.string.please_check_network));
                mPageList.setVisibility(View.GONE);
                mRlNoData.setVisibility(View.VISIBLE);
                tvLoad.setVisibility(View.VISIBLE);
                hideLoading();
                if (throwable == null) {
                    return;
                }
                //AppContext.showToast(R.string.get_application_list_failed);
            }
        });
    }

    private void refreshDatas(ArrayList<Application> results) {
        mApplications.clear();
        AppInfoBean bean = null;
        //过滤信息给AppInfoBean
        for (int j = 0; j < results.size(); j++) {
            bean = new AppInfoBean();
            if (!StringUtils.isEmpty(results.get(j).getId())) {
                bean.setId(results.get(j).getId());
            }
            bean.setUrl(SysCode.APP_DOWNLOAD_URL + results.get(j).getAppAndroidPath());
            bean.setAppName(results.get(j).getName());
            bean.setAppIcon(AppConfig.IMAGE_FONT_URL + results.get(j).getAppIcon());
            bean.setFileSize(results.get(j).getFileSize());
            bean.setVersionCode(results.get(j).getVersionCode());
            bean.setAppPackageName(results.get(j).getAppPackageName());
            //用于获取所有数据库中的appBean
            bean.setBasicUrl(SysCode.APP_DOWNLOAD_URL);
            //区分重复通知
            bean.setLabel(SysCode.APPINFO_APPLICATION);
            mApplications.add(bean);
        }
        mAppInfoBeans = mDao.getApps(SysCode.APP_DOWNLOAD_URL);
        if (mAppInfoBeans != null && mAppInfoBeans.size() != 0) {
            for (int j = 0; j < mApplications.size(); j++) {
                for (int k = 0; k < mAppInfoBeans.size(); k++) {
                    if (mApplications.get(j).getId().equals(mAppInfoBeans.get(k).getId())) {
                        mApplications.get(j).setFinished(mAppInfoBeans.get(k).getFinished());
                        mApplications.get(j).setLength(mAppInfoBeans.get(k).getLength());
                        String versionCode = mApplications.get(j).getVersionCode();
                        if (versionCode != null) {
                            if (!versionCode.equals(mAppInfoBeans.get(k).getVersionCode())) {
                                mApplications.get(j).setIsUpdate(true);
                            }
                        }
                        if (mAppInfoBeans.get(k).getAppPackageName() != null) {
                            boolean isInstall = CommUtil.getInstance().isInstalled(activity, mAppInfoBeans.get(k).getAppPackageName());
                            mApplications.get(j).setIsInstall(isInstall);
                        }
                    }
                }
            }
        } else {
            //如果用户清理的缓存或卸载了App，还是要判断应用是否安装
            for (int i = 0; i < mApplications.size(); i++) {
                if (mApplications.get(i).getAppPackageName() != null) {
                    boolean isInstall = CommUtil.getInstance().isInstalled(activity, mApplications.get(i).getAppPackageName());
                    mApplications.get(i).setIsInstall(isInstall);
                }
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        Intent intent;
        switch (id) {
            case R.id.tv_change_batch:
                mType = "click";
                if (mApplications.size() > 0 && mApplications.size() < pageSize) {
                    if (currentPage == 1) {
                        return;
                    }
                    currentPage = 0;
                }
                if (currentPage * pageSize == totalRecord) {
                    currentPage = 0;
                }
                currentPage++;
                getAppInfoList(currentPage);
                break;
            case R.id.tv_more_applications:
                intent = new Intent(activity, MoreAppliactionsActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_my_appinfo:
                intent = new Intent(activity, MyApplicationsActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_my_download:
                intent = new Intent(activity, MyDownloadsActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventMessage(EventMessage eventMessage) {
        switch (eventMessage.getType()) {
            case 2://下载完成
                AppInfoBean appBean1 = (AppInfoBean) eventMessage.getObject();
                if (SysCode.APPINFO_APPLICATION.equals(appBean1.getLabel())) {
                    Toast.makeText(activity, appBean1.getAppName() + "已下载完成", Toast.LENGTH_SHORT).show();
                    updateFinished(appBean1);
                }
                break;
            case 3://下载进度刷新
                AppInfoBean appBean2 = (AppInfoBean) eventMessage.getObject();
                if (SysCode.APPINFO_APPLICATION.equals(appBean2.getLabel())) {
                    updateProgress(appBean2);
                }
                break;
            case 4://点击暂停
                AppInfoBean appBean3 = (AppInfoBean) eventMessage.getObject();
                if (SysCode.APPINFO_APPLICATION.equals(appBean3.getLabel())) {
                    updatePause(appBean3);
                }
                break;
            case 5:
                initNumber();
                break;
        }
    }

    private void initNumber() {
        if (mResults != null && mResults.size() != 0) {
            initMyDownloads(mResults);
        }
    }

    private void updateFinished(final AppInfoBean appBean1) {
        int position = appBean1.getPosition();
        View item = mGridView.getChildAt(position);
        TextView tvClick = (TextView) item.findViewById(R.id.tv_download_click);
        TextView tvPause = (TextView) item.findViewById(R.id.tv_pause_click);
        ProgressBar progressBarDownload = (ProgressBar) item.findViewById(R.id.progressBar);
        //安装
        TextView tvInstallApk = (TextView) item.findViewById(R.id.tv_install_apk);
        //打开
        TextView tvOpenApp = (TextView) item.findViewById(R.id.tv_open_app);
        for (AppInfoBean data : mApplications) {
            if (data.getId().equals(appBean1.getId())) {
                /*if (appBean1.isUpdate()) {
                    updatedInstalls.add(appBean1);
                    AppContext.getACache().put("updated_install", (Serializable)updatedInstalls);
                }*/
                //下载或更新完了，就安装
                tvClick.setVisibility(View.GONE);
                tvPause.setVisibility(View.GONE);
                tvOpenApp.setVisibility(View.GONE);
                progressBarDownload.setProgress(0);
                tvInstallApk.setVisibility(View.VISIBLE);
                tvInstallApk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //安装apk
                        CommUtil.getInstance().installApk(activity, appBean1.getAppName());
                    }
                });
                break;
            }
        }
    }

    private void updatePause(AppInfoBean appBean3) {
        int position = appBean3.getPosition();
        View item = mGridView.getChildAt(position);
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
        View item = mGridView.getChildAt(position);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    protected String getProjNameKey() {
        return SysCode.PROJECTPERIODID;
    }

    protected String getProjIdKey() {
        return SysCode.PROJECTPERIODID;
    }

    protected String getNetworkStateKey() {
        return ResultStatus.NETWORKSTATE5;
    }

    class getBroadcast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            if(Intent.ACTION_PACKAGE_ADDED.equals(intent.getAction())){
                initNumber();
                //Toast.makeText(context, "有应用被添加", Toast.LENGTH_LONG).show();
            }
            else  if(Intent.ACTION_PACKAGE_REMOVED.equals(intent.getAction())){
                //Toast.makeText(context, "有应用被删除", Toast.LENGTH_LONG).show();
            }
            else  if(Intent.ACTION_PACKAGE_REPLACED.equals(intent.getAction())){
                //Toast.makeText(context, "有应用被替换", Toast.LENGTH_LONG).show();
            }

        }

    }
}
