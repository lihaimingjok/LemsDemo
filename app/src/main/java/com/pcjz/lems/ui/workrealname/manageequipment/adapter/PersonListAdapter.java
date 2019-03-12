package com.pcjz.lems.ui.workrealname.manageequipment.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.pcjz.lems.R;
import com.pcjz.lems.business.common.utils.StringUtils;
import com.pcjz.lems.business.config.AppConfig;
import com.pcjz.lems.business.entity.PersonInfoEntity;
import com.pcjz.lems.ui.workrealname.manageequipment.EquipmentInfoActivity;

import java.util.List;

/**
 * created by yezhengyu on 2017/9/18 10:50
 */

public class PersonListAdapter extends RecyclerView.Adapter<PersonListAdapter.MyViewHolder> {

    private Context mContext;
    private List<PersonInfoEntity> mDatas;
    private final LayoutInflater mInflater;

    private DisplayImageOptions mOption = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.load_pho_default_image)
            .showImageForEmptyUri(R.drawable.load_pho_default_image)
            .showImageOnFail(R.drawable.load_pho_default_image)
            .cacheInMemory(true)
            .cacheOnDisc(false).bitmapConfig(Bitmap.Config.RGB_565)
            .imageScaleType(ImageScaleType.EXACTLY).build();

    public PersonListAdapter(Context context, List<PersonInfoEntity> datas) {
        this.mContext = context;
        this.mDatas = datas;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getItemCount() {
        return mDatas == null ? 0 : mDatas.size();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_person_list, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.tvHeader.setVisibility(View.VISIBLE);
        holder.ivHeader.setVisibility(View.GONE);
        holder.tvName.setVisibility(View.VISIBLE);
        holder.ivDelete.setVisibility(View.GONE);
        holder.ivAdd.setVisibility(View.GONE);
        String mechineName = mDatas.get(position).getEmpName();
        String backGroundColor = mDatas.get(position).getBackGroundColor();
        String deleteStatus = mDatas.get(position).getDeleteStatus();
        String facePhotoUrl = mDatas.get(position).getFacephoto();
        if (StringUtils.equals(deleteStatus, "1")) {
            if (position == mDatas.size() - 1) {
                holder.tvHeader.setVisibility(View.GONE);
                holder.ivHeader.setVisibility(View.GONE);
                holder.tvName.setText("添加");
                holder.ivDelete.setVisibility(View.GONE);
                holder.ivAdd.setVisibility(View.VISIBLE);
                holder.ivAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((EquipmentInfoActivity) mContext).addPerson();
                    }
                });
            } else {
                holder.ivDelete.setVisibility(View.VISIBLE);
                holder.ivAdd.setVisibility(View.GONE);
                holder.ivDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onClickListener.setDeleteClick(position);
                    }
                });
            }
        } else {
            holder.ivDelete.setVisibility(View.GONE);
            holder.ivAdd.setVisibility(View.GONE);
        }

        if (position != mDatas.size() - 1 || StringUtils.equals(deleteStatus, "0")) {
            if (StringUtils.isEmpty(facePhotoUrl)) {
                holder.tvHeader.setVisibility(View.VISIBLE);
                holder.ivHeader.setVisibility(View.GONE);
                GradientDrawable myGrad = (GradientDrawable) holder.tvHeader.getBackground();
                if (!StringUtils.isEmpty(backGroundColor)) {
                    myGrad.setColor(Color.parseColor(backGroundColor));
                } else {
                    myGrad.setColor(Color.parseColor("#38AFF7"));
                }
                if (!StringUtils.isEmpty(mechineName) && mechineName.length() >= 2) {
                    holder.tvHeader.setText(mechineName.substring(mechineName.length() - 2, mechineName.length()));
                } else {
                    holder.tvHeader.setText(mechineName);
                }
            } else {
                holder.ivHeader.setVisibility(View.VISIBLE);
                holder.tvHeader.setVisibility(View.GONE);
                ImageLoader.getInstance().displayImage(AppConfig.IMAGE_FONT_URL + facePhotoUrl, holder.ivHeader, mOption);
            }
            if (!StringUtils.isEmpty(mechineName)) {
                holder.tvName.setText(mechineName);
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickListener.setItemClick(holder.itemView, position);
                }
            });
        }
    }

    private OnClickListenerI onClickListener;

    public void setOnItemClickListener(OnClickListenerI onClickListener) {
        this.onClickListener = onClickListener;
    }

    public interface OnClickListenerI {

        void setItemClick(View holder, int position);

        void setDeleteClick(int position);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvHeader;
        ImageView ivHeader;
        TextView tvName;
        ImageView ivDelete;
        ImageView ivAdd;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvHeader = (TextView) itemView.findViewById(R.id.tv_person_header);
            ivHeader = (ImageView) itemView.findViewById(R.id.iv_person_header);
            tvName = (TextView) itemView.findViewById(R.id.tv_person_name);
            ivDelete = (ImageView) itemView.findViewById(R.id.iv_delete);
            ivAdd = (ImageView) itemView.findViewById(R.id.iv_add_click);
        }
    }
}
