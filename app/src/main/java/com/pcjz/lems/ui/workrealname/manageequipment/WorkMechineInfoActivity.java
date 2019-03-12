package com.pcjz.lems.ui.workrealname.manageequipment;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.pcjz.lems.R;
import com.pcjz.lems.business.base.BaseActivity;
import com.pcjz.lems.business.base.basebean.Base;
import com.pcjz.lems.business.base.basebean.BaseData;
import com.pcjz.lems.business.base.basebean.BaseListData;
import com.pcjz.lems.business.common.utils.StringUtils;
import com.pcjz.lems.business.common.utils.TLog;
import com.pcjz.lems.business.common.view.SingleDialog;
import com.pcjz.lems.business.common.view.dialog.NoMsgDialog;
import com.pcjz.lems.business.common.view.dialog.OnMyNegativeListener;
import com.pcjz.lems.business.common.view.dialog.OnMyPositiveListener;
import com.pcjz.lems.business.config.AppConfig;
import com.pcjz.lems.business.constant.ResultStatus;
import com.pcjz.lems.business.constant.SysCode;
import com.pcjz.lems.business.context.AppContext;
import com.pcjz.lems.business.entity.SelectEntity;
import com.pcjz.lems.business.storage.SharedPreferencesManager;
import com.pcjz.lems.business.webapi.MainApi;
import com.pcjz.lems.ui.workrealname.manageequipment.bean.EquInfoBean;
import com.pcjz.lems.ui.workrealname.manageequipment.bean.WorkMechineBean;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * created by yezhengyu on 2017/9/15 09:37
 */

public class WorkMechineInfoActivity extends BaseActivity implements View.OnClickListener {

    private EditText mEtNumber;
    private EditText mEtName;
    private EditText mEtIp;
    private EditText mEtPortNumber;
    private LinearLayout mLlBind;
    private TextView mTvBind;
    private ImageView mIvBind;
    private LinearLayout mLlType;
    private TextView mTvType;
    private ImageView mIvtype;
    private LinearLayout mLlStatus;
    private TextView mTvStatus;
    private ImageView mIvStatus;
    private LinearLayout mLlSuppiler;
    private TextView mTvSuppiler;
    private ImageView mIvSuppiler;
    private EditText mEtPhone;
    private String mType;
    private TextView mTvRight;
    private TextView mTvLeft;

    private String mMode = "";
    private String mSelect = "";
    private SingleDialog mSingleDialog;
    private RelativeLayout mRlBack;
    private List<EquInfoBean> mAllEqus = new ArrayList<>();
    private SelectEntity mSelectEquInfo;
    private CommonMethond mMethod;
    private List<SelectEntity> mDeviceTypes;
    private SelectEntity mDeviceType;
    private List<SelectEntity> mDeviceStatus;
    private SelectEntity mSelectStaus;
    private List<SelectEntity> mSuppilers;
    private SelectEntity mSuppiler;
    private String mNumberText;
    private String mNameText;
    private String mIpText;
    private String mPortNumberText;
    private String mPhoneText;
    private String mDeviceId;

    private String mUserId;
    private String mProjectId;

