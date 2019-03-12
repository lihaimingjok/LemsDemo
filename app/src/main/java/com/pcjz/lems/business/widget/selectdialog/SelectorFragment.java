package com.pcjz.lems.business.widget.selectdialog;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pcjz.lems.R;
import com.pcjz.lems.business.base.BaseFragment;
import com.pcjz.lems.business.common.utils.StringUtils;
import com.pcjz.lems.business.common.utils.TLog;
import com.pcjz.lems.business.constant.ResultStatus;
import com.pcjz.lems.business.constant.SysCode;
import com.pcjz.lems.business.context.AppContext;
import com.pcjz.lems.business.storage.SharedPreferencesManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pc on 2017/5/2.
 */

public class SelectorFragment extends BaseFragment {
    private ListView lvContent;
    private RelativeLayout rlNoData;
    private TextView tvNoData;
    private ImageView ivNoData;
    private SelectorAdapter adapter;
    private boolean mHasLoadedOnce = false;

    private int mPosition;
    private Handler handler;
    private static int count = 0;
    private static int currentPosition = 0;
    private int mSelectCount = 0;
    private List<SelectInfo> mSelectInfoList = null;
    private boolean mHasReviewOnce = false;

    private String title;

    private int position_fragment = 0;
    private SelectorDialog selectorDialog;
    private String mLabel;

