package com.pcjz.lems.ui.workrealname.projectsituation;

import android.animation.ObjectAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.pcjz.lems.R;
import com.pcjz.lems.business.base.basebean.BaseListData;
import com.pcjz.lems.business.common.utils.MessageBus;
import com.pcjz.lems.business.common.utils.StringUtils;
import com.pcjz.lems.business.common.utils.TLog;
import com.pcjz.lems.business.config.AppConfig;
import com.pcjz.lems.business.constant.ResultStatus;
import com.pcjz.lems.business.constant.SysCode;
import com.pcjz.lems.business.context.AppContext;
import com.pcjz.lems.business.storage.SharedPreferencesManager;
import com.pcjz.lems.business.webapi.MainApi;
import com.pcjz.lems.business.widget.lineChart.LineChartLayout;
import com.pcjz.lems.ui.workrealname.WorkbenchBaseListFragment;
import com.pcjz.lems.ui.workrealname.manageequipment.CommonMethond;
import com.pcjz.lems.ui.workrealname.projectsituation.adapter.HomeWarnMessageAdapter;
import com.pcjz.lems.ui.workrealname.projectsituation.bean.DeviceStaticEntity;
import com.pcjz.lems.ui.workrealname.projectsituation.bean.DeviceStaticListEntity;
import com.pcjz.lems.ui.workrealname.projectsituation.bean.LineEntity;
import com.pcjz.lems.ui.workrealname.projectsituation.bean.WarnMsgEntity;
import com.squareup.otto.BasicBus;
import com.squareup.otto.Subscribe;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * created by yezhengyu on 2017/9/12 16:31
 */

public class ProjectSituationFragment extends WorkbenchBaseListFragment implements View.OnClickListener {

    private boolean isHided = false;
    private boolean mHasLoadedOnce = false;
    private TextView tvWarnHistory;
    private ScrollView scrollView;
//    private ListScrollView scrollView;;
    private ListView mWarnMsgListView;
//    private ScrollListView mWarnMsgListView;
    private List<WarnMsgEntity> mWarnMsgList = new ArrayList<>();
    private HomeWarnMessageAdapter mWarnAdapter;
    private int curPage = 1;
    private int maxPage = 1;
    private int totalNums = 0;


    private RelativeLayout rlRealtime;
    private LineChartLayout mInclude;
    private LinearLayout llParent;
    private String mProjectId;

    protected BasicBus mBasicBus = MessageBus.getBusInstance();



    //今日设备概况
    private TextView tvAllDevNum, tvLeftName, tvLeftNum, tvLeftRuning, tvLeftRuned, tvLeftUsed, tvLeftBreaken,
            tvRightName, tvRightNum, tvRightRuning, tvRightRuned, tvRightUsed, tvRightBreaken;
    private CommonMethond mCommon;
    private ArrayList<DeviceStaticEntity> mDeviceList;
    private String mUserId;