    @Override
    public void setView() {
        setContentView(R.layout.activity_work_mechine);
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
        mRlBack = (RelativeLayout) findViewById(R.id.rl_back);
        mType = getIntent().getExtras().getString("type");
        mTvRight = (TextView) findViewById(R.id.tv_mechine_right);
        mTvLeft = (TextView) findViewById(R.id.tv_mechine_left);
        if (StringUtils.equals(mType, "detail")) {
            mDeviceId = getIntent().getExtras().getString("id");
            setTitle("考勤机信息");
            mTvLeft.setText("删除");
            mTvRight.setText("修改");
            mMode = "modify";
        } else {
            setTitle("新增考勤机");
            mTvLeft.setText("取消");
            mTvRight.setText("提交");
            mMode = "submit";
        }
        //all view
        mEtNumber = (EditText) findViewById(R.id.et_mechine_number);
        mEtName = (EditText) findViewById(R.id.et_mechine_name);
        mEtIp = (EditText) findViewById(R.id.et_ip_address);
        mEtPortNumber = (EditText) findViewById(R.id.et_port_number);
        mLlBind = (LinearLayout) findViewById(R.id.ll_equipment_bind);
        mTvBind = (TextView) findViewById(R.id.tv_equipment_bind);
        mIvBind = (ImageView) findViewById(R.id.iv_equipment_bind);
        mLlType = (LinearLayout) findViewById(R.id.ll_type);
        mTvType = (TextView) findViewById(R.id.tv_type);
        mIvtype = (ImageView) findViewById(R.id.iv_type);
        mLlStatus = (LinearLayout) findViewById(R.id.ll_status);
        mTvStatus = (TextView) findViewById(R.id.tv_status);
        mIvStatus = (ImageView) findViewById(R.id.iv_status);
        mLlSuppiler = (LinearLayout) findViewById(R.id.ll_suppiler);
        mTvSuppiler = (TextView) findViewById(R.id.tv_suppiler);
        mIvSuppiler = (ImageView) findViewById(R.id.iv_suppiler);
        mEtPhone = (EditText) findViewById(R.id.et_service_phone);
        mUserId = SharedPreferencesManager.getString(ResultStatus.USER_ID);
        mProjectId = SharedPreferencesManager.getString(mUserId + SysCode.PROJECTPERIODID);
        //初始化view
        initViewXml();
        mMethod = CommonMethond.getInstance();
        //初始化类型，状态，设备供应商等数据
        initDatas();
        mTvRight.setOnClickListener(this);
        mTvLeft.setOnClickListener(this);
        mLlBind.setOnClickListener(this);
        mLlType.setOnClickListener(this);
        mLlStatus.setOnClickListener(this);
        mLlSuppiler.setOnClickListener(this);
    }

    private void initDatas() {
        mDeviceTypes = mMethod.initDeviceTypes();
        mDeviceStatus = mMethod.initDeviceStatus("workMechine");
        mSuppilers = mMethod.initSuppilers();
    }

