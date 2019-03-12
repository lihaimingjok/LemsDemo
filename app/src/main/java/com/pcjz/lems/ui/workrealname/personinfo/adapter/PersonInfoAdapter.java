package com.pcjz.lems.ui.workrealname.personinfo.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.pcjz.lems.R;
import com.pcjz.lems.business.config.AppConfig;
import com.pcjz.lems.business.entity.person.PersonEntity;

import java.util.List;

/**
 * Created by Greak on 2017/9/20.
 */

public class PersonInfoAdapter extends RecyclerView.Adapter<PersonInfoAdapter.MyViewHolder> {

    private DisplayImageOptions mOption = new DisplayImageOptions.Builder()
            .showStubImage(R.drawable.my_leftbar_pho_header)
            .showImageForEmptyUri(R.drawable.my_leftbar_pho_header)
            .showImageOnFail(R.drawable.my_leftbar_pho_header).cacheInMemory(false)
            .cacheOnDisc(false).bitmapConfig(Bitmap.Config.RGB_565)
            .imageScaleType(ImageScaleType.EXACTLY).build();

    private LayoutInflater mLayoutInflater ;
    private Context mContext;
    private List<PersonEntity> mDatas;
    private OnItemClickListener listener;
    private onDialListener mOnDialListener;

    public PersonInfoAdapter(Context context, List<PersonEntity> datas) {
        this.mContext = context;
        this.mDatas = datas;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getItemCount() {
        return mDatas == null ? 0 : mDatas.size();
    }

    @Override
    public PersonInfoAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.item_person_info, parent, false);
        TypedValue typedValue = new TypedValue();
        mContext.getTheme().resolveAttribute(R.attr.selectableItemBackground, typedValue, true);
        view.setBackgroundResource(typedValue.resourceId);
        PersonInfoAdapter.MyViewHolder viewHolder = new PersonInfoAdapter.MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder,  int position) {
        holder.tvId.setText(mDatas.get(position).getUserId());
        holder.tvName.setText(mDatas.get(position).getJobName());
        holder.tvPhone.setText(mDatas.get(position).getPhoneNum());
        holder.tvPhone.setTextColor(Color.parseColor("#999999"));
        holder.tvPersonType.setText(mDatas.get(position).getJobType());
        if(mDatas.get(position).getCheckType() == 0){
            holder.tvState.setText("未审核");
            holder.tvState.setTextColor(Color.parseColor("#FD9426"));
        }else if(mDatas.get(position).getCheckType() == 1){
            holder.tvState.setText("");
        } else if(mDatas.get(position).getCheckType() == 2){
            holder.tvState.setText("未通过");
            holder.tvState.setTextColor(Color.parseColor("#FF3A30"));
        }
        ImageLoader.getInstance().displayImage(AppConfig.IMAGE_FONT_URL + mDatas.get(position).getAvater(), holder.ivHeader,  mOption);
        setOnClickListener(holder, position);
        setOnDialListener(holder, position);

    }

    private void setOnDialListener(final MyViewHolder holder, final int position) {
        holder.tvPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnDialListener.onDialPhone(holder.itemView, position);

            }
        });
    }

    public interface onDialListener{
        void onDialPhone(View holder, int position);
    }

    public void setOnDialListener(onDialListener mOnDialListener){
        this.mOnDialListener = mOnDialListener;
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

        TextView tvName, tvPersonType, tvPhone, tvState, tvId;
        ImageView ivHeader;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvId = (TextView) itemView.findViewById(R.id.tvId);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            tvPersonType = (TextView) itemView.findViewById(R.id.tvPersonType);
            tvPhone = (TextView) itemView.findViewById(R.id.tvPersonPhone);
            tvState = (TextView) itemView.findViewById(R.id.tvSate);
            ivHeader = (ImageView) itemView.findViewById(R.id.ivHeader);
        }
    }


}
