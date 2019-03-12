package com.pcjz.lems.ui.workrealname.manageequipment;

import android.animation.ObjectAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pcjz.lems.R;
import com.pcjz.lems.business.base.BaseFragment;
import com.pcjz.lems.business.common.utils.CommUtil;
import com.pcjz.lems.business.common.utils.CommonUtil;
import com.pcjz.lems.business.common.utils.StringUtils;
import com.pcjz.lems.business.common.view.MyViewPager;
import com.pcjz.lems.business.common.view.downpopuwindow.DownBean;
import com.pcjz.lems.business.common.view.downpopuwindow.DownPopupWindow;
import com.pcjz.lems.business.constant.SysCode;
import com.pcjz.lems.business.context.AppContext;
import com.pcjz.lems.ui.workrealname.WorkbenchBaseListFragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * created by yezhengyu on 2017/9/12 16:35
 */

public class ManageEquipmentFragment extends WorkbenchBaseListFragment implements View.OnClickListener {

    private boolean isHided = false;
    private boolean mHasLoadedOnce = false;
    private final int[] TAB_TITLES = new int[]{R.string.work_machine, R.string.large_equipment, R.string.operate_record};
    private final Fragment[] TAB_FRAGMENTS = new Fragment[]{new WorkMechineFragment(), new LargeEquFragment(), new OperateRecordFragment()};
    private TabLayout tlTitle;
    private MyViewPager viewPager;
    private LinearLayout llParent;
    private TextView tvAddOrMore;
    private int position = 0;

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_manage_equipment;
    }

    @Override
    protected void initView(View view) {
        registerBroadcast();
        llParent = (LinearLayout) view.findViewById(R.id.ll_parent);
        tvAddOrMore = (TextView) view.findViewById(R.id.tv_add_or_more);
        tvAddOrMore.setOnClickListener(this);
        viewPager = (MyViewPager) view.findViewById(R.id.viewpager);
        tlTitle = (TabLayout) view.findViewById(R.id.tl_title);
        tlTitle.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                position = tab.getPosition();
                if (position == 0 || position == 1) {
                    tvAddOrMore.setText(R.string.add_workmechine_and_equipment);
                } else {
                    tvAddOrMore.setText(R.string.more_record);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        MyPagerAdapter adapter = new MyPagerAdapter(this.getChildFragmentManager());
        viewPager.setAdapter(adapter);
        tlTitle.setupWithViewPager(viewPager);
        viewPager.setCurrentItem(0);
        viewPager.setOffscreenPageLimit(2);
        setTabs();
    }

    public static final int MIN_CLICK_DELAY_TIME = 1200;
    private long lastClickTime = 0;

    @Override
    public void onClick(View v) {
        int id = v.getId();
        //防止点击过快弹框弹了多次
        long currentTime = Calendar.getInstance().getTimeInMillis();
        if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
            lastClickTime = currentTime;
            switch (id) {
                case R.id.tv_add_or_more:
                    if (position == 0 || position == 1) {
                        List<DownBean> datas = new ArrayList<>();
                        DownBean bean1 = new DownBean();
                        bean1.setName("新增考勤机");
                        DownBean bean2 = new DownBean();
                        bean2.setName("新增大型设备");
                        datas.add(bean1);
                        datas.add(bean2);
                        DownPopupWindow popupWindow = new DownPopupWindow(getActivity(), datas);
                        popupWindow.setOnItemSelectedListener(new DownPopupWindow.OnItemSelectedListener() {
                            @Override
                            public void onSelected(DownBean downBean, int position) {
                                if (position == 0) {
                                    Intent intent = new Intent(getActivity(), WorkMechineInfoActivity.class);
                                    intent.putExtra("type", "add");
                                    startActivity(intent);

                                } else if (position == 1) {
                                    Intent intent = new Intent(getActivity(), EquipmentInfoActivity.class);
                                    intent.putExtra("type", "add");
                                    startActivity(intent);
                                }
                            }
                        });
                        popupWindow.show(v, tvAddOrMore.getHeight());
                    } else {
                        Intent intent = new Intent(getActivity(), MoreOperateActivity.class);
                        startActivity(intent);
                    }
                    break;
            }
        }
    }

    class MyPagerAdapter extends FragmentPagerAdapter {


        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return ManageEquipmentFragment.this.getActivity().getResources().getString(TAB_TITLES[position]);
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

    private void setTabs() {
        /*LinearLayout linearLayout = (LinearLayout) tlTitle.getChildAt(0);
        linearLayout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        linearLayout.setDividerDrawable(ContextCompat.getDrawable(getActivity(),
                R.drawable.shape_divider_vertical));*/
        for (int i = 0; i < TAB_TITLES.length; i++) {
            TabLayout.Tab tab = tlTitle.getTabAt(i);//获得每一个tab
            tab.setCustomView(R.layout.tab_manage_equipment);//给每一个tab设置view
            TextView textView = (TextView) tab.getCustomView().findViewById(R.id.tv_tab);
            textView.setText(TAB_TITLES[i]);//设置tab上的文字
            if (i == 0) {
                textView.setBackgroundResource(R.drawable.manage_background_left_selector);
            } else if (i == 1) {
                textView.setBackgroundResource(R.drawable.manage_background_middle_selector);
            } else if (i == 2) {
                textView.setBackgroundResource(R.drawable.manage_background_right_selector);
            }
        }
    }

    protected void registerBroadcast() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SysCode.JUMP_CURRENTINDEX);
        getActivity().registerReceiver(receiver, intentFilter);
    }

    protected BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (StringUtils.equals(SysCode.JUMP_CURRENTINDEX, intent.getAction())) {
                int currentIndex = intent.getExtras().getInt("currentIndex");
                viewPager.setCurrentItem(currentIndex);
                //并重新刷新数据
                ((BaseFragment) TAB_FRAGMENTS[currentIndex]).refreshWebData();
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(receiver);
    }

    private View view;

    @Override
    protected void updateView(boolean isHideForceOpen) {
        try {
            /*if (isNoData) {
                if (view == null || view != null && !view.equals(rlNoData)) {
                    isHided = true;
                }
                view = rlNoData;
            } else {
                view = lvContent;
            }*/
            view = llParent;
            if (view == null) return;
            if (view.getVisibility() != View.VISIBLE) {
                view.setVisibility(View.VISIBLE);
            }
            ObjectAnimator oa = null;
            if (isHideForceOpen) {
                if (isHided) {
                    oa = ObjectAnimator.ofFloat(view, "translationY", -1.2f * view.getHeight(), 0f);
                }
            } else {
                if (!isHided) {
                    oa = ObjectAnimator.ofFloat(view, "translationY", 0f, -1.2f * view.getHeight());
                } else {
                    oa = ObjectAnimator.ofFloat(view, "translationY", -1.2f * view.getHeight(), 0f);
                }
            }
            if (oa != null) {
                oa.setDuration(500);
                oa.start();
                isHided = !isHided;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && this.isVisible()) {
            if (isVisibleToUser && !mHasLoadedOnce) {
                mHasLoadedOnce = true;
            }
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        setUserVisibleHint(true);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    protected View getAnimatorView() {
        return llParent;
    }

    @Override
    protected boolean isAutoInit() {
        return true;
    }
}
