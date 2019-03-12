package com.pcjz.lems.ui.workrealname.personinfo;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.pcjz.lems.R;
import com.pcjz.lems.business.common.utils.MessageBus;
import com.pcjz.lems.business.common.utils.StringUtils;
import com.pcjz.lems.business.common.utils.TLog;
import com.pcjz.lems.business.common.view.MyViewPager;
import com.pcjz.lems.business.config.AppConfig;
import com.pcjz.lems.business.constant.ResultStatus;
import com.pcjz.lems.business.constant.SysCode;
import com.pcjz.lems.business.storage.SharedPreferencesManager;
import com.pcjz.lems.business.webapi.MainApi;
import com.pcjz.lems.ui.workrealname.WorkbenchBaseListFragment;
import com.squareup.otto.BasicBus;
import com.squareup.otto.Subscribe;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

/*import static com.umeng.analytics.pro.x.R;*/

/**
 * created by greak on 2017/9/12 16:36
 */

public class PersonInfoFragment extends WorkbenchBaseListFragment implements View.OnClickListener{

    private boolean isHided = false;
    private boolean mHasLoadedOnce = false;
    private TextView tvAddBtn;
    private PersonManageFragment personManageFragment;
    private PersonCheckFragment personCheckFragment;
    private TabLayout mTlTitle;
    private LinearLayout llParent;
    private String mPostId;

    private int tipNum;
    private TextView tvRedTips;
    public BasicBus mBasicBus = MessageBus.getBusInstance();

    private String mUserId;
    private String mProjectId;


    Fragment[] TAB_FRAGMENTS = new Fragment[]{new PersonManageFragment(), new PersonCheckFragment()};
    private final int[] TAB_TITLES = new int[]{R.string.person_manage_table, R.string.person_check_table};

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_person_info;
    }

    @Override
    protected void initView(View view) {
        super.initView(view);

        llParent = (LinearLayout) view.findViewById(R.id.ll_parent);
        tvAddBtn = (TextView) view.findViewById(R.id.tvAddBtn);
        final MyViewPager viewPager = (MyViewPager) view.findViewById(R.id.viewpager);
        mTlTitle = (TabLayout) view.findViewById(R.id.tl_title);
        tvRedTips = (TextView) view.findViewById(R.id.tvRedTips);
        MyPagerAdapter adapter = new MyPagerAdapter(this.getChildFragmentManager());
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
        viewPager.setCurrentItem(0);
        setTabs();
        tvAddBtn.setOnClickListener(this);
        mUserId = SharedPreferencesManager.getString(ResultStatus.USER_ID);
        mProjectId = SharedPreferencesManager.getString(mUserId + SysCode.PROJECTPERIODID);
        getRedTips();
    }

    private void getRedTips(){
        HttpEntity entity = null;
        JSONObject pData = new JSONObject();
        JSONObject obj = new JSONObject();
        try {
            obj.put("isVerifyed", "0");
            obj.put("projectId", mProjectId);
            pData.put("params", obj);
            pData.put("pageNo", 1);
            pData.put("pageSize", 10);

            entity = new StringEntity(pData.toString(), "UTF-8");


        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        MainApi.requestCommon(getActivity(), AppConfig.GET_PERSON_PAGE, entity, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String httpResult = new String(bytes);
                TLog.log("httpResults "+httpResult);
                try {
                    JSONObject obj = new JSONObject(httpResult);
                    if(StringUtils.equals(obj.getString("code"), SysCode.SUCCESS)){

                        JSONArray tempList = obj.getJSONObject("data").getJSONArray("results");
                        String tempMax =  obj.getJSONObject("data").getString("totalPage");
                        tipNum = obj.getJSONObject("data").getInt("noVerifySize");
                        if(tipNum > 0){
                            tvRedTips.setVisibility(View.VISIBLE);
                        }else{
                            tvRedTips.setVisibility(View.INVISIBLE);
                        }



                    }else{

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
            }
        });
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.tvAddBtn:
                Intent intent = new Intent(getActivity(), PersonInfoAddActivity.class);
                startActivity(intent);
                break;
        }
    }

    class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return PersonInfoFragment.this.getActivity().getResources().getString(TAB_TITLES[position]);
        }

        @Override
        public Fragment getItem(int position) {

            return getFragment(position);
        }

        @Override
        public int getCount() {
            return TAB_TITLES.length;
        }
    }

    private Fragment getFragment(int position) {
        switch (position) {
            case 0:
                if (personManageFragment == null) {
                    personManageFragment = new PersonManageFragment();
                }
                return personManageFragment;
            case 1:
                    if (personCheckFragment == null) {
                        personCheckFragment = new PersonCheckFragment();
                    }
                    return personCheckFragment;

            default:
                if (personManageFragment == null) {
                    personManageFragment = new PersonManageFragment();
                }
                return personManageFragment;
        }
    }

    private void setTabs() {
        LinearLayout linearLayout = (LinearLayout) mTlTitle.getChildAt(0);
        linearLayout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        linearLayout.setDividerDrawable(ContextCompat.getDrawable(getActivity(),
                R.drawable.bg_table_person_chose));
        for (int i = 0; i < TAB_TITLES.length; i++) {
            TabLayout.Tab tab = mTlTitle.getTabAt(i);
            View view = View.inflate(getActivity(), R.layout.tab_personinfo, null);
            TextView textView = (TextView) view.findViewById(R.id.tv_tab);
            textView.setText(TAB_TITLES[i]);
            tab.setCustomView(view);
        }

        /*for (int i = 0; i < TAB_TITLES.length; i++) {
            TabLayout.Tab tab = mTlTitle.getTabAt(i);
            tab.setCustomView(R.layout.tab_personinfo);
            if (i == 0) {
                tab.getCustomView().findViewById(R.id.tv_tab).setSelected(true);
            }
            TextView textView = (TextView) tab.getCustomView().findViewById(R.id.tv_tab);
            textView.setText(TAB_TITLES[i]);
        }*/
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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBasicBus.register(this);
    }

    @Subscribe
    public void excuteAction(String action){
        if (SysCode.CHANGE_PROJECT_PERIOD.equals(action)) {
            String projectId = SharedPreferencesManager.getString(mUserId + SysCode.PROJECTPERIODID);
            if (!StringUtils.equals(projectId, mProjectId)) {
                tipNum = 0;
                mProjectId = projectId;
                getRedTips();
            }
        }else{
            tipNum = 0;
            getRedTips();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mBasicBus.unregister(this);
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
