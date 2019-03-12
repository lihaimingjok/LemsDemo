package com.pcjz.lems.ui.login;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pcjz.lems.R;
import com.pcjz.lems.business.common.utils.StatusBarUtil;
import com.pcjz.lems.business.context.AppContext;
import com.pcjz.lems.business.common.utils.PrefUtils;

import java.util.ArrayList;
import java.util.List;
//测试提交

public class GuideActivity extends Activity {

    private static final String TAG = "GuideActivity";

    /**
     * 功能引导页
     */
    private ViewPager mVpGuide;

    private Button mBtnStart;

    /**
     * 小灰点的父控件
     */
    private LinearLayout mDotGroup;

    /**
     * 小红点
     */
    private View mRedDot;

    /**
     * 相邻小灰点之间的距离
     */
    private int mDotDistance;
    private List<View> mViewList;
    private TextView mTvTitle;
    private TextView mTvSmallTitle;
    public static GuideActivity guideActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        guideActivity = this;
        setStatusBar();
        initView();
    }

    private void setStatusBar() {
        StatusBarUtil.setColor(this, ContextCompat.getColor(getApplicationContext(), R.color.white));
    }

    /**
     * 初始化页面
     */
    private void initView() {
        mVpGuide = (ViewPager) findViewById(R.id.vp_guide);
        mBtnStart = (Button) findViewById(R.id.btn_start);
        mDotGroup = (LinearLayout) findViewById(R.id.ll_dot_group);
        mRedDot = findViewById(R.id.view_red_dot);
        //标题名
        mTvTitle = (TextView) findViewById(R.id.tv_guide_title);
        mTvSmallTitle = (TextView) findViewById(R.id.tv_guide_small_title);
        //电话
        final TextView tvTel = (TextView) findViewById(R.id.tv_tel);

        mViewList = new ArrayList<>();
        LayoutInflater lf = getLayoutInflater().from(GuideActivity.this);
        mViewList.add(lf.inflate(R.layout.guide_viewpager1, null));
        mViewList.add(lf.inflate(R.layout.guide_viewpager2, null));
        mViewList.add(lf.inflate(R.layout.guide_viewpager3, null));
        mViewList.add(lf.inflate(R.layout.guide_viewpager4, null));
        mViewList.add(lf.inflate(R.layout.guide_viewpager5, null));

        // 获取控件树，对 onLayout 结束事件进行监听
        mDotGroup.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        // OnGlobalLayoutListener可能会被多次触发，
                        // 因此在得到了高度之后，要将 OnGlobalLayoutListener 注销掉
                        mDotGroup.getViewTreeObserver()
                                .removeGlobalOnLayoutListener(this);
                        // 计算相邻小灰点之间的距离
                        mDotDistance = mDotGroup.getChildAt(1).getLeft()
                                - mDotGroup.getChildAt(0).getLeft();
                        Log.d(TAG, "相邻小灰点之间的距离：" + mDotDistance);
                    }
                });

        mVpGuide.setAdapter(new GuideAdapter());
        mVpGuide.setOnPageChangeListener(new GuidePageChangeListener());

        mBtnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 按钮一旦被点击，更新 SharedPreferences
                PrefUtils.setBoolean(GuideActivity.this, PrefUtils.GUIDE_SHOWED, true);
                // 跳转到主页面
                startActivity(new Intent(GuideActivity.this, LoginActivity.class));
            }
        });

        tvTel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent phoneIntent = new Intent("android.intent.action.DIAL", Uri.parse("tel:" + tvTel.getText().toString()));
                startActivity(phoneIntent);
            }
        });
    }


    /**
     * 适配器
     */
    class GuideAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return mViewList.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(mViewList.get(position));
            return mViewList.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mViewList.get(position));
        }
    }

    /**
     * 滑动监听
     */
    class GuidePageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }

        // 页面滑动时回调此方法
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            // 页面滑动过程中，小红点移动的距离
            int distance = (int) (mDotDistance * (positionOffset + position));
            Log.d(TAG, "小红点移动的距离：" + distance);
            // 获取小红点的布局参数
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mRedDot
                    .getLayoutParams();
            // 修改小红点的左边缘和父控件(RelativeLayout)左边缘的距离
            params.leftMargin = distance;
            // 修改小红点的布局参数
            mRedDot.setLayoutParams(params);
        }

        // 某个页面被选中时回调此方法
        @Override
        public void onPageSelected(int position) {
            if (position == 0) {
                mTvTitle.setText(R.string.guide_title_01);
                mTvSmallTitle.setText(R.string.guide_small_title_01);
            } else if (position == 1) {
                mTvTitle.setText(R.string.guide_title_02);
                mTvSmallTitle.setText(R.string.guide_small_title_02);
            } else if (position == 2) {
                mTvTitle.setText(R.string.guide_title_03);
                mTvSmallTitle.setText(R.string.guide_small_title_03);
            } else if (position == 3) {
                mTvTitle.setText(R.string.guide_title_04);
                mTvSmallTitle.setText(R.string.guide_small_title_04);
            } else if (position == 4) {
                mTvTitle.setText(R.string.guide_title_05);
                mTvSmallTitle.setText(R.string.guide_small_title_05);
            }
         }
    }

    private long exitTime = 0;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                AppContext.showToast(R.string.press_again_exit_app);
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