    public SelectorFragment()
    {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub  
        super.onCreate(savedInstanceState);
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    @Override
    protected void initView(View view) {
        lvContent = (ListView) view.findViewById(R.id.lv_content);
        //by zhengyuye
        rlNoData = (RelativeLayout) view.findViewById(R.id.rl_no_data);
        tvNoData = (TextView) view.findViewById(R.id.tv_no_data);
        ivNoData = (ImageView) view.findViewById(R.id.iv_selector_no_data);

        Bundle bundle = getArguments();
        mPosition = bundle.getInt("position");
        mSelectCount = bundle.getInt("selectCount");

        try {
            if (adapter == null) {
                if (selectorDialog != null && selectorDialog.getmSelectName() != null
                        && selectorDialog.getmSelectName().size() > mPosition
                        && selectorDialog.getmSelectId().size() > mPosition) {
                    adapter = new SelectorAdapter(mSelectInfoList,
                            selectorDialog.getmSelectName().get(mPosition),
                            selectorDialog.getmSelectId().get(mPosition));
                } else {
                    adapter = new SelectorAdapter(mSelectInfoList,
                            null,
                            null);
                }
            }

            if (lvContent != null) {
                lvContent.setAdapter(adapter);
            }

            refreshView(view);

            if (mSelectInfoList != null) {
                reviewData();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_selector;
    }

    @Override
    protected void setListener() {
        super.setListener();
        lvContent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mSelectInfoList.get(position) == null) return;

                sendMsg(0, position);
            }
        });
    }

    private void sendMsg(int what, int position) {
        try {
            Message message = new Message();
            message.what = what;
            message.arg1 = mPosition;
            message.arg2 = position;
            SelectInfo selectInfo = new SelectInfo();
            if (!StringUtils.isEmpty(mSelectInfoList.get(position).getName())) {
                selectInfo.setName(mSelectInfoList.get(position).getName());
            }
            if (!StringUtils.isEmpty(mSelectInfoList.get(position).getId())) {
                selectInfo.setId(mSelectInfoList.get(position).getId());
            }
            message.obj = selectInfo;
            handler.sendMessage(message);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public int reviewData() {
        String selectName = null;
        String selectId = null;
        SelectInfo selectInfo = null;
        int position = -1;
        int first = -1;
        String listName = "";
        String selectedName = "";
        int initPosition = 0;
        int iRet = -1;
        try {
            if (mHasReviewOnce) {
                iRet = -2;
                return iRet;
            } else {
                mHasReviewOnce = true;
            }

            if (mSelectInfoList != null && mSelectInfoList.size() > 0) {
                if (selectorDialog.getmSelectId() != null && selectorDialog.getmSelectId().size() > mPosition) {
                    for (int i = 0; i < mSelectInfoList.size(); i++) {
                        selectInfo = mSelectInfoList.get(i);

                        listName = selectInfo.getName() + selectInfo.getId();

                        for (int index = 0; index < selectorDialog.getmSelectId().get(mPosition).size(); index++) {
                            selectName = selectorDialog.getmSelectName().get(mPosition).get(index);
                            selectId = selectorDialog.getmSelectId().get(mPosition).get(index);

                            selectedName = selectName + selectId;

                            if (listName != null && selectedName != null && listName.compareTo(selectedName) == 0) {
                                if (first == -1) {
                                    first = i;

                                    if (adapter != null) {
                                        if (selectorDialog != null && selectorDialog.getmSelectName().size() > mPosition
                                                && selectorDialog.getmSelectId().size() > mPosition) {
                                            adapter.setSelectedNames(selectorDialog.getmSelectName().get(mPosition));
                                            adapter.setSelectedIds(selectorDialog.getmSelectId().get(mPosition));
                                        }

                                        adapter.notifyDataSetChanged();

                                        hideLoading();
                                    }

                                    sendMsg(2, first);

                                    mHasReviewOnce = true;

                                    iRet = 0;

                                    return iRet;
                                }
                            }
                        }
                    }
                }
                else
                {
                    iRet = -5;
                }

                if (adapter != null) {
                    adapter.notifyDataSetChanged();

                    hideLoading();
                }
            }
            else
            {
                iRet = -4;
            }

            mHasReviewOnce = false;
        } catch (Exception ex) {
            ex.printStackTrace();
            iRet = -3;
        }

        return iRet;
    }

    //
    public boolean onClickSelectedData(int selectPosition, String fragmentMode) {
        String selectId = null;
        String selectName = null;
        boolean bSelected = false;

        try {
            if (mSelectInfoList != null && mSelectInfoList.size() > selectPosition) {
                selectId = mSelectInfoList.get(selectPosition).getId();
                selectName = mSelectInfoList.get(selectPosition).getName();
            }

            onSelectedItemClick(selectId, selectName, fragmentMode);

            if (SelectorDialog.MULTI_MODE.compareTo(fragmentMode) == 0)   // 复选Fragment
            {
                if (mPosition == 0) // 当为第一个复选时
                {
                    if (selectorDialog.getFragmentSize() > 0) {
                        ((SelectorFragment) selectorDialog.getFragments().get(selectorDialog.getFragmentSize() - 1)).onSelectedItemClick(selectId,
                                selectName, SelectorDialog.MULTI_MODE);

                        bSelected = ((SelectorFragment) selectorDialog.getFragments().get(mPosition)).onItemClick(selectId, selectName);
                    }
                } else if (mPosition == selectorDialog.getFragmentSize() - 1)  // 当为复选并且是最后一层级时
                {
                    if (selectorDialog.getFragmentSize() > 0) {
                        ((SelectorFragment) selectorDialog.getFragments().get(0)).onSelectedItemClick(selectId,
                                selectName, SelectorDialog.MULTI_MODE);

                        bSelected = ((SelectorFragment) selectorDialog.getFragments().get(0)).onItemClick(selectId, selectName);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return bSelected;
    }

    public void onReviewSelectedData(int selectPosition, String fragmentMode) {
        try {
            if (selectorDialog.getmSelectId() != null) {
                // 初始化情况下，选择器里面已选内容未初始化，需要进行初始化操作
                if (selectorDialog.getmSelectId().size() == mPosition) {
                    selectorDialog.getmSelectId().add(new ArrayList());
                }
                if (selectorDialog.getmSelectName().size() == mPosition) {
                    selectorDialog.getmSelectName().add(new ArrayList());
                }

                if (SelectorDialog.SINGLE_MODE.compareTo(fragmentMode) == 0) {
                    if (selectorDialog.getmSelectId().size() > mPosition) {
                        if (selectorDialog.getmSelectId().get(mPosition).size() <= 0
                                || !StringUtils.equals(selectorDialog.getmSelectId().get(mPosition).get(0),
                                mSelectInfoList.get(selectPosition).getId())) {
                            selectorDialog.getmSelectId().get(mPosition).clear();

                            selectorDialog.getmSelectId().get(mPosition).add(mSelectInfoList.get(selectPosition).getId());
                        }
                    }
                    if (selectorDialog.getmSelectName().size() > mPosition) {
                        if (selectorDialog.getmSelectName().get(mPosition).size() <= 0
                                || !StringUtils.equals(selectorDialog.getmSelectName().get(mPosition).get(0),
                                mSelectInfoList.get(selectPosition).getName())) {
                            selectorDialog.getmSelectName().get(mPosition).clear();

                            selectorDialog.getmSelectName().get(mPosition).add(mSelectInfoList.get(selectPosition).getName());
                        }
                    }
                } else {
                    if (selectorDialog.getmSelectId().size() > mPosition) {
                        if (selectorDialog.getmSelectId().get(mPosition) != null
                                && selectorDialog.getmSelectId().get(mPosition).size() >= 0) {
                            if (!selectorDialog.getmSelectId().get(mPosition).contains(mSelectInfoList.get(selectPosition).getId())) {
                                selectorDialog.getmSelectId().get(mPosition).add(mSelectInfoList.get(selectPosition).getId());
                            }
                        }
                    }
                    if (selectorDialog.getmSelectName().size() > mPosition) {
                        if (selectorDialog.getmSelectName().get(mPosition) != null
                                && selectorDialog.getmSelectName().get(mPosition).size() >= 0) {
                            if (!selectorDialog.getmSelectName().get(mPosition).contains(mSelectInfoList.get(selectPosition).getName())) {
                                selectorDialog.getmSelectName().get(mPosition).add(mSelectInfoList.get(selectPosition).getName());
                            }
                        }
                    }
                }
            }

            if (selectorDialog.getmSelectId().get(mPosition) != null && adapter != null) {
                adapter.setSelectedNames(selectorDialog.getmSelectName().get(mPosition));
                adapter.setSelectedIds(selectorDialog.getmSelectId().get(mPosition));
                adapter.notifyDataSetChanged();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        try {
            super.setUserVisibleHint(isVisibleToUser);
            if (isVisibleToUser && this.isVisible()) {
                //initWebData();
                //refreshView();
                if (isVisibleToUser && !mHasLoadedOnce) {
                    mHasLoadedOnce = true;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void refreshData() {
        try {
            if (adapter != null) {
                if (mSelectInfoList != null && mSelectInfoList.size() > 0) {
                    adapter.setData(mSelectInfoList);
                    adapter.notifyDataSetChanged();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void refreshView(View view) {
        try {
            if (mSelectInfoList != null) {
                if (rlNoData != null && rlNoData.getVisibility() == View.VISIBLE) {
                    rlNoData.setVisibility(View.GONE);
                }
                lvContent.setVisibility(View.VISIBLE);
                if (adapter != null) {
                    adapter.setData(mSelectInfoList);
                    adapter.notifyDataSetChanged();

                    hideLoading();
                }
            } else {
                if (StringUtils.equals(selectorDialog.getmType(), SelectorDialog.MULTI_MODE)) {
                    position_fragment = 1;
                } else {
                    position_fragment = 0;
                }
                if (mPosition == position_fragment) {
                    if (mLabel != null) {
                        if (mLabel.equals(SysCode.FAILURE_INTERNET)) {
                            hideLoading();
                            if (lvContent != null && lvContent.getVisibility() == View.VISIBLE) {
                                lvContent.setVisibility(View.GONE);
                            }
                            rlNoData.setVisibility(View.VISIBLE);
                            tvNoData.setText(R.string.please_check_network);
                            ivNoData.setImageResource(R.drawable.no_internet_icon);
                        } else {
                            if (StringUtils.equals(SharedPreferencesManager.getString(ResultStatus.NETWORKSTATE2), SysCode.NETWORKSTATE_OFF)) {
                                hideLoading();
                                if (lvContent != null && lvContent.getVisibility() == View.VISIBLE) {
                                    lvContent.setVisibility(View.GONE);
                                }
                                rlNoData.setVisibility(View.VISIBLE);
                                tvNoData.setText(R.string.no_data);
                                ivNoData.setImageResource(R.drawable.pop_window_choose_no_content);
                            } else {
                                initLoading(AppContext.mResource.getString(R.string.loading_view_loading), view);
                            }
                        }
                    } else {
                        initLoading(AppContext.mResource.getString(R.string.loading_view_loading), view);
                    }
                }

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected boolean hasLoading() {
        return true;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        setUserVisibleHint(true);
        super.onActivityCreated(savedInstanceState);
    }

    public void setData(List<SelectInfo> list, String label) {
        try {
            this.mSelectInfoList = list;
            this.mLabel = label;
            if (mSelectInfoList != null && mSelectInfoList.size() > 0) {
                if (adapter != null) {
                    adapter.setData(mSelectInfoList);
                    if (lvContent != null) {
                        lvContent.setAdapter(adapter);
                    }

                    adapter.notifyDataSetChanged();

                    hideLoading();
                }
            } else {
                hideLoading();
                if (StringUtils.equals(selectorDialog.getmType(), SelectorDialog.MULTI_MODE)) {
                    position_fragment = 1;
                } else {
                    position_fragment = 0;
                }
                if (mPosition == position_fragment) {
                    if (lvContent != null && lvContent.getVisibility() == View.VISIBLE) {
                        lvContent.setVisibility(View.GONE);
                    }
                    TLog.log("initView setData");
                    if (rlNoData != null) {
                        rlNoData.setVisibility(View.VISIBLE);
                        if (label.equals(SysCode.FAILURE_INTERNET)) {
                            tvNoData.setText(R.string.please_check_network);
                            ivNoData.setImageResource(R.drawable.no_internet_icon);
                        } else {
                            tvNoData.setText(R.string.no_data);
                            ivNoData.setImageResource(R.drawable.pop_window_choose_no_content);
                        }
                    }
                }
                //loadError(AppContext.mResource.getString(R.string.loading_view_no_data), false);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /*public void reviewData(ArrayList<SelectInfo> list) {
        this.mSelectInfoList = list;
        if (mSelectInfoList != null && mSelectInfoList.size() > 0) {
            if (adapter != null) {
                adapter.setData(mSelectInfoList);
                if (lvContent != null) {
                    lvContent.setAdapter(adapter);

                    initSelectedInfo();
                }

                adapter.notifyDataSetChanged();

                hideLoading();
            }
        } else {
            loadError(AppContext.mResource.getString(R.string.loading_view_no_data), false);
        }
    }*/

    public void clearData() {
        count = 0;
        currentPosition = 0;
        mSelectInfoList.clear();
    }

    public void clearDataRefreshView() {
        try {
            if (adapter == null) return;
            if (mSelectInfoList == null) return;
            adapter.notifyDataSetChanged();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public SelectorDialog getSelectorDialog() {
        return selectorDialog;
    }

    public void setSelectorDialog(SelectorDialog selectorDialog) {
        this.selectorDialog = selectorDialog;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    /* public void reloadData(String selectedName, String selectedId) {
         if (selectorDialog.getmSelectName().size() <= 0
                 || selectorDialog.getmSelectName().get(mPosition) == null) {
             selectorDialog.getmSelectName().add(mPosition, new ArrayList<String>());
         }
         if (selectorDialog.getmSelectId().size() <= 0
                 || selectorDialog.getmSelectId().get(mPosition) == null) {
             selectorDialog.getmSelectId().add(mPosition, new ArrayList<String>());
         }

         if(selectorDialog.getmSelectId().get(mPosition).contains(selectedId))
         {
             return;
         }
         else
         {
             selectorDialog.getmSelectId().get(mPosition).add(selectedId);
         }
         if(selectorDialog.getmSelectName().get(mPosition).contains(selectedName))
         {
             return;
         }
         else
         {
             selectorDialog.getmSelectName().get(mPosition).add(selectedName);
         }

         if(mSelectInfoList == null)
         {
             mSelectInfoList = new ArrayList<>();
         }

         boolean bExist = false;
         for(int index = 0; index < mSelectInfoList.size(); index ++)
         {
             if(selectedId != null && selectedId.compareTo(mSelectInfoList.get(index).getId()) == 0)
             {
                 bExist = true;
                 break;
             }
         }
         if(!bExist)
         {
             SelectInfo selectInfo = new SelectInfo();
             if (selectedName != null) {
                 selectInfo.setName(selectedName);
             }
             if (selectedId != null) {
                 selectInfo.setId(selectedId);
             }

             mSelectInfoList.add(selectInfo);
         }

         if (adapter != null) {
             adapter.setSelectedIds(selectorDialog.getmSelectId().get(mPosition));
             adapter.setSelectedNames(selectorDialog.getmSelectName().get(mPosition));
             adapter.setmSelectInfoList(mSelectInfoList);
             adapter.notifyDataSetChanged();
         }
     }

     public void removeData(String selectedName, String selectedId) {
         SelectInfo selectInfo;
         if(mSelectInfoList != null) {
             for (int i = 0; i < mSelectInfoList.size(); i++) {
                 selectInfo = mSelectInfoList.get(i);
                 if (selectInfo != null && StringUtils.equals(selectInfo.getId(), selectedId)) {
                     mSelectInfoList.remove(i);

                     if (selectedName != null) {
                         selectorDialog.getmSelectName().get(mPosition).remove(selectedName);
                     }
                     if (selectedId != null) {
                         selectorDialog.getmSelectId().get(mPosition).remove(selectedId);
                     }

                     if(adapter != null) {
                         adapter.setSelectedIds(selectorDialog.getmSelectId().get(mPosition));
                         adapter.setSelectedNames(selectorDialog.getmSelectName().get(mPosition));
                         adapter.setmSelectInfoList(mSelectInfoList);

                         adapter.notifyDataSetChanged();
                     }
                     return;
                 }
             }
         }
     }*/
    public boolean onItemClick(String selectedId, String selectedName) {
        SelectInfo selectInfo = null;
        boolean bExist = false;
        try {
            if (selectedId == null || selectedName == null) {
                return bExist;
            }

            if (mSelectInfoList != null) {
                for (int i = 0; i < mSelectInfoList.size(); i++) {
                    selectInfo = mSelectInfoList.get(i);
                    if (selectInfo != null && StringUtils.equals(selectInfo.getId(), selectedId)) {
                        bExist = true;
                        break;
                    }
                }
                if (!bExist) {
                    selectInfo = new SelectInfo();
                    if (selectedName != null) {
                        selectInfo.setName(selectedName);
                    }
                    if (selectedId != null) {
                        selectInfo.setId(selectedId);
                    }

                    mSelectInfoList.add(selectInfo);
                } else {
                    mSelectInfoList.remove(selectInfo);
                }

                if (adapter != null) {
                    adapter.setmSelectInfoList(mSelectInfoList);
                    adapter.notifyDataSetChanged();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return bExist;
    }

    public void onSelectedItemClick(String selectedId, String selectedName,
                                    String fragmentMode) {
        SelectInfo selectInfo = null;
        boolean bExist = false;
        try {
            if (selectedId == null || selectedName == null) {
                return;
            }

            List<String> selectorIds = null;
            List<String> selectorNames = null;

            if (selectorDialog.getmSelectId() != null) {
                // 如果数据加载异常，直接返回
                if (selectorDialog.getmSelectId().size() < mPosition
                        || selectorDialog.getmSelectName().size() < mPosition) {
                    return;
                }

                // 初始化情况下，选择器里面已选内容未初始化，需要进行初始化操作
                if (selectorDialog.getmSelectId().size() == mPosition) {
                    selectorIds = new ArrayList();
                    selectorDialog.getmSelectId().add(selectorIds);
                } else {
                    selectorIds = selectorDialog.getmSelectId().get(mPosition);
                }
                if (selectorDialog.getmSelectName().size() == mPosition) {
                    selectorNames = new ArrayList();
                    selectorDialog.getmSelectName().add(selectorNames);
                } else {
                    selectorNames = selectorDialog.getmSelectName().get(mPosition);
                }

                // 单选Fragment
                if (SelectorDialog.SINGLE_MODE.compareTo(fragmentMode) == 0) {
                    int last = mPosition + 1;

                    {
                        if (selectorIds.size() <= 0
                                || !StringUtils.equals(selectorIds.get(0), selectedId)) {
                            selectorIds.clear();

                            selectorIds.add(selectedId);
                        }

                        // 如果是多选的对话框，在多选内容选择器内，最后一个选择项可以不进行清空处理
                        last = mPosition + 1;
                        if (SelectorDialog.SINGLE_MODE.compareTo(selectorDialog.getmType()) == 0) {
                            last = selectorDialog.getmSelectId().size();
                        } else {
                            last = selectorDialog.getmSelectId().size() - 1;
                        }

                        for (int index = mPosition + 1; index < last; index++) {
                            selectorDialog.getmSelectId().get(index).clear();
                        }
                    }

                    {
                        if (selectorNames.size() <= 0
                                || !StringUtils.equals(selectorNames.get(0),
                                selectedName)) {
                            selectorNames.clear();

                            selectorNames.add(selectedName);
                        }

                        last = mPosition + 1;
                        if (SelectorDialog.SINGLE_MODE.compareTo(selectorDialog.getmType()) == 0) {
                            last = selectorDialog.getmSelectName().size();
                        } else {
                            last = selectorDialog.getmSelectName().size() - 1;
                        }

                        for (int index = mPosition + 1; index < last; index++) {
                            selectorDialog.getmSelectName().get(index).clear();
                        }
                    }
                } else   // 复选Fragment
                {
                    {
                        if (selectorIds != null
                                && selectorIds.size() >= 0) {
                            if (!selectorIds.contains(selectedId)) {
                                selectorIds.add(selectedId);
                            } else {
                                selectorIds.remove(selectedId);
                            }
                        }
                    }
                    {
                        if (selectorNames != null
                                && selectorNames.size() >= 0) {
                            if (!selectorNames.contains(selectedName)) {
                                selectorNames.add(selectedName);
                            } else {
                                selectorNames.remove(selectedName);
                            }
                        }
                    }
                }
            }

            if (adapter != null) {
                if (selectorDialog.getmSelectId() != null && selectorDialog.getmSelectId().size() > mPosition) {
                    adapter.setSelectedIds(selectorDialog.getmSelectId().get(mPosition));
                }
                if (selectorDialog.getmSelectName() != null && selectorDialog.getmSelectName().size() > mPosition) {
                    adapter.setSelectedNames(selectorDialog.getmSelectName().get(mPosition));
                }
                adapter.notifyDataSetChanged();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public int getmPosition() {
        return mPosition;
    }

    public void setmPosition(int mPosition) {
        this.mPosition = mPosition;
    }

}