    private RelativeLayout rlNoData;
    private ImageView ivNoData;
    private TextView tvNoData;

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_project_situation;
    }

    @Override
    protected void initView(View view) {
        llParent = (LinearLayout) view.findViewById(R.id.ll_parent);
        rlRealtime = (RelativeLayout) view.findViewById(R.id.rlRealtime);
        mInclude = (LineChartLayout) view.findViewById(R.id.include);
        tvWarnHistory = (TextView) view.findViewById(R.id.tvWarnHistory);
        mUserId = SharedPreferencesManager.getString(ResultStatus.USER_ID);
        mProjectId = SharedPreferencesManager.getString(mUserId + SysCode.PROJECTPERIODID);
        tvAllDevNum = (TextView) view.findViewById(R.id.tvAllDevNum);
        tvLeftName = (TextView) view.findViewById(R.id.tvLeftName);
        tvLeftNum = (TextView) view.findViewById(R.id.tvLeftNum);
        tvLeftRuning = (TextView) view.findViewById(R.id.tvLeftRuning);
        tvLeftRuned = (TextView) view.findViewById(R.id.tvLeftRuned);
        tvLeftUsed = (TextView) view.findViewById(R.id.tvLeftUsed);
        tvLeftBreaken = (TextView) view.findViewById(R.id.tvLeftBreaken);
        tvRightName = (TextView) view.findViewById(R.id.tvRightName);
        tvRightNum = (TextView) view.findViewById(R.id.tvRightNum);
        tvRightRuning = (TextView) view.findViewById(R.id.tvRightRuning);
        tvRightRuned = (TextView) view.findViewById(R.id.tvRightRuned);
        tvRightUsed = (TextView) view.findViewById(R.id.tvRightUsed);
        tvRightBreaken = (TextView) view.findViewById(R.id.tvRightBreaken);

        TextView tvTextView47 = (TextView) view.findViewById(R.id.textView47);
        tvTextView47.setOnClickListener(this);
        rlRealtime.setOnClickListener(this);
        tvWarnHistory.setOnClickListener(this);

        rlNoData = (RelativeLayout) view.findViewById(R.id.rl_no_data);
        ivNoData = (ImageView) view.findViewById(R.id.iv_no_data);
        tvNoData = (TextView) view.findViewById(R.id.tv_no_data);

        mCommon = CommonMethond.getInstance();

        /*mPullDownScrollView = (PullDownScrollView) view.findViewById(R.id.refresh_root);
        mPullDownScrollView.setRefreshListener(this);
        mPullDownScrollView.setPullDownElastic(new PullDownElasticImp(getActivity()));*/

        registerBroadcastReceiver();

        //获取今日设备概况
        getTodayDeviceSummary();

        //获取设备运行统计
        requestStatics();

        //预警消息
        scrollView = (ScrollView) view.findViewById(R.id.slView);
        mWarnMsgListView = (ListView) view.findViewById(R.id.lv_content);
        /*scrollView.setListView(mWarnMsgListView);*/
        mWarnAdapter = new HomeWarnMessageAdapter();
        mWarnAdapter.setData(mWarnMsgList);
        mWarnMsgListView.setAdapter(mWarnAdapter);
        initNoDataView();
        getWarnMessageList(1);

        /*mWarnMsgListView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction()==MotionEvent.ACTION_UP) {
                    scrollView.requestDisallowInterceptTouchEvent(false);
                }else {
                    scrollView.requestDisallowInterceptTouchEvent(true);
                }
                return false;
            }
        });*/
        mWarnAdapter.setOnIDoListener(new HomeWarnMessageAdapter.onIDoListener(){

            @Override
            public void onIDoClick(int position) {
                String wmid = mWarnMsgList.get(position).getMsgId();
                readWarnMsg(wmid, position);
            }
        });
        mWarnMsgListView.setFocusable(false);
    }

    /*@Override
    public void onRefresh(PullDownScrollView view) {
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                mPullDownScrollView.finishRefresh("");
                //获取今日设备概况
                getTodayDeviceSummary();

                //获取设备运行统计
                requestStatics();
                getWarnMessageList(1);
            }
        }, 2000);
    }*/

    private void registerBroadcastReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SysCode.ACTION_JPUSH_NOTIFICATION_RECEIVED);
        intentFilter.addAction(SysCode.ACTION_RELOAD_DATA_SUCCESS);
        getActivity().registerReceiver(receiver, intentFilter);
    }

    private void unregisterBroadcastReceiver() {
        getActivity().unregisterReceiver(receiver);
    }

    private void initNoDataView() {
        if (ivNoData == null) return;
        rlNoData.setBackground(ContextCompat.getDrawable(getActivity(), R.color.white));
        ivNoData.setImageResource(R.drawable.yj_message_blank_pages_icon);
        tvNoData.setText("当前没有预警消息哟");
    }

    private void setListViewHeightBasedOnChildren(ListView listview) {

        ListAdapter listAdapter = listview.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listview);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listview.getLayoutParams();
        params.height = totalHeight
                + (listview.getDividerHeight() * (listAdapter.getCount() - 1));
        // if without this statement,the listview will be a
        // little short
