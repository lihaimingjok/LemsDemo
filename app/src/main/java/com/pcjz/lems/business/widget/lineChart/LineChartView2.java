package com.pcjz.lems.business.widget.lineChart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.Rect;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import com.pcjz.lems.business.common.utils.TLog;
import com.pcjz.lems.ui.workrealname.manageequipment.CommonMethond;
import com.pcjz.lems.ui.workrealname.projectsituation.bean.LineEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * created by yezhengyu on 2017/9/25 17:48
 */

public class LineChartView2 extends View {

    private List<LineEntity> mDatas;
    /**
     * 坐标轴、坐标轴字体、线条的画笔
     */
    private Paint mPaint1;
    private Paint mPaint2;
    private Paint mPaint3;
    private Paint lineOnePaint;
    private Paint lineTwoPaint;
    private int dateColor1 = Color.parseColor("#E8E8E8");
    private int dateColor2 = Color.parseColor("#999999");
    private int lineOneColor = Color.parseColor("#38AFF7");
    private int lineTwoColor = Color.parseColor("#8F74FD");
    private int mWidth;
    private int mHeight;
    private int mMaxValue;
    private int mMode;
    private int mSize;
    private Rect mBound;
    private Paint mCirclePaint;
    private Paint mPaintShader;
    private int[] shadeColors;
    private int[] shadeColors1;

    private List<Integer> mClickDistanceList = new ArrayList<>();
    private Path onePath;
    private Path twoPath;
    private Path oneShaderPath;
    private Path twoShaderPath;

    private int screenWidth;

    public LineChartView2(Context context) {
        this(context, null);
    }

