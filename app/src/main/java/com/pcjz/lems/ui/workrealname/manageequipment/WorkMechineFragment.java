package com.pcjz.lems.ui.workrealname.manageequipment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.pcjz.lems.R;
import com.pcjz.lems.business.base.BaseFragment;
import com.pcjz.lems.business.base.basebean.BaseData;
import com.pcjz.lems.business.common.utils.MessageBus;
import com.pcjz.lems.business.common.utils.StringUtils;
import com.pcjz.lems.business.common.utils.TLog;
import com.pcjz.lems.business.common.view.DefineBAGRefreshView;
import com.pcjz.lems.business.common.view.DefineBAGRefreshWithLoadView;
import com.pcjz.lems.business.common.view.RecyclerViewDivider;
import com.pcjz.lems.business.config.AppConfig;
import com.pcjz.lems.business.constant.ResultStatus;
import com.pcjz.lems.business.constant.SysCode;
import com.pcjz.lems.business.context.AppContext;
import com.pcjz.lems.business.storage.SharedPreferencesManager;
import com.pcjz.lems.business.webapi.MainApi;
import com.pcjz.lems.ui.workrealname.manageequipment.adapter.WorkMechineAdapter;
import com.pcjz.lems.ui.workrealname.manageequipment.bean.MechineBean;
import com.pcjz.lems.ui.workrealname.manageequipment.bean.WorkMechineBean;
import com.squareup.otto.BasicBus;
import com.squareup.otto.Subscribe;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.refreshlayout.BGARefreshLayout;

import static com.pcjz.lems.business.base.BaseApplication.mContext;

/**
 * created by yezhengyu on 2017/9/14 16:37
 */

public class WorkMechineFragment extends BaseFragment implements BGARefreshLayout.BGARefreshLayoutDelegate {

    private BGARefreshLayout mRefreshLayout;
    private RecyclerView mRecyclerView;
    private RelativeLayout rlNoData;
    private ImageView ivNoData;
    private TextView tvNoData;
    private TextView tvLoad;

    List<WorkMechineBean> mWorkMechines = new ArrayList<>();
    private WorkMechineAdapter mAdapter;
    private int currentPage = 1;
    private int totalPage;
    private String mUserId;
    private String mProjectId;
    /**
     * eventBus消息传递
     */
    protected BasicBus mBasicBus = MessageBus.getBusInstance();
    private DefineBAGRefreshWithLoadView mDefineRefreshWithLoadView;

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_work_mechine;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBasicBus.register(this);
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
                refreshWebData();
            }
        });
        mUserId = SharedPreferencesManager.getString(ResultStatus.USER_ID);
        mProjectId = SharedPreferencesManager.getString(mUserId + SysCode.PROJECTPERIODID);
        initNoDataView();
        initBGARefreshLayout();
    }

    private void initBGARefreshLayout() {
        // 为BGARefreshLayout 设置代理
        mRefreshLayout.setDelegate(this);
        mDefineRefreshWithLoadView = new DefineBAGRefreshWithLoadView(mContext, true, true);
        //设置刷新样式
        mRefreshLayout.setRefreshViewHolder(mDefineRefreshWithLoadView);
        mAdapter = new WorkMechineAdapter(getActivity(), mWorkMechines);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_work_mechine_header, null);
        mAdapter.addHeaderView(view);
        mRecyclerView.setAdapter(mAdapter);
        //设置listview垂直如何显示
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.addItemDecoration(new RecyclerViewDivider(
                getContext(), LinearLayoutManager.HORIZONTAL, 1, getResources().getColor(R.color.txt_gray_divider)));
        mAdapter.setOnItemClickListener(new WorkMechineAdapter.OnItemClickListener() {
            @Override
            public void setOnClick(View holder, int position) {
                //跳转到考勤机信息页
                Intent intent = new Intent(getActivity(), WorkMechineInfoActivity.class);
                intent.putExtra("type", "detail");
                intent.putExtra("id", mWorkMechines.get(position).getId());
                startActivity(intent);
            }
        });
        refreshWebData();
    }

    @Subscribe
    public void excuteAction(String action) {
        if (SysCode.CHANGE_PROJECT_PERIOD.equals(action)) {
            String projectId = SharedPreferencesManager.getString(mUserId + SysCode.PROJECTPERIODID);
            if (!StringUtils.equals(projectId, mProjectId)) {
                mProjectId = projectId;
                refreshWebData();
            }
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
        }
    }

    //有网无数据
    private void initNoDataView() {
        if (ivNoData == null) return;
        rlNoData.setBackground(ContextCompat.getDrawable(getActivity(), R.color.white));
        ivNoData.setImageResource(R.drawable.pop_window_choose_no_content);
        tvNoData.setText(AppContext.mResource.getString(R.string.work_mechine_no_data));
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
        //请求考勤机列表
        JSONObject obj = new JSONObject();
        HttpEntity entity = null;
        try {
            JSONObject obj0 = new JSONObject();
            obj.put("pageNo", currentPage);
            obj.put("pageSize", 10);
            obj0.put("projectId", mProjectId);
            obj.put("params", obj0);
            entity = new StringEntity(obj.toString(), "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        MainApi.requestCommon(getActivity(), AppConfig.GET_WORK_MECHINE_LIST, entity, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                try {
                    String httpResult = new String(bytes);
                    TLog.log("getWorkMechineList : " + httpResult);
                    Type type = new TypeToken<BaseData<MechineBean>>() {
                    }.getType();
                    BaseData<MechineBean> datas = new Gson().fromJson(httpResult, type);
                    if (StringUtils.equals(datas.getCode(), ResultStatus.SUCCESS)) {
                        List<WorkMechineBean> results = datas.getData().results;
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
                                        mWorkMechines.clear();
                                    }
                                    mWorkMechines.addAll(results);
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

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
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
        mDefineRefreshWithLoadView.updateLoadingMoreText("加载中...");
        mDefineRefreshWithLoadView.showLoadingMoreImg();
        handler.sendEmptyMessageDelayed(0, 1000);
    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
        if (currentPage >= totalPage) {
            mDefineRefreshWithLoadView.updateLoadingMoreText("没有更多数据啦");
            mDefineRefreshWithLoadView.hideLoadingMoreImg();
            handler.sendEmptyMessageDelayed(2 , 1000);
            return true;
        }
        //上拉加载更多数据
        handler.sendEmptyMessageDelayed(1, 1000);
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            mBasicBus.unregister(getActivity());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
