package com.pcjz.lems.ui.workrealname.manageequipment;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.TimePickerView;
import com.bigkoo.pickerview.lib.WheelView;
import com.bigkoo.pickerview.listener.CustomListener;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.pcjz.lems.R;
import com.pcjz.lems.business.base.BaseActivity;
import com.pcjz.lems.business.base.basebean.BaseData;
import com.pcjz.lems.business.base.basebean.BaseListData;
import com.pcjz.lems.business.common.utils.StringUtils;
import com.pcjz.lems.business.common.utils.TLog;
import com.pcjz.lems.business.common.view.DefineBAGRefreshView;
import com.pcjz.lems.business.common.view.DefineBAGRefreshWithLoadView;
import com.pcjz.lems.business.common.view.RecyclerViewDivider;
import com.pcjz.lems.business.common.view.SingleDialog;
import com.pcjz.lems.business.config.AppConfig;
import com.pcjz.lems.business.constant.ResultStatus;
import com.pcjz.lems.business.constant.SysCode;
import com.pcjz.lems.business.context.AppContext;
import com.pcjz.lems.business.entity.SelectEntity;
import com.pcjz.lems.business.storage.SharedPreferencesManager;
import com.pcjz.lems.business.webapi.MainApi;
import com.pcjz.lems.ui.workrealname.manageequipment.adapter.OperateAdapter;
import com.pcjz.lems.ui.workrealname.manageequipment.bean.EquInfoBean;
import com.pcjz.lems.ui.workrealname.manageequipment.bean.OperateBean;
import com.pcjz.lems.ui.workrealname.manageequipment.bean.OperateRecordBean;
import com.pcjz.lems.ui.workrealname.manageequipment.bean.OperationBean;
import com.pcjz.lems.ui.workrealname.manageequipment.bean.OperationParams;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cn.bingoogolapple.refreshlayout.BGARefreshLayout;

import static cn.finalteam.toolsfinal.DateUtils.getTime;
import static com.pcjz.lems.business.base.BaseApplication.mContext;

/**
 * created by yezhengyu on 2017/9/25 10:27
 */

public class MoreOperateActivity extends BaseActivity implements View.OnClickListener, BGARefreshLayout.BGARefreshLayoutDelegate {

    private EditText mEtJobNumber;
    private EditText mEtName;
    private TextView mTvSelectEqu;
    private TextView mTvStartTime;
    private TextView mTvEndTime;
    private TimePickerView pvCustomTime;
    private String mMode = "";
    private CommonMethond mCommonMethond;

    private BGARefreshLayout mRefreshLayout;
    private RecyclerView mRecyclerView;
    private RelativeLayout rlNoData;
    private ImageView ivNoData;
    private TextView tvNoData;
    private TextView tvLoad;

    private OperateAdapter mAdapter;
    private int currentPage = 0;
    private int totalPage;
    List<OperateRecordBean> mOperateInfos = new ArrayList<>();
    private ArrayList<EquInfoBean> mAllEqus;
    private SingleDialog mSingleDialog;
    private SelectEntity mSelectEquInfo;
    private RelativeLayout mRlBack;

    private String mUserId;
    private String mProjectId;
    private DefineBAGRefreshWithLoadView mDefineRefreshWithLoadView;

    @Override
    public void setView() {
        setContentView(R.layout.activity_more_operate);
    }

    @Override
    public void setListener() {

    }

