package com.pcjz.lems.business.widget.selectdialog;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.pcjz.lems.R;
import com.pcjz.lems.business.base.BaseFragment;
import com.pcjz.lems.business.common.utils.StringUtils;

import java.util.ArrayList;

/**
 * Created by ${YGP} on 2017/5/17.
 */

public class SelectedFragment extends BaseFragment {
    private ListView lvContent;
    private SelectedAdapter selectedAdapter;
    public static ArrayList<SelectInfo> mSelectInfos;


    private String title;

    @Override
    protected void initView(View view) {
        lvContent = (ListView) view.findViewById(R.id.lv_content);
        selectedAdapter = new SelectedAdapter();
        if (mSelectInfos == null) {
            mSelectInfos = new ArrayList<SelectInfo>();
        }
        selectedAdapter.setData(mSelectInfos);
        lvContent.setAdapter(selectedAdapter);
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
                mSelectInfos.remove(position);
                selectedAdapter.notifyDataSetChanged();
            }
        });
    }

    public void reloadData(String selectedName, String selectedId) {
        if (mSelectInfos == null) {
            mSelectInfos = new ArrayList<SelectInfo>();
        } else {
            SelectInfo selectInfo;
            for (int i = 0; i < mSelectInfos.size(); i++) {
                selectInfo = mSelectInfos.get(i);
                if (selectInfo != null && StringUtils.equals(selectInfo.getName(), selectedName)) {
                    return;
                }
            }
        }
        SelectInfo selectInfo = new SelectInfo();
        if (selectedName != null) {
            selectInfo.setName(selectedName);
        }
        if (selectedId != null) {
            selectInfo.setId(selectedId);
        }
        if (mSelectInfos != null) {
            mSelectInfos.add(selectInfo);
        }
        if (selectedAdapter != null) {
            selectedAdapter.notifyDataSetChanged();
        }
    }

    public ArrayList<SelectInfo> getSelectInfos() {
        return mSelectInfos;
    }

    public void clearData() {
        if (mSelectInfos == null) return;
        mSelectInfos.clear();
        selectedAdapter.notifyDataSetChanged();
    }

    public void removeData(String selectedName, String selectedId) {
        SelectInfo selectInfo;
        for (int i = 0; i < mSelectInfos.size(); i++) {
            selectInfo = mSelectInfos.get(i);
            if (selectInfo != null && StringUtils.equals(selectInfo.getName(), selectedName)) {
                mSelectInfos.remove(i);
                selectedAdapter.notifyDataSetChanged();
                return;
            }
        }
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
