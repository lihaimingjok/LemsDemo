package com.pcjz.lems.ui.myinfo;

import android.widget.TextView;

import com.pcjz.lems.R;
import com.pcjz.lems.business.context.AppContext;
import com.pcjz.lems.business.base.BaseActivity;
import com.pcjz.lems.business.common.utils.VersionChk;

/**
 * Created by ${YGP} on 2017/4/12.
 */

public class AboutActivity extends BaseActivity {
    private TextView tvVersionName;

    @Override
    public void setView() {
        setContentView(R.layout.activity_about);
        setTitle(AppContext.mResource.getString(R.string.about));
    }

    @Override
    protected void initView() {
        super.initView();
        tvVersionName = (TextView) findViewById(R.id.tv_version_name);
        tvVersionName.setText("版本V"+new VersionChk(AboutActivity.this).getVerName());
    }

    @Override
    public void setListener() {

    }
}
