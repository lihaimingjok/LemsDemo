package com.pcjz.lems.ui.application.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.pcjz.lems.R;
import com.pcjz.lems.business.base.BaseListAdapter;
import com.pcjz.lems.business.common.downloadapp.bean.AppInfoBean;
import com.pcjz.lems.business.common.utils.CommUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * created by yezhengyu on 2017/5/22 16:58
 */
public class MoreApplicationAdapter extends BaseListAdapter {

    private final Context context;
    private final String mode;
    private DisplayImageOptions mOption = new DisplayImageOptions.Builder()
            .showStubImage(R.color.white)
            .showImageForEmptyUri(R.color.white)
            .showImageOnFail(R.color.white).cacheInMemory(true)
            .cacheOnDisc(false).bitmapConfig(Bitmap.Config.RGB_565)
            .imageScaleType(ImageScaleType.EXACTLY).build();

    public MoreApplicationAdapter(Context context, String mode) {
        this.context = context;
        this.mode = mode;
        init();
    }

    // 用来记录按钮状态的Map
    public static Map<Integer, Boolean> isChecked;

    // 初使化操作，默认都是false
    private void init() {
        isChecked = new HashMap<Integer, Boolean>();
        for (int i = 0; i < _data.size(); i++) {
            isChecked.put(i, false);
        }
    }

    @Override
    protected View getRealView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null || convertView.getTag() == null) {
            convertView = getLayoutInflater(parent.getContext()).inflate(
                    R.layout.item_more_applications, null);
            holder = new ViewHolder(convertView);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (_data != null && _data.size() > 0) {
            //避免复用，清空状态
            holder.progressBarDownload.setProgress(0);
            holder.tvDownload.setText("下载");
            holder.tvDownload.setTextColor(context.getResources().getColor(R.color.common_blue_color));
            holder.tvDownload.setVisibility(View.VISIBLE);
            holder.tvPauseDownload.setVisibility(View.GONE);
            holder.tvInstallApk.setVisibility(View.GONE);
            holder.tvOpenApp.setVisibility(View.GONE);

            final AppInfoBean appBean = (AppInfoBean) _data.get(position);
            ImageLoader.getInstance().displayImage(appBean.getAppIcon(), holder.ivAppIcon, mOption);
            holder.tvAppName.setText(appBean.getAppName());
            if ("more".equals(mode)) {
                holder.tvAppRemark.setText(appBean.getRemark());
            } else {
                holder.tvAppRemark.setVisibility(View.GONE);
            }
            holder.tvAppSize.setText("占用空间：" + appBean.getFileSize() + "MB");

            //if ("more".equals(mode)) {
            int progress = (int) (appBean.getFinished() * 1.0f / appBean.getLength() * 100);
            if (appBean.getLength() != 0) {
                holder.progressBarDownload.setProgress(progress);
            } else {
                if (appBean.isInstall()) {
                    holder.tvDownload.setVisibility(View.GONE);
                    holder.tvPauseDownload.setVisibility(View.GONE);
                    holder.tvInstallApk.setVisibility(View.GONE);
                    holder.tvOpenApp.setVisibility(View.VISIBLE);
                    holder.tvOpenApp.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //打开App
                            CommUtil.getInstance().doStartApplicationWithPackageName(context, appBean.getAppPackageName());
                        }
                    });
                }
                holder.progressBarDownload.setProgress(0);
            }
            if (progress > 0 && progress < 100) {
                holder.tvDownload.setVisibility(View.VISIBLE);
                holder.tvPauseDownload.setVisibility(View.GONE);
                holder.tvDownload.setText("继续");
                holder.tvDownload.setTextColor(context.getResources().getColor(R.color.off_line_pause));
            }
            if (progress == 100) {
                if (appBean.isUpdate()) {
                    holder.tvDownload.setVisibility(View.VISIBLE);
                    holder.tvPauseDownload.setVisibility(View.GONE);
                    holder.tvInstallApk.setVisibility(View.GONE);
                    holder.tvOpenApp.setVisibility(View.GONE);
                    holder.tvDownload.setText("更新");
                    holder.progressBarDownload.setProgress(0);
                    holder.tvDownload.setTextColor(context.getResources().getColor(R.color.off_line_pause));
                } else {
                    if (appBean.isInstall()) {
                        holder.tvDownload.setVisibility(View.GONE);
                        holder.tvPauseDownload.setVisibility(View.GONE);
                        holder.tvInstallApk.setVisibility(View.GONE);
                        holder.progressBarDownload.setProgress(0);
                        holder.tvOpenApp.setVisibility(View.VISIBLE);
                        holder.tvOpenApp.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //打开App
                                CommUtil.getInstance().doStartApplicationWithPackageName(context, appBean.getAppPackageName());
                            }
                        });
                    } else {
                        holder.tvDownload.setVisibility(View.GONE);
                        holder.tvPauseDownload.setVisibility(View.GONE);
                        holder.tvOpenApp.setVisibility(View.GONE);
                        holder.progressBarDownload.setProgress(0);
                        holder.tvInstallApk.setVisibility(View.VISIBLE);
                        holder.tvInstallApk.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //安装apk
                                CommUtil.getInstance().installApk(context, appBean.getAppName());
                            }
                        });
                    }
                }
            }
            /*}只有更新的item
            else {
                holder.tvDownload.setVisibility(View.VISIBLE);
                holder.tvPauseDownload.setVisibility(View.GONE);
                holder.tvInstallApk.setVisibility(View.GONE);
                holder.tvOpenApp.setVisibility(View.GONE);
                holder.tvDownload.setText("更新");
                holder.progressBarDownload.setProgress(0);
                holder.tvDownload.setTextColor(context.getResources().getColor(R.color.off_line_pause));
            }*/
            //更新
            holder.tvDownload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DownloadCommon.getInstance().downloadClick(context, appBean, position);
                }
            });
            holder.tvPauseDownload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DownloadCommon.getInstance().pauseClick(context, appBean);
                }
            });
        }
        return convertView;

    }

    public class ViewHolder {

        private final ImageView ivAppIcon;
        private final TextView tvAppName;
        private final TextView tvAppRemark;
        private final TextView tvAppSize;
        private final TextView tvDownload;
        private final TextView tvPauseDownload;
        private final ProgressBar progressBarDownload;
        private final TextView tvInstallApk;
        private final TextView tvOpenApp;

        public ViewHolder(View view) {
            ivAppIcon = (ImageView) view.findViewById(R.id.iv_appinfo_icon);
            tvAppName = (TextView) view.findViewById(R.id.tv_appinfo_name);
            tvAppRemark = (TextView) view.findViewById(R.id.tv_appinfo_remark);
            tvAppSize = (TextView) view.findViewById(R.id.tv_appinfo_filesize);
            //下载按钮
            tvDownload = (TextView) view.findViewById(R.id.tv_download_click);
            //下载暂停按钮
            tvPauseDownload = (TextView) view.findViewById(R.id.tv_pause_click);
            //下载progressbar
            progressBarDownload = (ProgressBar) view.findViewById(R.id.progressBar);
            //安装
            tvInstallApk = (TextView) view.findViewById(R.id.tv_install_apk);
            //打开
            tvOpenApp = (TextView) view.findViewById(R.id.tv_open_app);
            //view.setTag(this);
        }
    }
}
