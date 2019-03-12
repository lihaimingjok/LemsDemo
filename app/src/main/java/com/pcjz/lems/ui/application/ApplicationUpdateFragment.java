package com.pcjz.lems.ui.application;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pcjz.lems.R;
import com.pcjz.lems.business.base.BaseFragment;
import com.pcjz.lems.business.common.downloadapp.bean.AppInfoBean;
import com.pcjz.lems.business.common.downloadapp.event.EventMessage;
import com.pcjz.lems.business.common.utils.CommUtil;
import com.pcjz.lems.business.common.utils.TLog;
import com.pcjz.lems.business.constant.SysCode;
import com.pcjz.lems.business.context.AppContext;
import com.pcjz.lems.business.storage.downloaddb.AppInfoDaoImpl;
import com.pcjz.lems.ui.application.adapter.MoreApplicationAdapter;
import com.pcjz.lems.ui.application.bean.MoreApplication;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * created by yezhengyu on 2017/5/23 20:36
 */
public class ApplicationUpdateFragment extends BaseFragment {

    private FragmentActivity activity;
    private ArrayList<AppInfoBean> mApplications = new ArrayList<>();
    private MoreApplicationAdapter mAdapter;
    private ListView mLvList;
    private RelativeLayout mRlUpdate;
    private RelativeLayout rlNoData;
    private ProgressBar mProgressBar;
    private TextView mTvNumberSize;
    private AppInfoDaoImpl mDao;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        EventBus.getDefault().register(this);
        mDao = new AppInfoDaoImpl(activity);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_application_update;
    }

    @Override
    protected void initView(View view) {
        //应用布局
        mRlUpdate = (RelativeLayout) view.findViewById(R.id.rl_update_applications);
        mLvList = (ListView) view.findViewById(R.id.lv_update_application);
        rlNoData = (RelativeLayout) view.findViewById(R.id.rl_no_data);
        TextView tvNoData = (TextView) view.findViewById(R.id.tv_no_data);
        tvNoData.setText(AppContext.mResource.getString(R.string.no_update_applications));
        ImageView ivNoData = (ImageView) view.findViewById(R.id.iv_no_data);
        ivNoData.setImageResource(R.drawable.pop_window_choose_no_content);
        //全部更新progressBar
        mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        mTvNumberSize = (TextView) view.findViewById(R.id.tv_update_number_size);
        //过滤信息
        initDatas();
    }

    private void initDatas() {
        List<AppInfoBean> appInfoBeans = mDao.getApps(SysCode.APP_DOWNLOAD_URL);
        ArrayList<MoreApplication> allAppList = (ArrayList<MoreApplication>) AppContext.getACache().getAsObject("all_appinfo_list");
        if (allAppList != null && allAppList.size() != 0) {
            if (appInfoBeans != null && appInfoBeans.size() != 0) {
                for (int i = 0; i < allAppList.size(); i++) {
                    for (int j = 0; j < appInfoBeans.size(); j++) {
                        if (allAppList.get(i).getId().equals(appInfoBeans.get(j).getId())) {
                            String versionCode = allAppList.get(i).getVersionCode();
                            //过滤筛选去掉未安装和打开的应用
                            if (!versionCode.equals(appInfoBeans.get(j).getVersionCode())) {
                                if (appInfoBeans.get(j).getAppPackageName() != null) {
                                    boolean isInstall = CommUtil.getInstance().isInstalled(activity, appInfoBeans.get(j).getAppPackageName());
                                    appInfoBeans.get(j).setIsInstall(isInstall);
                                }
                                appInfoBeans.get(j).setIsUpdate(true);
                                mApplications.add(appInfoBeans.get(j));
                            }
                        }
                    }
                }
                if (mApplications.size() != 0) {
                    int finishedLength = 0;
                    int length = 0;
                    float size = 0;
                    for (int i = 0; i < mApplications.size(); i++) {
                        finishedLength = +mApplications.get(i).getFinished();
                        length = +mApplications.get(i).getLength();
                        size = +mApplications.get(i).getFileSize();
                    }
                    int progress = (int) (finishedLength * 1.0f / length * 100);
                    int number = mApplications.size();
                    if (progress == 100) {
                        mProgressBar.setProgress(0);
                    } else {
                        mProgressBar.setProgress(progress);
                    }
                    mTvNumberSize.setText(String.format(getResources().getString(R.string.app_number_size), number, size));

                    mAdapter = new MoreApplicationAdapter(activity, "update");
                    mAdapter.setData(mApplications);
                    mLvList.setAdapter(mAdapter);
                } else {
                    mRlUpdate.setVisibility(View.GONE);
                    rlNoData.setVisibility(View.VISIBLE);
                }
            } else {
                mRlUpdate.setVisibility(View.GONE);
                rlNoData.setVisibility(View.VISIBLE);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventMessage(EventMessage eventMessage) {
        switch (eventMessage.getType()) {
            case 2://下载完成
                AppInfoBean appBean1 = (AppInfoBean) eventMessage.getObject();
                Toast.makeText(activity, appBean1.getAppName() + "已下载完成", Toast.LENGTH_SHORT).show();
                updateFinished(appBean1);
                break;
            case 3://下载进度刷新
                AppInfoBean appBean2 = (AppInfoBean) eventMessage.getObject();
                updateProgress(appBean2);
                break;
            case 4://点击暂停
                AppInfoBean appBean3 = (AppInfoBean) eventMessage.getObject();
                updatePause(appBean3);
                break;
        }
    }

    private void updateFinished(final AppInfoBean appBean1) {
        int position = appBean1.getPosition();
        View item = mLvList.getChildAt(position);
        TextView tvClick = (TextView) item.findViewById(R.id.tv_download_click);
        TextView tvPause = (TextView) item.findViewById(R.id.tv_pause_click);
        ProgressBar progressBarDownload = (ProgressBar) item.findViewById(R.id.progressBar);
        //安装
        TextView tvInstallApk = (TextView) item.findViewById(R.id.tv_install_apk);
        //打开
        TextView tvOpenApp = (TextView) item.findViewById(R.id.tv_open_app);
        for (AppInfoBean data : mApplications) {
            if (data.getId().equals(appBean1.getId())) {
                //如果下载或更新完了就安装，没有打开一说
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
        View item = mLvList.getChildAt(position);
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
        View item = mLvList.getChildAt(position);
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
