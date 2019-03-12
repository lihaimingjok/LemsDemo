package com.pcjz.lems.ui.workrealname.projectsituation;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.pcjz.lems.R;
import com.pcjz.lems.business.base.BaseActivity;
import com.pcjz.lems.business.common.utils.StringUtils;
import com.pcjz.lems.business.common.utils.TLog;
import com.pcjz.lems.business.common.view.DefineBAGRefreshView;
import com.pcjz.lems.business.config.AppConfig;
import com.pcjz.lems.business.constant.SysCode;
import com.pcjz.lems.business.webapi.MainApi;
import com.pcjz.lems.business.widget.lineChart.LineChartLayout;
import com.pcjz.lems.ui.workrealname.projectsituation.adapter.WarnmsgAdapter;
import com.pcjz.lems.ui.workrealname.projectsituation.bean.WarnMsgEntity;

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
 * Created by Greak on 2017/9/23.
 */

public class WarnmsgHistoryActivity extends BaseActivity implements View.OnClickListener,  BGARefreshLayout.BGARefreshLayoutDelegate{

    private TextView tvTitle;

    private RelativeLayout rlRealtime;
    private LineChartLayout mInclude;

    private String mProjectId = "";
    private String mUserId;

    private BGARefreshLayout mRefreshLayout;
    private RecyclerView mRecyclerView;
    private List<WarnMsgEntity> mWarnMsgList = new ArrayList<>();
    private WarnmsgAdapter mWarnAdapter;
    private int curPage = 1;
    private int maxPage = 1;

    private RelativeLayout rlNoData;
    private ImageView ivNoData;
    private TextView tvNoData;

    @Override
    protected void initView() {
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvTitle.setText("历史消息");

        mRefreshLayout = (BGARefreshLayout) findViewById(R.id.refresh_layout);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        mProjectId = getIntent().getStringExtra("selectedProId");

        rlNoData = (RelativeLayout) findViewById(R.id.rl_no_data);
        ivNoData = (ImageView) findViewById(R.id.iv_no_data);
        tvNoData = (TextView) findViewById(R.id.tv_no_data);

        initNoDataView();
        initBGARefreshLayout();
    }

    private void initNoDataView() {
        if (ivNoData == null) return;
        rlNoData.setBackground(ContextCompat.getDrawable(this, R.color.white));
        ivNoData.setImageResource(R.drawable.pop_window_choose_no_content);
        tvNoData.setText("暂无预警消息");
    }

    private void initBGARefreshLayout() {
        // 为BGARefreshLayout 设置代理
        mRefreshLayout.setDelegate(this);
        //设置刷新样式
        mRefreshLayout.setRefreshViewHolder(new DefineBAGRefreshView(mContext, true, true));
        mWarnAdapter = new WarnmsgAdapter(this, mWarnMsgList);
        mRecyclerView.setAdapter(mWarnAdapter);
        //设置listview垂直如何显示
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mWarnAdapter.setOnItemClickListener(new WarnmsgAdapter.OnItemClickListener() {
            @Override
            public void setOnClick(View holder, int position) {
                WarnMsgEntity mWarnmsgEntity = new WarnMsgEntity();
                mWarnmsgEntity = mWarnMsgList.get(position);
                Intent intent = new Intent(WarnmsgHistoryActivity.this, WarnmsgDetailActivity.class);
                intent.putExtra("wmDev", mWarnmsgEntity.getWarnDevice());
                intent.putExtra("wmCnt", mWarnmsgEntity.getWarnContent());
                intent.putExtra("wmTime", mWarnmsgEntity.getWarnDate());
                startActivity(intent);

            }
        });

        getWarnMessageList(1);

    }

    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {
        //下拉加载最新数据
        curPage = 1;
        handler.sendEmptyMessageDelayed(0, 2000);
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            switch (what) {
                case 0:
                    curPage = 1;
                    mWarnMsgList.clear();
                    setRefresh();
                    mWarnAdapter.notifyDataSetChanged();
                    mRefreshLayout.endRefreshing();
                    break;
                case 1:
                    setLoadMore();
                    mWarnAdapter.notifyDataSetChanged();
                    mRefreshLayout.endLoadingMore();
                    break;
            }
        }
    };

    private void setLoadMore() {
        getWarnMessageList(curPage);
    }

    private void setRefresh() {
        getWarnMessageList(1);
    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
        //上拉加载更多数据
        curPage ++;
        if (curPage > maxPage) {
            return false;
        }
        handler.sendEmptyMessageDelayed(1, 2000);
        return true;
    }

    private void getWarnMessageList(int currutPage){

        HttpEntity entity = null;
        JSONObject pData = new JSONObject();
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("projectId", mProjectId);
            pData.put("params", paramObj);
            pData.put("pageNo", currutPage);
            pData.put("pageSize", 10);
            entity = new StringEntity(pData.toString(), "UTF-8");
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        MainApi.requestCommon(this, AppConfig.WARN_MSG_LIST, entity, new AsyncHttpResponseHandler() {


            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String httpResult = new String(bytes);
                TLog.log("warnMsgHistory -->"+httpResult);
                try {
                    JSONObject obj = new JSONObject(httpResult);
                    if(StringUtils.equals(obj.getString("code"), SysCode.SUCCESS)){
                        JSONArray tempList = obj.getJSONObject("data").getJSONArray("results");
                        String tempMax =  obj.getJSONObject("data").getString("totalPage");
                        maxPage = Integer.parseInt(tempMax);
                        WarnMsgEntity bean;
                        if(maxPage > 0 ){
                            if(curPage == 1){
                                mWarnMsgList.clear();
                            }
                            for(int j =0; j < tempList.length(); j ++){
                                JSONObject itemObj = tempList.getJSONObject(j);
                                bean = new WarnMsgEntity();
                                if(!itemObj.isNull("deviceName")){
                                    bean.setWarnDevice(itemObj.getString("deviceName"));
                                }
                                if(!itemObj.isNull("warnContent")){
                                    bean.setWarnContent(itemObj.getString("warnContent"));
                                }
                                if(!itemObj.isNull("warnTime")){
                                    bean.setWarnDate(itemObj.getString("warnTime"));
                                }
                                mWarnMsgList.add(bean);
                            }
                            mWarnAdapter.notifyDataSetChanged();
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
    public void onClick(View view) {

    }

    @Override
    public void setView() {
        setContentView(R.layout.activity_warnmsg_history);
    }

    @Override
    public void setListener() {

    }
}
