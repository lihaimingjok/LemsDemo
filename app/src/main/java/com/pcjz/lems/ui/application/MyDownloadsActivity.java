package com.pcjz.lems.ui.application;

import android.app.Dialog;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pcjz.lems.R;
import com.pcjz.lems.business.base.BaseActivity;
import com.pcjz.lems.business.common.downloadapp.ConfigAppPath;
import com.pcjz.lems.business.common.downloadapp.bean.AppInfoBean;
import com.pcjz.lems.business.common.downloadapp.event.EventMessage;
import com.pcjz.lems.business.common.utils.CommUtil;
import com.pcjz.lems.business.common.utils.TLog;
import com.pcjz.lems.business.constant.SysCode;
import com.pcjz.lems.business.context.AppContext;
import com.pcjz.lems.business.storage.downloaddb.AppInfoDaoImpl;
import com.pcjz.lems.business.widget.swipMenuListView.SwipeMenu;
import com.pcjz.lems.business.widget.swipMenuListView.SwipeMenuCreator;
import com.pcjz.lems.business.widget.swipMenuListView.SwipeMenuItem;
import com.pcjz.lems.business.widget.swipMenuListView.SwipeMenuListView;
import com.pcjz.lems.ui.application.adapter.MyApplicationsAdapter;
import com.pcjz.lems.ui.application.bean.MoreApplication;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * created by yezhengyu on 2017/5/22 20:16
 */
public class MyDownloadsActivity extends BaseActivity {

    private SwipeMenuListView lvContent;
    private RelativeLayout rlNoData;
    private RelativeLayout mRlBack;
    private Dialog mDialog;
    private AppInfoDaoImpl mDao;

    private ArrayList<AppInfoBean> mApplications = new ArrayList<>();
    private MyApplicationsAdapter mAdapter;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void setView() {
        setContentView(R.layout.activity_my_downloads);
        setTitle(AppContext.mResource.getString(R.string.my_downloads));
        EventBus.getDefault().register(this);
        mDao = new AppInfoDaoImpl(this);
    }

