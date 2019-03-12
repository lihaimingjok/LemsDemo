package com.pcjz.lems.ui.application;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pcjz.lems.R;
import com.pcjz.lems.business.base.BaseFragment;
import com.pcjz.lems.business.common.downloadapp.bean.AppInfoBean;
import com.pcjz.lems.business.common.utils.CommUtil;
import com.pcjz.lems.business.constant.SysCode;
import com.pcjz.lems.business.context.AppContext;
import com.pcjz.lems.business.storage.downloaddb.AppInfoDaoImpl;
import com.pcjz.lems.ui.application.adapter.MyApplicationOpenAdapter;
import com.pcjz.lems.ui.application.bean.MoreApplication;

import java.util.ArrayList;
import java.util.List;

/**
 * created by yezhengyu on 2017/5/23 20:35
 */
public class MyApplicationsFragment extends BaseFragment {

    private FragmentActivity activity;
    private ArrayList<AppInfoBean> mApplications = new ArrayList<>();
    private MyApplicationOpenAdapter mAdapter;
    private ListView mLvList;
    private RelativeLayout rlNoData;
    private AppInfoDaoImpl mDao;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        mDao = new AppInfoDaoImpl(activity);
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_my_applications;
    }

    @Override
    protected void initView(View view) {
        mLvList = (ListView) view.findViewById(R.id.lv_my_applications);
        rlNoData = (RelativeLayout) view.findViewById(R.id.rl_no_data);
        TextView tvNoData = (TextView) view.findViewById(R.id.tv_no_data);
        tvNoData.setText(AppContext.mResource.getString(R.string.no_install_applications));
        ImageView ivNoData = (ImageView) view.findViewById(R.id.iv_no_data);
        ivNoData.setImageResource(R.drawable.pop_window_choose_no_content);
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
                            int progress = (int) (appInfoBeans.get(j).getFinished() * 1.0f / appInfoBeans.get(j).getLength() * 100);
                            if (progress == 100) {
                                String versionCode = allAppList.get(i).getVersionCode();
                                //过滤筛选去掉更新和未安装的应用
                                if (versionCode.equals(appInfoBeans.get(j).getVersionCode())) {
                                    boolean installed = CommUtil.getInstance().isInstalled(activity, appInfoBeans.get(j).getAppPackageName());
                                    if (installed) {
                                        mApplications.add(appInfoBeans.get(j));
                                    }
                                }
                            }
                        }
                    }
                }
                if (mApplications.size() != 0) {
                    mAdapter = new MyApplicationOpenAdapter(activity);
                    mAdapter.setData(mApplications);
                    mLvList.setAdapter(mAdapter);
                } else {
                    mLvList.setVisibility(View.GONE);
                    rlNoData.setVisibility(View.VISIBLE);
                }
            } else {
                mLvList.setVisibility(View.GONE);
                rlNoData.setVisibility(View.VISIBLE);
            }
        }
    }
}
