package com.pcjz.lems.business.widget.selectdialog;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pcjz.lems.R;
import com.pcjz.lems.business.base.MyBaseAdapter;
import com.pcjz.lems.business.common.utils.StringUtils;

/**
 * Created by ${YGP} on 2017//18.
 */

public class SelectedAdapter extends MyBaseAdapter {
    @Override
    protected View getRealView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        SelectedAdapter.ViewHolder holder = null;
        if (convertView == null || convertView.getTag() == null) {
            convertView = getLayoutInflater(parent.getContext()).inflate(
                    R.layout.item_selector, null);
            holder = new SelectedAdapter.ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (SelectedAdapter.ViewHolder) convertView.getTag();
        }
        if (_data != null && _data.size() > 0) {
            SelectInfo selectInfo = (SelectInfo) _data.get(position);
            if (selectInfo != null) {
                if (!StringUtils.isEmpty(selectInfo.getName())) {
                    holder.tvName.setText(selectInfo.getName());
                }
            }

        }
        return convertView;
    }

    public class ViewHolder {
        ImageView ivSelected;
        TextView tvName;

        public ViewHolder(View view) {
            ivSelected = (ImageView) view.findViewById(R.id.iv_selected);
            ivSelected.setVisibility(View.VISIBLE);
            tvName = (TextView) view.findViewById(R.id.tv_name);

        }
    }

}
