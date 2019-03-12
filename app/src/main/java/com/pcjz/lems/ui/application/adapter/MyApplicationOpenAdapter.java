package com.pcjz.lems.ui.application.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.pcjz.lems.R;
import com.pcjz.lems.business.base.BaseListAdapter;
import com.pcjz.lems.business.common.downloadapp.bean.AppInfoBean;
import com.pcjz.lems.business.common.utils.CommUtil;

/**
 * created by yezhengyu on 2017/5/25 19:32
 */
public class MyApplicationOpenAdapter extends BaseListAdapter {
    private final Context context;
    private DisplayImageOptions mOption = new DisplayImageOptions.Builder()
            .showStubImage(R.color.white)
            .showImageForEmptyUri(R.color.white)
            .showImageOnFail(R.color.white).cacheInMemory(true)
            .cacheOnDisc(false).bitmapConfig(Bitmap.Config.RGB_565)
            .imageScaleType(ImageScaleType.EXACTLY).build();

    public MyApplicationOpenAdapter(Context context) {
        this.context = context;
    }

    @Override
    protected View getRealView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null || convertView.getTag() == null) {
            convertView = getLayoutInflater(parent.getContext()).inflate(
                    R.layout.item_my_applications_open, null);
            holder = new ViewHolder(convertView);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (_data != null && _data.size() > 0) {
            final AppInfoBean appBean = (AppInfoBean) _data.get(position);
            ImageLoader.getInstance().displayImage(appBean.getAppIcon(), holder.ivAppIcon, mOption);
            holder.tvAppName.setText(appBean.getAppName());
            holder.tvAppSize.setText("占用空间：" + appBean.getFileSize() + "MB");
            holder.tvOpenApp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //打开App
                    CommUtil.getInstance().doStartApplicationWithPackageName(context, appBean.getAppPackageName());
                }
            });
        }
        return convertView;

    }

    public class ViewHolder {

        private final ImageView ivAppIcon;
        private final TextView tvAppName;
        private final TextView tvAppSize;
        private final TextView tvOpenApp;

        public ViewHolder(View view) {
            ivAppIcon = (ImageView) view.findViewById(R.id.iv_appinfo_icon);
            tvAppName = (TextView) view.findViewById(R.id.tv_appinfo_name);
            tvAppSize = (TextView) view.findViewById(R.id.tv_appinfo_filesize);
            //打开
            tvOpenApp = (TextView) view.findViewById(R.id.tv_open_app);
            view.setTag(this);
        }
    }
}