    public LineChartView2(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LineChartView2(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setDatas(List<LineEntity> datas, int maxValue) {
        mDatas = datas;
        mMaxValue = maxValue;
        if (mMaxValue > 0 && mMaxValue <= 10) {
            mMode = 1;
        } else if (mMaxValue > 10 && mMaxValue <= 20) {
            mMode = 2;
        } else if (mMaxValue > 20 && mMaxValue <= 30) {
            mMode = 3;
        } else if (mMaxValue > 30 && mMaxValue <= 40) {
            mMode = 4;
        } else if (mMaxValue > 40 && mMaxValue <= 50) {
            mMode = 5;
        } else if (mMaxValue > 50 && mMaxValue <= 60) {
            mMode = 6;
        } else if (mMaxValue > 60 && mMaxValue <= 70) {
            mMode = 7;
        } else if (mMaxValue > 70 && mMaxValue <= 80) {
            mMode = 8;
        } else if (mMaxValue > 80 && mMaxValue <= 90) {
            mMode = 9;
        } else {
            mMode = 20;
        }
    }

    //初始化画笔
    private void init() {

        mPaint1 = new Paint();
        mPaint1.setStrokeWidth(1);
        mPaint1.setColor(dateColor1);
        mPaint1.setTextSize(dp2px(11));
        mPaint1.setTextAlign(Paint.Align.CENTER);
        mPaint1.setAntiAlias(true);
        mBound = new Rect();

        mPaint2 = new Paint();
        mPaint2.setStrokeWidth(2);
        mPaint2.setColor(dateColor2);
        mPaint2.setAntiAlias(true);

        mPaint3 = new Paint();
        mPaint3.setStyle(Paint.Style.STROKE);
        mPaint3.setStrokeWidth(1);
        mPaint3.setColor(dateColor2);
        mPaint3.setAntiAlias(true);

        mCirclePaint = new Paint();
        mCirclePaint.setAntiAlias(true);

        lineOnePaint = new Paint();
        lineOnePaint.setStrokeWidth(2);
        lineOnePaint.setStyle(Paint.Style.STROKE);
        lineOnePaint.setTextSize(dp2px(11));
        lineOnePaint.setColor(lineOneColor);

        onePath = new Path();
        oneShaderPath = new Path();

        lineTwoPaint = new Paint();
        lineTwoPaint.setStrokeWidth(2);
        lineTwoPaint.setStyle(Paint.Style.STROKE);
        lineTwoPaint.setTextSize(dp2px(11));
        lineTwoPaint.setColor(lineTwoColor);

        twoPath = new Path();
        twoShaderPath = new Path();
        /*shadeColors = new int[]{
                Color.argb(100, 255, 86, 86), Color.argb(15, 255, 86, 86),
                Color.argb(0, 255, 86, 86)};*/
        shadeColors = new int[]{Color.parseColor("#38AFF7"), Color.TRANSPARENT};
        shadeColors1 = new int[]{Color.parseColor("#8F74FD"), Color.TRANSPARENT};
        mPaintShader = new Paint();
        mPaintShader.setAntiAlias(true);
        mPaintShader.setStrokeWidth(2f);

    }

    public void initDefaultSelect(int i) {
        int size = mSize;
        int x1 = mSize / 3 + size * i - size / 2;
        int x2 = mSize / 3 + size * i + size / 2;
        if (listener != null) {
            listener.getNumber(i, (x1 + x2) / 2, mHeight);
        }
        mClickDistanceList.clear();
        mClickDistanceList.add(i);
        postInvalidate();
    }

    private void initView() {
        DisplayMetrics dm =getResources().getDisplayMetrics();
        screenWidth = dm.widthPixels;
        mWidth = getWidth();
        mHeight = getHeight();
        mSize = screenWidth / 8;
    }

    int times;
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        initView();
        times++ ;
        if (times == 1) {
            if (mDatas.size() == 1) {
                initDefaultSelect(0);
            } else {
                initDefaultSelect(1);
            }
        }
        int value;
        if (mMaxValue % mMode == 0) {
            value = mMaxValue;
        } else {
            value = mMaxValue + mMaxValue % mMode;
        }
        //画坐标轴
        for (int i = 0; i <= value / mMode; i++) {
            canvas.drawLine(0, (mHeight * 8 / 9) - (7 * mHeight / 9) * i / (value / mMode), mWidth, (mHeight * 8 / 9) - (7 * mHeight / 9) * i / (value / mMode), mPaint1);
        }
        //画横坐标轴
        canvas.drawLine(0, mHeight * 8 / 9, mWidth, mHeight * 8 / 9, mPaint2);
        //画刻度线
        for (int i = 0; i < mDatas.size(); i++) {
            canvas.drawLine(mSize / 3 + mSize * i, (mHeight * 8 / 9) - 8, mSize / 3 + mSize * i, mHeight * 8 / 9, mPaint2);
        }
        String text = "作业数量(个)";
        mPaint1.setColor(dateColor2);
        mPaint1.getTextBounds(text, 0, text.length(), mBound);
        //画时间
        for (int i = 0; i < mDatas.size(); i++) {
            canvas.drawText(mDatas.get(i).getTime(), mSize / 3 + i * mSize,
                    mHeight * 8 / 9 + mBound.height() * 3 / 2, mPaint1);
        }

        //画折线
        for (int i = 0; i < mDatas.size(); i++) {
            if (mDatas.size() > 1) {
                if (i == 0) {
                    float startX = mSize / 3 + mSize * i;
                    float division = (float) (value - mDatas.get(i).getTowerCranes()) / value;
                    float startY = (division * 7 + 1) * mHeight / 9;
                    float startX1 = mSize / 3 + mSize * i;
                    float division1 = (float) (value - mDatas.get(i).getElevators()) / value;
                    float startY1 = ((division1 * 7 + 1)) * mHeight / 9;
                    onePath.moveTo(startX, startY);
                    oneShaderPath.moveTo(startX, startY);
                    twoPath.moveTo(startX1, startY1);
                    twoShaderPath.moveTo(startX1, startY1);
                } else {
                    float endX = mSize / 3 + mSize * i;
                    float division = (float) (value - mDatas.get(i).getTowerCranes()) / value;
                    float endY = (division * 7 + 1) * mHeight / 9;
                    float endX1 = mSize / 3 + mSize * i;
                    float division1 = (float) (value - mDatas.get(i).getElevators()) / value;
                    float endY1 = (division1 * 7 + 1) * mHeight / 9;
                    onePath.lineTo(endX, endY);
                    oneShaderPath.lineTo(endX, endY);
                    twoPath.lineTo(endX1, endY1);
                    twoShaderPath.lineTo(endX1, endY1);
                    if (i == mDatas.size() - 1) {
                        oneShaderPath.lineTo(i * mSize + mSize / 3, mHeight * 8 / 9);
                        oneShaderPath.lineTo(mSize / 3, mHeight * 8 / 9);
                        oneShaderPath.close();
                        twoShaderPath.lineTo(i * mSize + mSize / 3, mHeight * 8 / 9);
                        twoShaderPath.lineTo(mSize / 3, mHeight * 8 / 9);
                        twoShaderPath.close();
                    }
                }
            }
        }

        if (mDatas.size() > 1) {
            canvas.drawPath(onePath, lineOnePaint);
            canvas.drawPath(twoPath, lineTwoPaint);
            canvas.drawPath(oneShaderPath, getShadeColorPaint(shadeColors));
            canvas.drawPath(twoShaderPath, getShadeColorPaint(shadeColors1));
        }

        for (int i = 0; i < mDatas.size(); i++) {
            if (mClickDistanceList.contains(i)) {
                float centerX = mSize / 3 + i * mSize;
                float division = (float) (value - mDatas.get(i).getTowerCranes()) / value;
                float centerY = (division * 7 + 1) * mHeight / 9;
                float innerCircle = dp2px(5); //内圆半径
                float ringWidth = dp2px(1);   //圆环宽度
                //绘制外圆
                mCirclePaint.setColor(lineOneColor);
                canvas.drawCircle(centerX, centerY, innerCircle + ringWidth, mCirclePaint);
                //绘制内圆
                mCirclePaint.setColor(Color.parseColor("#FFFFFF"));
                canvas.drawCircle(centerX, centerY, innerCircle, mCirclePaint);
                float centerX1 = mSize / 3 + i * mSize;
                float division1 = (float)  (value - mDatas.get(i).getElevators()) / value;
                float centerY1 = (division1 * 7 + 1) * mHeight / 9;
                //绘制外圆
                mCirclePaint.setColor(lineTwoColor);
                canvas.drawCircle(centerX1, centerY1, innerCircle + ringWidth, mCirclePaint);
                //绘制内圆
                mCirclePaint.setColor(Color.parseColor("#FFFFFF"));
                canvas.drawCircle(centerX1, centerY1, innerCircle, mCirclePaint);

                //画虚线
                Path path = new Path();
                path.moveTo(mSize / 3 + mSize * i, mHeight * 8 / 9);
                path.lineTo(mSize / 3 + mSize * i, mHeight * 1 / 9);
                PathEffect effects = new DashPathEffect(new float[]{5, 5, 5, 5}, 1);
                mPaint3.setPathEffect(effects);
                canvas.drawPath(path, mPaint3);
            }
        }
    }

    // 修改笔的颜色
    private Paint getShadeColorPaint(int[] shadeColors) {

        Shader mShader = new LinearGradient(0, 0, 0, mHeight,
                shadeColors, null, Shader.TileMode.CLAMP);
        // 新建一个线性渐变，前两个参数是渐变开始的点坐标，第三四个参数是渐变结束的点的坐标。连接这2个点就拉出一条渐变线了，玩过PS的都懂。然后那个数组是渐变的颜色。下一个参数是渐变颜色的分布，如果为空，每个颜色就是均匀分布的。最后是模式，这里设置的是循环渐变
        mPaintShader.setShader(mShader);
        return mPaintShader;
    }

    private int selectIndex = -1;
    private getNumberListener listener;

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int x = (int) ev.getX();
        int y = (int) ev.getY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                int mChartWidthd = mSize / 3;
                for (int i = 0; i < mDatas.size(); i++) {
                    int x1 = mChartWidthd + mSize * i - mSize / 2;
                    int x2 = mChartWidthd + mSize * i + mSize / 2;
                    int y1 = mHeight * 1 / 9;
                    int y2 = mHeight * 8 / 9;
                    if (x >= x1 && x <= x2 && y >= y1 && y <= y2) {
                        selectIndex = i;
                        //传入mData中的i，横坐标，高度
                        listener.getNumber(i, (x1 + x2) / 2, mHeight);
                        mClickDistanceList.clear();
                        mClickDistanceList.add(selectIndex);
                        invalidate();
                    }
                }
                break;
            case MotionEvent.ACTION_UP:

                break;
        }
        return true;
    }

    public void setListener(getNumberListener listener) {
        this.listener = listener;
    }

    public interface getNumberListener {
        void getNumber(int number, int x, int y);
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }
}
