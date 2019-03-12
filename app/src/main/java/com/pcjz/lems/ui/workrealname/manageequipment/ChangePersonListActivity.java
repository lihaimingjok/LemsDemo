package com.pcjz.lems.ui.workrealname.manageequipment;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.pcjz.lems.R;
import com.pcjz.lems.business.base.BaseActivity;
import com.pcjz.lems.business.base.basebean.BaseData;
import com.pcjz.lems.business.base.basebean.BaseListData;
import com.pcjz.lems.business.common.utils.StringUtils;
import com.pcjz.lems.business.common.utils.TLog;
import com.pcjz.lems.business.common.view.SideBar;
import com.pcjz.lems.business.common.view.personlist.CharacterParser;
import com.pcjz.lems.business.common.view.personlist.PinyinComparator;
import com.pcjz.lems.business.config.AppConfig;
import com.pcjz.lems.business.constant.ResultStatus;
import com.pcjz.lems.business.constant.SysCode;
import com.pcjz.lems.business.context.AppContext;
import com.pcjz.lems.business.entity.PersonInfoEntity;
import com.pcjz.lems.business.storage.SharedPreferencesManager;
import com.pcjz.lems.business.webapi.MainApi;
import com.pcjz.lems.business.widget.contractlist.CNPinyin;
import com.pcjz.lems.business.widget.contractlist.CNPinyinFactory;
import com.pcjz.lems.business.widget.contractlist.CNPinyinIndex;
import com.pcjz.lems.business.widget.contractlist.CNPinyinIndexFactory;
import com.pcjz.lems.ui.workrealname.manageequipment.adapter.ContactsSortAdapter;
import com.pcjz.lems.ui.workrealname.manageequipment.adapter.SearchContractsAdapter;
import com.pcjz.lems.ui.workrealname.manageequipment.adapter.SearchHeaderAdapter;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
import org.json.JSONObject;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * created by yezhengyu on 2017/9/19 11:21
 */

public class ChangePersonListActivity extends BaseActivity implements View.OnClickListener {

    private SideBar sideBar;
    private TextView dialog;
    ListView mListView;
    private ContactsSortAdapter contactsSortadapter;
    public static final int MSG_INITS_RESULTS = 0x0000;
    public static final int MSG_SEARCH_RESULTS = 0x0001;

    /**
     * 汉字转换成拼音的类
     */
    private CharacterParser characterParser;
    private List<PersonInfoEntity> sourceDateList;
    /**
     * 根据拼音来排列ListView里面的数据类
     */
    private PinyinComparator pinyinComparator;
    private EditText mEtSearch;
    private List<CNPinyinIndex<PersonInfoEntity>> filterDataList = new ArrayList<>();
    private ListView mSearchListView;
    private TextView mTvNoData;
    private SearchContractsAdapter searchContractsAdapter;
    private ArrayList<CNPinyin<PersonInfoEntity>> mCnPinyinArrayList;
    private LinearLayout mLlSearchResults;
    private String edittext;
    private List<PersonInfoEntity> mSearchHeaderList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private ImageView mIvSearchIcon;
    private RelativeLayout rlNoData;
    private ImageView ivNoData;
    private TextView tvNoData;
    private SearchHeaderAdapter searchHeaderAdapter;
    private int width;
    private int height;
    private String mLargeDeviceId;

    private String mUserId;
    private String mProjectId;
    private ArrayList<PersonInfoEntity> mPersonDatas;
    private LinearLayout mLayout;
    private RelativeLayout mRlBack;
    private TextView mTvTitleBarRight;

