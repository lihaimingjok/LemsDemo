package com.pcjz.lems.ui.workrealname.personinfo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.pcjz.lems.R;
import com.pcjz.lems.business.base.BaseFragment;
import com.pcjz.lems.business.common.view.DefineBAGRefreshWithLoadView;
import com.pcjz.lems.business.common.utils.MessageBus;
import com.pcjz.lems.business.common.utils.StringUtils;
import com.pcjz.lems.business.common.utils.TLog;
import com.pcjz.lems.business.config.AppConfig;
import com.pcjz.lems.business.constant.ResultStatus;
import com.pcjz.lems.business.constant.SysCode;
import com.pcjz.lems.business.context.AppContext;
import com.pcjz.lems.business.entity.person.PersonEntity;
import com.pcjz.lems.business.storage.SharedPreferencesManager;
import com.pcjz.lems.business.webapi.MainApi;
import com.pcjz.lems.ui.workrealname.personinfo.adapter.PersonInfoAdapter;
import com.squareup.otto.BasicBus;
import com.squareup.otto.Subscribe;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.refreshlayout.BGARefreshLayout;

import static com.pcjz.lems.business.base.BaseApplication.mContext;


/**
 * Created by Greak on 2017/9/14.
 */

public class PersonCheckFragment extends BaseFragment implements BGARefreshLayout.BGARefreshLayoutDelegate{

    private BGARefreshLayout mRefreshLayout;
    private RecyclerView mRecyclerView;

    private RelativeLayout rlNoData;
    private ImageView ivNoData;
    private TextView tvNoData, tvLoad;
    private LinearLayout mLayout;

    List<PersonEntity> tempDataList = new ArrayList<>();
    List<PersonEntity> mmDatas = new ArrayList<>();
    private PersonInfoAdapter mAdapter;
    private int currentPage = 1;
    private int maxPage = 1;
    private int tipNums = 0;

    public BasicBus mBasicBus = MessageBus.getBusInstance();

