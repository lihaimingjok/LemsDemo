package com.pcjz.lems.ui.application;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.pcjz.lems.R;
import com.pcjz.lems.business.base.BaseActivity;
import com.pcjz.lems.business.common.view.CircleFlowIndicator;
import com.pcjz.lems.business.common.view.ImagePagerAdapter;
import com.pcjz.lems.business.common.view.ViewFlow;
import com.pcjz.lems.business.config.AppConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * created by yezhengyu on 2017/5/23 11:37
 */
public class BrowseImageActivity extends BaseActivity {

    private int mCurrentIndex = 1;
    private ViewFlow mViewFlow;
    private CircleFlowIndicator mFlowIndicator;
    private TextView tvTitle;
    List<String> mImages = new ArrayList<String>();
    private int position;
    private ImageView mIvBack;

    @Override
    public void setView() {
        setContentView(R.layout.activity_browse_image);
    }

    @Override
    protected void initView() {
        tvTitle = (TextView) findViewById(R.id.tv_title);
        mIvBack = (ImageView) findViewById(R.id.iv_back);
        mFlowIndicator = (CircleFlowIndicator) findViewById(R.id.viewflowindic);
        mViewFlow = (ViewFlow) findViewById(R.id.viewflow);
        mViewFlow.setFlowIndicator(mFlowIndicator);
        mViewFlow.setTimeSpan(AppConfig.BANNER_TIME_SPAN);

        initData();
    }

    private void initData() {
        ArrayList<String> imageList = getIntent().getStringArrayListExtra("imageList");
        position = getIntent().getExtras().getInt("position");
        mImages.addAll(imageList);
        tvTitle.setText((position + 1) + "/" + mImages.size());
        initBanner(mImages);
    }

    @Override
    public void setListener() {
        mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mViewFlow.setOnViewSwitchListener(new ViewFlow.ViewSwitchListener() {
            @Override
            public void onSwitched(View view, int position) {
                mCurrentIndex = position + 1;
                tvTitle.setText(mCurrentIndex + "/" + mImages.size());
            }
        });
    }

    /**
     * 显示banner
     * @param
     */
    private void initBanner(int[] arr) {
        mViewFlow.setmSideBuffer(arr.length); // 实际图片张数，
        mViewFlow.setAdapter(new ImagePagerAdapter(BrowseImageActivity.this,
                arr).setInfiniteLoop(false));
        mViewFlow.setSelection(0/*arr.length * 1000*/); // 设置初始位置
        //mViewFlow.startAutoFlowTimer(); // 启动自动播放
    }

    private void initBanner(List<String> imageList) {
        mViewFlow.setmSideBuffer(imageList.size()); // 实际图片张数，
        mViewFlow.setAdapter(new ImagePagerAdapter(BrowseImageActivity.this,
                imageList).setInfiniteLoop(false));
        mViewFlow.setSelection(position);
        //mViewFlow.startAutoFlowTimer(); // 启动自动播放
    }
}
