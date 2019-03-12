package com.pcjz.lems.ui.reportforms;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.TextView;

import com.pcjz.lems.R;
import com.pcjz.lems.business.base.BaseFragment;


/**
 * created by yezhengyu on 2017/7/1 14:51
 */

public class ReportFormsFragment extends BaseFragment {


    private FragmentActivity activity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_reportforms;
    }

    @Override
    protected void initView(View view) {

    }

}
