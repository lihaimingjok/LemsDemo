package com.pcjz.lems.business.widget.selectdialog;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pcjz.lems.R;
import com.pcjz.lems.business.common.utils.StringUtils;
import com.pcjz.lems.business.common.utils.TDevice;
import com.pcjz.lems.business.common.utils.TLog;
import com.pcjz.lems.business.context.AppContext;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ${YGP} on 2017/5/4.
 */

public class SelectorDialog extends DialogFragment {
    public final static String SINGLE_MODE = "snl";
    public final static String MULTI_MODE = "multi";

    private ImageView ivDialogCancel;
    private TabLayout tlTitle;
    private ViewPager viewPager = null;
    private TextView tvClear, tvFinish, tvNotice, tvTitle;
    private RelativeLayout rlDialogTitle;

    //private final String[] TAB_TITLES = new String[] {"请选择0","请选择1","请选择2","请选择3","请选择4","请选择5","请选择6"/*R.string.recheck,R.string.recheck,R.string.recheck,R.string.recheck*/};
    //public static String[][] mSelectName;
    //public static String[][] mSelectId;
    private int mMaxSelectCount = 0;
    //private int mLevel = 15;

    /*private String[][] mSelectName = new String[1][mLevel];
    private String[][] mSelectId = new String[1][mLevel];*/
    private List<List<String>> mSelectName = new ArrayList<>();
    private List<List<String>> mSelectId = new ArrayList<>();

    private List<String> mMultiSelectName = new ArrayList<>();

    private List<String> mMultiSelectId = new ArrayList<>();

    private String mType = SINGLE_MODE;

    private ArrayList<SelectInfo> initSelectList;

    private List<Fragment> fragments = new ArrayList<>();
    private String title;
    private SelectorViewpagerAdapter viewpagerAdapter = null;
    private LayoutInflater inflater = null;

    private boolean reviewState = false;

