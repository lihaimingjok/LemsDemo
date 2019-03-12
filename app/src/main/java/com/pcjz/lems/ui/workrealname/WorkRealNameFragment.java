package com.pcjz.lems.ui.workrealname;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.pcjz.lems.R;
import com.pcjz.lems.business.base.HomeBaseFragment;
import com.pcjz.lems.business.base.basebean.BaseData;
import com.pcjz.lems.business.common.utils.CommUtil;
import com.pcjz.lems.business.common.utils.MessageBus;
import com.pcjz.lems.business.common.utils.StringUtils;
import com.pcjz.lems.business.common.utils.TLog;
import com.pcjz.lems.business.common.view.MyViewPager;
import com.pcjz.lems.business.config.AppConfig;
import com.pcjz.lems.business.constant.ResultStatus;
import com.pcjz.lems.business.constant.SysCode;
import com.pcjz.lems.business.context.AppContext;
import com.pcjz.lems.business.entity.WeatherInfo;
import com.pcjz.lems.business.storage.SharedPreferencesManager;
import com.pcjz.lems.business.webapi.MainApi;
import com.pcjz.lems.ui.homepage.HomePageActivity;
import com.pcjz.lems.ui.workrealname.manageequipment.ManageEquipmentFragment;
import com.pcjz.lems.ui.workrealname.personinfo.PersonInfoFragment;
import com.pcjz.lems.ui.workrealname.projectsituation.ProjectSituationFragment;
import com.squareup.otto.BasicBus;
import com.squareup.otto.Subscribe;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

