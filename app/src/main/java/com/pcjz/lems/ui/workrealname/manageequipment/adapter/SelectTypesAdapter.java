package com.pcjz.lems.ui.workrealname.manageequipment.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pcjz.lems.R;
import com.pcjz.lems.business.base.MyBaseAdapter;
import com.pcjz.lems.business.entity.SelectEntity;

/**
 * created by yezhengyu on 2017/7/3 11:28
 */

public class SelectTypesAdapter extends MyBaseAdapter {

    private final Context context;

    public SelectTypesAdapter(Context context) {
        this.context = context;
    }

    @Override
    protected View getRealView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if (convertView == null || convertView.getTag() == null) {
            convertView = getLayoutInflater(parent.getContext()).inflate(
                    R.layout.item_select_type, null);
            holder = new ViewHolder(convertView);
        } else {
            holder =  (ViewHolder) convertView.getTag();
        }
        if (_data != null && _data.size() > 0) {
            SelectEntity entity = (SelectEntity) _data.get(position);
            boolean isSelect = entity.isSelect();
            holder.tvName.setText(entity.getName());
            if (isSelect) {
                holder.ivSelect.setVisibility(View.VISIBLE);
                holder.tvName.setTextColor(ContextCompat.getColor(context, R.color.common_blue_color));
            } else {
                holder.ivSelect.setVisibility(View.GONE);
                holder.tvName.setTextColor(ContextCompat.getColor(context, R.color.black));
            }
        }
        return convertView;
    }

    public class ViewHolder {

        private final TextView tvName;
        private final ImageView ivSelect;

        public ViewHolder(View view) {
            tvName = (TextView) view.findViewById(R.id.textView);
            ivSelect = (ImageView) view.findViewById(R.id.imageViewCheckMark);
            view.setTag(this);
        }
    }
}
