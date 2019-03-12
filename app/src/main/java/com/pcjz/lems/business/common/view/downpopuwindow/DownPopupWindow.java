package com.pcjz.lems.business.common.view.downpopuwindow;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.pcjz.lems.R;
import com.pcjz.lems.business.common.utils.TDevice;

import java.util.List;

/**
 * created by yezhengyu on 2017/9/14 11:53
 */

public class DownPopupWindow extends PopupWindow {

    private final List<DownBean> datas;
    private final int width;
    private final Activity act;

    public DownPopupWindow(Activity act, List<DownBean> datas) {
        this.act = act;
        this.datas = datas;
        LayoutInflater inflater = LayoutInflater.from(act);
        View view = inflater.inflate(R.layout.layout_down_popuwindow, null);
        width = TDevice.getWindowsWidth(act);
        ListView listView = (ListView) view.findViewById(R.id.listview);
        listView.setOnItemClickListener(new ClicListener());
        this.setContentView(view);
        this.setWidth(dp2px(173));
        this.setHeight(dp2px(108));
        this.setOutsideTouchable(true);
        this.setFocusable(true);
        this.setBackgroundDrawable(new ColorDrawable(0000000000));
        DownAdapter adapter = new DownAdapter();
        adapter.setData(datas);
        listView.setAdapter(adapter);
    }

    public void show(View anchor, int height) {
        showAsDropDown(anchor , 0, dp2px(25) - height / 2);
    }

    class ClicListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (mListener != null) {
                mListener.onSelected(datas.get(position), position);
            }
            dismiss();
        }
    }

    private OnItemSelectedListener mListener;

    public void setOnItemSelectedListener(OnItemSelectedListener listener) {
        mListener = listener;
    }

    public interface OnItemSelectedListener {
        void onSelected(DownBean downBean, int position);
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, act.getResources().getDisplayMetrics());
    }
}