    @Override
    public void setView() {
        setContentView(R.layout.activity_change_person);
        setTitle(AppContext.mResource.getString(R.string.modify_person_list));
        DisplayMetrics dm = getResources().getDisplayMetrics();
        width = dm.widthPixels;
        height = dm.heightPixels;
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
        mTvTitleBarRight = (TextView) findViewById(R.id.tv_titlebar_right);
        mTvTitleBarRight.setText(R.string.modify_person_finish);
        RelativeLayout rlFinish = (RelativeLayout) findViewById(R.id.rl_my_download);
        rlFinish.setOnClickListener(this);
        mLargeDeviceId = getIntent().getExtras().getString("largeDeviceId");
        mPersonDatas = (ArrayList<PersonInfoEntity>) getIntent().getSerializableExtra("personDatas");
        sideBar = (SideBar) findViewById(R.id.sidrbar);
        dialog = (TextView) findViewById(R.id.dialog);
        sideBar.setTextView(dialog);
        mUserId = SharedPreferencesManager.getString(ResultStatus.USER_ID);
        mProjectId = SharedPreferencesManager.getString(mUserId + SysCode.PROJECTPERIODID);
        mListView = (ListView) findViewById(R.id.lv_contacts);
        //搜索图标
        mIvSearchIcon = (ImageView) findViewById(R.id.iv_search_icon);
        //搜索recyclerView
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_header);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(layoutManager);
        //搜索editText
        mEtSearch = (EditText) findViewById(R.id.et_search_person);
        //搜索记录布局
        mLlSearchResults = (LinearLayout) findViewById(R.id.ll_search_results);
        //搜索记录
        mSearchListView = (ListView) findViewById(R.id.lv_search_contacts);

        //无数据页面
        mLayout = (LinearLayout) findViewById(R.id.ll_content);
        mLayout.setVisibility(View.GONE);
        //没有搜索记录时
        mTvNoData = (TextView) findViewById(R.id.tv_no_search_data);
        rlNoData = (RelativeLayout) findViewById(R.id.rl_no_data);
        ivNoData = (ImageView) findViewById(R.id.iv_no_data);
        tvNoData = (TextView) findViewById(R.id.tv_no_data);
        initNoDataView();

        //实例化汉字转拼音类
        characterParser = CharacterParser.getInstance();
        pinyinComparator = new PinyinComparator();

        //搜索记录listView
        searchContractsAdapter = new SearchContractsAdapter(this, filterDataList);
        mSearchListView.setAdapter(searchContractsAdapter);
        //搜索头部添加recyclerView
        searchHeaderAdapter = new SearchHeaderAdapter(this, mSearchHeaderList, width);
        mRecyclerView.setAdapter(searchHeaderAdapter);

