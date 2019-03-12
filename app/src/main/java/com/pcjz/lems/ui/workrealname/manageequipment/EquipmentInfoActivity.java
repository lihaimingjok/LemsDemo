package com.pcjz.lems.ui.workrealname.manageequipment;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
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
import com.pcjz.lems.business.entity.PersonInfoEntity;
import com.pcjz.lems.business.entity.SelectEntity;
import com.pcjz.lems.business.storage.SharedPreferencesManager;
import com.pcjz.lems.business.webapi.MainApi;
import com.pcjz.lems.ui.workrealname.manageequipment.adapter.PersonListAdapter;
import com.pcjz.lems.ui.workrealname.manageequipment.bean.EquInfoBean;
import com.pcjz.lems.ui.workrealname.manageequipment.bean.EquInfoRequestBean;
import com.pcjz.lems.ui.workrealname.manageequipment.bean.EquInfoRequestParam;
import com.pcjz.lems.ui.workrealname.manageequipment.bean.EquTypeBean;
import com.pcjz.lems.ui.workrealname.personinfo.PersonInfoDetailActivity;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
import org.json.JSONObject;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

/**
 * created by yezhengyu on 2017/9/18 10:23
 */

public class EquipmentInfoActivity extends BaseActivity implements View.OnClickListener {

    private static final int RESULT_RECODE = 0;
    private RecyclerView mRecyclerView;
    List<PersonInfoEntity> mPersonDatas = new ArrayList<>();
    private PersonListAdapter mAdapter;
    private TextView mTvRight;
    private String mType;
    private TextView mTvLeft;
    private EditText mEtNumber;
    private EditText mEtName;
    private EditText mEtOrder;
    private EditText mEtPhone;
    private EditText mEtRemark;
    private LinearLayout mLlType;
    private TextView mTvType;
    private ImageView mIvType;
    private LinearLayout mLlStatus;
    private TextView mTvStatus;
    private ImageView mIvStatus;

    private String mMode = "";
    private RelativeLayout mRlBack;
    private SingleDialog mSingleDialog;
    private SelectEntity mSelectType;
    private ArrayList<EquTypeBean> mEquTypes;
    private CommonMethond mMethod;
    private String mSelect = "";
    private List<SelectEntity> mEquStatuss;
    private SelectEntity mSelectStatus;
    private TextView mTvOperatePersons;
    private String mDeviceId;

    private String mUserId;
    private String mProjectId;
    private List<PersonInfoEntity> mBindPersons;

