package com.pcjz.lems.business.widget.lineChart;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pcjz.lems.R;
import com.pcjz.lems.business.common.utils.TDevice;
import com.pcjz.lems.ui.workrealname.manageequipment.CommonMethond;
import com.pcjz.lems.ui.workrealname.projectsituation.bean.LineEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * created by yezhengyu on 2017/10/9 17:39
 */

public class LineChartLayout2 extends LinearLayout {
    private Context mContext;
    private List<LineEntity> mDatas;
    private LineChartView2 mLineChartView;
    private RelativeLayout relativeLayout;
    private View chartPop;
    private int maxValue;
    private RelativeLayout linearLayout;
    private int screenWidth;
    private int screenHeight;

    public LineChartLayout2(Context context) {
        this(context, null);
    }

    public LineChartLayout2(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LineChartLayout2(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    public void refreshLineView(List<LineEntity> datas) {
        mDatas = datas;
        View view = LayoutInflater.from(mContext).inflate(R.layout.view_line_chart2, null);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        setOrientation(VERTICAL);
        addView(view, lp);
        relativeLayout = (RelativeLayout) view.findViewById(R.id.rl_chart_contain);
        mLineChartView = (LineChartView2) view.findViewById(R.id.my_line_chart);
        linearLayout = (RelativeLayout) view.findViewById(R.id.ll_contain_number);
        DisplayMetrics dm =getResources().getDisplayMetrics();
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mLineChartView.getLayoutParams();
        params.width = initWidth(mDatas);
        mLineChartView.setLayoutParams(params);
        relativeLayout.removeView(chartPop);
        initMaxDatas();
        initLineChartView();
        postInvalidate();
        initViewXml();
    }

    private void initViewXml() {
        int intMode = CommonMethond.getInstance().getIntMode(maxValue);
        int value;
        if (maxValue % intMode == 0) {
            value = maxValue;
        } else {
            value = maxValue + maxValue % intMode;
        }
        //获取控件的高度度要减去一个状态栏的高度
        int linearLayoutHeight = screenHeight - dp2px(205) - TDevice.getStatusHeight(mContext);
        for (int i = value / intMode; i >= 0; i--) {
            TextView textView = new TextView(mContext);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT);
            int top = (linearLayoutHeight * 8 / 9) - (7 * linearLayoutHeight / 9) * i / (value / intMode) - dp2px(5);
            params.setMargins(0, top, dp2px(6), 0);
            textView.setTextColor(getResources().getColor(R.color.txt_gray_shallow));
            textView.setTextSize(10);
            textView.setText(i * intMode + "");
            textView.setGravity(Gravity.RIGHT);
            textView.setLayoutParams(params);
            linearLayout.addView(textView);
        }
    }


    private int initWidth(List<LineEntity> datas) {
        int width = screenWidth / 8 + screenWidth * datas.size() / 8 ;
        if (width < screenWidth) {
            return screenWidth;
        } else {
            return width;
        }
    }

    private void initMaxDatas() {
        //初始化datas
        for (int i = 0; i < mDatas.size(); i++) {
            String time = mDatas.get(i).getTime();
            String subTime = time.substring(time.length() - 5, time.length());
            if (subTime != null) {
                mDatas.get(i).setTime(subTime);
            }
        }
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < mDatas.size(); i++) {
            list.add(mDatas.get(i).getElevators());
            list.add(mDatas.get(i).getTowerCranes());
        }
        Collections.sort(list);
        maxValue = list.get(list.size() - 1).intValue();
        if (maxValue <= 0) {
            maxValue = 10;
        }
    }

    private void initLineChartView() {
        mLineChartView.setDatas(mDatas, maxValue);
        mLineChartView.setListener(new LineChartView2.getNumberListener() {
            @Override
            public void getNumber(int number, int x, int y) {
                relativeLayout.removeView(chartPop);
                chartPop = LayoutInflater.from(mContext).inflate(R.layout.view_line_chart_pop, null);
                //塔吊起重机
                TextView tvTowerCrane = (TextView) chartPop.findViewById(R.id.tv_tower_crane);
                //施工升降机
                TextView tvElevator = (TextView) chartPop.findViewById(R.id.tv_elevator);
                int selectHeights = 0;
                if (mDatas != null) {
                    for (int i = 0; i < mDatas.size(); i++) {
                        int towerCranes = mDatas.get(number).getTowerCranes();
                        int elevators = mDatas.get(number).getElevators();
                        tvTowerCrane.setText(mContext.getResources().getString(R.string.number_tower_crane, towerCranes + ""));
                        tvElevator.setText(mContext.getResources().getString(R.string.number_elevator, elevators + ""));
                        if (towerCranes >= elevators) {
                            selectHeights = towerCranes;
                        } else {
                            selectHeights = elevators;
                        }
                    }
                }
                chartPop.measure(0, 0);
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
                if (x - chartPop.getMeasuredWidth() / 2 >= 0) {
                    layoutParams.leftMargin = x - chartPop.getMeasuredWidth() / 2;
                } else {
                    layoutParams.leftMargin = 0;
                }
                float division = (float) (maxValue - selectHeights) / maxValue;
                int topMargin = (int) ((division * 7 + 1) * y / 9 - dp2px(10) - chartPop.getMeasuredHeight());
                if (topMargin >= 0) {
                    layoutParams.topMargin = topMargin;
                } else {
                    layoutParams.topMargin = 0;
                }
                chartPop.setLayoutParams(layoutParams);
                relativeLayout.addView(chartPop);
            }
        });
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }
}