    @Override
    protected void initView() {
        lvContent = (SwipeMenuListView) findViewById(R.id.lv_content);
        rlNoData = (RelativeLayout) findViewById(R.id.rl_no_data);
        TextView tvNoData = (TextView) findViewById(R.id.tv_no_data);
        ImageView ivNoData = (ImageView) findViewById(R.id.iv_no_data);
        ivNoData.setImageResource(R.drawable.pop_window_choose_no_content);
        mRlBack = (RelativeLayout) findViewById(R.id.rl_back);
        tvNoData.setText(AppContext.mResource.getString(R.string.no_download_applications));
        //初始化SwipeMenuListView
        SwipeMenuCreator creater = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                // create置顶item
                SwipeMenuItem item = new SwipeMenuItem(MyDownloadsActivity.this);
                // set item width
                item.setWidth(dp2px(75));
                // set item title
                //TODO
                 item.setTitleBackground(AppContext.mResource.getString(R.string.download_delete));
                // set item title fontsize
                item.setTitleSize(dp2px(7));
                // set item title font color
                item.setTitleColor(AppContext.mResource.getColor(R.color.tab_info_point));
                // add to menu
                menu.addMenuItem(item);
            }
        };
        // set creator
        lvContent.setMenuCreator(creater);
        //菜单栏点击事件
        lvContent.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        showDeleteDialog(position);
                        break;
                }
                return false;
            }
        });
        //过滤信息
        initDatas();
    }

    private void initDatas() {
        ArrayList<AppInfoBean> applications = new ArrayList<>();
        List<AppInfoBean> appInfoBeans = mDao.getApps(SysCode.APP_DOWNLOAD_URL);
        ArrayList<MoreApplication> allAppList = (ArrayList<MoreApplication>) AppContext.getACache().getAsObject("all_appinfo_list");
        if (allAppList != null && allAppList.size() != 0) {
            if (appInfoBeans != null && appInfoBeans.size() != 0) {
                for (int i = 0; i < allAppList.size(); i++) {
                    for (int j = 0; j < appInfoBeans.size(); j++) {
                        if (allAppList.get(i).getId().equals(appInfoBeans.get(j).getId())) {
                            int progress = (int) (appInfoBeans.get(j).getFinished() * 1.0f / appInfoBeans.get(j).getLength() * 100);
                            if (progress == 100) {
                                String versionCode = allAppList.get(i).getVersionCode();
                                //过滤筛选去掉更新和已安装的应用
                                if (versionCode.equals(appInfoBeans.get(j).getVersionCode())) {
                                    boolean installed = CommUtil.getInstance().isInstalled(this, appInfoBeans.get(j).getAppPackageName());
                                    if (!installed) {
                                        appInfoBeans.get(j).setLabel(SysCode.APPINFO_MYDOWNLOADS);
                                        applications.add(appInfoBeans.get(j));
                                    }
                                }
                            } else {
                                appInfoBeans.get(j).setLabel(SysCode.APPINFO_MYDOWNLOADS);
                                applications.add(appInfoBeans.get(j));
                            }
                        }
                    }
                }
                if (applications.size() != 0) {
                    //去重操作
                    for (int i = 0; i < applications.size(); i++) {
                        if (!mApplications.contains(applications.get(i))) {
                            mApplications.add(applications.get(i));
                        }
                    }
                    mAdapter = new MyApplicationsAdapter(this);
                    mAdapter.setData(mApplications);
                    lvContent.setAdapter(mAdapter);
                } else {
                    lvContent.setVisibility(View.GONE);
                    rlNoData.setVisibility(View.VISIBLE);
                }
            } else {
                lvContent.setVisibility(View.GONE);
                rlNoData.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void setListener() {
        mRlBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void showDeleteDialog(final int position) {
        mDialog = new Dialog(this, R.style.select_dialog);
        mDialog.setContentView(R.layout.delete_alert_dialog);
        mDialog.findViewById(R.id.confirm_inspect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppInfoBean appBean = mApplications.get(position);
                String appPath = ConfigAppPath.downLoadPath + appBean.getAppName();
                //删除数据库该条数据
                mDao.deleteApp(appBean.getId());
                mApplications.remove(position);
                mAdapter.notifyDataSetChanged();
                //删除文件夹中当前应用信息
                File file = new File(appPath);
                if (file.isFile() && file.exists()) {
                    file.delete();
                }
                EventMessage eventMessage1 = new EventMessage(5, "myDownloadNo");
                EventBus.getDefault().post(eventMessage1);
                mDialog.dismiss();
            }
        });
        mDialog.findViewById(R.id.cancel_inspect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventMessage(EventMessage eventMessage) {
        switch (eventMessage.getType()) {
            case 2://下载完成
                AppInfoBean appBean1 = (AppInfoBean) eventMessage.getObject();
                if (SysCode.APPINFO_MYDOWNLOADS.equals(appBean1.getLabel())) {
                    Toast.makeText(this, appBean1.getAppName() + "已下载完成", Toast.LENGTH_SHORT).show();
                    updateFinished(appBean1);
                }
                break;
            case 3://下载进度刷新
                AppInfoBean appBean2 = (AppInfoBean) eventMessage.getObject();
                if (SysCode.APPINFO_MYDOWNLOADS.equals(appBean2.getLabel())) {
                    updateProgress(appBean2);
                }
                break;
            case 4://点击暂停
                AppInfoBean appBean3 = (AppInfoBean) eventMessage.getObject();
                if (SysCode.APPINFO_MYDOWNLOADS.equals(appBean3.getLabel())) {
                    updatePause(appBean3);
                }
                break;
        }
    }

    private void updateFinished(final AppInfoBean appBean1) {
        int position = appBean1.getPosition();
        View item = lvContent.getChildAt(position);
        TextView tvClick = (TextView) item.findViewById(R.id.tv_download_click);
        TextView tvPause = (TextView) item.findViewById(R.id.tv_pause_click);
        ProgressBar progressBarDownload = (ProgressBar) item.findViewById(R.id.progressBar);
        //安装
        TextView tvInstallApk = (TextView) item.findViewById(R.id.tv_install_apk);
        for (AppInfoBean data : mApplications) {
            if (data.getId().equals(appBean1.getId())) {
                tvClick.setVisibility(View.GONE);
                tvPause.setVisibility(View.GONE);
                progressBarDownload.setProgress(0);
                tvInstallApk.setVisibility(View.VISIBLE);
                tvInstallApk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //安装apk
                        CommUtil.getInstance().installApk(MyDownloadsActivity.this, appBean1.getAppName());
                    }
                });
                break;
            }
        }
    }

    private void updatePause(AppInfoBean appBean3) {
        int position = appBean3.getPosition();
        View item = lvContent.getChildAt(position);
        TextView tvClick = (TextView) item.findViewById(R.id.tv_download_click);
        TextView tvPause = (TextView) item.findViewById(R.id.tv_pause_click);
        TextView tvInstallApk = (TextView) item.findViewById(R.id.tv_install_apk);
        ProgressBar progressBarDownload = (ProgressBar) item.findViewById(R.id.progressBar);
        for (AppInfoBean data : mApplications) {
            if (data.getId().equals(appBean3.getId())) {
                int progress = (int) (appBean3.getFinished() * 1.0f / appBean3.getLength() * 100);
                tvPause.setVisibility(View.GONE);
                tvInstallApk.setVisibility(View.GONE);
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
        View item = lvContent.getChildAt(position);
        TextView tvClick = (TextView) item.findViewById(R.id.tv_download_click);
        TextView tvPause = (TextView) item.findViewById(R.id.tv_pause_click);
        TextView tvInstallApk = (TextView) item.findViewById(R.id.tv_install_apk);
        ProgressBar progressBarDownload = (ProgressBar) item.findViewById(R.id.progressBar);
        for (AppInfoBean data : mApplications) {
            if (data.getId().equals(appBean2.getId())) {
                int progress = (int) (appBean2.getFinished() * 1.0f / appBean2.getLength() * 100);
                if (progress > 0 && progress < 100) {
                    tvInstallApk.setVisibility(View.GONE);
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