    @Override
    public void setView() {
        setContentView(R.layout.activity_equipment_info);
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
        mTvRight = (TextView) findViewById(R.id.tv_equipment_right);
        mTvLeft = (TextView) findViewById(R.id.tv_equipment_left);
        LinearLayout llTop = (LinearLayout) findViewById(R.id.ll_equipment_top);
        TextView tvTop = (TextView) findViewById(R.id.tv_equipment_top);
        RelativeLayout rlBottom = (RelativeLayout) findViewById(R.id.rl_operate);
        LinearLayout llStatus = (LinearLayout) findViewById(R.id.ll_status);
        View viewStatus = findViewById(R.id.view_status);
        LinearLayout llPhone = (LinearLayout) findViewById(R.id.ll_phone);
        if (StringUtils.equals(mType, "add")) {
            setTitle(AppContext.mResource.getString(R.string.add_equipment_info));
            mTvLeft.setText("取消");
            mTvRight.setText("提交");
            mMode = "submit";
            tvTop.setVisibility(View.GONE);
            rlBottom.setVisibility(View.GONE);
            llStatus.setVisibility(View.GONE);
            viewStatus.setVisibility(View.GONE);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp2px(50));
            layoutParams.setMargins(0, dp2px(20), 0, 0);//4个参数按顺序分别是左上右下
            llTop.setLayoutParams(layoutParams);
            LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp2px(50));
            layoutParams2.setMargins(0, dp2px(10), 0, 0);//4个参数按顺序分别是左上右下
            llPhone.setLayoutParams(layoutParams2);
        } else {
            mDeviceId = getIntent().getExtras().getString("id");
            setTitle(AppContext.mResource.getString(R.string.equipment_info));
            mTvLeft.setText("删除");
            mTvRight.setText("修改");
            mMode = "modify";
        }
        //all view
        mEtNumber = (EditText) findViewById(R.id.et_equipment_number);
        mEtName = (EditText) findViewById(R.id.et_equipment_name);
        mEtOrder = (EditText) findViewById(R.id.et_order_number);
        mEtPhone = (EditText) findViewById(R.id.et_service_phone);
        mEtRemark = (EditText) findViewById(R.id.et_remark);
        mLlType = (LinearLayout) findViewById(R.id.ll_equipment_type);
        mTvType = (TextView) findViewById(R.id.tv_equipment_type);
        mIvType = (ImageView) findViewById(R.id.iv_equipment_type);
        mLlStatus = (LinearLayout) findViewById(R.id.ll_equipment_status);
        mTvStatus = (TextView) findViewById(R.id.tv_equipment_status);
        mIvStatus = (ImageView) findViewById(R.id.iv_equipment_status);
        mTvOperatePersons = (TextView) findViewById(R.id.tv_operate_persons);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_horizontal);
        mUserId = SharedPreferencesManager.getString(ResultStatus.USER_ID);
        mProjectId = SharedPreferencesManager.getString(mUserId + SysCode.PROJECTPERIODID);
        //初始化view
        initViewXml();
        mMethod = CommonMethond.getInstance();
        mEquStatuss = mMethod.initDeviceStatus("equip");
        mTvRight.setOnClickListener(this);
        mTvLeft.setOnClickListener(this);
        mLlType.setOnClickListener(this);
        mLlStatus.setOnClickListener(this);

        GridLayoutManager layoutManager = new GridLayoutManager(this, 6);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new PersonListAdapter(this, mPersonDatas);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setFocusable(false);
        mAdapter.setOnItemClickListener(new PersonListAdapter.OnClickListenerI() {
            @Override
            public void setItemClick(View holder, int position) {
                Intent intent = new Intent(EquipmentInfoActivity.this, PersonInfoDetailActivity.class);
                intent.putExtra("pstate", "8003");
                intent.putExtra("pid", mPersonDatas.get(position).getProjectPersonId());
                startActivity(intent);
            }

            @Override
            public void setDeleteClick(int position) {
                deletePerson(position);
            }
        });
    }


    private void deletePerson(final int position) {
        mPersonDatas.remove(position);
        mTvOperatePersons.setText(this.getResources().getString(R.string.operate_persons, mPersonDatas.size() - 1 + ""));
        mAdapter.notifyDataSetChanged();
    }

    private void initViewXml() {
        if (StringUtils.equals(mType, "detail")) {
            mEtNumber.setFocusable(false);
            mEtNumber.setFocusableInTouchMode(false);
            mEtNumber.setHint("");
            mEtName.setFocusable(false);
            mEtName.setFocusableInTouchMode(false);
            mEtName.setHint("");
            mEtOrder.setFocusable(false);
            mEtOrder.setFocusableInTouchMode(false);
            mEtOrder.setHint("");
            mEtPhone.setFocusable(false);
            mEtPhone.setFocusableInTouchMode(false);
            mEtPhone.setHint("");
            mEtRemark.setFocusable(false);
            mEtRemark.setFocusableInTouchMode(false);
            mEtRemark.setHint("");
            mLlType.setEnabled(false);
            mTvType.setText("");
            mIvType.setVisibility(View.INVISIBLE);
            mLlStatus.setEnabled(false);
            mTvStatus.setText("");
            mIvStatus.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void initWebData() {
        initLoading("");
        //获取大型设备类型
        requestEquType();
    }

    private void requestEquType() {
        HttpEntity entity = null;
        try {
            JSONObject obj0 = new JSONObject();
            entity = new StringEntity(obj0.toString(), "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        MainApi.requestCommon(this, AppConfig.GET_EQUIPMENT_TYPE, entity, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                try {
                    String httpResult = new String(bytes);
                    TLog.log("getEquType : " + httpResult);
                    Type type = new TypeToken<BaseListData<EquTypeBean>>() {
                    }.getType();
                    BaseListData<EquTypeBean> datas = new Gson().fromJson(httpResult, type);
                    if (StringUtils.equals(datas.getCode(), SysCode.SUCCESS)) {
                        mEquTypes = datas.getData();
                        if (StringUtils.equals(mType, "detail")) {
                            //获取大型设备信息
                            requestEquIfo();
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
                mStatus = "no_network";
                hideLoading();
            }
        });
    }

    private void requestEquIfo() {
        JSONObject obj = new JSONObject();
        HttpEntity entity = null;
        try {
            JSONObject obj0 = new JSONObject();
            obj.put("id", mDeviceId);
            obj.put("projectId", mProjectId);
            obj0.put("params", obj);
            entity = new StringEntity(obj0.toString(), "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        MainApi.requestCommon(this, AppConfig.GET_EQUIPMENT_INFO, entity, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                hideLoading();
                try {
                    String httpResult = new String(bytes);
                    TLog.log("getEquIfo : " + httpResult);
                    Type type = new TypeToken<BaseData<EquInfoBean>>() {
                    }.getType();
                    BaseData<EquInfoBean> datas = new Gson().fromJson(httpResult, type);
                    if (StringUtils.equals(datas.getCode(), ResultStatus.SUCCESS)) {
                        EquInfoBean info = datas.getData();
                        if (info != null) {
                            refreshPageData(info);
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
                mStatus = "no_network";
            }
        });
    }

    //跳转联系人列表状态
    private String mStatus = "";

    private void refreshPageData(final EquInfoBean info) {
        int bindDeviceSize = info.getBindAttendanceDeviceSize();
        if (bindDeviceSize != 0) {
            mStatus = "bind";
        } else {
            mStatus = "no_bind";
        }
        if (!StringUtils.isEmpty(info.getDeviceCode())) {
            mEtNumber.setText(info.getDeviceCode());
        }
        if (!StringUtils.isEmpty(info.getDeviceName())) {
            mEtName.setText(info.getDeviceName());
        }

        Observable.just(info.getDeviceTypeName()).filter(new Predicate<String>() {
            @Override
            public boolean test(String s) throws Exception {
                return !StringUtils.isEmpty(s);
            }
        }).fromIterable(mEquTypes).filter(new Predicate<EquTypeBean>() {
            @Override
            public boolean test(EquTypeBean equTypeBean) throws Exception {
                return StringUtils.equals(equTypeBean.getId(), info.getDeviceTypeId());
            }
        }).subscribe(new Consumer<EquTypeBean>() {
            @Override
            public void accept(EquTypeBean equTypeBean) throws Exception {
                SelectEntity entity = new SelectEntity();
                entity.setId(equTypeBean.getId());
                entity.setName(equTypeBean.getTypeName());
                entity.setSelect(false);
                mSelectType = entity;
                mTvType.setText(info.getDeviceTypeName());
            }
        });

        /*if (!StringUtils.isEmpty(info.getDeviceTypeName())) {
            for (int i = 0; i < mEquTypes.size(); i++) {
                String id = mEquTypes.get(i).getId();
                if (StringUtils.equals(id, info.getDeviceTypeId())) {
                    SelectEntity entity = new SelectEntity();
                    entity.setId(id);
                    entity.setName(mEquTypes.get(i).getTypeName());
                    entity.setSelect(false);
                    mSelectType = entity;
                    mTvType.setText(info.getDeviceTypeName());
                    break;
                }
            }
        }*/

        if (!StringUtils.isEmpty(info.getDeviceSerial())) {
            mEtOrder.setText(info.getDeviceSerial());
        }

        //遍历过滤集合

        Observable.just(info).filter(new Predicate<EquInfoBean>() {
            @Override
            public boolean test(EquInfoBean equInfoBean) throws Exception {
                return !StringUtils.isEmpty(equInfoBean.getUseStatus());
            }
        }).fromIterable(mEquStatuss).filter(new Predicate<SelectEntity>() {
            @Override
            public boolean test(SelectEntity selectEntity) throws Exception {
                return StringUtils.equals(selectEntity.getId(), info.getUseStatus());
            }
        }).subscribe(new Consumer<SelectEntity>() {
            @Override
            public void accept(SelectEntity selectEntity) throws Exception {
                mSelectStatus = selectEntity;
                mTvStatus.setText(mSelectStatus.getName());
            }
        });

        /*if (!StringUtils.isEmpty(info.getUseStatus())) {
            for (int i = 0; i < mEquStatuss.size(); i++) {
                String id = mEquStatuss.get(i).getId();
                if (StringUtils.equals(id, info.getUseStatus())) {
                    mSelectStatus = mEquStatuss.get(i);
                    mTvStatus.setText(mSelectStatus.getName());
                    break;
                }
            }
        }*/

        if (!StringUtils.isEmpty(info.getServicesPhone())) {
            mEtPhone.setText(info.getServicesPhone());
        }

        if (!StringUtils.isEmpty(info.getRemark())) {
            mEtRemark.setText(info.getRemark());
        }
        mTvOperatePersons.setText(this.getResources().getString(R.string.operate_persons, info.getBindPersons().size() + ""));
        mBindPersons = info.getBindPersons();
        for (int i = 0; i < mBindPersons.size(); i++) {
            mBindPersons.get(i).setDeleteStatus("0");
        }
        mPersonDatas.addAll(mBindPersons);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.tv_equipment_right:
                if (StringUtils.equals(mMode, "modify")) {
                    reFreshView();
                } else {
                    //提交大型设备数据
                    submitAddLargeEqu();
                }
                break;
            case R.id.tv_equipment_left:
                if (StringUtils.equals(mMode, "modify")) {
                    //删除大型设备
                    showDeleteDialog();
                } else {
                    finish();
                }
                break;
            //获取大型设备类型
            case R.id.ll_equipment_type:
                mSelect = "type";
                mSingleDialog = new SingleDialog(this, mSelectType, new SingleDialog.DataBackListener() {
                    @Override
                    public void getData(SelectEntity entity) {
                        equTypeFinish(entity);
                    }
                });
                mSingleDialog.setSelectTitle(getResources().getString(R.string.select_device_type));
                if (mEquTypes != null && mEquTypes.size() != 0) {
                    mSingleDialog.setInitSelecList(getInitSelecList());
                } else {
                    mSingleDialog.setInitSelecList(null);
                }
                mSingleDialog.show();
                break;
            case R.id.ll_equipment_status:
                mSelect = "status";
                mSingleDialog = new SingleDialog(this, mSelectStatus, new SingleDialog.DataBackListener() {
                    @Override
                    public void getData(SelectEntity entity) {
                        equStatusFinish(entity);
                    }
                });
                mSingleDialog.setSelectTitle(getResources().getString(R.string.please_select_status));
                if (mEquStatuss != null && mEquStatuss.size() != 0) {
                    mSingleDialog.setInitSelecList(getInitSelecList());
                } else {
                    mSingleDialog.setInitSelecList(null);
                }
                mSingleDialog.show();
                break;
        }
    }

    private void showDeleteDialog() {
        NoMsgDialog noMsgDialog = new NoMsgDialog(this, "确定是否删除？", "", "确定", "取消",
                new OnMyPositiveListener() {
                    @Override
                    public void onClick() {
                        if (StringUtils.equals(mType, "detail")) {
                            requestDeleteEquInfo();
                        }
                    }
                }, new OnMyNegativeListener() {
            @Override
            public void onClick() {

            }
        });
        noMsgDialog.show();
    }

    private void requestDeleteEquInfo() {
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
        MainApi.requestCommon(this, AppConfig.DELETE_EQUIPMENT_INFO, entity, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                try {
                    String httpResult = new String(bytes);
                    TLog.log("submitDeleteEquInfo : " + httpResult);
                    Base base = new Gson().fromJson(httpResult, Base.class);
                    if (StringUtils.equals(base.getCode(), SysCode.SUCCESS)) {
                        sendCurrentItemBroadcast(1);
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

    private void equStatusFinish(SelectEntity entity) {
        mSelectStatus = entity;
        mTvStatus.setText(entity.getName());
    }

    private void submitAddLargeEqu() {
        if (!mustEntering()) return;
        initLoading("");
        EquInfoRequestBean requestBean = new EquInfoRequestBean();
        if (StringUtils.equals(mType, "add")) {
            requestBean.setProjectId(mProjectId);
        } else {
            filterDeletePersons();
            List<String> newBinds = new ArrayList<>();
            List<String> unBinds = new ArrayList<>();
            for (int i = 0; i < newBindPersons.size(); i++) {
                newBinds.add(newBindPersons.get(i).getPersonId());
            }
            for (int i = 0; i < unBindPersons.size(); i++) {
                unBinds.add(unBindPersons.get(i).getPersonId());
            }
            String[] arrNewBind = newBinds.toArray(new String[newBinds.size()]);
            String[] arrUnBind = unBinds.toArray(new String[unBinds.size()]);
            requestBean.setId(mDeviceId);
            //requestBean.setDeviceStatus(mSelectStatus.getId());
            requestBean.setUseStatus(mSelectStatus.getId());
            requestBean.setUnbindPersons(arrUnBind);
            requestBean.setNewbindPersons(arrNewBind);
        }
        requestBean.setDeviceCode(mEtNumber.getText().toString());
        requestBean.setDeviceName(mEtName.getText().toString());
        requestBean.setDeviceSerial(mEtOrder.getText().toString());
        requestBean.setDeviceTypeId(mSelectType.getId());
        requestBean.setServicesPhone(mEtPhone.getText().toString());
        requestBean.setRemark(mEtRemark.getText().toString());
        EquInfoRequestParam param = new EquInfoRequestParam();
        param.setParams(requestBean);
        HttpEntity entity = null;
        try {
            String params = new Gson().toJson(param);
            entity = new StringEntity(params, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        String appConfig;
        if (StringUtils.equals(mType, "add")) {
            appConfig = AppConfig.SUBMIT_ADD_LARGE_EQUIPMENT;
        } else {
            appConfig = AppConfig.MODIFY_EQUIPMENT_INFO;
        }
        MainApi.requestCommon(this, appConfig, entity, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                hideLoading();
                try {
                    String httpResult = new String(bytes);
                    TLog.log("submitAddLargeEqu : " + httpResult);
                    Base base = new Gson().fromJson(httpResult, Base.class);
                    if (StringUtils.equals(base.getCode(), SysCode.SUCCESS)) {
                        sendCurrentItemBroadcast(1);
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
                hideLoading();
                AppContext.showToast(R.string.please_check_network);
            }
        });
    }

    private void filterDeletePersons() {
        unBindPersons.clear();
        final List<PersonInfoEntity> bindPersons = new ArrayList<>();
        bindPersons.addAll(mBindPersons);
        List<PersonInfoEntity> personDatas = new ArrayList<>();
        personDatas.addAll(mPersonDatas.subList(0, mPersonDatas.size() - 1));

        List<Integer> intj = new ArrayList<>();
        for (int i = 0; i < personDatas.size(); i++) {
            String person = personDatas.get(i).getPersonId();
            for (int j = 0; j < bindPersons.size(); j++) {
                String bindPerson = bindPersons.get(j).getPersonId();
                if (StringUtils.equals(bindPerson, person)) {
                    intj.add(j);
                }
            }
        }
        List<Integer> newListj = new ArrayList<>(new TreeSet(intj));
        for (int j = newListj.size() - 1; j >= 0; j--) {
            bindPersons.remove(newListj.get(j).intValue());
        }
        unBindPersons.addAll(bindPersons);
    }

    private boolean mustEntering() {
        if (StringUtils.isEmpty(mEtNumber.getText().toString())) {
            AppContext.showToast(R.string.please_entering_equipment_number);
            return false;
        }
        if (StringUtils.isEmpty(mEtName.getText().toString())) {
            AppContext.showToast(R.string.please_entering_equipment_name);
            return false;
        }
        if (mSelectType == null) {
            AppContext.showToast(R.string.select_device_type);
            return false;
        }
        if (StringUtils.equals(mType, "detail")) {
            if (mSelectStatus == null) {
                AppContext.showToast(R.string.please_select_status);
                return false;
            }
        }
        if (StringUtils.isEmpty(mEtOrder.getText().toString())) {
            AppContext.showToast(R.string.please_entering_order);
            return false;
        }
        if (StringUtils.isEmpty(mEtPhone.getText().toString())) {
            AppContext.showToast(R.string.please_entering_phone);
            return false;
        }
        return true;
    }

    private void equTypeFinish(SelectEntity entity) {
        mSelectType = entity;
        mTvType.setText(entity.getName());
    }

    private List<SelectEntity> getInitSelecList() {
        final List<SelectEntity> selectInfoList = new ArrayList<>();

        //
        Observable.just(mSelect).filter(new Predicate<String>() {
            @Override
            public boolean test(String s) throws Exception {
                return StringUtils.equals(mSelect, "type");
            }
        }).flatMap(new Function<String, ObservableSource<EquTypeBean>>() {
            @Override
            public ObservableSource<EquTypeBean> apply(String s) throws Exception {
                return Observable.fromIterable(mEquTypes);
            }
        }).subscribe(new Consumer<EquTypeBean>() {
            @Override
            public void accept(EquTypeBean equTypeBean) throws Exception {
                SelectEntity selectInfo = new SelectEntity();
                selectInfo.setId(equTypeBean.getId());
                selectInfo.setName(equTypeBean.getTypeName());
                selectInfo.setSelect(false);
                selectInfoList.add(selectInfo);
            }
        });

        if (StringUtils.equals(mSelect, "type")) {
            /*SelectEntity selectInfo;
            for (int i = 0; i < mEquTypes.size(); i++) {
                EquTypeBean equTypeBean = mEquTypes.get(i);
                selectInfo = new SelectEntity();
                selectInfo.setId(equTypeBean.getId());
                selectInfo.setName(equTypeBean.getTypeName());
                selectInfo.setSelect(false);
                selectInfoList.add(selectInfo);
            }*/
        } else if (StringUtils.equals(mSelect, "status")) {
            selectInfoList.addAll(mEquStatuss);
        }
        return selectInfoList;
    }

    private void reFreshView() {
        mEtName.setFocusable(true);
        mEtName.setFocusableInTouchMode(true);
        mEtName.requestFocus();
        mEtName.setSelection(mEtName.getText().length());

        mEtOrder.setFocusable(true);
        mEtOrder.setFocusableInTouchMode(true);
        mEtOrder.requestFocus();
        mEtOrder.setSelection(mEtOrder.getText().length());

        mEtPhone.setFocusable(true);
        mEtPhone.setFocusableInTouchMode(true);
        mEtPhone.requestFocus();
        mEtPhone.setSelection(mEtPhone.getText().length());

        mEtRemark.setFocusable(true);
        mEtRemark.setFocusableInTouchMode(true);
        mEtRemark.requestFocus();
        mEtRemark.setSelection(mEtRemark.getText().length());

        mEtNumber.setFocusable(true);
        mEtNumber.setFocusableInTouchMode(true);
        mEtNumber.requestFocus();
        mEtNumber.setSelection(mEtNumber.getText().length());

        mLlType.setEnabled(true);
        mIvType.setVisibility(View.VISIBLE);

        mLlStatus.setEnabled(true);
        mIvStatus.setVisibility(View.VISIBLE);

        initIvDelete();

        mMode = "submit";
        setTitle(AppContext.mResource.getString(R.string.modify_large_equipment));
        mTvLeft.setText("取消");
        mTvRight.setText("提交");

    }

    private void initIvDelete() {
        for (int i = 0; i < mPersonDatas.size(); i++) {
            mPersonDatas.get(i).setDeleteStatus("1");
        }
        PersonInfoEntity bean = new PersonInfoEntity();
        bean.setEmpName("");
        bean.setDeleteStatus("1");
        mPersonDatas.add(bean);
        mAdapter.notifyDataSetChanged();
    }

    public void addPerson() {
        if (StringUtils.equals(mStatus, "no_network")) {
            AppContext.showToast(R.string.please_check_network);
            return;
        } else if (StringUtils.equals(mStatus, "no_bind")) {
            AppContext.showToast(R.string.no_bind_work_mechine);
            return;
        }
        Intent intent = new Intent(this, ChangePersonListActivity.class);
        intent.putExtra("largeDeviceId", mDeviceId);
        intent.putExtra("personDatas", (Serializable) mPersonDatas);
        startActivityForResult(intent, RESULT_RECODE);
    }

    List<PersonInfoEntity> newBindPersons = new ArrayList<>();
    List<PersonInfoEntity> unBindPersons = new ArrayList<>();

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 1) {
            if (requestCode == RESULT_RECODE) {
                List<PersonInfoEntity> resultPersons = (ArrayList<PersonInfoEntity>) data.getSerializableExtra("personDatas");
                filterAddPersons(resultPersons);
                mPersonDatas.clear();
                mPersonDatas.addAll(resultPersons);
                mTvOperatePersons.setText(this.getResources().getString(R.string.operate_persons, mPersonDatas.size() + ""));
                initIvDelete();
            }
        }
    }

    private void filterAddPersons(List<PersonInfoEntity> resultPersons) {
        newBindPersons.clear();
        List<PersonInfoEntity> bindPersons = new ArrayList<>();
        bindPersons.addAll(mBindPersons);
        List<PersonInfoEntity> backPersons = new ArrayList<>();
        backPersons.addAll(resultPersons);
        List<Integer> intj = new ArrayList<>();
        for (int i = 0; i < bindPersons.size(); i++) {
            String bindPerson = bindPersons.get(i).getPersonId();
            for (int j = 0; j < backPersons.size(); j++) {
                String resultPerson = backPersons.get(j).getPersonId();
                if (StringUtils.equals(bindPerson, resultPerson)) {
                    intj.add(j);
                }
            }
        }
        List<Integer> newListj = new ArrayList<>(new TreeSet(intj));
        for (int j = newListj.size() - 1; j >= 0; j--) {
            backPersons.remove(newListj.get(j).intValue());
        }
        newBindPersons.addAll(backPersons);
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

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, this.getResources().getDisplayMetrics());
    }
}