public class WorkRealNameFragment extends HomeBaseFragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    //private OnFragmentInteractionListener mListener;
    //tablayout.getTabAt(position).select();默认选中某项

    //工作台标题
    private final int[] TAB_TITLES = new int[]{R.string.project_situation, R.string.equipment_manage, R.string.person_information};
    private final Fragment[] TAB_FRAGMENTS = new Fragment[]{new ProjectSituationFragment(), new ManageEquipmentFragment(), new PersonInfoFragment()};
    private TabLayout tlTitle;
    public static boolean isFisrtLoadedVP = true;
    private HomePageActivity homePageActivity;
    private MyViewPager viewPager;
    private LinearLayout mWeather;
    private TextView mTvTemperature;
    private TextView mTvLocation;
    private TextView mTvWeather;
    private LocationManager locationManager;

    private int tipNum;
    private TextView tvRedTips;
    public BasicBus mBasicBus = MessageBus.getBusInstance();

    private String mUserId;
    private String mProjectId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBasicBus.register(this);
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_workbench;
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
        /*view.findViewById(R.id.iv_message).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });*/
        tvRedTips = (TextView) view.findViewById(R.id.tvRedTips);
        mWeather = (LinearLayout) view.findViewById(R.id.ll_weather);
        mTvTemperature = (TextView) view.findViewById(R.id.tv_temperature);
        mTvWeather = (TextView) view.findViewById(R.id.tv_weather_txt);
        mTvLocation = (TextView) view.findViewById(R.id.tv_weather_location);
        TextView mTvWeek = (TextView) view.findViewById(R.id.tv_weather_week);
        TextView mTvTime = (TextView) view.findViewById(R.id.tv_weather_time);
        //获取星期数
        mTvWeek.setText(CommUtil.haveWeek());
        mTvTime.setText(CommUtil.haveDate());
        viewPager = (MyViewPager) view.findViewById(R.id.viewpager);
        tlTitle = (TabLayout) view.findViewById(R.id.tl_title);
        MyPagerAdapter adapter = new MyPagerAdapter(this.getChildFragmentManager());
        tlTitle.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition(), false);
                if (isFisrtLoadedVP && tab.getPosition() == 0) {
                    ((WorkbenchBaseListFragment) TAB_FRAGMENTS[tab.getPosition()]).updateView(false);
                } else {
                    ((WorkbenchBaseListFragment) TAB_FRAGMENTS[tab.getPosition()]).updateView(true);
                }
                if (isFisrtLoadedVP) isFisrtLoadedVP = false;
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                //if (tab.getPosition() == 0) {
                ((WorkbenchBaseListFragment) TAB_FRAGMENTS[tab.getPosition()]).updateView(false);
                //}

            }
        });
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tlTitle));
        viewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        tlTitle.setTabsFromPagerAdapter(adapter);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(0);
        //表示三个界面之间来回切换都不会重新加载
        viewPager.setOffscreenPageLimit(2);
        mUserId = SharedPreferencesManager.getString(ResultStatus.USER_ID);
        mProjectId = SharedPreferencesManager.getString(mUserId + SysCode.PROJECTPERIODID);
        getRedTips();
        showProj();
        setTabs(tlTitle, getActivity().getLayoutInflater(), TAB_TITLES);
        initCity();
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

    //定位城市
    private void initCity() {
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        new Thread() {
            @Override
            public void run() {
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    Message msg = handler.obtainMessage();
                    msg.what = SysCode.HANDLER_WEATHER_LOCATION_FAILED;
                    handler.sendMessage(msg);
                    return;
                }
                Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (location != null) {
                    double latitude = location.getLatitude(); // 经度
                    double longitude = location.getLongitude(); // 纬度
                    double[] data = {latitude, longitude};
                    Message msg = handler.obtainMessage();
                    msg.what = SysCode.HANDLER_WEATHER_LOCATION_SUCCESS;
                    msg.obj = data;
                    handler.sendMessage(msg);
                } else {
                    Message msg = handler.obtainMessage();
                    msg.what = SysCode.HANDLER_WEATHER_LOCATION_FAILED;
                    handler.sendMessage(msg);
                }
            }
        }.start();
    }

    String latLongString;
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case SysCode.HANDLER_WEATHER_LOCATION_SUCCESS:
                    double[] data = (double[]) msg.obj;
                    List<Address> addList = null;
                    Geocoder ge = new Geocoder(getActivity().getApplicationContext());
                    try {
                        addList = ge.getFromLocation(data[0], data[1], 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (addList != null && addList.size() > 0) {
                        for (int i = 0; i < addList.size(); i++) {
                            Address ad = addList.get(i);
                            latLongString = ad.getLocality();
                        }
                    }
                    if (StringUtils.isEmpty(latLongString)) {
                        latLongString = "深圳市";
                    }
                    initWeather();
                    break;
                case SysCode.HANDLER_WEATHER_LOCATION_FAILED:
                    latLongString = "深圳市";
                    initWeather();
                    break;
            }
        }
    };

    private void initWeather() {
        /*if (StringUtils.isEmpty(mPeriodId)) {
            return;
        }*/
        if (StringUtils.isEmpty(latLongString)) {
            return;
        }
        latLongString = latLongString.replace("市", "");
        JSONObject obj = new JSONObject();
        HttpEntity entity = null;
        try {
            JSONObject obj0 = new JSONObject();
            obj.put("cityName", latLongString);
            //obj.put("projectPeriodId", mPeriodId);
            obj0.put("params", obj);
            entity = new StringEntity(obj0.toString(), "UTF-8");
            requestWeather(entity);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return;
        }
    }

    private void requestWeather(HttpEntity entity) {
        MainApi.requestCommon(getActivity(), AppConfig.WEATHER_URL, entity, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                try {
                    String httpResult = new String(bytes);
                    TLog.log("getWeather : " + httpResult);
                    BaseData<WeatherInfo> basedata = new Gson().fromJson(httpResult,
                            new TypeToken<BaseData<WeatherInfo>>() {
                            }.getType());
                    if (StringUtils.equals(basedata.getCode(), SysCode.SUCCESS)) {
                        if (basedata.getData() != null) {
                            if (!StringUtils.isEmpty(basedata.getData().getWeather())) {
                                if ("多云".equals(basedata.getData().getWeather())) {
                                    mWeather.setBackgroundResource(R.drawable.home_bg_weather_01);
                                } else if ("晴".equals(basedata.getData().getWeather())) {
                                    mWeather.setBackgroundResource(R.drawable.home_bg_weather_02);
                                } else if ("阴".equals(basedata.getData().getWeather())) {
                                    mWeather.setBackgroundResource(R.drawable.home_bg_weather_04);
                                } else if ("雷阵雨".equals(basedata.getData().getWeather())) {
                                    mWeather.setBackgroundResource(R.drawable.home_bg_weather_05);
                                } else if ("雨".equals(basedata.getData().getWeather())) {
                                    mWeather.setBackgroundResource(R.drawable.home_bg_weather_03);
                                } else if ("雪".equals(basedata.getData().getWeather())) {
                                    mWeather.setBackgroundResource(R.drawable.home_bg_weather_06);
                                }
                                mTvWeather.setText(basedata.getData().getWeather());
                            }
                            mTvTemperature.setText(basedata.getData().getTemperature() + "℃");
                            mTvLocation.setText(latLongString);
                        }
                        return;
                    } else {
                        AppContext.showToast(basedata.getMsg());
                        return;
                    }
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                }
                mWeather.setBackgroundResource(R.drawable.home_bg_weather_01);
                //AppContext.showToast(R.string.request_weather_failed);
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                mWeather.setBackgroundResource(R.drawable.home_bg_weather_01);
                //AppContext.showToast(R.string.request_weather_failed);
            }
        });
    }

    /**
     * 设置tab
     *
     * @param tabLayout
     * @param layoutInflater
     * @param tabTitles
     */
    private void setTabs(TabLayout tabLayout, LayoutInflater layoutInflater, int[] tabTitles) {
        for (int i = 0; i < tabTitles.length; i++) {
            TabLayout.Tab tab = tlTitle.getTabAt(i);
            View view = View.inflate(getActivity(), R.layout.tab_workbench_item, null);
            TextView textView = (TextView) view.findViewById(R.id.tv_tab);
            textView.setText(tabTitles[i]);
            tab.setCustomView(view);
        }

    }

    class MyPagerAdapter extends FragmentPagerAdapter {


        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return WorkRealNameFragment.this.getActivity().getResources().getString(TAB_TITLES[position]);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = TAB_FRAGMENTS[position];
            Bundle bundle = new Bundle();
            bundle.putInt("title", TAB_TITLES[position]);
            fragment.setArguments(bundle);
            return fragment;
        }

        @Override
        public int getCount() {
            return TAB_TITLES.length;
        }
    }

    //项目期更改，下面页面刷新
    @Override
    public void reloadData() {
        /*for (int i = 0; i < TAB_FRAGMENTS.length; i++) {
            ((BaseFragment) TAB_FRAGMENTS[i]).refreshWebData();
        }*/
    }

    @Override
    public void onAttach(Activity activity) {
        // TODO Auto-generated method stub
        super.onAttach(activity);

        // 主界面Activity
        homePageActivity = (HomePageActivity) activity;
        homePageActivity.setHandler(mHandler);
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    reloadData();
                    break;
                case 1:
                    break;
                default:
                    break;
            }
        }
    };

    public void setCurrentIndex(int currentIndex) {
        if (viewPager == null) return;
        viewPager.setCurrentItem(currentIndex, false);
        reloadData();
    }

    protected String getProjNameKey() {
        return SysCode.PROJECTPERIODNAME;
    }

    protected String getProjIdKey() {
        return SysCode.PROJECTPERIODID;
    }

    protected String getNetworkStateKey() {
        return ResultStatus.NETWORKSTATE1;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mBasicBus.unregister(this);
    }
}