    @Override
    protected void setBack() {
        mRlBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeKeyboard();
                finish();
            }
        });
    }

    @Override
    protected void initView() {
        super.initView();
        setTitle(AppContext.mResource.getString(R.string.more_records));
        mRlBack = (RelativeLayout) findViewById(R.id.rl_back);
        mRefreshLayout = (BGARefreshLayout) findViewById(R.id.refresh_layout);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        rlNoData = (RelativeLayout) findViewById(R.id.rl_no_data);
        ivNoData = (ImageView) findViewById(R.id.iv_no_data);
        tvNoData = (TextView) findViewById(R.id.tv_no_data);
        tvLoad = (TextView) findViewById(R.id.tv_again_loading);
        tvLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshWebData();
            }
        });
        initNoDataView();
        initBGARefreshLayout();
        mEtJobNumber = (EditText) findViewById(R.id.et_please_job_number);
        mEtName = (EditText) findViewById(R.id.et_please_name);
        LinearLayout llSelectEqu = (LinearLayout) findViewById(R.id.ll_select_equipment);
        mTvSelectEqu = (TextView) findViewById(R.id.tv_selected_equipment);
        LinearLayout llStartTime = (LinearLayout) findViewById(R.id.ll_select_start_time);
        mTvStartTime = (TextView) findViewById(R.id.tv_selected_start_time);
        LinearLayout llEndTime = (LinearLayout) findViewById(R.id.ll_select_end_time);
        mTvEndTime = (TextView) findViewById(R.id.tv_selected_end_time);
        LinearLayout llFilter = (LinearLayout) findViewById(R.id.ll_filter);
        llSelectEqu.setOnClickListener(this);
        llStartTime.setOnClickListener(this);
        llEndTime.setOnClickListener(this);
        llFilter.setOnClickListener(this);

        //初始化开始结束时间
        mCommonMethond = CommonMethond.getInstance();
        mTvStartTime.setText(mCommonMethond.initTime());
        mTvEndTime.setText(mCommonMethond.initTime());
        refreshWebData();
    }

    private void initBGARefreshLayout() {
        // 为BGARefreshLayout 设置代理
        mRefreshLayout.setDelegate(this);
        mDefineRefreshWithLoadView = new DefineBAGRefreshWithLoadView(mContext, true, true);
        //设置刷新样式
        mRefreshLayout.setRefreshViewHolder(mDefineRefreshWithLoadView);
        mAdapter = new OperateAdapter(this, mOperateInfos);
        View view = LayoutInflater.from(MoreOperateActivity.this).inflate(R.layout.layout_operate_record_header, null);
        mAdapter.addHeaderView(view);
        mRecyclerView.setAdapter(mAdapter);
        //设置listview垂直如何显示
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new RecyclerViewDivider(
                getApplicationContext(), LinearLayoutManager.HORIZONTAL, 1, getResources().getColor(R.color.txt_gray_divider)));
        mAdapter.setOnItemClickListener(new OperateAdapter.OnItemClickListener() {
            @Override
            public void setOnClick(View holder, int position) {

            }
        });
        mUserId = SharedPreferencesManager.getString(ResultStatus.USER_ID);
        mProjectId = SharedPreferencesManager.getString(mUserId + SysCode.PROJECTPERIODID);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        closeKeyboard();
        Calendar selectedDate;
        switch (id) {
            //选择设备
            case R.id.ll_select_equipment:
                mSingleDialog = new SingleDialog(this, mSelectEquInfo, new SingleDialog.DataBackListener() {
                    @Override
                    public void getData(SelectEntity entity) {
                        equInfoFinish(entity);
                    }
                });
                mSingleDialog.setSelectTitle(getResources().getString(R.string.select_equipment));
                if (mAllEqus != null && mAllEqus.size() != 0) {
                    mSingleDialog.setInitSelecList(getInitSelecList());
                } else {
                    mSingleDialog.setInitSelecList(null);
                }
                mSingleDialog.show();
                break;
            case R.id.ll_select_start_time:
                mMode = "start";
                String startTime = mTvStartTime.getText().toString();
                selectedDate = mCommonMethond.initCustomeTimer(startTime);
                initCustomTimePicker(selectedDate);
                break;
            case R.id.ll_select_end_time:
                mMode = "end";
                String endTime = mTvEndTime.getText().toString();
                selectedDate = mCommonMethond.initCustomeTimer(endTime);
                initCustomTimePicker(selectedDate);
                break;
            case R.id.ll_filter:
                refreshWebData();
                break;
        }
    }

    private void equInfoFinish(SelectEntity entity) {
        mSelectEquInfo = entity;
        mTvSelectEqu.setText(entity.getName());
        refreshWebData();
    }

    @Override
    protected void initWebData() {
        initLoading("");
        //获取大型设备
        requestAllEqu();
    }

    private void requestAllEqu() {
        JSONObject obj = new JSONObject();
        HttpEntity entity = null;
        try {
            JSONObject obj0 = new JSONObject();
            obj.put("projectId", mProjectId);
            obj0.put("params", obj);
            entity = new StringEntity(obj0.toString(), "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        MainApi.requestCommon(this, AppConfig.GET_ALL_LARGE_EQUIPMENT, entity, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                hideLoading();
                try {
                    String httpResult = new String(bytes);
                    TLog.log("getAllEqu : " + httpResult);
                    Type type = new TypeToken<BaseListData<EquInfoBean>>() {
                    }.getType();
                    BaseListData<EquInfoBean> datas = new Gson().fromJson(httpResult, type);
                    if (StringUtils.equals(datas.getCode(), SysCode.SUCCESS)) {
                        mAllEqus = datas.getData();
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
                AppContext.showToast(R.string.please_check_network);
                hideLoading();
            }
        });
    }

    //有网无数据
    private void initNoDataView() {
        if (ivNoData == null) return;
        rlNoData.setBackground(ContextCompat.getDrawable(this, R.color.white));
        ivNoData.setImageResource(R.drawable.pop_window_choose_no_content);
        tvNoData.setText(AppContext.mResource.getString(R.string.operate_record_no_data));
        tvLoad.setVisibility(View.GONE);
    }

    //网络异常
    private void initNoInternetView() {
        if (ivNoData == null) return;
        ivNoData.setImageResource(R.drawable.no_internet_icon);
        tvNoData.setText(AppContext.mResource.getString(R.string.please_check_network));
        tvLoad.setVisibility(View.VISIBLE);
    }

    @Override
    public void refreshWebData() {

        mDefineRefreshWithLoadView.updateLoadingMoreText("加载中...");
        mDefineRefreshWithLoadView.showLoadingMoreImg();

        currentPage = 1;
        requestList();
    }

    private void requestList() {
        OperationParams param = new OperationParams();
        param.setProjectId(mProjectId);
        if (mSelectEquInfo != null) {
            param.setDeviceId(mSelectEquInfo.getId());
        }
        if (!StringUtils.isEmpty(mEtJobNumber.getText().toString())) {
            param.setEmpWorkCode(mEtJobNumber.getText().toString());
        }
        if (!StringUtils.isEmpty(mEtName.getText().toString())) {
            param.setEmpName(mEtName.getText().toString());
        }
        param.setStartTime(mTvStartTime.getText().toString());
        param.setEndTime(mTvEndTime.getText().toString());
        OperationBean operation = new OperationBean();
        operation.setPageNo(currentPage);
        operation.setPageSize(10);
        operation.setParams(param);
        //请求大型设备列表
        HttpEntity entity = null;
        try {
            String params = new Gson().toJson(operation);
            entity = new StringEntity(params, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        MainApi.requestCommon(this, AppConfig.GET_OPERATE_RECORD_LIST, entity, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                try {
                    String httpResult = new String(bytes);
                    TLog.log("getOperateInfoList : " + httpResult);
                    Type type = new TypeToken<BaseData<OperateBean>>() {
                    }.getType();
                    BaseData<OperateBean> datas = new Gson().fromJson(httpResult, type);
                    if (StringUtils.equals(datas.getCode(), ResultStatus.SUCCESS)) {
                        List<OperateRecordBean> results = datas.getData().results;
                        if (datas.getData() != null) {
                            totalPage = datas.getData().totalPage;
                            if (totalPage > 0) {
                                if (results != null && results.size() != 0) {
                                    if (mRecyclerView != null && mRecyclerView.getVisibility() != View.VISIBLE) {
                                        mRecyclerView.setVisibility(View.VISIBLE);
                                    }
                                    if (rlNoData != null && rlNoData.getVisibility() == View.VISIBLE) {
                                        rlNoData.setVisibility(View.GONE);
                                    }
                                    if (currentPage == 1) {
                                        mOperateInfos.clear();
                                    }
                                    mOperateInfos.addAll(results);
                                    mAdapter.notifyDataSetChanged();
                                    if (currentPage == 1) {
                                        mRefreshLayout.endRefreshing();
                                    } else {
                                        mRefreshLayout.endLoadingMore();
                                    }
                                } else {
                                    initNoDataView();
                                    initNoData();
                                }
                            } else {
                                initNoDataView();
                                initNoData();
                            }
                        } else {
                            if (currentPage == 1) {
                                initNoDataView();
                                initNoData();
                            }
                        }
                        return;
                    } else {
                        AppContext.showToast(datas.getMsg());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                initNoDataView();
                initNoData();
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                initNoData();
                initNoInternetView();
            }
        });
    }

    private void initNoData() {
        if (rlNoData == null) return;
        if (rlNoData != null && rlNoData.getVisibility() != View.VISIBLE) {
            rlNoData.setVisibility(View.VISIBLE);
        }
        if (mRecyclerView != null && mRecyclerView.getVisibility() == View.VISIBLE) {
            mRecyclerView.setVisibility(View.GONE);
        }
        if (currentPage == 1) {
            mRefreshLayout.endRefreshing();
        } else {
            mRefreshLayout.endLoadingMore();
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    //下拉加载最新数据
                    currentPage = 1;
                    requestList();
                    break;
                case 1:
                    //上拉加载更多数据
                    currentPage++;
                    requestList();
                    break;
                case 2:
                    mRefreshLayout.endLoadingMore();
                    break;
                default:
                    break;

            }
        }
    };

    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {
        //下拉加载最新数据
        mDefineRefreshWithLoadView.updateLoadingMoreText("加载中...");
        mDefineRefreshWithLoadView.showLoadingMoreImg();
        handler.sendEmptyMessageDelayed(0, 1000);
    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
        if (currentPage >= totalPage) {
            mDefineRefreshWithLoadView.updateLoadingMoreText("没有更多数据啦");
            mDefineRefreshWithLoadView.hideLoadingMoreImg();
            handler.sendEmptyMessageDelayed(2, 1000);
            return true;
        }
        //上拉加载更多数据
        handler.sendEmptyMessageDelayed(1, 1000);
        return true;
    }


    private void initCustomTimePicker(Calendar selectedDate) {
        //时间选择器 ，自定义布局
        pvCustomTime = new TimePickerView.Builder(this, new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {//选中事件回调
                changeSelectDate(date);
            }
        })
                .setDate(selectedDate)
                .setRangDate(mCommonMethond.mStartDate, mCommonMethond.mEndDate)
                .setContentSize(18)
                .setTextColorCenter(ContextCompat.getColor(getApplicationContext(), R.color.txt_black))
                .setTextColorOut(ContextCompat.getColor(getApplicationContext(), R.color.txt_black))
                .setLayoutRes(R.layout.pickerview_custom_time, new CustomListener() {
                    @Override
                    public void customLayout(View v) {
                        final TextView tvSubmit = (TextView) v.findViewById(R.id.tv_finish);
                        tvSubmit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                pvCustomTime.returnData();
                                pvCustomTime.dismiss();
                            }
                        });
                    }
                })
                .setType(new boolean[]{true, true, true, false, false, false})
                .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .setDividerType(WheelView.DividerType.WRAP)
                .build();
        pvCustomTime.show();
    }

    private String getTime(Date date) {//可根据需要自行截取数据显示
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date);
    }

    private void changeSelectDate(Date date) {
        DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        String time;
        if (StringUtils.equals(mMode, "start")) {
            time = mTvEndTime.getText().toString();
        } else {
            time = mTvStartTime.getText().toString();
        }
        Date date2 = null;
        try {
            date2 = fmt.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        boolean startAfter = date2.after(date);
        if (StringUtils.equals(mMode, "start")) {
            if (!startAfter) {
                mTvEndTime.setText(getTime(date));
            }
        } else {
            if (startAfter) {
                mTvStartTime.setText(getTime(date));
            }
        }
        if (StringUtils.equals(mMode, "start")) {
            mTvStartTime.setText(getTime(date));
        } else if (StringUtils.equals(mMode, "end")) {
            mTvEndTime.setText(getTime(date));
        }
        refreshWebData();
    }

    @Override
    protected boolean hasLoading() {
        return true;
    }

    private List<SelectEntity> getInitSelecList() {
        List<SelectEntity> selectInfoList = new ArrayList<>();
        SelectEntity selectInfo;
        for (int i = 0; i < mAllEqus.size(); i++) {
            EquInfoBean equInfoBean = mAllEqus.get(i);
            selectInfo = new SelectEntity();
            selectInfo.setId(equInfoBean.getId());
            selectInfo.setName(equInfoBean.getDeviceName());
            selectInfo.setSelect(false);
            selectInfoList.add(selectInfo);
        }
        return selectInfoList;
    }

    private void closeKeyboard() {
        View view = getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
