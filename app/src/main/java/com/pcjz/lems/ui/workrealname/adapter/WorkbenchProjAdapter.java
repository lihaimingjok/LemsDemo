package com.pcjz.lems.ui.workrealname.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pcjz.lems.R;
import com.pcjz.lems.business.base.MyBaseAdapter;
import com.pcjz.lems.business.common.utils.StringUtils;
import com.pcjz.lems.business.context.AppContext;
import com.pcjz.lems.business.entity.ProjectPeroidInfo;

/**
 * Created by ${YGP} on 2017/3/25.
 */

public class WorkbenchProjAdapter extends MyBaseAdapter {

    private String projTitle="";
    public WorkbenchProjAdapter(String projTitle) {
        this.projTitle = projTitle;
    }

    @Override
    protected View getRealView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null || convertView.getTag() == null) {
            convertView = getLayoutInflater(parent.getContext()).inflate(
                    R.layout.item_workbench_proj, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (_data != null && _data.size() > 0) {
            ProjectPeroidInfo projectPeroidInfo = (ProjectPeroidInfo)_data.get(position);
            if (projectPeroidInfo != null && projectPeroidInfo.getPeriodName() != null) {
                if (StringUtils.equals(projTitle,projectPeroidInfo.getPeriodName())) {
                    holder.ivSelected.setVisibility(View.VISIBLE);
                    holder.tvProjName.setTextColor(AppContext.mResource.getColor(R.color.workbench_proj_color_sel));
                } else {
                    holder.ivSelected.setVisibility(View.GONE);
                    holder.tvProjName.setTextColor(AppContext.mResource.getColor(R.color.workbench_proj_color_nor));
                }
                holder.tvProjName.setText(projectPeroidInfo.getPeriodName());
            }
        }
        return convertView;
    }

    public class ViewHolder {
        ImageView ivSelected;
        TextView tvProjName;

        public ViewHolder(View view) {
            ivSelected = (ImageView) view.findViewById(R.id.iv_selected);
            tvProjName = (TextView) view.findViewById(R.id.tv_proj_name);
        }
    }
}