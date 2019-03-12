package com.pcjz.lems.business.common.view;

/*
 * Copyright 2014 trinea.cn All right reserved. This software is the confidential and proprietary information of
 * trinea.cn ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with trinea.cn.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.pcjz.lems.R;
import com.pcjz.lems.business.common.utils.TDevice;
import com.pcjz.lems.business.common.utils.TLog;
import com.pcjz.lems.business.config.AppConfig;
import com.pcjz.lems.business.context.AppContext;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;


/**
 * @Description: 图片适配�?
 */
public class ImagePagerAdapter extends BaseAdapter {

    private Context context;
    private List<String> imageIdList;
    private List<String> linkUrlArray;
    private int size;
    private boolean isInfiniteLoop;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    private int[] imgId;

    private DisplayImageOptions mOption = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.load_pho_default_image)
            .showImageForEmptyUri(R.drawable.load_pho_default_image)
            .showImageOnFail(R.drawable.load_pho_default_image)
            .cacheInMemory(true)
            .cacheOnDisc(false).bitmapConfig(Bitmap.Config.RGB_565)
            .imageScaleType(ImageScaleType.EXACTLY).build();
    private List<Bitmap> bitmaps;

    public ImagePagerAdapter(Context context, List<String> imageIdList) {
        this.context = context;
        this.imageIdList = imageIdList;
        if (imageIdList != null) {
            this.size = imageIdList.size();
        }
        isInfiniteLoop = false;
        // 初始化imageLoader 否则会报�?
        imageLoader = ImageLoader.getInstance();
        /*
         * imageLoader.init(ImageLoaderConfiguration.createDefault(context));
		 * options = new DisplayImageOptions.Builder()
		 * .showStubImage(R.drawable.ic_launcher) // 设置图片下载期间显示的图�?
		 * .showImageForEmptyUri(R.drawable.meinv) // 设置图片Uri为空或是错误的时候显示的图片
		 * .showImageOnFail(R.drawable.meinv) // 设置图片加载或解码过程中发生错误显示的图�?
		 * .cacheInMemory(true) // 设置下载的图片是否缓存在内存�? .cacheOnDisc(true) //
		 * 设置下载的图片是否缓存在SD卡中 .build();
		 */
    }

    public ImagePagerAdapter(Context context, int[] imgid) {
        this.context = context;
        this.imgId = imgid;
        if (imgid != null) {
            this.size = imgid.length;
        }
        isInfiniteLoop = false;
        // 初始化imageLoader 否则会报�?
        imageLoader = ImageLoader.getInstance();
    }

    @Override
    public int getCount() {
        // Infinite loop
        return isInfiniteLoop ? Integer.MAX_VALUE : /*imgId.length;*/imageIdList.size();
    }

    /**
     * get really position
     *
     * @param position
     * @return
     */
    private int getPosition(int position) {
        return isInfiniteLoop ? position % size : position;
    }

    @Override
    public View getView(final int position, View view, ViewGroup container) {
        final ViewHolder holder;
        if (view == null) {
            view = LayoutInflater.from(container.getContext()).inflate(
                    R.layout.layout_imageview, null);
            holder = new ViewHolder();
            holder.imageView = (ImageView) view.findViewById(R.id.imageview);//new ImageView(context);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        String imagePath = imageIdList.get(position);
        if (imagePath.contains(AppConfig.IMAGE_FONT_URL)) {
            imageLoader.displayImage(imagePath, holder.imageView, mOption, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String s, View view) {

                }

                @Override
                public void onLoadingFailed(String s, View view, FailReason failReason) {

                }

                @Override
                public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                    ((ImageView) view).setImageBitmap(bitmap);
                    //获取屏幕信息
                    //720
                    int screenWidth = TDevice.getWidthPixels(context);
                    //1280
                    int screenHeight = TDevice.getHeightPixels(context);

                    final float scale1 = AppContext.mResource.getDisplayMetrics().density;
                    float scale = 1;
                    //966
                    int rlHeight = screenHeight - (int) ((50) * scale1 + TDevice.getStatusBarHeight(context));
                    //720
                    int rlWidth = screenWidth;
                    float imageHeight = bitmap.getHeight();//basedata.getData().getImageHeight();
                    float imageWidth = bitmap.getWidth();//basedata.getData().getImageWidth();
                    FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) view.getLayoutParams();
                    if ((imageHeight / rlHeight) > (imageWidth / rlWidth)) {
                        scale = imageHeight / rlHeight;
                        lp.width = (int) (imageWidth / scale);
                        lp.gravity = Gravity.CENTER;//addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                        lp.height = rlHeight;
                    } else {
                        scale = imageWidth / screenWidth;
                        lp.width = screenWidth;
                        lp.gravity = Gravity.CENTER;//lp.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                        lp.height = (int) (imageHeight / scale);
                    }
                    view.setLayoutParams(lp);
                }

                @Override
                public void onLoadingCancelled(String s, View view) {

                }
            });
        } else {
            imageLoader.displayImage("file://" + imagePath, holder.imageView);
        }
        holder.imageView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
//				AppContext
//						.showToast("点击了第" + getPosition(position) + "张banner");
            }
        });

        return view;
    }

    private Bitmap readPicture(String path) {
        InputStream is = null;
        try {
            is = new FileInputStream(path);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
        TLog.log("before");
        BitmapFactory.Options opts = new BitmapFactory.Options();
        //opts.inTempStorage = new byte[100 * 1024];
        opts.inPreferredConfig = Bitmap.Config.RGB_565;
        opts.inPurgeable = true;
        //opts.inSampleSize = 4;
        opts.inInputShareable = true;
        Bitmap bm = BitmapFactory.decodeStream(is, null, opts);
        return bm;
    }

    private static class ViewHolder {

        ImageView imageView;
    }

    /**
     * @return the isInfiniteLoop
     */
    public boolean isInfiniteLoop() {
        return isInfiniteLoop;
    }

    /**
     * @param isInfiniteLoop the isInfiniteLoop to set
     */
    public ImagePagerAdapter setInfiniteLoop(boolean isInfiniteLoop) {
        this.isInfiniteLoop = isInfiniteLoop;
        return this;
    }

    @Override
    public Object getItem(int arg0) {
        return arg0;
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

}
