package com.pcjz.lems.ui.application;

import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.pcjz.lems.R;
import com.pcjz.lems.business.base.BaseActivity;
import com.pcjz.lems.business.base.basebean.BaseData;
import com.pcjz.lems.business.common.downloadapp.bean.AppInfoBean;
import com.pcjz.lems.business.common.downloadapp.event.EventMessage;
import com.pcjz.lems.business.common.downloadapp.services.DownloadService;
import com.pcjz.lems.business.common.utils.CommUtil;
import com.pcjz.lems.business.common.utils.StringUtils;
import com.pcjz.lems.business.common.utils.TLog;
import com.pcjz.lems.business.config.AppConfig;
import com.pcjz.lems.business.constant.ResultStatus;
import com.pcjz.lems.business.constant.SysCode;
import com.pcjz.lems.business.context.AppContext;
import com.pcjz.lems.business.storage.downloaddb.AppInfoDaoImpl;
import com.pcjz.lems.business.webapi.MainApi;
import com.pcjz.lems.ui.application.adapter.DownloadCommon;
import com.pcjz.lems.ui.application.bean.AppDetailPhoto;
import com.pcjz.lems.ui.application.bean.AppInfoDetailInfo;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * created by yezhengyu on 2017/5/23 11:53
 */
public class ApplicationDeatilActivity extends BaseActivity {

    private ImageView mIvAppIcon;
    private TextView mTvAppName;
    private TextView mTvAppSize;
    private TextView mTvAppRemark;
    private LinearLayout mLinearItem;
    private String mId;
    private RelativeLayout mRlBack;
    private ImageLoader mImageLoader;

    private TextView tvDownload;
    private TextView tvPauseDownload;
    private ProgressBar progressBarDownload;
    private TextView tvInstallApk;
    private TextView tvOpenApp;

