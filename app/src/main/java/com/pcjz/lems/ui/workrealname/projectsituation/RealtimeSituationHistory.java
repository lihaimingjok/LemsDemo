package com.pcjz.lems.ui.workrealname.projectsituation;

import android.graphics.drawable.BitmapDrawable;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.pcjz.lems.R;
import com.pcjz.lems.business.base.BaseActivity;
import com.pcjz.lems.business.common.utils.StringUtils;
import com.pcjz.lems.business.common.utils.TLog;
import com.pcjz.lems.business.config.AppConfig;
import com.pcjz.lems.business.constant.ResultStatus;
import com.pcjz.lems.business.constant.SysCode;
import com.pcjz.lems.business.storage.SharedPreferencesManager;
import com.pcjz.lems.business.webapi.MainApi;
import com.pcjz.lems.business.widget.calendarPlugin.MonthlyFragment;
import com.pcjz.lems.business.widget.calendarPlugin.OneDayView;
import com.pcjz.lems.ui.workrealname.projectsituation.adapter.RealtimeHistoryAdapter;
import com.pcjz.lems.ui.workrealname.projectsituation.bean.DeviceDriverEntity;
import com.pcjz.lems.ui.workrealname.projectsituation.bean.DeviceEntity;
import com.pcjz.lems.ui.workrealname.projectsituation.bean.RelateMachineEntity;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Greak on 2017/9/21.
 */

public class RealtimeSituationHistory extends BaseActivity implements View.OnClickListener{
    private TextView tvTitle, tvMod, tvDate, tvTilteTime;
    private String teampDate;
    private RelativeLayout rlRight;


    private View parentView;
    private Button mBtnDate;
    private PopupWindow mPopupCalendar;
    private LinearLayout llPopup;
    private View cancleViewBtn;
    private OneDayView preOneDayView = null;
    private int curYear;

    private String publicProjectId = "";
    private String mUserId;

    private ArrayList<DeviceEntity> mDeviceList = new ArrayList<>();

    private ListView mListView;
    private RealtimeHistoryAdapter mAdapter;

    private RelativeLayout rlNoData;
    private ImageView ivNoData;
    private TextView tvNoData;

