package com.pcjz.lems.business.common.view.downpopuwindow;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pcjz.lems.R;
import com.pcjz.lems.business.base.MyBaseAdapter;

import java.util.List;

/**
 * created by yezhengyu on 2017/9/14 14:41
 */

public class DownAdapter extends MyBaseAdapter {

    @Override
    protected View getRealView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null || convertView.getTag() == null) {
            convertView = getLayoutInflater(parent.getContext()).inflate(
                    R.layout.item_down_select, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (_data != null) {
            DownBean downBean = (DownBean) _data.get(position);
            holder.tvName.setText(downBean.getName());
            if (position == 1) {
                holder.viewDivider.setVisibility(View.GONE);
            }
        }
        return convertView;
    }

    public class ViewHolder {
        TextView tvName;
        View viewDivider;

        public ViewHolder(View view) {
            tvName = (TextView) view.findViewById(R.id.tv_name);
            viewDivider = view.findViewById(R.id.view_divider);
        }
    }
}
