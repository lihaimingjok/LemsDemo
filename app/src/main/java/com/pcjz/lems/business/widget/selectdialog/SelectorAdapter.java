package com.pcjz.lems.business.widget.selectdialog;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pcjz.lems.R;
import com.pcjz.lems.business.base.MyBaseAdapter;
import com.pcjz.lems.business.common.utils.StringUtils;
import com.pcjz.lems.business.context.AppContext;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ${YGP} on 2017/5/8.
 */

public class SelectorAdapter extends MyBaseAdapter {
    public final static String MULTI_CHOICE_MODE = "multi";
    public final static String SINGLE_CHOICE_MODE = "snl";
    private List<String> selectedNames = new ArrayList<>();
    private List<String> selectedIds = new ArrayList<>();
    private List<SelectInfo> mSelectInfoList;

    private String mode = SINGLE_CHOICE_MODE;

    public SelectorAdapter(List<String> selectedNames) {
        this.selectedNames = selectedNames;
    }

    public SelectorAdapter(List<SelectInfo> data, List<String> selectedNames, List<String> selectedIds)  {
        this.mSelectInfoList = data;
        if(selectedNames != null) {
            this.selectedNames = selectedNames;
        }
        if(selectedIds != null) {
            this.selectedIds = selectedIds;
        }
    }
    @Override
    protected View getRealView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        SelectorAdapter.ViewHolder holder = null;
        if (convertView == null || convertView.getTag() == null) {
            convertView = getLayoutInflater(parent.getContext()).inflate(
                    R.layout.item_selector, null);
            holder = new SelectorAdapter.ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (SelectorAdapter.ViewHolder) convertView.getTag();
        }
        if (_data != null && _data.size() > 0) {
            SelectInfo selectInfo = (SelectInfo)_data.get(position);
            if (selectInfo != null) {
                String id = selectInfo.getId();
                String selectedName = selectInfo.getName();
                String key = id + selectedName;
                int i = 0;
                if(selectedIds != null && selectedIds.size() > 0) {
                    String last = "";
                    for (i = 0; i < selectedIds.size(); i++) {
                        last = selectedIds.get(i) + selectedNames.get(i);

                        if (last != null && key != null && last.compareTo(key) == 0) {
                            holder.ivSelected.setVisibility(View.VISIBLE);
                            holder.tvName.setTextColor(AppContext.mResource.getColor(R.color.workbench_proj_color_sel));
                            break;
                        }
                    }

                    if (i > selectedIds.size() - 1) {
                        holder.ivSelected.setVisibility(View.GONE);
                        holder.tvName.setTextColor(AppContext.mResource.getColor(R.color.workbench_proj_color_nor));
                    }
                }
                holder.tvName.setText(selectedName);
            }

        }
        return convertView;
    }

    private String getLastName(String[] names) {
        for (int i = names.length-1; i >= 0; i--) {
            if (!StringUtils.isEmpty(names[i])) {
                return names[i];
            }
        }
        return "";
    }

    public class ViewHolder {
        ImageView ivSelected;
        TextView tvName;

        public ViewHolder(View view) {
            ivSelected = (ImageView) view.findViewById(R.id.iv_selected);
            tvName = (TextView) view.findViewById(R.id.tv_name);
            ivSelected.setTag("gone");
        }
    }

    public List<String> getSelectedNames() {
        return selectedNames;
    }

    public void setSelectedNames(List<String> selectedNames) {
        this.selectedNames = selectedNames;
    }

    public List<String> getSelectedIds() {
        return selectedIds;
    }

    public void setSelectedIds(List<String> selectedIds) {
        this.selectedIds = selectedIds;
    }

    public List<SelectInfo> getmSelectInfoList() {
        return mSelectInfoList;
    }

    public void setmSelectInfoList(List<SelectInfo> mSelectInfoList) {
        this.mSelectInfoList = mSelectInfoList;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }


}