    @Override
    protected void initView() {
        Calendar c = Calendar.getInstance();
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvTilteTime = (TextView) findViewById(R.id.tvTilteTime);
        tvMod = (TextView) findViewById(R.id.tv_titlebar_right);
        rlRight = (RelativeLayout) findViewById(R.id.rl_my_download);
        tvTitle.setText("历史记录");
        tvMod.setText("日历");
        rlRight.setOnClickListener(this);
        teampDate = c.get(Calendar.YEAR)+"-"+(c.get(Calendar.MONTH) + 1)+"-"+(c.get(Calendar.DAY_OF_MONTH) - 1);
        tvTilteTime.setText("所选日期："+teampDate);

        rlNoData = (RelativeLayout) findViewById(R.id.rl_no_data);
        ivNoData = (ImageView) findViewById(R.id.iv_no_data);
        tvNoData = (TextView) findViewById(R.id.tv_no_data);

        View view = getLayoutInflater().inflate(R.layout.calendar_popwindow,
                null);
        llPopup = (LinearLayout) view.findViewById(R.id.ll_popup);
        tvDate = (TextView) view.findViewById(R.id.tvSelectedDate);
        mBtnDate = (Button) view.findViewById(R.id.btnSureDate);
        cancleViewBtn = view.findViewById(R.id.cancleViewBtn);
        mBtnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPopupCalendar.dismiss();
                llPopup.clearAnimation();
                tvTilteTime.setText("所选日期："+teampDate);
                mDeviceList.clear();
                mAdapter.notifyDataSetChanged();
                getDeviceSituation();
            }
        });
        cancleViewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPopupCalendar.dismiss();
                llPopup.clearAnimation();

            }
        });
        mPopupCalendar = new PopupWindow();
        mPopupCalendar.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        mPopupCalendar.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopupCalendar.setBackgroundDrawable(new BitmapDrawable());
        mPopupCalendar.setFocusable(true);
        mPopupCalendar.setOutsideTouchable(true);
        mPopupCalendar.setContentView(view);


        MonthlyFragment mf = (MonthlyFragment) getSupportFragmentManager().findFragmentById(R.id.monthly);
        mf.setOnMonthChangeListener(new MonthlyFragment.OnMonthChangeListener() {

            @Override
            public void onChange(int year, int month) {
                curYear = year;
                tvDate.setText(year + "-" + (month + 1) + "-" + "01");

            }

            @Override
            public void onDayClick(OneDayView dayView) {
                teampDate = curYear + "-" + (dayView.get(Calendar.MONTH) + 1) + "-" + dayView.get(Calendar.DAY_OF_MONTH);
                tvDate.setText(curYear + "-" + (dayView.get(Calendar.MONTH) + 1) + "-" + dayView.get(Calendar.DAY_OF_MONTH));
                dayView.setSelectedBg(dayView.get(Calendar.DAY_OF_MONTH), preOneDayView, dayView);
                preOneDayView = dayView;

            }
        });


        mListView = (ListView) findViewById(R.id.lv_content);
        mAdapter = new RealtimeHistoryAdapter();
        mUserId = SharedPreferencesManager.getString(ResultStatus.USER_ID);
        publicProjectId = SharedPreferencesManager.getString(mUserId + SysCode.PROJECTPERIODID);


        mAdapter.setData(mDeviceList);
        mListView.setAdapter(mAdapter);
        initNoDataView();
        getDeviceSituation();
    }

    private void initNoDataView() {
        if (ivNoData == null) return;
        rlNoData.setBackground(ContextCompat.getDrawable(this, R.color.white));
        ivNoData.setImageResource(R.drawable.pop_window_choose_no_content);
        tvNoData.setText("暂无该日设备记录");
    }

    private void getDeviceSituation(){
        HttpEntity entity = null;
        JSONObject pData = new JSONObject();
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("projectId", publicProjectId);
            paramObj.put("date", teampDate);
            pData.put("params", paramObj);
            entity = new StringEntity(pData.toString(), "UTF-8");
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        MainApi.requestCommon(this, AppConfig.RUNNING_DEVICE_SUMMARY, entity, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String httpResult = new String(bytes);
                TLog.log("realtime "+httpResult);
                try {
                    JSONObject obj = new JSONObject(httpResult);
                    if(StringUtils.equals(obj.getString("code"), SysCode.SUCCESS)){
                        JSONArray devList = obj.getJSONArray("data");
                        DeviceEntity tempDevEntity = null;
                        if(devList.length()  > 0){
                            if (rlNoData != null && rlNoData.getVisibility() == View.VISIBLE) {
                                rlNoData.setVisibility(View.GONE);
                            }
                            for(int j = 0; j < devList.length(); j ++){
                                JSONObject itemObj = devList.getJSONObject(j);
                                tempDevEntity = new DeviceEntity();
                                if(!itemObj.isNull("largeDeviceName")){
                                    tempDevEntity.setDevName(itemObj.getString("largeDeviceName"));
                                }
                                if(!itemObj.isNull("useStatus")){
                                    tempDevEntity.setDevState(itemObj.getString("useStatus"));
                                }else{
                                    tempDevEntity.setDevState("0");
                                }
                                if(!itemObj.isNull("totalTimes")){
                                    tempDevEntity.setTotalTimes(itemObj.getString("totalTimes"));
                                }else{
                                    tempDevEntity.setTotalTimes("0");
                                }
                                if(!itemObj.isNull("userTotalTimes")){
                                    tempDevEntity.setTodayUseTimes(itemObj.getString("userTotalTimes"));
                                }else{
                                    tempDevEntity.setTodayUseTimes("0");
                                }
                                if(!itemObj.isNull("deviceStatus")){
                                    tempDevEntity.setDevCurState(itemObj.getString("deviceStatus"));
                                }else{
                                    tempDevEntity.setDevCurState("0");
                                }
                                if(!itemObj.isNull("typeName")){
                                    tempDevEntity.setDevType(itemObj.getString("typeName"));
                                }
                                if(!itemObj.isNull("userList")){
                                    List<DeviceDriverEntity> mDevDriverList = new ArrayList<DeviceDriverEntity>();
                                    DeviceDriverEntity tempEntity = null;
                                    JSONObject tempDriverList = itemObj.getJSONObject("userList");
                                    Iterator iterator = tempDriverList.keys();
                                    while(iterator.hasNext()){
                                        String key = (String) iterator.next();
                                        int value = tempDriverList.getInt(key);
                                        tempEntity = new DeviceDriverEntity();
                                        tempEntity.setDrvName(key);
                                        tempEntity.setDrvSate(Integer.toString(value));
                                        mDevDriverList.add(tempEntity);
                                    }
                                    tempDevEntity.setMdevDrvList(mDevDriverList);
                                }

                                if(!itemObj.isNull("attendanceDeviceList")){
                                    List<RelateMachineEntity> mRelateList = new ArrayList<RelateMachineEntity>();
                                    RelateMachineEntity tempRelate = null;
                                    JSONArray tempList = itemObj.getJSONArray("attendanceDeviceList");
                                    for(int s =0; s < tempList.length(); s ++){
                                        tempRelate = new RelateMachineEntity();
                                        String curobj = tempList.getString(s);
                                        tempRelate.setRelateMacName(curobj);
                                        mRelateList.add(tempRelate);
                                    }
                                    tempDevEntity.setmRelateMachineList(mRelateList);
                                }
                                if(tempDevEntity.getDevState().equals("0")){
                                    tempDevEntity.setSequeNum(5);
                                }else if(tempDevEntity.getDevState().equals("1")){
                                    tempDevEntity.setSequeNum(4);
                                }else{

                                    int times = Integer.parseInt(tempDevEntity.getTodayUseTimes());
                                    if(times > 0){
                                        tempDevEntity.setSequeNum(2);
                                    }else{
                                        tempDevEntity.setSequeNum(3);
                                    }

                                }
                                mDeviceList.add(tempDevEntity);
                            }
                            Collections.sort(mDeviceList);
                            mAdapter.notifyDataSetChanged();
                        }else{
                            initNoData();
                        }

                    }else{
                        initNoData();
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

    private void initNoData() {
        if (rlNoData == null) return;
        if (rlNoData != null && rlNoData.getVisibility() != View.VISIBLE) {
            rlNoData.setVisibility(View.VISIBLE);
        }
        /*if (mLayout != null && mLayout.getVisibility() == View.VISIBLE) {
            mLayout.setVisibility(View.GONE);
        }*/
    }

    @Override
    public void setView() {
        parentView = getLayoutInflater().inflate(
                R.layout.activity_realtime_situation, null);
        setContentView(parentView);
    }

    @Override
    public void setListener() {

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.rl_my_download:
                llPopup.startAnimation(AnimationUtils.loadAnimation(
                        this,
                        R.anim.activity_translate_in));
                mPopupCalendar.showAtLocation(parentView, Gravity.BOTTOM, 0,
                        0);
                break;

        }
    }
}