        requestContractData();
    }

    private void requestContractData() {
        initLoading("");
        JSONObject obj = new JSONObject();
        HttpEntity entity = null;
        try {
            JSONObject obj0 = new JSONObject();
            obj.put("projectId", mProjectId);
            obj.put("largeDeviceId", mLargeDeviceId);
            obj0.put("params", obj);
            entity = new StringEntity(obj0.toString(), "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        MainApi.requestCommon(this, AppConfig.GET_LARGE_EQUIPMENT_BIND_WORK_MECHINE_PERSONS, entity, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                hideLoading();
                try {
                    String httpResult = new String(bytes);
                    TLog.log("getChangePersonList : " + httpResult);
                    Type type = new TypeToken<BaseListData<PersonInfoEntity>>() {
                    }.getType();
                    BaseListData<PersonInfoEntity> datas = new Gson().fromJson(httpResult, type);
                    if (StringUtils.equals(datas.getCode(), ResultStatus.SUCCESS)) {
                        List<PersonInfoEntity> personList = datas.getData();
                        if (personList != null && personList.size() != 0) {
                            if (mTvTitleBarRight.getVisibility() != View.VISIBLE) {
                                mTvTitleBarRight.setVisibility(View.VISIBLE);
                            }
                            if (mLayout != null && mLayout.getVisibility() != View.VISIBLE) {
                                mLayout.setVisibility(View.VISIBLE);
                            }
                            if (rlNoData != null && rlNoData.getVisibility() == View.VISIBLE) {
                                rlNoData.setVisibility(View.GONE);
                            }
                            sourceDateList = filledData(personList);
                            //集合中所有拼音的初始化，考虑在子线程中运行
                            CNPinyinFactory.createCNPinyinList(sourceDateList, MSG_INITS_RESULTS, mHandler);
                            // 根据a-z进行排序源数据
                            Collections.sort(sourceDateList, pinyinComparator);
                            mPersonDatas.remove(mPersonDatas.size()-1);
                            //列表listView
                            contactsSortadapter = new ContactsSortAdapter(ChangePersonListActivity.this, sourceDateList, mPersonDatas);
                            mListView.setAdapter(contactsSortadapter);
                            initHeadList();
                        } else {
                            initNoData();
                        }
                        return;
                    } else {
                        initNoData();
                        AppContext.showToast(datas.getMsg());
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                initNoData();
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                hideLoading();
                initNoInternetView();
            }
        });
    }

    //有网无数据
    private void initNoDataView() {
        if (ivNoData == null) return;
        rlNoData.setBackground(ContextCompat.getDrawable(this, R.color.white));
        ivNoData.setImageResource(R.drawable.pop_window_choose_no_content);
        tvNoData.setText(AppContext.mResource.getString(R.string.person_list_no_data));
    }

    //网络异常
    private void initNoInternetView() {
        if (ivNoData == null) return;
        ivNoData.setImageResource(R.drawable.no_internet_icon);
        tvNoData.setText(AppContext.mResource.getString(R.string.please_check_network));
        if (mTvTitleBarRight.getVisibility() == View.VISIBLE) {
            mTvTitleBarRight.setVisibility(View.GONE);
        }
    }

    private void initNoData() {
        if (rlNoData == null) return;
        if (rlNoData != null && rlNoData.getVisibility() != View.VISIBLE) {
            rlNoData.setVisibility(View.VISIBLE);
        }
        if (mLayout != null && mLayout.getVisibility() == View.VISIBLE) {
            mLayout.setVisibility(View.GONE);
        }
        if (mTvTitleBarRight.getVisibility() == View.VISIBLE) {
            mTvTitleBarRight.setVisibility(View.GONE);
        }
    }

    @Override
    public void setListener() {
        // 设置右侧[A-Z]快速导航栏触摸监听
        sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {
                if (contactsSortadapter != null && mListView != null) {
                    //by yezhengyu
                    /*if (StringUtils.equals(s, "#")) {
                        mListView.setSelection(sourceDateList.size() - 1);
                        return;
                    }*/

                    // 该字母首次出现的位置
                    int position = contactsSortadapter.getPositionForSection(s.charAt(0));
                    if (position != -1) {
                        mListView.setSelection(position);
                    }
                }
            }
        });
        // item事件
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long arg3) {
                ContactsSortAdapter.ViewHolder viewHolder = (ContactsSortAdapter.ViewHolder) view.getTag();
                viewHolder.cbChecked.performClick();
                contactsSortadapter.toggleChecked(position);
                initHeadList();
            }
        });

        mEtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    edittext = s.toString();
                    mLlSearchResults.setVisibility(View.VISIBLE);
                    //搜索考虑在子线程中运行
                    CNPinyinIndexFactory.indexList(mCnPinyinArrayList, s.toString(), MSG_SEARCH_RESULTS, mHandler);
                } else {
                    mLlSearchResults.setVisibility(View.GONE);
                }
            }
        });

        mSearchListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SearchContractsAdapter.ViewHolder viewHolder = (SearchContractsAdapter.ViewHolder) view.getTag();
                viewHolder.cbChecked.performClick();
                searchContractsAdapter.toggleChecked(position);
                List<PersonInfoEntity> searchSelectedList = searchContractsAdapter.getSelectedList();
                contactsSortadapter.updateListView(searchSelectedList);
                mLlSearchResults.setVisibility(View.GONE);
                mEtSearch.setText("");
                initHeadList();
            }
        });

        //头像点击删除
        searchHeaderAdapter.setOnItemClickListener(new SearchHeaderAdapter.OnClickLisenerI() {
            @Override
            public void setOnClickListener(View view, int position) {
                mSearchHeaderList.remove(position);
                contactsSortadapter.updateListView(mSearchHeaderList);
                initHeadList();
                if (mLlSearchResults != null && mLlSearchResults.getVisibility() == View.VISIBLE) {
                    if (mSearchListView != null && mSearchListView.getVisibility() == View.VISIBLE) {
                        searchContractsAdapter.updateListView2(mSearchHeaderList);
                    }
                }
            }
        });
    }

    private void initHeadList() {
        List<PersonInfoEntity> contactsSelectedList = contactsSortadapter.getSelectedList();
        if (contactsSelectedList.size() > 0) {
            mIvSearchIcon.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
            mSearchHeaderList.clear();
            mSearchHeaderList.addAll(contactsSelectedList);
            searchHeaderAdapter.notifyDataSetChanged();
            int itemCount = searchHeaderAdapter.getItemCount();
            ViewGroup.LayoutParams layoutParams = mRecyclerView.getLayoutParams();
            if (itemCount >= 6) {
                layoutParams.width = 3 * width / 4;
            } else {
                layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT;
            }
            mRecyclerView.setLayoutParams(layoutParams);
            mRecyclerView.scrollToPosition(itemCount - 1);
        } else {
            mIvSearchIcon.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            switch (what) {
                case MSG_INITS_RESULTS:
                    mCnPinyinArrayList = (ArrayList<CNPinyin<PersonInfoEntity>>) msg.obj;
                    break;
                case MSG_SEARCH_RESULTS:
                    ArrayList<CNPinyinIndex<PersonInfoEntity>> cnPinyinIndexArrayList = (ArrayList<CNPinyinIndex<PersonInfoEntity>>) msg.obj;
                    if (cnPinyinIndexArrayList != null && cnPinyinIndexArrayList.size() != 0) {
                        if (mSearchListView != null && mSearchListView.getVisibility() == View.GONE) {
                            mSearchListView.setVisibility(View.VISIBLE);
                        }
                        if (mTvNoData != null && mTvNoData.getVisibility() == View.VISIBLE) {
                            mTvNoData.setVisibility(View.GONE);
                        }

                        List<PersonInfoEntity> contactSelectedList = contactsSortadapter.getSelectedList();
                        searchContractsAdapter.updateListView(cnPinyinIndexArrayList, contactSelectedList);
                    } else {
                        if (mSearchListView != null && mSearchListView.getVisibility() == View.VISIBLE) {
                            mSearchListView.setVisibility(View.GONE);
                        }
                        if (mTvNoData != null && mTvNoData.getVisibility() == View.GONE) {
                            mTvNoData.setVisibility(View.VISIBLE);
                        }
                        mTvNoData.setText(Html.fromHtml("没有找到“" + "<font color='#FD9426' size='20'>" + edittext + "</font>" + "”相关的结果"));
                    }
                    break;
            }
        }
    };


    /**
     * 为ListView填充数据
     * @return
     */
    private List<PersonInfoEntity> filledData(List<PersonInfoEntity> personList) {
        List<PersonInfoEntity> mSortList = new ArrayList<PersonInfoEntity>();
        for (int i = 0; i < personList.size(); i++) {
            PersonInfoEntity sortModel = new PersonInfoEntity();
            PersonInfoEntity personInfo = personList.get(i);
            sortModel.setEmpName(personInfo.getEmpName());
            sortModel.setJobName(personInfo.getJobName());
            sortModel.setFacephoto(personInfo.getFacephoto());
            sortModel.setPersonId(personInfo.getPersonId());
            sortModel.setProjectPersonId(personInfo.getProjectPersonId());
            sortModel.setBackGroundColor(personInfo.getBackGroundColor());
            // 汉字转换成拼音
            String pinyin = characterParser.getSelling(personList.get(i).getEmpName());
            String sortString = pinyin.substring(0, 1).toUpperCase();

            // 正则表达式，判断首字母是否是英文字母
            if (sortString.matches("[A-Z]")) {
                sortModel.setSortLetters(sortString.toUpperCase());
            } else {
                sortModel.setSortLetters("#");
            }
            mSortList.add(sortModel);
        }
        return mSortList;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.rl_my_download:
                List<PersonInfoEntity> contactsSelectedList = contactsSortadapter.getSelectedList();
                Intent intent = new Intent();
                intent.putExtra("personDatas", (Serializable)contactsSelectedList);
                this.setResult(1, intent);
                closeKeyboard();
                finish();
                break;
        }
    }

    @Override
    protected boolean hasLoading() {
        return true;
    }

    private void closeKeyboard() {
        View view = getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
