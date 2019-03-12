package com.pcjz.lems.ui.workrealname.manageequipment.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pcjz.lems.R;
import com.pcjz.lems.business.common.utils.StringUtils;
import com.pcjz.lems.ui.workrealname.manageequipment.bean.OperateBean;
import com.pcjz.lems.ui.workrealname.manageequipment.bean.OperateRecordBean;

import java.util.List;

/**
 * created by yezhengyu on 2017/9/25 09:31
 */

public class OperateAdapter extends RecyclerView.Adapter<OperateAdapter.MyViewHolder>{

    private final List<OperateRecordBean> mDatas;
    private final Context mContext;
    private final LayoutInflater mInflater;
    private OnItemClickListener listener;

    private View VIEW_HEADER;
    private int TYPE_NORMAL = 1000;
    private int TYPE_HEADER = 1001;

    public OperateAdapter(Context context, List<OperateRecordBean> datas) {
        mContext = context;
        mDatas = datas;
        mInflater = LayoutInflater.from(mContext);
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
            MyViewHolder viewHolder = new MyViewHolder(VIEW_HEADER);
            return viewHolder;
        } else {
            View view = mInflater.inflate(R.layout.item_operate_record, parent, false);
            MyViewHolder viewHolder = new MyViewHolder(view);
            return viewHolder;
        }
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        if ((!isHeaderView(position))) {
            OperateRecordBean operate = mDatas.get(position - 1);
            holder.tvJobNumber.setText(operate.getEmpWorkCode());
            holder.tvName.setText(operate.getEmpName());
            holder.tvOperateEqu.setText(operate.getDeviceName());
            String operatTime = operate.getOperatTime();
            String[] split = operatTime.split(" ");
            String date = "";
            if (!StringUtils.isEmpty(split[0])) {
                date = split[0];
            }
            String time = "";
            if (!StringUtils.isEmpty(split[1])) {
                time = split[1];
            }
            holder.tvTime.setText(mContext.getResources().getString(R.string.operate_time, date, time));
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
        TextView tvJobNumber;
        TextView tvName;
        TextView tvOperateEqu;
        TextView tvTime;
        public MyViewHolder(View itemView) {
            super(itemView);
            tvJobNumber = (TextView) itemView.findViewById(R.id.tv_job_number);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            tvOperateEqu = (TextView) itemView.findViewById(R.id.tv_operate_equipment);
            tvTime = (TextView) itemView.findViewById(R.id.tv_operate_time);
        }
    }
}