//        params.height += 5;

        // 将测量的高度设置给listView
        listview.setLayoutParams(params);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBasicBus.register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mBasicBus.unregister(this);
        unregisterBroadcastReceiver();
    }

    @Subscribe
    public void excuteAction(String action) {
        if (SysCode.CHANGE_PROJECT_PERIOD.equals(action)) {
            String projectId = SharedPreferencesManager.getString(mUserId + SysCode.PROJECTPERIODID);
                mProjectId = projectId;
                //获取今日设备概况
                getTodayDeviceSummary();
                //获取设备运行统计
                requestStatics();
                //获取预警消息
                maxPage = 0;
                totalNums = 0;
                getWarnMessageList(1);
        }
    }

    private void readWarnMsg(String id, final int position){

        HttpEntity entity = null;
        JSONObject pData = new JSONObject();
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("id", id);
            pData.put("params", paramObj);
            entity = new StringEntity(pData.toString(), "UTF-8");
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        MainApi.requestCommon(getActivity(), AppConfig.WARN_MSG_READ, entity, new AsyncHttpResponseHandler() {


            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String httpResult = new String(bytes);
                try {
                    JSONObject obj = new JSONObject(httpResult);
                    if(StringUtils.equals(obj.getString("code"), SysCode.SUCCESS)){
                        /*totalNums --;
                        mWarnMsgList.remove(position);
                        mWarnAdapter.notifyDataSetChanged();
                        if(totalNums == 0){
                            rlNoData.setVisibility(View.VISIBLE);
                            initNoData();
                        }else{

                        }*/
                        maxPage = 0;
                        totalNums = 0;
                        getWarnMessageList(1);

                    }else{
                        AppContext.showToast("操作失败");
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



    private void getWarnMessageList(int currutPage){

        HttpEntity entity = null;
        JSONObject pData = new JSONObject();
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("projectId", mProjectId);
            paramObj.put("isRead", "0");
            pData.put("params", paramObj);
            pData.put("pageNo", currutPage);
            pData.put("pageSize", 100);
            entity = new StringEntity(pData.toString(), "UTF-8");
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        MainApi.requestCommon(getActivity(), AppConfig.WARN_MSG_LIST, entity, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String httpResult = new String(bytes);
                try {

                    JSONObject obj = new JSONObject(httpResult);
                    if(StringUtils.equals(obj.getString("code"), SysCode.SUCCESS)){
                        JSONArray tempList = obj.getJSONObject("data").getJSONArray("results");
                        String tempMax =  obj.getJSONObject("data").getString("totalPage");
                        totalNums = Integer.parseInt(obj.getJSONObject("data").getString("totalRecord"));
                        maxPage = Integer.parseInt(tempMax);
                        WarnMsgEntity bean;
                        mWarnMsgList.clear();
                        if(maxPage > 0){
                            if (rlNoData != null && rlNoData.getVisibility() == View.VISIBLE) {
                                rlNoData.setVisibility(View.GONE);

                            }
                            if(curPage == 1){
                                mWarnMsgList.clear();
                            }
                            for(int j =0; j < tempList.length(); j ++){
                                JSONObject itemObj = tempList.getJSONObject(j);
                                bean = new WarnMsgEntity();
                                if(!itemObj.isNull("deviceName")){
                                    bean.setWarnDevice(itemObj.getString("deviceName"));
                                }
                                if(!itemObj.isNull("warnContent")){
                                    bean.setWarnContent(itemObj.getString("warnContent"));
                                }
                                if(!itemObj.isNull("warnTime")){
                                    bean.setWarnDate(itemObj.getString("warnTime"));
                                }
                                bean.setMsgId(itemObj.getString("id"));
                                bean.setIsRead(0);
                                mWarnMsgList.add(bean);
                            }
//                        setListViewHeightBasedOnChildren(mWarnMsgListView);
                            mWarnAdapter.notifyDataSetChanged();
                        }else {
                            mWarnMsgList.clear();
                            mWarnAdapter.notifyDataSetChanged();
                            rlNoData.setVisibility(View.VISIBLE);
                            initNoData();
                        }


                    }else{
                        mWarnMsgList.clear();
                        mWarnAdapter.notifyDataSetChanged();
                        rlNoData.setVisibility(View.VISIBLE);
                        initNoData();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    mWarnMsgList.clear();
                    mWarnAdapter.notifyDataSetChanged();
                    rlNoData.setVisibility(View.VISIBLE);
                    initNoData();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                mWarnMsgList.clear();
                mWarnAdapter.notifyDataSetChanged();
                rlNoData.setVisibility(View.VISIBLE);
                initNoData();
            }
        });
    }

    private void initNoData() {
        if (rlNoData == null) return;
        if (rlNoData != null && rlNoData.getVisibility() != View.VISIBLE) {
            rlNoData.setVisibility(View.VISIBLE);
        }
        /*if (mLayout != null && mLayout.getVisibility() == View.VISIBLE) {
            mLayout.setVisibility(View.GONE);
        }*/
    }

    private void getTodayDeviceSummary(){
        HttpEntity entity = null;
        JSONObject pData = new JSONObject();
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("projectId", mProjectId);
            pData.put("params", paramObj);
            entity = new StringEntity(pData.toString(), "UTF-8");
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        MainApi.requestCommon(getActivity(), AppConfig.TODAY_DEVICE_SUMMARY, entity, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String httpResult = new String(bytes);
                try {
                    JSONObject obj = new JSONObject(httpResult);
                    if (StringUtils.equals(obj.getString("code"), SysCode.SUCCESS)) {

                        JSONArray typeList = obj.getJSONObject("data").getJSONArray("typeList");
                        tvAllDevNum.setText(obj.getJSONObject("data").getString("deviceTotal"));
                        for (int j = 0; j < typeList.length(); j++) {
                            JSONObject itemObj = typeList.getJSONObject(j);
                             /*tvAllDevNum, tvLeftName, tvLeftNum,tvLeftRuning, tvLeftRuned, tvLeftUsed, tvLeftBreaken,
                                tvRightName, tvRightNum, tvRightRuning, tvRightRuned, tvRightUsed, tvRightBreaken*/
                            if (j == 0) {
                                if (!itemObj.isNull("typeName")) {
                                    tvLeftName.setText(itemObj.getString("typeName"));
                                }
                                if (!itemObj.isNull("deviceTypeTotal")) {
                                    tvLeftNum.setText(itemObj.getString("deviceTypeTotal"));
                                }
                                if (!itemObj.isNull("deviceYxzTotal")) {
                                    tvLeftRuning.setText(itemObj.getString("deviceYxzTotal"));
                                }
                                if (!itemObj.isNull("deviceWyxTotal")) {
                                    tvLeftRuned.setText(itemObj.getString("deviceWyxTotal"));
                                }
                                if (!itemObj.isNull("deviceWsyTotal")) {
                                    tvLeftUsed.setText(itemObj.getString("deviceWsyTotal"));
                                }
                                if (!itemObj.isNull("deviceYccTotal")) {
                                    tvLeftBreaken.setText(itemObj.getString("deviceYccTotal"));
                                }

                            } else {
                                if (!itemObj.isNull("typeName")) {
                                    tvRightName.setText(itemObj.getString("typeName"));
                                }
                                if (!itemObj.isNull("deviceTypeTotal")) {
                                    tvRightNum.setText(itemObj.getString("deviceTypeTotal"));
                                }
                                if (!itemObj.isNull("deviceYxzTotal")) {
                                    tvRightRuning.setText(itemObj.getString("deviceYxzTotal"));
                                }
                                if (!itemObj.isNull("deviceWyxTotal")) {
                                    tvRightRuned.setText(itemObj.getString("deviceWyxTotal"));
                                }
                                if (!itemObj.isNull("deviceWsyTotal")) {
                                    tvRightUsed.setText(itemObj.getString("deviceWsyTotal"));
                                }
                                if (!itemObj.isNull("deviceYccTotal")) {
                                    tvRightBreaken.setText(itemObj.getString("deviceYccTotal"));
                                }
                            }
                        }


                    } else {

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

    private void requestStatics() {
        JSONObject obj = new JSONObject();
        HttpEntity entity = null;
        try {
            JSONObject obj0 = new JSONObject();
            obj.put("projectId", mProjectId);
            obj.put("startTime", mCommon.initBeforeSix());
            obj.put("endTime", mCommon.initTime());
            obj0.put("params", obj);
            entity = new StringEntity(obj0.toString(), "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        MainApi.requestCommon(getActivity(), AppConfig.GET_DEVICE_RUN_STATICS, entity, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                try {
                    String httpResult = new String(bytes);
                    TLog.log("getStatics : " + httpResult);
                    Type type = new TypeToken<BaseListData<DeviceStaticEntity>>() {
                    }.getType();
                    BaseListData<DeviceStaticEntity> datas = new Gson().fromJson(httpResult, type);
                    if (StringUtils.equals(datas.getCode(), SysCode.SUCCESS)) {
                        mDeviceList = datas.getData();
                        //初始化时间对应的使用数目
                        initDevice();
                        return;
                    } else {
                        AppContext.showToast(datas.getMsg());
                        return;
                    }
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

            }
        });
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (SysCode.ACTION_JPUSH_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
                maxPage = 0;
                totalNums = 0;
                getWarnMessageList(1);
            }
        }
    };

    private void initDevice() {
        List<LineEntity> listEntities = new ArrayList<>();
        listEntities.addAll(mCommon.initObjectTime());
        if (mDeviceList != null && mDeviceList.size() != 0) {
            //如果是起重机
            if (mDeviceList.get(0) != null) {
                List<DeviceStaticListEntity> listTimes = mDeviceList.get(0).getList();
                for (int i = 0; i < listEntities.size(); i++) {
                    LineEntity listEntityAll = listEntities.get(i);
                    for (int j = 0; j < listTimes.size(); j++) {
                        DeviceStaticListEntity listEntityLack = listTimes.get(j);
                        if (StringUtils.equals(listEntityAll.getTime(), listEntityLack.getOperatDate())) {
                            listEntityAll.setTowerCranes(listEntityLack.getUsedCount());
                        }
                    }
                }
            }
            //如果是升降机
            if (mDeviceList.get(1) != null) {
                List<DeviceStaticListEntity> listTimes = mDeviceList.get(1).getList();
                for (int i = 0; i < listEntities.size(); i++) {
                    LineEntity listEntityAll = listEntities.get(i);
                    for (int j = 0; j < listTimes.size(); j++) {
                        DeviceStaticListEntity listEntityLack = listTimes.get(j);
                        if (StringUtils.equals(listEntityAll.getTime(), listEntityLack.getOperatDate())) {
                            listEntityAll.setElevators(listEntityLack.getUsedCount());
                        }
                    }
                }
            }
        }
        mInclude.removeAllViews();
        mInclude.refreshLineView(listEntities);
    }

    @Override
    public void initWebData() {

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

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.rlRealtime:
                Intent intent = new Intent(getActivity(), RealtimeSituationActivity.class);
                startActivity(intent);
                break;
            case R.id.textView47:
                Intent intent1 = new Intent(getActivity(), DeviceRunStaActivity.class);
                startActivity(intent1);
                break;
            case R.id.tvWarnHistory:
                Intent intenth = new Intent(getActivity(), WarnmsgHistoryActivity.class);
                intenth.putExtra("selectedProId", mProjectId);
                startActivity(intenth);
                break;
        }
    }
}
