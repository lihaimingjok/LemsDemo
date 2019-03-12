package com.pcjz.lems.ui.workrealname.projectsituation.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.pcjz.lems.R;
import com.pcjz.lems.ui.workrealname.projectsituation.bean.WarnMsgEntity;

import java.util.List;

/**
 * Created by ThinkPad on 2017/9/23.
 */

public class WarnmsgAdapter extends RecyclerView.Adapter<WarnmsgAdapter.MyViewHolder>{

    private DisplayImageOptions mOption = new DisplayImageOptions.Builder()
            .showStubImage(R.drawable.my_leftbar_pho_header)
            .showImageForEmptyUri(R.drawable.my_leftbar_pho_header)
            .showImageOnFail(R.drawable.my_leftbar_pho_header).cacheInMemory(false)
            .cacheOnDisc(false).bitmapConfig(Bitmap.Config.RGB_565)
            .imageScaleType(ImageScaleType.EXACTLY).build();

    private LayoutInflater mLayoutInflater ;
    private Context mContext;
    private List<WarnMsgEntity> mDatas;
    private OnItemClickListener listener;

    public WarnmsgAdapter(Context context, List<WarnMsgEntity> datas) {
        this.mContext = context;
        this.mDatas = datas;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getItemCount() {
        return mDatas == null ? 0 : mDatas.size();
    }

    @Override
    public WarnmsgAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.item_warn_message, parent, false);
        WarnmsgAdapter.MyViewHolder viewHolder = new WarnmsgAdapter.MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder,  int position) {
        /*tvWarnDev, tvWarnContent, tvWarnPart, tvWarnTime*/
        holder.tvWarnDev.setText("预警设备："+mDatas.get(position).getWarnDevice());
        holder.tvWarnContent.setText("预警内容："+mDatas.get(position).getWarnContent());

        holder.tvWarnTime.setText(FromatDate(mDatas.get(position).getWarnDate()));
        setOnClickListener(holder, position);

    }

    private String FromatDate(String time) {
        return  time.substring(5, 7) + "-" + time.substring(8, 10);
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
    


    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView tvWarnDev, tvWarnContent, tvWarnPart, tvWarnTime;
        LinearLayout llIDo;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvWarnDev = (TextView) itemView.findViewById(R.id.tvWarnDev);
            tvWarnContent = (TextView) itemView.findViewById(R.id.tvWarnContent);
            /*tvWarnPart = (TextView) itemView.findViewById(R.id.tvWarnPart);*/
            tvWarnTime = (TextView) itemView.findViewById(R.id.tvWarnDate);
        }
    }
}
