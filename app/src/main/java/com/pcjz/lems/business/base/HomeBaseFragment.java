package com.pcjz.lems.business.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.pcjz.lems.R;
import com.pcjz.lems.business.base.basebean.BaseData;
import com.pcjz.lems.business.base.basebean.BaseListData;
import com.pcjz.lems.business.base.basebean.BaseListTree;
import com.pcjz.lems.business.common.utils.FileUtils;
import com.pcjz.lems.business.common.utils.MessageBus;
import com.pcjz.lems.business.common.utils.StringUtils;
import com.pcjz.lems.business.common.utils.TDevice;
import com.pcjz.lems.business.common.utils.TLog;
import com.pcjz.lems.business.config.AppConfig;
import com.pcjz.lems.business.config.ConfigPath;
import com.pcjz.lems.business.constant.ResultStatus;
import com.pcjz.lems.business.constant.SysCode;
import com.pcjz.lems.business.context.AppContext;
import com.pcjz.lems.business.entity.ProjectPeroidInfo;
import com.pcjz.lems.business.entity.ProjectTreeMultiInfo;
import com.pcjz.lems.business.storage.SharedPreferencesManager;
import com.pcjz.lems.business.webapi.MainApi;
import com.pcjz.lems.business.widget.selectdialog.SelectInfo;
import com.pcjz.lems.business.widget.selectdialog.SelectorDialog;
import com.pcjz.lems.ui.homepage.HomePageActivity;
import com.pcjz.lems.ui.workrealname.adapter.WorkbenchProjAdapter;
import com.squareup.otto.BasicBus;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.pcjz.lems.ui.homepage.HomePageActivity.RELOAD_DATA;

/**
 * Created by pc on 2017/5/28.
 */

public class HomeBaseFragment extends BaseFragment implements SelectorDialog.ICallback {
    protected ImageView ivPersonCenter;
    protected ImageView ivSelectPro;
    protected ImageView ivWifiOnOff;
    protected LinearLayout llTitle;
    protected TextView tvProj;
    //项目实录
    protected TextView tvProjrecords;
    protected ArrayList<ProjectPeroidInfo> mProjectPeroids;
    protected TextView mTitle;
    protected String mProjectPeriodId;
    protected SelectorDialog selectorDialog;
    protected int mSelectCount = 7;
    private String mUserId;
    private String mMode = "";
    private ImageView ivMessage;