    private boolean initState = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullHeightDialog);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        this.inflater = inflater;
        View view = inflater.inflate(R.layout.layout_selector_dialog, container);
        init(view, inflater);
        return view;
    }

    private void init(View view, LayoutInflater inflater) {
        try {
            initState = false;
            // 初始化等待
            initLoading(AppContext.mResource.getString(R.string.loading_view_loading), view);

            ivDialogCancel = (ImageView) view.findViewById(R.id.iv_dialog_cancel);
            tvClear = (TextView) view.findViewById(R.id.tv_clear);
            tvNotice = (TextView) view.findViewById(R.id.tv_notice);
            tvFinish = (TextView) view.findViewById(R.id.tv_finish);
            tvTitle = (TextView) view.findViewById(R.id.tv_title);
            rlDialogTitle = (RelativeLayout) view.findViewById(R.id.rl_dialog_title);
            if (titleHeight != -1) {
                ViewGroup.LayoutParams layoutParams = rlDialogTitle.getLayoutParams();
                layoutParams.height = (int) (titleHeight * TDevice.getDensity(getActivity()));
                rlDialogTitle.setLayoutParams(layoutParams);
            }
            if (!StringUtils.isEmpty(title)) {
                tvTitle.setText(title);
            }
            //多选
            if (StringUtils.equals(mType, MULTI_MODE)) {
                tvClear.setVisibility(View.VISIBLE);
                tvFinish.setVisibility(View.VISIBLE);
                ivDialogCancel.setVisibility(View.GONE);
                tvNotice.setVisibility(View.VISIBLE);
            }

            viewPager = (ViewPager) view.findViewById(R.id.viewpager);
            tlTitle = (TabLayout) view.findViewById(R.id.tl_title);
            setListener();
            viewpagerAdapter = new SelectorViewpagerAdapter(getChildFragmentManager(), fragments);
            viewPager.setAdapter(viewpagerAdapter);
            tlTitle.setupWithViewPager(viewPager);
            setTabs(tlTitle, inflater);

            if (initSelectList != null && initSelectList.size() > 0) {
                setInitSelecList(initSelectList, "");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        finally {
            initState = true;
            onInitCompletely();
        }
    }

    private void setListener() {
        ivDialogCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectorDialog.this.dismiss();
            }
        });
        tvClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (fragments != null && fragments.size() > 0) {
                        ((SelectorFragment) fragments.get(0)).clearData();
                    }
                    if (mSelectName != null && mSelectName.size() > 0) {
                        mSelectName.get(0).clear();
                    }
                    if (mSelectId != null && mSelectId.size() > 0) {
                        mSelectId.get(0).clear();
                    }
                    if (mMultiSelectName != null && mMultiSelectName.size() > 0) {
                        mMultiSelectName.clear();
                    }
                    if (mMultiSelectId != null && mMultiSelectId.size() > 0) {
                        mMultiSelectId.clear();
                    }

                    iCallback.clearData();
                    SelectorDialog.this.dismiss();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        tvFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (StringUtils.equals(mType, MULTI_MODE)) {
                        iCallback.finish(mSelectName, mSelectId,
                                mSelectName.get(0), mSelectId.get(0), mType);
                    } else {
                        singleFinish();
                    }

                    SelectorDialog.this.dismiss();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    public void singleFinish() {
        try {
            List<String> selectNames = new ArrayList<String>();
            List<String> selectIds = new ArrayList<String>();

            if (mSelectName != null && mSelectName.size() > 0) {
                for (int index = 0; index < mSelectName.size(); index++) {
                    if (mSelectName.get(index) != null && mSelectName.get(index).size() > 0) {
                        for (int idx = 0; idx < mSelectName.get(index).size(); idx++) {
                            selectNames.add(mSelectName.get(index).get(idx));
                        }
                    }
                }
            }
            if (mSelectId != null && mSelectId.size() > 0) {
                for (int index = 0; index < mSelectId.size(); index++) {
                    if (mSelectId.get(index) != null && mSelectId.get(index).size() > 0) {
                        for (int idx = 0; idx < mSelectId.get(index).size(); idx++) {
                            selectIds.add(mSelectId.get(index).get(idx));
                        }
                    }
                }
            }

            iCallback.finish(selectNames, selectIds, mType);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 设置tab
     *
     * @param tabLayout
     * @param layoutInflater
     * @param tabTitles
     */
    private void setTabs(TabLayout tabLayout, LayoutInflater layoutInflater, int[] tabTitles) {
        for (int i = 0; i < tabTitles.length; i++) {
            TabLayout.Tab tab = tlTitle.getTabAt(i);
            View view = View.inflate(getActivity(), R.layout.tab_workbench_item, null);
            TextView textView = (TextView) view.findViewById(R.id.tv_tab);
            textView.setText(AppContext.mResource.getString(tabTitles[i]));
            tab.setCustomView(view);
        }
    }

    /**
     * 设置tab
     *
     * @param tabLayout
     * @param layoutInflater
     * @param tabTitles
     */
    private void setTabs(TabLayout tabLayout, LayoutInflater layoutInflater, String[] tabTitles) {
        for (int i = 0; i < tabTitles.length; i++) {
            TabLayout.Tab tab = tlTitle.getTabAt(i);
            View view = View.inflate(getActivity(), R.layout.tab_workbench_item, null);
            TextView textView = (TextView) view.findViewById(R.id.tv_tab);
            textView.setText(tabTitles[i]);
            tab.setCustomView(view);
        }
    }

    private void setTabName(int position, String name, boolean hasNext) {
        try {
            if (tlTitle == null) {
                return;
            }
            View tabView = null;

            for (int i = 0; i < fragments.size(); i++) {
                if (i == position) {
                    TabLayout.Tab tab = tlTitle.getTabAt(position);
                    if (tab == null) return;

                    View view = tab.getCustomView();
                    if (view == null) return;
                    tabView = (View) view.getParent();
                    if (tabView != null) {
                        tabView.setEnabled(true);
                    }

                    ((SelectorFragment) fragments.get(i)).setTitle(name);

                    TextView textView = (TextView) view.findViewById(R.id.tv_tab);
                    if (hasNext) {
                        textView.setText(name);
                        textView.setVisibility(View.VISIBLE);
                        tab = tlTitle.getTabAt(position + 1);
                        if (tab != null) {
                            view = tab.getCustomView();
                        }
                        if (view != null) {
                            tabView = (View) view.getParent();
                            if (tabView != null) {
                                tabView.setEnabled(true);
                            }
                            textView = (TextView) view.findViewById(R.id.tv_tab);
                            textView.setVisibility(View.VISIBLE);
                        }
                    } else {
                        if (!StringUtils.equals(mType, MULTI_MODE)) {
                            textView.setText(name);
                            SelectorDialog.this.dismiss();
                            singleFinish();
                        }
                    }
                } else if (i == position + 1) {
                    TabLayout.Tab tab = tlTitle.getTabAt(i);
                    if (tab == null) return;
                    View view = tab.getCustomView();
                    if (view != null) {
                        tabView = (View) view.getParent();
                        if (tabView != null) {
                            tabView.setEnabled(true);
                        }

                        TextView textView = (TextView) view.findViewById(R.id.tv_tab);

                        textView.setVisibility(View.VISIBLE);
                        textView.setText(AppContext.mResource.getString(R.string.please_select));
                    }
                } else if (i > position + 1) {
                    TabLayout.Tab tab = tlTitle.getTabAt(i);
                    if (tab == null) return;
                    View view = tab.getCustomView();
                    if (view != null) {
                        tabView = (View) view.getParent();
                        if (tabView != null) {
                            tabView.setEnabled(false);
                        }

                        TextView textView = (TextView) view.findViewById(R.id.tv_tab);
                        if (textView.getVisibility() == View.VISIBLE) {
                            textView.setVisibility(View.INVISIBLE);
                            textView.setText(AppContext.mResource.getString(R.string.please_select));
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void setTabs(TabLayout tabLayout, LayoutInflater layoutInflater) {
        try {
            String title = null;
            if (this.fragments != null && this.fragments.size() > 0) {
                for (int i = 0; i < this.fragments.size(); i++) {
                    SelectorFragment selectorFragment = (SelectorFragment) fragments.get(i);
                    if (selectorFragment == null) {
                        continue;
                    }
                    title = selectorFragment.getTitle();

                    TabLayout.Tab tab = tlTitle.getTabAt(i);
                    if (tab == null) {
                        continue;
                    }
                    AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();
                    if (appCompatActivity == null) {
                        continue;
                    }
                    View view = View.inflate(appCompatActivity, R.layout.tab_workbench_item, null);
                    if (view == null) {
                        continue;
                    }

                    TextView textView = (TextView) view.findViewById(R.id.tv_tab);
                    if (!StringUtils.equals(mType, MULTI_MODE)) {
                        if (title != null) {
                            textView.setText(title);
                            textView.setVisibility(View.VISIBLE);
                        } else {
                            textView.setText(AppContext.mResource.getString(R.string.please_select));
                            if (i != 0) {
                                textView.setVisibility(View.INVISIBLE);
                            }
                        }
                    } else {
                        if (i == 0) {
                            textView.setText(AppContext.mResource.getString(R.string.selected_label));
                            textView.setVisibility(View.VISIBLE);
                        } else {
                            textView.setVisibility(View.INVISIBLE);
                            textView.setText(AppContext.mResource.getString(R.string.please_select));
                            if (title != null) {
                                textView.setText(title);
                                textView.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                    tab.setCustomView(view);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    List<List<SelectInfo>> selectInfoLists;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                // 点击事件
                case 0:
                    onSelect((SelectInfo) msg.obj, msg.arg1, msg.arg2);
                    break;
                /*case 1:// 多选事件
                    SelectInfo selectInfo1 = (SelectInfo) msg.obj;
                    int position1 = msg.arg1;
                    String selectedName1 = "";
                    String selectedId1 = "";
                    if (selectInfo1 != null) {
                        selectedName1 = selectInfo1.getName();
                        selectedId1 = selectInfo1.getId();
                    }
                    *//*if (StringUtils.equals(mType, MULTI_MODE)) {
                        ((SelectorFragment) fragments.get(0)).removeData(selectedName1, selectedId1);
                    }*//*
                    break;*/
                case 2:
                    onReview((SelectInfo) msg.obj, msg.arg1, msg.arg2);
                    break;
                default:
                    break;
            }
        }
    };

    public void onReview(SelectInfo selectInfo, int position, int selectedPosition) {
        try {
            String selectedName = "";
            String selectedId = "";
            int nextPosition = position + 1;
            SelectorFragment nextSelectorFragment = null;
            SelectorFragment curSelectorFragment = null;

            if (selectInfo != null) {
                selectedName = selectInfo.getName();
                selectedId = selectInfo.getId();
            }

            ArrayList<SelectInfo> selectInfoList = null;
            if (MULTI_MODE.compareTo(mType) == 0) {
                selectInfoList = iCallback.getNextSelectList(position - 1, selectedName);
            } else {
                // 获取下一级的数据内容，position为当前级所在容器位置
                selectInfoList = iCallback.getNextSelectList(position, selectedPosition, selectedName);
            }

            curSelectorFragment = addOrGetFragment(position);
            //ArrayList<SelectInfo> selectInfoList = iCallback.getNextSelectList(position, selectedPosition, selectedName);
            if (selectInfoList != null && selectInfoList.size() > 0) {
                if (selectInfoLists == null) {
                    selectInfoLists = new ArrayList<>();
                }
                selectInfoLists.add(selectInfoList);

                curSelectorFragment.setTitle(selectedName);

                // 单选和多选处理
                curSelectorFragment.onReviewSelectedData(selectedPosition, SINGLE_MODE);

                // 必须先添加fragment，产生tab
                nextSelectorFragment = addOrGetFragment(nextPosition);
                nextSelectorFragment.setData(selectInfoList, "");

                if (nextPosition < fragments.size()) {
                    int iRet = nextSelectorFragment.reviewData();
                    if(iRet == -1)
                    {
                        if (viewPager != null) {
                            viewPager.setCurrentItem(nextPosition, false);
                        }
                    }
                }
            } else {
                if (viewPager != null) {
                    viewPager.setCurrentItem(position, false);
                }

                setTabName(position, selectedName, true);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        finally{
            reviewState = true;
            onInitCompletely();
        }
    }

    public void onSelect(SelectInfo selectInfo, int position, int selectedPosition) {
        String selectedName = "";
        String selectedId = "";
        List<SelectInfo> selectInfoList = null;
        int nextPosition = position + 1;
        SelectorFragment nextSelectorFragment = null;
        SelectorFragment curSelectorFragment = null;

        try {
            if (selectInfo != null) {
                selectedName = selectInfo.getName();
                selectedId = selectInfo.getId();
            }

            if (MULTI_MODE.compareTo(mType) == 0) {
                selectInfoList = iCallback.getNextSelectList(position - 1, selectedName);
            } else {
                selectInfoList = iCallback.getNextSelectList(position, selectedPosition, selectedName);
            }

            curSelectorFragment = addOrGetFragment(position);

            // 选择过渡阶段的访问
            if (selectInfoList != null && selectInfoList.size() > 0) {
                if (selectInfoLists == null) {
                    selectInfoLists = new ArrayList<>();
                }
                selectInfoLists.add(selectInfoList);

                curSelectorFragment.setTitle(selectedName);

                // 单选和多选处理
                curSelectorFragment.onClickSelectedData(selectedPosition, SINGLE_MODE);

                // 必须先添加fragment，产生tab
                nextSelectorFragment = addOrGetFragment(nextPosition);

                setTabName(position, selectedName, true);

                if (viewPager != null) {
                    viewPager.setCurrentItem(nextPosition, true);
                }

                if (nextSelectorFragment != null) {
                    nextSelectorFragment.setData(selectInfoList, "");
                }
            } else  // 当最后一层访问时触发，或没有任何下级时出发，此时包括多选的已选择、多选的最后一级选择、单选的最后一级选择的情况
            {
                if (StringUtils.equals(mType, MULTI_MODE)) {
                    boolean bExist = false;
                    if (mSelectId != null && mSelectId.size() > 0
                            && mSelectId.get(0) != null && mSelectId.get(0).size() > 0) {
                        for (int index = 0; index < mSelectId.get(0).size(); index++) {
                            if (selectInfo.getId().compareTo(mSelectId.get(0).get(index)) == 0) {
                                bExist = true;
                                break;
                            }
                        }
                        if (!bExist) {
                            if (mSelectId.get(0).size() >= mMaxSelectCount) {
                                tvNotice.setText(AppContext.mResource.getString(R.string.dontselectprocedure_overseven));

                                return;
                            }
                        }
                    }
                    // 单选和多选处理
                    curSelectorFragment.onClickSelectedData(selectedPosition, MULTI_MODE);

                    if (position != 0) {
                        setTabName(position + 1, selectedName, false);
                    }
                } else {
                    // 单选和多选处理
                    curSelectorFragment.onClickSelectedData(selectedPosition, SINGLE_MODE);

                    setTabName(position, selectedName, false);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private SelectorFragment addOrGetFragment(int index) {

        SelectorFragment fragment = null;
        int size = 0;

        try {
            if (fragments == null) {
                fragments = new ArrayList<>();
            }

            size = fragments.size();

            if (size >= index) {
                if (size == index) {
                    fragment = getFragment(index, AppContext.mResource.getString(R.string.please_select));
                    fragments.add(fragment);
                    if (viewpagerAdapter != null) {
                        viewpagerAdapter.notifyDataSetChanged();
                    }

                    if (tlTitle != null && inflater != null) {
                        setTabs(tlTitle, inflater);
                    }

                    if (mSelectName == null) {
                        mSelectName = new ArrayList<>();
                    }
                    if (mSelectName.size() < fragments.size()) {
                        mSelectName.add(new ArrayList());
                    }
                    if (mSelectId == null) {
                        mSelectId = new ArrayList<>();
                    }
                    if (mSelectId.size() < fragments.size()) {
                        mSelectId.add(new ArrayList());
                    }
                } else {
                    fragment = (SelectorFragment) fragments.get(index);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return fragment;
    }

    private SelectorFragment getFragment(int index, String title) {
        Bundle bundle = null;
        SelectorFragment selectorFragment = null;

        selectorFragment = new SelectorFragment();
        selectorFragment.setmPosition(index);
        selectorFragment.setSelectorDialog(this);
        bundle = new Bundle();
        bundle.putInt("position", index);
        bundle.putInt("maxSelectCount", mMaxSelectCount);

        selectorFragment.setArguments(bundle);

        selectorFragment.setHandler(handler);

        if (title != null) {
            selectorFragment.setTitle(title);
        }

        return selectorFragment;
    }

    public class SelectorViewpagerAdapter extends FragmentPagerAdapter {
        private List<Fragment> fragments;
        private FragmentManager fragmentManager;

        public SelectorViewpagerAdapter(FragmentManager fragmentManager, List<Fragment> fragments) {
            super(fragmentManager);
            this.fragments = fragments;
            this.fragmentManager = fragmentManager;
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = this.fragments.get(position);
            return fragment;
        }

        @Override
        public int getCount() {
            return this.fragments.size();
        }

        public void setFragments(List<Fragment> fragments) {
            if (this.fragments != null) {
                FragmentTransaction ft = fragmentManager.beginTransaction();
                for (Fragment f : this.fragments) {
                    ft.remove(f);
                }
                ft.commit();
                ft = null;
                fragmentManager.executePendingTransactions();
            }
            this.fragments = fragments;
            notifyDataSetChanged();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Window window = getDialog().getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.BOTTOM;
        DisplayMetrics dm = new DisplayMetrics();
        window.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenWidth = dm.widthPixels;
        int screenHeight = dm.heightPixels;
        params.height = (int) (screenHeight * 0.70);//WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = screenWidth;
        window.setAttributes(params);
    }

    private ICallback iCallback;

    public void setCallBack(ICallback iCallback) {
        this.iCallback = iCallback;
    }

    public interface ICallback {
        void finish(List selectedNames, List selectedIds, String mode);

        void finish(List selcectorNames, List selectorIds, List selectedNames, List selectedIds, String mode);

        ArrayList<SelectInfo> getNextSelectList(int currentPostion, String currentSelectName);

        ArrayList<SelectInfo> getNextSelectList(int currentPostion, int selectedPosition, String currentSelectName);

        void clearData();
    }

    public void setmType(String mType) {
        this.mType = mType;
    }

    public void setSelectCount(int selectCount) {
        this.mMaxSelectCount = selectCount;
    }

    public void setInitSelecList(ArrayList<SelectInfo> initSelectList, String label) {
        int initPosition = 0;
        SelectorFragment selectorFragment = null;

        TLog.log("Selector Dialog initSelect start");

        try {
            reviewState = false;

            this.initSelectList = initSelectList;
            if (selectInfoLists == null) {
                selectInfoLists = new ArrayList<List<SelectInfo>>();
            }
            if (initSelectList != null) {
                selectInfoLists.add(initSelectList);
            }


            if (StringUtils.equals(mType, MULTI_MODE)) {
                initMultiSelectData();
                initPosition = 1;
            }

            selectorFragment = addOrGetFragment(initPosition);
            selectorFragment.setData(initSelectList, label);

            if (fragments != null && fragments.get(initPosition) != null && initSelectList != null) {
                int iRet = selectorFragment.reviewData();
                if(iRet == -1)
                {
                    if (viewPager != null) {
                        viewPager.setCurrentItem(initPosition, false);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        finally
        {
            reviewState = true;
            onInitCompletely();
        }

        TLog.log("Selector Dialog initSelect end");
    }

    public void initMultiSelectData() {
        SelectorFragment selectorFragment = null;

        try {
            selectorFragment = addOrGetFragment(0);

            SelectInfo selectInfo = null;
            List<SelectInfo> list = new ArrayList<>();
            if (mMultiSelectId != null && mMultiSelectId.size() > 0) {
                for (int index = 0; index < mMultiSelectId.size(); index++) {
                    selectInfo = new SelectInfo();
                    selectInfo.setId(mMultiSelectId.get(index));
                    if (mMultiSelectName != null && mMultiSelectName.size() > index) {
                        selectInfo.setName(mMultiSelectName.get(index));
                    }

                    list.add(selectInfo);
                }
            }

            selectorFragment.setData(list, "");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void multiSelectedFocus() {
        try {
            if (StringUtils.equals(mType, MULTI_MODE)) {
                if (mMultiSelectId != null && mMultiSelectId.size() > 0) {
                    if (viewPager != null) {
                        viewPager.setCurrentItem(0, true);
                    }
                } else {
                    if (viewPager != null && mSelectId != null && (mSelectId.get(1) == null
                            || (mSelectId.get(1) != null
                            && mSelectId.get(1).size() <= 0))) {
                        viewPager.setCurrentItem(1, true);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public int getFragmentSize() {
        return fragments.size();
    }

    public void setTitle(String title) {
        this.title = title;
    }

    private int titleHeight = -1;

    public void setDialogTitleHeight(int height) {
        this.titleHeight = height;
    }


    public List<List<String>> getmSelectName() {
        return mSelectName;
    }

    public void setmSelectName(List<List<String>> mSelectName) {
        if (mSelectName != null) {
            this.mSelectName = mSelectName;
        }
    }

    public void setSingleSelectName(List<String> selectNames) {
        List<String> list = null;

        if (selectNames != null) {
            if (this.mSelectName == null) {
                this.mSelectName = new ArrayList<>();
            }

            this.mSelectName.clear();
            for (int index = 0; index < selectNames.size(); index++) {
                list = new ArrayList<String>();
                list.add(selectNames.get(index));

                this.mSelectName.add(list);
            }
        }
    }

    public List<List<String>> getmSelectId() {
        return mSelectId;
    }

    public void setmSelectId(List<List<String>> mSelectId) {
        if (mSelectId != null) {
            this.mSelectId = mSelectId;
        }
    }

    public void setSingleSelectId(List<String> selectId) {
        List<String> list = null;

        if (selectId != null) {
            if (this.mSelectId == null) {
                this.mSelectId = new ArrayList<>();
            }

            this.mSelectId.clear();
            for (int index = 0; index < selectId.size(); index++) {
                list = new ArrayList<String>();
                list.add(selectId.get(index));

                this.mSelectId.add(list);
            }
        }
    }

    public List<String> getmMultiSelectName() {
        return mMultiSelectName;
    }

    public void setmMultiSelectName(List<String> mMultiSelectName) {
        this.mMultiSelectName = mMultiSelectName;
    }

    public List<String> getmMultiSelectId() {
        return mMultiSelectId;
    }

    public void setmMultiSelectId(List<String> mMultiSelectId) {
        this.mMultiSelectId = mMultiSelectId;
    }

    public String getmType() {
        return mType;
    }

    public List<Fragment> getFragments() {
        return fragments;
    }

    public void setFragments(List<Fragment> fragments) {
        this.fragments = fragments;
    }


    private LinearLayout llLoading;
    private TextView tvLoading;
    private ProgressBar pbLoading;
    private ImageView ivNoData;

    protected boolean hasLoading() {
        return true;
    }

    protected void initLoading(String msg, View v) {
        try {
            if (hasLoading()) {
                llLoading = (LinearLayout) v.findViewById(R.id.ll_loading);
                llLoading.setVisibility(View.VISIBLE);
                tvLoading = (TextView) v.findViewById(R.id.tv_loading);
                tvLoading.setText(msg);
                ivNoData = (ImageView) v.findViewById(R.id.iv_no_data);
                //ivNoData.setVisibility(View.GONE);
                pbLoading = (ProgressBar) v.findViewById(R.id.pb_loading);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void loadError(String msg, boolean doAgain) {
        try {
            if (llLoading != null
                    && llLoading.getVisibility() == View.VISIBLE) {
                pbLoading.setVisibility(View.GONE);
                tvLoading.setText(msg);
                if (doAgain) {
                    llLoading.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            loadAgain();
                        }
                    });
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    protected void loadAgain() {
        try {
            if (llLoading != null) {
                pbLoading.setVisibility(View.VISIBLE);
                tvLoading.setText("正在加载...");
                llLoading.setClickable(false);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    protected void hideLoading() {
        try {
            if (llLoading != null && llLoading.getVisibility() == View.VISIBLE) {
                //System.out.println("hide");
                llLoading.setVisibility(View.GONE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void onInitCompletely() {
        if (initState && reviewState) {
            multiSelectedFocus();

            hideLoading();
        }

        TLog.log("onInitCompletely:" + initState + ":" + reviewState);
    }

    boolean dismissed = false;
    boolean mShownByMe = true;
    Boolean mViewDestroyed = false;

    public void onAttach(Context context) {
        super.onAttach(context);
        if (!this.mShownByMe) {
            this.dismissed = false;
        }

    }

    public void onDetach() {
        super.onDetach();
        if (!this.mShownByMe && !this.dismissed) {
            this.dismissed = true;
        }
    }

    public void dismiss() {
        super.dismiss();
        if (!this.dismissed) {
            this.dismissed = true;
            this.mShownByMe = false;
            this.mViewDestroyed = true;
        }
    }

    public void dismissAllowingStateLoss() {
        super.dismissAllowingStateLoss();
        if (!this.dismissed) {
            this.dismissed = true;
            this.mShownByMe = false;
            this.mViewDestroyed = true;
        }
    }

    public int show(FragmentTransaction transaction, String tag) {

        this.dismissed = false;
        this.mShownByMe = true;
        this.mViewDestroyed = false;
        return super.show(transaction, tag);
    }

    public void show(FragmentManager manager, String tag) {
        super.show(manager, tag);
        this.dismissed = false;
        this.mShownByMe = true;
    }

    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (!this.mViewDestroyed) {
            if (!this.dismissed) {
                this.dismissed = true;
                this.mShownByMe = false;
                this.mViewDestroyed = true;
            }
        }
    }

    public void onStart() {
        super.onStart();
        if (this.getDialog() != null) {
            this.mViewDestroyed = false;
        }
    }

    public boolean isDismissed() {
        return dismissed;
    }

    public void setDismissed(boolean dismissed) {
        this.dismissed = dismissed;
    }

}

