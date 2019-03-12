package com.pcjz.lems.ui.workrealname.projectsituation;

import android.app.Dialog;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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
import com.pcjz.lems.business.base.basebean.BaseListData;
import com.pcjz.lems.business.common.utils.StringUtils;
import com.pcjz.lems.business.common.utils.TLog;
import com.pcjz.lems.business.config.AppConfig;
import com.pcjz.lems.business.constant.ResultStatus;
import com.pcjz.lems.business.constant.SysCode;
import com.pcjz.lems.business.context.AppContext;
import com.pcjz.lems.business.storage.SharedPreferencesManager;
import com.pcjz.lems.business.webapi.MainApi;
import com.pcjz.lems.business.widget.lineChart.LineChartLayout2;
import com.pcjz.lems.ui.workrealname.manageequipment.CommonMethond;
import com.pcjz.lems.ui.workrealname.projectsituation.bean.DeviceStaticEntity;
import com.pcjz.lems.ui.workrealname.projectsituation.bean.DeviceStaticListEntity;
import com.pcjz.lems.ui.workrealname.projectsituation.bean.LineEntity;

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

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;

/**
 * created by yezhengyu on 2017/10/9 17:24
 */

public class DeviceRunStaActivity extends BaseActivity implements View.OnClickListener {

    private TextView mTvStartTime;
    private TextView mTvEndTime;
    private CommonMethond mCommonMethond;
    private TimePickerView pvCustomTime;
    private String mMode = "";
    private LineChartLayout2 mInclude;
    private ArrayList<DeviceStaticEntity> mDeviceList;
    private String mUserId;
    private String mProjectId;
    private int width;
    private int height;
    private Date mStartDate;
    private Date mEndDate;
    private int mDays;

    @Override
    public void setView() {
        setContentView(R.layout.activity_device_run);
        setTitle(AppContext.mResource.getString(R.string.device_run_statics));
        DisplayMetrics dm = getResources().getDisplayMetrics();
        width = dm.widthPixels;
        height = dm.heightPixels;
    }

    @Override
    public void setListener() {
        LinearLayout llStartTime = (LinearLayout) findViewById(R.id.ll_select_start_time);
        mTvStartTime = (TextView) findViewById(R.id.tv_selected_start_time);
        LinearLayout llEndTime = (LinearLayout) findViewById(R.id.ll_select_end_time);
        mTvEndTime = (TextView) findViewById(R.id.tv_selected_end_time);
        TextView tvSure = (TextView) findViewById(R.id.tv_static_sure);
        mInclude = (LineChartLayout2) findViewById(R.id.include);
        mUserId = SharedPreferencesManager.getString(ResultStatus.USER_ID);
        mProjectId = SharedPreferencesManager.getString(mUserId + SysCode.PROJECTPERIODID);
        llStartTime.setOnClickListener(this);
        llEndTime.setOnClickListener(this);
        tvSure.setOnClickListener(this);
        //初始化开始结束时间
        mCommonMethond = CommonMethond.getInstance();
        initDate();
        requestStatics();
    }

    String mStartTime = "";
    String mEndTime = "";

    private void initDate() {
        mTvStartTime.setText(mCommonMethond.initBeforeSix());
        mTvEndTime.setText(mCommonMethond.initTime());
        mStartDate = mCommonMethond.initBeforeDateSix();
        mEndDate = Calendar.getInstance().getTime();
        mDays = CommonMethond.differentDays(mStartDate, mEndDate);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        Calendar selectedDate;
        switch (id) {
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
            case R.id.tv_static_sure:
                mDays = CommonMethond.differentDays(mStartDate, mEndDate);
                if (mDays <= 30) {
                    requestStatics();
                } else {
                    //弹窗提示
                    showDialog();
                }
                break;
        }
    }

    private void requestStatics() {
        String startTime = mTvStartTime.getText().toString();
        String endTime = mTvEndTime.getText().toString();
        if (StringUtils.equals(mStartTime, startTime) && StringUtils.equals(mEndTime, endTime)) {
            return;
        }
        mStartTime = startTime;
        mEndTime = endTime;

        JSONObject obj = new JSONObject();
        HttpEntity entity = null;
        try {
            JSONObject obj0 = new JSONObject();
            obj.put("projectId", mProjectId);
            obj.put("startTime", mTvStartTime.getText().toString());
            obj.put("endTime", mTvEndTime.getText().toString());
            obj0.put("params", obj);
            entity = new StringEntity(obj0.toString(), "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        MainApi.requestCommon(this, AppConfig.GET_DEVICE_RUN_STATICS, entity, new AsyncHttpResponseHandler() {
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

    private void initDevice() {
        List<LineEntity> listEntities = new ArrayList<>();
        listEntities.addAll(mCommonMethond.initStartEndObject(mEndDate, mDays));
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

    private void showDialog() {
        final Dialog dialog = new Dialog(this, R.style.select_dialog);
        dialog.setContentView(R.layout.tip_three_dialog);
        dialog.findViewById(R.id.tv_know).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
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
        //date在date2之后，则返回false
        boolean startAfter = date2.after(date);
        //对比开始时间和结束时间，如果开始时间在结束时间之后，则结束时间置为选中的开始时间
        //如果结束时间在开始时间之前，则开始时间置为结束时间
        if (StringUtils.equals(mMode, "start")) {
            if (!startAfter) {
                mTvEndTime.setText(getTime(date));
                mEndDate = date;
            }
        } else {
            if (startAfter) {
                mTvStartTime.setText(getTime(date));
                mStartDate = date;
            }
        }
        if (StringUtils.equals(mMode, "start")) {
            mTvStartTime.setText(getTime(date));
            mStartDate = date;
        } else if (StringUtils.equals(mMode, "end")) {
            mTvEndTime.setText(getTime(date));
            mEndDate = date;
        }
    }
}