    private String mUserId;
    private String mProjectId;
    private DefineBAGRefreshWithLoadView mDefineBAGRefreshWithLoadView;

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_person_check;
    }

    @Override
    protected void initView(View view) {
        mRefreshLayout = (BGARefreshLayout) view.findViewById(R.id.refresh_layout);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        rlNoData = (RelativeLayout) view.findViewById(R.id.rl_no_data);
        ivNoData = (ImageView) view.findViewById(R.id.iv_no_data);
        tvNoData = (TextView) view.findViewById(R.id.tv_no_data);
        tvLoad = (TextView) view.findViewById(R.id.tv_again_loading);
        tvLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                firstLoadData(1);
            }
        });
        mUserId = SharedPreferencesManager.getString(ResultStatus.USER_ID);
        mProjectId = SharedPreferencesManager.getString(mUserId + SysCode.PROJECTPERIODID);
        initNoDataView();
        initBGARefreshLayout();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBasicBus.register(this);
    }

    private void initNoData() {
        if (rlNoData == null) return;
        if (rlNoData != null && rlNoData.getVisibility() != View.VISIBLE) {
            rlNoData.setVisibility(View.VISIBLE);
        }
        if (mRecyclerView != null && mRecyclerView.getVisibility() == View.VISIBLE) {
            mRecyclerView.setVisibility(View.GONE);
        }
    }

    //有网无数据
    private void initNoDataView() {
        if (ivNoData == null) return;
        rlNoData.setBackground(ContextCompat.getDrawable(getActivity(), R.color.white));
        ivNoData.setImageResource(R.drawable.pop_window_choose_no_content);
        tvLoad.setVisibility(View.GONE);
        tvNoData.setText(AppContext.mResource.getString(R.string.person_no_data));
    }

    //网络异常
    private void initNoInternetView() {
        if (ivNoData == null) return;
        ivNoData.setImageResource(R.drawable.no_internet_icon);
        tvNoData.setText(AppContext.mResource.getString(R.string.please_check_network));
        tvLoad.setVisibility(View.VISIBLE);

    }



    private void initBGARefreshLayout() {
        // 为BGARefreshLayout 设置代理
        mRefreshLayout.setDelegate(this);
        mDefineBAGRefreshWithLoadView = new DefineBAGRefreshWithLoadView(mContext, true, true);
        //设置刷新样式
        mRefreshLayout.setRefreshViewHolder(mDefineBAGRefreshWithLoadView);
        mAdapter = new PersonInfoAdapter(getActivity(), mmDatas);
        mRecyclerView.setAdapter(mAdapter);
        //设置listview垂直如何显示
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter.setOnItemClickListener(new PersonInfoAdapter.OnItemClickListener() {
            @Override
            public void setOnClick(View holder, int position) {
                PersonEntity personEntity = new PersonEntity();
                personEntity = mmDatas.get(position);
                if(personEntity.getCheckType() == 0){
                    Intent intent = new Intent(getActivity(), PersonInfoCheckActivity.class);
                    intent.putExtra("pid", personEntity.getUserId());
                    intent.putExtra("mState", "1002");
                    startActivity(intent);
                }else{
                    Intent intent = new Intent(getActivity(), PersonInfoDetailActivity.class);
                    intent.putExtra("pid", personEntity.getUserId());
                    intent.putExtra("pstate", "8002");
                    startActivity(intent);
                }

            }
        });
        mAdapter.setOnDialListener(new PersonInfoAdapter.onDialListener(){
            @Override
            public void onDialPhone(View holder, int position) {
                String mPhone = mmDatas.get(position).getPhoneNum();
                Intent intent2 = new Intent("android.intent.action.DIAL", Uri.parse("tel:"+mPhone));
                startActivity(intent2);
            }
        });
        mmDatas.clear();
        firstLoadData(1);

    }


    private void firstLoadData(final int currentPage)  {
        HttpEntity entity = null;
        JSONObject pData = new JSONObject();
        JSONObject obj = new JSONObject();
        try {
            obj.put("isVerifyed", "0");
            obj.put("projectId", mProjectId);
            pData.put("params", obj);
            pData.put("pageNo", currentPage);
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
                        tipNums = obj.getJSONObject("data").getInt("noVerifySize");
                        maxPage = Integer.parseInt(tempMax);
                        maxPage = Integer.parseInt(tempMax);
                        rlNoData.setVisibility(View.GONE);
                        PersonEntity bean;

                        if(Integer.parseInt(tempMax) > 0) {
                            if (mRecyclerView != null && mRecyclerView.getVisibility() != View.VISIBLE) {
                                mRecyclerView.setVisibility(View.VISIBLE);
                            }
                            if (rlNoData != null && rlNoData.getVisibility() == View.VISIBLE) {
                                rlNoData.setVisibility(View.GONE);
                            }
                            for (int j = 0; j < tempList.length(); j++) {
                                JSONObject itemObj = tempList.getJSONObject(j);
                                bean = new PersonEntity();
                                bean.setUserId(itemObj.getString("id"));

                                if (!itemObj.isNull("empName")) {
                                    bean.setJobName(itemObj.getString("empName"));
                                }
                                if (!itemObj.isNull("jobTypeName")) {
                                    bean.setJobType(itemObj.getString("jobTypeName"));
                                }
                                if (!itemObj.isNull("verifyed")) {
                                    bean.setCheckType(Integer.parseInt(itemObj.getString("verifyed")));
                                }
                                if (!itemObj.isNull("empPhone")) {
                                    bean.setPhoneNum(itemObj.getString("empPhone"));
                                }
                                if (!itemObj.isNull("facephoto")) {
                                    bean.setAvater(itemObj.getString("facephoto"));
                                }

                                mmDatas.add(bean);
                            }
                            mAdapter.notifyDataSetChanged();
                        }else{
                            mmDatas.clear();
                            mAdapter.notifyDataSetChanged();
                            initNoDataView();
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
                initNoData();
                initNoInternetView();
            }
        });
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            switch (what) {
                case 0:
                    currentPage = 1;
                    mmDatas.clear();
                    setRefresh();
                    mAdapter.notifyDataSetChanged();
                    mRefreshLayout.endRefreshing();
                    break;
                case 1:
                    setLoadMore();
                    mAdapter.notifyDataSetChanged();
                    mRefreshLayout.endLoadingMore();
                    break;
                case 2:
                    mRefreshLayout.endLoadingMore();
                    break;
                default:
                    break;
            }
        }
    };

    private void setLoadMore() {
        firstLoadData(currentPage);
    }

    private void setRefresh() {
        firstLoadData(1);
    }

    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {
        mDefineBAGRefreshWithLoadView.updateLoadingMoreText("加载中...");
        mDefineBAGRefreshWithLoadView.showLoadingMoreImg();
        //下拉加载最新数据
        currentPage = 1;
        handler.sendEmptyMessageDelayed(0, 2000);
    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
        //上拉加载更多数据
        currentPage ++;
        if (currentPage > maxPage) {
            mDefineBAGRefreshWithLoadView.updateLoadingMoreText("没有更多数据啦");
            mDefineBAGRefreshWithLoadView.hideLoadingMoreImg();
            handler.sendEmptyMessageDelayed(2 , 2000);
            return true;
        }
        handler.sendEmptyMessageDelayed(1, 2000);
        return true;
    }

    @Subscribe
    public void excuteAction(String action){
        if (SysCode.CHANGE_PROJECT_PERIOD.equals(action)) {
            String projectId = SharedPreferencesManager.getString(mUserId + SysCode.PROJECTPERIODID);
            /*if (!StringUtils.equals(projectId, mProjectId)) {*/
                mmDatas.clear();
                mAdapter.notifyDataSetChanged();
                maxPage = 1;
                mProjectId = projectId;
                firstLoadData(1);
            /*}*/
        }else{
            maxPage = 1;
            mmDatas.clear();
            mAdapter.notifyDataSetChanged();
            firstLoadData(1);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mBasicBus.unregister(this);
    }
}
