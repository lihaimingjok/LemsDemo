package com.pcjz.lems.business.widget.swipMenuListView;

import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pcjz.lems.R;

import java.util.List;

/**
 * Created by ${YGP} on 2017/7/19.
 */

public class SwipeMenuView extends LinearLayout implements View.OnClickListener {
    private SwipeMenuListView mListView;
    private SwipeMenuLayout mLayout;
    private SwipeMenu mMenu;
    private OnSwipeItemClickListener onItemClickListener;
    private int position;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public SwipeMenuView(SwipeMenu menu, SwipeMenuListView listView) {
        super(menu.getContext());
        mListView = listView;
        mMenu = menu;
        List<SwipeMenuItem> items = menu.getMenuItems();
        int id = 0;
        for (SwipeMenuItem item : items) {
            addItem(item, id++);
        }
    }

    private void addItem(SwipeMenuItem item, int id) {
        LayoutParams params = new LayoutParams(item.getWidth(),
                LayoutParams.MATCH_PARENT);
        LinearLayout parent = new LinearLayout(getContext());
        parent.setId(id);
        parent.setGravity(Gravity.CENTER);
        parent.setOrientation(LinearLayout.VERTICAL);
        parent.setLayoutParams(params);
        parent.setBackgroundDrawable(item.getBackground());
        parent.setOnClickListener(this);
        addView(parent);

        if (item.getIcon() != null) {
            parent.addView(createIcon(item));
        }
        if (!TextUtils.isEmpty(item.getTitle())) {
            parent.addView(createTitle(item));
        }
        if (!TextUtils.isEmpty(item.getTitleBackground())) {
            LayoutParams lp = new LayoutParams(dp2px(55), dp2px(26));
            //设置padding值
            //createTitleBackground(item).setPadding(dp2px(14), dp2px(7), dp2px(14), dp2px(7));
            parent.addView(createTitleBackground(item), lp);
        }
    }

    private ImageView createIcon(SwipeMenuItem item) {
        ImageView iv = new ImageView(getContext());
        iv.setImageDrawable(item.getIcon());
        return iv;
    }

    private TextView createTitle(SwipeMenuItem item) {
        TextView tv = new TextView(getContext());
        tv.setText(item.getTitle());
        tv.setGravity(Gravity.CENTER);
        tv.setTextSize(item.getTitleSize());
        tv.setTextColor(item.getTitleColor());
        return tv;
    }

    private TextView createTitleBackground(SwipeMenuItem item) {
        TextView tv = new TextView(getContext());
        tv.setText(item.getTitleBackground());
        tv.setBackgroundDrawable(getResources().getDrawable(R.drawable.application_download_red_shape));
        tv.setGravity(Gravity.CENTER);
        tv.setTextSize(item.getTitleSize());
        tv.setTextColor(item.getTitleColor());
        //tv.setWidth(dp2px(55));
        //tv.setHeight(dp2px(26));
        return tv;
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    @Override
    public void onClick(View v) {
        if (onItemClickListener != null && mLayout.isOpen()) {
            onItemClickListener.onItemClick(this, mMenu, v.getId());
        }
    }

    public OnSwipeItemClickListener getOnSwipeItemClickListener() {
        return onItemClickListener;
    }

    public void setOnSwipeItemClickListener(OnSwipeItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setLayout(SwipeMenuLayout mLayout) {
        this.mLayout = mLayout;
    }

    public interface OnSwipeItemClickListener {
        void onItemClick(SwipeMenuView view, SwipeMenu menu, int index);
    }
}