    @Override
    protected void initView(View view) {
        ivPersonCenter = (ImageView) view.findViewById(R.id.iv_personcenter);
        ivSelectPro = (ImageView) view.findViewById(R.id.iv_select_pro);
        ivWifiOnOff = (ImageView) view.findViewById(R.id.iv_wifi_onff);
        ivMessage = (ImageView) view.findViewById(R.id.iv_message);
        llTitle = (LinearLayout) view.findViewById(R.id.ll_title);
        //项目
        tvProj = (TextView) view.findViewById(R.id.tv_proj);
        tvProjrecords = (TextView) view.findViewById(R.id.tv_projrecords);
        tvProjrecords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        mUserId = SharedPreferencesManager.getString(ResultStatus.USER_ID);
        try {
            Bundle bundle = getArguments();
            int title = bundle.getInt("title");
            setTitle(AppContext.mResource.getString(title), view);
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
        if (!StringUtils.isEmpty(SharedPreferencesManager.getString(mUserId + getProjNameKey()))) {
            //存在sharedpreferences
            tvProj.setText(SharedPreferencesManager.getString(mUserId + getProjNameKey()));
        }
        if (mProjectTreeCalInfoArrayList != null) {
            mProjectTreeCalInfoArrayList.clear();
        }
        if (mSelectedName != null && mSelectedName.size() != 0) {
            mSelectedName.clear();
        }
        if (mSelectedId != null && mSelectedId.size() != 0) {
            mSelectedId.clear();
        }
        registerBroadcast();
    }

    @Override
    protected int getLayoutRes() {
        return 0;
    }

    @Override
    public void onResume() {
        super.onResume();
        String networkstate = SharedPreferencesManager.getString(getNetworkStateKey());
        if (networkstate != null && StringUtils.equals(networkstate, SysCode.NETWORKSTATE_OFF)) {
            ivWifiOnOff.setImageResource(R.drawable.home_toolbar_right_icon_no_wifi);
        } else {
            ivWifiOnOff.setImageResource(R.drawable.home_toolbar_right_icon_wifi);
        }
    }

    @Override
    public void initWebData() {
        /*super.initWebData();
        String postId = SharedPreferencesManager.getString(ResultStatus.POSTID);
        if (StringUtils.equals(postId, SysCode.POSTID_QUALIFIER) || StringUtils.equals(postId, SysCode.POSTID_SUPERVISOR) || StringUtils.equals(postId, SysCode.POSTID_CONSTRUCTION_TEAM)) {
            getProjectPeriod("init");
        } else {
            getProjTree();
        }*/
        getProjectPeriod("init");
    }

    protected ArrayList<SelectInfo> getInitSelecList() {
        ArrayList<SelectInfo> selectInfoArrayList = new ArrayList<SelectInfo>();
        if (mProjectTreeCalInfoArrayList != null) {
            SelectInfo selectInfo;
            for (int j = 0; j < mProjectTreeCalInfoArrayList.size(); j++) {
                ProjectTreeMultiInfo projectTreeMultiInfo1 = mProjectTreeCalInfoArrayList.get(j);
                if (projectTreeMultiInfo1 != null) {
                    selectInfo = new SelectInfo();
                    if (projectTreeMultiInfo1.getId() != null) {
                        selectInfo.setId(projectTreeMultiInfo1.getId());
                    }
                    if (projectTreeMultiInfo1.getOrganizationName() != null) {
                        selectInfo.setName(projectTreeMultiInfo1.getOrganizationName());
                    } else {
                        if (projectTreeMultiInfo1.getPeriodName() != null) {
                            selectInfo.setName(projectTreeMultiInfo1.getPeriodName());
                        }
                    }
                    selectInfoArrayList.add(selectInfo);
                }
            }
        }
        return selectInfoArrayList;
    }

    protected void getProjectPeriod(String mode) {
        HttpEntity entity = null;
        try {
            JSONObject obj0 = new JSONObject();
            entity = new StringEntity(obj0.toString(), "UTF-8");
            requestProjectPeriod(entity, mode);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    /**
     * eventBus消息传递
     */
    protected BasicBus mBasicBus = MessageBus.getBusInstance();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBasicBus.register(this);
    }

    private void requestProjectPeriod(HttpEntity entity, final String mode) {
        MainApi.requestCommon(getActivity(), AppConfig.PROJECTPERIOD_URL, entity, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                try {
                    String httpResult = new String(bytes);
                    TLog.log("getProjectPeroid : " + httpResult);
                    Type type = new TypeToken<BaseListData<ProjectPeroidInfo>>() {
                    }.getType();
                    BaseListData<ProjectPeroidInfo> datas = new Gson().fromJson(httpResult, type);
                    if (StringUtils.equals(datas.getCode(), SysCode.SUCCESS)) {
                        if (datas != null) {
                            mProjectPeroids = datas.getData();
                            //判断默认选中第一个，如果没有就显示请选择项目类型
                            if (mProjectPeroids != null && mProjectPeroids.size() > 0) {
                                if ("init".equals(mode)) {
                                    if (StringUtils.isEmpty(SharedPreferencesManager.getString(mUserId + getProjNameKey()))) {
                                        ProjectPeroidInfo peroidBean = mProjectPeroids.get(0);
                                        if (peroidBean != null && peroidBean.getPeriodName() != null) {
                                            //保存projectName到本地
                                            SharedPreferencesManager.putString(mUserId + getProjNameKey(), peroidBean.getPeriodName());
                                            if (peroidBean.getId() != null) {
                                                //保存projectId到本地
                                                SharedPreferencesManager.putString(mUserId + getProjIdKey(), peroidBean.getId());
                                            }
                                            tvProj.setText(peroidBean.getPeriodName());
                                            //项目期更改发送通知，和下面这种方式效果一样
                                            setMsg(0);
                                            //项目期数更改发送通知
                                            postMsg();
                                        }
                                    }
                                } else {
                                    defaultProjectperiod();
                                }
                            }
                        }
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
                if (throwable == null) {
                    return;
                }
            }
        });
    }

    protected void postMsg() {
        mBasicBus.post(SysCode.CHANGE_PROJECT_PERIOD);
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

    private void setMsg(int cmd) {
        if (mHandler != null) {
            Message msg = mHandler.obtainMessage(RELOAD_DATA);
            msg.what = cmd;
            mHandler.sendMessage(msg);
        }
    }

    protected Handler mHandler = new Handler() {
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

    protected void reloadData() {

    }

    private PopupWindow popClass;

    protected void popupWindowProj(View v) {

        View view = getActivity().getLayoutInflater().inflate(
                R.layout.popup_workbench_proj, null);
        popClass = new PopupWindow(view, TDevice.getWindowsWidth(getActivity()),
                LinearLayout.LayoutParams.WRAP_CONTENT);
        popClass.setOutsideTouchable(true);
        popClass.setBackgroundDrawable(new BitmapDrawable());
        popClass.setFocusable(true);
        popClass.showAsDropDown(v, 0, 0);
        final ListView mylvRes = (ListView) view.findViewById(R.id.lv_content);
        final WorkbenchProjAdapter mPopupWindowFirAdapter = new WorkbenchProjAdapter(tvProj.getText().toString());

        mPopupWindowFirAdapter.setData(mProjectPeroids);
        mylvRes.setAdapter(mPopupWindowFirAdapter);
        mylvRes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ProjectPeroidInfo projectPeroidInfo = (ProjectPeroidInfo) mPopupWindowFirAdapter.getItem(position);
                if (projectPeroidInfo != null && projectPeroidInfo.getPeriodName() != null) {
                    SharedPreferencesManager.putString(mUserId + getProjNameKey(), projectPeroidInfo.getPeriodName());
                    if (projectPeroidInfo.getId() != null) {
                        SharedPreferencesManager.putString(mUserId + getProjIdKey(), projectPeroidInfo.getId());
                    }
                    tvProj.setText(projectPeroidInfo.getPeriodName());
                    setMsg(0);
                    postMsg();
                }
                if (popClass != null && popClass.isShowing()) {
                    popClass.dismiss();
                    popClass = null;
                }
            }
        });
        popClass.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams params = getActivity().getWindow().getAttributes();
                params.alpha = 1f;
                getActivity().getWindow().setAttributes(params);
                ivSelectPro.setImageResource(R.drawable.home_toolbar_icon_drop_down);
            }
        });
        WindowManager.LayoutParams params = getActivity().getWindow().getAttributes();
        params.alpha = 0.8f;
        getActivity().getWindow().setAttributes(params);
    }

    protected void hideProj() {
        if (tvProj.getVisibility() == View.VISIBLE) {
            ivPersonCenter.setVisibility(View.GONE);
            ivSelectPro.setVisibility(View.GONE);
            ivWifiOnOff.setVisibility(View.VISIBLE);
            tvProjrecords.setVisibility(View.GONE);
            tvProj.setVisibility(View.GONE);
            mTitle.setVisibility(View.VISIBLE);
            ivPersonCenter.setVisibility(View.VISIBLE);
        }
    }

    protected void showProj() {
        ivPersonCenter.setVisibility(View.VISIBLE);
        ivSelectPro.setVisibility(View.VISIBLE);
        /*String postId = SharedPreferencesManager.getString(ResultStatus.POSTID);
        if (StringUtils.equals(postId, SysCode.POSTID_QUALIFIER) || StringUtils.equals(postId, SysCode.POSTID_SUPERVISOR) || StringUtils.equals(postId, SysCode.POSTID_CONSTRUCTION_TEAM)) {
            ivWifiOnOff.setVisibility(View.VISIBLE);
            tvProjrecords.setVisibility(View.GONE);
        } else {
            ivWifiOnOff.setVisibility(View.GONE);
            tvProjrecords.setVisibility(View.VISIBLE);
        }*/
        ivWifiOnOff.setVisibility(View.GONE);
        //ivMessage.setVisibility(View.VISIBLE);
        tvProj.setVisibility(View.VISIBLE);
        mTitle.setVisibility(View.GONE);
    }

    protected void setTitle(String title, View view) {
        try {
            mTitle = (TextView) view.findViewById(R.id.tv_title);
            mTitle.setText(title);
        } catch (Exception e) {

        }
    }


    @Override
    protected void setListener() {
        super.setListener();
        setWifiListener();
        if (ivSelectPro.getVisibility() == View.VISIBLE) {
            setTitleListener();
        }
        ivPersonCenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HomePageActivity) getActivity()).openCloseDrawer();
            }
        });
    }

    protected void registerBroadcast() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SysCode.STATE_WIFI_CHANGE);
        getActivity().registerReceiver(receiver, intentFilter);
    }

    protected BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (StringUtils.equals(SysCode.STATE_WIFI_CHANGE, intent.getAction())) {
                String networkstate = SharedPreferencesManager.getString(getNetworkStateKey());
                if (StringUtils.equals(networkstate, SysCode.NETWORKSTATE_OFF)) {
                    ivWifiOnOff.setImageResource(R.drawable.home_toolbar_right_icon_no_wifi);
                } else {
                    ivWifiOnOff.setImageResource(R.drawable.home_toolbar_right_icon_wifi);
                }
            }
        }
    };

    private void defaultProjectperiod() {
        if (mProjectPeroids != null && mProjectPeroids.size() > 0) {
            ProjectPeroidInfo peroidBean = mProjectPeroids.get(0);
            if (peroidBean != null && peroidBean.getPeriodName() != null) {
                SharedPreferencesManager.putString(mUserId + getProjNameKey(), peroidBean.getPeriodName());
                if (peroidBean.getId() != null) {
                    SharedPreferencesManager.putString(mUserId + getProjIdKey(), peroidBean.getId());
                }
                // tvProj.setText(peroidBean.getPeriodName());
                setMsg(0);
                postMsg();
            }
        }
    }

    protected void setWifiListener() {
        ivWifiOnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*try {
                    String networkstate = SharedPreferencesManager.getString(getNetworkStateKey());
                    if (StringUtils.equals(networkstate, SysCode.NETWORKSTATE_OFF)) {
                        SharedPreferencesManager.putString(getNetworkStateKey(), SysCode.NETWORKSTATE_ON);
                        String postId = SharedPreferencesManager.getString(ResultStatus.POSTID);
                        if (StringUtils.equals(postId, SysCode.POSTID_QUALIFIER) || StringUtils.equals(postId, SysCode.POSTID_SUPERVISOR) || StringUtils.equals(postId, SysCode.POSTID_CONSTRUCTION_TEAM)) {
                            getProjectPeriod("init_wifi");
                        } else {
                            getProjTree();
                        }
                    } else {
                        SharedPreferencesManager.putString(getNetworkStateKey(), SysCode.NETWORKSTATE_OFF);
                        AppContext.showToast(R.string.current_be_in_offline_state);
                        //在线切离线，获取离线项目期并且设置头部项目期
                        String content = FileUtils.readTxtFile(ConfigPath.downLoadPath, SysCode.PERIOD_LIST);
                        mProjectPeroids = new Gson().fromJson(content, new TypeToken<ArrayList<ProjectPeroidInfo>>() {
                        }.getType());
                        defaultProjectperiod();
                    }
                    Intent intent = new Intent();
                    intent.setAction(SysCode.STATE_WIFI_CHANGE);
                    getActivity().sendBroadcast(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }*/
            }
        });
    }

    protected void setTitleListener() {
        llTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //String postname = SharedPreferencesManager.getString(ResultStatus.POSTNAME);
                //if (StringUtils.equals(AuthorityManager.CURRENT_USER,AuthorityManager.MANAGER)) {
                //String postId = SharedPreferencesManager.getString(ResultStatus.POSTID);
                //if (StringUtils.equals(postId, SysCode.POSTID_QUALIFIER) || StringUtils.equals(postId, SysCode.POSTID_SUPERVISOR) || StringUtils.equals(postId, SysCode.POSTID_CONSTRUCTION_TEAM)) {
                /*String networkstate = SharedPreferencesManager.getString(getNetworkStateKey());
                if (StringUtils.equals(networkstate, SysCode.NETWORKSTATE_OFF)) {
                    String content = FileUtils.readTxtFile(ConfigPath.downLoadPath, SysCode.PERIOD_LIST);
                    mProjectPeroids = new Gson().fromJson(content, new TypeToken<ArrayList<ProjectPeroidInfo>>() {
                    }.getType());
                    if (mProjectPeroids == null || (mProjectPeroids != null && mProjectPeroids.size() == 0)) {
                        AppContext.showToast(R.string.please_download_data);
                        return;
                    }
                } else {*/
                if (mProjectPeroids == null || (mProjectPeroids != null && mProjectPeroids.size() == 0)) {
                    AppContext.showToast(R.string.no_proj_data);
                    return;
                }
                //}
                ivSelectPro.setImageResource(R.drawable.home_toolbar_icon_drop_down_open);
                popupWindowProj(v);
                /*} else {
                    selectorDialog = new SelectorDialog();
                    selectorDialog.setCallBack(HomeBaseFragment.this);
                    if (mSelectedName == null || mSelectedName.size() == 0) {
                        mSelectedName = (List<String>) AppContext.getACache().getAsObject(mUserId + "progress_list_names");
                    }
                    if (mSelectedId == null || mSelectedId.size() == 0) {
                        mSelectedId = (List<String>) AppContext.getACache().getAsObject(mUserId + "progress_list_ids");
                    }
                    selectorDialog.setSingleSelectName(mSelectedName);
                    selectorDialog.setSingleSelectId(mSelectedId);
                    selectorDialog.setSelectCount(mSelectCount);
                    selectorDialog.setmType(SelectorDialog.SINGLE_MODE);
                    //selectorDialog.setLevel(8);
                    if (mProjectTreeCalInfoArrayList != null) {
                        selectorDialog.setInitSelecList(getInitSelecList(), "");
                    } else {
                        selectorDialog.setInitSelecList(null, mMode);
                    }
                    selectorDialog.show(getActivity().getSupportFragmentManager(), "tag");
                }*/
            }
        });
    }

    protected ArrayList<ProjectTreeMultiInfo> mProjectTreeCalInfoArrayList;

    //管理岗位获取项目树
    protected void getProjTree() {
        HttpEntity entity = null;
        try {
            entity = new StringEntity("{}", "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        MainApi.requestCommon(getActivity(), AppConfig.GET_ORGPROJECTTREE_URL, entity, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                mMode = "";
                try {
                    String httpResult = new String(bytes);
                    TLog.log("getProjectTreeList : " + httpResult);
                    Type type = new TypeToken<BaseData<BaseListTree<ProjectTreeMultiInfo>>>() {
                    }.getType();
                    BaseData<BaseListTree<ProjectTreeMultiInfo>> datas = new Gson().fromJson(httpResult, type);
                    if (StringUtils.equals(datas.getCode(), SysCode.SUCCESS)) {
                        if (datas != null) {
                            //((WorkbenchChechProcessFragment) (AuthManager.getFragment(0))).setProjectCount(datas.getData().getCount() + "");
                            mProjectTreeCalInfoArrayList = datas.getData().getTree();
                            if (mProjectTreeCalInfoArrayList != null) {
                                if (StringUtils.isEmpty(SharedPreferencesManager.getString(mUserId + getProjNameKey()))) {
                                    ProjectTreeMultiInfo projectTreeMultiInfo = mProjectTreeCalInfoArrayList.get(0);
                                    projectTreeMultiInfo = getPeriod(projectTreeMultiInfo);
                                    if (projectTreeMultiInfo != null && projectTreeMultiInfo.getPeriodName() != null) {
                                        SharedPreferencesManager.putString(mUserId + getProjNameKey(), projectTreeMultiInfo.getPeriodName());
                                        if (projectTreeMultiInfo.getId() != null) {
                                            SharedPreferencesManager.putString(mUserId + getProjIdKey(), projectTreeMultiInfo.getId());
                                        }
                                        //    tvProj.setText(projectTreeMultiInfo.getPeriodName());
                                        setMsg(0);
                                        //项目期数更改发送通知
                                        //mBasicBus.post(SysCode.SWITCH_PERIOD);
                                        //postMsg();
                                    }
                                } else {
                                }
                            }
                            if (selectorDialog != null) {
                                if (mProjectTreeCalInfoArrayList != null && mProjectTreeCalInfoArrayList.size() > 0) {
                                    selectorDialog.setInitSelecList(getInitSelecList(), "");
                                } else {
                                    selectorDialog.setInitSelecList(null, "");
                                }
                            }
                        }
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
                mMode = SysCode.FAILURE_INTERNET;
                if (throwable == null) {
                    return;
                }
            }
        });
    }

    private ProjectTreeMultiInfo getPeriod(ProjectTreeMultiInfo projectTreeMultiInfo) {
        if (projectTreeMultiInfo == null) return null;
        if (StringUtils.isEmpty(projectTreeMultiInfo.getPeriodName())) {
            if (projectTreeMultiInfo.getList() != null && projectTreeMultiInfo.getList().size() > 0) {
                return getPeriod(projectTreeMultiInfo.getList().get(0));
            } else {
                return null;
            }
        } else {
            return projectTreeMultiInfo;
        }
    }

    protected List mSelectedName;
    protected List mSelectedId;

    public void finish(List selectedName, List selectedId, String mode) {
        mSelectedName = selectedName;
        mSelectedId = selectedId;
        selectorDialog = null;
        /*if (AuthManager.getFragment(0) instanceof WorkbenchChechProcessFragment) {
            AppContext.getACache().put(mUserId + "progress_list_names", (Serializable) selectedName);
            AppContext.getACache().put(mUserId + "progress_list_ids", (Serializable) selectedId);
            if (SelectorDialog.SINGLE_MODE.compareTo(mode) == 0) {
                for (int i = mSelectedName.size() - 1; i >= 0; i--) {
                    if (!StringUtils.isEmpty((String) mSelectedName.get(i))) {
                        mProjectPeriodId = (String) mSelectedId.get(i);
                        if (!StringUtils.isEmpty((String) mSelectedName.get(i))) {
                            tvProj.setText((String) mSelectedName.get(i));
                            SharedPreferencesManager.putString(mUserId + getProjNameKey(), (String) mSelectedName.get(i));
                        }
                        if (!StringUtils.isEmpty(mProjectPeriodId)) {
                            SharedPreferencesManager.putString(mUserId + getProjIdKey(), mProjectPeriodId);
                        }
                        ((WorkbenchChechProcessFragment) (AuthManager.getFragment(0))).setProjectPeriodId(mSelectedId.get(i) + "", (String) mSelectedName.get(i));
                        return;
                    }
                }
            }
        }*/
    }

    @Override
    public void finish(List selcectorNames, List selectorIds, List selectedNames, List selectedIds, String mode) {

    }


    Map<String, List<ProjectTreeMultiInfo>> mProjectTreeCalInfoMap;

    @Override
    public ArrayList<SelectInfo> getNextSelectList(int currentPostion, String currentSelectName) {
        if (mProjectTreeCalInfoArrayList == null) return null;
        if (currentPostion == 0) {
            for (int i = 0; i < mProjectTreeCalInfoArrayList.size(); i++) {
                ProjectTreeMultiInfo projectTreeMultiInfo = mProjectTreeCalInfoArrayList.get(i);
                String itemName;
                if (StringUtils.isEmpty(projectTreeMultiInfo.getOrganizationName())) {
                    itemName = projectTreeMultiInfo.getPeriodName();
                } else {
                    itemName = projectTreeMultiInfo.getOrganizationName();
                }
                if (projectTreeMultiInfo != null && StringUtils.equals(currentSelectName, itemName)) {
                    if (mProjectTreeCalInfoMap == null) {
                        mProjectTreeCalInfoMap = new HashMap<>();
                    }
                    List<ProjectTreeMultiInfo> projectTreeMultiInfoArrayList = projectTreeMultiInfo.getList();
                    if (projectTreeMultiInfoArrayList != null) {
                        mProjectTreeCalInfoMap.put(currentPostion + "", projectTreeMultiInfoArrayList);
                    }
                    ArrayList<SelectInfo> selectInfoArrayList = new ArrayList<SelectInfo>();
                    SelectInfo selectInfo;
                    if (projectTreeMultiInfoArrayList != null) {
                        for (int j = 0; j < projectTreeMultiInfoArrayList.size(); j++) {
                            ProjectTreeMultiInfo projectTreeMultiInfo1 = projectTreeMultiInfoArrayList.get(j);
                            if (projectTreeMultiInfo1 != null) {
                                selectInfo = new SelectInfo();

                                if (projectTreeMultiInfo1.getOrganizationName() != null) {
                                    selectInfo.setName(projectTreeMultiInfo1.getOrganizationName());
                                    if (projectTreeMultiInfo1.getId() != null) {
                                        selectInfo.setId(projectTreeMultiInfo1.getId());
                                    }
                                } else {
                                    if (projectTreeMultiInfo1.getPeriodName() != null) {
                                        selectInfo.setName(projectTreeMultiInfo1.getPeriodName());
                                    }
                                    if (projectTreeMultiInfo1.getId() != null) {
                                        selectInfo.setId(projectTreeMultiInfo1.getId());
                                    }
                                }
                                selectInfoArrayList.add(selectInfo);
                            }
                        }
                    }
                    return selectInfoArrayList;
                }
            }
            return null;
        } else {
            if (mProjectTreeCalInfoMap == null || mProjectTreeCalInfoMap.get(currentPostion - 1 + "") == null)
                return null;
            for (int i = 0; i < mProjectTreeCalInfoMap.get(currentPostion - 1 + "").size(); i++) {
                ProjectTreeMultiInfo projectTreeMultiInfo = mProjectTreeCalInfoMap.get(currentPostion - 1 + "").get(i);
                String itemName;
                if (StringUtils.isEmpty(projectTreeMultiInfo.getOrganizationName())) {
                    itemName = projectTreeMultiInfo.getPeriodName();
                } else {
                    itemName = projectTreeMultiInfo.getOrganizationName();
                }
                if (projectTreeMultiInfo != null && StringUtils.equals(currentSelectName, itemName)) {
                    if (mProjectTreeCalInfoMap == null) {
                        mProjectTreeCalInfoMap = new HashMap<String, List<ProjectTreeMultiInfo>>();
                    }
                    List<ProjectTreeMultiInfo> projectTreeMultiInfoArrayList = projectTreeMultiInfo.getList();
                    if (projectTreeMultiInfoArrayList != null) {
                        mProjectTreeCalInfoMap.put(currentPostion + "", projectTreeMultiInfoArrayList);
                    }
                    ArrayList<SelectInfo> selectInfoArrayList = new ArrayList<SelectInfo>();
                    SelectInfo selectInfo;
                    if (projectTreeMultiInfoArrayList != null) {
                        for (int j = 0; j < projectTreeMultiInfoArrayList.size(); j++) {
                            ProjectTreeMultiInfo projectTreeMultiInfo1 = projectTreeMultiInfoArrayList.get(j);
                            if (projectTreeMultiInfo1 != null) {
                                selectInfo = new SelectInfo();

                                if (projectTreeMultiInfo1.getOrganizationName() != null) {
                                    selectInfo.setName(projectTreeMultiInfo1.getOrganizationName());
                                    if (projectTreeMultiInfo1.getId() != null) {
                                        selectInfo.setId(projectTreeMultiInfo1.getId());
                                    }
                                } else {
                                    if (projectTreeMultiInfo1.getPeriodName() != null) {
                                        selectInfo.setName(projectTreeMultiInfo1.getPeriodName());
                                    }
                                    if (projectTreeMultiInfo1.getId() != null) {
                                        selectInfo.setId(projectTreeMultiInfo1.getId());
                                    }
                                }
                                selectInfoArrayList.add(selectInfo);
                            }
                        }
                    }
                    return selectInfoArrayList;
                }
            }
            return null;
        }
    }

    @Override
    public ArrayList<SelectInfo> getNextSelectList(int currentPostion, int selectedPosition, String currentSelectName) {
        return getNextSelectList(currentPostion, currentSelectName);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            mBasicBus.unregister(getActivity());
            getActivity().unregisterReceiver(receiver);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void clearData() {
    }
}
