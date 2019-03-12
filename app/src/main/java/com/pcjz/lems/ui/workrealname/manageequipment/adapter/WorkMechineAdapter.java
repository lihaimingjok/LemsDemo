package com.pcjz.lems.ui.workrealname.manageequipment.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pcjz.lems.R;
import com.pcjz.lems.business.common.utils.StringUtils;
import com.pcjz.lems.ui.workrealname.manageequipment.bean.WorkMechineBean;

import java.util.List;

/**
 * created by yezhengyu on 2017/9/15 16:02
 */

public class WorkMechineAdapter extends RecyclerView.Adapter<WorkMechineAdapter.MyViewHolder> {

    private LayoutInflater mLayoutInflater ;
    private Context mContext;
    private List<WorkMechineBean> mDatas;
    private OnItemClickListener listener;

    private View VIEW_HEADER;
    private int TYPE_NORMAL = 1000;
    private int TYPE_HEADER = 1001;

    public WorkMechineAdapter(Context context, List<WorkMechineBean> datas) {
        this.mContext = context;
        this.mDatas = datas;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getItemCount() {
        int count = mDatas == null ? 0 : mDatas.size();
        if (VIEW_HEADER != null) {
            count++;
        }
        return count;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == TYPE_HEADER) {
            TypedValue typedValue = new TypedValue();
            mContext.getTheme().resolveAttribute(R.attr.selectableItemBackground, typedValue, true);
            VIEW_HEADER.setBackgroundResource(typedValue.resourceId);
            MyViewHolder viewHolder = new MyViewHolder(VIEW_HEADER);
            return viewHolder;
        } else {
            View view = mLayoutInflater.inflate(R.layout.item_work_mechine, parent, false);
            TypedValue typedValue = new TypedValue();
            mContext.getTheme().resolveAttribute(R.attr.selectableItemBackground, typedValue, true);
            view.setBackgroundResource(typedValue.resourceId);
            MyViewHolder viewHolder = new MyViewHolder(view);
            return viewHolder;
        }
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        if ((!isHeaderView(position))) {
            WorkMechineBean mechineBean = mDatas.get(position - 1);
            holder.tvMechineName.setText(mechineBean.getDeviceName());
            String supplier = mechineBean.getSupplier();
            if (StringUtils.equals(supplier, "ZK")) {
                holder.tvBrand.setText("中控");
            } else if (StringUtils.equals(supplier, "HW")) {
                holder.tvBrand.setText("汉王");
            }
            if (mechineBean.getBindDeviceName() != null) {
                holder.tvBindEqu.setText(mechineBean.getBindDeviceName());
            } else {
                holder.tvBindEqu.setText("无");
            }
            if (StringUtils.equals(mechineBean.getDeviceStatus(), "0")) {
                holder.tvMechineStatus.setText("未使用");
            } else if (StringUtils.equals(mechineBean.getDeviceStatus(), "1")) {
                holder.tvMechineStatus.setText("启用中");
            } else {
                holder.tvMechineStatus.setText("故障中");
            }
            setOnClickListener(holder, position - 1);
        }
    }

    private void setOnClickListener(final MyViewHolder holder, final int position) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.setOnClick(holder.itemView, position);
            }
        });
    }


    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void setOnClick(View holder, int position);
    }

    @Override
    public int getItemViewType(int position) {
        if (isHeaderView(position)) {
            return TYPE_HEADER;
        } else {
            return TYPE_NORMAL;
        }
    }

    private boolean isHeaderView(int position) {
        return haveHeaderView() && position == 0;
    }

    public void addHeaderView(View headerView) {
        if (haveHeaderView()) {
            throw new IllegalStateException("hearview has already exists!");
        } else {
            //避免出现宽度自适应
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            headerView.setLayoutParams(params);
            VIEW_HEADER = headerView;
            notifyItemInserted(0);
        }

    }

    private boolean haveHeaderView() {
        return VIEW_HEADER != null;
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView tvMechineName;
        TextView tvBindEqu;
        TextView tvMechineStatus;
        TextView tvBrand;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvMechineName = (TextView) itemView.findViewById(R.id.tv_mechine_name);
            tvBindEqu = (TextView) itemView.findViewById(R.id.tv_bind_equipment);
            tvMechineStatus = (TextView) itemView.findViewById(R.id.tv_mechine_status);
            tvBrand = (TextView) itemView.findViewById(R.id.tv_brand);
        }
    }
}
