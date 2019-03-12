package com.pcjz.lems.ui.workrealname.projectsituation;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
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
import com.pcjz.lems.ui.workrealname.projectsituation.adapter.RealtimeSituationAdapter;
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

public class RealtimeSituationActivity extends BaseActivity implements View.OnClickListener{
    private TextView tvTitle, tvMod, tvTilteTime;
    private RelativeLayout rlRight;
    private String curDate = "";

    private String publicProjectId = "";
    private String mUserId;

    private ArrayList<DeviceEntity> mDeviceList = new ArrayList<>();

    private ListView mListView;
    private RealtimeSituationAdapter mAdapter;

    private RelativeLayout rlNoData;
    private ImageView ivNoData;
    private TextView tvNoData;

    private ArrayList<DeviceEntity> mTempDeviceList = new ArrayList<>();


    @Override
    protected void initView() {
        Calendar c = Calendar.getInstance();

        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvMod = (TextView) findViewById(R.id.tv_titlebar_right);
        rlRight = (RelativeLayout) findViewById(R.id.rl_my_download);
        tvTilteTime = (TextView) findViewById(R.id.tvTilteTime);
        tvTitle.setText("今日设备实时概况");
        tvMod.setText("历史记录");
        rlRight.setOnClickListener(this);

        rlNoData = (RelativeLayout) findViewById(R.id.rl_no_data);
        ivNoData = (ImageView) findViewById(R.id.iv_no_data);
        tvNoData = (TextView) findViewById(R.id.tv_no_data);

        mListView = (ListView) findViewById(R.id.lv_content);
        mAdapter = new RealtimeSituationAdapter();
        tvTilteTime.setText("所选日期："+c.get(Calendar.YEAR)+"-"+(c.get(Calendar.MONTH) + 1)+"-"+c.get(Calendar.DAY_OF_MONTH));
        curDate = c.get(Calendar.YEAR)+"-"+(c.get(Calendar.MONTH) + 1)+"-"+c.get(Calendar.DAY_OF_MONTH);
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
        tvNoData.setText("暂无今日设备记录");
    }

    private void getDeviceSituation(){
        HttpEntity entity = null;
        JSONObject pData = new JSONObject();
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("projectId", publicProjectId);
            paramObj.put("date", curDate);
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
                TLog.log("devList -->"+httpResult);
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
                                    if(tempDevEntity.getDevCurState().equals("1")){
                                        tempDevEntity.setSequeNum(1);
                                    }else{
                                        int times = Integer.parseInt(tempDevEntity.getTodayUseTimes());
                                        if(times > 0){
                                            tempDevEntity.setSequeNum(2);
                                        }else{
                                            tempDevEntity.setSequeNum(3);
                                        }
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
        setContentView(R.layout.activity_realtime_situation);
    }

    @Override
    public void setListener() {

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.rl_my_download:
                Intent intent = new Intent(RealtimeSituationActivity.this, RealtimeSituationHistory.class);
                startActivity(intent);
                break;

        }
    }
}
