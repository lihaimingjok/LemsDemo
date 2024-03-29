package com.pcjz.lems.ui.workrealname.manageequipment.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.pcjz.lems.R;
import com.pcjz.lems.business.common.utils.StringUtils;
import com.pcjz.lems.business.common.view.CircleImageView;
import com.pcjz.lems.business.config.AppConfig;
import com.pcjz.lems.business.entity.PersonInfoEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class ContactsSortAdapter extends BaseAdapter implements SectionIndexer {
    private List<PersonInfoEntity> mList;
    private List<PersonInfoEntity> mSelectedList;
    private Context mContext;
    LayoutInflater mInflater;

    private DisplayImageOptions mOption = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.load_pho_default_image)
            .showImageForEmptyUri(R.drawable.load_pho_default_image)
            .showImageOnFail(R.drawable.load_pho_default_image)
            .cacheInMemory(true)
            .cacheOnDisc(false).bitmapConfig(Bitmap.Config.RGB_565)
            .imageScaleType(ImageScaleType.EXACTLY).build();

    public ContactsSortAdapter(Context mContext, List<PersonInfoEntity> list, List<PersonInfoEntity> personDatas) {
        this.mContext = mContext;
        mSelectedList = new ArrayList<>();
        mSelectedList.addAll(personDatas);
        if (list == null) {
            this.mList = new ArrayList<>();
        } else {
            this.mList = list;
        }
    }

    /**
     * 当ListView数据发生变化时,调用此方法来更新ListView
     */
    public void updateListView(/*List<PersonInfoEntity> list*/List<PersonInfoEntity> selectedList) {
        /*if (list == null) {
			this.mList = new ArrayList<>();
		} else {
			this.mList = list;
		}*/
        mSelectedList.clear();
        mSelectedList.addAll(selectedList);
        notifyDataSetChanged();
    }

    public int getCount() {
        return this.mList.size();
    }

    public Object getItem(int position) {
        return mList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View view, ViewGroup arg2) {
        ViewHolder viewHolder = null;
        final PersonInfoEntity mContent = mList.get(position);
        if (view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.item_contact, null);
            viewHolder.tvTitle = (TextView) view.findViewById(R.id.tv_title_name);
            viewHolder.tvHeader = (TextView) view.findViewById(R.id.tv_person_header);
            viewHolder.ivHeader = (CircleImageView) view.findViewById(R.id.iv_person_header);
            viewHolder.tvCompany = (TextView) view.findViewById(R.id.tv_content_company);
            viewHolder.tvLetter = (TextView) view.findViewById(R.id.catalog);
            viewHolder.cbChecked = (CheckBox) view.findViewById(R.id.cbChecked);
            viewHolder.viewDivider = view.findViewById(R.id.view_divider);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        //根据position获取分类的首字母的Char ascii值
        int section = getSectionForPosition(position);

        //如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
        if (position == getPositionForSection(section)) {
            viewHolder.tvLetter.setVisibility(View.VISIBLE);
            viewHolder.viewDivider.setVisibility(View.VISIBLE);
            viewHolder.tvLetter.setText(mContent.getSortLetters());
        } else {
            viewHolder.tvLetter.setVisibility(View.GONE);
            viewHolder.viewDivider.setVisibility(View.GONE);
        }
        String name = this.mList.get(position).getEmpName();
        String backGroundColor = this.mList.get(position).getBackGroundColor();
        String facePhotoUrl = this.mList.get(position).getFacephoto();
        if (StringUtils.isEmpty(facePhotoUrl)) {
            viewHolder.tvHeader.setVisibility(View.VISIBLE);
            viewHolder.ivHeader.setVisibility(View.GONE);
            GradientDrawable myGrad = (GradientDrawable) viewHolder.tvHeader.getBackground();
            if (!StringUtils.isEmpty(backGroundColor)) {
                myGrad.setColor(Color.parseColor(backGroundColor));
            } else {
                myGrad.setColor(Color.parseColor("#38AFF7"));
            }
            if (!StringUtils.isEmpty(name) && name.length() >= 2) {
                viewHolder.tvHeader.setText(name.substring(name.length() - 2, name.length()));
            } else {
                viewHolder.tvHeader.setText(name);
            }
        } else {
            viewHolder.ivHeader.setVisibility(View.VISIBLE);
            viewHolder.tvHeader.setVisibility(View.GONE);
            ImageLoader.getInstance().displayImage(AppConfig.IMAGE_FONT_URL + facePhotoUrl, viewHolder.ivHeader, mOption);
        }
        viewHolder.tvTitle.setText(name);
        viewHolder.tvCompany.setText(this.mList.get(position).getJobName());
        viewHolder.cbChecked.setChecked(isSelected(mContent));

        return view;

    }

    public static class ViewHolder {
        public TextView tvLetter;
        public TextView tvTitle;
        public TextView tvHeader;
        public CircleImageView ivHeader;
        public TextView tvCompany;
        public CheckBox cbChecked;
        public View viewDivider;
    }

    /**
     * 根据ListView的当前位置获取分类的首字母的Char ascii值
     */
    public int getSectionForPosition(int position) {
        return mList.get(position).getSortLetters().charAt(0);
    }

    /**
     * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
     */
    public int getPositionForSection(int section) {
        for (int i = 0; i < getCount(); i++) {
            String sortStr = mList.get(i).getSortLetters();
            char firstChar = sortStr.toUpperCase(Locale.CHINESE).charAt(0);
            if (firstChar == section) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public Object[] getSections() {
        return null;
    }

    private boolean isSelected(PersonInfoEntity model) {
        for (int i = 0; i < mSelectedList.size(); i++) {
            if (StringUtils.equals(mSelectedList.get(i).getPersonId(), model.getPersonId())) {
                return true;
            }
        }
        return false;
    }

    public void toggleChecked(int position) {
        if (isSelected(mList.get(position))) {
            removeSelected(position);
        } else {
            setSelected(position);
        }

    }

    private void setSelected(int position) {
        mSelectedList.add(mList.get(position));
    }

    private void removeSelected(int position) {
        for (int i = 0; i < mSelectedList.size(); i++) {
            if (StringUtils.equals(mSelectedList.get(i).getPersonId(), mList.get(position).getPersonId())) {
                mSelectedList.remove(mSelectedList.get(i));
                break;
            }
        }
    }

    public List<PersonInfoEntity> getSelectedList() {
        return mSelectedList;
    }
}