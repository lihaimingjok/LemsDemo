package com.pcjz.lems.ui.application;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pcjz.lems.R;
import com.pcjz.lems.business.base.BaseActivity;
import com.pcjz.lems.business.base.BaseFragment;
import com.pcjz.lems.business.common.view.MyViewPager;

/**
 * created by yezhengyu on 2017/5/23 19:21
 */
public class MyApplicationsActivity extends BaseActivity {

    private RelativeLayout mRlBack;
    private TabLayout mTlTitle;

    BaseFragment[] TAB_FRAGMENTS = new BaseFragment[] {new MyApplicationsFragment(), new ApplicationUpdateFragment()};

    private final int[] TAB_TITLES = new int[]{R.string.my_applications, R.string.application_update};

    @Override
    public void setView() {
        setContentView(R.layout.activity_my_applications);
    }

    @Override
    protected void initView() {

        TextView tvTitle = (TextView) findViewById(R.id.tv_title);
        tvTitle.setText(R.string.my_applications);
        //返回
        mRlBack = (RelativeLayout) findViewById(R.id.rl_back);
        //我的下载
        RelativeLayout mRlMyDownload = (RelativeLayout) findViewById(R.id.rl_my_download);
        mRlMyDownload.setVisibility(View.GONE);
        final MyViewPager viewPager = (MyViewPager) findViewById(R.id.viewpager);
        mTlTitle = (TabLayout) findViewById(R.id.tl_title);
        MyPagerAdapter adapter = new MyPagerAdapter(getSupportFragmentManager());
        mTlTitle.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition(), false);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTlTitle));
        viewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        mTlTitle.setTabsFromPagerAdapter(adapter);
        viewPager.setAdapter(adapter);
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

    class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return MyApplicationsActivity.this.getResources().getString(TAB_TITLES[position]);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = TAB_FRAGMENTS[position];
            return fragment;
        }

        @Override
        public int getCount() {
            return TAB_TITLES.length;
        }
    }
}