    private void initViewXml() {
        if (StringUtils.equals(mType, "detail")) {
            mEtNumber.setHint("");
            mEtNumber.setFocusable(false);
            mEtNumber.setFocusableInTouchMode(false);
            mEtNumber.setBackground(null);
            mEtName.setHint("");
            mEtName.setFocusable(false);
            mEtName.setFocusableInTouchMode(false);
            mEtName.setBackground(null);
            mEtIp.setHint("");
            mEtIp.setFocusable(false);
            mEtIp.setFocusableInTouchMode(false);
            mEtIp.setBackground(null);
            mEtPortNumber.setHint("");
            mEtPortNumber.setFocusable(false);
            mEtPortNumber.setFocusableInTouchMode(false);
            mEtPortNumber.setBackground(null);
            mEtPhone.setHint("");
            mEtPhone.setFocusable(false);
            mEtPhone.setFocusableInTouchMode(false);
            mEtPhone.setBackground(null);
            mLlBind.setEnabled(false);
            mTvBind.setText("");
            mIvBind.setVisibility(View.INVISIBLE);
            mLlType.setEnabled(false);
            mTvType.setText("");
            mIvtype.setVisibility(View.INVISIBLE);
            mLlSuppiler.setEnabled(false);
            mTvSuppiler.setText("");
            mIvSuppiler.setVisibility(View.INVISIBLE);
            mLlStatus.setEnabled(false);
            mTvStatus.setText("");
            mIvStatus.setVisibility(View.INVISIBLE);
        }
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
                try {
                    String httpResult = new String(bytes);
                    TLog.log("getAllEqu : " + httpResult);
                    Type type = new TypeToken<BaseListData<EquInfoBean>>() {
                    }.getType();
                    BaseListData<EquInfoBean> datas = new Gson().fromJson(httpResult, type);
                    if (StringUtils.equals(datas.getCode(), SysCode.SUCCESS)) {
                        if (datas.getData() != null) {
                            mAllEqus.clear();
                            mAllEqus.addAll(datas.getData());
                        }
                        if (StringUtils.equals(mType, "detail")) {
                            //获取考勤机信息
                            requestWorkMechineInfo();
                        } else {
                            hideLoading();
                        }
                        return;
                    } else {
                        hideLoading();
                        AppContext.showToast(datas.getMsg());
                        return;
                    }
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                }
                hideLoading();
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                AppContext.showToast(R.string.please_check_network);
                hideLoading();
            }
        });
    }

    private void requestWorkMechineInfo() {
        JSONObject obj = new JSONObject();
        HttpEntity entity = null;
        try {
            JSONObject obj0 = new JSONObject();
            obj.put("id", mDeviceId);
            obj0.put("params", obj);
            entity = new StringEntity(obj0.toString(), "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        MainApi.requestCommon(this, AppConfig.GET_WORK_MECHINE_INFO, entity, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                hideLoading();
                try {
                    String httpResult = new String(bytes);
                    TLog.log("getWorkMechineList : " + httpResult);
                    Type type = new TypeToken<BaseData<WorkMechineBean>>() {
                    }.getType();
                    BaseData<WorkMechineBean> datas = new Gson().fromJson(httpResult, type);
                    if (StringUtils.equals(datas.getCode(), ResultStatus.SUCCESS)) {
                        WorkMechineBean workMechineInfo = datas.getData();
                        if (workMechineInfo != null) {
                            refreshPageData(workMechineInfo);
                        }
                    } else {
                        AppContext.showToast(datas.getMsg());
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                hideLoading();
            }
        });
    }

    private void refreshPageData(WorkMechineBean info) {
        if (!StringUtils.isEmpty(info.getDeviceCode())) {
            mEtNumber.setText(info.getDeviceCode());
        }
        if (!StringUtils.isEmpty(info.getDeviceName())) {
            mEtName.setText(info.getDeviceName());
        }
        if (!StringUtils.isEmpty(info.getDeviceIp())) {
            mEtIp.setText(info.getDeviceIp());
        }
        if (!StringUtils.isEmpty(info.getDevicePort())) {
            mEtPortNumber.setText(info.getDevicePort());
        }
        if (!StringUtils.isEmpty(info.getServicesPhone())) {
            mEtPhone.setText(info.getServicesPhone());
        }
        if (!StringUtils.isEmpty(info.getLargeDeviceName())) {
            for (int i = 0; i < mAllEqus.size(); i++) {
                String id = mAllEqus.get(i).getId();
                if (StringUtils.equals(id, info.getLargeDeviceId())) {
                    SelectEntity entity = new SelectEntity();
                    entity.setId(id);
                    entity.setName(mAllEqus.get(i).getDeviceName());
                    entity.setSelect(false);
                    mSelectEquInfo = entity;
                    mTvBind.setText(info.getLargeDeviceName());
                    break;
                }
            }
        } else {
            mTvBind.setText("无");
        }
        if (!StringUtils.isEmpty(info.getDeviceType())) {
            for (int i = 0; i < mDeviceTypes.size(); i++) {
                String id = mDeviceTypes.get(i).getId();
                if (StringUtils.equals(id, info.getDeviceType())) {
                    mDeviceType = mDeviceTypes.get(i);
                    mTvType.setText(mDeviceType.getName());
                    break;
                }
            }
        }
        if (!StringUtils.isEmpty(info.getDeviceStatus())) {
            for (int i = 0; i < mDeviceStatus.size(); i++) {
                String id = mDeviceStatus.get(i).getId();
                if (StringUtils.equals(id, info.getDeviceStatus())) {
                    mSelectStaus = mDeviceStatus.get(i);
                    mTvStatus.setText(mSelectStaus.getName());
                    break;
                }
            }
        }
        if (!StringUtils.isEmpty(info.getDeviceSupplier())) {
            for (int i = 0; i < mSuppilers.size(); i++) {
                String id = mSuppilers.get(i).getId();
                if (StringUtils.equals(id, info.getDeviceSupplier())) {
                    mSuppiler = mSuppilers.get(i);
                    mTvSuppiler.setText(mSuppiler.getName());
                    break;
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.tv_mechine_right:
                if (StringUtils.equals(mMode, "modify")) {
                    reFreshView();
                } else {
                    //提交
                    submitAddMechine();
                }
                break;
            case R.id.tv_mechine_left:
                if (StringUtils.equals(mMode, "modify")) {
                    showDeleteDialog();
                } else {
                    finish();
                }
                break;
            case R.id.ll_equipment_bind:
                mSelect = "bind";
                mSingleDialog = new SingleDialog(this, mSelectEquInfo, new SingleDialog.DataBackListener() {
                    @Override
                    public void getData(SelectEntity entity) {
                        equInfoFinish(entity);
                    }
                });
                mSingleDialog.setSelectTitle(getResources().getString(R.string.select_bind_equipment));
                if (mAllEqus != null && mAllEqus.size() != 0) {
                    mSingleDialog.setInitSelecList(getInitSelecList());
                } else {
                    mSingleDialog.setInitSelecList(null);
                }
                mSingleDialog.show();
                break;
            case R.id.ll_type:
                mSelect = "type";
                mSingleDialog = new SingleDialog(this, mDeviceType, new SingleDialog.DataBackListener() {
                    @Override
                    public void getData(SelectEntity entity) {
                        typeFinish(entity);
                    }
                });
                mSingleDialog.setSelectTitle(getResources().getString(R.string.select_type2));
                if (mDeviceTypes != null && mDeviceTypes.size() != 0) {
                    mSingleDialog.setInitSelecList(getInitSelecList());
                } else {
                    mSingleDialog.setInitSelecList(null);
                }
                mSingleDialog.show();
                break;
            case R.id.ll_status:
                mSelect = "status";
                mSingleDialog = new SingleDialog(this, mSelectStaus, new SingleDialog.DataBackListener() {
                    @Override
                    public void getData(SelectEntity entity) {
                        statusFinish(entity);
                    }
                });
                mSingleDialog.setSelectTitle(getResources().getString(R.string.select_status));
                if (mDeviceStatus != null && mDeviceStatus.size() != 0) {
                    mSingleDialog.setInitSelecList(getInitSelecList());
                } else {
                    mSingleDialog.setInitSelecList(null);
                }
                mSingleDialog.show();
                break;
            case R.id.ll_suppiler:
                mSelect = "suppiler";
                mSingleDialog = new SingleDialog(this, mSuppiler, new SingleDialog.DataBackListener() {
                    @Override
                    public void getData(SelectEntity entity) {
                        suppilerFinish(entity);
                    }
                });
                mSingleDialog.setSelectTitle(getResources().getString(R.string.select_supplier));
                if (mSuppilers != null && mSuppilers.size() != 0) {
                    mSingleDialog.setInitSelecList(getInitSelecList());
                } else {
                    mSingleDialog.setInitSelecList(null);
                }
                mSingleDialog.show();
                break;
        }
    }

    private void showDeleteDialog() {
        NoMsgDialog noMsgDialog = new NoMsgDialog(this,"确定是否删除？","","确定","取消",
                new OnMyPositiveListener(){
                    @Override
                    public void onClick(){
                        if (StringUtils.equals(mType, "detail")) {
                            requestDeleteDevice();
                        }
                    }
                },new OnMyNegativeListener(){
            @Override
            public void onClick() {

            }
        });
        noMsgDialog.show();
    }

    private void requestDeleteDevice() {
        JSONObject obj = new JSONObject();
        HttpEntity entity = null;
        try {
            JSONObject obj0 = new JSONObject();
            obj.put("id", mDeviceId);
            obj0.put("params", obj);
            entity = new StringEntity(obj0.toString(), "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        MainApi.requestCommon(this, AppConfig.DELETE_WORK_MECHINE_INFO, entity, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                try {
                    String httpResult = new String(bytes);
                    TLog.log("submitDeleteDevice : " + httpResult);
                    Base base = new Gson().fromJson(httpResult, Base.class);
                    if (StringUtils.equals(base.getCode(), SysCode.SUCCESS)) {
                        sendCurrentItemBroadcast(0);
                        AppContext.showToast("删除成功");
                        finish();
                        return;
                    } else {
                        AppContext.showToast(base.getMsg());
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

    private void submitAddMechine() {
        if (!mustEntering()) return;
        initLoading("");
        JSONObject obj = new JSONObject();
        HttpEntity entity = null;
        try {

            JSONObject obj0 = new JSONObject();
            //修改需要加考勤机id
            if (StringUtils.equals(mType, "detail")) {
                obj.put("id", mDeviceId);
            } else {
                obj.put("projectId", mProjectId);
            }
            obj.put("deviceCode", mNumberText);
            obj.put("deviceName", mNameText);
            obj.put("deviceIp", mIpText);
            obj.put("devicePort", mPortNumberText);
            obj.put("deviceType", mDeviceType.getId());
            obj.put("deviceStatus", mSelectStaus.getId());
            //品牌和供应商
            obj.put("deviceBrand", mSuppiler.getId());
            obj.put("deviceSupplier", mSuppiler.getId());
            if (!StringUtils.isEmpty(mSelectEquInfo.getId())) {
                obj.put("largeDeviceId", mSelectEquInfo.getId());
            }
            obj.put("servicesPhone", mPhoneText);
            obj0.put("params", obj);
            entity = new StringEntity(obj0.toString(), "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        String appConfig;
        if (StringUtils.equals(mType, "add")) {
            appConfig = AppConfig.ADD_WORK_MECHINE;
        } else {
            appConfig = AppConfig.MODIFY_WORK_MECHINE_INFO;
        }
        MainApi.requestCommon(this, appConfig, entity, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                hideLoading();
                try {
                    String httpResult = new String(bytes);
                    TLog.log("submitAddMechine : " + httpResult);
                    Base base = new Gson().fromJson(httpResult, Base.class);
                    if (StringUtils.equals(base.getCode(), SysCode.SUCCESS)) {
                        sendCurrentItemBroadcast(0);
                        if (StringUtils.equals(mType, "add")) {
                            AppContext.showToast("提交成功");
                        } else {
                            AppContext.showToast("修改成功");
                        }
                        finish();
                        return;
                    } else {
                        AppContext.showToast(base.getMsg());
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

    private boolean mustEntering() {
        mNumberText = mEtNumber.getText().toString();
        mNameText = mEtName.getText().toString();
        mIpText = mEtIp.getText().toString();
        mPortNumberText = mEtPortNumber.getText().toString();
        mPhoneText = mEtPhone.getText().toString();
        if (StringUtils.isEmpty(mNumberText)) {
            AppContext.showToast(R.string.please_entering_device_number);
            return false;
        }
        if (StringUtils.isEmpty(mNameText)) {
            AppContext.showToast(R.string.please_entering_device_name);
            return false;
        }
        if (StringUtils.isEmpty(mIpText)) {
            AppContext.showToast(R.string.please_entering_device_ip);
            return false;
        }
        if (!isIP(mIpText)) {
            AppContext.showToast(R.string.please_entering_correct_ip);
            return false;
        }
        if (StringUtils.isEmpty(mPortNumberText)) {
            AppContext.showToast(R.string.please_entering_device_port);
            return false;
        }
        if (Long.parseLong(mPortNumberText) > 65535) {
            AppContext.showToast(R.string.please_entering_correct_port);
            return false;
        }
        if (mSelectEquInfo == null) {
            AppContext.showToast(R.string.please_select_bind_equipment);
            return false;
        }
        if (mDeviceType == null) {
            AppContext.showToast(R.string.please_select_type);
            return false;
        }
        if (mSelectStaus == null) {
            AppContext.showToast(R.string.please_select_status);
            return false;
        }
        if (mSuppiler == null) {
            AppContext.showToast(R.string.please_select_supplier);
            return false;
        }
        if (StringUtils.isEmpty(mPhoneText)) {
            AppContext.showToast(R.string.please_entering_phone);
            return false;
        }
        return true;
    }

    private void suppilerFinish(SelectEntity entity) {
        mSuppiler = entity;
        mTvSuppiler.setText(entity.getName());
    }

    private void statusFinish(SelectEntity entity) {
        mSelectStaus = entity;
        mTvStatus.setText(entity.getName());
    }

    private void typeFinish(SelectEntity entity) {
        mDeviceType = entity;
        mTvType.setText(entity.getName());
    }

    private void equInfoFinish(SelectEntity entity) {
        mSelectEquInfo = entity;
        mTvBind.setText(entity.getName());
    }

    private List<SelectEntity> getInitSelecList() {
        List<SelectEntity> selectInfoList = new ArrayList<>();
        if (StringUtils.equals(mSelect, "bind")) {
            SelectEntity selectInfo;
            for (int i = 0; i < mAllEqus.size(); i++) {
                EquInfoBean equInfoBean = mAllEqus.get(i);
                selectInfo = new SelectEntity();
                selectInfo.setId(equInfoBean.getId());
                selectInfo.setName(equInfoBean.getDeviceName());
                selectInfo.setSelect(false);
                selectInfoList.add(selectInfo);
            }
            SelectEntity selectInfo0 = new SelectEntity();
            selectInfo0.setName("无");
            selectInfo0.setSelect(false);
            selectInfoList.add(0, selectInfo0);
        } else if (StringUtils.equals(mSelect, "type")) {
            selectInfoList.addAll(mDeviceTypes);
        } else if (StringUtils.equals(mSelect, "status")) {
            selectInfoList.addAll(mDeviceStatus);
        } else if (StringUtils.equals(mSelect, "suppiler")) {
            selectInfoList.addAll(mSuppilers);
        }
        return selectInfoList;
    }

    private void reFreshView() {
        mEtName.setFocusable(true);
        mEtName.setFocusableInTouchMode(true);
        mEtName.requestFocus();
        mEtName.setSelection(mEtName.getText().length());

        mEtIp.setFocusable(true);
        mEtIp.setFocusableInTouchMode(true);
        mEtIp.requestFocus();
        mEtIp.setSelection(mEtIp.getText().length());

        mEtPortNumber.setFocusable(true);
        mEtPortNumber.setFocusableInTouchMode(true);
        mEtPortNumber.requestFocus();
        mEtPortNumber.setSelection(mEtPortNumber.getText().length());

        mEtPhone.setFocusable(true);
        mEtPhone.setFocusableInTouchMode(true);
        mEtPhone.requestFocus();
        mEtPhone.setSelection(mEtPhone.getText().length());

        mEtNumber.setFocusable(true);
        mEtNumber.setFocusableInTouchMode(true);
        mEtNumber.requestFocus();
        mEtNumber.setSelection(mEtNumber.getText().length());

        mLlBind.setEnabled(true);
        mIvBind.setVisibility(View.VISIBLE);

        mLlType.setEnabled(true);
        mIvtype.setVisibility(View.VISIBLE);

        mLlStatus.setEnabled(true);
        mIvStatus.setVisibility(View.VISIBLE);

        mLlSuppiler.setEnabled(true);
        mIvSuppiler.setVisibility(View.VISIBLE);

        mMode = "submit";
        mTvLeft.setText("取消");
        mTvRight.setText("提交");
    }

    @Override
    protected boolean hasLoading() {
        return true;
    }

    private void sendCurrentItemBroadcast(int currentIndex) {
        Intent intent = new Intent();
        intent.setAction(SysCode.JUMP_CURRENTINDEX);
        intent.putExtra("currentIndex", currentIndex);
        sendBroadcast(intent);
        finish();
    }

    private void closeKeyboard() {
        View view = getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public boolean isIP(String addr)
    {
        if(addr.length() < 7 || addr.length() > 15 || "".equals(addr))
        {
            return false;
        }
        /**
         * 判断IP格式和范围
         */
        String rexp = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";

        Pattern pat = Pattern.compile(rexp);

        Matcher mat = pat.matcher(addr);

        boolean ipAddress = mat.find();

        return ipAddress;
    }
}
