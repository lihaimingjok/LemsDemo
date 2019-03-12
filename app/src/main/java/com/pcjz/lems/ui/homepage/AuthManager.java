package com.pcjz.lems.ui.homepage;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.pcjz.lems.R;
import com.pcjz.lems.business.common.utils.StringUtils;
import com.pcjz.lems.business.constant.ResultStatus;
import com.pcjz.lems.business.constant.SysCode;
import com.pcjz.lems.business.storage.SharedPreferencesManager;
import com.pcjz.lems.ui.application.ApplicationFragment;
import com.pcjz.lems.ui.homepage.fragment.WorkSpaceFragment;
import com.pcjz.lems.ui.reportforms.ReportFormsFragment;
import com.pcjz.lems.ui.workrealname.WorkRealNameFragment;

import static android.R.attr.fragment;

/**
 * Created by ${YGP} on 2017/5/11.
 */

public class AuthManager {
    /*private static CheckerFragment checkerFragment;
    private static WorkRealNameFragment workbenchFragment;
    private static AcceptancePageFragment acceptancePageFragment;
    private static ProvinStatFormsFragment provinStatFormsFragment;
    private static WorkbenchChechProcessFragment workbenchChechProcessFragment;*/
    private static ReportFormsFragment reportFormsFragment;
    private static ApplicationFragment applicationFragment;


    public static final int[] TAB_TITLES = new int[]{R.string.tab_realname_work, R.string.tab_equipment_protect, R.string.tab_provincestatisticsforms, R.string.tab_reportforms, R.string.tab_tools};
    private static WorkRealNameFragment workRealNameFragment;

    public static Fragment getFragment(int tabPosition) {
        String postId = SharedPreferencesManager.getString(ResultStatus.POSTID);
        Bundle bundle;
        switch (tabPosition) {
            case 0:
                /*if (StringUtils.equals(postId, SysCode.POSTID_CONSTRUCTION_TEAM)*//*postName.contains(ResultStatus.POST_CONSTRUCTION_TEAM)*//*) {
                    if (checkerFragment == null) {
                        checkerFragment = new CheckerFragment();
                        Bundle bundle = new Bundle();
                        bundle.putInt("title", TAB_TITLES[0]);
                        checkerFragment.setArguments(bundle);
                    }
                    return checkerFragment;
                }
                if (StringUtils.equals(postId, SysCode.POSTID_QUALIFIER) || StringUtils.equals(postId, SysCode.POSTID_SUPERVISOR)) {
                    if (workbenchFragment == null) {
                        workbenchFragment = new WorkRealNameFragment();
                        Bundle bundle = new Bundle();
                        bundle.putInt("title", TAB_TITLES[0]);
                        workbenchFragment.setArguments(bundle);
                    }
                    return workbenchFragment;
                }
                if (!StringUtils.equals(postId, SysCode.POSTID_CONSTRUCTION_TEAM) && !StringUtils.equals(postId, SysCode.POSTID_QUALIFIER) && !StringUtils.equals(postId, SysCode.POSTID_SUPERVISOR)) {
                    return getFragment("progress");
                } else {
                    if (workbenchFragment == null) {
                        workbenchFragment = new WorkRealNameFragment();
                        Bundle bundle = new Bundle();
                        bundle.putInt("title", TAB_TITLES[0]);
                        workbenchFragment.setArguments(bundle);
                    }
                    return workbenchFragment;
                }*/
                if (workRealNameFragment == null) {
                    workRealNameFragment = new WorkRealNameFragment();
                    bundle = new Bundle();
                    bundle.putInt("title", TAB_TITLES[0]);
                    workRealNameFragment.setArguments(bundle);
                }
                return workRealNameFragment;
            /*case 1:
                *//*if (acceptancePageFragment == null) {
                    acceptancePageFragment = new AcceptancePageFragment();
                    Bundle bundle = new Bundle();
                    bundle.putInt("title", TAB_TITLES[1]);
                    acceptancePageFragment.setArguments(bundle);
                }
                return acceptancePageFragment;*//*
                WorkSpaceFragment workSpaceFragment2 = new WorkSpaceFragment();
                bundle = new Bundle();
                bundle.putInt("title", TAB_TITLES[1]);
                workSpaceFragment2.setArguments(bundle);
                return workSpaceFragment2;
            case 2:
                *//*if (provinStatFormsFragment == null) {
                    provinStatFormsFragment = new ProvinStatFormsFragment();
                    Bundle bundle = new Bundle();
                    bundle.putInt("title", TAB_TITLES[2]);
                    provinStatFormsFragment.setArguments(bundle);
                }
                return provinStatFormsFragment;*//*
                WorkSpaceFragment workSpaceFragment3 = new WorkSpaceFragment();
                bundle = new Bundle();
                bundle.putInt("title", TAB_TITLES[2]);
                workSpaceFragment3.setArguments(bundle);
                return workSpaceFragment3;*/
            case 1:
                if (reportFormsFragment == null) {
                    reportFormsFragment = new ReportFormsFragment();
                    Bundle bundle3 = new Bundle();
                    bundle3.putInt("title", TAB_TITLES[4]);
                    reportFormsFragment.setArguments(bundle3);
                }
                return reportFormsFragment;
            case 2:
                if (applicationFragment == null) {
                    applicationFragment = new ApplicationFragment();
                    Bundle bundle4 = new Bundle();
                    bundle4.putInt("title", R.string.tab_tools);
                    applicationFragment.setArguments(bundle4);
                }
                return applicationFragment;
            default:
               /* Bundle bundle = new Bundle();
                bundle.putInt("title", TAB_TITLES[position]);
                fragment.setArguments(bundle);*/
                return new WorkSpaceFragment();
        }

    }

    /*public static Fragment getFragment(String type) {
        Fragment fragment = new Fragment();
        if (StringUtils.equals(type, "progress")) {
            if (workbenchChechProcessFragment == null) {
                workbenchChechProcessFragment = new WorkbenchChechProcessFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("title", TAB_TITLES[0]);
                workbenchChechProcessFragment.setArguments(bundle);
            }
            fragment = workbenchChechProcessFragment;
        }
        return fragment;
    }*/
}
