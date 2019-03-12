package com.pcjz.lems.business.base;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import com.pcjz.lems.business.config.AppConfig;

import java.util.ArrayList;
import java.util.List;


public abstract class BaseListFragment extends BaseFragment {

    protected static final int STATE_NONE = 0;
    protected static final int STATE_REFRESH = 1;
    protected static final int STATE_LOADMORE = 2;
    private int mState = STATE_NONE;
    private int mCurrentPage = 0;
    private int mVisibleLastIndex = 0;
    private int mVisibleItemCount;

    public abstract BaseListAdapter getAdapter();

    public abstract AbsListView getListView();

    public abstract void requestList(int page);

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        initListView();
        return view;
    }

    private void initListView() {
        getListView().setOnScrollListener(mScrollListener);
        getAdapter().setOnNetErrorListener(new BaseListAdapter.OnNetErrorListener() {
            @Override
            public void onNetError() {
                loadMore();
            }
        });
    }

    public AbsListView.OnScrollListener mScrollListener = new AbsListView.OnScrollListener() {

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem,
                             int visibleItemCount, int totalItemCount) {
            if (mState == STATE_NONE
                    && getAdapter() != null
                    && getAdapter().getState() == BaseListAdapter.STATE_LOAD_MORE
                    && getListView().getLastVisiblePosition() == (getListView()
                    .getCount() - 1)) {
                mVisibleItemCount = visibleItemCount;
                mVisibleLastIndex = firstVisibleItem + visibleItemCount - 1;
                loadMore();
                showPageToast();
            }
            boolean enable = false;
            if (getListView() != null && getListView().getChildCount() > 0) {
                // check if the first item of the list is visible
                boolean firstItemVisible = getListView().getFirstVisiblePosition() == 0;
                // check if the top of the first item is visible
                boolean topOfFirstItemVisible = getListView().getChildAt(0).getTop() == 0;
                // enabling or disabling the refresh layout
                enable = firstItemVisible && topOfFirstItemVisible;
            }
            setSwipeRefresh(enable);
        }
    };

    @SuppressLint("NewApi")
    public void executeOnLoadFinish(boolean isSuccess, ArrayList list) {
        synchronized (this) {

            if (mCurrentPage == 0)
                //第一次设置状态，之后刷新
                mState = STATE_REFRESH;
            if (mState == STATE_REFRESH) {
                if (isSuccess) {
                    if (list != null && list.size() > 0) {
                        mCurrentPage = 1;// 刷新成功
                        if (getAdapter() != null) {
                            getAdapter().clear();
                        }
                        getAdapter().setData(list);
                        if (getListView().getAdapter() == null)
                            getListView().setAdapter(getAdapter());
                        if (isCanLoadMore()) {
                            if (list.size() >= AppConfig.pageSize) {
                                getAdapter().setState(
                                        BaseListAdapter.STATE_LOAD_MORE);
                                //mState = STATE_LOADMORE;
                                sendReloadBroadcast(-1);
                            } else {
                                getAdapter().setState(
                                        BaseListAdapter.STATE_LESS_ONE_PAGE);
                                sendReloadBroadcast(list.size());
                            }
                        } else {
                            getAdapter().setState(
                                    BaseListAdapter.STATE_LESS_ONE_PAGE);
                        }
                        hideLoading();
                        requestFinish();
                    } else {
                        if (getAdapter() != null) {
                            getAdapter().clear();
                        }
                        loadError("当前无数据", false);
                    }
                } else {
                    loadError("加载失败，点击重试", true);
                }
                mState = STATE_NONE;
            } else if (mState == STATE_LOADMORE) {
                if (isSuccess) {
                    if (list == null || list.size() == 0) {
                        List data = getAdapter().getData();
                        if (data != null && data.size() > 0) {
                            getAdapter().setState(BaseListAdapter.STATE_NO_MORE);
                        }
                    } else {
                        List data = getAdapter().getData();
                        if ((list.size() > 0 && list.size() < AppConfig.pageSize) || isFinished()) {
                            getAdapter().setState(BaseListAdapter.STATE_NO_MORE);
                            showPageToast();
                            sendReloadBroadcast(data.size());
                        } else {
                            getAdapter().setState(BaseListAdapter.STATE_LOAD_MORE);
                            sendReloadBroadcast(-1);
                        }
                        getAdapter().addData(list);
                        getListView().setSelection(
                                mVisibleLastIndex - mVisibleItemCount + 1);
                        mCurrentPage = mCurrentPage + 1;// 加载成功
                    }
                } else {
                    getAdapter().setState(BaseListAdapter.STATE_NETWORK_ERROR);
                }
                requestFinish();
                mState = STATE_NONE;
            }
        }

    }

    protected void sendReloadBroadcast(int totalSize) {

    }

    @Override
    public void initWebData() {
        mState = STATE_REFRESH;
        requestList(1);
    }

    @Override
    public void refreshWebData() {
        super.refreshWebData();
        mState = STATE_REFRESH;
        requestList(1);
    }

    public void loadMore() {
        mState = STATE_LOADMORE;
        requestList(mCurrentPage + 1);
    }

    public void setCurrentPage(int currentPage) {
        mCurrentPage = currentPage + 1;
        mState = STATE_NONE;
        getAdapter().setState(BaseListAdapter.STATE_LOAD_MORE);
    }

    @Override
    protected boolean isCanPtr() {
        return true;
    }

    @Override
    protected boolean hasLoading() {
        return true;
    }

    protected boolean isCanLoadMore() {
        return true;
    }

    protected void showPageToast() {

    }

    protected boolean isFinished() {
        return false;
    }

    protected void setSwipeRefresh(boolean Enable) {

    }
}