    private DisplayImageOptions mOption = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.default_no_pic_icon)
            .showImageForEmptyUri(R.drawable.default_no_pic_icon)
            .showImageOnFail(R.drawable.default_no_pic_icon)
            .cacheInMemory(true)
            .cacheOnDisc(false).bitmapConfig(Bitmap.Config.RGB_565)
            .imageScaleType(ImageScaleType.EXACTLY).build();

    private AppInfoDaoImpl mDao;
    private AppInfoBean mAppbean;

    @Override
    public void setView() {
        setContentView(R.layout.activity_appinfo_detail);
        EventBus.getDefault().register(this);
        mDao = new AppInfoDaoImpl(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void initView() {
        mId = getIntent().getExtras().getString("id");
        mRlBack = (RelativeLayout) findViewById(R.id.rl_back);
        mIvAppIcon = (ImageView) findViewById(R.id.iv_appinfo_icon);
        mTvAppName = (TextView) findViewById(R.id.tv_appinfo_name);
        mTvAppSize = (TextView) findViewById(R.id.tv_appinfo_filesize);
        mTvAppRemark = (TextView) findViewById(R.id.tv_appinfo_remark);
        mLinearItem = (LinearLayout) findViewById(R.id.ll_appinfo_detail_image);
        mImageLoader = ImageLoader.getInstance();

        //下载逻辑
        //下载按钮
        tvDownload = (TextView) findViewById(R.id.tv_download_click);
        //下载暂停按钮
        tvPauseDownload = (TextView) findViewById(R.id.tv_pause_click);
        //下载progressbar
        progressBarDownload = (ProgressBar) findViewById(R.id.progressBar);
        //安装
        tvInstallApk = (TextView) findViewById(R.id.tv_install_apk);
        //打开
        tvOpenApp = (TextView) findViewById(R.id.tv_open_app);

        tvDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long currentTime = Calendar.getInstance().getTimeInMillis();
                if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
                    lastClickTime = currentTime;
                    Intent startIntent = new Intent(ApplicationDeatilActivity.this, DownloadService.class);
                    startIntent.setAction(DownloadService.ACTION_START);
                    startIntent.putExtra("appBean", mAppbean);
                    ApplicationDeatilActivity.this.startService(startIntent);
                }
            }
        });
        tvPauseDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DownloadCommon.getInstance().pauseClick(ApplicationDeatilActivity.this, mAppbean);
            }
        });
    }

    public static final int MIN_CLICK_DELAY_TIME = 1500;
    private long lastClickTime = 0;

    private void initDownload() {
        List<AppInfoBean> appInfoBeans = mDao.getApps(SysCode.APP_DOWNLOAD_URL);
        if (appInfoBeans != null && appInfoBeans.size() != 0) {
            for (int i = 0; i < appInfoBeans.size(); i++) {
                if (mAppbean.getId().equals(appInfoBeans.get(i).getId())) {
                    mAppbean.setFinished(appInfoBeans.get(i).getFinished());
                    mAppbean.setLength(appInfoBeans.get(i).getLength());
                    String versionCode = mAppbean.getVersionCode();
                    if (versionCode != null) {
                        if (!versionCode.equals(appInfoBeans.get(i).getVersionCode())) {
                            mAppbean.setIsUpdate(true);
                        }
                    }
                    int progress = (int) (mAppbean.getFinished() * 1.0f / mAppbean.getLength() * 100);
                    if (mAppbean.getLength() != 0) {
                        progressBarDownload.setProgress(progress);
                    }
                    if (progress > 0 && progress < 100) {
                        tvDownload.setVisibility(View.VISIBLE);
                        tvPauseDownload.setVisibility(View.GONE);
                        tvDownload.setText("继续");
                        tvDownload.setTextColor(getResources().getColor(R.color.off_line_pause));
                    }
                    if (progress == 100) {
                        if (mAppbean.isUpdate()) {
                            tvDownload.setVisibility(View.VISIBLE);
                            tvPauseDownload.setVisibility(View.GONE);
                            tvInstallApk.setVisibility(View.GONE);
                            tvOpenApp.setVisibility(View.GONE);
                            tvDownload.setText("更新");
                            progressBarDownload.setProgress(0);
                            tvDownload.setTextColor(this.getResources().getColor(R.color.off_line_pause));
                        } else {
                            if (mAppbean.isInstall()) {
                                tvDownload.setVisibility(View.GONE);
                                tvPauseDownload.setVisibility(View.GONE);
                                tvInstallApk.setVisibility(View.GONE);
                                progressBarDownload.setProgress(0);
                                tvOpenApp.setVisibility(View.VISIBLE);
                                tvOpenApp.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        //打开App
                                        CommUtil.getInstance().doStartApplicationWithPackageName(ApplicationDeatilActivity.this, mAppbean.getAppPackageName());
                                    }
                                });
                            } else {
                                tvDownload.setVisibility(View.GONE);
                                tvPauseDownload.setVisibility(View.GONE);
                                tvOpenApp.setVisibility(View.GONE);
                                progressBarDownload.setProgress(0);
                                tvInstallApk.setVisibility(View.VISIBLE);
                                tvInstallApk.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        //安装apk
                                        CommUtil.getInstance().installApk(ApplicationDeatilActivity.this, mAppbean.getAppName());
                                    }
                                });
                            }
                        }
                    }
                    break;
                }
            }
        }
    }

    @Override
    public void setListener() {
        mRlBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void initWebData() {
        JSONObject obj = new JSONObject();
        HttpEntity entity = null;
        try {
            JSONObject obj0 = new JSONObject();
            obj.put("id", mId);
            obj0.put("params", obj);
            String s = obj0.toString();
            entity = new StringEntity(s, "UTF-8");
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return;
        }
        MainApi.requestCommon(this, AppConfig.GET_APPINFO_DETAIL_URL, entity, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                try {
                    String httpResult = new String(bytes);
                    TLog.log("getAppInfoDetail : " + httpResult);
                    Type type = new TypeToken<BaseData<AppInfoDetailInfo>>() {
                    }.getType();
                    BaseData<AppInfoDetailInfo> datas = new Gson().fromJson(httpResult, type);
                    if (StringUtils.equals(datas.getCode(), ResultStatus.SUCCESS)) {
                        AppInfoDetailInfo appInfo = datas.getData();
                        if (appInfo != null) {
                            refreshView(appInfo);
                        }
                        return;
                    } else {
                        AppContext.showToast(datas.getMsg());
                        return;
                    }
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                }
                AppContext.showToast("获取应用详情失败！");
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                AppContext.showToast("获取应用详情失败！");
            }
        });
    }

    private void refreshView(AppInfoDetailInfo appInfo) {
        //赋值给appBean
        mAppbean = new AppInfoBean();
        mAppbean.setId(appInfo.getId());
        mAppbean.setUrl(SysCode.APP_DOWNLOAD_URL + appInfo.getAppAndroidPath());
        mAppbean.setAppName(appInfo.getName());
        mAppbean.setAppIcon(AppConfig.IMAGE_FONT_URL + appInfo.getAppIcon());
        mAppbean.setFileSize(appInfo.getFileSize());
        mAppbean.setVersionCode(appInfo.getVersionCode());
        mAppbean.setAppPackageName(appInfo.getAppPackageName());
        //用于获取所有数据库中的appBean
        mAppbean.setBasicUrl(SysCode.APP_DOWNLOAD_URL);

        if (appInfo.getAppPackageName() != null) {
            boolean isInstall = CommUtil.getInstance().isInstalled(this, appInfo.getAppPackageName());
            mAppbean.setIsInstall(isInstall);
        }

        //初始化下载状态
        initDownload();
        //更新页面数据
        mImageLoader.displayImage(AppConfig.IMAGE_FONT_URL + appInfo.getAppIcon(), mIvAppIcon, mOption);
        if (!StringUtils.isEmpty(appInfo.getName())) {
            mTvAppName.setText(appInfo.getName());
        }
        mTvAppSize.setText(appInfo.getFileSize() + "MB");
        if (!StringUtils.isEmpty(appInfo.getRemark())) {
            mTvAppRemark.setText(appInfo.getRemark());
        }
        List<AppDetailPhoto> photos = appInfo.getPhotos();
        final ArrayList<String> photoUrls = new ArrayList<>();
        for (int i = 0; i < photos.size(); i++) {
            photoUrls.add(AppConfig.IMAGE_FONT_URL + photos.get(i).getPhotoPath());
        }
        if (photoUrls != null && photoUrls.size() != 0) {
            ImageView view;
            for (int i = 0; i < photoUrls.size(); i++) {
                view = (ImageView) LayoutInflater.from(this).inflate(R.layout.layout_simple_imageview, null);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(CommUtil.dp2px(this, 156), CommUtil.dp2px(this, 275));
                view.setId(i);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = v.getId();
                        Intent intent = new Intent(ApplicationDeatilActivity.this, BrowseImageActivity.class);
                        intent.putStringArrayListExtra("imageList", photoUrls);
                        intent.putExtra("position", position);
                        startActivity(intent);
                    }
                });
                mImageLoader.displayImage(photoUrls.get(i), view, mOption);
                mLinearItem.addView(view, lp);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventMessage(EventMessage eventMessage) {
        switch (eventMessage.getType()) {
            case 2://下载完成
                AppInfoBean appBean1 = (AppInfoBean) eventMessage.getObject();
                Toast.makeText(this, appBean1.getAppName() + "已下载完成", Toast.LENGTH_SHORT).show();
                updateFinished(appBean1);
                break;
            case 3://下载进度刷新
                AppInfoBean appBean2 = (AppInfoBean) eventMessage.getObject();
                updateProgress(appBean2);
                break;
            case 4://点击暂停
                AppInfoBean appBean3 = (AppInfoBean) eventMessage.getObject();
                updatePause(appBean3);
                break;
        }
    }

    private void updateFinished(final AppInfoBean appBean1) {
        //如果是下载或更新完就安装，不存在打开或更新一说
        tvDownload.setVisibility(View.GONE);
        tvPauseDownload.setVisibility(View.GONE);
        tvOpenApp.setVisibility(View.GONE);
        progressBarDownload.setProgress(0);
        tvInstallApk.setVisibility(View.VISIBLE);
        tvInstallApk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //安装apk
                CommUtil.getInstance().installApk(ApplicationDeatilActivity.this, appBean1.getAppName());
            }
        });
    }

    private void updatePause(AppInfoBean appBean3) {
        int progress = (int) (appBean3.getFinished() * 1.0f / appBean3.getLength() * 100);
        tvPauseDownload.setVisibility(View.GONE);
        tvDownload.setVisibility(View.VISIBLE);
        tvDownload.setText("继续");
        tvDownload.setTextColor(getResources().getColor(R.color.off_line_pause));
        progressBarDownload.setProgress(progress);
    }

    public void updateProgress(AppInfoBean appBean2) {
        int progress = (int) (appBean2.getFinished() * 1.0f / appBean2.getLength() * 100);
        if (progress > 0 && progress < 100) {
            tvDownload.setVisibility(View.GONE);
            tvPauseDownload.setVisibility(View.VISIBLE);
        }
        progressBarDownload.setProgress(progress);
        TLog.log("finished::" + appBean2.getFinished());
        TLog.log("progress" + progress);
    }
}
