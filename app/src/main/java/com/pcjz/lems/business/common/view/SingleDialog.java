package com.pcjz.lems.business.common.view;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pcjz.lems.R;
import com.pcjz.lems.business.common.utils.StringUtils;
import com.pcjz.lems.business.entity.SelectEntity;
import com.pcjz.lems.ui.workrealname.manageequipment.adapter.SelectTypesAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * created by yezhengyu on 2017/8/29 21:18
 */

public class SingleDialog extends Dialog {
    private List<SelectEntity> mSelectList = new ArrayList<>();
    private SelectTypesAdapter mAdapter;
    private Context context;
    private DataBackListener listener;
    private UninterceptableListView mListView;
    private RelativeLayout rlNoData;
    private SelectEntity mSelectType;
    private TextView tvTitle;

    //定义接口
    public interface DataBackListener {
        void getData(SelectEntity entity);
    }

    public SingleDialog(@NonNull Context context, SelectEntity mSelectType, DataBackListener listener) {
        super(context, R.style.bottom_dialog);
        this.context = context;
        this.listener = listener;
        this.mSelectType = mSelectType;
        init();
    }

    public SingleDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
    }

    protected SingleDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public void init() {
        setContentView(R.layout.layout_single_dialog);
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = dip2px(context, 413);
        window.setAttributes(params);
        window.setGravity(Gravity.BOTTOM);
        initView();
    }

    private void initView() {
        tvTitle = (TextView) findViewById(R.id.tv_select_title);
        rlNoData = (RelativeLayout) findViewById(R.id.rl_no_data);
        TextView tvNoData = (TextView) findViewById(R.id.tv_no_data);
        ImageView ivNoData = (ImageView) findViewById(R.id.iv_no_data);
        ivNoData.setImageResource(R.drawable.pop_window_choose_no_content);
        tvNoData.setText(R.string.loading_view_no_data);
        ImageView ivCancel = (ImageView) findViewById(R.id.iv_dialog_cancel);
        ivCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        mListView = (UninterceptableListView) findViewById(R.id.listView);
        mAdapter = new SelectTypesAdapter(context);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mSelectList.get(position).isSelect()) {
                    mSelectList.get(position).setSelect(false);
                } else {
                    mSelectList.get(position).setSelect(true);
                    listener.getData(mSelectList.get(position));
                }
                mAdapter.notifyDataSetChanged();
                dismiss();
            }
        });
    }

    public void setInitSelecList(List<SelectEntity> initSelectList) {
        if (initSelectList != null) {
            rlNoData.setVisibility(View.GONE);
            mListView.setVisibility(View.VISIBLE);
            if (mSelectType != null) {
                for (int j = 0; j < initSelectList.size(); j++) {
                    if (StringUtils.equals(initSelectList.get(j).getId(), mSelectType.getId())) {
                        initSelectList.get(j).setSelect(true);
                    } else {
                        initSelectList.get(j).setSelect(false);
                    }
                }
            } /*else {
                initSelectList.get(0).setSelect(true);
            }*/
            mSelectList.addAll(initSelectList);
            mAdapter.setData(mSelectList);
            mListView.setAdapter(mAdapter);
        } else {
            mListView.setVisibility(View.GONE);
            rlNoData.setVisibility(View.VISIBLE);
        }
    }

    public void setSelectTitle(String title) {
        if (!StringUtils.isEmpty(title) && tvTitle != null) {
            tvTitle.setText(title);
        }
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
